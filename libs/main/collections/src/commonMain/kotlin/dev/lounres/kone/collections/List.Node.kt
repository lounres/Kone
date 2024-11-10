/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


/**
 * Represents a node in the inner structure of a [KoneNoddedList].
 * See [KoneNoddedList] and [KoneList] for more.
 *
 * The node can be detached by removing corresponding place from the collection it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneNoddedList
 * @see KoneList
 */
public interface KoneListNode<out Element> {
    /**
     * Returns element corresponding to that node.
     *
     * When detached (that happens only when the corresponding place is removed)
     * returns last value that was in the removed place.
     */
    public val element: Element
    /**
     * Returns index corresponding to that node.
     *
     * Be aware that the index may not be constant-time computable.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws [DetachedNodeException] when the node is already detached.
     */
    public val index: UInt
    
    /**
     * Returns node corresponding to the next place from this node's corresponding place.
     * If there is no such node (i.e. this node is the last one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws [DetachedNodeException] when the node is already detached.
     */
    public val nextNode: KoneListNode<Element>?
    /**
     * Returns node corresponding to the previous place from this node's corresponding place.
     * If there is no such node (i.e. this node is the first one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws [DetachedNodeException] when the node is already detached.
     */
    public val previousNode: KoneListNode<Element>?
    
    /**
     * Initiates an iterator over corresponding [KoneNoddedList]
     * with cursor placed right before this node.
     *
     * It is equivalent to
     * ```
     * thisList.iteratorFrom(thisNode.index)
     * ```
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws [DetachedNodeException] when the node is already detached.
     */
    public fun iteratorFromBeforeHere(): KoneLinearIterator<Element>
    /**
     * Initiates an iterator over corresponding [KoneNoddedList]
     * with cursor placed right after this node.
     *
     * It is equivalent to
     * ```
     * thisList.iteratorFrom(thisNode.index + 1u)
     * ```
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws [DetachedNodeException] when the node is already detached.
     */
    public fun iteratorFromAfterHere(): KoneLinearIterator<Element>
}

/**
 * Represents a node in the inner structure of a [KoneSettableNoddedList].
 * See [KoneSettableNoddedList] and [KoneSettableList] for more.
 *
 * The node can be detached by removing corresponding place from the collection it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneSettableNoddedList
 * @see KoneSettableList
 */
public interface KoneSettableListNode<Element> : KoneListNode<Element> {
    /**
     * Its getter returns element corresponding to that node
     * and its setter assigns new element to this node.
     *
     * When detached (that happens only when the corresponding place is removed)
     * returns last value that was in the removed place. In that case no assignation is provided.
     */
    override var element: Element
    
    override val nextNode: KoneSettableListNode<Element>?
    override val previousNode: KoneSettableListNode<Element>?
    
    override fun iteratorFromBeforeHere(): KoneSettableLinearIterator<Element>
    override fun iteratorFromAfterHere(): KoneSettableLinearIterator<Element>
}

public interface KoneMutableListNode<Element> : KoneSettableListNode<Element> {
    /**
     * Removes corresponding place from the list and detaches the node.
     */
    public fun remove()
    
    override val nextNode: KoneMutableListNode<Element>?
    override val previousNode: KoneMutableListNode<Element>?
    
    override fun iteratorFromBeforeHere(): KoneMutableLinearIterator<Element>
    override fun iteratorFromAfterHere(): KoneMutableLinearIterator<Element>
}