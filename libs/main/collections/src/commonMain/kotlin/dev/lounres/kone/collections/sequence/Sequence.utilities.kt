/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.sequence

import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.noNextElementInIteratorException
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


/**
 * Thread-safety modes for caching a [KoneSequence].
 */
public enum class KoneSequenceCacheThreadSafetyMode {
    /**No thread-safety synchronization is applied.*/
    NONE,
    /**Access to the cached sequence is synchronized.*/
    SYNCHRONIZED,
}

/**
 * Returns a cached (memorized) version of this sequence.
 *
 * The elements are cached on first access and subsequent iterations reuse the cached values.
 *
 * It's permitted to create two different iterators (via [KoneSequence.iterator]) and use them at the same time.
 * But be sure to use the same thread safety semantic on them
 * (i.e. with [KoneSequenceCacheThreadSafetyMode.NONE] you should not call the iterators' methods concurrently).
 *
 * @param Element The type of elements.
 * @param mode The thread-safety mode for the cached sequence.
 * @return A cached [KoneSequence] instance.
 */
public fun <Element> KoneSequence<Element>.cached(mode: KoneSequenceCacheThreadSafetyMode = NONE): KoneSequence<Element> =
    when (mode) {
        NONE -> KoneCachedSequence(this)
        SYNCHRONIZED -> KoneSynchronizedCachedSequence(this)
    }

private class KoneCachedSequence<Element>(sequence: KoneSequence<Element>) : KoneSequence<Element> {
    private val cache = KoneArrayGrowableList<Element>()
    private var iterator: KoneIterator<Element>? = sequence.iterator()
    
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val cachedSequence: KoneCachedSequence<Element>) : KoneIterator<Element> {
        private var index = 0u
        override fun hasNext(): Boolean {
            if (cachedSequence.cache.size > index) return true
            val iterator = cachedSequence.iterator ?: return false
            if (iterator.hasNext()) return true
            cachedSequence.iterator = null
            return false
        }
        override fun getNext(): Element {
            if (cachedSequence.cache.size > index) return cachedSequence.cache[index]
            val iterator = cachedSequence.iterator ?: noNextElementInIteratorException()
            if (iterator.hasNext()) return iterator.getAndMoveNext().also { cachedSequence.cache.add(it) }
            cachedSequence.iterator = null
            noNextElementInIteratorException()
        }
        override fun moveNext() {
            if (cachedSequence.cache.size > index) {
                index++
                return
            }
            val iterator = cachedSequence.iterator ?: noNextElementInIteratorException()
            if (iterator.hasNext()) {
                cachedSequence.cache.add(iterator.getNext())
                iterator.moveNext()
                index++
                return
            }
            noNextElementInIteratorException()
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[index = $index]"
    }
}

private class KoneSynchronizedCachedSequence<Element>(sequence: KoneSequence<Element>) : SynchronizedObject(), KoneSequence<Element> {
    private val cache = KoneArrayGrowableList<Element>()
    private var iterator: KoneIterator<Element>? = sequence.iterator()
    
    override fun iterator(): KoneIterator<Element> = Iterator(this)
    
    private class Iterator<Element>(private val cachedSequence: KoneSynchronizedCachedSequence<Element>) : KoneIterator<Element> {
        private var index = 0u
        override fun hasNext(): Boolean {
            if (cachedSequence.cache.size > index) return true
            synchronized(cachedSequence) {
                val iterator = cachedSequence.iterator ?: return false
                if (iterator.hasNext()) return true
                cachedSequence.iterator = null
                return false
            }
        }
        override fun getNext(): Element {
            if (cachedSequence.cache.size > index) return cachedSequence.cache[index]
            synchronized(cachedSequence) {
                val iterator = cachedSequence.iterator ?: noNextElementInIteratorException()
                if (iterator.hasNext()) return iterator.getAndMoveNext().also { cachedSequence.cache.add(it) }
                cachedSequence.iterator = null
                noNextElementInIteratorException()
            }
        }
        override fun moveNext() {
            if (cachedSequence.cache.size > index) {
                index++
                return
            }
            synchronized(cachedSequence) {
                val iterator = cachedSequence.iterator ?: noNextElementInIteratorException()
                if (iterator.hasNext()) {
                    cachedSequence.cache.add(iterator.getNext())
                    iterator.moveNext()
                    index++
                    return
                }
                noNextElementInIteratorException()
            }
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[index = $index]"
    }
}