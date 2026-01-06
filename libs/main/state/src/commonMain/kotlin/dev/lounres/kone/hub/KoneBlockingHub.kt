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
import kotlin.jvm.JvmInline


public interface KoneBlockingHub<Value> {
    public val value: Value
    
    public fun subscribe(callback: (Value) -> Unit): Subscription
    
    @InternalKoneHubApi
    public val callbacksValue: Value
    @InternalKoneHubApi
    public fun lockSubscriptions()
    @InternalKoneHubApi
    @Deprecated(
        message = "This method will be removed when implementations will be moved to concurrent queue.",
        level = DeprecationLevel.WARNING,
    )
    public fun subscribeAnyway(callback: (Value) -> Unit): Subscription
    @InternalKoneHubApi
    public fun unlockSubscriptions()
    @InternalKoneHubApi
    public fun compareAndSetCallbackValue(expected: Value): Boolean
    
    public fun interface Subscription {
        public fun cancel()
    }
}

@JvmInline
public value class KoneBlockingHub2BlockingSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneBlockingHub<Value>) {
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: (Value) -> Unit): KoneBlockingHub.Subscription = hub.subscribeAnyway(callback)
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneBlockingHub<Value>.buildSubscriptionLocking(builder: KoneBlockingHub2BlockingSubscriptionScope<Value>.(Value) -> Result): Result {
    lockSubscriptions()
    val result = try {
        KoneBlockingHub2BlockingSubscriptionScope(this).builder(callbacksValue)
    } finally {
        unlockSubscriptions()
    }
    return result
}

public class KoneBlockingHub2AtomicSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneBlockingHub<Value>) {
    @PublishedApi
    internal val subscriptions: KoneMutableList<KoneBlockingHub.Subscription> = KoneArrayGrowableList()
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: (Value) -> Unit): KoneBlockingHub.Subscription =
        hub.subscribeAnyway(callback).also { subscriptions.add(it) }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneBlockingHub<Value>.buildSubscriptionAtomic(builder: KoneBlockingHub2AtomicSubscriptionScope<Value>.(Value) -> Result): Result {
    while (true) {
        val value = callbacksValue
        val scope = KoneBlockingHub2AtomicSubscriptionScope(this)
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

public fun <Value, Result> KoneBlockingHub<Value>.map(elementEquality: Equality<Result> = Equality.defaultFor(), transform: (Value) -> Result): KoneMutableBlockingHub<Result> =
    buildSubscriptionAtomic { initialValue ->
        val hub = KoneMutableBlockingHub(transform(initialValue), elementEquality)
        subscribe { hub.set(transform(it)) }
        hub
    }

public fun <Value> KoneBlockingHub<Value>.toStateFlow(): StateFlow<Value> =
    buildSubscriptionAtomic { initialValue ->
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

public fun <Value> KoneMutableBlockingHub(
    initialValue: Value,
    elementEquality: Equality<Value> = Equality.defaultFor(),
): KoneMutableBlockingHub<Value> =
    KoneMutableBlockingHubImpl(
        initialValue = initialValue,
        elementEquality = elementEquality,
    )

@OptIn(InternalKoneHubApi::class)
private class KoneMutableBlockingHubImpl<Value>(
    initialValue: Value,
    private val elementEquality: Equality<Value> = Equality.defaultFor(),
) : KoneMutableBlockingHub<Value> {
    override var value: Value = initialValue
    
    // TODO: Replace with concurrent queue
    override var callbacksValue: Value = initialValue
    private val callbacksLock: ReentrantLock = ReentrantLock()
    private val callbacks: KoneMutableNoddedList<(Value) -> Unit> = KoneGCLinkedSizedList()
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
    override fun lockSubscriptions() {
        callbacksLock.lock()
    }
    @Deprecated(
        "This method will be removed when implementations will be moved to concurrent queue.",
        level = DeprecationLevel.WARNING
    )
    override fun subscribeAnyway(callback: (Value) -> Unit): KoneBlockingHub.Subscription {
        val node = callbacks.addNode(callback)
        return KoneBlockingHub.Subscription {
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
    
    private val lock = ReentrantLock()
    override fun acquire() {
        lock.lock()
    }
    override fun acceptNewValue(newValue: Value) {
        if (elementEquality { newValue eq value }) return
        val callbacksToCall = callbacksLock.withLock {
            callbacksValue = newValue
            callbacks.toKoneList()
        }
        callbacksToCall.forEach { callback ->
            try {
                callback(newValue)
            } catch (_: Exception) {}
        }
        value = newValue
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

//@OptIn(InternalKoneHubApi::class)
//public inline fun <SourceValue, TargetValue> KoneMutableBlockingHub2<SourceValue>.view(
//    crossinline get: (SourceValue) -> TargetValue,
//    crossinline set: (previousSourceValue: SourceValue, newTargetValue: TargetValue) -> SourceValue,
//): KoneMutableBlockingHub2<TargetValue> =
//    object : KoneMutableBlockingHub2<TargetValue> {
//        override val value: TargetValue get() = get(this@view.value)
//
//        override fun subscribe(callback: (TargetValue) -> Unit): KoneBlockingHub2.Subscription =
//            this@view.subscribe { callback(get(it)) }
//        override val callbacksValue: TargetValue get() = get(this@view.callbacksValue)
//        override fun lockSubscriptions() {
//            this@view.lockSubscriptions()
//        }
//        @Deprecated(
//            "This method will be removed when implementations will be moved to concurrent queue.",
//            level = DeprecationLevel.WARNING
//        )
//        override fun subscribeAnyway(callback: (TargetValue) -> Unit): KoneBlockingHub2.Subscription =
//            this@view.subscribeAnyway { callback(get(it)) }
//        override fun unlockSubscriptions() {
//            this@view.unlockSubscriptions()
//        }
//        override fun compareAndSetCallbackValue(expected: TargetValue): Boolean =
//            this@view.compareAndSetCallbackValue(expected)
//
//        override fun acquire() {
//            this@view.acquire()
//        }
//        override fun acceptNewValue(newValue: TargetValue) {
//            this@view.acceptNewValue(newValue)
//        }
//        override fun release() {
//            mutex.unlock()
//        }
//    }