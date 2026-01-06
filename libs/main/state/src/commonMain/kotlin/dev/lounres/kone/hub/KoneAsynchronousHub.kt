/*
 * Copyright © 2025 Gleb Minaev
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
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlin.jvm.JvmInline


public interface KoneAsynchronousHub<Value> {
    public val value: Value
    
    public fun subscribe(callback: suspend (Value) -> Unit): Subscription
    
    @InternalKoneHubApi
    public val callbacksValue: Value
    @InternalKoneHubApi
    public fun lockSubscriptions()
    @InternalKoneHubApi
    @Deprecated(
        message = "This method will be removed when implementations will be moved to concurrent queue.",
        level = DeprecationLevel.WARNING,
    )
    public fun subscribeAnyway(callback: suspend (Value) -> Unit): Subscription
    @InternalKoneHubApi
    public fun unlockSubscriptions()
    @InternalKoneHubApi
    public fun compareAndSetCallbackValue(expected: Value): Boolean
    
    public fun interface Subscription {
        public fun cancel()
    }
}

@JvmInline
public value class KoneAsynchronousHub2BlockingSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneAsynchronousHub<Value>) {
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription = hub.subscribeAnyway(callback)
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneAsynchronousHub<Value>.buildSubscriptionLocking(builder: KoneAsynchronousHub2BlockingSubscriptionScope<Value>.(Value) -> Result): Result {
    lockSubscriptions()
    val result = try {
        KoneAsynchronousHub2BlockingSubscriptionScope(this).builder(callbacksValue)
    } finally {
        unlockSubscriptions()
    }
    return result
}

public class KoneAsynchronousHub2AtomicSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneAsynchronousHub<Value>) {
    @PublishedApi
    internal val subscriptions: KoneMutableList<KoneAsynchronousHub.Subscription> = KoneArrayGrowableList()
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription =
        hub.subscribeAnyway(callback).also { subscriptions.add(it) }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneAsynchronousHub<Value>.buildSubscriptionAtomic(builder: KoneAsynchronousHub2AtomicSubscriptionScope<Value>.(Value) -> Result): Result {
    while (true) {
        val value = callbacksValue
        val scope = KoneAsynchronousHub2AtomicSubscriptionScope(this)
        var isSuccess = false
        val result = try {
            scope.builder(value).also { isSuccess = true }
        } finally {
            if (!isSuccess) scope.subscriptions.forEach { it.cancel() }
        }
        if (compareAndSetCallbackValue(value)) return result
        else {
            scope.subscriptions.forEach { it.cancel() }
        }
    }
}

public fun <Value, Result> KoneAsynchronousHub<Value>.map(elementEquality: Equality<Result> = Equality.defaultFor(), transform: (Value) -> Result): KoneMutableAsynchronousHub<Result> =
    buildSubscriptionAtomic { initialValue ->
        val hub = KoneMutableAsynchronousHub(transform(initialValue), elementEquality)
        subscribe { hub.set(transform(it)) }
        hub
    }

public fun <Value> KoneAsynchronousHub<Value>.toStateFlow(): StateFlow<Value> =
    buildSubscriptionAtomic { initialValue ->
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
}

public fun <Value> KoneMutableAsynchronousHub(
    initialValue: Value,
    elementEquality: Equality<Value> = Equality.defaultFor(),
): KoneMutableAsynchronousHub<Value> =
    KoneMutableAsynchronousHubImpl(
        initialValue = initialValue,
        elementEquality = elementEquality,
    )

@OptIn(InternalKoneHubApi::class)
private class KoneMutableAsynchronousHubImpl<Value>(
    initialValue: Value,
    private val elementEquality: Equality<Value> = Equality.defaultFor(),
) : KoneMutableAsynchronousHub<Value> {
    override var value: Value = initialValue
    
    // TODO: Replace with concurrent queue
    override var callbacksValue: Value = initialValue
    private val callbacksLock: ReentrantLock = ReentrantLock()
    private val callbacks: KoneMutableNoddedList<suspend (Value) -> Unit> = KoneGCLinkedSizedList()
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
    override fun lockSubscriptions() {
        callbacksLock.lock()
    }
    @Deprecated(
        "This method will be removed when implementations will be moved to concurrent queue.",
        level = DeprecationLevel.WARNING
    )
    override fun subscribeAnyway(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription {
        val node = callbacks.addNode(callback)
        return KoneAsynchronousHub.Subscription {
            callbacksLock.withLock {
                node.remove()
            }
        }
    }
    override fun unlockSubscriptions() {
        callbacksLock.unlock()
    }
    override fun compareAndSetCallbackValue(expected: Value): Boolean {
        TODO("Not yet implemented")
    }
    
    private val mutex = Mutex()
    override suspend fun acquire() {
        mutex.lock()
    }
    override suspend fun acceptNewValue(newValue: Value) {
        if (elementEquality { newValue eq value }) return
        val callbacksToLaunch = callbacksLock.withLock {
            callbacksValue = newValue
            callbacks.toKoneList()
        }
        supervisorScope {
            callbacksToLaunch.forEach { callback ->
                launch { callback(newValue) }
            }
        }
        value = newValue
    }
    override fun release() {
        mutex.unlock()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend inline fun <Value, Result> KoneMutableAsynchronousHub<Value>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result {
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
public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.update(transform: (Value) -> Value) {
    acquire()
    try {
        val newValue = transform(value)
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
public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.getAndUpdate(transform: (Value) -> Value): Value {
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

//@OptIn(InternalKoneHubApi::class)
//public inline fun <SourceValue, TargetValue> KoneMutableAsynchronousHub2<SourceValue>.view(
//    crossinline get: (SourceValue) -> TargetValue,
//    crossinline set: suspend (previousSourceValue: SourceValue, newTargetValue: TargetValue) -> SourceValue,
//): KoneMutableAsynchronousHub2<TargetValue> =
//    object : KoneMutableAsynchronousHub2<TargetValue> {
//        override val value: TargetValue get() = get(this@view.value)
//
//        override fun subscribe(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHub2.Subscription =
//            this@view.subscribe { callback(get(it)) }
//        override val callbacksValue: TargetValue get() = get(this@view.callbacksValue)
//        override fun lockSubscriptions() {
//            this@view.lockSubscriptions()
//        }
//        @Deprecated(
//            "This method will be removed when implementations will be moved to concurrent queue.",
//            level = DeprecationLevel.WARNING
//        )
//        override fun subscribeAnyway(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHub2.Subscription =
//            this@view.subscribeAnyway { callback(get(it)) }
//        override fun unlockSubscriptions() {
//            this@view.unlockSubscriptions()
//        }
//        override fun compareAndSetCallbackValue(expected: TargetValue): Boolean =
//            this@view.compareAndSetCallbackValue(expected)
//
//        override suspend fun acquire() {
//            this@view.acquire()
//        }
//        override suspend fun acceptNewValue(newValue: TargetValue) {
//            this@view.acceptNewValue(newValue)
//        }
//        override fun release() {
//            mutex.unlock()
//        }
//    }