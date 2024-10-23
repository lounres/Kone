/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.MinimumHeap
import dev.lounres.kone.collections.koneIterableListOf
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.sorted
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.combinatorics.enumerative.permutationsWithoutRepetitions
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive


interface MinimumHeapBuilder {
    fun <E, P> build(elementContext: Equality<E>, priorityContext: Order<P>): MinimumHeap<E, P>
}

interface MinimumHeapDescription {
    val name: String
    val builder: MinimumHeapBuilder
}

val minHeapImplementations = listOf<MinimumHeapDescription>(
    object : MinimumHeapDescription {
        override val name = "BinaryGCMinimumHeap"
        override val builder: MinimumHeapBuilder =
            object : MinimumHeapBuilder {
                override fun <E, P> build(elementContext: Equality<E>, priorityContext: Order<P>): MinimumHeap<E, P> =
                    BinaryGCMinimumHeap(elementContext, priorityContext)
            }
    }
)

class HeapImplementationsTests : FunSpec({
    for (impl in minHeapImplementations) context(impl.name) {
        val builder = impl.builder

        test("test mutability") {
            val init = koneIterableListOf(0u, 0u, 2u, 4u, 4u, 4u).sorted()
            val permutationsExhaustive = init.permutationsWithoutRepetitions().toList().exhaustive()
            checkAll(permutationsExhaustive) { toAdd ->
                val heap = builder.build(defaultEquality<String>(), defaultOrder<UInt>())
                
                for ((index, item) in toAdd.withIndex()) {
                    val node = heap.add("$index", item)
                    node.element shouldBe "$index"
                    node.priority shouldBe item
                    heap.size shouldBe index + 1u
                }
                
                for (item in init) {
                    val node = heap.takeMinimum()
                    node.priority shouldBe item
                    heap.popMinimum() shouldBe node
                }
            }
        }
    }
})