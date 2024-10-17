/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.ConnectedSearchTree
import dev.lounres.kone.collections.ConnectedSearchTreeNode
import dev.lounres.kone.collections.KoneIterableListSet
import dev.lounres.kone.collections.SearchSegmentResult
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.context.invoke


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
                rootHolder = TwoNodeHolder(
                    isItBottom = false,
                    firstChild = firstNewChild,
                    element = node,
                    secondChild = secondNewChild,
                )
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
                        ThreeNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstNewChild,
                            firstElement = node,
                            secondChild = secondNewChild,
                            secondElement = element,
                            thirdChild = secondChild,
                        )
                    secondChild ->
                        ThreeNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstChild,
                            firstElement = element,
                            secondChild = firstNewChild,
                            secondElement = node,
                            thirdChild = secondNewChild,
                        )
                    else -> throw IllegalStateException("Received not a child of the parent")
                }
                
                newNodeHolder.firstElement.holder = newNodeHolder
                newNodeHolder.secondElement.holder = newNodeHolder
                
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
                        firstNewParent = TwoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstNewChild,
                            element = node,
                            secondChild = secondNewChild,
                        )
                        parentNode = firstElement
                        secondNewParent = TwoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = secondChild,
                            element = secondElement,
                            secondChild = thirdChild,
                        )
                    }
                    secondChild -> {
                        firstNewParent = TwoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstChild,
                            element = firstElement,
                            secondChild = firstNewChild,
                        )
                        parentNode = node
                        secondNewParent = TwoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = secondNewChild,
                            element = secondElement,
                            secondChild = thirdChild,
                        )
                    }
                    thirdChild -> {
                        firstNewParent = TwoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstChild,
                            element = firstElement,
                            secondChild = secondChild,
                        )
                        parentNode = secondElement
                        secondNewParent = TwoNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstNewChild,
                            element = node,
                            secondChild = secondNewChild,
                        )
                    }
                    else -> throw IllegalStateException("Received an incorrect position")
                }
                
                firstNewParent.element.holder = firstNewParent
                secondNewParent.element.holder = secondNewParent
                
                if (parent == null) {
                    rootHolder = TwoNodeHolder(
                        isItBottom = false,
                        firstChild = firstNewParent,
                        element = parentNode,
                        secondChild = secondNewParent,
                    )
                } else {
                    parent.replaceChild(
                        oldChild = this,
                        firstNewChild = firstNewParent,
                        node = parentNode,
                        secondNewChild = secondNewParent,
                    )
                }
            }
        }
    }
    
    private tailrec fun NodeHolder<E>?.replaceChildWithReference(oldChild: NodeHolder<E>, referredChild: NodeHolder<E>?) {
        when (this) {
            null -> {
                check(rootHolder === oldChild) { "For some reason non-root holder tries to replace root one" }
                rootHolder = referredChild
            }
            is TwoThreeTree<E, EC>.TwoNodeHolder ->
                when (oldChild) {
                    this.firstChild ->
                        when (val secondChild = this.secondChild!!) {
                            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
                                val parent = this.parent
                                val newChild = ThreeNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = referredChild,
                                    firstElement = this.element,
                                    secondChild = secondChild.firstChild,
                                    secondElement = secondChild.element,
                                    thirdChild = secondChild.secondChild,
                                )
                                newChild.firstElement.holder = newChild
                                newChild.secondElement.holder = newChild
                                this.dispose()
                                secondChild.dispose()
                                parent.replaceChildWithReference(this, newChild)
                            }
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = TwoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = referredChild,
                                    element = this.element,
                                    secondChild = secondChild.firstChild,
                                )
                                newFirstChild.element.holder = newFirstChild
                                val newSecondChild = TwoNodeHolder(
                                    isItBottom = secondChild.isItBottom,
                                    firstChild = secondChild.secondChild,
                                    element = secondChild.secondElement,
                                    secondChild = secondChild.thirdChild,
                                )
                                newSecondChild.element.holder = newSecondChild
                                val newThis = TwoNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    element = secondChild.firstElement,
                                    secondChild = newSecondChild
                                )
                                newThis.element.holder = newThis
                                secondChild.dispose()
                                this.dispose()
                                parent.replaceChild(this, newThis)
                            }
                        }
                    this.secondChild ->
                        when (val firstChild = this.firstChild!!) {
                            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
                                val parent = this.parent
                                val newChild = ThreeNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.firstChild,
                                    firstElement = firstChild.element,
                                    secondChild = firstChild.secondChild,
                                    secondElement = this.element,
                                    thirdChild = referredChild,
                                )
                                newChild.firstElement.holder = newChild
                                newChild.secondElement.holder = newChild
                                this.dispose()
                                firstChild.dispose()
                                parent.replaceChildWithReference(this, newChild)
                            }
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
                                val parent = this.parent
                                val newFirstChild = TwoNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.firstChild,
                                    element = firstChild.firstElement,
                                    secondChild = firstChild.secondChild,
                                )
                                newFirstChild.element.holder = newFirstChild
                                val newSecondChild = TwoNodeHolder(
                                    isItBottom = firstChild.isItBottom,
                                    firstChild = firstChild.thirdChild,
                                    element = this.element,
                                    secondChild = referredChild,
                                )
                                newSecondChild.element.holder = newSecondChild
                                val newThis = TwoNodeHolder(
                                    isItBottom = false,
                                    firstChild = newFirstChild,
                                    element = firstChild.secondElement,
                                    secondChild = newSecondChild
                                )
                                newThis.element.holder = newThis
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
                                TODO()
                            }
                            is TwoThreeTree<E, EC>.ThreeNodeHolder -> TODO()
                        }
                    this.secondChild -> TODO()
                    this.thirdChild -> TODO()
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
            is TwoThreeTree<E, EC>.ThreeNodeHolder -> {
                val newHolder = TwoNodeHolder(
                    isItBottom = true,
                    firstChild = null,
                    element = when (node) {
                        holder.firstElement -> holder.secondElement
                        holder.secondElement -> holder.firstElement
                        else -> throw IllegalStateException("Received not a holder of the node")
                    },
                    secondChild = null,
                )
                newHolder.element.holder = newHolder
                holder.parent.replaceChild(holder, newHolder)
                holder.dispose()
            }
            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
                holder.parent.replaceChildWithReference(holder, null)
                holder.dispose()
            }
        }
    }
    
    private fun removeNode(node: Node<E>) {
        val nextNode = node.nextNode
        val previousNode = node.previousNode
        when {
            size == 1u -> {
                rootHolder!!.dispose()
                rootHolder = null
                size = 0u
            }
            node.holder.isItBottom -> removeBottomNode(node)
            nextNode != null -> {
                val holder = node.holder
                val nextHolder = nextNode.holder
                when (holder) {
                    is TwoThreeTree<E, EC>.TwoNodeHolder -> TODO()
                    is TwoThreeTree<E, EC>.ThreeNodeHolder -> TODO()
                }
                when (nextHolder) {
                    is TwoThreeTree<E, EC>.TwoNodeHolder -> TODO()
                    is TwoThreeTree<E, EC>.ThreeNodeHolder -> TODO()
                }
            }
        }
        TODO()
    }
    
    override val nodesView: KoneIterableListSet<ConnectedSearchTreeNode<E>> = TODO("Not yet implemented")
    override val elementsView: KoneIterableListSet<ConnectedSearchTreeNode<E>> = TODO("Not yet implemented")
    
    override fun add(element: E): ConnectedSearchTreeNode<E> =
        findSegmentForAndDo(
            element = element,
            onEmpty = {
                val newNode = Node(element)
                val newHolder = TwoNodeHolder(
                    isItBottom = true,
                    firstChild = null,
                    element = newNode,
                    secondChild = null
                )
                rootHolder = newHolder
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
                        lowerBoundHolder.parent.replaceChild(
                            oldChild = lowerBoundHolder,
                            newChild = ThreeNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                firstElement = lowerBound,
                                secondChild = null,
                                secondElement = newNode,
                                thirdChild = null,
                            ).also {
                                lowerBound.holder = it
                                newNode.holder = it
                            },
                        )
                        lowerBoundHolder.dispose()
                    }
                    upperBoundHolder.isItBottom && upperBoundHolder is TwoThreeTree<E, EC>.TwoNodeHolder -> {
                        upperBoundHolder.parent.replaceChild(
                            oldChild = upperBoundHolder,
                            newChild = ThreeNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                firstElement = newNode,
                                secondChild = null,
                                secondElement = upperBound,
                                thirdChild = null,
                            ).also {
                                upperBound.holder = it
                                newNode.holder = it
                            },
                        )
                        upperBoundHolder.dispose()
                    }
                    lowerBoundHolder.isItBottom && upperBoundHolder.isItBottom -> {
                        check(lowerBoundHolder === upperBoundHolder) { "For some reason, lower and upper bounds' holders are both bottom but are not the same" }
                        lowerBoundHolder.parent.replaceChild(
                            oldChild = lowerBoundHolder,
                            firstNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = lowerBound,
                                secondChild = null,
                            ).also{
                                lowerBound.holder = it
                            },
                            node = newNode,
                            secondNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = upperBound,
                                secondChild = null,
                            ).also {
                                upperBound.holder = it
                            },
                        )
                        lowerBoundHolder.dispose()
                    }
                    lowerBoundHolder.isItBottom -> {
                        lowerBoundHolder as TwoThreeTree<E, EC>.ThreeNodeHolder
                        lowerBoundHolder.parent.replaceChild(
                            oldChild = lowerBoundHolder,
                            firstNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = lowerBoundHolder.firstElement,
                                secondChild = null,
                            ).also {
                                lowerBoundHolder.firstElement.holder = it
                            },
                            node = lowerBoundHolder.secondElement,
                            secondNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ).also {
                                newNode.holder = it
                            },
                        )
                        lowerBoundHolder.dispose()
                    }
                    upperBoundHolder.isItBottom -> {
                        upperBoundHolder as TwoThreeTree<E, EC>.ThreeNodeHolder
                        upperBoundHolder.parent.replaceChild(
                            oldChild = upperBoundHolder,
                            firstNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ).also {
                                newNode.holder = it
                            },
                            node = upperBoundHolder.firstElement,
                            secondNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = upperBoundHolder.secondElement,
                                secondChild = null,
                            ).also {
                                upperBoundHolder.secondElement.holder = it
                            },
                        )
                        upperBoundHolder.dispose()
                    }
                    else -> throw IllegalStateException("For some reason, lower and upper bounds' holders are both not at the bottom")
                }
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
                    is TwoThreeTree<E, EC>.TwoNodeHolder ->
                        minimumHolder.parent.replaceChild(
                            oldChild = minimumHolder,
                            newChild = ThreeNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                firstElement = newNode,
                                secondChild = null,
                                secondElement = minimum,
                                thirdChild = null,
                            ).also {
                                newNode.holder = it
                                minimum.holder = it
                            }
                        )
                    is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                        minimumHolder.parent.replaceChild(
                            oldChild = minimumHolder,
                            firstNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ).also {
                                newNode.holder = it
                            },
                            node = minimumHolder.firstElement,
                            secondNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = minimumHolder.secondElement,
                                secondChild = null,
                            ).also {
                                minimumHolder.secondElement.holder = it
                            },
                        )
                }
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
                    is TwoThreeTree<E, EC>.TwoNodeHolder ->
                        maximumHolder.parent.replaceChild(
                            oldChild = maximumHolder,
                            newChild = ThreeNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                firstElement = maximum,
                                secondChild = null,
                                secondElement = newNode,
                                thirdChild = null,
                            ).also {
                                maximum.holder = it
                                newNode.holder = it
                            }
                        )
                    is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                        maximumHolder.parent.replaceChild(
                            oldChild = maximumHolder,
                            firstNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = maximumHolder.firstElement,
                                secondChild = null,
                            ).also {
                                maximumHolder.firstElement.holder = it
                            },
                            node = maximumHolder.secondElement,
                            secondNewChild = TwoNodeHolder(
                                isItBottom = true,
                                firstChild = null,
                                element = newNode,
                                secondChild = null,
                            ).also {
                                newNode.holder = it
                            },
                        )
                }
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
}