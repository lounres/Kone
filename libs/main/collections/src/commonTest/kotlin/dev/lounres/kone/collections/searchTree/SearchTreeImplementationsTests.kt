/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.searchTree

import dev.lounres.kone.collections.searchTree.implementations.KoneTwoThreeSearchTree
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.testEqualityByIteration
import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive


//interface SearchTreeBuilder {
//    fun <E> build(elementContext: Order<E>): SearchTree<E>
//}
//
//interface ConnectedSearchTreeBuilder: SearchTreeBuilder {
//    override fun <E> build(elementContext: Order<E>): LinkedSearchTree<E>
//}
//
//interface SearchTreeImplementationDescription {
//    val name: String
//    val builder: SearchTreeBuilder
//}
//
//val searchTreeImplementations = listOf<SearchTreeImplementationDescription>(
//    object : SearchTreeImplementationDescription {
//        override val name: String = "KoneTwoThreeSearchTree"
//        override val builder: ConnectedSearchTreeBuilder =
//            object : ConnectedSearchTreeBuilder {
//                override fun <E> build(elementContext: Order<E>): LinkedSearchTree<E> =
//                    KoneTwoThreeSearchTree(elementContext)
//            }
//    }
//)
//
//class SearchTreeImplementationsTests: FunSpec({
//    for (impl in searchTreeImplementations) context(impl.name) {
//        val builder = impl.builder
//
//        test("test mutability") {
//            val init = (0u .. 10u step 2).toKoneList()
//            val permutationsExhaustive = init.permutations().toList().exhaustive()
//            checkAll(permutationsExhaustive, permutationsExhaustive) { toAdd, toRemove ->
//                val tree = builder.build(defaultOrder<UInt>())
//                val nodesMap = mutableMapOf<UInt, SearchTreeNode<UInt>>()
//
//                for (item in toAdd) {
//                    val node = tree.add(item)
//                    nodesMap[item] = node
//                }
//
//                val sortedNodes = nodesMap.entries.sortedBy { it.key }
//                for (index in 0 ..< sortedNodes.size) {
//                    val (item, node) = sortedNodes[index]
//                    node.element shouldBe item
//                    tree.find(item) shouldBeSameInstanceAs node
//                    tree.size shouldBe sortedNodes.size.toUInt()
//                    tree.nodesView.size shouldBe sortedNodes.size.toUInt()
//                    tree.elementsView.size shouldBe sortedNodes.size.toUInt()
//                }
//
//                for (item in toRemove) {
//                    val node = nodesMap[item]!!
//                    node.remove()
//                }
//            }
//        }
//
//        if (builder is ConnectedSearchTreeBuilder) test("test elements and nodes views") {
//            val init = (0u .. 10u step 2).toKoneList()
//            val permutationsExhaustive = init.permutations().toList().exhaustive()
//            checkAll(permutationsExhaustive) { toAdd ->
//                val tree = builder.build(defaultOrder<UInt>())
//                val nodesMap = mutableMapOf<UInt, SearchTreeNode<UInt>>()
//
//                for (item in toAdd) {
//                    val node = tree.add(item)
//                    nodesMap[item] = node
//                    // TODO: Fix the test
////                    testEqualityByIteration(tree.elementsView, nodesMap.keys.sorted())
//                    testEqualityByIteration(tree.nodesView.toKoneList() /* TODO: Remove `.toKoneList()` */, nodesMap.entries.sortedBy { it.key }.map { it.value })
//                }
//            }
//        }
//    }
//})