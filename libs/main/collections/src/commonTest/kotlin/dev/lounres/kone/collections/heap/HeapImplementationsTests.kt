/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap


//interface MinimumHeapBuilder {
//    fun <E, P> build(priorityContext: Order<P>): MinimumHeap<E, P>
//}
//
//interface MinimumHeapDescription {
//    val name: String
//    val builder: MinimumHeapBuilder
//}
//
//val minHeapImplementations = listOf<MinimumHeapDescription>(
//    object : MinimumHeapDescription {
//        override val name = "BinaryGCMinimumHeap"
//        override val builder: MinimumHeapBuilder =
//            object : MinimumHeapBuilder {
//                override fun <E, P> build(priorityContext: Order<P>): MinimumHeap<E, P> =
//                    KoneGCBinaryMinimumHeap(priorityContext)
//            }
//    }
//)
//
//class HeapImplementationsTests : FunSpec({
//    for (impl in minHeapImplementations) context(impl.name) {
//        val builder = impl.builder
//
//        test("test mutability") {
//            val init = koneListOf(0u, 0u, 2u, 4u, 4u, 4u).sorted()
//            val permutationsExhaustive = init.permutationsWithoutRepetitions().toList().exhaustive()
//            checkAll(permutationsExhaustive) { toAdd ->
//                val heap = builder.build<String, UInt>(defaultOrder())
//
//                for ((index, item) in toAdd.withIndex()) {
//                    val node = heap.add("$index", item)
//                    node.element shouldBe "$index"
//                    node.priority shouldBe item
//                    heap.size shouldBe index + 1u
//                }
//
//                for (item in init) {
//                    heap.popMinimum().priority shouldBe item
//                }
//            }
//        }
//    }
//})