/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.NoFollowingElementInIteratorException
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneRemovableIterator
import dev.lounres.kone.collections.iterables.KoneReversibleIterator
import dev.lounres.kone.collections.iterables.KoneReversibleRemovableIterator


/**
 * Represents iterator over [KoneSet] with iteration order coincident with the list elements order.
 * See [KoneIterator] for more.
 */
public typealias KoneSetIterator<Element> = KoneIterator<Element>

/**
 * Represents iterator over [KoneMutableSet] with iteration order coincident with the list elements order.
 * See [KoneRemovableIterator] for more.
 */
public typealias KoneMutableSetIterator<Element> = KoneRemovableIterator<Element>

/**
 * Represents iterator over [KoneNoddedSet].
 */
public interface KoneNoddedSetIterator<out Element> : KoneSetIterator<Element> {
    /**
     * Returns a node corresponding to the next element
     * or throws [NoFollowingElementInIteratorException] if there is no next element.
     */
    public fun getNextNode(): KoneSetNode<Element>
}

/**
 * Represents iterator over [KoneMutableNoddedSet].
 */
public interface KoneMutableNoddedSetIterator<out Element> : KoneNoddedSetIterator<Element>, KoneMutableSetIterator<Element> {
    override fun getNextNode(): KoneMutableSetNode<Element>
}

/**
 * Represents iterator over [KoneLinkedSet] with iteration order coincident with the list elements order.
 * See [KoneReversibleIterator] for more.
 */
public typealias KoneLinkedSetIterator<Element> = KoneReversibleIterator<Element>

/**
 * Represents iterator over [KoneMutableLinkedSet] with iteration order coincident with the list elements order.
 * See [KoneReversibleRemovableIterator] for more.
 */
public typealias KoneMutableLinkedSetIterator<Element> = KoneReversibleRemovableIterator<Element>

/**
 * Represents iterator over [KoneLinkedNoddedSet].
 */
public interface KoneLinkedNoddedSetIterator<out Element> : KoneLinkedSetIterator<Element>, KoneNoddedSetIterator<Element> {
    override fun getNextNode(): KoneLinkedSetNode<Element>
    /**
     * Returns a node corresponding to the previous element
     * or throws [NoFollowingElementInIteratorException] if there is no next element.
     */
    public fun getPreviousNode(): KoneLinkedSetNode<Element>
}

/**
 * Represents iterator over [KoneMutableLinkedNoddedSet].
 */
public interface KoneMutableLinkedNoddedSetIterator<out Element> : KoneLinkedNoddedSetIterator<Element>, KoneMutableLinkedSetIterator<Element>, KoneMutableNoddedSetIterator<Element> {
    override fun getNextNode(): KoneMutableLinkedSetNode<Element>
    override fun getPreviousNode(): KoneMutableLinkedSetNode<Element>
}