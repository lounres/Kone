/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import dev.lounres.kone.scope
import org.jetbrains.kotlinx.lincheck.annotations.StateRepresentation
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions
import kotlin.test.Test


class KoneConcurrentSundellTsigasNoddedDequeueConcurrencyTest {
    typealias Element = Int
    
    private val dequeue = KoneConcurrentSundellTsigasNoddedDequeue<Element>()
    
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
    
//    @Operation
//    fun removeFirstIfPresent() {
//        dequeue.removeFirstIfPresent()
//    }

    @Operation
    fun popFirstMaybe() = dequeue.popFirstMaybe()
    
    @Operation
    fun popLastMaybe() = dequeue.popLastMaybe()
    
    @Test
    fun stress() {
        StressOptions()
            .actorsBefore(4)
            .threads(4)
            .actorsPerThread(4)
            .actorsAfter(4)
            .check(this::class)
    }
    
    @Test
    fun modelChecking() {
        ModelCheckingOptions()
            .actorsBefore(4)
            .threads(2)
            .actorsPerThread(4)
            .actorsAfter(4)
            .check(this::class)
    }
}