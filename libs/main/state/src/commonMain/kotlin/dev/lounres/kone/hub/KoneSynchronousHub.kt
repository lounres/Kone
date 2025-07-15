package dev.lounres.kone.hub

import dev.lounres.kone.automata.*
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneGCLinkedList
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.eq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.JvmInline


public abstract class KoneSynchronousHub<out Value> internal constructor() {
    @PublishedApi
    internal abstract val callbacksValue: Value
    internal val callbacks: KoneMutableNoddedList<(@UnsafeVariance Value) -> Unit> = KoneGCLinkedList() // TODO: Replace with concurrent queue
    
    internal abstract val automaton: SynchronousAutomaton<@UnsafeVariance Value, @UnsafeVariance Value, Nothing?>
    
    public fun interface Subscription {
        public fun cancel()
    }
}

public val <Value> KoneSynchronousHub<Value>.value: Value get() = automaton.state

public fun <Value> KoneSynchronousHub<Value>.subscribe(callback: (Value) -> Unit): KoneSynchronousHub.Subscription {
    val node = callbacks.addNode(callback)
    return KoneSynchronousHub.Subscription { node.remove() }
}

@JvmInline
public value class KoneSynchronousHubSubscriptionScope<out Value> @PublishedApi internal constructor(private val hub: KoneSynchronousHub<Value>) {
    public fun subscribe(callback: (Value) -> Unit): KoneSynchronousHub.Subscription {
        val node = hub.callbacks.addNode(callback)
        return KoneSynchronousHub.Subscription { node.remove() }
    }
}

public inline fun <Value, Result> KoneSynchronousHub<Value>.buildSubscription(builder: KoneSynchronousHubSubscriptionScope<Value>.(Value) -> Result): Result =
    KoneSynchronousHubSubscriptionScope(this).builder(callbacksValue)

public class KoneMutableSynchronousHub<Value>(
    initialElement: Value,
    private val elementEquality: Equality<Value>
) : KoneSynchronousHub<Value>() {
    override var callbacksValue: Value = initialElement
    
    @PublishedApi
    override val automaton: SynchronousAutomaton<Value, Value, Nothing?> =
        SynchronousAutomaton(
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

public inline fun <Value, Result> KoneMutableSynchronousHub<Value>.updateAndMap(transform: (Value) -> Value, map: (Value, Value) -> Result): Result =
    automaton.moveAndCompute { previousValue ->
        val newValue = transform(previousValue)
        val result = map(previousValue, newValue)
        TransitionAndComputation(newValue, result)
    }.computation

public inline fun <Value> KoneMutableSynchronousHub<Value>.update(transform: (Value) -> Value) {
    automaton.move { previousValue -> transform(previousValue) }
}

public var <Value> KoneMutableSynchronousHub<Value>.value: Value
    get() = automaton.state
    set(value) { automaton.move { _ -> value } }

public inline fun <Value> KoneMutableSynchronousHub<Value>.updateAndGet(transform: (Value) -> Value): Value =
    automaton.move { previousValue -> transform(previousValue) }.transition

public inline fun <Value> KoneMutableSynchronousHub<Value>.getAndUpdate(transform: (Value) -> Value): Value =
    automaton.move { previousValue -> transform(previousValue) }.previousState

public fun <Value, Result> KoneSynchronousHub<Value>.map(elementEquality: Equality<Result> = defaultEquality(), transform: (Value) -> Result): KoneMutableSynchronousHub<Result> =
    buildSubscription { initialValue ->
        val hub = KoneMutableSynchronousHub(transform(initialValue), elementEquality)
        subscribe { hub.value = transform(it) }
        hub
    }

public fun <Value> KoneSynchronousHub<Value>.toStateFlow(): StateFlow<Value> =
    buildSubscription { initialValue ->
        val stateFlow = MutableStateFlow(initialValue)
        subscribe { stateFlow.value = it }
        stateFlow
    }