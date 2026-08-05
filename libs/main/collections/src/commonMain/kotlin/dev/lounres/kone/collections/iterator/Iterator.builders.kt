/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterator

import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterable.isNotEmpty
import dev.lounres.kone.collections.iterator.empty.KoneEmptySettableLinearIterator
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.sequence.KoneSequence
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.jvm.JvmInline


/**
 * Returns an empty [KoneIterator].
 *
 * @param Element The type of elements.
 * @return An empty [KoneIterator] instance.
 */
public fun <Element> KoneIterator.Companion.empty(): KoneIterator<Element> = KoneEmptySettableLinearIterator

/**
 * Builder interface for constructing [KoneIterator] instances via [KoneIterator.build].
 *
 * @param Element The type of elements produced by the iterator builder.
 */
@RestrictsSuspension
public interface KoneIteratorBuilder<in Element> {
    /**
     * Yields a single [element] to the iterator being built.
     *
     * @param element The element to yield.
     */
    public suspend fun yield(element: Element)
    /**
     * Yields all elements from the given [iterator] to the iterator being built.
     *
     * @param iterator The iterator whose elements are to be yielded.
     */
    public suspend fun yieldAll(iterator: KoneIterator<Element>)
}

/**
 * Yields all elements from the given [iterable] to the iterator being built.
 *
 * @param Element The type of elements.
 * @param iterable The iterable whose elements are to be yielded.
 */
public suspend fun <Element> KoneIteratorBuilder<Element>.yieldAll(iterable: KoneIterable<Element>) {
    if (iterable.isNotEmpty()) yieldAll(iterable.iterator())
}

/**
 * Yields all elements from the given [sequence] to the iterator being built.
 *
 * @param Element The type of elements.
 * @param sequence The sequence whose elements are to be yielded.
 */
public suspend fun <Element> KoneIteratorBuilder<Element>.yieldAll(sequence: KoneSequence<Element>) {
    yieldAll(sequence.iterator())
}

private class KoneIteratorBuilderImpl<Element>(builder: suspend KoneIteratorBuilder<Element>.() -> Unit) : KoneIteratorBuilder<Element>, KoneIterator<Element>, Continuation<Unit> {
    @JvmInline
    private value class State private constructor(val id: Byte) {
        companion object {
            val Finished = State(0)
            val Failed = State(1)
            val Waits = State(2)
            val WaitsWithElement = State(3)
            val WaitsWithNonEmptyIterator = State(4)
            val WaitsWithSomeIterator = State(4)
        }
    }
    
    private var state = State.Waits
    private var nextElement: Element? = null
    private var nextIterator: KoneIterator<Element>? = null
    private var nextStep: Continuation<Unit>? = builder.createCoroutineUnintercepted(this, this)
    
    override val context: CoroutineContext
        get() = EmptyCoroutineContext
    
    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
        state = State.Finished
    }
    
    override suspend fun yield(element: Element) {
        nextElement = element
        state = State.WaitsWithElement
        return suspendCoroutineUninterceptedOrReturn { continuation ->
            nextStep = continuation
            COROUTINE_SUSPENDED
        }
    }
    
    override suspend fun yieldAll(iterator: KoneIterator<Element>) {
        if (!iterator.hasNext()) return
        nextIterator = iterator
        state = State.WaitsWithNonEmptyIterator
        return suspendCoroutineUninterceptedOrReturn { continuation ->
            nextStep = continuation
            COROUTINE_SUSPENDED
        }
    }
    
    override fun hasNext(): Boolean {
        while (true) {
            when (state) {
                State.Finished -> return false
                State.Failed -> error("Iterator has failed.")
                State.Waits -> {}
                State.WaitsWithElement -> return true
                State.WaitsWithNonEmptyIterator -> return true
                State.WaitsWithSomeIterator -> {
                    if (nextIterator!!.hasNext()) {
                        state = State.WaitsWithNonEmptyIterator
                        return true
                    }
                    nextIterator = null
                }
                else -> error("Unexpected state of the iterator: $state")
            }
            
            state = State.Failed
            val step = nextStep!!
            nextStep = null
            step.resume(Unit)
        }
    }
    
    override fun getNext(): Element {
        if (!hasNext()) noNextElementInIteratorException()
        
        when (state) {
            State.Finished -> error("Unexpected state of the iterator: $state")
            State.Failed -> error("Unexpected state of the iterator: $state")
            State.Waits -> error("Unexpected state of the iterator: $state")
            State.WaitsWithElement -> @Suppress("UNCHECKED_CAST") return nextElement as Element
            State.WaitsWithNonEmptyIterator -> return nextIterator!!.getNext()
            State.WaitsWithSomeIterator -> error("Unexpected state of the iterator: $state")
            else -> error("Unexpected state of the iterator: $state")
        }
    }
    
    override fun moveNext() {
        if (!hasNext()) noNextElementInIteratorException()
        
        when (state) {
            State.Finished -> error("Unexpected state of the iterator: $state")
            State.Failed -> error("Unexpected state of the iterator: $state")
            State.Waits -> error("Unexpected state of the iterator: $state")
            State.WaitsWithElement -> {
                state = State.Waits
            }
            State.WaitsWithNonEmptyIterator -> {
                val iterator = nextIterator!!
                iterator.moveNext()
                if (!iterator.hasNext()) {
                    state = State.Waits
                    nextIterator = null
                }
            }
            State.WaitsWithSomeIterator -> error("Unexpected state of the iterator: $state")
            else -> error("Unexpected state of the iterator: $state")
        }
    }
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = super.hashCode()
    override fun toString(): String = "${super.toString()}[state = $state]"
}

/**
 * Builds a [KoneIterator] using the provided coroutine-based [builder] function.
 *
 * @param Element The type of elements.
 * @param builder The suspending builder function that yields elements.
 * @return A new [KoneIterator] instance.
 */
public fun <Element> KoneIterator.Companion.build(builder: suspend KoneIteratorBuilder<Element>.() -> Unit): KoneIterator<Element> =
    KoneIteratorBuilderImpl(builder)