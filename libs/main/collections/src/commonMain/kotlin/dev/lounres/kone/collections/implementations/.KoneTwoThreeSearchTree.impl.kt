/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.KoneLinkedReifiedSet
import dev.lounres.kone.collections.KoneLinkedSet
import dev.lounres.kone.collections.KoneReversibleIterator
import dev.lounres.kone.collections.LinkedSearchTree
import dev.lounres.kone.collections.LinkedSearchTreeNode
import dev.lounres.kone.collections.SearchSegmentResult
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.comparison.lt
import dev.lounres.kone.context.invoke


public class KoneTwoThreeSearchTree<Element, out ElementContext: Order<Element>> /*internal*/ constructor(
    public val elementContext: ElementContext,
) : LinkedSearchTree<Element> {
    override var size: UInt = 0u
        private set
    
    private var rootHolder: NodeHolder<Element>? = null
    private var minimum: Node<Element>? = null
    
    private fun NodeHolder<Element>?.replaceChild(oldChild: NodeHolder<Element>, newChild: NodeHolder<Element>) {
        newChild.parent = this
        when (this) {
            null -> rootHolder = newChild
            is TwoNodeHolder ->
                when (oldChild) {
                    this.firstChild -> this.firstChild = newChild
                    this.secondChild -> this.secondChild = newChild
                    else -> error("Trying to change parent's non-existent child")
                }
            is ThreeNodeHolder ->
                when (oldChild) {
                    this.firstChild -> this.firstChild = newChild
                    this.secondChild -> this.secondChild = newChild
                    this.thirdChild -> this.thirdChild = newChild
                    else -> error("Trying to change parent's non-existent child")
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
    
    private inline fun <R> findSegmentForAndDo(
        element: Element,
        onEmpty: () -> R,
        onCoincidence: (value: Node<Element>) -> R,
        onBetween: (lowerBound: Node<Element>, upperBound: Node<Element>) -> R,
        onLessThanMinimum: (minimum: Node<Element>) -> R,
        onGreaterThanMaximum: (maximum: Node<Element>) -> R,
    ): R {
        var subtree = rootHolder
        var lowerBound: Node<Element>? = null
        var upperBound: Node<Element>? = null
        while (true) {
            if (subtree == null)
                return when {
                    lowerBound != null && upperBound != null -> onBetween(lowerBound, upperBound)
                    lowerBound != null -> onGreaterThanMaximum(lowerBound)
                    upperBound != null -> onLessThanMinimum(upperBound)
                    else -> onEmpty()
                }
            when (subtree) {
                is TwoNodeHolder ->
                    when {
                        elementContext { element lt subtree.element.element } -> {
                            upperBound = subtree.element
                            subtree = subtree.firstChild
                        }
                        elementContext { element eq subtree.element.element } -> return onCoincidence(subtree.element)
                        else -> {
                            lowerBound = subtree.element
                            subtree = subtree.secondChild
                        }
                    }
                is ThreeNodeHolder ->
                    when {
                        elementContext { element lt subtree.firstElement.element } -> {
                            upperBound = subtree.firstElement
                            subtree = subtree.firstChild
                        }
                        elementContext { element eq subtree.firstElement.element } -> return onCoincidence(subtree.firstElement)
                        elementContext { element lt subtree.secondElement.element } -> {
                            lowerBound = subtree.firstElement
                            upperBound = subtree.secondElement
                            subtree = subtree.secondChild
                        }
                        elementContext { element eq subtree.secondElement.element } -> return onCoincidence(subtree.secondElement)
                        else -> {
                            lowerBound = subtree.secondElement
                            subtree = subtree.thirdChild
                        }
                    }
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
        if (previousNode == null) minimum = nextNode
    }
    
    private fun removeNode(node: Node<Element>) {
        when {
            size == 1u -> {
                rootHolder!!.dispose()
                rootHolder = null
                minimum = null
                size = 0u
            }
            node.holder.isItBottom -> {
                removeBottomNode(node)
                size--
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
                nextNode.holder = node.holder.also { node.holder = nextNode.holder }
                removeBottomNode(node)
                size--
            }
        }
        node.detach()
    }
    
    override val nodesView: KoneLinkedReifiedSet<LinkedSearchTreeNode<Element>> = Nodes()
    override val elementsView: KoneLinkedSet<Element> = Elements()
    
    override fun add(element: Element): LinkedSearchTreeNode<Element> =
        findSegmentForAndDo(
            element = element,
            onEmpty = {
                val newNode = Node(element)
                val newHolder = twoNodeHolder(
                    isItBottom = true,
                    firstChild = null,
                    element = newNode,
                    secondChild = null
                )
                rootHolder = newHolder
                minimum = newNode
                size++
                newNode
            },
            onCoincidence = { value ->
                value
            },
            onBetween = { lowerBound, upperBound ->
                val newNode = Node(element)
                newNode.previousNode = lowerBound
                newNode.nextNode = upperBound
                lowerBound.nextNode = newNode
                upperBound.previousNode = newNode
                val lowerBoundHolder = lowerBound.holder
                val upperBoundHolder = upperBound.holder
                when {
                    lowerBoundHolder.isItBottom && lowerBoundHolder is TwoNodeHolder -> {
                        val parent = lowerBoundHolder.parent
                        val newLowerBoundHolder = threeNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            firstElement = lowerBound,
                            secondChild = null,
                            secondElement = newNode,
                            thirdChild = null,
                        )
                        parent.replaceChild(
                            oldChild = lowerBoundHolder,
                            newChild = newLowerBoundHolder,
                        )
                        lowerBoundHolder.dispose()
                    }
                    upperBoundHolder.isItBottom && upperBoundHolder is TwoNodeHolder -> {
                        val parent = upperBoundHolder.parent
                        val newUpperBoundHolder = threeNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            firstElement = newNode,
                            secondChild = null,
                            secondElement = upperBound,
                            thirdChild = null,
                        )
                        parent.replaceChild(
                            oldChild = upperBoundHolder,
                            newChild = newUpperBoundHolder,
                        )
                        upperBoundHolder.dispose()
                    }
                    lowerBoundHolder.isItBottom && upperBoundHolder.isItBottom -> {
                        check(lowerBoundHolder === upperBoundHolder) { "For some reason, lower and upper bounds' holders are both bottom but are not the same" }
                        lowerBoundHolder.parent.replaceChild(
                            oldChild = lowerBoundHolder,
                            firstNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = lowerBound,
                                secondChild = null,
                            ),
                            node = newNode,
                            secondNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = upperBound,
                                secondChild = null,
                            ),
                        )
                        lowerBoundHolder.dispose()
                    }
                    lowerBoundHolder.isItBottom -> {
                        lowerBoundHolder as ThreeNodeHolder
                        lowerBoundHolder.parent.replaceChild(
                            oldChild = lowerBoundHolder,
                            firstNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = lowerBoundHolder.firstElement,
                                secondChild = null,
                            ),
                            node = lowerBoundHolder.secondElement,
                            secondNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ),
                        )
                        lowerBoundHolder.dispose()
                    }
                    upperBoundHolder.isItBottom -> {
                        upperBoundHolder as ThreeNodeHolder
                        upperBoundHolder.parent.replaceChild(
                            oldChild = upperBoundHolder,
                            firstNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ),
                            node = upperBoundHolder.firstElement,
                            secondNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = upperBoundHolder.secondElement,
                                secondChild = null,
                            ),
                        )
                        upperBoundHolder.dispose()
                    }
                    else -> error("For some reason, lower and upper bounds' holders are both not at the bottom")
                }
                size++
                newNode
            },
            onLessThanMinimum = { minimum ->
                val newNode = Node(element)
                newNode.nextNode = minimum
                minimum.previousNode = newNode
                this.minimum = newNode
                val minimumHolder = minimum.holder
                check(minimumHolder.isItBottom) { "For some reason, minimum is not at the bottom" }
                when (minimumHolder) {
                    is TwoNodeHolder -> {
                        val parent = minimumHolder.parent
                        val newMinimumHolder = threeNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            firstElement = newNode,
                            secondChild = null,
                            secondElement = minimum,
                            thirdChild = null,
                        )
                        parent.replaceChild(
                            oldChild = minimumHolder,
                            newChild = newMinimumHolder
                        )
                    }
                    is ThreeNodeHolder ->
                        minimumHolder.parent.replaceChild(
                            oldChild = minimumHolder,
                            firstNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ),
                            node = minimumHolder.firstElement,
                            secondNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = minimumHolder.secondElement,
                                secondChild = null,
                            ),
                        )
                }
                size++
                minimumHolder.dispose()
                newNode
            },
            onGreaterThanMaximum = { maximum ->
                val newNode = Node(element)
                newNode.previousNode = maximum
                maximum.nextNode = newNode
                val maximumHolder = maximum.holder
                check(maximum.holder.isItBottom) { "For some reason, maximum is not at the bottom" }
                when (maximumHolder) {
                    is TwoNodeHolder -> {
                        val parent = maximumHolder.parent
                        val newMaximumHolder = threeNodeHolder(
                            isItBottom = true,
                            firstChild = null,
                            firstElement = maximum,
                            secondChild = null,
                            secondElement = newNode,
                            thirdChild = null,
                        )
                        parent.replaceChild(
                            oldChild = maximumHolder,
                            newChild = newMaximumHolder
                        )
                    }
                    is ThreeNodeHolder ->
                        maximumHolder.parent.replaceChild(
                            oldChild = maximumHolder,
                            firstNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = maximumHolder.firstElement,
                                secondChild = null,
                            ),
                            node = maximumHolder.secondElement,
                            secondNewChild = twoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ),
                        )
                }
                size++
                maximumHolder.dispose()
                newNode
            }
        )
    
    override fun find(element: Element): LinkedSearchTreeNode<Element>? =
        findSegmentForAndDo(
            element = element,
            onEmpty = {
                null
            },
            onCoincidence = { value ->
                value
            },
            onBetween = { lowerBound, upperBound ->
                null
            },
            onLessThanMinimum = { minimum ->
                null
            },
            onGreaterThanMaximum = { maximum ->
                null
            }
        )
    
    override fun findSegmentFor(element: Element): SearchSegmentResult<LinkedSearchTreeNode<Element>> =
        findSegmentForAndDo(
            element = element,
            onEmpty = {
                SearchSegmentResult.Empty
            },
            onCoincidence = { value ->
                SearchSegmentResult.Coincidence(value = value)
            },
            onBetween = { lowerBound, upperBound ->
                SearchSegmentResult.Between(lowerBound = lowerBound, upperBound = upperBound)
            },
            onLessThanMinimum = { minimum ->
                SearchSegmentResult.LessThanMinimum(minimum = minimum)
            },
            onGreaterThanMaximum = { maximum ->
                SearchSegmentResult.GreaterThanMaximum(maximum = maximum)
            }
        )
    
    internal sealed interface NodeHolder<E> : Disposable {
        var parent: NodeHolder<E>?
        val isItBottom: Boolean
        val tree: KoneTwoThreeSearchTree<E, *>
    }
    internal class TwoNodeHolder<Element>(
        tree: KoneTwoThreeSearchTree<Element, *>,
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<Element>?,
        element: Node<Element>,
        var secondChild: NodeHolder<Element>?,
    ) : NodeHolder<Element> {
        override var isDisposed: Boolean = false
            private set
        
        private var _tree: KoneTwoThreeSearchTree<Element, *>? = tree
        override val tree: KoneTwoThreeSearchTree<Element, *> get() = _tree!!
        override var parent: NodeHolder<Element>? = null
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
    internal class ThreeNodeHolder<Element>(
        tree: KoneTwoThreeSearchTree<Element, *>,
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<Element>?,
        firstElement: Node<Element>,
        var secondChild: NodeHolder<Element>?,
        secondElement: Node<Element>,
        var thirdChild: NodeHolder<Element>?,
    ) : NodeHolder<Element> {
        override var isDisposed: Boolean = false
            private set
        
        private var _tree: KoneTwoThreeSearchTree<Element, *>? = tree
        override val tree: KoneTwoThreeSearchTree<Element, *> get() = _tree!!
        override var parent: NodeHolder<Element>? = null
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
            tree = this,
            isItBottom = isItBottom,
            firstChild = firstChild,
            element = element,
            secondChild = secondChild,
        )
        firstChild?.parent = newHolder
        element.holder = newHolder
        secondChild?.parent = newHolder
        return newHolder
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
            tree = this,
            isItBottom = isItBottom,
            firstChild = firstChild,
            firstElement = firstElement,
            secondChild = secondChild,
            secondElement = secondElement,
            thirdChild = thirdChild,
        )
        firstChild?.parent = newHolder
        firstElement.holder = newHolder
        secondChild?.parent = newHolder
        secondElement.holder = newHolder
        thirdChild?.parent = newHolder
        return newHolder
    }
    
    internal class Node<E>(
        override val element: E,
    ) : LinkedSearchTreeNode<E> {
        override var isDetached: Boolean = false
            private set
        
        private var _holder: NodeHolder<E>? = null
        internal var holder: NodeHolder<E>
            get() = _holder!!
            set(value) { _holder = value }
        override var nextNode: Node<E>? = null
            internal set
        override var previousNode: Node<E>? = null
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
            _holder!!.tree.removeNode(this)
        }
    }
    
    internal class NodesIterator<Element>(
        private var nextNode: Node<Element>?,
        private val size: UInt,
    ) : KoneReversibleIterator<Node<Element>> {
        private var previousNode: Node<Element>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextNode != null
        override fun getNext(): Node<Element> {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            return nextNode!!
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            nextIndex++
            previousNode = nextNode
            nextNode = nextNode!!.nextNode
        }
        
        override fun hasPrevious(): Boolean = previousNode != null
        override fun getPrevious(): Node<Element> {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            return previousNode!!
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            nextIndex++
            nextNode = previousNode
            previousNode = previousNode!!.previousNode
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class Nodes : KoneLinkedReifiedSet<Node<Element>> {
        override val size: UInt get() = this@KoneTwoThreeSearchTree.size
        
        override fun contains(element: Node<Element>): Boolean = find(element.element) === element
        
        override fun iterator(): KoneReversibleIterator<Node<Element>> = NodesIterator(minimum, size)
        
        // TODO: Add usual `toString` overload
    }
    
    internal class ElementsIterator<E>(
        private var nextNode: Node<E>?,
        private val size: UInt,
    ) : KoneReversibleIterator<E> {
        private var previousNode: Node<E>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextNode != null
        override fun getNext(): E {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            return nextNode!!.element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            nextIndex++
            previousNode = nextNode
            nextNode = nextNode!!.nextNode
        }
        
        override fun hasPrevious(): Boolean = previousNode != null
        override fun getPrevious(): E {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            return previousNode!!.element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            nextIndex++
            nextNode = previousNode
            previousNode = previousNode!!.previousNode
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class Elements : KoneLinkedSet<Element> {
        override val size: UInt get() = this@KoneTwoThreeSearchTree.size
        
        override fun contains(element: Element): Boolean = find(element) != null
        
        override fun iterator(): KoneReversibleIterator<Element> = ElementsIterator(minimum, size)
        
        // TODO: Add usual `toString` overload
    }
}