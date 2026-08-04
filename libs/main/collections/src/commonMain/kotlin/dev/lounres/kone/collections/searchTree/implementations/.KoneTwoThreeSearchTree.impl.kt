/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.searchTree.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.collections.iterable.KoneReversibleIterable
import dev.lounres.kone.collections.iterator.KoneReversibleIterator
import dev.lounres.kone.collections.searchTree.LinkedSearchTree
import dev.lounres.kone.collections.searchTree.LinkedSearchTreeNode
import dev.lounres.kone.collections.searchTree.SearchSegmentResult
import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedSet
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareWith


public class KoneTwoThreeSearchTree<Element, Priority>(
    public val priorityOrder: Order<Priority>,
) : LinkedSearchTree<Element, Priority> {
    override var size: UInt = 0u
        private set

    private var rootHolder: NodeHolder<Element, Priority>? = null
    private var minimum: Node<Element, Priority>? = null

    private fun NodeHolder<Element, Priority>?.replaceChild(oldChild: NodeHolder<Element, Priority>, newChild: NodeHolder<Element, Priority>) {
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

    private tailrec fun NodeHolder<Element, Priority>?.replaceChild(
        oldChild: NodeHolder<Element, Priority>,
        firstNewChild: NodeHolder<Element, Priority>,
        node: Node<Element, Priority>,
        secondNewChild: NodeHolder<Element, Priority>,
    ) {
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

                val firstNewParent: TwoNodeHolder<Element, Priority>
                val parentNode: Node<Element, Priority>
                val secondNewParent: TwoNodeHolder<Element, Priority>

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

    private tailrec fun NodeHolder<Element, Priority>?.replaceChildWithOneNodeHolder(
        oldChild: NodeHolder<Element, Priority>,
        referredChild: NodeHolder<Element, Priority>?,
    ) {
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
        priority: Priority,
        onEmpty: () -> R,
        onCoincidence: (value: Node<Element, Priority>) -> R,
        onBetween: (lowerBound: Node<Element, Priority>, upperBound: Node<Element, Priority>) -> R,
        onLessThanMinimum: (minimum: Node<Element, Priority>) -> R,
        onGreaterThanMaximum: (maximum: Node<Element, Priority>) -> R,
    ): R {
        var subtree = rootHolder
        var lowerBound: Node<Element, Priority>? = null
        var upperBound: Node<Element, Priority>? = null
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
                    when (priorityOrder { priority compareWith subtree.element.priority }) {
                        LeftIsLessThanRight -> {
                            upperBound = subtree.element
                            subtree = subtree.firstChild
                        }
                        Equal -> return onCoincidence(subtree.element)
                        LeftIsGreaterThanRight -> {
                            lowerBound = subtree.element
                            subtree = subtree.secondChild
                        }
                    }
                is ThreeNodeHolder -> {
                    val firstElementComparisonResult = priorityOrder { priority compareWith subtree.firstElement.priority }
                    val secondElementComparisonResult = priorityOrder { priority compareWith subtree.secondElement.priority }
                    when {
                        firstElementComparisonResult == LeftIsLessThanRight -> {
                            upperBound = subtree.firstElement
                            subtree = subtree.firstChild
                        }
                        firstElementComparisonResult == Equal -> return onCoincidence(subtree.firstElement)
                        secondElementComparisonResult == LeftIsLessThanRight -> {
                            lowerBound = subtree.firstElement
                            upperBound = subtree.secondElement
                            subtree = subtree.secondChild
                        }
                        secondElementComparisonResult == Equal -> return onCoincidence(subtree.secondElement)
                        else -> {
                            lowerBound = subtree.secondElement
                            subtree = subtree.thirdChild
                        }
                    }
                }
            }
        }
    }

    private fun removeBottomNode(node: Node<Element, Priority>) {
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

    private fun removeNode(node: Node<Element, Priority>) {
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

    override val nodesView: KoneLinkedReifiedSet<LinkedSearchTreeNode<Element, Priority>> = Nodes(this)
    override val prioritiesView: KoneLinkedSet<Priority> = Priorities(this)
    override val elementsView: KoneReversibleIterable<Element> = Elements(this)

    override fun add(element: Element, priority: Priority): LinkedSearchTreeNode<Element, Priority> =
        findSegmentForAndDo(
            priority = priority,
            onEmpty = {
                val newNode = Node(element, priority)
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
                val newNode = Node(element, priority)
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
                val newNode = Node(element, priority)
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
                val newNode = Node(element, priority)
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

    override fun find(priority: Priority): LinkedSearchTreeNode<Element, Priority>? =
        findSegmentForAndDo(
            priority = priority,
            onEmpty = {
                null
            },
            onCoincidence = { value ->
                value
            },
            onBetween = { _, _ ->
                null
            },
            onLessThanMinimum = { _ ->
                null
            },
            onGreaterThanMaximum = { _ ->
                null
            }
        )

    override fun findSegmentFor(priority: Priority): SearchSegmentResult<LinkedSearchTreeNode<Element, Priority>> =
        findSegmentForAndDo(
            priority = priority,
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

    internal sealed interface NodeHolder<Element, Priority> : Disposable {
        var parent: NodeHolder<Element, Priority>?
        val isItBottom: Boolean
        val tree: KoneTwoThreeSearchTree<Element, Priority>
    }
    internal class TwoNodeHolder<Element, Priority>(
        tree: KoneTwoThreeSearchTree<Element, Priority>,
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<Element, Priority>?,
        element: Node<Element, Priority>,
        var secondChild: NodeHolder<Element, Priority>?,
    ) : NodeHolder<Element, Priority> {
        override var isDisposed: Boolean = false
            private set

        private var _tree: KoneTwoThreeSearchTree<Element, Priority>? = tree
        override val tree: KoneTwoThreeSearchTree<Element, Priority> get() = _tree!!
        override var parent: NodeHolder<Element, Priority>? = null
        private var _element: Node<Element, Priority>? = element
        var element: Node<Element, Priority>
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
    internal class ThreeNodeHolder<Element, Priority>(
        tree: KoneTwoThreeSearchTree<Element, Priority>,
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<Element, Priority>?,
        firstElement: Node<Element, Priority>,
        var secondChild: NodeHolder<Element, Priority>?,
        secondElement: Node<Element, Priority>,
        var thirdChild: NodeHolder<Element, Priority>?,
    ) : NodeHolder<Element, Priority> {
        override var isDisposed: Boolean = false
            private set

        private var _tree: KoneTwoThreeSearchTree<Element, Priority>? = tree
        override val tree: KoneTwoThreeSearchTree<Element, Priority> get() = _tree!!
        override var parent: NodeHolder<Element, Priority>? = null
        private var _firstElement: Node<Element, Priority>? = firstElement
        var firstElement: Node<Element, Priority>
            get() = _firstElement!!
            set(value) { _firstElement = value }
        private var _secondElement: Node<Element, Priority>? = secondElement
        var secondElement: Node<Element, Priority>
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
        firstChild: NodeHolder<Element, Priority>?,
        element: Node<Element, Priority>,
        secondChild: NodeHolder<Element, Priority>?,
    ): TwoNodeHolder<Element, Priority> {
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
        firstChild: NodeHolder<Element, Priority>?,
        firstElement: Node<Element, Priority>,
        secondChild: NodeHolder<Element, Priority>?,
        secondElement: Node<Element, Priority>,
        thirdChild: NodeHolder<Element, Priority>?,
    ): ThreeNodeHolder<Element, Priority> {
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

    internal class Node<Element, Priority>(
        override var element: Element,
        override val priority: Priority,
    ) : LinkedSearchTreeNode<Element, Priority> {
        override var isDetached: Boolean = false
            private set

        private var _holder: NodeHolder<Element, Priority>? = null
        internal var holder: NodeHolder<Element, Priority>
            get() = _holder!!
            set(value) { _holder = value }
        override var nextNode: Node<Element, Priority>? = null
            internal set
        override var previousNode: Node<Element, Priority>? = null
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

    internal class NodesIterator<Element, Priority>(
        private var nextNode: Node<Element, Priority>?,
        private val size: UInt,
    ) : KoneReversibleIterator<Node<Element, Priority>> {
        private var previousNode: Node<Element, Priority>? = null
        private var nextIndex: UInt = 0u

        override fun hasNext(): Boolean = nextNode != null
        override fun getNext(): Node<Element, Priority> {
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
        override fun getPrevious(): Node<Element, Priority> {
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
    internal class Nodes<Element, Priority>(
        val tree: KoneTwoThreeSearchTree<Element, Priority>,
    ) : KoneLinkedReifiedSet<Node<Element, Priority>> {
        override val size: UInt get() = tree.size

        override fun contains(element: Node<Element, Priority>): Boolean = !element.isDetached && element.holder.tree === tree

        override fun iterator(): KoneReversibleIterator<Node<Element, Priority>> = NodesIterator(tree.minimum, size)

        // TODO: Add usual `toString` overload
    }

    internal class PrioritiesIterator<Priority>(
        private var nextNode: Node<*, Priority>?,
        private val size: UInt,
    ) : KoneReversibleIterator<Priority> {
        private var previousNode: Node<*, Priority>? = null
        private var nextIndex: UInt = 0u

        override fun hasNext(): Boolean = nextNode != null
        override fun getNext(): Priority {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            return nextNode!!.priority
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            nextIndex++
            previousNode = nextNode
            nextNode = nextNode!!.nextNode
        }

        override fun hasPrevious(): Boolean = previousNode != null
        override fun getPrevious(): Priority {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            return previousNode!!.priority
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            nextIndex++
            nextNode = previousNode
            previousNode = previousNode!!.previousNode
        }
    }

    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class Priorities<Priority>(
        private val tree: KoneTwoThreeSearchTree<*, Priority>,
    ) : KoneLinkedSet<Priority> {
        override val size: UInt get() = tree.size

        override fun contains(element: Priority): Boolean = tree.find(element) != null

        override fun iterator(): KoneReversibleIterator<Priority> = PrioritiesIterator(tree.minimum, size)

        // TODO: Add usual `toString` overload
    }
    
    internal class ElementsIterator<Elements>(
        private var nextNode: Node<Elements, *>?,
        private val size: UInt,
    ) : KoneReversibleIterator<Elements> {
        private var previousNode: Node<Elements, *>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextNode != null
        override fun getNext(): Elements {
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
        override fun getPrevious(): Elements {
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
    internal class Elements<Element>(
        private val tree: KoneTwoThreeSearchTree<Element, *>,
    ) : KoneReversibleIterable<Element> {
        override val size: UInt get() = tree.size
        
        override fun iterator(): KoneReversibleIterator<Element> = ElementsIterator(tree.minimum, size)
        
        // TODO: Add usual `toString` overload
    }
}