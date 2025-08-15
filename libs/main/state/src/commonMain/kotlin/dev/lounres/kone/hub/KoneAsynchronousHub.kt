/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hub

import dev.lounres.kone.automata.AsynchronousAutomaton
import dev.lounres.kone.automata.CheckResult
import dev.lounres.kone.automata.TransitionAndComputation
import dev.lounres.kone.automata.move
import dev.lounres.kone.automata.moveAndCompute
import dev.lounres.kone.collections.list.KoneMutableNoddedList
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
import kotlin.jvm.JvmInline


public abstract class KoneAsynchronousHub<out Value> internal constructor() {
    public abstract val value: Value

    @PublishedApi
    internal val callbacksLock: ReentrantLock = ReentrantLock()
    @PublishedApi
    internal abstract val callbacksValue: Value
    internal val callbacks: KoneMutableNoddedList<suspend (@UnsafeVariance Value) -> Unit> = KoneGCLinkedSizedList() // TODO: Replace with concurrent queue

    public fun interface Subscription {
        public fun cancel()
    }
}

public fun <Value> KoneAsynchronousHub<Value>.subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription =
    callbacksLock.withLock {
        val node = callbacks.addNode(callback)
        KoneAsynchronousHub.Subscription {
            callbacksLock.withLock {
                node.remove()
            }
        }
    }

@JvmInline
public value class KoneAsynchronousHubSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneAsynchronousHub<Value>) {
    @IgnorableReturnValue
    public fun subscribe(callback: suspend (Value) -> Unit): KoneAsynchronousHub.Subscription {
        val node = hub.callbacks.addNode(callback)
        return KoneAsynchronousHub.Subscription {
            hub.callbacksLock.withLock {
                node.remove()
            }
        }
    }
}

public inline fun <Value, Result> KoneAsynchronousHub<Value>.buildSubscription(builder: KoneAsynchronousHubSubscriptionScope<Value>.(Value) -> Result): Result =
    callbacksLock.withLock {
        KoneAsynchronousHubSubscriptionScope(this).builder(callbacksValue)
    }

public class KoneMutableAsynchronousHub<Value>(
    initialValue: Value,
    private val elementEquality: Equality<Value> = Equality.defaultFor(),
) : KoneAsynchronousHub<Value>() {
    override var callbacksValue: Value = initialValue

    @PublishedApi
    internal val automaton: AsynchronousAutomaton<Value, Value, Nothing?> =
        AsynchronousAutomaton(
            initialState = initialValue,
            checkTransition = { previousState, transition ->
                if (elementEquality { previousState eq transition }) CheckResult.Failure(null)
                else CheckResult.Success(transition)
            },
            onTransition = { _, _, nextState ->
                val callbacksToLaunch = callbacksLock.withLock {
                    callbacksValue = nextState
                    callbacks.toKoneList()
                }
                supervisorScope {
                    callbacksToLaunch.forEach { callback ->
                        launch { callback(nextState) }
                    }
                }
            }
        )

    override val value: Value
        get() = automaton.state
}

public suspend inline fun <Value, Result> KoneMutableAsynchronousHub<Value>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result =
    automaton.moveAndCompute { previousValue ->
        val newValue = transform(previousValue)
        val result = map(previousValue, newValue)
        TransitionAndComputation(newValue, result)
    }.computation

public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.update(transform: (Value) -> Value) {
    automaton.move { previousValue -> transform(previousValue) }
}

public suspend fun <Value> KoneMutableAsynchronousHub<Value>.set(newValue: Value) {
    automaton.move { _ -> newValue }
}

public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.updateAndGet(transform: (Value) -> Value): Value =
    automaton.move { previousValue -> transform(previousValue) }.transition

public suspend inline fun <Value> KoneMutableAsynchronousHub<Value>.getAndUpdate(transform: (Value) -> Value): Value =
    automaton.move { previousValue -> transform(previousValue) }.previousState

public fun <Value, Result> KoneAsynchronousHub<Value>.map(elementEquality: Equality<Result> = Equality.defaultFor(), transform: (Value) -> Result): KoneMutableAsynchronousHub<Result> =
    buildSubscription { initialValue ->
        val hub = KoneMutableAsynchronousHub(transform(initialValue), elementEquality)
        subscribe { hub.set(transform(it)) }
        hub
    }

public fun <Value> KoneAsynchronousHub<Value>.toStateFlow(): StateFlow<Value> =
    buildSubscription { initialValue ->
        val stateFlow = MutableStateFlow(initialValue)
        subscribe { stateFlow.value = it }
        stateFlow
    }