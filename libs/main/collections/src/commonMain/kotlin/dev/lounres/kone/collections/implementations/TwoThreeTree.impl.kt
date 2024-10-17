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
    
    private tailrec fun NodeHolder<E>.replaceChild(firstNewChild: NodeHolder<E>?, node: Node<E>, secondNewChild: NodeHolder<E>?, position: UByte) {
        when (this) {
            is TwoThreeTree<E, EC>.TwoNodeHolder -> {
                val parent = this.parent
                val isThisBottom = this.isItBottom
                val firstChild = this.firstChild
                val element = this.element
                val secondChild = this.secondChild
                this.dispose()
                
                val newNodeHolder = when (position) {
                    0u.toUByte() ->
                        ThreeNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstNewChild,
                            firstElement = node,
                            secondChild = secondNewChild,
                            secondElement = element,
                            thirdChild = secondChild,
                        )
                    1u.toUByte() ->
                        ThreeNodeHolder(
                            isItBottom = isThisBottom,
                            firstChild = firstChild,
                            firstElement = element,
                            secondChild = firstNewChild,
                            secondElement = node,
                            thirdChild = secondNewChild,
                        )
                    else -> throw IllegalStateException("Received an incorrect position")
                }
                
                newNodeHolder.firstElement.holder = newNodeHolder
                newNodeHolder.secondElement.holder = newNodeHolder
                
                when (parent) {
                    null -> rootHolder = newNodeHolder
                    is TwoThreeTree<E, EC>.TwoNodeHolder ->
                        when (this) {
                            parent.firstChild -> parent.firstChild = newNodeHolder
                            parent.secondChild -> parent.secondChild = newNodeHolder
                            else -> throw IllegalStateException("Trying to change parent's non-existent child")
                        }
                    is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                        when (this) {
                            parent.firstChild -> parent.firstChild = newNodeHolder
                            parent.secondChild -> parent.secondChild = newNodeHolder
                            parent.thirdChild -> parent.thirdChild = newNodeHolder
                            else -> throw IllegalStateException("Trying to change parent's non-existent child")
                        }
                }
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
                
                when (position) {
                    0u.toUByte() -> {
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
                    1u.toUByte() -> {
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
                    2u.toUByte() -> {
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
                        firstNewChild = firstNewParent,
                        node = parentNode,
                        secondNewChild = secondNewParent,
                        position = when (parent) {
                            is TwoThreeTree<E, EC>.TwoNodeHolder ->
                                when {
                                    parent.firstChild === this -> 0u.toUByte()
                                    parent.secondChild === this -> 1u.toUByte()
                                    else -> throw IllegalStateException("For some reason this holder is not a child of its own parent")
                                }
                            is TwoThreeTree<E, EC>.ThreeNodeHolder ->
                                when {
                                    parent.firstChild === this -> 0u.toUByte()
                                    parent.secondChild === this -> 1u.toUByte()
                                    parent.thirdChild === this -> 2u.toUByte()
                                    else -> throw IllegalStateException("For some reason this holder is not a child of its own parent")
                                }
                        },
                    )
                }
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
    
    private fun removeNode(node: Node<E>) {
//        val nextNode = node.nextNode
//        val previousNode = node.previousNode
//        when {
//            size == 1u -> {
//                rootHolder!!.dispose()
//                rootHolder = null
//                size = 0u
//            }
//
//            nextNode != null -> {
//                val holder = nextNode.holder
//                if (holder.isItBottom && holder is TwoThreeTree<E, EC>.ThreeNodeHolder) {
//                    val parent = holder.parent
//                    if (parent == null) {
//                        rootHolder = Tw
//                    }
//                }
//            }
//        }
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
                when {
                    lowerBound.holder.isItBottom && upperBound.holder.isItBottom -> {
                        check(lowerBound.holder === upperBound.holder) { "For some reason, lower and upper bounds' holders are not the same" }
                        lowerBound.holder.replaceChild(
                            firstNewChild = null,
                            node = newNode,
                            secondNewChild = null,
                            position = 1u,
                        )
                    }
                    lowerBound.holder.isItBottom -> {
                        lowerBound.holder.replaceChild(
                            firstNewChild = null,
                            node = newNode,
                            secondNewChild = null,
                            position = when(lowerBound.holder) {
                                is TwoThreeTree<E, EC>.TwoNodeHolder -> 1u
                                is TwoThreeTree<E, EC>.ThreeNodeHolder -> 2u
                            },
                        )
                    }
                    upperBound.holder.isItBottom -> {
                        lowerBound.holder.replaceChild(
                            firstNewChild = null,
                            node = newNode,
                            secondNewChild = null,
                            position = 0u,
                        )
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
                check(minimum.holder.isItBottom) { "For some reason, minimum is not at the bottom" }
                minimum.holder.replaceChild(
                    firstNewChild = null,
                    node = newNode,
                    secondNewChild = null,
                    position = 0u,
                )
                newNode
            },
            onGreaterThanMaximum = { maximum ->
                val newNode = Node(element)
                newNode.previousNode = maximum
                maximum.nextNode = newNode
                check(maximum.holder.isItBottom) { "For some reason, maximum is not at the bottom" }
                maximum.holder.replaceChild(
                    firstNewChild = null,
                    node = newNode,
                    secondNewChild = null,
                    position = when(maximum.holder) {
                        is TwoThreeTree<E, EC>.TwoNodeHolder -> 1u
                        is TwoThreeTree<E, EC>.ThreeNodeHolder -> 2u
                    },
                )
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
        val element: Node<E> get() = _element!!
        
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
        val firstElement: Node<E> get() = _firstElement!!
        private var _secondElement: Node<E>? = secondElement
        val secondElement: Node<E> get() = _secondElement!!
        
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