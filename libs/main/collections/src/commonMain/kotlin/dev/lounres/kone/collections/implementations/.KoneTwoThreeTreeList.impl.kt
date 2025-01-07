/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.KoneArray
import dev.lounres.kone.collections.KoneMutableListNode
import dev.lounres.kone.collections.KoneMutableNoddedList
import dev.lounres.kone.collections.KoneMutableNoddedListIterator
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.collections.isEmpty
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.noPreviousElementInIteratorException
import kotlinx.serialization.Serializable


@Serializable(with = KoneTwoThreeTreeListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneTwoThreeTreeList<Element> internal constructor(
    internal var rootHolder: NodeHolder<Element>? = null,
    internal var firstNode: Node<Element>? = null,
    internal var lastNode: Node<Element>? = null,
    size: UInt = 0u,
) : KoneMutableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    override var size: UInt = size
        private set
    
    override fun dispose() {
        if (isDisposed) return
        var currentNode = firstNode
        while (currentNode != null) {
            currentNode.holder.dispose()
            currentNode = currentNode.nextNode?.also { it.detach() }
        }
        rootHolder = null
        firstNode = null
        lastNode = null
        size = 0u
        isDisposed = true
    }
    
    public companion object {
        internal val NodeHolder<*>?.size: UInt
            get() = when (this) {
                null -> 0U
                is TwoNodeHolder<*> -> firstChildSize + 1u + secondChildSize
                is ThreeNodeHolder<*> -> firstChildSize + 1u + secondChildSize + 1u + thirdChildSize
            }
    }
    
    internal data class FollowingSubtree<Element>(
        val element: Node<Element>,
        val subtree: NodeHolder<Element>,
    )
    
    internal tailrec fun createTree(
        firstSubtree: NodeHolder<Element>,
        rest: KoneArray<FollowingSubtree<Element>>,
    ): NodeHolder<Element> {
        if (rest.isEmpty()) return firstSubtree
        
        val newFirstSubTree: NodeHolder<Element> =
            if (rest.size % 2u == 1u) {
                val continuation = rest[0u]
                TwoNodeHolder<Element>(
                    isItBottom = false,
                    firstChild = firstSubtree,
                    element = continuation.element,
                    secondChild = continuation.subtree,
                ).also {
                    firstSubtree.parent = it
                    continuation.element.holder = it
                    continuation.subtree.parent = it
                }
            } else {
                val continuation1 = rest[0u]
                val continuation2 = rest[1u]
                ThreeNodeHolder<Element>(
                    isItBottom = false,
                    firstChild = firstSubtree,
                    firstElement = continuation1.element,
                    secondChild = continuation1.subtree,
                    secondElement = continuation2.element,
                    thirdChild = continuation2.subtree,
                ).also {
                    firstSubtree.parent = it
                    continuation1.element.holder = it
                    continuation1.subtree.parent = it
                    continuation2.element.holder = it
                    continuation2.subtree.parent = it
                }
            }
        newFirstSubTree.tree = this
        val start = if (rest.size % 2u == 1u) 1u else 2u
        val newRest = KoneArray((rest.size - start) / 2u) {
            val continuation1 = rest[start + it * 2u]
            val continuation2 = rest[start + it * 2u + 1u]
            FollowingSubtree(
                continuation1.element,
                TwoNodeHolder(
                    isItBottom = false,
                    firstChild = continuation1.subtree,
                    element = continuation2.element,
                    secondChild = continuation2.subtree,
                ).also {
                    it.tree = this
                    continuation1.subtree.parent = it
                    continuation2.element.holder = it
                    continuation2.subtree.parent = it
                }
            )
        }
        return createTree(newFirstSubTree, newRest)
    }
    
    internal fun createTree(
        elements: KoneArray<Node<Element>>,
    ): NodeHolder<Element>? =
        when {
            elements.isEmpty() -> null
            elements.size % 2u == 1u -> {
                val startNode = elements[0u]
                createTree(
                    firstSubtree = TwoNodeHolder<Element>(
                        isItBottom = true,
                        firstChild = null,
                        element = startNode,
                        secondChild = null,
                    ).also {
                        it.tree = this
                        startNode.holder = it
                    },
                    rest = KoneArray(elements.size / 2u) {
                        val intermediateNode = elements[it * 2u + 1u]
                        val wrappedNode = elements[it * 2u + 2u]
                        FollowingSubtree(
                            intermediateNode,
                            TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = wrappedNode,
                                secondChild = null,
                            ).also {
                                it.tree = this
                                wrappedNode.holder = it
                            }
                        )
                    },
                )
            }
            else -> {
                val startNode1 = elements[0u]
                val startNode2 = elements[1u]
                createTree(
                    firstSubtree = ThreeNodeHolder<Element>(
                        isItBottom = true,
                        firstChild = null,
                        firstElement = startNode1,
                        secondChild = null,
                        secondElement = startNode2,
                        thirdChild = null,
                    ).also {
                        it.tree = this
                        startNode1.holder = it
                        startNode2.holder = it
                    },
                    rest = KoneArray(elements.size / 2u - 1u) {
                        val intermediateNode = elements[it * 2u + 2u]
                        val wrappedNode = elements[it * 2u + 3u]
                        FollowingSubtree(
                            intermediateNode,
                            TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = wrappedNode,
                                secondChild = null,
                            ).also {
                                it.tree = this
                                wrappedNode.holder = it
                            }
                        )
                    },
                )
            }
        }
    
    private tailrec fun NodeHolder<Element>?.updateSizeOfChildUpToTheRoot(child: NodeHolder<Element>) {
        when (this) {
            null -> this@KoneTwoThreeTreeList.size = child.size
            is TwoNodeHolder<Element> -> {
                when (child) {
                    this.firstChild -> this.firstChildSize = child.size
                    this.secondChild -> this.secondChildSize = child.size
                    else -> error("Trying to change parent's non-existent child")
                }
                this.parent.updateSizeOfChildUpToTheRoot(this)
            }
            is ThreeNodeHolder<Element> -> {
                when (child) {
                    this.firstChild -> this.firstChildSize = child.size
                    this.secondChild -> this.secondChildSize = child.size
                    this.thirdChild -> this.thirdChildSize = child.size
                    else -> error("Trying to change parent's non-existent child")
                }
                this.parent.updateSizeOfChildUpToTheRoot(this)
            }
        }
    }
    
    private fun NodeHolder<Element>?.replaceChild(oldChild: NodeHolder<Element>, newChild: NodeHolder<Element>) {
        newChild.parent = this
        when (this) {
            null -> {
                rootHolder = newChild
                this@KoneTwoThreeTreeList.size = newChild.size
            }
            is TwoNodeHolder -> {
                when (oldChild) {
                    this.firstChild -> {
                        this.firstChild = newChild
                        this.firstChildSize = newChild.size
                    }
                    this.secondChild -> {
                        this.secondChild = newChild
                        this.secondChildSize = newChild.size
                    }
                    else -> error("Trying to change parent's non-existent child")
                }
                this.parent.updateSizeOfChildUpToTheRoot(this)
            }
            is ThreeNodeHolder -> {
                when (oldChild) {
                    this.firstChild -> {
                        this.firstChild = newChild
                        this.firstChildSize = newChild.size
                    }
                    this.secondChild -> {
                        this.secondChild = newChild
                        this.secondChildSize = newChild.size
                    }
                    this.thirdChild -> {
                        this.thirdChild = newChild
                        this.thirdChildSize = newChild.size
                    }
                    else -> error("Trying to change parent's non-existent child")
                }
                this.parent.updateSizeOfChildUpToTheRoot(this)
            }
        }
    }
    
    private tailrec fun NodeHolder<Element>?.replaceChild(oldChild: NodeHolder<Element>, firstNewChild: NodeHolder<Element>, node: Node<Element>, secondNewChild: NodeHolder<Element>) {
        when (this) {
            null -> {
                check(rootHolder === oldChild) { "Received not a child of the parent" }
                val newHolder = twoNodeHolder(
                    isItBottom = false,
                    firstChild = firstNewChild,
                    element = node,
                    secondChild = secondNewChild,
                )
                rootHolder = newHolder
                this@KoneTwoThreeTreeList.size = newHolder.size
            }
            is TwoNodeHolder -> {
                val parent = this.parent
                val isThisBottom = this.isItBottom
                val firstChild = this.firstChild
                val element = this.element
                val secondChild = this.secondChild
                this.dispose()
                
                val newNodeHolder = when (oldChild) {
                    firstChild ->
                        threeNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstNewChild,
                            firstElement = node,
                            secondChild = secondNewChild,
                            secondElement = element,
                            thirdChild = secondChild,
                        )
                    secondChild ->
                        threeNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstChild,
                            firstElement = element,
                            secondChild = firstNewChild,
                            secondElement = node,
                            thirdChild = secondNewChild,
                        )
                    else -> error("Received not a child of the parent")
                }
                
                parent.replaceChild(this, newNodeHolder)
            }
            is ThreeNodeHolder -> {
                val parent = this.parent
                val isThisBottom = this.isItBottom
                val firstChild = this.firstChild
                val firstElement = this.firstElement
                val secondChild = this.secondChild
                val secondElement = this.secondElement
                val thirdChild = this.thirdChild
                this.dispose()
                
                val firstNewParent: TwoNodeHolder<Element>
                val parentNode: Node<Element>
                val secondNewParent: TwoNodeHolder<Element>
                
                when (oldChild) {
                    firstChild -> {
                        firstNewParent = twoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstNewChild,
                            element = node,
                            secondChild = secondNewChild,
                        )
                        parentNode = firstElement
                        secondNewParent = twoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = secondChild,
                            element = secondElement,
                            secondChild = thirdChild,
                        )
                    }
                    secondChild -> {
                        firstNewParent = twoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstChild,
                            element = firstElement,
                            secondChild = firstNewChild,
                        )
                        parentNode = node
                        secondNewParent = twoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = secondNewChild,
                            element = secondElement,
                            secondChild = thirdChild,
                        )
                    }
                    thirdChild -> {
                        firstNewParent = twoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstChild,
                            element = firstElement,
                            secondChild = secondChild,
                        )
                        parentNode = secondElement
                        secondNewParent = twoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstNewChild,
                            element = node,
                            secondChild = secondNewChild,
                        )
                    }
                    else -> error("Received an incorrect position")
                }
                
                parent.replaceChild(
                    oldChild = this,
                    firstNewChild = firstNewParent,
                    node = parentNode,
                    secondNewChild = secondNewParent,
                )
            }
        }
    }
    
    private tailrec fun NodeHolder<Element>?.replaceChildWithOneNodeHolder(oldChild: NodeHolder<Element>, referredChild: NodeHolder<Element>?) {
        when (this) {
            null -> {
                check(rootHolder === oldChild) { "For some reason non-root holder tries to replace root one" }
                rootHolder = referredChild
                referredChild?.parent = null
                this@KoneTwoThreeTreeList.size = referredChild.size
            }
            is TwoNodeHolder ->
                when (oldChild) {
                    this.firstChild ->
                        when (val secondChild = this.secondChild!!) {
                            is TwoNodeHolder -> {
                                val parent = this.parent
                                val newThis = threeNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = referredChild,
                                    firstElement = this.element,
                                    secondChild = secondChild.firstChild,
                                    secondElement = secondChild.element,
                                    thirdChild = secondChild.secondChild,
                                )
                                this.dispose()
                                secondChild.dispose()
                                newThis.parent = parent
                                parent.replaceChildWithOneNodeHolder(this, newThis)
                            }
                            is ThreeNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = twoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = referredChild,
                                    element = this.element,
                                    secondChild = secondChild.firstChild,
                                )
                                val newSecondChild = twoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = secondChild.secondChild,
                                    element = secondChild.secondElement,
                                    secondChild = secondChild.thirdChild,
                                )
                                val newThis = twoNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    element = secondChild.firstElement,
                                    secondChild = newSecondChild
                                )
                                newThis.parent = parent
                                secondChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                        }
                    this.secondChild ->
                        when (val firstChild = this.firstChild!!) {
                            is TwoNodeHolder -> {
                                val parent = this.parent
                                val newThis = threeNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.firstChild,
                                    firstElement = firstChild.element,
                                    secondChild = firstChild.secondChild,
                                    secondElement = this.element,
                                    thirdChild = referredChild,
                                )
                                this.dispose()
                                firstChild.dispose()
                                newThis.parent = parent
                                parent.replaceChildWithOneNodeHolder(this, newThis)
                            }
                            is ThreeNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = twoNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.firstChild,
                                    element = firstChild.firstElement,
                                    secondChild = firstChild.secondChild,
                                )
                                val newSecondChild = twoNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.thirdChild,
                                    element = this.element,
                                    secondChild = referredChild,
                                )
                                val newThis = twoNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    element = firstChild.secondElement,
                                    secondChild = newSecondChild
                                )
                                newThis.parent = parent
                                firstChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                        }
                    else -> error("Received not a child of the parent")
                }
            is ThreeNodeHolder ->
                when (oldChild) {
                    this.firstChild ->
                        when (val secondChild = this.secondChild!!) {
                            is TwoNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = threeNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = referredChild,
                                    firstElement = this.firstElement,
                                    secondChild = secondChild.firstChild,
                                    secondElement = secondChild.element,
                                    thirdChild = secondChild.secondChild,
                                )
                                val newThis = twoNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    element = this.secondElement,
                                    secondChild = this.thirdChild,
                                )
                                secondChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                            is ThreeNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = twoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = referredChild,
                                    element = this.firstElement,
                                    secondChild = secondChild.firstChild,
                                )
                                val newSecondChild = twoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = secondChild.secondChild,
                                    element = secondChild.secondElement,
                                    secondChild = secondChild.thirdChild,
                                )
                                val newThis = threeNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    firstElement = secondChild.firstElement,
                                    secondChild = newSecondChild,
                                    secondElement = this.secondElement,
                                    thirdChild = this.thirdChild,
                                )
                                newThis.parent = parent
                                secondChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                        }
                    this.secondChild ->
                        when (val firstChild = this.firstChild!!) {
                            is TwoNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = threeNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.firstChild,
                                    firstElement = firstChild.element,
                                    secondChild = firstChild.secondChild,
                                    secondElement = this.firstElement,
                                    thirdChild = referredChild,
                                )
                                val newThis = twoNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    element = this.secondElement,
                                    secondChild = this.thirdChild,
                                )
                                newThis.parent = parent
                                firstChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                            is ThreeNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = twoNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.firstChild,
                                    element = firstChild.firstElement,
                                    secondChild = firstChild.secondChild,
                                )
                                val newSecondChild = twoNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.thirdChild,
                                    element = this.firstElement,
                                    secondChild = referredChild,
                                )
                                val newThis = threeNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    firstElement = firstChild.secondElement,
                                    secondChild = newSecondChild,
                                    secondElement = this.secondElement,
                                    thirdChild = this.thirdChild,
                                )
                                newThis.parent = parent
                                firstChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                        }
                    this.thirdChild ->
                        when (val secondChild = this.secondChild!!) {
                            is TwoNodeHolder -> {
                                val parent = this.parent
                                val newSecondChild = threeNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = secondChild.firstChild,
                                    firstElement = secondChild.element,
                                    secondChild = secondChild.secondChild,
                                    secondElement = this.secondElement,
                                    thirdChild = referredChild,
                                )
                                val newThis = twoNodeHolder(
                                    isItBottom = false,
                                    firstChild = this.firstChild,
                                    element = this.firstElement,
                                    secondChild = newSecondChild,
                                )
                                newThis.parent = parent
                                secondChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                            is ThreeNodeHolder -> {
                                val parent = this.parent
                                val newSecondChild = twoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = secondChild.firstChild,
                                    element = secondChild.firstElement,
                                    secondChild = secondChild.secondChild,
                                )
                                val newThirdChild = twoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = secondChild.thirdChild,
                                    element = this.secondElement,
                                    secondChild = referredChild,
                                )
                                val newThis = threeNodeHolder(
                                    isItBottom = false,
                                    firstChild = this.firstChild,
                                    firstElement = this.firstElement,
                                    secondChild = newSecondChild,
                                    secondElement = secondChild.secondElement,
                                    thirdChild = newThirdChild,
                                )
                                newThis.parent = parent
                                secondChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                        }
                    else -> error("Received not a child of the parent")
                }
        }
    }
    
    private fun removeBottomNode(node: Node<Element>) {
        when (val holder = node.holder) {
            is TwoNodeHolder -> {
                val parent = holder.parent
                holder.dispose()
                parent.replaceChildWithOneNodeHolder(holder, null)
            }
            is ThreeNodeHolder -> {
                val newHolder = twoNodeHolder(
                    isItBottom = true,
                    firstChild = null,
                    element = when (node) {
                        holder.firstElement -> holder.secondElement
                        holder.secondElement -> holder.firstElement
                        else -> error("Received not a holder of the node")
                    },
                    secondChild = null,
                )
                holder.parent.replaceChild(holder, newHolder)
                holder.dispose()
            }
        }
        val previousNode = node.previousNode
        val nextNode = node.nextNode
        nextNode?.previousNode = previousNode
        previousNode?.nextNode = nextNode
        if (previousNode == null) firstNode = nextNode
        if (nextNode == null) lastNode = previousNode
    }
    
    private fun removeNode(node: Node<Element>) {
        when {
            size == 1u -> {
                rootHolder!!.dispose()
                rootHolder = null
                firstNode = null
                lastNode = null
                size = 0u
            }
            node.holder.isItBottom -> {
                removeBottomNode(node)
            }
            else -> {
                val nextNode = node.nextNode!!
                val holder = node.holder
                val nextHolder = nextNode.holder
                when (holder) {
                    is TwoNodeHolder ->
                        when (node) {
                            holder.element -> holder.element = nextNode
                            else -> error("Received not a holder of the node")
                        }
                    is ThreeNodeHolder ->
                        when (node) {
                            holder.firstElement -> holder.firstElement = nextNode
                            holder.secondElement -> holder.secondElement = nextNode
                            else -> error("Received not a holder of the node")
                        }
                }
                when (nextHolder) {
                    is TwoNodeHolder ->
                        when (nextNode) {
                            nextHolder.element -> nextHolder.element = node
                            else -> error("Received not a holder of the node")
                        }
                    is ThreeNodeHolder ->
                        when (nextNode) {
                            nextHolder.firstElement -> nextHolder.firstElement = node
                            nextHolder.secondElement -> nextHolder.secondElement = node
                            else -> error("Received not a holder of the node")
                        }
                }
                nextNode.nextNode?.previousNode = node
                node.previousNode?.nextNode = nextNode
                nextNode.previousNode = node.previousNode
                node.nextNode = nextNode.nextNode
                nextNode.nextNode = node
                node.previousNode = nextNode
                nextNode.holder = holder
                node.holder = nextHolder
                removeBottomNode(node)
            }
        }
        node.detach()
    }
    
    private tailrec fun getInternalNodeBy(index: UInt, holder: NodeHolder<Element>): Node<Element> {
        when (holder) {
            is TwoNodeHolder<Element> ->
                when {
                    index < holder.firstChildSize ->
                        return getInternalNodeBy(index, holder.firstChild!!)
                    index == holder.firstChildSize ->
                        return holder.element
                    index < holder.firstChildSize + 1u + holder.secondChildSize ->
                        return getInternalNodeBy(index - (holder.firstChildSize + 1u), holder.secondChild!!)
                    else -> error("Cannot find node by index. Got index out of bound of holder.")
                }
            is ThreeNodeHolder<Element> ->
                when {
                    index < holder.firstChildSize ->
                        return getInternalNodeBy(index, holder.firstChild!!)
                    index == holder.firstChildSize ->
                        return holder.firstElement
                    index < holder.firstChildSize + 1u + holder.secondChildSize ->
                        return getInternalNodeBy(index - (holder.firstChildSize + 1u), holder.secondChild!!)
                    index == holder.firstChildSize + 1u + holder.secondChildSize ->
                        return holder.secondElement
                    index < holder.firstChildSize + 1u + holder.secondChildSize + 1u + holder.thirdChildSize ->
                        return getInternalNodeBy(index - (holder.firstChildSize + 1u + holder.secondChildSize + 1u), holder.thirdChild!!)
                    else -> error("Cannot find node by index. Got index out of bound of holder.")
                }
        }
    }
    
    private fun getInternalNode(index: UInt): Node<Element> = getInternalNodeBy(index, rootHolder!!)
    
    override fun getNode(index: UInt): KoneMutableListNode<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index >= size -> indexOutOfBoundsException(index, size)
            else -> getInternalNode(index)
        }
    
    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        getNode(index).element = element
    }
    
    private fun addNodeToTheEnd(element: Element): Node<Element> {
        if (isDisposed) disposedInstanceException()
        if (size == 0u) {
            val newNode = Node(element)
            val newHolder = twoNodeHolder(
                isItBottom = true,
                firstChild = null,
                element = newNode,
                secondChild = null
            )
            rootHolder = newHolder
            firstNode = newNode
            lastNode = newNode
            size++
            return newNode
        }
        
        val newNode = Node(element)
        val oldLastNode = lastNode!!
        newNode.previousNode = oldLastNode
        oldLastNode.nextNode = newNode
        this.lastNode = newNode
        val oldLastNodeHolder = oldLastNode.holder
        check(oldLastNode.holder.isItBottom) { "For some reason, maximum is not at the bottom" }
        when (oldLastNodeHolder) {
            is TwoNodeHolder -> {
                val parent = oldLastNodeHolder.parent
                val newLastNodeHolder = threeNodeHolder(
                    isItBottom = true,
                    firstChild = null,
                    firstElement = oldLastNode,
                    secondChild = null,
                    secondElement = newNode,
                    thirdChild = null,
                )
                parent.replaceChild(
                    oldChild = oldLastNodeHolder,
                    newChild = newLastNodeHolder
                )
            }
            is ThreeNodeHolder ->
                oldLastNodeHolder.parent.replaceChild(
                    oldChild = oldLastNodeHolder,
                    firstNewChild = twoNodeHolder(
                        isItBottom = true,
                        firstChild = null,
                        element = oldLastNodeHolder.firstElement,
                        secondChild = null,
                    ),
                    node = oldLastNodeHolder.secondElement,
                    secondNewChild = twoNodeHolder(
                        isItBottom = true,
                        firstChild = null,
                        element = newNode,
                        secondChild = null,
                    ),
                )
        }
        oldLastNodeHolder.dispose()
        return newNode
    }
    private fun addNodeBefore(node: Node<Element>, element: Element): Node<Element> {
        val previousNode = node.previousNode
        
        return if (previousNode == null) {
            val newNode = Node(element)
            newNode.nextNode = node
            node.previousNode = newNode
            this.firstNode = newNode
            val nodeHolder = node.holder
            check(nodeHolder.isItBottom) { "For some reason, minimum is not at the bottom" }
            when (nodeHolder) {
                is TwoNodeHolder -> {
                    val parent = nodeHolder.parent
                    val newFirstNodeHolder = threeNodeHolder(
                        isItBottom = true,
                        firstChild = null,
                        firstElement = newNode,
                        secondChild = null,
                        secondElement = node,
                        thirdChild = null,
                    )
                    parent.replaceChild(
                        oldChild = nodeHolder,
                        newChild = newFirstNodeHolder
                    )
                }
                is ThreeNodeHolder ->
                    nodeHolder.parent.replaceChild(
                        oldChild = nodeHolder,
                        firstNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = newNode,
                            secondChild = null,
                        ),
                        node = nodeHolder.firstElement,
                        secondNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = nodeHolder.secondElement,
                            secondChild = null,
                        ),
                    )
            }
            nodeHolder.dispose()
            newNode
        } else {
            val newNode = Node(element)
            newNode.previousNode = previousNode
            newNode.nextNode = node
            previousNode.nextNode = newNode
            node.previousNode = newNode
            val previousNodeHolder = previousNode.holder
            val nodeHolder = node.holder
            when {
                previousNodeHolder.isItBottom && previousNodeHolder is TwoNodeHolder -> {
                    val parent = previousNodeHolder.parent
                    val newLowerBoundHolder = threeNodeHolder(
                        isItBottom = true,
                        firstChild = null,
                        firstElement = previousNode,
                        secondChild = null,
                        secondElement = newNode,
                        thirdChild = null,
                    )
                    parent.replaceChild(
                        oldChild = previousNodeHolder,
                        newChild = newLowerBoundHolder,
                    )
                    previousNodeHolder.dispose()
                }
                nodeHolder.isItBottom && nodeHolder is TwoNodeHolder -> {
                    val parent = nodeHolder.parent
                    val newUpperBoundHolder = threeNodeHolder(
                        isItBottom = true,
                        firstChild = null,
                        firstElement = newNode,
                        secondChild = null,
                        secondElement = node,
                        thirdChild = null,
                    )
                    parent.replaceChild(
                        oldChild = nodeHolder,
                        newChild = newUpperBoundHolder,
                    )
                    nodeHolder.dispose()
                }
                previousNodeHolder.isItBottom && nodeHolder.isItBottom -> {
                    check(previousNodeHolder === nodeHolder) { "For some reason, lower and upper bounds' holders are both bottom but are not the same" }
                    previousNodeHolder.parent.replaceChild(
                        oldChild = previousNodeHolder,
                        firstNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = previousNode,
                            secondChild = null,
                        ),
                        node = newNode,
                        secondNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = node,
                            secondChild = null,
                        ),
                    )
                    previousNodeHolder.dispose()
                }
                previousNodeHolder.isItBottom -> {
                    previousNodeHolder as ThreeNodeHolder
                    previousNodeHolder.parent.replaceChild(
                        oldChild = previousNodeHolder,
                        firstNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = previousNodeHolder.firstElement,
                            secondChild = null,
                        ),
                        node = previousNodeHolder.secondElement,
                        secondNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = newNode,
                            secondChild = null,
                        ),
                    )
                    previousNodeHolder.dispose()
                }
                nodeHolder.isItBottom -> {
                    nodeHolder as ThreeNodeHolder
                    nodeHolder.parent.replaceChild(
                        oldChild = nodeHolder,
                        firstNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = newNode,
                            secondChild = null,
                        ),
                        node = nodeHolder.firstElement,
                        secondNewChild = twoNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            element = nodeHolder.secondElement,
                            secondChild = null,
                        ),
                    )
                    nodeHolder.dispose()
                }
                else -> error("For some reason, lower and upper bounds' holders are both not at the bottom")
            }
            newNode
        }
    }
    
    override fun addNode(element: Element): KoneMutableListNode<Element> =
        if (isDisposed) disposedInstanceException()
        else addNodeToTheEnd(element)
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index > size) indexOutOfBoundsException(index, size)
        if (index == size) return addNodeToTheEnd(element)
        return addNodeBefore(getInternalNode(index), element)
    }
    // TODO: Maybe there is a better tree reconstruction?..
//    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
//        super.addSeveral(number, builder)
//    }
//    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
//        super.addSeveralAt(index, number, builder)
//    }
    
    override fun removeAt(index: UInt) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        removeNode(getInternalNode(index))
    }
    override fun removeAllThatIndexed(predicate: (UInt, Element) -> Boolean) {
        if (isDisposed) disposedInstanceException()
        
        // TODO: Maybe there is a better tree reconstruction?..
        var currentIndex = 0u
        var currentNode = firstNode
        while (currentNode != null) {
            currentNode = currentNode.nextNode.also { if (predicate(currentIndex, currentNode.element)) removeNode(currentNode) }
            currentIndex++
        }
    }
    override fun removeAll() {
        if (isDisposed) disposedInstanceException()
        var currentNode = firstNode
        while (currentNode != null) {
            currentNode.holder.dispose()
            currentNode = currentNode.nextNode?.also { it.detach() }
        }
        rootHolder = null
        firstNode = null
        lastNode = null
        size = 0u
    }
    
    override fun iterator(): KoneMutableNoddedListIterator<Element> =
        if (isDisposed) disposedInstanceException()
        else Iterator(this, firstNode, 0u)
    override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element> =
        if (isDisposed) disposedInstanceException()
        else Iterator(this, getInternalNode(index), index)
    
    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        var currentNode = firstNode
        if (currentNode != null) {
            append(currentNode.element)
            currentNode = currentNode.nextNode
        }
        while (currentNode != null) {
            append(", ")
            append(currentNode.element)
            currentNode = currentNode.nextNode
        }
        append(']')
    }
    
    internal sealed interface NodeHolder<Element> : Disposable {
        var parent: NodeHolder<Element>?
        val isItBottom: Boolean
        var tree: KoneTwoThreeTreeList<Element>
    }
    internal class TwoNodeHolder<Element>(
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<Element>?,
        element: Node<Element>,
        var secondChild: NodeHolder<Element>?,
    ) : NodeHolder<Element> {
        override var isDisposed: Boolean = false
            private set
        
        private var _tree: KoneTwoThreeTreeList<Element>? = null
        override var tree: KoneTwoThreeTreeList<Element>
            get() = _tree!!
            set(value) { _tree = value }
        override var parent: NodeHolder<Element>? = null
        var firstChildSize: UInt = firstChild.size
        var secondChildSize: UInt = secondChild.size
        private var _element: Node<Element>? = element
        var element: Node<Element>
            get() = _element!!
            set(value) { _element = value }
        
        override fun dispose() {
            if (isDisposed) return
            _tree = null
            parent = null
            firstChild = null
            _element = null
            secondChild = null
            isDisposed = true
        }
    }
    
    private fun twoNodeHolder(
        isItBottom: Boolean,
        firstChild: NodeHolder<Element>?,
        element: Node<Element>,
        secondChild: NodeHolder<Element>?,
    ): TwoNodeHolder<Element> {
        check(
            if (isItBottom) firstChild == null && secondChild == null
            else firstChild != null && secondChild != null
        ) { "Flag isItBottom contradicts the truth" }
        val newHolder = TwoNodeHolder(
            isItBottom = isItBottom,
            firstChild = firstChild,
            element = element,
            secondChild = secondChild,
        )
        newHolder.tree = this
        firstChild?.parent = newHolder
        element.holder = newHolder
        secondChild?.parent = newHolder
        return newHolder
    }
    
    internal class ThreeNodeHolder<Element>(
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<Element>?,
        firstElement: Node<Element>,
        var secondChild: NodeHolder<Element>?,
        secondElement: Node<Element>,
        var thirdChild: NodeHolder<Element>?,
    ) : NodeHolder<Element> {
        override var isDisposed: Boolean = false
            private set
        
        private var _tree: KoneTwoThreeTreeList<Element>? = null
        override var tree: KoneTwoThreeTreeList<Element>
            get() = _tree!!
            set(value) { _tree = value }
        override var parent: NodeHolder<Element>? = null
        var firstChildSize: UInt = firstChild.size
        var secondChildSize: UInt = secondChild.size
        var thirdChildSize: UInt = thirdChild.size
        private var _firstElement: Node<Element>? = firstElement
        var firstElement: Node<Element>
            get() = _firstElement!!
            set(value) { _firstElement = value }
        private var _secondElement: Node<Element>? = secondElement
        var secondElement: Node<Element>
            get() = _secondElement!!
            set(value) { _secondElement = value }
        
        override fun dispose() {
            parent = null
            firstChild = null
            _firstElement = null
            secondChild = null
            _secondElement = null
            thirdChild = null
            isDisposed = true
        }
    }
    
    private fun threeNodeHolder(
        isItBottom: Boolean,
        firstChild: NodeHolder<Element>?,
        firstElement: Node<Element>,
        secondChild: NodeHolder<Element>?,
        secondElement: Node<Element>,
        thirdChild: NodeHolder<Element>?,
    ): ThreeNodeHolder<Element> {
        check(
            if (isItBottom) firstChild == null && secondChild == null && thirdChild == null
            else firstChild != null && secondChild != null && thirdChild != null
        ) { "Flag isItBottom contradicts the truth" }
        val newHolder = ThreeNodeHolder(
            isItBottom = isItBottom,
            firstChild = firstChild,
            firstElement = firstElement,
            secondChild = secondChild,
            secondElement = secondElement,
            thirdChild = thirdChild,
        )
        newHolder.tree = this
        firstChild?.parent = newHolder
        firstElement.holder = newHolder
        secondChild?.parent = newHolder
        secondElement.holder = newHolder
        thirdChild?.parent = newHolder
        return newHolder
    }
    
    internal class Node<Element>(
        override var element: Element,
    ) : KoneMutableListNode<Element> {
        override var isDetached: Boolean = false
            private set
        
        private var _holder: NodeHolder<Element>? = null
        internal var holder: NodeHolder<Element>
            get() = _holder!!
            set(value) { _holder = value }
        override var nextNode: Node<Element>? = null
            internal set
        override var previousNode: Node<Element>? = null
            internal set
        
        fun detach() {
            if (isDetached) return
            _holder = null
            nextNode = null
            previousNode = null
            isDetached = true
        }
        
        override fun remove() {
            if (isDetached) detachedNodeException()
            holder.tree.removeNode(this)
        }
        
        override val index: UInt
            get() {
                if (isDetached) detachedNodeException()
                
                tailrec fun NodeHolder<*>.getNumberOfNodesBeforeAndAddItTo(number: UInt): UInt =
                    when (val parent = this.parent) {
                        null -> number
                        is TwoNodeHolder<*> -> parent.getNumberOfNodesBeforeAndAddItTo(
                            number + when (this) {
                                parent.firstChild -> 0u
                                parent.secondChild -> parent.firstChildSize + 1u
                                else -> error("For some reason, parent holder does not contain the holder as a child")
                            }
                        )
                        is ThreeNodeHolder<*> -> parent.getNumberOfNodesBeforeAndAddItTo(
                            number + when (this) {
                                parent.firstChild -> 0u
                                parent.secondChild -> parent.firstChildSize + 1u
                                parent.thirdChild -> parent.firstChildSize + 1u + parent.secondChildSize + 1u
                                else -> error("For some reason, parent holder does not contain the holder as a child")
                            }
                        )
                    }
                
                val holder = holder
                
                return holder.getNumberOfNodesBeforeAndAddItTo(
                    when (holder) {
                        is TwoNodeHolder<*> ->
                            when (this) {
                                holder.element -> holder.firstChildSize
                                else -> error("For some reason, holder does not contain the node as an element")
                            }
                        is ThreeNodeHolder<*> ->
                            when (this) {
                                holder.firstElement -> holder.firstChildSize
                                holder.secondElement -> holder.firstChildSize + 1u + holder.secondChildSize
                                else -> error("For some reason, holder does not contain the node as an element")
                            }
                    }
                )
            }
        
        override fun iteratorFromBeforeHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException()
            else Iterator(holder.tree, this)
        
        override fun iteratorFromAfterHere(): KoneMutableNoddedListIterator<Element> =
            if (isDetached) detachedNodeException()
            else Iterator(holder.tree, nextNode)
    }
    
    internal class Iterator<Element>(
        val list: KoneTwoThreeTreeList<Element>,
        var nextNode: Node<Element>?,
        var _nextIndex: UInt? = null,
    ) : KoneMutableNoddedListIterator<Element> {
        val nextIndex: UInt get() = (_nextIndex ?: nextNode?.index ?: list.size).also { _nextIndex = it }
        
        override fun hasNext(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else nextNode != null
        override fun getNext(): Element =
            if (!hasNext()) noNextElementInIteratorException()
            else nextNode!!.element
        override fun getNextNode(): KoneMutableListNode<Element> =
            if (!hasNext()) noNextElementInIteratorException()
            else nextNode!!
        override fun nextIndex(): UInt =
            if (!hasNext()) noNextElementInIteratorException()
            else nextIndex
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextNode = nextNode!!.nextNode
            _nextIndex = _nextIndex?.let { it + 1u }
        }
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            nextNode!!.element = element
        }
        override fun addNext(element: Element) {
            nextNode = if (nextNode == null) {
                list.addNodeToTheEnd(element)
            } else {
                list.addNodeBefore(nextNode!!, element)
            }
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextNode = nextNode!!.nextNode.also { list.removeNode(nextNode!!) }
        }
        
        override fun hasPrevious(): Boolean =
            if (list.isDisposed) disposedInstanceException()
            else nextNode != list.firstNode
        override fun getPrevious(): Element =
            when {
                !hasPrevious() -> noPreviousElementInIteratorException()
                nextNode == null -> list.lastNode!!.element
                else -> nextNode!!.previousNode!!.element
            }
        override fun getPreviousNode(): KoneMutableListNode<Element> =
            when {
                !hasPrevious() -> noPreviousElementInIteratorException()
                nextNode == null -> list.lastNode!!
                else -> nextNode!!.previousNode!!
            }
        override fun previousIndex(): UInt =
            if (!hasPrevious()) noPreviousElementInIteratorException()
            else nextIndex - 1u
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            nextNode = if (nextNode == null) list.lastNode else nextNode!!.previousNode
            _nextIndex = _nextIndex?.let { it - 1u }
        }
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            val previousNode = if (nextNode == null) list.lastNode else nextNode!!.previousNode
            previousNode!!.element = element
        }
        override fun addPrevious(element: Element) {
            if (nextNode == null) {
                list.addNodeToTheEnd(element)
            } else {
                list.addNodeBefore(nextNode!!, element)
            }
            _nextIndex = _nextIndex?.let { it + 1u }
        }
        override fun removePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            val previousNode = if (nextNode == null) list.lastNode else nextNode!!.previousNode
            list.removeNode(previousNode!!)
            _nextIndex = _nextIndex?.let { it - 1u }
        }
    }
}