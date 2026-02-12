/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hub

import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneGCLinkedSizedList
import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


public interface KoneAsynchronousHub<out Value> {
    @InternalKoneHubApi
    public val callbacksValue: Value
    
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): Subscription
    
    @InternalKoneHubApi
    public fun lockCallbacksValue()
    @InternalKoneHubApi
    public fun unlockCallbacksValue()
    
    public fun interface Subscription {
        public fun cancel()
    }
}

@OptIn(InternalKoneHubApi::class)
public val <Value> KoneAsynchronousHub<Value>.value: Value get() = callbacksValue

@JvmInline
public value class KoneAsynchronousHubBlockingSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneAsynchronousHub<Value>) {
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription = hub.subscribe(callback)
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneAsynchronousHub<Value>.buildSubscriptionLocking(builder: KoneAsynchronousHubBlockingSubscriptionScope<Value>.(Value) -> Result): Result {
    lockCallbacksValue()
    val result = try {
        KoneAsynchronousHubBlockingSubscriptionScope(this).builder(callbacksValue)
    } finally {
        unlockCallbacksValue()
    }
    return result
}

public class KoneAsynchronousHubAtomicSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneAsynchronousHub<Value>) {
    @PublishedApi
    internal val subscriptions: KoneMutableList<KoneAsynchronousHub.Subscription> = KoneArrayGrowableList()
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription =
        hub.subscribe(callback).also { subscriptions.add(it) }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneAsynchronousHub<Value>.buildSubscriptionAtomic(valueEquality: Equality<Value> = Equality.absoluteFor(), builder: KoneAsynchronousHubAtomicSubscriptionScope<Value>.(Value) -> Result): Result {
    while (true) {
        val callbacksValue = this.callbacksValue
        val scope = KoneAsynchronousHubAtomicSubscriptionScope(this)
        val result = try {
            scope.builder(callbacksValue)
        } catch (throwable: Throwable) {
            scope.subscriptions.forEach { it.cancel() }
            throw throwable
        }
        if (valueEquality { callbacksValue eq this.callbacksValue }) return result
        else {
            scope.subscriptions.forEach { it.cancel() }
        }
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <SourceValue, TargetValue> KoneAsynchronousHub<SourceValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
): KoneAsynchronousHub<TargetValue> =
    object : KoneAsynchronousHub<TargetValue> {
        override val callbacksValue: TargetValue get() = get(this@view.callbacksValue)
        override fun subscribe(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHub.Subscription =
            this@view.subscribe { callback(get(it)) }
        override fun lockCallbacksValue(){
            this@view.lockCallbacksValue()
        }
        override fun unlockCallbacksValue() {
            this@view.unlockCallbacksValue()
        }
    }

public fun <Value, Result> KoneAsynchronousHub<Value>.map(valueEquality: Equality<Value> = Equality.absoluteFor(), transform: (Value) -> Result): KoneMutableAsynchronousHub<Result> =
    buildSubscriptionAtomic(valueEquality = valueEquality) { initialValue ->
        val hub = KoneMutableAsynchronousHub(transform(initialValue))
        subscribe { hub.set(transform(it)) }
        hub
    }

public fun <Value> KoneAsynchronousHub<Value>.toStateFlow(): StateFlow<Value> =
    buildSubscriptionAtomic(valueEquality = Equality.defaultFor()) { initialValue ->
        val stateFlow = MutableStateFlow(initialValue)
        subscribe { stateFlow.value = it }
        stateFlow
    }

public interface KoneMutableAsynchronousHub<Value> : KoneAsynchronousHub<Value> {
    @InternalKoneHubApi
    public suspend fun acquire()
    @InternalKoneHubApi
    public suspend fun acceptNewValue(newValue: Value)
    @InternalKoneHubApi
    public fun release()
    
    public companion object
}

public fun <Value> KoneMutableAsynchronousHub(initialValue: Value): KoneMutableAsynchronousHub<Value> =
    KoneMutableAsynchronousHubImpl(initialValue = initialValue)

@OptIn(InternalKoneHubApi::class)
private class KoneMutableAsynchronousHubImpl<Value>(initialValue: Value) : KoneMutableAsynchronousHub<Value> {
    // TODO: Replace with concurrent queue
    override var callbacksValue: Value = initialValue
    private val callbacksValueLock: ReentrantLock = ReentrantLock()
    private val callbacks: KoneMutableNoddedList<suspend (Value) -> Unit> = KoneGCLinkedSizedList()
    private val callbacksLock: ReentrantLock = ReentrantLock()
    override fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription {
        callbacksLock.withLock {
            val node = callbacks.addNode(callback)
            return KoneAsynchronousHub.Subscription {
                callbacksLock.withLock {
                    node.remove()
                }
            }
        }
    }
    override fun lockCallbacksValue() {
        callbacksValueLock.lock()
    }
    override fun unlockCallbacksValue() {
        callbacksValueLock.unlock()
    }
    
    private val mutex = Mutex()
    override suspend fun acquire() {
        mutex.lock()
    }
    override suspend fun acceptNewValue(newValue: Value) {
        val callbacksToLaunch = callbacksValueLock.withLock {
            callbacksValue = newValue
            callbacksLock.withLock { callbacks.toKoneList() }
        }
        supervisorScope {
            callbacksToLaunch.forEach { callback ->
                launch { callback(newValue) }
            }
        }
    }
    override fun release() {
        mutex.unlock()
    }
}

public fun <Value> KoneMutableAsynchronousHub.Companion.acceptingNew(
    initialValue: Value,
    valueEquality: Equality<Value> = Equality.defaultFor(),
): KoneMutableAsynchronousHub<Value> =
    KoneMutableAsynchronousHubAcceptingNewImpl(initialValue = initialValue, valueEquality = valueEquality)

@OptIn(InternalKoneHubApi::class)
private class KoneMutableAsynchronousHubAcceptingNewImpl<Value>(
    initialValue: Value,
    val valueEquality: Equality<Value>,
) : KoneMutableAsynchronousHub<Value> {
    // TODO: Replace with concurrent queue
    override var callbacksValue: Value = initialValue
    private val callbacksValueLock: ReentrantLock = ReentrantLock()
    private val callbacks: KoneMutableNoddedList<suspend (Value) -> Unit> = KoneGCLinkedSizedList()
    private val callbacksLock: ReentrantLock = ReentrantLock()
    override fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription {
        callbacksLock.withLock {
            val node = callbacks.addNode(callback)
            return KoneAsynchronousHub.Subscription {
                callbacksLock.withLock {
                    node.remove()
                }
            }
        }
    }
    override fun lockCallbacksValue() {
        callbacksValueLock.lock()
    }
    override fun unlockCallbacksValue() {
        callbacksValueLock.unlock()
    }
    
    private val mutex = Mutex()
    override suspend fun acquire() {
        mutex.lock()
    }
    override suspend fun acceptNewValue(newValue: Value) {
        val callbacksToLaunch = callbacksValueLock.withLock {
            if (valueEquality { newValue eq callbacksValue }) return
            callbacksValue = newValue
            callbacksLock.withLock { callbacks.toKoneList() }
        }
        supervisorScope {
            callbacksToLaunch.forEach { callback ->
                launch { callback(newValue) }
            }
        }
    }
    override fun release() {
        mutex.unlock()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend inline fun <Value, Result> KoneMutableAsynchronousHub<Value>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result {
    contract {
        callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
        callsInPlace(map, InvocationKind.EXACTLY_ONCE)
    }
    acquire()
    try {
        val newValue = transform(callbacksValue)
        val result = map(callbacksValue, newValue)
        acceptNewValue(newValue)
        return result
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.update(transform: (Value) -> Value) {
    contract {
        callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
    }
    acquire()
    try {
        val newValue = transform(callbacksValue)
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend fun <Value> KoneMutableAsynchronousHub<Value>.set(newValue: Value) {
    acquire()
    try {
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.updateAndGet(transform: (Value) -> Value): Value {
    contract {
        callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
    }
    acquire()
    try {
        val newValue = transform(callbacksValue)
        acceptNewValue(newValue)
        return newValue
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.getAndUpdate(transform: (Value) -> Value): Value {
    contract {
        callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
    }
    acquire()
    try {
        val previousValue = callbacksValue
        val newValue = transform(callbacksValue)
        acceptNewValue(newValue)
        return previousValue
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <SourceValue, TargetValue> KoneMutableAsynchronousHub<SourceValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
    crossinline set: suspend (previousSourceValue: SourceValue, newTargetValue: TargetValue) -> SourceValue,
): KoneMutableAsynchronousHub<TargetValue> =
    object : KoneMutableAsynchronousHub<TargetValue> {
        override val callbacksValue: TargetValue get() = get(this@view.callbacksValue)
        override fun subscribe(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHub.Subscription =
            this@view.subscribe { callback(get(it)) }
        override fun lockCallbacksValue() {
            this@view.lockCallbacksValue()
        }
        override fun unlockCallbacksValue() {
            this@view.unlockCallbacksValue()
        }

        override suspend fun acquire() {
            this@view.acquire()
        }
        override suspend fun acceptNewValue(newValue: TargetValue) {
            this@view.acceptNewValue(set(this@view.callbacksValue, newValue))
        }
        override fun release() {
            this@view.release()
        }
    }