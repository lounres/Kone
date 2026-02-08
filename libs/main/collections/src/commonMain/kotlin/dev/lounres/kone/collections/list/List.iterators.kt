/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.NoFollowingElementInIteratorException
import dev.lounres.kone.collections.iterables.KoneLinearIterator
import dev.lounres.kone.collections.iterables.KoneMutableLinearIterator
import dev.lounres.kone.collections.iterables.KoneSettableLinearIterator


/**
 * Represents iterator over [KoneList] with iteration order coincident with the list elements order.
 * See [KoneLinearIterator] for more.
 */
public typealias KoneListIterator<Element> = KoneLinearIterator<Element>

/**
 * Represents iterator over [KoneSettableList] with iteration order coincident with the list elements order.
 * See [KoneSettableLinearIterator] for more.
 */
public typealias KoneSettableListIterator<Element> = KoneSettableLinearIterator<Element>

/**
 * Represents iterator over [KoneMutableList] with iteration order coincident with the list elements order.
 * See [KoneMutableLinearIterator] for more.
 */
public typealias KoneMutableListIterator<Element> = KoneMutableLinearIterator<Element>

/**
 * Represents iterator over [KoneNoddedList].
 */
public interface KoneNoddedListIterator<out Element> : KoneListIterator<Element> {
    /**
     * Returns a node corresponding to the next element
     * or throws [NoFollowingElementInIteratorException] if there is no next element.
     */
    public fun getNextNode(): KoneListNode<Element>
    /**
     * Returns a node corresponding to the previous element
     * or throws [NoFollowingElementInIteratorException] if there is no next element.
     */
    public fun getPreviousNode(): KoneListNode<Element>
}

/**
 * Represents iterator over [KoneSettableNoddedList].
 */
public interface KoneSettableNoddedListIterator<Element> : KoneNoddedListIterator<Element>, KoneSettableListIterator<Element> {
    override fun getNextNode(): KoneSettableListNode<Element>
    override fun getPreviousNode(): KoneSettableListNode<Element>
}

/**
 * Represents iterator over [KoneMutableNoddedList].
 */
public interface KoneMutableNoddedListIterator<Element> : KoneSettableNoddedListIterator<Element>, KoneMutableListIterator<Element> {
    override fun getNextNode(): KoneMutableListNode<Element>
    override fun getPreviousNode(): KoneMutableListNode<Element>
}