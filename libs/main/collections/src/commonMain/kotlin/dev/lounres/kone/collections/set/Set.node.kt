/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.DetachedNodeException


/**
 * Represents a node in the inner structure of a [KoneNoddedSet].
 * See [KoneNoddedSet] and [KoneSet] for more.
 *
 * The node can be detached by removing the corresponding element from the set it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneNoddedSet
 * @see KoneSet
 */
public interface KoneSetNode<out Element> {
    /**
     * Indicates if the node is detached from the structure it was a part of.
     */
    public val isDetached: Boolean
    
    /**
     * Returns element corresponding to that node.
     *
     * When detached (that happens only when the corresponding place is removed)
     * returns the last value in the removed place.
     */
    public val element: Element
}

/**
 * Represents a node in the inner structure of a [KoneMutableNoddedSet].
 * See [KoneMutableNoddedSet] and [KoneSetNode] for more.
 *
 * The node can be detached by removing the corresponding element from the set it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneMutableNoddedSet
 * @see KoneSetNode
 */
public interface KoneMutableSetNode<out Element> : KoneSetNode<Element> {
    public fun remove()
}

/**
 * Represents a node in the inner structure of a [KoneLinkedNoddedSet].
 * See [KoneLinkedNoddedSet] and [KoneSetNode] for more.
 *
 * The node can be detached by removing the corresponding element from the set it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneLinkedNoddedSet
 * @see KoneSetNode
 */
public interface KoneLinkedSetNode<out Element> : KoneSetNode<Element> {
    /**
     * Returns node corresponding to the next place from this node's corresponding place.
     * If there is no such node (i.e. this node is the last one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val nextNode: KoneLinkedSetNode<Element>?
    /**
     * Returns node corresponding to the previous place from this node's corresponding place.
     * If there is no such node (i.e. this node is the first one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val previousNode: KoneLinkedSetNode<Element>?
    
    /**
     * Initiates an iterator over corresponding [KoneLinkedNoddedSet]
     * with cursor placed right before this node.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public fun iteratorFromBeforeHere(): KoneLinkedNoddedSetIterator<Element>
    /**
     * Initiates an iterator over corresponding [KoneLinkedNoddedSet]
     * with cursor placed right after this node.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public fun iteratorFromAfterHere(): KoneLinkedNoddedSetIterator<Element>
}

/**
 * Represents a node in the inner structure of a [KoneMutableLinkedNoddedSet].
 * See [KoneMutableLinkedNoddedSet] and [KoneSetNode] for more.
 *
 * The node can be detached by removing the corresponding element from the set it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneMutableLinkedNoddedSet
 * @see KoneSetNode
 */
public interface KoneMutableLinkedSetNode<out Element> : KoneMutableSetNode<Element>, KoneLinkedSetNode<Element> {
    override val nextNode: KoneMutableLinkedSetNode<Element>?
    override val previousNode: KoneMutableLinkedSetNode<Element>?
    
    override fun iteratorFromBeforeHere(): KoneMutableLinkedNoddedSetIterator<Element>
    override fun iteratorFromAfterHere(): KoneMutableLinkedNoddedSetIterator<Element>
}