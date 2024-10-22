/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.KoneSettableIterableList
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.repeat
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.uInt
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import kotlin.test.fail


interface IterableListBuilder {
    fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneIterableList<E>
}

interface SettableIterableListBuilder : IterableListBuilder {
    override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneSettableIterableList<E>
}

interface MutableFixedCapacityIterableListBuilder : SettableIterableListBuilder {
    fun <E, EC: Equality<E>> build(capacity: UInt, elementContext: EC): KoneMutableIterableList<E>
    override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E>
    fun <E, EC: Equality<E>> buildByGenerator(size: UInt, capacity: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E>
}

interface MutableIterableListBuilder : SettableIterableListBuilder {
    fun <E, EC: Equality<E>> build(elementContext: EC): KoneMutableIterableList<E>
    override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E>
}

interface ListImplementationDescription {
    val name: String
    val builder: IterableListBuilder
}

val listImplementations = listOf<ListImplementationDescription>(
    object : ListImplementationDescription {
        override val name = "KoneFixedCapacityArrayList"
        override val builder: MutableFixedCapacityIterableListBuilder =
            object : MutableFixedCapacityIterableListBuilder {
                override fun <E, EC : Equality<E>> build(capacity: UInt, elementContext: EC): KoneMutableIterableList<E> =
                    KoneFixedCapacityArrayList(capacity, elementContext)
                override fun <E, EC : Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneFixedCapacityArrayList(size, elementContext, generator)
                override fun <E, EC : Equality<E>> buildByGenerator(size: UInt, capacity: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneFixedCapacityArrayList(size, capacity, elementContext, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneFixedCapacityLinkedArrayList"
        override val builder: MutableFixedCapacityIterableListBuilder =
            object : MutableFixedCapacityIterableListBuilder {
                override fun <E, EC : Equality<E>> build(capacity: UInt, elementContext: EC): KoneMutableIterableList<E> =
                    KoneFixedCapacityLinkedArrayList(capacity, elementContext)
                override fun <E, EC : Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneFixedCapacityLinkedArrayList(size, elementContext, generator)
                override fun <E, EC : Equality<E>> buildByGenerator(size: UInt, capacity: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneFixedCapacityLinkedArrayList(size, capacity, elementContext, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneGrowableArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E, EC: Equality<E>> build(elementContext: EC): KoneMutableIterableList<E> =
                    KoneGrowableArrayList(elementContext)
                override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneGrowableArrayList(size, elementContext, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneGrowableLinkedArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E, EC: Equality<E>> build(elementContext: EC): KoneMutableIterableList<E> =
                    KoneGrowableLinkedArrayList(elementContext)
                override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneGrowableLinkedArrayList(size, elementContext, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneLinkedGCList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E, EC: Equality<E>> build(elementContext: EC): KoneMutableIterableList<E> =
                    KoneLinkedGCList(elementContext)
                override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneLinkedGCList(size, elementContext, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneResizableArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E, EC: Equality<E>> build(elementContext: EC): KoneMutableIterableList<E> =
                    KoneResizableArrayList(elementContext)
                override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneResizableArrayList(size, elementContext, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneResizableLinkedArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E, EC: Equality<E>> build(elementContext: EC): KoneMutableIterableList<E> =
                    KoneResizableLinkedArrayList(elementContext)
                override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneMutableIterableList<E> =
                    KoneResizableLinkedArrayList(size, elementContext, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneSettableArrayList"
        override val builder: SettableIterableListBuilder =
            object : SettableIterableListBuilder {
                override fun <E, EC: Equality<E>> buildByGenerator(size: UInt, elementContext: EC, generator: (UInt) -> E): KoneSettableIterableList<E> =
                    KoneSettableArrayList(size, elementContext, generator)
            }
    },
)

fun <E> testEqualityByIteration(list1: KoneIterableList<E>, list2: List<E>) {
    withClue("Checking equality of the lists by iteration through them") {
        val listIterator = list1.iterator()
        for (i in 0u ..< list2.size.toUInt()) {
            if (!listIterator.hasNext()) fail("List iterator stopped before the length ended")
            val nextValue = listIterator.getAndMoveNext()
            withClue({ "Checking equality of elements at index $i" }) {
                nextValue shouldBe list2[i.toInt()]
            }
        }
        if (listIterator.hasNext()) fail("List iterator has extra elements")
    }
}

fun <E> testEqualityByStringRepresentation(list1: KoneIterableList<E>, list2: List<E>) {
    withClue("Checking equality of the lists' string representations") {
        list1.toString() shouldBe list2.toString()
    }
}

sealed interface MutableListOperation<out E> {
    data class AddAt<out E>(val index: UInt, val element: E): MutableListOperation<E>
    data class RemoveAt(val index: UInt): MutableListOperation<Nothing>
}

data class MutableListOperationWithResult<out E>(
    val initialList: List<E>,
    val numberOfOperations: UInt,
    val operations: List<MutableListOperation<E>>,
    val results: List<List<E>>,
)

fun <E> arbMutableListOperationsWithResults(
    arbElements: Arb<E>,
    initialSize: UInt,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): Arb<MutableListOperationWithResult<E>> = arbitrary { source ->
    val initialList = List(initialSize.toInt()) { arbElements.bind() }
    val operations = mutableListOf<MutableListOperation<E>>()
    val results = mutableListOf<List<E>>()
    repeat(numberOfOperations) {
        val lastList = results.lastOrNull() ?: initialList
        if ((capacity == null || capacity.toInt() > lastList.size) && (lastList.isEmpty() || source.random.nextBoolean())) {
            val newElement = arbElements.bind()
            val insertionIndex = source.random.nextInt(lastList.size + 1)
            operations.add(MutableListOperation.AddAt(insertionIndex.toUInt(), newElement))
            results.add(lastList.subList(0, insertionIndex) + newElement + lastList.subList(insertionIndex, lastList.size))
        } else {
            val deletionIndex = source.random.nextInt(lastList.size)
            operations.add(MutableListOperation.RemoveAt(deletionIndex.toUInt()))
            results.add(lastList.subList(0, deletionIndex) + lastList.subList(deletionIndex + 1, lastList.size))
        }
    }
    MutableListOperationWithResult(
        initialList,
        numberOfOperations,
        operations,
        results,
    )
}

class ListImplementationsTests: FunSpec({
    for (impl in listImplementations) context(impl.name) {
        val builder = impl.builder
        
        test("test generative construction") {
            checkAll(Exhaustive.ints(0 .. 20)) { length ->
                checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                    val list = builder.buildByGenerator(length.toUInt(), defaultEquality()) { input[it.toInt()] }
                    testEqualityByIteration(list, input)
                    testEqualityByStringRepresentation(list, input)
                }
            }
        }
        
        if (builder is MutableIterableListBuilder) test("test element-by-element extension") {
            checkAll(Exhaustive.ints(0..20)) { length ->
                checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                    val list = builder.build(defaultEquality<UInt>())
                    testEqualityByIteration(list, emptyList())
                    testEqualityByStringRepresentation(list, emptyList())
                    for (index in 0 ..< length) {
                        list.add(input[index.toInt()])
                        testEqualityByIteration(list, input.subList(0, index + 1))
                        testEqualityByStringRepresentation(list, input.subList(0, index + 1))
                    }
                }
            }
        }
        
        if (builder is MutableFixedCapacityIterableListBuilder) test("test element-by-element extension") {
            checkAll(Exhaustive.ints(0..20)) { length ->
                checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                    val list = builder.build(30u, defaultEquality<UInt>())
                    testEqualityByIteration(list, emptyList())
                    testEqualityByStringRepresentation(list, emptyList())
                    for (index in 0 ..< length) {
                        list.add(input[index.toInt()])
                        testEqualityByIteration(list, input.subList(0, index + 1))
                        testEqualityByStringRepresentation(list, input.subList(0, index + 1))
                    }
                }
            }
        }
        
        if (builder is MutableIterableListBuilder) test("test mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                val mutableList = builder.buildByGenerator(arbData.initialList.size.toUInt(), defaultEquality()) { arbData.initialList[it.toInt()] }
                repeat(arbData.numberOfOperations) {
                    val operation = arbData.operations[it.toInt()]
                    val expected = arbData.results[it.toInt()]
                    withClue("at iteration $it with current state $mutableList, operation $operation, and expected result $expected") {
                        when (operation) {
                            is MutableListOperation.AddAt<UInt> -> mutableList.addAt(operation.index, operation.element)
                            is MutableListOperation.RemoveAt -> mutableList.removeAt(operation.index)
                        }
                        testEqualityByIteration(mutableList, expected)
                        testEqualityByStringRepresentation(mutableList, expected)
                    }
                }
            }
        }
        
        if (builder is MutableFixedCapacityIterableListBuilder) test("test mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
                val mutableList = builder.buildByGenerator(arbData.initialList.size.toUInt(), 20u, defaultEquality()) { arbData.initialList[it.toInt()] }
                repeat(arbData.numberOfOperations) {
                    val operation = arbData.operations[it.toInt()]
                    val expected = arbData.results[it.toInt()]
                    withClue("at iteration $it with current state $mutableList, operation $operation, and expected result $expected") {
                        when (operation) {
                            is MutableListOperation.AddAt<UInt> -> mutableList.addAt(operation.index, operation.element)
                            is MutableListOperation.RemoveAt -> mutableList.removeAt(operation.index)
                        }
                        testEqualityByIteration(mutableList, expected)
                        testEqualityByStringRepresentation(mutableList, expected)
                    }
                }
            }
        }
        
        if (builder is MutableIterableListBuilder) test("test iterator mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                val mutableList = builder.buildByGenerator(arbData.initialList.size.toUInt(), defaultEquality()) { arbData.initialList[it.toInt()] }
                var nextIteratorIndex = 5u
                val iterator = mutableList.iteratorFrom(nextIteratorIndex)
                repeat(arbData.numberOfOperations) {
                    val operation = arbData.operations[it.toInt()]
                    val expected = arbData.results[it.toInt()]
                    withClue("at iteration $it with current state $mutableList, operation $operation, and expected result $expected") {
                        when (operation) {
                            is MutableListOperation.AddAt<UInt> -> {
                                if (operation.index >= nextIteratorIndex) {
                                    while (operation.index > nextIteratorIndex) {
                                        nextIteratorIndex++
                                        iterator.moveNext()
                                    }
                                    iterator.addNext(operation.element)
                                } else {
                                    while (operation.index < nextIteratorIndex) {
                                        nextIteratorIndex--
                                        iterator.movePrevious()
                                    }
                                    iterator.addPrevious(operation.element)
                                    nextIteratorIndex++
                                }
                            }
                            is MutableListOperation.RemoveAt -> {
                                if (operation.index >= nextIteratorIndex) {
                                    while (operation.index > nextIteratorIndex) {
                                        nextIteratorIndex++
                                        iterator.moveNext()
                                    }
                                    iterator.removeNext()
                                } else {
                                    while (operation.index < nextIteratorIndex - 1u) {
                                        nextIteratorIndex--
                                        iterator.movePrevious()
                                    }
                                    iterator.removePrevious()
                                    nextIteratorIndex--
                                }
                            }
                        }
                        testEqualityByIteration(mutableList, expected)
                        testEqualityByStringRepresentation(mutableList, expected)
                    }
                }
            }
        }
        
        if (builder is MutableFixedCapacityIterableListBuilder) test("test iterator mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
                val mutableList = builder.buildByGenerator(arbData.initialList.size.toUInt(), 20u, defaultEquality()) { arbData.initialList[it.toInt()] }
                var nextIteratorIndex = 5u
                val iterator = mutableList.iteratorFrom(nextIteratorIndex)
                repeat(arbData.numberOfOperations) {
                    val operation = arbData.operations[it.toInt()]
                    val expected = arbData.results[it.toInt()]
                    withClue("at iteration $it with current state $mutableList, operation $operation, and expected result $expected") {
                        when (operation) {
                            is MutableListOperation.AddAt<UInt> -> {
                                if (operation.index >= nextIteratorIndex) {
                                    while (operation.index > nextIteratorIndex) {
                                        nextIteratorIndex++
                                        iterator.moveNext()
                                    }
                                    iterator.addNext(operation.element)
                                } else {
                                    while (operation.index < nextIteratorIndex) {
                                        nextIteratorIndex--
                                        iterator.movePrevious()
                                    }
                                    iterator.addPrevious(operation.element)
                                    nextIteratorIndex++
                                }
                            }
                            is MutableListOperation.RemoveAt -> {
                                if (operation.index >= nextIteratorIndex) {
                                    while (operation.index > nextIteratorIndex) {
                                        nextIteratorIndex++
                                        iterator.moveNext()
                                    }
                                    iterator.removeNext()
                                } else {
                                    while (operation.index < nextIteratorIndex - 1u) {
                                        nextIteratorIndex--
                                        iterator.movePrevious()
                                    }
                                    iterator.removePrevious()
                                    nextIteratorIndex--
                                }
                            }
                        }
                        testEqualityByIteration(mutableList, expected)
                        testEqualityByStringRepresentation(mutableList, expected)
                    }
                }
            }
        }
    }
})