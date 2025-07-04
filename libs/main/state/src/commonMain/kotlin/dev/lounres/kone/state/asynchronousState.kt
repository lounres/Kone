package dev.lounres.kone.state

import dev.lounres.kone.automata.AsynchronousAutomaton
import dev.lounres.kone.automata.CheckResult
import dev.lounres.kone.automata.MovementMaybeResult
import dev.lounres.kone.automata.TransitionOrReason
import dev.lounres.kone.automata.move
import dev.lounres.kone.automata.moveMaybe
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneGCLinkedList
import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.eq
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


public interface KoneAsynchronousState<out Value> {
    public val value: Value
    
    public fun subscribe(observer: suspend (Value) -> Unit): Cancellation
    
    public fun interface Cancellation {
        public fun cancel()
    }
}

public interface KoneMutableAsynchronousState<Value> : KoneAsynchronousState<Value> {
    public suspend fun set(value: Value)
    
    public suspend fun compareAndSet(expected: Value, new: Value): Boolean
}

public fun <Value> KoneMutableAsynchronousState(
    initialElement: Value,
    elementEquality: Equality<Value> = defaultEquality()
): KoneMutableAsynchronousState<Value> = KoneMutableAsynchronousStateImpl(initialElement, elementEquality)

internal class KoneMutableAsynchronousStateImpl<Value>(
    initialElement: Value,
    private val elementEquality: Equality<Value>
) : KoneMutableAsynchronousState<Value> {
    private val observersLock = ReentrantLock() // TODO: Maybe it should replaced with reentrant lock or concurrent queue
    private val observers: KoneMutableNoddedList<suspend (Value) -> Unit> = KoneGCLinkedList()
    private val automaton =
        AsynchronousAutomaton<Value, Value, Nothing?>(
            initialState = initialElement,
            checkTransition = { previousState, transition ->
                if (elementEquality { previousState eq transition }) CheckResult.Failure(null)
                else CheckResult.Success(transition)
            },
            onTransition = { _, _, nextState ->
                observersLock.withLock { observers.toKoneList() }.forEach { it(nextState) }
            }
        )
    
    override val value: Value
        get() = automaton.state
    
    override suspend fun set(value: Value) {
        automaton.move(value)
    }
    
    override suspend fun compareAndSet(expected: Value, new: Value): Boolean =
        automaton.moveMaybe { current ->
            if (elementEquality { current eq expected }) TransitionOrReason.Success(new)
            else TransitionOrReason.Failure(null)
        } is MovementMaybeResult.Success
    
    override fun subscribe(observer: suspend (Value) -> Unit): KoneAsynchronousState.Cancellation =
        observersLock.withLock {
            val node = observers.addNode(observer)
            KoneAsynchronousState.Cancellation {
                observersLock.withLock {
                    node.remove()
                }
            }
        }
}

public suspend fun <Value> KoneMutableAsynchronousState<Value>.update(function: (Value) -> Value) {
    while (true) {
        val previousElement = value
        val nextElement = function(previousElement)
        
        if (compareAndSet(previousElement, nextElement)) return
    }
}

public suspend fun <Value> KoneMutableAsynchronousState<Value>.updateAndGet(function: (Value) -> Value): Value {
    while (true) {
        val previousElement = value
        val nextElement = function(previousElement)
        
        if (compareAndSet(previousElement, nextElement)) return nextElement
    }
}

public suspend fun <Value> KoneMutableAsynchronousState<Value>.getAndUpdate(function: (Value) -> Value): Value {
    while (true) {
        val previousElement = value
        val nextElement = function(previousElement)
        
        if (compareAndSet(previousElement, nextElement)) return previousElement
    }
}

public suspend fun <Value, Result> KoneAsynchronousState<Value>.map(elementEquality: Equality<Result> = defaultEquality(), transform: (Value) -> Result): KoneMutableAsynchronousState<Result> {
    val temporaryMutex = Mutex()
    temporaryMutex.withLock {
        val temporarySubscription = subscribe { temporaryMutex.withLock {  } }
        val result = KoneMutableAsynchronousState(transform(value), elementEquality)
        subscribe { result.set(transform(it)) }
        temporarySubscription.cancel()
        return result
    }
}

public suspend fun <Value> KoneAsynchronousState<Value>.toStateFlow(): StateFlow<Value> {
    val temporaryMutex = Mutex()
    temporaryMutex.withLock {
        val temporarySubscription = subscribe { temporaryMutex.withLock {  } }
        val result = MutableStateFlow(value)
        subscribe { result.value = it }
        temporarySubscription.cancel()
        return result
    }
}