/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.DetachedNodeException
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing


/**
 * Represents a node in the inner structure of a [KoneNoddedList].
 * See [KoneNoddedList] and [KoneList] for more.
 *
 * The node can be detached by removing the corresponding place from the list it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneNoddedList
 * @see KoneList
 */
public interface KoneListNode<out Element> {
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
    /**
     * Returns index corresponding to that node.
     *
     * Be aware that the index may not be constant-time computable.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val index: UInt
    
    /**
     * Returns node corresponding to the next place from this node's corresponding place.
     * If there is no such node (i.e. this node is the last one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
     */
    public val nextNode: KoneListNode<Element>?
    /**
     * Returns node corresponding to the previous place from this node's corresponding place.
     * If there is no such node (i.e. this node is the first one), `null` is returned.
     *
     * Also, this property throws [DetachedNodeException] when the node is already detached.
     *
     * @throws DetachedNodeException when the node is already detached.
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
     * @throws DetachedNodeException when the node is already detached.
     */
    public fun iteratorFromBeforeHere(): KoneNoddedListIterator<Element>
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
     * @throws DetachedNodeException when the node is already detached.
     */
    public fun iteratorFromAfterHere(): KoneNoddedListIterator<Element>
    
    /**
     * Legacy equality operation. Must return the result of referential equality.
     *
     * See [Equality] for idiomatic replacement and use this operation with caution.
     *
     * @param other Another element to check referential equality with.
     * @return The result of legacy equality check.
     */
    override fun equals(other: Any?): Boolean
    /**
     * Legacy hash computation operation. Must return any [Int] value.
     *
     * See [Hashing] for idiomatic replacement and use this operation with caution.
     *
     * @return The result of legacy hash computation.
     */
    override fun hashCode(): Int
    /**
     * Represents the list node as a string. Must return a string in the following form.
     * ```
     * "<node name>[<element>]"
     * ```
     * "Node name" here can mean anything, but (FQ) name of the node's class with system hash code (if there is any) is enough.
     *
     * @return The string representation of the node.
     */
    override fun toString(): String
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
    
    override fun iteratorFromBeforeHere(): KoneSettableNoddedListIterator<Element>
    override fun iteratorFromAfterHere(): KoneSettableNoddedListIterator<Element>
}

/**
 * Represents a node in the inner structure of a [KoneMutableNoddedList].
 * See [KoneMutableNoddedList] and [KoneMutableList] for more.
 *
 * The node can be detached by removing corresponding place from the collection it was defined in.
 * In that case only its element is preserved and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneMutableNoddedList
 * @see KoneMutableList
 */
public interface KoneMutableListNode<Element> : KoneSettableListNode<Element> {
    /**
     * Removes the corresponding place from the list and detaches the node.
     *
     * The operation must be "throwing idempotent".
     * It means that calling this function again must throw an exception.
     * Here the exception should be [DetachedNodeException].
     */
    public fun remove()
    
    override val nextNode: KoneMutableListNode<Element>?
    override val previousNode: KoneMutableListNode<Element>?
    
    override fun iteratorFromBeforeHere(): KoneMutableNoddedListIterator<Element>
    override fun iteratorFromAfterHere(): KoneMutableNoddedListIterator<Element>
}