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
import kotlin.jvm.JvmInline


public interface KoneBlockingHub<out Value> {
    @InternalKoneHubApi
    public val callbacksValue: Value
    
    @IgnorableReturnValue
    public fun subscribe(callback: (Value) -> Unit): Subscription
    
    @InternalKoneHubApi
    public fun lockCallbacksValue()
    @InternalKoneHubApi
    public fun unlockCallbacksValue()
    
    public fun interface Subscription {
        public fun cancel()
    }
}

@OptIn(InternalKoneHubApi::class)
public val <Value> KoneBlockingHub<Value>.value: Value get() = callbacksValue

@JvmInline
public value class KoneBlockingHubBlockingSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneBlockingHub<Value>) {
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: (Value) -> Unit): KoneBlockingHub.Subscription = hub.subscribe(callback)
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneBlockingHub<Value>.buildSubscriptionLocking(builder: KoneBlockingHubBlockingSubscriptionScope<Value>.(Value) -> Result): Result {
    lockCallbacksValue()
    val result = try {
        KoneBlockingHubBlockingSubscriptionScope(this).builder(callbacksValue)
    } finally {
        unlockCallbacksValue()
    }
    return result
}

public class KoneBlockingHubAtomicSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneBlockingHub<Value>) {
    @PublishedApi
    internal val subscriptions: KoneMutableList<KoneBlockingHub.Subscription> = KoneArrayGrowableList()
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: (Value) -> Unit): KoneBlockingHub.Subscription =
        hub.subscribe(callback).also { subscriptions.add(it) }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneBlockingHub<Value>.buildSubscriptionAtomic(valueEquality: Equality<Value> = Equality.absoluteFor(), builder: KoneBlockingHubAtomicSubscriptionScope<Value>.(Value) -> Result): Result {
    while (true) {
        val callbacksValue = this.callbacksValue
        val scope = KoneBlockingHubAtomicSubscriptionScope(this)
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
public inline fun <SourceValue, TargetValue> KoneBlockingHub<SourceValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
): KoneBlockingHub<TargetValue> =
    object : KoneBlockingHub<TargetValue> {
        override val callbacksValue: TargetValue get() = get(this@view.callbacksValue)
        override fun subscribe(callback: (TargetValue) -> Unit): KoneBlockingHub.Subscription =
            this@view.subscribe { callback(get(it)) }
        override fun lockCallbacksValue() {
            this@view.lockCallbacksValue()
        }
        override fun unlockCallbacksValue() {
            this@view.unlockCallbacksValue()
        }
    }

public fun <Value, Result> KoneBlockingHub<Value>.map(valueEquality: Equality<Value> = Equality.defaultFor(), transform: (Value) -> Result): KoneMutableBlockingHub<Result> =
    buildSubscriptionAtomic(valueEquality = valueEquality) { initialValue ->
        val hub = KoneMutableBlockingHub(transform(initialValue))
        subscribe { hub.set(transform(it)) }
        hub
    }

public fun <Value> KoneBlockingHub<Value>.toStateFlow(): StateFlow<Value> =
    buildSubscriptionAtomic(valueEquality = Equality.defaultFor()) { initialValue ->
        val stateFlow = MutableStateFlow(initialValue)
        subscribe { stateFlow.value = it }
        stateFlow
    }

public interface KoneMutableBlockingHub<Value> : KoneBlockingHub<Value> {
    @InternalKoneHubApi
    public fun acquire()
    @InternalKoneHubApi
    public fun acceptNewValue(newValue: Value)
    @InternalKoneHubApi
    public fun release()
}

public fun <Value> KoneMutableBlockingHub(initialValue: Value): KoneMutableBlockingHub<Value> =
    KoneMutableBlockingHubImpl(initialValue = initialValue)

@OptIn(InternalKoneHubApi::class)
private class KoneMutableBlockingHubImpl<Value>(initialValue: Value) : KoneMutableBlockingHub<Value> {
    // TODO: Replace with concurrent queue
    override var callbacksValue: Value = initialValue
    private val callbacksValueLock: ReentrantLock = ReentrantLock()
    private val callbacks: KoneMutableNoddedList<(Value) -> Unit> = KoneGCLinkedSizedList()
    private val callbacksLock: ReentrantLock = ReentrantLock()
    override fun subscribe(callback: (Value) -> Unit): KoneBlockingHub.Subscription {
        callbacksLock.withLock {
            val node = callbacks.addNode(callback)
            return KoneBlockingHub.Subscription {
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
    
    private val lock = ReentrantLock()
    override fun acquire() {
        lock.lock()
    }
    override fun acceptNewValue(newValue: Value) {
        val callbacksToCall = callbacksValueLock.withLock {
            callbacksValue = newValue
            callbacksLock.withLock { callbacks.toKoneList() }
        }
        callbacksToCall.forEach { callback ->
            try {
                callback(newValue)
            } catch (_: Exception) {}
        }
    }
    override fun release() {
        lock.unlock()
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneMutableBlockingHub<Value>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result {
    acquire()
    try {
        val newValue = transform(value)
        val result = map(value, newValue)
        acceptNewValue(newValue)
        return result
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value> KoneMutableBlockingHub<Value>.update(transform: (Value) -> Value) {
    acquire()
    try {
        val newValue = transform(value)
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public fun <Value> KoneMutableBlockingHub<Value>.set(newValue: Value) {
    acquire()
    try {
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value> KoneMutableBlockingHub<Value>.updateAndGet(transform: (Value) -> Value): Value {
    acquire()
    try {
        val newValue = transform(value)
        acceptNewValue(newValue)
        return newValue
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value> KoneMutableBlockingHub<Value>.getAndUpdate(transform: (Value) -> Value): Value {
    acquire()
    try {
        val previousValue = value
        val newValue = transform(value)
        acceptNewValue(newValue)
        return previousValue
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <SourceValue, TargetValue> KoneMutableBlockingHub<SourceValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
    crossinline set: (previousSourceValue: SourceValue, newTargetValue: TargetValue) -> SourceValue,
): KoneMutableBlockingHub<TargetValue> =
    object : KoneMutableBlockingHub<TargetValue> {
        override val callbacksValue: TargetValue get() = get(this@view.callbacksValue)
        override fun subscribe(callback: (TargetValue) -> Unit): KoneBlockingHub.Subscription =
            this@view.subscribe { callback(get(it)) }
        override fun lockCallbacksValue() {
            this@view.lockCallbacksValue()
        }
        override fun unlockCallbacksValue() {
            this@view.unlockCallbacksValue()
        }

        override fun acquire() {
            this@view.acquire()
        }
        override fun acceptNewValue(newValue: TargetValue) {
            this@view.acceptNewValue(set(this@view.value, newValue))
        }
        override fun release() {
            this@view.release()
        }
    }