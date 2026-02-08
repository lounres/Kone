/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque.implementations

import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.emptyDequeAccessException
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.last
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneListBackedDeque<Element>(internal val data: KoneMutableList<Element>) : KoneDeque<Element> {
    override val size: UInt get() = data.size
    
    override fun getFirst(): Element = if (data.isEmpty()) emptyDequeAccessException() else data.first()
    override fun getLast(): Element = if (data.isEmpty()) emptyDequeAccessException() else data.last()
    override fun addFirst(element: Element) {
        data.addAt(0u, element)
    }
    override fun addLast(element: Element) {
        data.addAt(data.size, element)
    }
    override fun removeFirst() {
        if (data.isEmpty()) emptyDequeAccessException()
        data.removeAt(0u)
    }
    override fun removeLast() {
        if (data.isEmpty()) emptyDequeAccessException()
        data.removeAt(data.lastIndex)
    }
    override fun removeAll() {
        data.removeAll()
    }
}