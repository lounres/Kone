/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.implementations.KoneTwoThreeTreeList.Companion.size
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import io.kotest.assertions.fail


object KoneTwoThreeTreeListDescription : ListImplementationDescription {
    override val name get() = "KoneTwoThreeTreeList"
    
    override val listProducer: KoneListProducer get() = KoneTwoThreeTreeListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        override fun validate(list: KoneList<Any>) {
            if (list !is KoneTwoThreeTreeList<Any>) fail("The list is invalid")
            if (list.isDisposed) fail("The list is invalid")
            
            val size = list.size
            val rootHolder = list.rootHolder
            val firstNode = list.firstNode
            val lastNode = list.lastNode
            
            if (size == 0u) {
                if (rootHolder != null || firstNode != null || lastNode != null) fail("The list is invalid")
            } else {
                if (rootHolder == null || firstNode == null || lastNode == null) fail("The list is invalid")
                
                if (rootHolder.size != size) fail("The list is invalid")
                
                tailrec fun KoneTwoThreeTreeList.NodeHolder<Any>?.heightAddedTo(number: UInt): UInt =
                    when (this) {
                        null -> number
                        is KoneTwoThreeTreeList.TwoNodeHolder<Any> -> this.firstChild.heightAddedTo(number + 1u)
                        is KoneTwoThreeTreeList.ThreeNodeHolder<Any> -> this.firstChild.heightAddedTo(number + 1u)
                    }
                
                val depth = rootHolder.heightAddedTo(0u)
                
                fun validateSubtree(
                    holder: KoneTwoThreeTreeList.NodeHolder<Any>,
                    depth: UInt,
                    firstNode: KoneTwoThreeTreeList.Node<Any>,
                    lastNode: KoneTwoThreeTreeList.Node<Any>,
                ) {
                    if (holder.isDisposed) fail("The list is invalid")
                    if (holder.tree !== list) fail("The list is invalid")
                    if (depth == 0u) fail("The list is invalid")
                    if (depth == 1u) {
                        if (!holder.isItBottom) fail("The list is invalid")
                        when (holder) {
                            is KoneTwoThreeTreeList.TwoNodeHolder<Any> -> {
                                if (holder.firstChild != null || holder.secondChild != null) fail("The list is invalid")
                                if (holder.firstChildSize != 0u || holder.secondChildSize != 0u) fail("The list is invalid")
                                val actualNode = holder.element
                                if (actualNode !== firstNode || actualNode !== lastNode) fail("The list is invalid")
                            }
                            is KoneTwoThreeTreeList.ThreeNodeHolder<Any> -> {
                                if (holder.firstChild != null || holder.secondChild != null || holder.thirdChild != null) fail("The list is invalid")
                                if (holder.firstChildSize != 0u || holder.secondChildSize != 0u || holder.thirdChildSize != 0u) fail("The list is invalid")
                                val firstActualNode = holder.firstElement
                                val secondActualNode = holder.secondElement
                                if (firstNode !== firstActualNode || lastNode !== secondActualNode) fail("The list is invalid")
                                if (firstActualNode.nextNode !== secondActualNode || secondActualNode.previousNode !== firstActualNode) fail("The list is invalid")
                                if (firstActualNode.holder !== holder || secondActualNode.holder !== holder) fail("The list is invalid")
                            }
                        }
                    } else {
                        if (holder.isItBottom) fail("The list is invalid")
                        when (holder) {
                            is KoneTwoThreeTreeList.TwoNodeHolder<Any> -> {
                                if (holder.firstChild == null || holder.secondChild == null) fail("The list is invalid")
                                if (holder.firstChild!!.parent !== holder || holder.secondChild!!.parent !== holder) fail("The list is invalid")
                                if (holder.firstChildSize != holder.firstChild.size || holder.secondChildSize != holder.secondChild.size) fail("The list is invalid")
                                val node = holder.element
                                if (node.holder !== holder) fail("The list is invalid")
                                val previousNode = node.previousNode ?: fail("The list is invalid")
                                val nextNode = node.nextNode ?: fail("The list is invalid")
                                if (previousNode.nextNode !== node || nextNode.previousNode !== node) fail("The list is invalid")
                                validateSubtree(
                                    holder.firstChild!!,
                                    depth - 1u,
                                    firstNode,
                                    previousNode,
                                )
                                validateSubtree(
                                    holder.secondChild!!,
                                    depth - 1u,
                                    nextNode,
                                    lastNode
                                )
                            }
                            is KoneTwoThreeTreeList.ThreeNodeHolder<Any> -> {
                                if (holder.firstChild == null || holder.secondChild == null || holder.thirdChild == null) fail("The list is invalid")
                                if (holder.firstChild!!.parent !== holder || holder.secondChild!!.parent !== holder) fail("The list is invalid")
                                if (holder.firstChildSize != holder.firstChild.size || holder.secondChildSize != holder.secondChild.size || holder.thirdChildSize != holder.thirdChild.size) fail("The list is invalid")
                                val node1 = holder.firstElement
                                val node2 = holder.secondElement
                                if (node1.holder !== holder || node2.holder !== holder) fail("The list is invalid")
                                val previousNode1 = node1.previousNode ?: fail("The list is invalid")
                                val nextNode1 = node1.nextNode ?: fail("The list is invalid")
                                if (previousNode1.nextNode !== node1 || nextNode1.previousNode !== node1) fail("The list is invalid")
                                val previousNode2 = node2.previousNode ?: fail("The list is invalid")
                                val nextNode2 = node2.nextNode ?: fail("The list is invalid")
                                if (previousNode2.nextNode !== node2 || nextNode2.previousNode !== node2) fail("The list is invalid")
                                validateSubtree(
                                    holder.firstChild!!,
                                    depth - 1u,
                                    firstNode,
                                    previousNode1,
                                )
                                validateSubtree(
                                    holder.secondChild!!,
                                    depth - 1u,
                                    nextNode1,
                                    previousNode2,
                                )
                                validateSubtree(
                                    holder.thirdChild!!,
                                    depth - 1u,
                                    nextNode2,
                                    lastNode
                                )
                            }
                        }
                    }
                }
                
                validateSubtree(rootHolder, depth, firstNode, lastNode)
            }
        }
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>
        ) {
            validate(list)
            list as KoneTwoThreeTreeList<Any>
            
            if (iterator !is KoneTwoThreeTreeList.Iterator<Any>) fail("The iterator is invalid")
            if (iterator.list !== list) fail("The iterator is invalid")
            
            val nextNode = iterator.nextNode
            val nextIndex = iterator._nextIndex
            
            if (nextIndex != null && (nextNode?.index ?: list.size) != nextIndex) fail("The iterator is invalid")
        }
    }
}