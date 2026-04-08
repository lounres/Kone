/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.collections.deque.implementations.KoneListBackedDeque
import dev.lounres.kone.collections.deque.isEmpty
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.deque.popLast
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.implementations.KoneGCLinkedSizedList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.last
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions


class KoneConcurrentSundellTsigasNoddedDequeueConcurrencyOperations {
    typealias Element = Int
    
    private val dequeue = KoneConcurrentSundellTsigasNoddedDequeue<Element>()
    private val nodesDequeue = KoneListBackedDeque<KoneConcurrentSundellTsigasNoddedDequeue.Node<Element>>()
    
//    @StateRepresentation
//    fun stateRepresentation() = buildString {
//        appendLine()
//        appendLine("  head: ${dequeue.head}")
//        appendLine("  tail: ${dequeue.tail}")
//
//        scope {
//            val nodes = mutableSetOf<KoneConcurrentSundellTsigasNoddedDequeue.Node<Element>>()
//            val nodesToCheck = mutableSetOf(dequeue.head, dequeue.tail)
//            while (nodesToCheck.isNotEmpty()) {
//                val currentNode = nodesToCheck.first()
//                nodesToCheck.remove(currentNode)
//                nodes.add(currentNode)
//                val newNodes = setOfNotNull(currentNode.prev.load()?.node, currentNode.next.load()?.node)
//                for (newNode in newNodes) if (newNode !in nodes) nodesToCheck.add(newNode)
//            }
//            appendLine("  reachable nodes:")
//            for (node in nodes) {
//                appendLine("    $node:")
//
//                appendLine("      next:")
//                val nextLink = node.next.load()
//                if (nextLink != null) {
//                    appendLine("        node: ${nextLink.node}")
//                    appendLine("        isBeingDeleted: ${nextLink.isBeingDeleted}")
//                } else {
//                    appendLine("        null")
//                }
//
//                appendLine("      prev:")
//                val prevLink = node.prev.load()
//                if (prevLink != null) {
//                    appendLine("        node: ${prevLink.node}")
//                    appendLine("        isBeingDeleted: ${prevLink.isBeingDeleted}")
//                } else {
//                    appendLine("        null")
//                }
//            }
//        }
//    }
    
    @Operation
    fun addFirst(element: Element) {
        dequeue.addFirst(element)
    }
    
    @Operation
    fun addLast(element: Element) {
        dequeue.addLast(element)
    }
    
    @Operation(nonParallelGroup = "nodesDequeue")
    fun addFirstPushFirst(element: Element) {
        nodesDequeue.addFirst(dequeue.addFirst(element))
    }
    
    @Operation(nonParallelGroup = "nodesDequeue")
    fun addFirstPushLast(element: Element) {
        nodesDequeue.addLast(dequeue.addFirst(element))
    }
    
    @Operation(nonParallelGroup = "nodesDequeue")
    fun addLastPushFirst(element: Element) {
        nodesDequeue.addFirst(dequeue.addLast(element))
    }
    
    @Operation(nonParallelGroup = "nodesDequeue")
    fun addLastPushLast(element: Element) {
        nodesDequeue.addLast(dequeue.addLast(element))
    }

    @Operation
    fun popFirstMaybe() = dequeue.popFirstMaybe()?.value
    
    @Operation
    fun popLastMaybe() = dequeue.popLastMaybe()?.value
    
    @Operation(nonParallelGroup = "nodesDequeue")
    fun removeFirstNode() = nodesDequeue.run { if (isEmpty()) null else popFirst() }?.also { it.remove() }?.value
    
    @Operation(nonParallelGroup = "nodesDequeue")
    fun removeLastNode() = nodesDequeue.run { if (isEmpty()) null else popLast() }?.also { it.remove() }?.value
    
    class SequentialSpecification {
        private val dequeue = KoneGCLinkedSizedList<Element>()
        private val nodesDequeue = KoneListBackedDeque<KoneMutableListNode<Element>>()
        
        @Operation
        fun addFirst(element: Element) {
            dequeue.addAt(0u, element)
        }
        
        @Operation
        fun addLast(element: Element) {
            dequeue.add(element)
        }

        @Operation
        fun addFirstPushFirst(element: Element) {
            nodesDequeue.addFirst(dequeue.addNodeAt(0u, element))
        }

        @Operation
        fun addFirstPushLast(element: Element) {
            nodesDequeue.addLast(dequeue.addNodeAt(0u, element))
        }

        @Operation
        fun addLastPushFirst(element: Element) {
            nodesDequeue.addFirst(dequeue.addNode(element))
        }

        @Operation
        fun addLastPushLast(element: Element) {
            nodesDequeue.addLast(dequeue.addNode(element))
        }

        @Operation
        fun popFirstMaybe() =
            if (dequeue.isNotEmpty()) dequeue.first().also { dequeue.removeAt(0u) }
            else null

        @Operation
        fun popLastMaybe() =
            if (dequeue.isNotEmpty()) dequeue.last().also { dequeue.removeAt(dequeue.lastIndex) }
            else null

        @Operation
        fun removeFirstNode() =
            if (nodesDequeue.isNotEmpty()) nodesDequeue.popFirst().also { if (!it.isDetached) it.remove() }.element
            else null

        @Operation
        fun removeLastNode() =
            if (nodesDequeue.isNotEmpty()) nodesDequeue.popLast().also { if (!it.isDetached) it.remove() }.element
            else null
    }
}

val KoneConcurrentSundellTsigasNoddedDequeueConcurrencyTest by testSuite {
    test("stress") {
        StressOptions()
            .sequentialSpecification(KoneConcurrentSundellTsigasNoddedDequeueConcurrencyOperations.SequentialSpecification::class.java)
            .check(KoneConcurrentSundellTsigasNoddedDequeueConcurrencyOperations::class)
    }
    test("modelChecking") {
        ModelCheckingOptions()
            .sequentialSpecification(KoneConcurrentSundellTsigasNoddedDequeueConcurrencyOperations.SequentialSpecification::class.java)
            .check(KoneConcurrentSundellTsigasNoddedDequeueConcurrencyOperations::class)
    }
}