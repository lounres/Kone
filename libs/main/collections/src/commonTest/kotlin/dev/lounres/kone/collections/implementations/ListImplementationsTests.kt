/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.KoneSettableList
import dev.lounres.kone.collections.getAndMoveNext
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
    fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneList<E>
}

interface SettableIterableListBuilder : IterableListBuilder {
    override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneSettableList<E>
}

interface MutableFixedCapacityIterableListBuilder : SettableIterableListBuilder {
    fun <E> build(capacity: UInt): KoneMutableList<E>
    override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E>
    fun <E> buildByGenerator(size: UInt, capacity: UInt, generator: (UInt) -> E): KoneMutableList<E>
}

interface MutableIterableListBuilder : SettableIterableListBuilder {
    fun <E> build(): KoneMutableList<E>
    override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E>
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
                override fun <E> build(capacity: UInt): KoneMutableList<E> =
                    KoneFixedCapacityArrayList(capacity)
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneFixedCapacityArrayList(size, generator)
                override fun <E> buildByGenerator(size: UInt, capacity: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneFixedCapacityArrayList(size, capacity, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneFixedCapacityLinkedArrayList"
        override val builder: MutableFixedCapacityIterableListBuilder =
            object : MutableFixedCapacityIterableListBuilder {
                override fun <E> build(capacity: UInt): KoneMutableList<E> =
                    KoneFixedCapacityLinkedArrayList(capacity)
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneFixedCapacityLinkedArrayList(size, generator)
                override fun <E> buildByGenerator(size: UInt, capacity: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneFixedCapacityLinkedArrayList(size, capacity, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneGrowableArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E> build(): KoneMutableList<E> =
                    KoneGrowableArrayList()
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneGrowableArrayList(size, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneGrowableLinkedArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E> build(): KoneMutableList<E> =
                    KoneGrowableLinkedArrayList()
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneGrowableLinkedArrayList(size, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneLinkedGCList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E> build(): KoneMutableList<E> =
                    KoneLinkedGCList()
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneLinkedGCList(size, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneResizableArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E> build(): KoneMutableList<E> =
                    KoneResizableArrayList()
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneResizableArrayList(size, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneResizableLinkedArrayList"
        override val builder: MutableIterableListBuilder =
            object : MutableIterableListBuilder {
                override fun <E> build(): KoneMutableList<E> =
                    KoneResizableLinkedArrayList()
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneMutableList<E> =
                    KoneResizableLinkedArrayList(size, generator)
            }
    },
    object : ListImplementationDescription {
        override val name = "KoneSettableArrayList"
        override val builder: SettableIterableListBuilder =
            object : SettableIterableListBuilder {
                override fun <E> buildByGenerator(size: UInt, generator: (UInt) -> E): KoneSettableList<E> =
                    KoneSettableArrayList(size, generator)
            }
    },
)

fun <E> testEqualityByIteration(list1: KoneList<E>, list2: List<E>) {
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

fun <E> testEqualityByStringRepresentation(list1: KoneList<E>, list2: List<E>) {
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
                    val list = builder.buildByGenerator(length.toUInt()) { input[it.toInt()] }
                    testEqualityByIteration(list, input)
                    testEqualityByStringRepresentation(list, input)
                }
            }
        }
        
        if (builder is MutableIterableListBuilder) test("test element-by-element extension") {
            checkAll(Exhaustive.ints(0..20)) { length ->
                checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                    val list = builder.build<UInt>()
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
                    val list = builder.build<UInt>(30u)
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
                val mutableList = builder.buildByGenerator(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
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
                val mutableList = builder.buildByGenerator(arbData.initialList.size.toUInt(), 20u) { arbData.initialList[it.toInt()] }
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
                val mutableList = builder.buildByGenerator<UInt>(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
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
                val mutableList = builder.buildByGenerator(arbData.initialList.size.toUInt(), 20u) { arbData.initialList[it.toInt()] }
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