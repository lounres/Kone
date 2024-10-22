/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.ConnectedSearchTree
import dev.lounres.kone.collections.ConnectedSearchTreeNode
import dev.lounres.kone.collections.KoneIterableListSet
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneListWithContext
import dev.lounres.kone.collections.SearchSegmentResult
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.noElementException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.repeat


public class TwoThreeTree<E, out EC: Order<E>> /*internal*/ constructor(
    public val elementContext: EC,
) : ConnectedSearchTree<E> {
    override var size: UInt = 0u
        private set
    
    private var rootHolder: NodeHolder<E>? = null
    private var minimum: Node<E>? = null
    
    private fun NodeHolder<E>?.replaceChild(oldChild: NodeHolder<E>, newChild: NodeHolder<E>) {
        when (this) {
            null -> rootHolder = newChild
            is TwoThreeTree<E, EC>.TwoNodeHolder ->
                when (oldChild) {
                    this.firstChild -> this.firstChild = newChild
                    this.secondChild -> this.secondChild = newChild
                    else -> throw IllegalStateException("Trying to change parent's non-existent child")
                }
            is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                when (oldChild) {
                    this.firstChild -> this.firstChild = newChild
                    this.secondChild -> this.secondChild = newChild
                    this.thirdChild -> this.thirdChild = newChild
                    else -> throw IllegalStateException("Trying to change parent's non-existent child")
                }
        }
    }
    
    private tailrec fun NodeHolder<E>?.replaceChild(oldChild: NodeHolder<E>, firstNewChild: NodeHolder<E>, node: Node<E>, secondNewChild: NodeHolder<E>) {
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
            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                    else -> throw IllegalStateException("Received not a child of the parent")
                }
                
                newNodeHolder.parent = parent
                
                parent.replaceChild(this, newNodeHolder)
            }
            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
                val parent = this.parent
                val isThisBottom = this.isItBottom
                val firstChild = this.firstChild
                val firstElement = this.firstElement
                val secondChild = this.secondChild
                val secondElement = this.secondElement
                val thirdChild = this.thirdChild
                this.dispose()
                
                val firstNewParent: TwoNodeHolder
                val parentNode: Node<E>
                val secondNewParent: TwoNodeHolder
                
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
                    else -> throw IllegalStateException("Received an incorrect position")
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
    
    private tailrec fun NodeHolder<E>?.replaceChildWithReference(oldChild: NodeHolder<E>, referredChild: NodeHolder<E>?) {
        when (this) {
            null -> {
                check(rootHolder === oldChild) { "For some reason non-root holder tries to replace root one" }
                rootHolder = referredChild
                referredChild?.parent = null
            }
            is TwoThreeTree<E, EC>.TwoNodeHolder ->
                when (oldChild) {
                    this.firstChild ->
                        when (val secondChild = this.secondChild!!) {
                            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                                parent.replaceChildWithReference(this, newThis)
                            }
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
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
                            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                                parent.replaceChildWithReference(this, newThis)
                            }
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
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
                    else -> throw IllegalStateException("Received not a child of the parent")
                }
            is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                when (oldChild) {
                    this.firstChild ->
                        when (val secondChild = this.secondChild!!) {
                            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
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
                            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
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
                            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
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
                    else -> throw IllegalStateException("Received not a child of the parent")
                }
        }
    }
    
    private inline fun <R> findSegmentForAndDo(
        element: E,
        onEmpty: () -> R,
        onCoincidence: (value: Node<E>) -> R,
        onBetween: (lowerBound: Node<E>, upperBound: Node<E>) -> R,
        onLessThanMinimum: (minimum: Node<E>) -> R,
        onGreaterThanMaximum: (maximum: Node<E>) -> R,
    ): R {
        var subtree = rootHolder
        var lowerBound: Node<E>? = null
        var upperBound: Node<E>? = null
        while (true) {
            if (subtree == null)
                return when {
                    lowerBound != null && upperBound != null -> onBetween(lowerBound, upperBound)
                    lowerBound != null -> onGreaterThanMaximum(lowerBound)
                    upperBound != null -> onLessThanMinimum(upperBound)
                    else -> onEmpty()
                }
            when (subtree) {
                is TwoThreeTree<E, EC>.TwoNodeHolder ->
                    when {
                        elementContext { element < subtree.element.element } -> {
                            upperBound = subtree.element
                            subtree = subtree.firstChild
                        }
                        elementContext { element eq subtree.element.element } -> return onCoincidence(subtree.element)
                        else -> {
                            lowerBound = subtree.element
                            subtree = subtree.secondChild
                        }
                    }
                is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                    when {
                        elementContext { element < subtree.firstElement.element } -> {
                            upperBound = subtree.firstElement
                            subtree = subtree.firstChild
                        }
                        elementContext { element eq subtree.firstElement.element } -> return onCoincidence(subtree.firstElement)
                        elementContext { element < subtree.secondElement.element } -> {
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
    
    private fun removeBottomNode(node: Node<E>) {
        when (val holder = node.holder) {
            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
                val parent = holder.parent
                holder.dispose()
                parent.replaceChildWithReference(holder, null)
            }
            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
                val newHolder = twoNodeHolder(
                    isItBottom = true,
                    firstChild = null,
                    element = when (node) {
                        holder.firstElement -> holder.secondElement
                        holder.secondElement -> holder.firstElement
                        else -> throw IllegalStateException("Received not a holder of the node")
                    },
                    secondChild = null,
                )
                newHolder.parent = holder.parent
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
    
    private fun removeNode(node: Node<E>) {
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
                    is TwoThreeTree<E, EC>.TwoNodeHolder ->
                        when (node) {
                            holder.element -> holder.element = nextNode
                            else -> throw IllegalStateException("Received not a holder of the node")
                        }
                    is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                        when (node) {
                            holder.firstElement -> holder.firstElement = nextNode
                            holder.secondElement -> holder.secondElement = nextNode
                            else -> throw IllegalStateException("Received not a holder of the node")
                        }
                }
                when (nextHolder) {
                    is TwoThreeTree<E, EC>.TwoNodeHolder ->
                        when (nextNode) {
                            nextHolder.element -> nextHolder.element = node
                            else -> throw IllegalStateException("Received not a holder of the node")
                        }
                    is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                        when (nextNode) {
                            nextHolder.firstElement -> nextHolder.firstElement = node
                            nextHolder.secondElement -> nextHolder.secondElement = node
                            else -> throw IllegalStateException("Received not a holder of the node")
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
    }
    
    override val nodesView: KoneIterableListSet<ConnectedSearchTreeNode<E>> = Nodes()
    override val elementsView: KoneIterableListSet<E> = Elements()
    
    override fun add(element: E): ConnectedSearchTreeNode<E> =
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
                    lowerBoundHolder.isItBottom && lowerBoundHolder is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                        newLowerBoundHolder.parent = parent
                        lowerBoundHolder.dispose()
                    }
                    upperBoundHolder.isItBottom && upperBoundHolder is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                        newUpperBoundHolder.parent = parent
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
                        lowerBoundHolder as TwoThreeTree<E, EC>.ThreeNodeHolder
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
                        upperBoundHolder as TwoThreeTree<E, EC>.ThreeNodeHolder
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
                    else -> throw IllegalStateException("For some reason, lower and upper bounds' holders are both not at the bottom")
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
                check(minimum.holder.isItBottom) { "For some reason, minimum is not at the bottom" }
                when (minimumHolder) {
                    is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                        newMinimumHolder.parent = parent
                    }
                    is TwoThreeTree<E, EC>.ThreeNodeHolder ->
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
                    is TwoThreeTree<E, EC>.TwoNodeHolder -> {
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
                        newMaximumHolder.parent = parent
                    }
                    is TwoThreeTree<E, EC>.ThreeNodeHolder ->
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
    
    override fun find(element: E): ConnectedSearchTreeNode<E>? =
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
    
    override fun findSegmentFor(element: E): SearchSegmentResult<ConnectedSearchTreeNode<E>> =
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
        val tree: TwoThreeTree<E, *>
    }
    internal inner class TwoNodeHolder(
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<E>?,
        element: Node<E>,
        var secondChild: NodeHolder<E>?,
    ) : NodeHolder<E> {
        override var parent: NodeHolder<E>? = null
        private var _element: Node<E>? = element
        var element: Node<E>
            get() = _element!!
            set(value) { _element = value }
        
        override val tree: TwoThreeTree<E, *> get() = this@TwoThreeTree
        override fun dispose() {
            parent = null
            firstChild = null
            _element = null
            secondChild = null
        }
    }
    internal inner class ThreeNodeHolder(
        override val isItBottom: Boolean,
        var firstChild: NodeHolder<E>?,
        firstElement: Node<E>,
        var secondChild: NodeHolder<E>?,
        secondElement: Node<E>,
        var thirdChild: NodeHolder<E>?,
    ) : NodeHolder<E> {
        override var parent: NodeHolder<E>? = null
        private var _firstElement: Node<E>? = firstElement
        var firstElement: Node<E>
            get() = _firstElement!!
            set(value) { _firstElement = value }
        private var _secondElement: Node<E>? = secondElement
        var secondElement: Node<E>
            get() = _secondElement!!
            set(value) { _secondElement = value }
        
        override val tree: TwoThreeTree<E, *> get() = this@TwoThreeTree
        override fun dispose() {
            parent = null
            firstChild = null
            _firstElement = null
            secondChild = null
            _secondElement = null
            thirdChild = null
        }
    }
    
    private fun twoNodeHolder(
        isItBottom: Boolean,
        firstChild: NodeHolder<E>?,
        element: Node<E>,
        secondChild: NodeHolder<E>?,
    ): TwoNodeHolder {
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
        firstChild?.parent = newHolder
        element.holder = newHolder
        secondChild?.parent = newHolder
        return newHolder
    }
    
    private fun threeNodeHolder(
        isItBottom: Boolean,
        firstChild: NodeHolder<E>?,
        firstElement: Node<E>,
        secondChild: NodeHolder<E>?,
        secondElement: Node<E>,
        thirdChild: NodeHolder<E>?,
    ): ThreeNodeHolder {
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
        firstChild?.parent = newHolder
        firstElement.holder = newHolder
        secondChild?.parent = newHolder
        secondElement.holder = newHolder
        thirdChild?.parent = newHolder
        return newHolder
    }
    
    internal class Node<E>(
        override val element: E,
    ) : ConnectedSearchTreeNode<E> {
        private var _holder: NodeHolder<E>? = null
        internal var holder: NodeHolder<E>
            get() = _holder!!
            set(value) { _holder = value }
        override var nextNode: Node<E>? = null
            internal set
        override var previousNode: Node<E>? = null
            internal set
        
        override fun remove() {
            if (_holder == null) throw IllegalStateException("The node has already been removed")
            _holder!!.tree.removeNode(this)
            _holder = null
        }
    }
    
    internal class NodesIterator<E>(
        private var nextNode: Node<E>?,
        private val size: UInt,
    ) : KoneLinearIterator<Node<E>> {
        private var previousNode: Node<E>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextNode != null
        override fun nextIndex(): UInt {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): Node<E> {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextNode!!
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(nextIndex, size)
            nextIndex++
            previousNode = nextNode
            nextNode = nextNode!!.nextNode
        }
        
        override fun hasPrevious(): Boolean = previousNode != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): Node<E> {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return previousNode!!
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            nextIndex++
            nextNode = previousNode
            previousNode = previousNode!!.previousNode
        }
    }
    
    internal inner class Nodes : KoneIterableListSet<Node<E>>, KoneListWithContext<Node<E>, Equality<Node<E>>> {
        override val size: UInt get() = this@TwoThreeTree.size
        override val elementContext: Equality<Node<E>> get() = absoluteEquality()
        
        override fun get(index: UInt): Node<E> {
            if (index >= size) indexException(index, size)
            var currentNode = minimum!!
            repeat(index) {
                currentNode = currentNode.nextNode!!
            }
            return currentNode
        }
        
        override fun iterator(): KoneLinearIterator<Node<E>> = NodesIterator(minimum, size)
        
        // TODO: Add usual `toString` overload
    }
    
    internal class ElementsIterator<E>(
        private var nextNode: Node<E>?,
        private val size: UInt,
    ) : KoneLinearIterator<E> {
        private var previousNode: Node<E>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextNode != null
        override fun nextIndex(): UInt {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): E {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextNode!!.element
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(nextIndex, size)
            nextIndex++
            previousNode = nextNode
            nextNode = nextNode!!.nextNode
        }
        
        override fun hasPrevious(): Boolean = previousNode != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return previousNode!!.element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            nextIndex++
            nextNode = previousNode
            previousNode = previousNode!!.previousNode
        }
    }
    
    internal inner class Elements : KoneIterableListSet<E>, KoneListWithContext<E, EC> {
        override val size: UInt get() = this@TwoThreeTree.size
        override val elementContext: EC get() = this@TwoThreeTree.elementContext
        
        override fun get(index: UInt): E {
            if (index >= size) indexException(index, size)
            var currentNode = minimum!!
            repeat(index) {
                currentNode = currentNode.nextNode!!
            }
            return currentNode.element
        }
        
        override fun iterator(): KoneLinearIterator<E> = ElementsIterator(minimum, size)
        
        // TODO: Add usual `toString` overload
    }
}