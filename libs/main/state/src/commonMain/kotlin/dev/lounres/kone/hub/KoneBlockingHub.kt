/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hub

import dev.lounres.kone.automata.*
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneGCLinkedList
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.eq
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.JvmInline


public abstract class KoneBlockingHub<out Value> internal constructor() {
    @PublishedApi
    internal val callbacksLock: ReentrantLock = ReentrantLock()
    @PublishedApi
    internal abstract val callbacksValue: Value
    internal val callbacks: KoneMutableNoddedList<(@UnsafeVariance Value) -> Unit> = KoneGCLinkedList() // TODO: Replace with concurrent queue
    
    internal abstract val automaton: BlockingAutomaton<@UnsafeVariance Value, @UnsafeVariance Value, Nothing?>
    
    public fun interface Subscription {
        public fun cancel()
    }
}

public val <Value> KoneBlockingHub<Value>.value: Value get() = automaton.state

public fun <Value> KoneBlockingHub<Value>.subscribe(callback: (Value) -> Unit): KoneBlockingHub.Subscription =
    callbacksLock.withLock {
        val node = callbacks.addNode(callback)
        KoneBlockingHub.Subscription {
            callbacksLock.withLock {
                node.remove()
            }
        }
    }

@JvmInline
public value class KoneBlockingHubSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneBlockingHub<Value>) {
    public fun subscribe(callback: (Value) -> Unit): KoneBlockingHub.Subscription {
        val node = hub.callbacks.addNode(callback)
        return KoneBlockingHub.Subscription {
            hub.callbacksLock.withLock {
                node.remove()
            }
        }
    }
}

public inline fun <Value, Result> KoneBlockingHub<Value>.buildSubscription(builder: KoneBlockingHubSubscriptionScope<Value>.(Value) -> Result): Result =
    callbacksLock.withLock {
        KoneBlockingHubSubscriptionScope(this).builder(callbacksValue)
    }

public class KoneMutableBlockingHub<Value>(
    initialElement: Value,
    private val elementEquality: Equality<Value> = defaultEquality(),
) : KoneBlockingHub<Value>() {
    override var callbacksValue: Value = initialElement
    
    @PublishedApi
    override val automaton: BlockingAutomaton<Value, Value, Nothing?> =
        BlockingAutomaton(
            initialState = initialElement,
            checkTransition = { previousState, transition ->
                if (elementEquality { previousState eq transition }) CheckResult.Failure(null)
                else CheckResult.Success(transition)
            },
            onTransition = { _, _, nextState ->
                callbacksValue = nextState
                callbacks.forEach { it(nextState) }
            }
        )
}

public inline fun <Value, Result> KoneMutableBlockingHub<Value>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result =
    automaton.moveAndCompute { previousValue ->
        val newValue = transform(previousValue)
        val result = map(previousValue, newValue)
        TransitionAndComputation(newValue, result)
    }.computation

public inline fun <Value> KoneMutableBlockingHub<Value>.update(transform: (Value) -> Value) {
    automaton.move { previousValue -> transform(previousValue) }
}

public var <Value> KoneMutableBlockingHub<Value>.value: Value
    get() = automaton.state
    set(value) { automaton.move { _ -> value } }

public inline fun <Value> KoneMutableBlockingHub<Value>.updateAndGet(transform: (Value) -> Value): Value =
    automaton.move { previousValue -> transform(previousValue) }.transition

public inline fun <Value> KoneMutableBlockingHub<Value>.getAndUpdate(transform: (Value) -> Value): Value =
    automaton.move { previousValue -> transform(previousValue) }.previousState

public fun <Value, Result> KoneBlockingHub<Value>.map(elementEquality: Equality<Result> = defaultEquality(), transform: (Value) -> Result): KoneMutableBlockingHub<Result> =
    buildSubscription { initialValue ->
        val hub = KoneMutableBlockingHub(transform(initialValue), elementEquality)
        subscribe { hub.value = transform(it) }
        hub
    }

public fun <Value> KoneBlockingHub<Value>.toStateFlow(): StateFlow<Value> =
    buildSubscription { initialValue ->
        val stateFlow = MutableStateFlow(initialValue)
        subscribe { stateFlow.value = it }
        stateFlow
    }