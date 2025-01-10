/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.list.implementations.*
import dev.lounres.kone.collections.list.producers.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.producers.KoneListProducer
import dev.lounres.kone.collections.list.producers.KoneResizableMutableListProducer
import dev.lounres.kone.repeat
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.uInt
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.ints
import kotlin.random.nextUInt


interface KoneListValidator {
    fun validate(
        list: KoneList<Any>,
    )
    
    fun validateWithIterator(
        list: KoneList<Any>,
        iterator: KoneIterator<Any>,
    )
}

fun <Validator: KoneListValidator> Validator.shouldValidate(list: KoneList<Any>): Validator =
    apply { validate(list) }
fun <Validator: KoneListValidator> Validator.shouldValidate(list: KoneList<Any>, iterator: KoneIterator<Any>): Validator =
    apply { validateWithIterator(list, iterator) }

interface ListImplementationDescription {
    val name: String
    val listProducer: KoneListProducer
    val listValidator: KoneListValidator
}

val listImplementations = listOf<ListImplementationDescription>(
    KoneArrayFixedCapacityLinkedListDescription,
    KoneArrayFixedCapacityLinkedNoddedListDescription,
    KoneArrayFixedCapacityListDescription,
    KoneArrayFixedCapacityNoddedListDescription,
    KoneArrayGrowableLinkedListDescription,
    KoneArrayGrowableLinkedNoddedListDescription,
    KoneArrayGrowableListDescription,
    KoneArrayGrowableNoddedListDescription,
    KoneArrayResizableLinkedListDescription,
    KoneArrayResizableLinkedNoddedListDescription,
    KoneArrayResizableListDescription,
    KoneArrayResizableNoddedListDescription,
    KoneArraySettableListDescription,
    KoneArraySettableNoddedListDescription,
    KoneGCLinkedListDescription,
    KoneTwoThreeTreeListDescription,
)

fun <Element> testEqualityByIteration(list1: KoneList<Element>, list2: List<Element>) {
    withClue("Checking equality of the lists by iteration through them") {
        val listIterator = list1.iterator()
        for (i in 0u ..< list2.size.toUInt()) {
            if (!listIterator.hasNext()) fail("List iterator stopped before the length ended")
            withClue({ "Checking equality of elements at index $i" }) {
                listIterator.getNext() shouldBe list2[i.toInt()]
            }
            listIterator.moveNext()
        }
        if (listIterator.hasNext()) fail("List iterator has extra elements")
    }
}

fun <Element> testEqualityByStringRepresentation(list1: KoneList<Element>, list2: List<Element>) {
    withClue("Checking equality of the lists' string representations") {
        list1.toString() shouldBe list2.toString()
    }
}

fun <Element> testEquality(list1: KoneList<Element>, list2: List<Element>) {
    testEqualityByIteration(list1, list2)
    testEqualityByStringRepresentation(list1, list2)
}

sealed interface MutableListOperation<out Element> {
    data class SetAt<out Element>(val index: UInt, val element: Element): MutableListOperation<Element>
    data class AddAt<out Element>(val index: UInt, val element: Element): MutableListOperation<Element>
    data class RemoveAt(val index: UInt): MutableListOperation<Nothing>
}

data class MutableListOperationsWithResultsSeries<out Element>(
    val initialList: List<Element>,
    val numberOfOperations: UInt,
    val operations: List<MutableListOperation<Element>>,
    val results: List<List<Element>>,
)

val <Element> MutableListOperationsWithResultsSeries<Element>.lastResult: List<Element> get() = results.lastOrNull() ?: initialList

fun <Element> arbMutableListOperationsWithResultsSeries(
    arbElements: Arb<Element>,
    initialSize: UInt,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): Arb<MutableListOperationsWithResultsSeries<Element>> = arbitrary { source ->
    val initialList = List(initialSize.toInt()) { arbElements.bind() }
    val operations = mutableListOf<MutableListOperation<Element>>()
    val results = mutableListOf<List<Element>>()
    repeat(numberOfOperations) {
        val lastList = results.lastOrNull() ?: initialList
        when {
            lastList.isNotEmpty() && source.random.nextUInt(3u) == 0u -> {
                val newElement = arbElements.bind()
                val settingIndex = source.random.nextInt(lastList.size)
                operations.add(MutableListOperation.SetAt(settingIndex.toUInt(), newElement))
                results.add(lastList.subList(0, settingIndex) + newElement + lastList.subList(settingIndex + 1, lastList.size))
            }
            (capacity == null || capacity.toInt() > lastList.size) && (lastList.isEmpty() || source.random.nextBoolean()) -> {
                val newElement = arbElements.bind()
                val insertionIndex = source.random.nextInt(lastList.size + 1)
                operations.add(MutableListOperation.AddAt(insertionIndex.toUInt(), newElement))
                results.add(lastList.subList(0, insertionIndex) + newElement + lastList.subList(insertionIndex, lastList.size))
            }
            else -> {
                val deletionIndex = source.random.nextInt(lastList.size)
                operations.add(MutableListOperation.RemoveAt(deletionIndex.toUInt()))
                results.add(lastList.subList(0, deletionIndex) + lastList.subList(deletionIndex + 1, lastList.size))
            }
        }
    }
    MutableListOperationsWithResultsSeries(
        initialList,
        numberOfOperations,
        operations,
        results,
    )
}

fun <Element> Exhaustive.Companion.allMutableListOperationsWithResultsSeriesWithLengthsNoMoreThan(
    arbElements: Arb<Element>,
    initialSize: UInt,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): Exhaustive<MutableListOperationsWithResultsSeries<Element>> = buildList<MutableListOperationsWithResultsSeries<Element>> {
    val newElementsIterator = arbElements.samples(RandomSource.default()).iterator()
    fun element() = newElementsIterator.next().value
    
    val seriesToProcess = ArrayDeque<MutableListOperationsWithResultsSeries<Element>>()
    seriesToProcess.add(
        MutableListOperationsWithResultsSeries(
            initialList = List(initialSize.toInt()) { element() },
            numberOfOperations = 0u,
            operations = listOf(),
            results = listOf(),
        )
    )
    
    while (seriesToProcess.isNotEmpty()) {
        val series = seriesToProcess.removeFirst()
        add(series)
        val lastResult = series.lastResult
        
        if (series.numberOfOperations < numberOfOperations) {
            if (lastResult.isNotEmpty()) {
                for (index in lastResult.indices) {
                    val newElement = element()
                    seriesToProcess.add(
                        MutableListOperationsWithResultsSeries(
                            initialList = series.initialList,
                            numberOfOperations = series.numberOfOperations + 1u,
                            operations = series.operations + MutableListOperation.SetAt(index.toUInt(), newElement),
                            results = series.results + listOf(lastResult.subList(0, index) + newElement + lastResult.subList(index + 1, lastResult.size)),
                        )
                    )
                }
            }
            
            if (lastResult.isNotEmpty()) {
                for (index in lastResult.indices) {
                    seriesToProcess.add(
                        MutableListOperationsWithResultsSeries(
                            initialList = series.initialList,
                            numberOfOperations = series.numberOfOperations + 1u,
                            operations = series.operations + MutableListOperation.RemoveAt(index.toUInt()),
                            results = series.results + listOf(lastResult.subList(0, index) + lastResult.subList(index + 1, lastResult.size)),
                        )
                    )
                }
            }
            
            if (capacity == null || lastResult.size < capacity.toInt()) {
                for (index in 0..lastResult.size) {
                    val newElement = element()
                    seriesToProcess.add(
                        MutableListOperationsWithResultsSeries(
                            initialList = series.initialList,
                            numberOfOperations = series.numberOfOperations + 1u,
                            operations = series.operations + MutableListOperation.AddAt(index.toUInt(), newElement),
                            results = series.results + listOf(lastResult.subList(0, index) + newElement + lastResult.subList(index, lastResult.size)),
                        )
                    )
                }
            }
        }
    }
}.exhaustive()

class ListImplementationsTests : FunSpec({
    for (impl in listImplementations) context(impl.name) {
        val producer = impl.listProducer
        
        test("test generative construction") {
            checkAll(Exhaustive.ints(0 .. 20)) { length ->
                checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                    val list = producer.produceBy(length.toUInt()) { input[it.toInt()] }
                    impl.listValidator.shouldValidate(list)
                    testEquality(list, input)
                }
            }
        }
        
        if (producer is KoneResizableMutableListProducer) {
            test("test element-by-element extension") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>()
                        impl.listValidator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) {
                            list.add(input[index.toInt()])
                            impl.listValidator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element reduction") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                        impl.listValidator.shouldValidate(list)
                        testEquality(list, input)
                        for (index in length - 1 downTo 0) {
                            list.removeAt(index.toUInt())
                            impl.listValidator.shouldValidate(list)
                            testEquality(list, input.subList(0, index))
                        }
                    }
                }
            }
        }
        
        if (producer is KoneGrowableMutableListProducer) {
            test("test element-by-element extension") {
                checkAll(Exhaustive.ints(0..20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>()
                        impl.listValidator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) {
                            list.add(input[index.toInt()])
                            impl.listValidator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element extension with ensured capacity") {
                checkAll(Exhaustive.ints(0..20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>(length.toUInt())
                        impl.listValidator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) {
                            list.add(input[index.toInt()])
                            impl.listValidator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element reduction") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                        impl.listValidator.shouldValidate(list)
                        testEquality(list, input)
                        for (index in length - 1 downTo 0) {
                            list.removeAt(index.toUInt())
                            impl.listValidator.shouldValidate(list)
                            testEquality(list, input.subList(0, index))
                        }
                    }
                }
            }
        }
        
        if (producer is KoneFixedCapacityMutableListProducer) {
            test("test element-by-element extension") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>(30u)
                        impl.listValidator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) withClue("at iteration $index") {
                            list.add(input[index.toInt()])
                            impl.listValidator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element reduction") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                        impl.listValidator.shouldValidate(list)
                        testEquality(list, input)
                        for (index in length - 1 downTo 0) {
                            list.removeAt(index.toUInt())
                            impl.listValidator.shouldValidate(list)
                            testEquality(list, input.subList(0, index))
                        }
                    }
                }
            }
        }
        
        fun <Element: Any> testKoneMutableListMutabilityOperationsOn(
            arbData: MutableListOperationsWithResultsSeries<Element>,
            mutableList: KoneMutableList<Element>,
            validator: KoneListValidator,
        ) {
            repeat(arbData.numberOfOperations) {
                val operation = arbData.operations[it.toInt()]
                val expected = arbData.results[it.toInt()]
                withClue({ "at iteration $it with current state $mutableList, operation $operation, and expected result $expected" }) {
                    when (operation) {
                        is MutableListOperation.SetAt<Element> -> mutableList[operation.index] = operation.element
                        is MutableListOperation.AddAt<Element> -> mutableList.addAt(operation.index, operation.element)
                        is MutableListOperation.RemoveAt -> mutableList.removeAt(operation.index)
                    }
                    validator.shouldValidate(mutableList)
                    testEquality(mutableList, expected)
                }
            }
        }
        
        if (producer is KoneResizableMutableListProducer) test("test random series of mutability operations") {
            checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListMutabilityOperationsOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    validator = impl.listValidator,
                )
            }
        }
        
        if (producer is KoneGrowableMutableListProducer) {
            test("test random series of mutability operations") {
                checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListMutabilityOperationsOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        validator = impl.listValidator,
                    )
                }
            }
            test("test random series of mutability operations with ensured capacity") {
                checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListMutabilityOperationsOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        validator = impl.listValidator,
                    )
                }
            }
        }
        
        if (producer is KoneFixedCapacityMutableListProducer) test("test random series of mutability operations") {
            checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListMutabilityOperationsOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    validator = impl.listValidator,
                )
            }
        }
        
        fun <Element: Any> testKoneMutableListIteratorOn(
            arbData: MutableListOperationsWithResultsSeries<Element>,
            mutableList: KoneMutableList<Element>,
            nextIteratorIndex: UInt,
            validator: KoneListValidator,
        ) {
            var nextIteratorIndex = nextIteratorIndex
            val iterator = mutableList.iteratorFrom(nextIteratorIndex)
            repeat(arbData.numberOfOperations) {
                val operation = arbData.operations[it.toInt()]
                val expected = arbData.results[it.toInt()]
                withClue("at iteration $it with current state $mutableList, current next iterator index $nextIteratorIndex, operation $operation, and expected result $expected") {
                    when (operation) {
                        is MutableListOperation.SetAt<Element> -> {
                            if (operation.index >= nextIteratorIndex) {
                                while (operation.index > nextIteratorIndex) {
                                    iterator.hasNext().shouldBeTrue()
                                    iterator.nextIndex() shouldBe nextIteratorIndex
                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
                                    iterator.moveNext()
                                    nextIteratorIndex++
                                }
                                iterator.hasNext().shouldBeTrue()
                                iterator.nextIndex() shouldBe operation.index
                                iterator.setNext(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                iterator.hasNext().shouldBeTrue()
                                iterator.nextIndex() shouldBe operation.index
                            } else {
                                while (operation.index < nextIteratorIndex - 1u) {
                                    iterator.hasPrevious().shouldBeTrue()
                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
                                    iterator.movePrevious()
                                    nextIteratorIndex--
                                }
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.previousIndex() shouldBe operation.index
                                iterator.setPrevious(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.previousIndex() shouldBe operation.index
                            }
                        }
                        is MutableListOperation.AddAt<Element> -> {
                            if (operation.index >= nextIteratorIndex) {
                                while (operation.index > nextIteratorIndex) {
                                    iterator.hasNext().shouldBeTrue()
                                    iterator.nextIndex() shouldBe nextIteratorIndex
                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
                                    iterator.moveNext()
                                    nextIteratorIndex++
                                }
                                iterator.addNext(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                iterator.hasNext().shouldBeTrue()
                                iterator.getNext() shouldBe operation.element
                                iterator.nextIndex() shouldBe nextIteratorIndex
                            } else {
                                while (operation.index < nextIteratorIndex) {
                                    iterator.hasPrevious().shouldBeTrue()
                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
                                    iterator.movePrevious()
                                    nextIteratorIndex--
                                }
                                iterator.addPrevious(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                nextIteratorIndex++
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.getPrevious() shouldBe operation.element
                                iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                            }
                        }
                        is MutableListOperation.RemoveAt -> {
                            if (operation.index >= nextIteratorIndex) {
                                while (operation.index > nextIteratorIndex) {
                                    iterator.hasNext().shouldBeTrue()
                                    iterator.nextIndex() shouldBe nextIteratorIndex
                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
                                    iterator.moveNext()
                                    nextIteratorIndex++
                                }
                                iterator.hasNext().shouldBeTrue()
                                iterator.nextIndex() shouldBe operation.index
                                iterator.removeNext()
                                validator.shouldValidate(mutableList, iterator)
                            } else {
                                while (operation.index < nextIteratorIndex - 1u) {
                                    iterator.hasPrevious().shouldBeTrue()
                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
                                    iterator.movePrevious()
                                    nextIteratorIndex--
                                }
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.previousIndex() shouldBe operation.index
                                iterator.removePrevious()
                                validator.shouldValidate(mutableList, iterator)
                                nextIteratorIndex--
                            }
                        }
                    }
                    testEquality(mutableList, expected)
                }
            }
        }
        
        if (producer is KoneResizableMutableListProducer) test("test random series of iterator mutability operations") {
            checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy<UInt>(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListIteratorOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    nextIteratorIndex = 5u,
                    validator = impl.listValidator,
                )
            }
        }
        
        if (producer is KoneGrowableMutableListProducer) {
            test("test random series of iterator mutability operations") {
                checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy<UInt>(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListIteratorOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        nextIteratorIndex = 5u,
                        validator = impl.listValidator,
                    )
                }
            }
            test("test random series of iterator mutability operations with ensured capacity") {
                checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy<UInt>(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListIteratorOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        nextIteratorIndex = 5u,
                        validator = impl.listValidator,
                    )
                }
            }
        }
        
        if (producer is KoneFixedCapacityMutableListProducer) test("test random series of iterator mutability operations") {
            checkAll(arbMutableListOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListIteratorOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    nextIteratorIndex = 5u,
                    validator = impl.listValidator,
                )
            }
        }
    }
})