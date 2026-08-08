/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.DelicateBulkElementsRemoverAPI
import dev.lounres.kone.collections.DelicateSeveralElementsInserterAPI
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneSettableListProducer
import dev.lounres.kone.collections.list.implementations.*
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlin.random.Random
import kotlin.random.nextUInt


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

val mutableListImplementations = listOf<ListImplementationDescription>(
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
    KoneGCLinkedSizedListDescription,
    KoneTwoThreeTreeListDescription,
)

val settableListImplementations = listOf<ListImplementationDescription>(
    KoneArraySettableListDescription,
    KoneArraySettableNoddedListDescription,
)

val listImplementations = buildList<ListImplementationDescription> {
    addAll(mutableListImplementations)
    addAll(settableListImplementations)
}

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
    data class StartAddingSeveralAt<Element>(val elements: List<Element>, val index: UInt) : MutableListOperation<Element>
    data class RemoveAt(val index: UInt) : MutableListOperation<Nothing>
    data class StartBulkyRemoving(val number: UInt, val indices: List<UInt>) : MutableListOperation<Nothing>
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

fun <Element> Random.randomSettableListGettingSettingOperationsWithResultsSeries(
    randomElement: () -> Element,
    initialSize: UInt,
    numberOfOperations: UInt,
): SettableListGettingSettingOperationsWithResultsSeries<Element> {
    require(initialSize != 0u) { "Getting and setting operations are impossible on empty list" }
    val initialList = List(initialSize.toInt()) { randomElement() }
    val operations = mutableListOf<SettableListGettingSettingOperation<Element>>()
    val results = mutableListOf<List<Element>>()
    repeat(numberOfOperations) {
        val lastList = results.lastOrNull() ?: initialList
        when {
            nextBoolean() -> {
                val newElement = randomElement()
                val settingIndex = nextInt(lastList.size)
                operations.add(SettableListGettingSettingOperation.Set(settingIndex.toUInt(), newElement))
                results.add(lastList.subList(0, settingIndex) + newElement + lastList.subList(settingIndex + 1, lastList.size))
            }
            else -> {
                val gettingIndex = nextInt(lastList.size)
                operations.add(SettableListGettingSettingOperation.Get(gettingIndex.toUInt(), lastList[gettingIndex]))
                results.add(lastList)
            }
        }
    }
    return SettableListGettingSettingOperationsWithResultsSeries(
        initialList,
        numberOfOperations,
        operations,
        results,
    )
}

fun <Element> Random.randomMutableListExtensionReductionOperationsWithResultsSeries(
    randomElement: () -> Element,
    initialSize: UInt,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): MutableListExtensionReductionOperationsWithResultsSeries<Element> {
    val initialList = List(initialSize.toInt()) { randomElement() }
    val operations = mutableListOf<MutableListExtensionReductionOperation<Element>>()
    val results = mutableListOf<List<Element>>()
    repeat(numberOfOperations) {
        val lastList = results.lastOrNull() ?: initialList
        when {
//            lastList.isNotEmpty() && nextUInt(3u) == 0u -> {
//                val newElement = randomElement()
//                val settingIndex = nextInt(lastList.size)
//                operations.add(MutableListExtensionReductionOperation.Set(settingIndex.toUInt(), newElement))
//                results.add(lastList.subList(0, settingIndex) + newElement + lastList.subList(settingIndex + 1, lastList.size))
//            }
            (capacity == null || capacity.toInt() > lastList.size) && (lastList.isEmpty() || nextBoolean()) -> {
                val newElement = randomElement()
                val insertionIndex = nextInt(lastList.size + 1)
                operations.add(MutableListExtensionReductionOperation.AddAt(insertionIndex.toUInt(), newElement))
                results.add(lastList.subList(0, insertionIndex) + newElement + lastList.subList(insertionIndex, lastList.size))
            }
            else -> {
                val deletionIndex = nextInt(lastList.size)
                operations.add(MutableListExtensionReductionOperation.RemoveAt(deletionIndex.toUInt()))
                results.add(lastList.subList(0, deletionIndex) + lastList.subList(deletionIndex + 1, lastList.size))
            }
        }
    }
    return MutableListExtensionReductionOperationsWithResultsSeries(
        initialList,
        numberOfOperations,
        operations,
        results,
    )
}

fun <Element> allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(
    randomElement: () -> Element,
    initialSize: UInt,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): Sequence<MutableListExtensionReductionOperationsWithResultsSeries<Element>> = sequence {
    data class GenerationLevel(
        val series: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
        val nextSeries: Iterator<MutableListExtensionReductionOperationsWithResultsSeries<Element>>,
    )
    
    val initialSeries = MutableListExtensionReductionOperationsWithResultsSeries(
        initialList = List(initialSize.toInt()) { randomElement() },
        numberOfOperations = 0u,
        operations = listOf(),
        results = listOf(),
    )
    
    yield(initialSeries)
    
    val generationStack = ArrayDeque<GenerationLevel>()
    generationStack.add(
        GenerationLevel(
            series = initialSeries,
            nextSeries = emptyList<MutableListExtensionReductionOperationsWithResultsSeries<Element>>().iterator()
        )
    )
    
    fun MutableListExtensionReductionOperationsWithResultsSeries<Element>.nextLevel(): Iterator<MutableListExtensionReductionOperationsWithResultsSeries<Element>> = buildList {
        val series = this@nextLevel
        
//        for (index in lastResult.indices) {
//            val newElement = randomElement()
//            seriesToProcess.add(
//                MutableListExtensionReductionOperationsWithResultsSeries(
//                    initialList = series.initialList,
//                    numberOfOperations = series.numberOfOperations + 1u,
//                    operations = series.operations + MutableListExtensionReductionOperation.Set(index.toUInt(), newElement),
//                    results = series.results + listOf(lastResult.subList(0, index) + newElement + lastResult.subList(index + 1, lastResult.size)),
//                )
//            )
//        }
        
        for (index in lastResult.indices) {
            add(
                MutableListExtensionReductionOperationsWithResultsSeries(
                    initialList = series.initialList,
                    numberOfOperations = series.numberOfOperations + 1u,
                    operations = series.operations + MutableListExtensionReductionOperation.RemoveAt(index.toUInt()),
                    results = series.results + listOf(lastResult.subList(0, index) + lastResult.subList(index + 1, lastResult.size)),
                )
            )
        }
        
        if (capacity == null || lastResult.size < capacity.toInt()) {
            for (index in 0..lastResult.size) {
                val newElement = randomElement()
                add(
                    MutableListExtensionReductionOperationsWithResultsSeries(
                        initialList = series.initialList,
                        numberOfOperations = series.numberOfOperations + 1u,
                        operations = series.operations + MutableListExtensionReductionOperation.AddAt(index.toUInt(), newElement),
                        results = series.results + listOf(lastResult.subList(0, index) + newElement + lastResult.subList(index, lastResult.size)),
                    )
                )
            }
        }
    }.iterator()
    
    while (true) {
        scope {
            if (generationStack.size.toUInt() == numberOfOperations + 1u) return@scope
            val last = generationStack.last()
            val nextLevelIterator = last.series.nextLevel()
            if (!nextLevelIterator.hasNext()) return@scope
            val nextLevelSeries = nextLevelIterator.next()
            yield(nextLevelSeries)
            generationStack.addLast(
                GenerationLevel(
                    series = nextLevelSeries,
                    nextSeries = nextLevelIterator,
                )
            )
            continue
        }
        while (generationStack.isNotEmpty()) {
            if (generationStack.last().nextSeries.hasNext()) break
            generationStack.removeLast()
        }
        if (generationStack.isEmpty()) break
        val oldLastLevel = generationStack.removeLast()
        generationStack.addLast(
            GenerationLevel(
                series = oldLastLevel.nextSeries.next(),
                nextSeries = oldLastLevel.nextSeries,
            )
        )
    }
}

fun <Element> allMutableListOperationWithResult(
    randomElement: () -> Element,
    initialList: List<Element>,
    capacity: Int? = null,
    severalElementsAdditionLimit: Int,
) : Sequence<MutableListOperationWithResult<Element>> = sequence {

    // Set
    for (index in initialList.indices) {
        val newElement = randomElement()
        yield(
            MutableListOperationWithResult(
                operation = MutableListOperation.Set(index.toUInt(), newElement),
                result = initialList.subList(0, index) + newElement + initialList.subList(index + 1, initialList.size),
            )
        )
    }

    // Add
    if (capacity == null || initialList.size < capacity) {
        val newElement = randomElement()
        yield(
            MutableListOperationWithResult(
                operation = MutableListOperation.Add(newElement),
                result = initialList + newElement,
            )
        )
    }

    // AddAt
    if (capacity == null || initialList.size < capacity) {
        for (index in 0 .. initialList.size) {
            val newElement = randomElement()
            yield(
                MutableListOperationWithResult(
                    operation = MutableListOperation.AddAt(newElement, index.toUInt()),
                    result = initialList.subList(0, index) + newElement + initialList.subList(index, initialList.size),
                )
            )
        }
    }

    // StartAddingSeveralAt
    scope {
        val newElementsNumberLimit =
            if (capacity == null) severalElementsAdditionLimit
            else minOf(severalElementsAdditionLimit, capacity - initialList.size)
        
        for (extraSize in 0 .. newElementsNumberLimit) for (index in 0 .. initialList.size) {
            val newElements = List(extraSize) { randomElement() }
            yield(
                MutableListOperationWithResult(
                    operation = MutableListOperation.StartAddingSeveralAt(newElements, index.toUInt()),
                    result = initialList.subList(0, index) + newElements + initialList.subList(index, initialList.size),
                )
            )
        }
    }

    // RemoveAt
    for (index in initialList.indices) {
        yield(
            MutableListOperationWithResult(
                operation = MutableListOperation.RemoveAt(index.toUInt()),
                result = initialList.subList(0, index) + initialList.subList(index + 1, initialList.size),
            )
        )
    }

    // RemoveAllThatIndexed
    for (number in 0 .. initialList.size) {
        yield(
            MutableListOperationWithResult(
                operation = MutableListOperation.StartBulkyRemoving(
                    number = number.toUInt(),
                    indices = initialList.indices.take(number).filter { it % 2 == 0 }.map { it.toUInt() }
                ),
                result = initialList.filterIndexed { index, _ -> index >= number || index % 2 != 0 }
            )
        )
    }

    // RemoveAll
    yield(
        MutableListOperationWithResult(
            operation = MutableListOperation.RemoveAll,
            result = emptyList(),
        )
    )
}

fun <Element> allMutableNoddedListOperationWithResult(
    randomElement: () -> Element,
    initialList: List<Element>,
    capacity: Int? = null,
) : Sequence<MutableNoddedListOperationWithResult<Element>> = sequence {
    // AddNode
    if (capacity == null || initialList.size < capacity)  {
        val newElement = randomElement()
        yield(
            MutableNoddedListOperationWithResult(
                operation = MutableNoddedListOperation.AddNode(newElement),
                result = initialList + newElement,
            )
        )
    }

    // AddNodeAt
    if (capacity == null || initialList.size < capacity) {
        for (index in 0 .. initialList.size) {
            val newElement = randomElement()
            yield(
                MutableNoddedListOperationWithResult(
                    operation = MutableNoddedListOperation.AddNodeAt(newElement, index.toUInt()),
                    result = initialList.subList(0, index) + newElement + initialList.subList(index, initialList.size),
                )
            )
        }
    }
}

// TODO: Написать тесты на:
//   1. Ноды
//   2. `dispose`.
//   3. `hashCode`, `equals`.
//   4. `getNextNode`, `getPreviousNode`.
@OptIn(DelicateSeveralElementsInserterAPI::class, DelicateBulkElementsRemoverAPI::class)
val ListImplementationsTests by testSuite {
    for (impl in listImplementations) testSuite(impl.name) {
        val producer = impl.listProducer

        testSuite("test generative construction") {
            for (length in 0 .. 20) test("length $length") {
                AssertionScope.softly {
                    repeat(10u) {
                        val input = List(length) { Random.nextUInt() }
                        withClue({ "input = $input" }) {
                            val list = producer.produceBy(length.toUInt()) { input[it.toInt()] }
                            impl.listValidator.validate(list)
                            testEquality(list, input)
                        }
                    }
                }
            }
        }

        if (producer is KoneSettableListProducer)
            test("test random series of getting and setting operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomSettableListGettingSettingOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 100u)
                        withClue({
                            """
                                Data:
                                  initialList: ${data.initialList}
                                  numberOfOperations: ${data.numberOfOperations}
                                  operations: ${data.operations}
                                  results: ${data.results}
                                
                            """.trimIndent()
                        }) {
                            val settableList = producer.produceBy(data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                            repeat(data.numberOfOperations) {
                                val operation = data.operations[it.toInt()]
                                val expected = data.results[it.toInt()]
                                withClue({ "at iteration $it with current state $settableList, operation $operation, and expected result $expected" }) {
                                    when (operation) {
                                        is SettableListGettingSettingOperation.Get<UInt> -> Expect of settableList[operation.index] toBe operation.expected
                                        is SettableListGettingSettingOperation.Set<UInt> -> settableList[operation.index] = operation.element
                                    }
                                    impl.listValidator.validate(settableList)
                                    testEquality(settableList, expected)
                                }
                            }
                        }
                    }
                }
            }

        if (producer is KoneSettableListProducer)
            test("test random series of iterator getting and setting operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomSettableListGettingSettingOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 100u)
                        withClue({
                            """
                                Data:
                                  initialList: ${data.initialList}
                                  numberOfOperations: ${data.numberOfOperations}
                                  operations: ${data.operations}
                                  results: ${data.results}
                                
                            """.trimIndent()
                        }) {
                            val settableList = producer.produceBy(data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                            var nextIteratorIndex = 5u
                            val iterator = settableList.iteratorFrom(nextIteratorIndex)
                            repeat(data.numberOfOperations) {
                                val operation = data.operations[it.toInt()]
                                val expected = data.results[it.toInt()]
                                withClue("at iteration $it with current state $settableList, current next iterator index $nextIteratorIndex, operation $operation, and expected result $expected") {
                                    when (operation) {
                                        is SettableListGettingSettingOperation.Set<UInt> -> {
                                            if (operation.index >= nextIteratorIndex) {
                                                while (operation.index > nextIteratorIndex) {
                                                    Expect of iterator.hasNext() toBe true
                                                    Expect of iterator.nextIndex() toBe nextIteratorIndex
                                                    Expect of iterator.getNext() toBe settableList[nextIteratorIndex]
                                                    iterator.moveNext()
                                                    nextIteratorIndex++
                                                }
                                                Expect of iterator.hasNext() toBe true
                                                Expect of iterator.nextIndex() toBe operation.index
                                                iterator.setNext(operation.element)
                                                impl.listValidator.validateWithIterator(settableList, iterator)
                                                Expect of iterator.hasNext() toBe true
                                                Expect of iterator.nextIndex() toBe operation.index
                                            } else {
                                                while (operation.index < nextIteratorIndex - 1u) {
                                                    Expect of iterator.hasPrevious() toBe true
                                                    Expect of iterator.previousIndex() toBe nextIteratorIndex - 1u
                                                    Expect of iterator.getPrevious() toBe settableList[nextIteratorIndex - 1u]
                                                    iterator.movePrevious()
                                                    nextIteratorIndex--
                                                }
                                                Expect of iterator.hasPrevious() toBe true
                                                Expect of iterator.previousIndex() toBe operation.index
                                                iterator.setPrevious(operation.element)
                                                impl.listValidator.validateWithIterator(settableList, iterator)
                                                Expect of iterator.hasPrevious() toBe true
                                                Expect of iterator.previousIndex() toBe operation.index
                                            }
                                        }
                                        is SettableListGettingSettingOperation.Get<UInt> -> {
                                            if (operation.index >= nextIteratorIndex) {
                                                while (operation.index > nextIteratorIndex) {
                                                    Expect of iterator.hasNext() toBe true
                                                    Expect of iterator.nextIndex() toBe nextIteratorIndex
                                                    Expect of iterator.getNext() toBe settableList[nextIteratorIndex]
                                                    iterator.moveNext()
                                                    nextIteratorIndex++
                                                }
                                                Expect of iterator.hasNext() toBe true
                                                Expect of iterator.nextIndex() toBe operation.index
                                                Expect of iterator.getNext() toBe operation.expected
                                                impl.listValidator.validateWithIterator(settableList, iterator)
                                                Expect of iterator.hasNext() toBe true
                                                Expect of iterator.nextIndex() toBe operation.index
                                            } else {
                                                while (operation.index < nextIteratorIndex - 1u) {
                                                    Expect of iterator.hasPrevious() toBe true
                                                    Expect of iterator.previousIndex() toBe nextIteratorIndex - 1u
                                                    Expect of iterator.getPrevious() toBe settableList[nextIteratorIndex - 1u]
                                                    iterator.movePrevious()
                                                    nextIteratorIndex--
                                                }
                                                Expect of iterator.hasPrevious() toBe true
                                                Expect of iterator.previousIndex() toBe operation.index
                                                Expect of iterator.getPrevious() toBe operation.expected
                                                impl.listValidator.validateWithIterator(settableList, iterator)
                                                Expect of iterator.hasPrevious() toBe true
                                                Expect of iterator.previousIndex() toBe operation.index
                                            }
                                        }
                                    }
                                    testEquality(settableList, expected)
                                }
                            }
                        }
                    }
                }
            }

        if (producer is KoneResizableMutableListProducer) {
            testSuite("test element-by-element extension") {
                for (length in 0 .. 20) test("length $length") {
                    AssertionScope.softly {
                        repeat(10u) {
                            val input = List(length) { Random.nextUInt() }
                            withClue({ "input = $input" }) {
                                val list = producer.produce<UInt>()
                                impl.listValidator.validate(list)
                                testEquality(list, emptyList())
                                for (index in 0 ..< length) {
                                    list.add(input[index])
                                    impl.listValidator.validate(list)
                                    testEquality(list, input.subList(0, index + 1))
                                }
                            }
                        }
                    }
                }
            }
            testSuite("test element-by-element reduction") {
                for (length in 0 .. 20) test("length $length") {
                    AssertionScope.softly {
                        repeat(10u) {
                            val input = List(length) { Random.nextUInt() }
                            withClue({ "input = $input" }) {
                                val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                                impl.listValidator.validate(list)
                                testEquality(list, input)
                                for (index in length - 1 downTo 0) {
                                    list.removeAt(index.toUInt())
                                    impl.listValidator.validate(list)
                                    testEquality(list, input.subList(0, index))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (producer is KoneGrowableMutableListProducer) {
            testSuite("test element-by-element extension") {
                for (length in 0 .. 20) test("length $length") {
                    AssertionScope.softly {
                        repeat(10u) {
                            val input = List(length) { Random.nextUInt() }
                            withClue({ "input = $input" }) {
                                val list = producer.produce<UInt>()
                                impl.listValidator.validate(list)
                                testEquality(list, emptyList())
                                for (index in 0 ..< length) {
                                    list.add(input[index])
                                    impl.listValidator.validate(list)
                                    testEquality(list, input.subList(0, index + 1))
                                }
                            }
                        }
                    }
                }
            }
            testSuite("test element-by-element extension with ensured capacity") {
                for (length in 0 .. 20) test("length $length") {
                    AssertionScope.softly {
                        repeat(10u) {
                            val input = List(length) { Random.nextUInt() }
                            withClue({ "input = $input" }) {
                                val list = producer.produce<UInt>(length.toUInt())
                                impl.listValidator.validate(list)
                                testEquality(list, emptyList())
                                for (index in 0 ..< length) {
                                    list.add(input[index])
                                    impl.listValidator.validate(list)
                                    testEquality(list, input.subList(0, index + 1))
                                }
                            }
                        }
                    }
                }
            }
            testSuite("test element-by-element reduction") {
                for (length in 0 .. 20) test("length $length") {
                    AssertionScope.softly {
                        repeat(10u) {
                            val input = List(length) { Random.nextUInt() }
                            withClue({ "input = $input" }) {
                                val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                                impl.listValidator.validate(list)
                                testEquality(list, input)
                                for (index in length - 1 downTo 0) {
                                    list.removeAt(index.toUInt())
                                    impl.listValidator.validate(list)
                                    testEquality(list, input.subList(0, index))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (producer is KoneFixedCapacityMutableListProducer) {
            testSuite("test element-by-element extension") {
                for (length in 0 .. 20) test("length $length") {
                    AssertionScope.softly {
                        repeat(10u) {
                            val input = List(length) { Random.nextUInt() }
                            withClue({ "input = $input" }) {
                                val list = producer.produce<UInt>(30u)
                                impl.listValidator.validate(list)
                                testEquality(list, emptyList())
                                for (index in 0 ..< length) withClue("at iteration $index") {
                                    list.add(input[index])
                                    impl.listValidator.validate(list)
                                    testEquality(list, input.subList(0, index + 1))
                                }
                            }
                        }
                    }
                }
            }
            testSuite("test element-by-element reduction") {
                for (length in 0 .. 20) test("length $length") {
                    AssertionScope.softly {
                        repeat(10u) {
                            val input = List(length) { Random.nextUInt() }
                            withClue({ "input = $input" }) {
                                val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                                impl.listValidator.validate(list)
                                testEquality(list, input)
                                for (index in length - 1 downTo 0) {
                                    list.removeAt(index.toUInt())
                                    impl.listValidator.validate(list)
                                    testEquality(list, input.subList(0, index))
                                }
                            }
                        }
                    }
                }
            }
        }

        context(_: AssertionScope)
        fun <Element: Any> testKoneMutableListExtensionReductionOperationsOn(
            data: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
            mutableList: KoneMutableList<Element>,
            validator: KoneListValidator,
        ) {
            firmly {
                withClue({
                    """
                        Data:
                          initialList: ${data.initialList}
                          numberOfOperations: ${data.numberOfOperations}
                          operations: ${data.operations}
                          results: ${data.results}
                          
                    """.trimIndent()
                }) {
                    repeat(data.numberOfOperations) {
                        val operation = data.operations[it.toInt()]
                        val expected = data.results[it.toInt()]
                        withClue({ "at iteration $it with current state $mutableList, operation $operation, and expected result $expected" }) {
                            when (operation) {
    //                            is MutableListExtensionReductionOperation.Set<Element> -> mutableList[operation.index] = operation.element
                                is MutableListExtensionReductionOperation.AddAt<Element> -> mutableList.addAt(operation.index, operation.element)
                                is MutableListExtensionReductionOperation.RemoveAt -> mutableList.removeAt(operation.index)
                            }
                            validator.validate(mutableList)
                            testEquality(mutableList, expected)
                        }
                    }
                }
            }
        }

        if (producer is KoneResizableMutableListProducer)
            test("test random series of extension and reduction operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 100u)
                        val mutableList = producer.produceBy(data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListExtensionReductionOperationsOn(
                            data = data,
                            mutableList = mutableList,
                            validator = impl.listValidator,
                        )
                    }
                }
            }

        if (producer is KoneGrowableMutableListProducer) {
            test("test random series of extension and reduction operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 100u)
                        val mutableList = producer.produceBy(data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListExtensionReductionOperationsOn(
                            data = data,
                            mutableList = mutableList,
                            validator = impl.listValidator,
                        )
                    }
                }
            }
            test("test random series of extension and reduction operations with ensured capacity") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 100u)
                        val mutableList = producer.produceBy(20u, data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListExtensionReductionOperationsOn(
                            data = data,
                            mutableList = mutableList,
                            validator = impl.listValidator,
                        )
                    }
                }
            }
        }

        if (producer is KoneFixedCapacityMutableListProducer)
            test("test random series of extension and reduction operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, capacity = 20u, numberOfOperations = 100u)
                        val mutableList = producer.produceBy(20u, data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListExtensionReductionOperationsOn(
                            data = data,
                            mutableList = mutableList,
                            validator = impl.listValidator,
                        )
                    }
                }
            }
        
        context(_: AssertionScope)
        fun <Element: Any> testKoneMutableListMutabilityOperationsOn(
            previousSteps: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
            operation: MutableListOperation<Element>,
            result: List<Element>,
            mutableList: KoneMutableList<Element>,
            validator: KoneListValidator,
        ) {
            firmly {
                withClue({
                     """
                         Previous steps:
                           initialList: ${previousSteps.initialList}
                           numberOfOperations: ${previousSteps.numberOfOperations}
                           operations: ${previousSteps.operations}
                           results: ${previousSteps.results}
                         
                         operation: $operation
                         
                     """.trimIndent()
                }) {
                    repeat(previousSteps.numberOfOperations) {
                        val step = previousSteps.operations[it.toInt()]
                        when (step) {
        //                    is MutableListExtensionReductionOperation.Set<Element> -> mutableList[step.index] = step.element
                            is MutableListExtensionReductionOperation.AddAt<Element> -> mutableList.addAt(step.index, step.element)
                            is MutableListExtensionReductionOperation.RemoveAt -> mutableList.removeAt(step.index)
                        }
                    }
                    validator.validate(mutableList)
                    when (operation) {
                        is MutableListOperation.Set<Element> -> mutableList[operation.index] = operation.element
                        is MutableListOperation.Add<Element> -> mutableList.add(operation.element)
                        is MutableListOperation.AddAt<Element> -> mutableList.addAt(operation.index, operation.element)
                        is MutableListOperation.StartAddingSeveralAt<Element> -> {
                            val inserter = mutableList.startAddingSeveralAt(operation.index, operation.elements.size.toUInt())
                            for (element in operation.elements) inserter.insert(element)
                            inserter.close()
                        }
                        is MutableListOperation.RemoveAt -> mutableList.removeAt(operation.index)
                        is MutableListOperation.StartBulkyRemoving -> {
                            val remover = mutableList.startBulkyRemoving()
                            var currentIndexIndex = 0
                            repeat(operation.number) {
                                Expect of remover.hasNext() toBe true
                                if (remover.nextIndex() == operation.indices.getOrNull(currentIndexIndex)) {
                                    remover.removeNext()
                                    currentIndexIndex++
                                } else {
                                    remover.moveNext()
                                }
                            }
                            remover.close()
                        }
                        MutableListOperation.RemoveAll -> mutableList.removeAll()
                    }
                    validator.validate(mutableList)
                    testEquality(mutableList, result)
                }
            }
        }

        if (producer is KoneResizableMutableListProducer)
            test("test of mutability operations after series of changes") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult, severalElementsAdditionLimit = 5)) {
                            val mutableList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableList = mutableList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }

        if (producer is KoneGrowableMutableListProducer) {
            test("test of mutability operations after series of changes") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult, severalElementsAdditionLimit = 5)) {
                            val mutableList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableList = mutableList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }
            test("test of mutability operations after series of changes with ensured capacity") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult, severalElementsAdditionLimit = 5)) {
                            val mutableList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableList = mutableList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }
        }

        if (producer is KoneFixedCapacityMutableListProducer)
            test("test of mutability operations after series of changes") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult, capacity = 20, severalElementsAdditionLimit = 5)) {
                            val mutableList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableList = mutableList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }
        
        context(_: AssertionScope)
        fun <Element: Any> testKoneMutableNoddedListMutabilityOperationsOn(
            previousSteps: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
            operation: MutableNoddedListOperation<Element>,
            result: List<Element>,
            mutableNoddedList: KoneMutableNoddedList<Element>,
            validator: KoneListValidator,
        ) {
            firmly {
                withClue({
                    """
                        Previous steps:
                          initialList: ${previousSteps.initialList}
                          numberOfOperations: ${previousSteps.numberOfOperations}
                          operations: ${previousSteps.operations}
                          results: ${previousSteps.results}
                        
                        operation: $operation
                        
                    """.trimIndent()
                }) {
                    repeat(previousSteps.numberOfOperations) {
                        val step = previousSteps.operations[it.toInt()]
                        when (step) {
        //                    is MutableListExtensionReductionOperation.Set<Element> -> mutableList[step.index] = step.element
                            is MutableListExtensionReductionOperation.AddAt<Element> -> mutableNoddedList.addAt(step.index, step.element)
                            is MutableListExtensionReductionOperation.RemoveAt -> mutableNoddedList.removeAt(step.index)
                        }
                    }
                    validator.validate(mutableNoddedList)
                    when (operation) {
                        is MutableNoddedListOperation.AddNode<Element> -> {
                            mutableNoddedList.addNode(operation.element)
                            // TODO: Добавить проверку получаемой ноды
                        }
                        is MutableNoddedListOperation.AddNodeAt<Element> -> {
                            mutableNoddedList.addNodeAt(operation.index, operation.element)
                            // TODO: Добавить проверку получаемой ноды
                        }
                    }
                    validator.validate(mutableNoddedList)
                    testEquality(mutableNoddedList, result)
                }
            }
        }

        if (producer is KoneResizableMutableNoddedListProducer)
            test("test of nodded mutability operations after series of changes") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableNoddedListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult)) {
                            val mutableNoddedList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableNoddedListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableNoddedList = mutableNoddedList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }

        if (producer is KoneGrowableMutableNoddedListProducer) {
            test("test of nodded mutability operations after series of changes") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableNoddedListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult)) {
                            val mutableNoddedList = producer.produceBy(previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableNoddedListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableNoddedList = mutableNoddedList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }
            test("test of nodded mutability operations after series of changes with ensured capacity") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableNoddedListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult)) {
                            val mutableNoddedList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableNoddedListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableNoddedList = mutableNoddedList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }
        }

        if (producer is KoneFixedCapacityMutableNoddedListProducer)
            test("test of nodded mutability operations after series of changes") {
                AssertionScope {
                    for (previousSteps in allMutableListExtensionReductionOperationsWithResultsSeriesWithLengthsNoMoreThan(randomElement = { Random.nextUInt() }, initialSize = 10u, capacity = 20u, numberOfOperations = 3u)) {
                        for ((operation, result) in allMutableNoddedListOperationWithResult(randomElement = { Random.nextUInt() }, initialList = previousSteps.lastResult, capacity = 20)) {
                            val mutableNoddedList = producer.produceBy(20u, previousSteps.initialList.size.toUInt()) { previousSteps.initialList[it.toInt()] }
                            testKoneMutableNoddedListMutabilityOperationsOn(
                                previousSteps = previousSteps,
                                operation = operation,
                                result = result,
                                mutableNoddedList = mutableNoddedList,
                                validator = impl.listValidator,
                            )
                        }
                    }
                }
            }
        
        context(_: AssertionScope)
        fun <Element: Any> testKoneMutableListIteratorExtensionReductionOperationsOn(
            arbData: MutableListExtensionReductionOperationsWithResultsSeries<Element>,
            mutableList: KoneMutableList<Element>,
            nextIteratorIndex: UInt,
            validator: KoneListValidator,
        ) {
            firmly {
                var nextIteratorIndex = nextIteratorIndex
                val iterator = mutableList.iteratorFrom(nextIteratorIndex)
                repeat(arbData.numberOfOperations) {
                    val operation = arbData.operations[it.toInt()]
                    val expected = arbData.results[it.toInt()]
                    withClue("at iteration $it with current state $mutableList, current next iterator index $nextIteratorIndex, operation $operation, and expected result $expected") {
                        when (operation) {
    //                        is MutableListExtensionReductionOperation.Set<Element> -> {
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
    //                                iterator.setNext(operation.element)
    //                                validator.shouldValidate(mutableList, iterator)
    //                                iterator.hasNext().shouldBeTrue()
    //                                iterator.nextIndex() shouldBe operation.index
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
    //                                iterator.setPrevious(operation.element)
    //                                validator.shouldValidate(mutableList, iterator)
    //                                iterator.hasPrevious().shouldBeTrue()
    //                                iterator.previousIndex() shouldBe operation.index
    //                            }
    //                        }
                            is MutableListExtensionReductionOperation.AddAt<Element> -> {
                                if (operation.index >= nextIteratorIndex) {
                                    while (operation.index > nextIteratorIndex) {
                                        Expect of iterator.hasNext() toBe true
                                        Expect of iterator.nextIndex() toBe nextIteratorIndex
                                        Expect of iterator.getNext() toBe mutableList[nextIteratorIndex]
                                        iterator.moveNext()
                                        nextIteratorIndex++
                                    }
                                    iterator.addNext(operation.element)
                                    validator.validateWithIterator(mutableList, iterator)
                                    Expect of iterator.hasNext() toBe true
                                    Expect of iterator.getNext() toBe operation.element
                                    Expect of iterator.nextIndex() toBe nextIteratorIndex
                                } else {
                                    while (operation.index < nextIteratorIndex) {
                                        Expect of iterator.hasPrevious() toBe true
                                        Expect of iterator.previousIndex() toBe nextIteratorIndex - 1u
                                        Expect of iterator.getPrevious() toBe mutableList[nextIteratorIndex - 1u]
                                        iterator.movePrevious()
                                        nextIteratorIndex--
                                    }
                                    iterator.addPrevious(operation.element)
                                    validator.validateWithIterator(mutableList, iterator)
                                    nextIteratorIndex++
                                    Expect of iterator.hasPrevious() toBe true
                                    Expect of iterator.getPrevious() toBe operation.element
                                    Expect of iterator.previousIndex() toBe nextIteratorIndex - 1u
                                }
                            }
                            is MutableListExtensionReductionOperation.RemoveAt -> {
                                if (operation.index >= nextIteratorIndex) {
                                    while (operation.index > nextIteratorIndex) {
                                        Expect of iterator.hasNext() toBe true
                                        Expect of iterator.nextIndex() toBe nextIteratorIndex
                                        Expect of iterator.getNext() toBe mutableList[nextIteratorIndex]
                                        iterator.moveNext()
                                        nextIteratorIndex++
                                    }
                                    Expect of iterator.hasNext() toBe true
                                    Expect of iterator.nextIndex() toBe operation.index
                                    iterator.removeNext()
                                    validator.validateWithIterator(mutableList, iterator)
                                } else {
                                    while (operation.index < nextIteratorIndex - 1u) {
                                        Expect of iterator.hasPrevious() toBe true
                                        Expect of iterator.previousIndex() toBe nextIteratorIndex - 1u
                                        Expect of iterator.getPrevious() toBe mutableList[nextIteratorIndex - 1u]
                                        iterator.movePrevious()
                                        nextIteratorIndex--
                                    }
                                    Expect of iterator.hasPrevious() toBe true
                                    Expect of iterator.previousIndex() toBe operation.index
                                    iterator.removePrevious()
                                    validator.validateWithIterator(mutableList, iterator)
                                    nextIteratorIndex--
                                }
                            }
                        }
                        testEquality(mutableList, expected)
                    }
                }
            }
        }

        if (producer is KoneResizableMutableListProducer)
            test("test random series of iterator extension and reduction operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)
                        val mutableList = producer.produceBy<UInt>(data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListIteratorExtensionReductionOperationsOn(
                            arbData = data,
                            mutableList = mutableList,
                            nextIteratorIndex = 5u,
                            validator = impl.listValidator,
                        )
                    }
                }
            }

        if (producer is KoneGrowableMutableListProducer) {
            test("test random series of iterator extension and reduction operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)
                        val mutableList = producer.produceBy<UInt>(data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListIteratorExtensionReductionOperationsOn(
                            arbData = data,
                            mutableList = mutableList,
                            nextIteratorIndex = 5u,
                            validator = impl.listValidator,
                        )
                    }
                }
            }
            test("test random series of iterator extension and reduction operations with ensured capacity") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, numberOfOperations = 3u)
                        val mutableList = producer.produceBy<UInt>(20u, data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListIteratorExtensionReductionOperationsOn(
                            arbData = data,
                            mutableList = mutableList,
                            nextIteratorIndex = 5u,
                            validator = impl.listValidator,
                        )
                    }
                }
            }
        }

        if (producer is KoneFixedCapacityMutableListProducer)
            test("test random series of iterator extension and reduction operations") {
                AssertionScope.softly {
                    repeat(1000u) {
                        val data = Random.randomMutableListExtensionReductionOperationsWithResultsSeries(randomElement = { Random.nextUInt() }, initialSize = 10u, capacity = 20u, numberOfOperations = 3u)
                        val mutableList = producer.produceBy<UInt>(20u, data.initialList.size.toUInt()) { data.initialList[it.toInt()] }
                        testKoneMutableListIteratorExtensionReductionOperationsOn(
                            arbData = data,
                            mutableList = mutableList,
                            nextIteratorIndex = 5u,
                            validator = impl.listValidator,
                        )
                    }
                }
            }
    }
}