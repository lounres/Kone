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


public enum class KoneSequenceCacheThreadSafetyMode {
    NONE, SYNCHRONIZED,
}

public fun <Element> KoneSequence<Element>.cached(mode: KoneSequenceCacheThreadSafetyMode = KoneSequenceCacheThreadSafetyMode.NONE): KoneSequence<Element> =
    when (mode) {
        KoneSequenceCacheThreadSafetyMode.NONE -> KoneCachedSequence(this)
        KoneSequenceCacheThreadSafetyMode.SYNCHRONIZED -> KoneSynchronizedCachedSequence(this)
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
    }
}