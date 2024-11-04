/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.repeat


public interface KoneList<out E> : KoneLinearIterable<E> {
    public val size: UInt
    
    public operator fun get(index: UInt): E
    
    public fun iteratorFrom(index: UInt): KoneLinearIterator<E>
    override fun iterator(): KoneLinearIterator<E> = iteratorFrom(0u)
}

public interface KoneSettableList<E> : KoneList<E> {
    public operator fun set(index: UInt, element: E)
}

public interface KoneMutableList<E> : KoneSettableList<E>, KoneMutableLinearIterable<E> {
    public fun add(element: E) { addAt(size, element) }
    public fun addAt(index: UInt, element: E)
    public fun addSeveral(number: UInt, builder: (index: UInt) -> E) {
        repeat(number) { add(builder(it)) }
    }
    public fun addSeveralAt(index: UInt, number: UInt, builder: (index: UInt) -> E) {
        repeat(number) { addAt(index + it, builder(it)) }
    }
    
    public fun removeAt(index: UInt)
    public fun removeAllThat(predicate: (element: E) -> Boolean) {
        removeAllThatIndexed { _, element -> predicate(element) }
    }
    public fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean)
    public fun removeAll()
    
    override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E>
    override fun iterator(): KoneMutableLinearIterator<E> = iteratorFrom(0u)
}

public interface KoneNoddedList<out E> : KoneList<E> {
    public fun getNode(index: UInt): KoneListNode<E>
    override fun get(index: UInt): E = getNode(index).element
}

public interface KoneNoddedSettableList<E> : KoneNoddedList<E>, KoneSettableList<E> {
    override fun getNode(index: UInt): KoneSettableListNode<E>
}

public interface KoneNoddedMutableList<E> : KoneNoddedList<E>, KoneMutableList<E> {
    public fun addNode(element: E): KoneMutableListNode<E>
    override fun add(element: E) { addNode(element) }
    public fun addNodeAt(index: UInt, element: E): KoneMutableListNode<E>
    override fun addAt(index: UInt, element: E) { addNodeAt(index, element) }
    
    override fun getNode(index: UInt): KoneMutableListNode<E>
}