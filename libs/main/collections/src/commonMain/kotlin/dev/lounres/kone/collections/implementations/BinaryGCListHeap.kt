/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.HeapNode
import dev.lounres.kone.collections.MinimumHeapWithContext
import dev.lounres.kone.collections.noMatchingKeyException
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.context.invoke


//@Suppress("UNCHECKED_CAST")
//public class BinaryGCListHeap<E, out EC: Equality<E>, P, out PC: Order<P>> internal constructor(
//    size: UInt = 0u,
//    private var root: RootNode? = null,
//    private var lastNode: Node<E, P>? = null,
//    override val elementContext: EC,
//    override val priorityContext: PC,
//): MinimumHeapWithContext<E, EC, P, PC> {
//    override var size: UInt = size
//        private set
//
//    private fun findNode(element: E): Node<E, P>? {
//        fun Node<E, P>.findNodeInSubtree(): Node<E, P>? =
//            if (elementContext { this.element eq element }) this else
//                leftChild?.findNodeInSubtree() ?:
//                rightChild?.findNodeInSubtree()
//
//        return root?.findNodeInSubtree()
//    }
//
//    private fun justRemoveLeafNode(node: Node<E, P>) {
//        check(node.leftChild != null && node.rightChild != null) { "For some reason non-leaf node is being removed" }
//        when (node) {
//            is BinaryGCListHeap<E, *, P, *>.ChildNode -> {
//                val parent = node.parent
//                val isItALeftChild = parent.leftChild === node
//                val isItARightChild = parent.rightChild === node
//                when {
//                    isItALeftChild && isItARightChild -> throw IllegalStateException("Child node's parent stores it as both left child and right child")
//                    isItALeftChild -> parent.leftChild = null
//                    isItARightChild -> parent.rightChild = null
//                    else -> throw IllegalStateException("Child node's parent does not store it as either left child or right child")
//                }
//            }
//            is BinaryGCListHeap<E, *, P, *>.RootNode -> {
//                root = null
//                lastNode = null
//            }
//        }
//    }
//
//    private fun swapNodesIdentities(node1: Node<E, P>, node2: Node<E, P>) {
//        node1.element = node2.element.also { node2.element = node1.element }
//        node1._priority = node2._priority.also { node2._priority = node1._priority }
//    }
//
//    private tailrec fun siftTheNode(node: Node<E, P>) {
//        if (node is BinaryGCListHeap<E, *, P, *>.ChildNode && priorityContext { node.parent.priority > node.priority }) {
//            swapNodesIdentities(node, node.parent)
//            siftTheNode(node.parent)
//            return
//        }
//
//        val leftChild = node.leftChild
//        val rightChild = node.rightChild
//        when {
//             leftChild != null && rightChild != null -> {
//                 when {
//                     priorityContext { leftChild.priority <= rightChild.priority && leftChild.priority < node.priority } -> {
//                         swapNodesIdentities(leftChild, node)
//                         siftTheNode(leftChild)
//                     }
//                     priorityContext { rightChild.priority <= leftChild.priority && rightChild.priority < node.priority } -> {
//                         swapNodesIdentities(rightChild, node)
//                         siftTheNode(rightChild)
//                     }
//                 }
//             }
//            leftChild != null && priorityContext { leftChild.priority < node.priority } -> {
//                swapNodesIdentities(leftChild, node)
//                siftTheNode(leftChild)
//            }
//            rightChild != null && priorityContext { rightChild.priority < node.priority } -> {
//                swapNodesIdentities(rightChild, node)
//                siftTheNode(rightChild)
//            }
//        }
//    }
//
//    private fun removeNode(node: Node<E, P>) {
//        if (node !== lastNode) swapNodesIdentities(node, lastNode!!)
//
//        justRemoveLeafNode(lastNode!!)
//        size--
//
//        TODO()
//    }
//
//    private fun decreasePriorityFor(node: Node<E, P>, newPriority: P) {
//        node._priority = newPriority
//        siftTheNode(node)
//    }
//
//    override fun contains(element: E): Boolean = findNode(element) != null
//
//    override fun add(element: E, priority: P): HeapNode<E, P> {
//        TODO("Not yet implemented")
//    }
//
//    override fun decreasePriority(element: E, newPriority: P) {
//        decreasePriorityFor(findNode(element) ?: noMatchingKeyException(element), newPriority)
//    }
//
//    override fun popMinimum(): HeapNode<E, P> {
//        TODO("Not yet implemented")
//    }
//
//    internal sealed class Node<E, P>(
//        override var element: E,
//        var _priority: P,
//        var leftChild: BinaryGCListHeap<E, *, P, *>.ChildNode?,
//        var rightChild: BinaryGCListHeap<E, *, P, *>.ChildNode?,
//    ): HeapNode<E, P>
//
//    internal inner class RootNode(
//        element: E,
//        priority: P,
//        leftChild: BinaryGCListHeap<E, *, P, *>.ChildNode? = null,
//        rightChild: BinaryGCListHeap<E, *, P, *>.ChildNode? = null,
//    ) : Node<E, P>(
//        element = element,
//        _priority = priority,
//        leftChild = leftChild,
//        rightChild = rightChild,
//    ) {
//        override fun remove() = this@BinaryGCListHeap.removeNode(this)
//
//        override var priority: P
//            get() = _priority
//            set(value) {
//                require(priorityContext { value <= _priority }) { "Can not assign increased priority to minimum heap node" }
//
//            }
//    }
//
//    internal inner class ChildNode(
//        element: E,
//        priority: P,
//        leftChild: BinaryGCListHeap<E, *, P, *>.ChildNode?,
//        rightChild: BinaryGCListHeap<E, *, P, *>.ChildNode?,
//        parent: Node<E, P>,
//    ) : Node<E, P>(
//        element = element,
//        leftChild = leftChild,
//        rightChild = rightChild,
//    ) {
//        private var _parent: Node<E, P>? = parent
//        var parent: Node<E, P>
//            get() = _parent!!
//            set(value) { _parent = value }
//        override fun remove() = this@BinaryGCListHeap.removeNode(this)
//    }
//}