/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.repeat


public interface KoneList<out Element> : KoneLinearIterable<Element> {
    public val size: UInt
    
    public operator fun get(index: UInt): Element
    
    public fun iteratorFrom(index: UInt): KoneLinearIterator<Element>
    override fun iterator(): KoneLinearIterator<Element> = iteratorFrom(0u)
}

public interface KoneSettableList<Element> : KoneList<Element> {
    public operator fun set(index: UInt, element: Element)
}

public interface KoneMutableList<Element> : KoneSettableList<Element>, KoneMutableLinearIterable<Element> {
    public fun add(element: Element) { addAt(size, element) }
    public fun addAt(index: UInt, element: Element)
    public fun addSeveral(number: UInt, builder: (index: UInt) -> Element) {
        repeat(number) { add(builder(it)) }
    }
    public fun addSeveralAt(index: UInt, number: UInt, builder: (index: UInt) -> Element) {
        repeat(number) { addAt(index + it, builder(it)) }
    }
    
    public fun removeAt(index: UInt)
    public fun removeAllThat(predicate: (element: Element) -> Boolean) {
        removeAllThatIndexed { _, element -> predicate(element) }
    }
    public fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean)
    public fun removeAll()
    
    override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element>
    override fun iterator(): KoneMutableLinearIterator<Element> = iteratorFrom(0u)
}

public interface KoneNoddedList<out Element> : KoneList<Element> {
    public fun getNode(index: UInt): KoneListNode<Element>
    override fun get(index: UInt): Element = getNode(index).element
}

public interface KoneSettableNoddedList<Element> : KoneNoddedList<Element>, KoneSettableList<Element> {
    override fun getNode(index: UInt): KoneSettableListNode<Element>
}

public interface KoneMutableNoddedList<Element> : KoneSettableNoddedList<Element>, KoneMutableList<Element> {
    override fun getNode(index: UInt): KoneMutableListNode<Element>
    
    public fun addNode(element: Element): KoneMutableListNode<Element>
    override fun add(element: Element) { addNode(element) }
    public fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element>
    override fun addAt(index: UInt, element: Element) { addNodeAt(index, element) }
}