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
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.JvmInline


public interface KoneBlockingHubView<out Value, CallbacksValue> {
    @InternalKoneHubApi
    public val callbacksState: CallbacksState<Value, CallbacksValue>
    
    public fun subscribe(callback: (Value) -> Unit): Subscription
    
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
    public fun compareAndSetCallbackValue(expected: CallbacksValue): Boolean
    
    public fun interface Subscription {
        public fun cancel()
    }
}

@OptIn(InternalKoneHubApi::class)
public val <Value> KoneBlockingHubView<Value, *>.value: Value get() = callbacksState.value

public typealias KoneBlockingHub<Value> = KoneBlockingHubView<Value, Value>

@JvmInline
public value class KoneBlockingHub2BlockingSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneBlockingHubView<Value, *>) {
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: (Value) -> Unit): KoneBlockingHubView.Subscription = hub.subscribeAnyway(callback)
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneBlockingHubView<Value, *>.buildSubscriptionLocking(builder: KoneBlockingHub2BlockingSubscriptionScope<Value>.(Value) -> Result): Result {
    lockSubscriptions()
    val result = try {
        KoneBlockingHub2BlockingSubscriptionScope(this).builder(callbacksState.value)
    } finally {
        unlockSubscriptions()
    }
    return result
}

public class KoneBlockingHub2AtomicSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneBlockingHubView<Value, *>) {
    @PublishedApi
    internal val subscriptions: KoneMutableList<KoneBlockingHubView.Subscription> = KoneArrayGrowableList()
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: (Value) -> Unit): KoneBlockingHubView.Subscription =
        hub.subscribeAnyway(callback).also { subscriptions.add(it) }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, CallbacksValue, Result> KoneBlockingHubView<Value, CallbacksValue>.buildSubscriptionAtomic(builder: KoneBlockingHub2AtomicSubscriptionScope<Value>.(Value) -> Result): Result {
    while (true) {
        val state = callbacksState
        val scope = KoneBlockingHub2AtomicSubscriptionScope(this)
        var isSuccess = false
        val result = try {
            scope.builder(state.value).also { isSuccess = true }
        } finally {
            if (!isSuccess) scope.subscriptions.forEach { it.cancel() }
        }
        if (compareAndSetCallbackValue(state.callbacksState)) return result
        else {
            scope.subscriptions.forEach { it.cancel() }
        }
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <SourceValue, TargetValue, CallbacksValue> KoneBlockingHubView<SourceValue, CallbacksValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
): KoneBlockingHubView<TargetValue, CallbacksValue> =
    object : KoneBlockingHubView<TargetValue, CallbacksValue> {
        override val callbacksState: CallbacksState<TargetValue, CallbacksValue>
            get() = this@view.callbacksState.let { CallbacksState(get(it.value), it.callbacksState) }
        override fun subscribe(callback: (TargetValue) -> Unit): KoneBlockingHubView.Subscription =
            this@view.subscribe { callback(get(it)) }
        override fun lockSubscriptions() {
            this@view.lockSubscriptions()
        }
        @Deprecated(
            "This method will be removed when implementations will be moved to concurrent queue.",
            level = DeprecationLevel.WARNING
        )
        @Suppress("DEPRECATION")
        override fun subscribeAnyway(callback: (TargetValue) -> Unit): KoneBlockingHubView.Subscription =
            this@view.subscribeAnyway { callback(get(it)) }
        override fun unlockSubscriptions() {
            this@view.unlockSubscriptions()
        }
        override fun compareAndSetCallbackValue(expected: CallbacksValue): Boolean =
            this@view.compareAndSetCallbackValue(expected)
    }

public fun <Value, Result> KoneBlockingHubView<Value, *>.map(elementEquality: Equality<Result> = Equality.defaultFor(), transform: (Value) -> Result): KoneMutableBlockingHub<Result> =
    /*buildSubscriptionAtomic*/ /* FIXME: Replace when `buildSubscriptionAtomic` will be available*/
    buildSubscriptionLocking  { initialValue ->
        val hub = KoneMutableBlockingHub(transform(initialValue), elementEquality)
        subscribe { hub.set(transform(it)) }
        hub
    }

public fun <Value> KoneBlockingHubView<Value, *>.toStateFlow(): StateFlow<Value> =
    /*buildSubscriptionAtomic*/ /* FIXME: Replace when `buildSubscriptionAtomic` will be available*/
    buildSubscriptionLocking  { initialValue ->
        val stateFlow = MutableStateFlow(initialValue)
        subscribe { stateFlow.value = it }
        stateFlow
    }

public interface KoneMutableBlockingHubView<Value, CallbacksValue> : KoneBlockingHubView<Value, CallbacksValue> {
    @InternalKoneHubApi
    public fun acquire()
    @InternalKoneHubApi
    public fun acceptNewValue(newValue: Value)
    @InternalKoneHubApi
    public fun release()
}

public typealias KoneMutableBlockingHub<Value> = KoneMutableBlockingHubView<Value, Value>

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
    // TODO: Replace with concurrent queue
    override var callbacksState: CallbacksState<Value, Value> = CallbacksState(initialValue, initialValue)
    private val callbacksLock: ReentrantLock = ReentrantLock()
    private val callbacks: KoneMutableNoddedList<(Value) -> Unit> = KoneGCLinkedSizedList()
    override fun subscribe(callback: (Value) -> Unit): KoneBlockingHubView.Subscription {
        callbacksLock.withLock {
            val node = callbacks.addNode(callback)
            return KoneBlockingHubView.Subscription {
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
    override fun subscribeAnyway(callback: (Value) -> Unit): KoneBlockingHubView.Subscription {
        val node = callbacks.addNode(callback)
        return KoneBlockingHubView.Subscription {
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
            callbacksState = CallbacksState(newValue, newValue)
            callbacks.toKoneList()
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
public inline fun <Value, Result> KoneMutableBlockingHubView<Value, *>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result {
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
public inline fun <Value> KoneMutableBlockingHubView<Value, *>.update(transform: (Value) -> Value) {
    acquire()
    try {
        val newValue = transform(value)
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public fun <Value> KoneMutableBlockingHubView<Value, *>.set(newValue: Value) {
    acquire()
    try {
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value> KoneMutableBlockingHubView<Value, *>.updateAndGet(transform: (Value) -> Value): Value {
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
public inline fun <Value> KoneMutableBlockingHubView<Value, *>.getAndUpdate(transform: (Value) -> Value): Value {
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
public inline fun <SourceValue, TargetValue, CallbacksValue> KoneMutableBlockingHubView<SourceValue, CallbacksValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
    crossinline set: (previousSourceValue: SourceValue, newTargetValue: TargetValue) -> SourceValue,
): KoneMutableBlockingHubView<TargetValue, CallbacksValue> =
    object : KoneMutableBlockingHubView<TargetValue, CallbacksValue> {
        override fun subscribe(callback: (TargetValue) -> Unit): KoneBlockingHubView.Subscription =
            this@view.subscribe { callback(get(it)) }
        override val callbacksState: CallbacksState<TargetValue, CallbacksValue>
            get() = this@view.callbacksState.let { CallbacksState(get(it.value), it.callbacksState) }
        override fun lockSubscriptions() {
            this@view.lockSubscriptions()
        }
        @Deprecated(
            "This method will be removed when implementations will be moved to concurrent queue.",
            level = DeprecationLevel.WARNING
        )
        @Suppress("DEPRECATION")
        override fun subscribeAnyway(callback: (TargetValue) -> Unit): KoneBlockingHubView.Subscription =
            this@view.subscribeAnyway { callback(get(it)) }
        override fun unlockSubscriptions() {
            this@view.unlockSubscriptions()
        }
        override fun compareAndSetCallbackValue(expected: CallbacksValue): Boolean =
            this@view.compareAndSetCallbackValue(expected)

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