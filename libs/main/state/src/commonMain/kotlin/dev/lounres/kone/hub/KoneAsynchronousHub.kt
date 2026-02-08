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
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlin.jvm.JvmInline


public interface KoneAsynchronousHubView<out Value, CallbacksValue> {
    @InternalKoneHubApi
    public val callbacksState: CallbacksState<Value, CallbacksValue>
    
    public fun subscribe(callback: suspend (Value) -> Unit): Subscription
    
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
    public fun compareCallbackValue(expected: CallbacksValue): Boolean
    
    public fun interface Subscription {
        public fun cancel()
    }
}

@OptIn(InternalKoneHubApi::class)
public val <Value> KoneAsynchronousHubView<Value, *>.value: Value get() = callbacksState.value

public typealias KoneAsynchronousHub<Value> = KoneAsynchronousHubView<Value, Value>

@JvmInline
public value class KoneAsynchronousHubViewBlockingSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneAsynchronousHubView<Value, *>) {
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHubView.Subscription = hub.subscribeAnyway(callback)
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, Result> KoneAsynchronousHubView<Value, *>.buildSubscriptionLocking(builder: KoneAsynchronousHubViewBlockingSubscriptionScope<Value>.(Value) -> Result): Result {
    lockSubscriptions()
    val result = try {
        KoneAsynchronousHubViewBlockingSubscriptionScope(this).builder(callbacksState.value)
    } finally {
        unlockSubscriptions()
    }
    return result
}

public class KoneAsynchronousHub2AtomicSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneAsynchronousHubView<Value, *>) {
    @PublishedApi
    internal val subscriptions: KoneMutableList<KoneAsynchronousHubView.Subscription> = KoneArrayGrowableList()
    @Suppress("DEPRECATION")
    @OptIn(InternalKoneHubApi::class)
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHubView.Subscription =
        hub.subscribeAnyway(callback).also { subscriptions.add(it) }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <Value, CallbacksValue, Result> KoneAsynchronousHubView<Value, CallbacksValue>.buildSubscriptionAtomic(builder: KoneAsynchronousHub2AtomicSubscriptionScope<Value>.(Value) -> Result): Result {
    while (true) {
        val callbacksValue = callbacksState
        val scope = KoneAsynchronousHub2AtomicSubscriptionScope(this)
        var isSuccess = false
        val result = try {
            scope.builder(callbacksValue.value).also { isSuccess = true }
        } finally {
            if (!isSuccess) scope.subscriptions.forEach { it.cancel() }
        }
        if (compareCallbackValue(callbacksValue.callbacksState)) return result
        else {
            scope.subscriptions.forEach { it.cancel() }
        }
    }
}

@OptIn(InternalKoneHubApi::class)
public inline fun <SourceValue, TargetValue, CallbacksValue> KoneAsynchronousHubView<SourceValue, CallbacksValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
): KoneAsynchronousHubView<TargetValue, CallbacksValue> =
    object : KoneAsynchronousHubView<TargetValue, CallbacksValue> {
        override val callbacksState: CallbacksState<TargetValue, CallbacksValue>
            get() = this@view.callbacksState.let { CallbacksState(get(it.value), it.callbacksState) }
        override fun subscribe(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHubView.Subscription =
            this@view.subscribe { callback(get(it)) }
        override fun lockSubscriptions() {
            this@view.lockSubscriptions()
        }
        @Deprecated(
            "This method will be removed when implementations will be moved to concurrent queue.",
            level = DeprecationLevel.WARNING
        )
        @Suppress("DEPRECATION")
        override fun subscribeAnyway(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHubView.Subscription =
            this@view.subscribeAnyway { callback(get(it)) }
        override fun unlockSubscriptions() {
            this@view.unlockSubscriptions()
        }
        override fun compareCallbackValue(expected: CallbacksValue): Boolean =
            this@view.compareCallbackValue(expected)
    }

public fun <Value, Result> KoneAsynchronousHubView<Value, *>.map(elementEquality: Equality<Result> = Equality.defaultFor(), transform: (Value) -> Result): KoneMutableAsynchronousHub<Result> =
    /*buildSubscriptionAtomic*/ /* FIXME: Replace when `buildSubscriptionAtomic` will be available*/
    buildSubscriptionLocking { initialValue ->
        val hub = KoneMutableAsynchronousHub(transform(initialValue), elementEquality)
        subscribe { hub.set(transform(it)) }
        hub
    }

public fun <Value> KoneAsynchronousHubView<Value, *>.toStateFlow(): StateFlow<Value> =
    /*buildSubscriptionAtomic*/ /* FIXME: Replace when `buildSubscriptionAtomic` will be available*/
    buildSubscriptionLocking  { initialValue ->
        val stateFlow = MutableStateFlow(initialValue)
        subscribe { stateFlow.value = it }
        stateFlow
    }

public interface KoneMutableAsynchronousHubView<Value, CallbacksValue> : KoneAsynchronousHubView<Value, CallbacksValue> {
    @InternalKoneHubApi
    public suspend fun acquire()
    @InternalKoneHubApi
    public suspend fun acceptNewValue(newValue: Value)
    @InternalKoneHubApi
    public fun release()
}

public typealias KoneMutableAsynchronousHub<Value> = KoneMutableAsynchronousHubView<Value, Value>

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
    // TODO: Replace with concurrent queue
    override var callbacksState: CallbacksState<Value, Value> = CallbacksState(initialValue, initialValue)
    private val callbacksLock: ReentrantLock = ReentrantLock()
    private val callbacks: KoneMutableNoddedList<suspend (Value) -> Unit> = KoneGCLinkedSizedList()
    override fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHubView.Subscription {
        callbacksLock.withLock {
            val node = callbacks.addNode(callback)
            return KoneAsynchronousHubView.Subscription {
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
    override fun subscribeAnyway(callback: suspend (Value) -> Unit): KoneAsynchronousHubView.Subscription {
        val node = callbacks.addNode(callback)
        return KoneAsynchronousHubView.Subscription {
            callbacksLock.withLock {
                node.remove()
            }
        }
    }
    override fun unlockSubscriptions() {
        callbacksLock.unlock()
    }
    override fun compareCallbackValue(expected: Value): Boolean {
        TODO("Not yet implemented")
    }
    
    private val mutex = Mutex()
    override suspend fun acquire() {
        mutex.lock()
    }
    override suspend fun acceptNewValue(newValue: Value) {
        if (elementEquality { newValue eq value }) return
        val callbacksToLaunch = callbacksLock.withLock {
            callbacksState = CallbacksState(newValue, newValue)
            callbacks.toKoneList()
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
public suspend inline fun <Value, Result> KoneMutableAsynchronousHubView<Value, *>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result {
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
public suspend inline fun <Value> KoneMutableAsynchronousHubView<Value, *>.update(transform: (Value) -> Value) {
    acquire()
    try {
        val newValue = transform(value)
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend fun <Value> KoneMutableAsynchronousHubView<Value, *>.set(newValue: Value) {
    acquire()
    try {
        acceptNewValue(newValue)
    } finally {
        release()
    }
}

@OptIn(InternalKoneHubApi::class)
public suspend inline fun <Value> KoneMutableAsynchronousHubView<Value, *>.updateAndGet(transform: (Value) -> Value): Value {
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
public suspend inline fun <Value> KoneMutableAsynchronousHubView<Value, *>.getAndUpdate(transform: (Value) -> Value): Value {
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
public inline fun <SourceValue, TargetValue, CallbacksValue> KoneMutableAsynchronousHubView<SourceValue, CallbacksValue>.view(
    crossinline get: (SourceValue) -> TargetValue,
    crossinline set: suspend (previousSourceValue: SourceValue, newTargetValue: TargetValue) -> SourceValue,
): KoneMutableAsynchronousHubView<TargetValue, CallbacksValue> =
    object : KoneMutableAsynchronousHubView<TargetValue, CallbacksValue> {
        override val callbacksState: CallbacksState<TargetValue, CallbacksValue>
            get() = this@view.callbacksState.let { CallbacksState(get(it.value), it.callbacksState) }
        override fun subscribe(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHubView.Subscription =
            this@view.subscribe { callback(get(it)) }
        override fun lockSubscriptions() {
            this@view.lockSubscriptions()
        }
        @Deprecated(
            "This method will be removed when implementations will be moved to concurrent queue.",
            level = DeprecationLevel.WARNING
        )
        @Suppress("DEPRECATION")
        override fun subscribeAnyway(callback: suspend (TargetValue) -> Unit): KoneAsynchronousHubView.Subscription =
            this@view.subscribeAnyway { callback(get(it)) }
        override fun unlockSubscriptions() {
            this@view.unlockSubscriptions()
        }
        override fun compareCallbackValue(expected: CallbacksValue): Boolean =
            this@view.compareCallbackValue(expected)

        override suspend fun acquire() {
            this@view.acquire()
        }
        override suspend fun acceptNewValue(newValue: TargetValue) {
            this@view.acceptNewValue(set(this@view.value, newValue))
        }
        override fun release() {
            this@view.release()
        }
    }