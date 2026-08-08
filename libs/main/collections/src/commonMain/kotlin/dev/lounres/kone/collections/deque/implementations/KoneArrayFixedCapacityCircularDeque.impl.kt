/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque.implementations

import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.capacityOverflowException
import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.emptyDequeAccessException
import dev.lounres.kone.repeat


@Suppress("UNCHECKED_CAST")
public class KoneArrayFixedCapacityCircularDeque<Element> internal constructor(
    size: UInt,
    internal val capacity: UInt,
    data: KoneMutableArray<Any?> = KoneMutableArray.generate<Any?>(capacity) { null },
    internal var start: UInt = 0u,
    internal var end: UInt = if (size > 0u) size - 1u else capacity - 1u,
) : KoneDeque<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    private var _data: KoneMutableArray<Any?>? = data
    internal val data: KoneMutableArray<Any?> get() = _data!!
    
    override fun dispose() {
        if (isDisposed) return
        var currentIndex = start
        repeat(size) {
            data[currentIndex] = null
            currentIndex++
        }
        _data = null
        isDisposed = true
    }
    
    override var size: UInt = size
        get() {
            if (isDisposed) disposedInstanceException()
            return field
        }
        private set
    
    override fun getFirst(): Element {
        if (size == 0u) emptyDequeAccessException()
        return data[start] as Element
    }
    
    override fun getLast(): Element {
        if (size == 0u) emptyDequeAccessException()
        return data[end] as Element
    }
    
    override fun addFirst(element: Element) {
        if (size == capacity) capacityOverflowException(capacity)
        start = if (start == 0u) capacity - 1u else start - 1u
        data[start] = element
        size++
    }
    
    override fun addLast(element: Element) {
        if (size == capacity) capacityOverflowException(capacity)
        end = if (end == capacity - 1u) 0u else end + 1u
        data[end] = element
        size++
    }
    
    override fun removeFirst() {
        if (size == 0u) emptyDequeAccessException()
        data[start] = null
        start = if (start == capacity - 1u) 0u else start + 1u
        size--
    }
    
    override fun removeLast() {
        if (size == 0u) emptyDequeAccessException()
        data[end] = null
        end = if (end == 0u) capacity - 1u else end - 1u
        size--
    }
    
    override fun removeAll() {
        repeat(size) {
            data[start] = null
            start = if (start == capacity - 1u) 0u else start + 1u
        }
        size = 0u
    }
}