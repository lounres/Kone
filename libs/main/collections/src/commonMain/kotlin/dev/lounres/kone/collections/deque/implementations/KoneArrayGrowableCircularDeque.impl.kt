/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque.implementations

import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.emptyDequeAccessException
import dev.lounres.kone.collections.implementations.MAX_CAPACITY
import dev.lounres.kone.collections.implementations.powerOf2ArraySizeGreaterOrEqualTo
import dev.lounres.kone.repeat


@Suppress("UNCHECKED_CAST")
public class KoneArrayGrowableCircularDeque<Element> internal constructor(
    size: UInt,
    internal var sizeUpperBound: UInt = powerOf2ArraySizeGreaterOrEqualTo(size),
    data: KoneMutableArray<Any?> = KoneMutableArray.generate<Any?>(sizeUpperBound) { null },
    internal var start: UInt = 0u,
    internal var end: UInt = if (size > 0u) size - 1u else sizeUpperBound - 1u,
) : KoneDeque<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Any?>? = data
    internal var data: KoneMutableArray<Any?>
        get() = _data!!
        set(value) { _data = value }
    
    private fun KoneMutableArray<in Nothing?>.dispose(size: UInt) {
        var currentActualIndexToClear = start
        repeat(size) {
            this[currentActualIndexToClear] = null
            currentActualIndexToClear = if (currentActualIndexToClear == this.size - 1u) 0u else currentActualIndexToClear + 1u
        }
    }
    override fun dispose() {
        if (isDisposed) return
        data.dispose(size)
        _data = null
        isDisposed = true
    }
    
    override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set
    
    private fun reinitializeBounds(newSize: UInt) {
        if (newSize > MAX_CAPACITY) throw IllegalArgumentException("KoneGrowableArrayList implementation can not allocate array of size more than 2^31")
        if (newSize > sizeUpperBound) {
            while (newSize > sizeUpperBound) {
                sizeUpperBound = if (sizeUpperBound == 0u) 1u else sizeUpperBound shl 1
            }
        }
    }
    private inline fun reinitializeData(oldSize: UInt = this.size, newDataSize: UInt = sizeUpperBound, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        val oldData = data
        data = KoneMutableArray.generate(newDataSize) { oldData.generator(it) }
        oldData.dispose(oldSize)
        start = 0u
    }
    private inline fun reinitializeBoundsAndData(newSize: UInt, generator: KoneMutableArray<Any?>.(index: UInt) -> Any?) {
        reinitializeBounds(newSize)
        reinitializeData(generator = generator)
        size = newSize
        end = if (size > 0u) size - 1u else sizeUpperBound - 1u
    }
    
    override fun getFirst(): Element {
        if (size == 0u) emptyDequeAccessException()
        return data[start] as Element
    }
    
    override fun getLast(): Element {
        if (size == 0u) emptyDequeAccessException()
        return data[end] as Element
    }
    
    override fun addFirst(element: Element) {
        if (size == sizeUpperBound) {
            val oldSize = size
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it == 0u -> element
                    it <= oldSize -> get(actualIndex).also { actualIndex = if (actualIndex == size - 1u) 0u else actualIndex + 1u }
                    else -> null
                }
            }
        } else {
            start = if (start == 0u) sizeUpperBound - 1u else start - 1u
            data[start] = element
            size++
        }
    }
    
    override fun addLast(element: Element) {
        if (size == sizeUpperBound) {
            val oldSize = size
            var actualIndex = start
            reinitializeBoundsAndData(size + 1u) {
                when {
                    it < oldSize -> get(actualIndex).also { actualIndex = if (actualIndex == size - 1u) 0u else actualIndex + 1u }
                    it == oldSize -> element
                    else -> null
                }
            }
        } else {
            end = if (end == sizeUpperBound - 1u) 0u else end + 1u
            data[end] = element
            size++
        }
    }
    
    override fun removeFirst() {
        if (size == 0u) emptyDequeAccessException()
        data[start] = null
        start = if (start == sizeUpperBound - 1u) 0u else start + 1u
        size--
    }
    
    override fun removeLast() {
        if (size == 0u) emptyDequeAccessException()
        data[end] = null
        end = if (end == 0u) sizeUpperBound - 1u else end - 1u
        size--
    }
    
    override fun removeAll() {
        repeat(size) {
            data[start] = null
            start = if (start == sizeUpperBound - 1u) 0u else start + 1u
        }
        size = 0u
    }
}