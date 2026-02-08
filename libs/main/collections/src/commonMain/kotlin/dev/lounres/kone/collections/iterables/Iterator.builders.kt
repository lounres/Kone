/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterables

import dev.lounres.kone.collections.iterables.empty.KoneEmptySettableLinearIterator
import dev.lounres.kone.collections.noNextElementInIteratorException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.jvm.JvmInline


public fun <Element> KoneIterator.Companion.empty(): KoneIterator<Element> = KoneEmptySettableLinearIterator

@RestrictsSuspension
public interface KoneIteratorBuilder<in Element> {
    public suspend fun yield(element: Element)
    public suspend fun yieldAll(iterator: KoneIterator<Element>)
}

public suspend fun <Element> KoneIteratorBuilder<Element>.yieldAll(iterable: KoneIterable<Element>) {
    if (iterable.isNotEmpty()) yieldAll(iterable.iterator())
}

public suspend fun <Element> KoneIteratorBuilder<Element>.yieldAll(sequence: KoneSequence<Element>) {
    yieldAll(sequence.iterator())
}

private class KoneIteratorBuilderImpl<Element>(builder: suspend KoneIteratorBuilder<Element>.() -> Unit) : KoneIteratorBuilder<Element>, KoneIterator<Element>, Continuation<Unit> {
    @JvmInline
    private value class State private constructor(val id: Int) {
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
}

public fun <Element> KoneIterator.Companion.build(builder: suspend KoneIteratorBuilder<Element>.() -> Unit): KoneIterator<Element> =
    KoneIteratorBuilderImpl(builder)