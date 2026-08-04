/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.collections.list.implementations.*


interface KoneListValidator {
    context(assertionScope: AssertionScope)
    fun validate(
        list: KoneList<Any>,
    )
    
    context(assertionScope: AssertionScope)
    fun validateWithIterator(
        list: KoneList<Any>,
        iterator: KoneIterator<Any>,
    )
}

@IgnorableReturnValue
context(assertionScope: AssertionScope)
fun <Validator: KoneListValidator> Expect<Validator>.toValidate(list: KoneList<Any>) { exposeValue().validate(list) }
@IgnorableReturnValue
context(assertionScope: AssertionScope)
fun <Validator: KoneListValidator> Expect<Validator>.toValidate(list: KoneList<Any>, iterator: KoneIterator<Any>) { exposeValue().validateWithIterator(list, iterator) }

interface ListDisposabilityTest {
    context(assertionScope: AssertionScope)
    fun <Element: Any> test(list: KoneList<Element>) {}
}

interface ListImplementationDescription {
    val name: String
    val listProducer: KoneListProducer
    val listValidator: KoneListValidator
    val listDisposabilityTest: ListDisposabilityTest? get() = null
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
    KoneGCLinkedSizedListDescription,
    KoneTwoThreeTreeListDescription,
)

context(assertionScope: AssertionScope)
fun <Element> testEqualityIndexAccess(list1: KoneList<Element>, list2: List<Element>) {
    withClue("Checking equality of the lists by accessing element by index") {
        if (list1.size != list2.size.toUInt()) fail("the lists have different sizes")
        for (index in 0u ..< list1.size)
            withClue({ "Checking equality of elements at index $index" }) {
                Expect of list1[index] toBe list2[index.toInt()]
            }
    }
}

context(assertionScope: AssertionScope)
fun <Element> testEqualityByIteration(list1: KoneList<Element>, list2: List<Element>) {
    withClue("Checking equality of the lists by iteration through them") {
        val listIterator = list1.iterator()
        for (i in 0u ..< list2.size.toUInt()) {
            if (!listIterator.hasNext()) fail("List iterator stopped before the length ended")
            withClue({ "Checking equality of elements at index $i" }) {
                Expect of listIterator.getNext() toBe list2[i.toInt()]
            }
            listIterator.moveNext()
        }
        if (listIterator.hasNext()) fail("List iterator has extra elements")
    }
}

context(assertionScope: AssertionScope)
fun <Element> testEqualityByStringRepresentation(list1: KoneList<Element>, list2: List<Element>) {
    withClue("Checking equality of the lists' string representations") {
        Expect of list1.toString() toBe list2.toString()
    }
}

context(assertionScope: AssertionScope)
fun <Element> testEquality(list1: KoneList<Element>, list2: List<Element>) {
    testEqualityIndexAccess(list1, list2)
    testEqualityByIteration(list1, list2)
    testEqualityByStringRepresentation(list1, list2)
}

sealed interface SettableListGettingSettingOperation<out Element> {
    data class Set<out Element>(val index: UInt, val element: Element) : SettableListGettingSettingOperation<Element>
    data class Get<out Element>(val index: UInt, val expected: Element) : SettableListGettingSettingOperation<Element>
}

sealed interface MutableListExtensionReductionOperation<out Element> {
//    data class Set<out Element>(val index: UInt, val element: Element) : MutableListExtensionReductionOperation<Element>
    data class AddAt<out Element>(val index: UInt, val element: Element) : MutableListExtensionReductionOperation<Element>
    data class RemoveAt(val index: UInt) : MutableListExtensionReductionOperation<Nothing>
}

sealed interface MutableListOperation<out Element> {
    data class Set<out Element>(val index: UInt, val element: Element) : MutableListOperation<Element>
    data class Add<out Element>(val element: Element) : MutableListOperation<Element>
    data class AddAt<out Element>(val element: Element, val index: UInt) : MutableListOperation<Element>
    data class AddSeveral<out Element>(val elements: List<Element>) : MutableListOperation<Element>
    data class AddSeveralAt<out Element>(val elements: List<Element>, val index: UInt) : MutableListOperation<Element>
    data class RemoveAt(val index: UInt) : MutableListOperation<Nothing>
    data class RemoveAllThatIndexed(val indices: List<UInt>) : MutableListOperation<Nothing>
    data object RemoveAll : MutableListOperation<Nothing>
}

sealed interface MutableNoddedListOperation<out Element> {
    data class AddNode<out Element>(val element: Element) : MutableNoddedListOperation<Element>
    data class AddNodeAt<out Element>(val element: Element, val index: UInt) : MutableNoddedListOperation<Element>
}

data class SettableListGettingSettingOperationsWithResultsSeries<out Element>(
    val initialList: List<Element>,
    val numberOfOperations: UInt,
    val operations: List<SettableListGettingSettingOperation<Element>>,
    val results: List<List<Element>>,
)

data class MutableListExtensionReductionOperationsWithResultsSeries<out Element>(
    val initialList: List<Element>,
    val numberOfOperations: UInt,
    val operations: List<MutableListExtensionReductionOperation<Element>>,
    val results: List<List<Element>>,
)

data class MutableListOperationWithResult<out Element>(
    val operation: MutableListOperation<Element>,
    val result: List<Element>,
)

data class MutableNoddedListOperationWithResult<out Element>(
    val operation: MutableNoddedListOperation<Element>,
    val result: List<Element>,
)

val <Element> MutableListExtensionReductionOperationsWithResultsSeries<Element>.lastResult: List<Element> get() = results.lastOrNull() ?: initialList

//fun <Element> arbSettableListGettingSettingOperationsWithResultsSeries(
//    arbElements: Arb<Element>,
//    initialSize: UInt,
//    numberOfOperations: UInt,
//): Arb<SettableListGettingSettingOperationsWithResultsSeries<Element>> = arbitrary { source ->
//    require(initialSize != 0u) { "Getting and setting operations are impossible on empty list" }
//    val initialList = List(initialSize.toInt()) { arbElements.bind() }
//    val operations = mutableListOf<SettableListGettingSettingOperation<Element>>()
//    val results = mutableListOf<List<Element>>()
//    repeat(numberOfOperations) {
//        val lastList = results.lastOrNull() ?: initialList
//        when {
//            source.random.nextBoolean() -> {
//                val newElement = arbElements.bind()
//                val settingIndex = source.random.nextInt(lastList.size)
//                operations.add(SettableListGettingSettingOperation.Set(settingIndex.toUInt(), newElement))
//                results.add(lastList.subList(0, settingIndex) + newElement + lastList.subList(settingIndex + 1, lastList.size))
//            }
//            else -> {
//                val gettingIndex = source.random.nextInt(lastList.size)
//                operations.add(SettableListGettingSettingOperation.Get(gettingIndex.toUInt(), lastList[gettingIndex]))
//                results.add(lastList)
//            }
//        }
//    }
//    SettableListGettingSettingOperationsWithResultsSeries(
//        initialList,
//        numberOfOperations,
//        operations,
//        results,
//    )
//}
//
//fun <Element> arbMutableListExtensionReductionOperationsWithResultsSeries(
//    arbElements: Arb<Element>,
//    initialSize: UInt,
//    capacity: UInt? = null,
//    numberOfOperations: UInt,
//): Arb<MutableListExtensionReductionOperationsWithResultsSeries<Element>> = arbitrary { source ->
//    val initialList = List(initialSize.toInt()) { arbElements.bind() }
//    val operations = mutableListOf<MutableListExtensionReductionOperation<Element>>()
//    val results = mutableListOf<List<Element>>()
//    repeat(numberOfOperations) {
//        val lastList = results.lastOrNull() ?: initialList
//        when {
////            lastList.isNotEmpty() && source.random.nextUInt(3u) == 0u -> {
////                val newElement = arbElements.bind()
////                val settingIndex = source.random.nextInt(lastList.size)
////                operations.add(MutableListExtensionReductionOperation.Set(settingIndex.toUInt(), newElement))
////                results.add(lastList.subList(0, settingIndex) + newElement + lastList.subList(settingIndex + 1, lastList.size))
////            }
//            (capacity == null || capacity.toInt() > lastList.size) && (lastList.isEmpty() || source.random.nextBoolean()) -> {
//                val newElement = arbElements.bind()
//                val insertionIndex = source.random.nextInt(lastList.size + 1)
//                operations.add(MutableListExtensionReductionOperation.AddAt(insertionIndex.toUInt(), newElement))
//                results.add(lastList.subList(0, insertionIndex) + newElement + lastList.subList(insertionIndex, lastList.size))
//            }
//            else -> {
//                val deletionIndex = source.random.nextInt(lastList.size)
//                operations.add(MutableListExtensionReductionOperation.RemoveAt(deletionIndex.toUInt()))
//                results.add(lastList.subList(0, deletionIndex) + lastList.subList(deletionIndex + 1, lastList.size))
//            }
//        }
//    }
//    MutableListExtensionReductionOperationsWithResultsSeries(
//        initialList,
//        numberOfOperations,
//        operations,
//        results,
//    )
//}
//
//fun <Element> Exhaustive.Companion.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(
//    arbElements: Arb<Element>,
//    initialSize: UInt,
//    capacity: UInt? = null,
//    numberOfOperations: UInt,
//): Exhaustive<MutableListExtensionReductionOperationsWithResultsSeries<Element>> = buildList<MutableListExtensionReductionOperationsWithResultsSeries<Element>> {
//    val newElementsIterator = arbElements.samples().iterator()
//    fun element() = newElementsIterator.next().value
//
//    val seriesToProcess = ArrayDeque<MutableListExtensionReductionOperationsWithResultsSeries<Element>>()
//    seriesToProcess.add(
//        MutableListExtensionReductionOperationsWithResultsSeries(
//            initialList = List(initialSize.toInt()) { element() },
//            numberOfOperations = 0u,
//            operations = listOf(),
//            results = listOf(),
//        )
//    )
//
//    while (seriesToProcess.isNotEmpty()) {
//        val series = seriesToProcess.removeFirst()
//        add(series)
//        val lastResult = series.lastResult
//
//        if (series.numberOfOperations < numberOfOperations) {
////            for (index in lastResult.indices) {
////                val newElement = element()
////                seriesToProcess.add(
////                    MutableListOperationsWithResultsSeries(
////                        initialList = series.initialList,
////                        numberOfOperations = series.numberOfOperations + 1u,
////                        operations = series.operations + MutableListExtensionReductionOperation.Set(index.toUInt(), newElement),
////                        results = series.results + listOf(lastResult.subList(0, index) + newElement + lastResult.subList(index + 1, lastResult.size)),
////                    )
////                )
////            }
//
//            for (index in lastResult.indices) {
//                seriesToProcess.add(
//                    MutableListExtensionReductionOperationsWithResultsSeries(
//                        initialList = series.initialList,
//                        numberOfOperations = series.numberOfOperations + 1u,
//                        operations = series.operations + MutableListExtensionReductionOperation.RemoveAt(index.toUInt()),
//                        results = series.results + listOf(lastResult.subList(0, index) + lastResult.subList(index + 1, lastResult.size)),
//                    )
//                )
//            }
//
//            if (capacity == null || lastResult.size < capacity.toInt()) {
//                for (index in 0..lastResult.size) {
//                    val newElement = element()
//                    seriesToProcess.add(
//                        MutableListExtensionReductionOperationsWithResultsSeries(
//                            initialList = series.initialList,
//                            numberOfOperations = series.numberOfOperations + 1u,
//                            operations = series.operations + MutableListExtensionReductionOperation.AddAt(index.toUInt(), newElement),
//                            results = series.results + listOf(lastResult.subList(0, index) + newElement + lastResult.subList(index, lastResult.size)),
//                        )
//                    )
//                }
//            }
//        }
//    }
//}.exhaustive()
//
//fun <Element> Exhaustive.Companion.allMutableListOperationWithResult(
//    arbElements: Arb<Element>,
//    initialList: List<Element>,
//    severalElementsAdditionLimit: Int,
//) : Exhaustive<MutableListOperationWithResult<Element>> = buildList {
//    val newElementsIterator = arbElements.samples().iterator()
//    fun element() = newElementsIterator.next().value
//
//    // Set
//    for (index in initialList.indices) {
//        val newElement = element()
//        add(
//            MutableListOperationWithResult(
//                operation = MutableListOperation.Set(index.toUInt(), newElement),
//                result = initialList.subList(0, index) + newElement + initialList.subList(index + 1, initialList.size),
//            )
//        )
//    }
//
//    // TODO: Добавить проверку вместимости
//    // Add
//    scope {
//        val newElement = element()
//        add(
//            MutableListOperationWithResult(
//                operation = MutableListOperation.Add(newElement),
//                result = initialList + newElement,
//            )
//        )
//    }
//
//    // TODO: Добавить проверку вместимости
//    // AddAt
//    for (index in 0 .. initialList.size) {
//        val newElement = element()
//        add(
//            MutableListOperationWithResult(
//                operation = MutableListOperation.AddAt(newElement, index.toUInt()),
//                result = initialList.subList(0, index) + newElement + initialList.subList(index, initialList.size),
//            )
//        )
//    }
//
//    // TODO: Добавить проверку вместимости
//    // AddSeveral
//    for (extraSize in 0 ..< severalElementsAdditionLimit) {
//        val newElements = List(extraSize) { element() }
//        add(
//            MutableListOperationWithResult(
//                operation = MutableListOperation.AddSeveral(newElements),
//                result = initialList + newElements,
//            )
//        )
//    }
//
//    // TODO: Добавить проверку вместимости
//    // AddSeveralAt
//    for (index in 0 .. initialList.size) for (extraSize in 0 ..< severalElementsAdditionLimit) {
//        val newElements = List(extraSize) { element() }
//        add(
//            MutableListOperationWithResult(
//                operation = MutableListOperation.AddSeveralAt(newElements, index.toUInt()),
//                result = initialList.subList(0, index) + newElements + initialList.subList(index, initialList.size),
//            )
//        )
//    }
//
//    // RemoveAt
//    for (index in initialList.indices) {
//        add(
//            MutableListOperationWithResult(
//                operation = MutableListOperation.RemoveAt(index.toUInt()),
//                result = initialList.subList(0, index) + initialList.subList(index + 1, initialList.size),
//            )
//        )
//    }
//
//    // RemoveAllThatIndexed
//    add(
//        MutableListOperationWithResult(
//            operation = MutableListOperation.RemoveAllThatIndexed(
//                initialList.indices.filter { it % 2 == 0 }.map { it.toUInt() }
//            ),
//            result = initialList.filterIndexed { index, _ -> index % 2 != 0 }
//        )
//    )
//
//    // RemoveAll
//    add(
//        MutableListOperationWithResult(
//            operation = MutableListOperation.RemoveAll,
//            result = emptyList(),
//        )
//    )
//}.exhaustive()
//
//fun <Element> Exhaustive.Companion.allMutableNoddedListOperationWithResult(
//    arbElements: Arb<Element>,
//    initialList: List<Element>,
//) : Exhaustive<MutableNoddedListOperationWithResult<Element>> = buildList {
//    val newElementsIterator = arbElements.samples().iterator()
//    fun element() = newElementsIterator.next().value
//
//    // TODO: Добавить проверку вместимости
//    // AddNode
//    scope {
//        val newElement = element()
//        add(
//            MutableNoddedListOperationWithResult(
//                operation = MutableNoddedListOperation.AddNode(newElement),
//                result = initialList + newElement,
//            )
//        )
//    }
//
//    // TODO: Добавить проверку вместимости
//    // AddNodeAt
//    for (index in 0 .. initialList.size) {
//        val newElement = element()
//        add(
//            MutableNoddedListOperationWithResult(
//                operation = MutableNoddedListOperation.AddNodeAt(newElement, index.toUInt()),
//                result = initialList.subList(0, index) + newElement + initialList.subList(index, initialList.size),
//            )
//        )
//    }
//}.exhaustive()
//
//// TODO: Написать тесты на:
////   1. Ноды
////   2. `dispose`.
////   3. `hashCode`, `equals`.
////   4. `getNextNode`, `getPreviousNode`.
//val ListImplementationsTests by testSuite {
//    for (impl in listImplementations) testSuite(impl.name) {
//        val producer = impl.listProducer
//
//        testSuite("test generative construction") {
//            for (length in 0 .. 20) testSuite("length $length") {
//                checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                    val list = producer.produceBy(length.toUInt()) { input[it.toInt()] }
//                    impl.listValidator.shouldValidate(list)
//                    testEquality(list, input)
//                }
//            }
//        }
//
//        if (producer is KoneSettableListProducer)
//            test("test random series of getting and setting operations") {
//                checkAll(arbSettableListGettingSettingOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val settableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    repeat(arbData.numberOfOperations) {
//                        val operation = arbData.operations[it.toInt()]
//                        val expected = arbData.results[it.toInt()]
//                        withClue({ "at iteration $it with current state $settableList, operation $operation, and expected result $expected" }) {
//                            when (operation) {
//                                is SettableListGettingSettingOperation.Get<UInt> -> settableList[operation.index] shouldBe operation.expected
//                                is SettableListGettingSettingOperation.Set<UInt> -> settableList[operation.index] = operation.element
//                            }
//                            impl.listValidator.shouldValidate(settableList)
//                            testEquality(settableList, expected)
//                        }
//                    }
//                }
//            }
//
//        if (producer is KoneSettableListProducer)
//            test("test random series of iterator getting and setting operations") {
//                checkAll(arbSettableListGettingSettingOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val settableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    var nextIteratorIndex = 5u
//                    val iterator = settableList.iteratorFrom(nextIteratorIndex)
//                    repeat(arbData.numberOfOperations) {
//                        val operation = arbData.operations[it.toInt()]
//                        val expected = arbData.results[it.toInt()]
//                        withClue("at iteration $it with current state $settableList, current next iterator index $nextIteratorIndex, operation $operation, and expected result $expected") {
//                            when (operation) {
//                                is SettableListGettingSettingOperation.Set<UInt> -> {
//                                    if (operation.index >= nextIteratorIndex) {
//                                        while (operation.index > nextIteratorIndex) {
//                                            iterator.hasNext().shouldBeTrue()
//                                            iterator.nextIndex() shouldBe nextIteratorIndex
//                                            iterator.getNext() shouldBe settableList[nextIteratorIndex]
//                                            iterator.moveNext()
//                                            nextIteratorIndex++
//                                        }
//                                        iterator.hasNext().shouldBeTrue()
//                                        iterator.nextIndex() shouldBe operation.index
//                                        iterator.setNext(operation.element)
//                                        impl.listValidator.shouldValidate(settableList, iterator)
//                                        iterator.hasNext().shouldBeTrue()
//                                        iterator.nextIndex() shouldBe operation.index
//                                    } else {
//                                        while (operation.index < nextIteratorIndex - 1u) {
//                                            iterator.hasPrevious().shouldBeTrue()
//                                            iterator.previousIndex() shouldBe nextIteratorIndex - 1u
//                                            iterator.getPrevious() shouldBe settableList[nextIteratorIndex - 1u]
//                                            iterator.movePrevious()
//                                            nextIteratorIndex--
//                                        }
//                                        iterator.hasPrevious().shouldBeTrue()
//                                        iterator.previousIndex() shouldBe operation.index
//                                        iterator.setPrevious(operation.element)
//                                        impl.listValidator.shouldValidate(settableList, iterator)
//                                        iterator.hasPrevious().shouldBeTrue()
//                                        iterator.previousIndex() shouldBe operation.index
//                                    }
//                                }
//                                is SettableListGettingSettingOperation.Get<UInt> -> {
//                                    if (operation.index >= nextIteratorIndex) {
//                                        while (operation.index > nextIteratorIndex) {
//                                            iterator.hasNext().shouldBeTrue()
//                                            iterator.nextIndex() shouldBe nextIteratorIndex
//                                            iterator.getNext() shouldBe settableList[nextIteratorIndex]
//                                            iterator.moveNext()
//                                            nextIteratorIndex++
//                                        }
//                                        iterator.hasNext().shouldBeTrue()
//                                        iterator.nextIndex() shouldBe operation.index
//                                        iterator.getNext() shouldBe operation.expected
//                                        impl.listValidator.shouldValidate(settableList, iterator)
//                                        iterator.hasNext().shouldBeTrue()
//                                        iterator.nextIndex() shouldBe operation.index
//                                    } else {
//                                        while (operation.index < nextIteratorIndex - 1u) {
//                                            iterator.hasPrevious().shouldBeTrue()
//                                            iterator.previousIndex() shouldBe nextIteratorIndex - 1u
//                                            iterator.getPrevious() shouldBe settableList[nextIteratorIndex - 1u]
//                                            iterator.movePrevious()
//                                            nextIteratorIndex--
//                                        }
//                                        iterator.hasPrevious().shouldBeTrue()
//                                        iterator.previousIndex() shouldBe operation.index
//                                        iterator.getPrevious() shouldBe operation.expected
//                                        impl.listValidator.shouldValidate(settableList, iterator)
//                                        iterator.hasPrevious().shouldBeTrue()
//                                        iterator.previousIndex() shouldBe operation.index
//                                    }
//                                }
//                            }
//                            testEquality(settableList, expected)
//                        }
//                    }
//                }
//            }
//
//        if (producer is KoneResizableMutableListProducer) {
//            testSuite("test element-by-element extension") {
//                withData(nameFn = { "length $it" }, 0 .. 20) { length ->
//                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                        val list = producer.produce<UInt>()
//                        impl.listValidator.shouldValidate(list)
//                        testEquality(list, emptyList())
//                        for (index in 0 ..< length) {
//                            list.add(input[index])
//                            impl.listValidator.shouldValidate(list)
//                            testEquality(list, input.subList(0, index + 1))
//                        }
//                    }
//                }
//            }
//            testSuite("test element-by-element reduction") {
//                withData(nameFn = { "length $it" }, 0 .. 20) { length ->
//                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
//                        impl.listValidator.shouldValidate(list)
//                        testEquality(list, input)
//                        for (index in length - 1 downTo 0) {
//                            list.removeAt(index.toUInt())
//                            impl.listValidator.shouldValidate(list)
//                            testEquality(list, input.subList(0, index))
//                        }
//                    }
//                }
//            }
//        }
//
//        if (producer is KoneGrowableMutableListProducer) {
//            testSuite("test element-by-element extension") {
//                withData(nameFn = { "length $it" }, 0..20) { length ->
//                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                        val list = producer.produce<UInt>()
//                        impl.listValidator.shouldValidate(list)
//                        testEquality(list, emptyList())
//                        for (index in 0 ..< length) {
//                            list.add(input[index])
//                            impl.listValidator.shouldValidate(list)
//                            testEquality(list, input.subList(0, index + 1))
//                        }
//                    }
//                }
//            }
//            testSuite("test element-by-element extension with ensured capacity") {
//                withData(nameFn = { "length $it" }, 0..20) { length ->
//                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                        val list = producer.produce<UInt>(length.toUInt())
//                        impl.listValidator.shouldValidate(list)
//                        testEquality(list, emptyList())
//                        for (index in 0 ..< length) {
//                            list.add(input[index])
//                            impl.listValidator.shouldValidate(list)
//                            testEquality(list, input.subList(0, index + 1))
//                        }
//                    }
//                }
//            }
//            testSuite("test element-by-element reduction") {
//                withData(nameFn = { "length $it" }, 0 .. 20) { length ->
//                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
//                        impl.listValidator.shouldValidate(list)
//                        testEquality(list, input)
//                        for (index in length - 1 downTo 0) {
//                            list.removeAt(index.toUInt())
//                            impl.listValidator.shouldValidate(list)
//                            testEquality(list, input.subList(0, index))
//                        }
//                    }
//                }
//            }
//        }
//
//        if (producer is KoneFixedCapacityMutableListProducer) {
//            testSuite("test element-by-element extension") {
//                withData(nameFn = { "length $it" }, 0 .. 20) { length ->
//                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                        val list = producer.produce<UInt>(30u)
//                        impl.listValidator.shouldValidate(list)
//                        testEquality(list, emptyList())
//                        for (index in 0 ..< length) withClue("at iteration $index") {
//                            list.add(input[index])
//                            impl.listValidator.shouldValidate(list)
//                            testEquality(list, input.subList(0, index + 1))
//                        }
//                    }
//                }
//            }
//            testSuite("test element-by-element reduction") {
//                withData(nameFn = { "length $it" }, 0 .. 20) { length ->
//                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
//                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
//                        impl.listValidator.shouldValidate(list)
//                        testEquality(list, input)
//                        for (index in length - 1 downTo 0) {
//                            list.removeAt(index.toUInt())
//                            impl.listValidator.shouldValidate(list)
//                            testEquality(list, input.subList(0, index))
//                        }
//                    }
//                }
//            }
//        }
//
//        fun <Element: Any> testKoneMutableListExtensionReductionOperationsOn(
//            arbData: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
//            mutableList: KoneMutableList<Element>,
//            validator: KoneListValidator,
//        ) {
//            repeat(arbData.numberOfOperations) {
//                val operation = arbData.operations[it.toInt()]
//                val expected = arbData.results[it.toInt()]
//                withClue({ "at iteration $it with current state $mutableList, operation $operation, and expected result $expected" }) {
//                    when (operation) {
////                        is MutableListExtensionReductionOperation.Set<Element> -> mutableList[operation.index] = operation.element
//                        is MutableListExtensionReductionOperation.AddAt<Element> -> mutableList.addAt(operation.index, operation.element)
//                        is MutableListExtensionReductionOperation.RemoveAt -> mutableList.removeAt(operation.index)
//                    }
//                    validator.shouldValidate(mutableList)
//                    testEquality(mutableList, expected)
//                }
//            }
//        }
//
//        if (producer is KoneResizableMutableListProducer)
//            test("test random series of extension and reduction operations") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//
//        if (producer is KoneGrowableMutableListProducer) {
//            test("test random series of extension and reduction operations") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//            test("test random series of extension and reduction operations with ensured capacity") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//        }
//
//        if (producer is KoneFixedCapacityMutableListProducer)
//            test("test random series of extension and reduction operations") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//
//        fun <Element: Any> testKoneMutableListMutabilityOperationsOn(
//            previousSteps: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
//            operation: MutableListOperation<Element>,
//            result: List<Element>,
//            mutableList: KoneMutableList<Element>,
//            validator: KoneListValidator,
//        ) {
//            repeat(previousSteps.numberOfOperations) {
//                val step = previousSteps.operations[it.toInt()]
//                when (step) {
////                    is MutableListExtensionReductionOperation.Set<Element> -> mutableList[step.index] = step.element
//                    is MutableListExtensionReductionOperation.AddAt<Element> -> mutableList.addAt(step.index, step.element)
//                    is MutableListExtensionReductionOperation.RemoveAt -> mutableList.removeAt(step.index)
//                }
//            }
//            validator.shouldValidate(mutableList)
//            when (operation) {
//                is MutableListOperation.Set<Element> -> mutableList[operation.index] = operation.element
//                is MutableListOperation.Add<Element> -> mutableList.add(operation.element)
//                is MutableListOperation.AddAt<Element> -> mutableList.addAt(operation.index, operation.element)
//                is MutableListOperation.AddSeveral<Element> -> mutableList.addSeveral(operation.elements.size.toUInt()) { operation.elements[it.toInt()] }
//                is MutableListOperation.AddSeveralAt<Element> -> mutableList.addSeveralAt(operation.index, operation.elements.size.toUInt()) { operation.elements[it.toInt()] }
//                is MutableListOperation.RemoveAt -> mutableList.removeAt(operation.index)
//                is MutableListOperation.RemoveAllThatIndexed -> mutableList.removeAllThatIndexed { index, _ -> index in operation.indices }
//                MutableListOperation.RemoveAll -> mutableList.removeAll()
//            }
//            validator.shouldValidate(mutableList)
//            testEquality(mutableList, result)
//        }
//
//        if (producer is KoneResizableMutableListProducer)
//            test("test of mutability operations after series of changes") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult, severalElementsAdditionLimit = 5)) { (operation, result) ->
//                        val mutableList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableList = mutableList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//
//        if (producer is KoneGrowableMutableListProducer) {
//            test("test of mutability operations after series of changes") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult, severalElementsAdditionLimit = 5)) { (operation, result) ->
//                        val mutableList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableList = mutableList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//            test("test of mutability operations after series of changes with ensured capacity") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult, severalElementsAdditionLimit = 5)) { (operation, result) ->
//                        val mutableList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableList = mutableList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//        }
//
//        if (producer is KoneFixedCapacityMutableListProducer)
//            test("test of mutability operations after series of changes") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult, severalElementsAdditionLimit = 5)) { (operation, result) ->
//                        val mutableList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableList = mutableList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//
//        fun <Element: Any> testKoneMutableNoddedListMutabilityOperationsOn(
//            previousSteps: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
//            operation: MutableNoddedListOperation<Element>,
//            result: List<Element>,
//            mutableNoddedList: KoneMutableNoddedList<Element>,
//            validator: KoneListValidator,
//        ) {
//            repeat(previousSteps.numberOfOperations) {
//                val step = previousSteps.operations[it.toInt()]
//                when (step) {
////                    is MutableListExtensionReductionOperation.Set<Element> -> mutableList[step.index] = step.element
//                    is MutableListExtensionReductionOperation.AddAt<Element> -> mutableNoddedList.addAt(step.index, step.element)
//                    is MutableListExtensionReductionOperation.RemoveAt -> mutableNoddedList.removeAt(step.index)
//                }
//            }
//            validator.shouldValidate(mutableNoddedList)
//            when (operation) {
//                is MutableNoddedListOperation.AddNode<Element> -> {
//                    mutableNoddedList.addNode(operation.element)
//                    // TODO: Добавить проверку получаемой ноды
//                }
//                is MutableNoddedListOperation.AddNodeAt<Element> -> {
//                    mutableNoddedList.addNodeAt(operation.index, operation.element)
//                    // TODO: Добавить проверку получаемой ноды
//                }
//            }
//            validator.shouldValidate(mutableNoddedList)
//            testEquality(mutableNoddedList, result)
//        }
//
//        if (producer is KoneResizableMutableNoddedListProducer)
//            test("test of nodded mutability operations after series of changes") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableNoddedListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult)) { (operation, result) ->
//                        val mutableNoddedList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableNoddedListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableNoddedList = mutableNoddedList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//
//        if (producer is KoneGrowableMutableNoddedListProducer) {
//            test("test of nodded mutability operations after series of changes") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableNoddedListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult)) { (operation, result) ->
//                        val mutableNoddedList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableNoddedListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableNoddedList = mutableNoddedList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//            test("test of nodded mutability operations after series of changes with ensured capacity") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableNoddedListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult)) { (operation, result) ->
//                        val mutableNoddedList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableNoddedListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableNoddedList = mutableNoddedList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//        }
//
//        if (producer is KoneFixedCapacityMutableNoddedListProducer)
//            test("test of nodded mutability operations after series of changes") {
//                checkAll(Exhaustive.allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 3u)) { previousSteps ->
//                    checkAll(Exhaustive.allMutableNoddedListOperationWithResult(arbElements = Arb.uInt(), initialList = previousSteps.lastResult)) { (operation, result) ->
//                        val mutableNoddedList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
//                        testKoneMutableNoddedListMutabilityOperationsOn(
//                            previousSteps = previousSteps,
//                            operation = operation,
//                            result = result,
//                            mutableNoddedList = mutableNoddedList,
//                            validator = impl.listValidator,
//                        )
//                    }
//                }
//            }
//
//        fun <Element: Any> testKoneMutableListIteratorExtensionReductionOperationsOn(
//            arbData: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
//            mutableList: KoneMutableList<Element>,
//            nextIteratorIndex: UInt,
//            validator: KoneListValidator,
//        ) {
//            var nextIteratorIndex = nextIteratorIndex
//            val iterator = mutableList.iteratorFrom(nextIteratorIndex)
//            repeat(arbData.numberOfOperations) {
//                val operation = arbData.operations[it.toInt()]
//                val expected = arbData.results[it.toInt()]
//                withClue("at iteration $it with current state $mutableList, current next iterator index $nextIteratorIndex, operation $operation, and expected result $expected") {
//                    when (operation) {
////                        is MutableListExtensionReductionOperation.Set<Element> -> {
////                            if (operation.index >= nextIteratorIndex) {
////                                while (operation.index > nextIteratorIndex) {
////                                    iterator.hasNext().shouldBeTrue()
////                                    iterator.nextIndex() shouldBe nextIteratorIndex
////                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
////                                    iterator.moveNext()
////                                    nextIteratorIndex++
////                                }
////                                iterator.hasNext().shouldBeTrue()
////                                iterator.nextIndex() shouldBe operation.index
////                                iterator.setNext(operation.element)
////                                validator.shouldValidate(mutableList, iterator)
////                                iterator.hasNext().shouldBeTrue()
////                                iterator.nextIndex() shouldBe operation.index
////                            } else {
////                                while (operation.index < nextIteratorIndex - 1u) {
////                                    iterator.hasPrevious().shouldBeTrue()
////                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
////                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
////                                    iterator.movePrevious()
////                                    nextIteratorIndex--
////                                }
////                                iterator.hasPrevious().shouldBeTrue()
////                                iterator.previousIndex() shouldBe operation.index
////                                iterator.setPrevious(operation.element)
////                                validator.shouldValidate(mutableList, iterator)
////                                iterator.hasPrevious().shouldBeTrue()
////                                iterator.previousIndex() shouldBe operation.index
////                            }
////                        }
//                        is MutableListExtensionReductionOperation.AddAt<Element> -> {
//                            if (operation.index >= nextIteratorIndex) {
//                                while (operation.index > nextIteratorIndex) {
//                                    iterator.hasNext().shouldBeTrue()
//                                    iterator.nextIndex() shouldBe nextIteratorIndex
//                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
//                                    iterator.moveNext()
//                                    nextIteratorIndex++
//                                }
//                                iterator.addNext(operation.element)
//                                validator.shouldValidate(mutableList, iterator)
//                                iterator.hasNext().shouldBeTrue()
//                                iterator.getNext() shouldBe operation.element
//                                iterator.nextIndex() shouldBe nextIteratorIndex
//                            } else {
//                                while (operation.index < nextIteratorIndex) {
//                                    iterator.hasPrevious().shouldBeTrue()
//                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
//                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
//                                    iterator.movePrevious()
//                                    nextIteratorIndex--
//                                }
//                                iterator.addPrevious(operation.element)
//                                validator.shouldValidate(mutableList, iterator)
//                                nextIteratorIndex++
//                                iterator.hasPrevious().shouldBeTrue()
//                                iterator.getPrevious() shouldBe operation.element
//                                iterator.previousIndex() shouldBe nextIteratorIndex - 1u
//                            }
//                        }
//                        is MutableListExtensionReductionOperation.RemoveAt -> {
//                            if (operation.index >= nextIteratorIndex) {
//                                while (operation.index > nextIteratorIndex) {
//                                    iterator.hasNext().shouldBeTrue()
//                                    iterator.nextIndex() shouldBe nextIteratorIndex
//                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
//                                    iterator.moveNext()
//                                    nextIteratorIndex++
//                                }
//                                iterator.hasNext().shouldBeTrue()
//                                iterator.nextIndex() shouldBe operation.index
//                                iterator.removeNext()
//                                validator.shouldValidate(mutableList, iterator)
//                            } else {
//                                while (operation.index < nextIteratorIndex - 1u) {
//                                    iterator.hasPrevious().shouldBeTrue()
//                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
//                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
//                                    iterator.movePrevious()
//                                    nextIteratorIndex--
//                                }
//                                iterator.hasPrevious().shouldBeTrue()
//                                iterator.previousIndex() shouldBe operation.index
//                                iterator.removePrevious()
//                                validator.shouldValidate(mutableList, iterator)
//                                nextIteratorIndex--
//                            }
//                        }
//                    }
//                    testEquality(mutableList, expected)
//                }
//            }
//        }
//
//        if (producer is KoneResizableMutableListProducer)
//            test("test random series of iterator extension and reduction operations") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy<UInt>(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListIteratorExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        nextIteratorIndex = 5u,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//
//        if (producer is KoneGrowableMutableListProducer) {
//            test("test random series of iterator extension and reduction operations") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy<UInt>(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListIteratorExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        nextIteratorIndex = 5u,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//            test("test random series of iterator extension and reduction operations with ensured capacity") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy<UInt>(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListIteratorExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        nextIteratorIndex = 5u,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//        }
//
//        if (producer is KoneFixedCapacityMutableListProducer)
//            test("test random series of iterator extension and reduction operations") {
//                checkAll(arbMutableListExtensionReductionOperationsWithResultsSeries(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
//                    val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
//                    testKoneMutableListIteratorExtensionReductionOperationsOn(
//                        arbData = arbData,
//                        mutableList = mutableList,
//                        nextIteratorIndex = 5u,
//                        validator = impl.listValidator,
//                    )
//                }
//            }
//    }
//}