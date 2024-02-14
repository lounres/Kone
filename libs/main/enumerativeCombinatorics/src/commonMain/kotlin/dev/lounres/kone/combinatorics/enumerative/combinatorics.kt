/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.combinatorics.enumerative

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.misc.scope


public fun <E1, E2> cartesianProduct(
    collection1: KoneIterable<E1>,
    collection2: KoneIterable<E2>,
): Sequence<Pair<E1, E2>> = sequence {
    for (e1 in collection1) for (e2 in collection2) yield(Pair(e1, e2))
}

public fun <E1, E2, E3> cartesianProduct(
    collection1: KoneIterable<E1>,
    collection2: KoneIterable<E2>,
    collection3: KoneIterable<E3>,
): Sequence<Triple<E1, E2, E3>> = sequence {
    for (e1 in collection1) for (e2 in collection2) for (e3 in collection3) yield(Triple(e1, e2, e3))
}

public fun <E> cartesianProduct(collections: KoneList<KoneList<E>>): Sequence<KoneList<E>> = sequence {
    if (collections.any { it.isEmpty() }) return@sequence
    // TODO: Remove eventually. It just fixes some strange bug
    fun <E> KoneIterableList<E>.foo(predicate: (index: UInt, element: E) -> Boolean): UInt = lastIndexThat(predicate)

    val size = collections.size
    val lastIndices = KoneUIntArray(size) { collections[it].lastIndex }
    val firstElements = KoneIterableList(size) { collections[it].first() }
    val currentIndices = KoneMutableUIntArray(size) { 0u }
    val currentElements = firstElements.toKoneMutableIterableList()

    while (true) {
        yield(currentElements.toKoneIterableList())

        val firstToIncrease = currentIndices.foo { k, index -> index != lastIndices[k] }
        if (firstToIncrease == UInt.MAX_VALUE) return@sequence

        val newIndex = ++currentIndices[firstToIncrease]
        currentElements[firstToIncrease] = collections[firstToIncrease][newIndex]

        for (k in (firstToIncrease+1u)..<size) {
            currentIndices[k] = 0u
            currentElements[k] = firstElements[k]
        }
    }
}

public fun <E> cartesianProduct(vararg collections: KoneList<E>): Sequence<KoneList<E>> = cartesianProduct(KoneArray(collections))

public infix fun <E> KoneList<E>.cartesianPower(power: UInt): Sequence<KoneList<E>> {
    val collection = this

    return sequence {
        if (collection.isEmpty()) return@sequence

        val lastIndex = collection.lastIndex
        val firstElement = collection.first()
        val currentIndices = KoneMutableUIntArray(power) { 0u }
        val currentElements = KoneSettableIterableList(power) { firstElement }

        while (true) {
            yield(currentElements.toKoneIterableList())

            val firstToIncrease = currentIndices.lastIndexThat { _, index -> index != lastIndex }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            val newIndex = ++currentIndices[firstToIncrease]
            currentElements[firstToIncrease] = collection[newIndex]

            for (k in (firstToIncrease + 1u) ..< power) {
                currentIndices[k] = 0u
                currentElements[k] = firstElement
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
public fun <E> KoneList<E>.selectiveCartesianPower(power: UInt, testPrefix: (KoneIterableList<E>) -> Boolean): Sequence<KoneList<E>> {
    val collection = this

    return sequence {
        if (collection.isEmpty()) return@sequence

        val lastIndex = collection.size - 1u
        var currentSize = 0u
        val currentIndices = KoneMutableUIntArray(power)
        val currentElements = KoneMutableArray<Any?>(power) { null }
        while (true) {
            if (testPrefix(KoneIterableList(currentSize) { currentElements[it] as E })) {
                if (currentSize < power) {
                    currentIndices[currentSize] = 0u
                    currentElements[currentSize] = collection[0u]
                    currentSize++
                    continue
                } else {
                    yield(KoneIterableList(power) { currentElements[it] as E })
                }
            }
            while (currentSize > 0u && currentIndices[currentSize - 1u] == lastIndex) currentSize--
            if (currentSize == 0u) break
            currentIndices[currentSize - 1u]++
            currentElements[currentSize - 1u] = collection[currentIndices[currentSize - 1u]]
        }
    }
}

// TODO: Add more selective functions

public fun <E> KoneList<E>.combinations(k: UInt = size): Sequence<KoneIterableList<E>> {
    val collection = this

    return sequence {
        if (collection.size < k) return@sequence

        val addition = collection.size - k
        val currentIndices = KoneMutableUIntArray(k) { it }
        val currentElements = KoneSettableIterableList(k) { collection[it] }

        while (true) {
            yield(currentElements.toKoneIterableList())

            val firstToIncrease = currentIndices.lastIndexThat { t, index -> index < addition + t }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            var index = currentIndices[firstToIncrease]
            for (t in firstToIncrease ..< k) {
                currentIndices[t] = ++index
                currentElements[t] = collection[index]
            }
        }
    }
}

public fun <E> KoneList<E>.allCombinations(): Sequence<KoneList<E>> {
    val collection = this

    return sequence {
        val size = collection.size
        val currentState = KoneMutableUIntArray(size) { 0u }
        var currentElements = emptyKoneIterableList<E>()

        while (true) {
            yield(currentElements)

            val firstToIncrease = currentState.indexThat { _, element -> element == 0u }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            currentState[firstToIncrease] = 1u
            for (i in 0u ..< firstToIncrease) currentState[i] = 0u
            currentElements = buildKoneIterableList {
                add(collection[firstToIncrease])
                addAll(currentElements.drop(firstToIncrease))
            }
        }
    }
}

public fun <E> KoneList<E>.permutations(k: UInt = size): Sequence<KoneIterableList<E>> {
    val collection = this

    return sequence {
        if (collection.size < k) return@sequence

        val size = collection.size
        val references = KoneMutableUIntArray(size + 1u) { it + 1u }.apply {
            this[0u] = k+1u
            for (t in 1u..k) this[t] = 0u
        }
        val currentIndices = KoneMutableUIntArray(k) { it + 1u }
        val currentElements = KoneSettableIterableList(k) { collection[it] }

        // FIXME: KT-17579
        fun addStartMark(): UInt {
            val index = references[0u]
            references[0u] = references[index]
            references[index] = 0u
            return index
        }
        // FIXME: KT-17579
        fun removeMark(index: UInt) {
            val prev = references[index]
            val next = references[prev]
            references[prev] = index
            references[index] = next
        }
        // FIXME: KT-17579
        fun moveToNextMark(index: UInt): UInt {
            val prev = references[index]
            val next = references[prev]
            val next2 = references[next]
            references[prev] = index
            references[index] = next2
            references[next] = index
            return next
        }

        while (true) {
            yield(currentElements.toKoneIterableList())

            val firstToIncrease = scope {
                var current = k - 1u
                var index = currentIndices[current]
                while (references[references[index]] == size + 1u) {
                    removeMark(index)
                    current--
                    if (current == UInt.MAX_VALUE) break
                    index = currentIndices[current]
                }
                current
            }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            val newIndex = moveToNextMark(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1u]

            for (t in firstToIncrease+1u ..< k) {
                val index = addStartMark()
                currentIndices[t] = index
                currentElements[t] = collection[index-1u]
            }
        }
    }
}

public fun <E> KoneList<E>.allPermutations(): Sequence<KoneIterableList<E>> {
    val collection = this

    return sequence {
        val size = collection.size
        val references = KoneMutableUIntArray(size + 1u) { it+1u }
        var currentSize = 0u
        val currentIndices = KoneMutableUIntArray(size) { 0u }
        val currentElements = KoneSettableIterableList<E?>(size) { null }

        // FIXME: KT-45725
        fun getElements(): KoneIterableList<E> = @Suppress("UNCHECKED_CAST") (currentElements.take(currentSize) as KoneIterableList<E>)

        // FIXME: KT-17579
        fun addStartMark(): UInt {
            val index = references[0u]
            references[0u] = references[index]
            references[index] = 0u
            return index
        }
        // FIXME: KT-17579
        fun removeMark(index: UInt) {
            val prev = references[index]
            val next = references[prev]
            references[prev] = index
            references[index] = next
        }
        // FIXME: KT-17579
        fun moveToNextMark(index: UInt): UInt {
            val prev = references[index]
            val next = references[prev]
            val next2 = references[next]
            references[prev] = index
            references[index] = next2
            references[next] = index
            return next
        }

        while (true) {
            yield(getElements())

            if (currentSize != size) {
                val index = addStartMark()
                currentIndices[currentSize] = index
                currentElements[currentSize] = collection[index-1u]
                currentSize++
                continue
            }

            val firstToIncrease = scope {
                var current = size - 1u
                var collectionIndex = currentIndices[current]
                while (references[references[collectionIndex]] == size + 1u) {
                    removeMark(collectionIndex)
                    currentElements[current] = null
                    current--
                    if (current == UInt.MAX_VALUE) break
                    collectionIndex = currentIndices[current]
                }
                current
            }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            val newIndex = moveToNextMark(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1u]
            currentSize = firstToIncrease + 1u
        }
    }
}

public fun <E> KoneList<E>.combinationsWithoutRepetitions(k: UInt = size, equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<KoneIterableList<E>> {
    val collection = this

    return sequence {
        if (collection.size < k) return@sequence
        if (collection.size == 0u) {
            yield(emptyKoneIterableList())
            return@sequence
        }

        val size = collection.size
        val sortedCollection: KoneIterableList<E>
        val counts: KoneUIntArray
        val groupStarts: KoneUIntArray
        scope {
            val references = KoneMutableUIntArray(size + 1u) { size + 1u }
            val countsBuilder = KoneGrowableArrayList<UInt>()
            scope {
                val countIndices = KoneMutableUIntArray(size)
                var indexOfLastElement = 0u
                for (i in 1u..size) {
                    var j = i - 1u
                    while (j != 0u) {
                        if (equalityTest(collection[j - 1u], collection[i - 1u])) break
                        j--
                    }
                    if (j == 0u) {
                        references[indexOfLastElement] = i
                        indexOfLastElement = i

                        countIndices[i - 1u] = countsBuilder.size
                        countsBuilder.add(1u)
                    } else {
                        references[i] = references[j]
                        references[j] = i
                        if (indexOfLastElement == j) indexOfLastElement = i

                        countIndices[i - 1u] = countIndices[j - 1u]
                        countsBuilder[countIndices[j - 1u]]++
                    }
                }
            }

            counts = countsBuilder.toKoneUIntArray()
            sortedCollection = scope {
                var lastIndex = 0u
                KoneIterableList(size) {
                    lastIndex = references[lastIndex]
                    collection[lastIndex-1u]
                }
            }
            groupStarts = scope {
                var lastStart = 0u
                KoneUIntArray(countsBuilder.size) { index ->
                    lastStart.also { lastStart += countsBuilder[index] }
                }
            }
        }

        val currentCounts = KoneMutableUIntArray(counts.size)
        val restCounts = KoneMutableUIntArray(counts.size) { 0u }
        val currentElements = KoneSettableIterableList<E>(k) { sortedCollection[0u] }

        fun reinitializeCurrentCountsFrom(startIndex: UInt, restCount: UInt) {
            if (startIndex == counts.size) return
            var currentIndex = startIndex
            var currentElementStart = k - restCount
            var rest = restCount
            while (true) {
                if (counts[currentIndex] >= rest) {
                    currentCounts[currentIndex] = rest
                    for (i in 0u ..< rest) {
                        currentElements[currentElementStart + i] = sortedCollection[groupStarts[currentIndex] + i]
                    }
                    for (i in currentIndex ..< counts.size) restCounts[i] = 0u
                    break
                } else {
                    currentCounts[currentIndex] = counts[currentIndex]
                    rest -= currentCounts[currentIndex]
                    restCounts[currentIndex] = rest
                    for (i in 0u ..< currentCounts[currentIndex]) {
                        currentElements[currentElementStart + i] = sortedCollection[groupStarts[currentIndex] + i]
                    }
                    currentElementStart += currentCounts[currentIndex]
                    currentIndex++
                }
            }
        }

        reinitializeCurrentCountsFrom(0u, k)

        while (true) {
            yield(currentElements.toKoneIterableList())

            val firstToDecrease = currentCounts.lastIndexThat { index, count ->
                index < counts.size - 1u &&
                        currentCounts[index + 1u] < counts[index + 1u] &&
                        count > 0u
            }
            if (firstToDecrease == UInt.MAX_VALUE) return@sequence

            currentCounts[firstToDecrease]--
            restCounts[firstToDecrease]++
            reinitializeCurrentCountsFrom(startIndex = firstToDecrease + 1u, restCount = restCounts[firstToDecrease])
        }
    }
}

public fun <E> KoneList<E>.allCombinationsWithoutRepetitions(equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<KoneIterableList<E>> {
    val collection = this

    return sequence {
        if (collection.size == 0u) {
            yield(emptyKoneIterableList())
            return@sequence
        }

        val size = collection.size
        val sortedCollection: KoneIterableList<E>
        val counts: KoneUIntArray
        val groupStarts: KoneUIntArray
        scope {
            val references = KoneMutableUIntArray(size + 1u) { size + 1u }
            val countsBuilder = koneMutableIterableListOf<UInt>()
            scope {
                val countIndices = KoneMutableUIntArray(size)
                var indexOfLastElement = 0u
                for (i in 1u..size) {
                    var j = i - 1u
                    while (j != 0u) {
                        if (equalityTest(collection[j - 1u], collection[i - 1u])) break
                        j--
                    }
                    if (j == 0u) {
                        references[indexOfLastElement] = i
                        indexOfLastElement = i

                        countIndices[i - 1u] = countsBuilder.size
                        countsBuilder.add(1u)
                    } else {
                        references[i] = references[j]
                        references[j] = i
                        if (indexOfLastElement == j) indexOfLastElement = i

                        countIndices[i - 1u] = countIndices[j - 1u]
                        countsBuilder[countIndices[j - 1u]]++
                    }
                }
            }

            counts = countsBuilder.toKoneUIntArray()
            sortedCollection = scope {
                var lastIndex = 0u
                KoneIterableList(size) {
                    lastIndex = references[lastIndex]
                    collection[lastIndex-1u]
                }
            }
            groupStarts = scope {
                var lastStart = 0u
                KoneUIntArray(countsBuilder.size) { index ->
                    lastStart.also { lastStart += countsBuilder[index] }
                }
            }
        }

        val currentCounts = KoneMutableUIntArray(counts.size) { 0u }
        val currentElements = KoneMutableIterableList<E?>(size) { null }
        var currentSize = 0u

        while (true) {
            @Suppress("UNCHECKED_CAST")
            yield(currentElements.take(currentSize) as KoneIterableList<E>)

            val firstToIncrease = currentCounts.lastIndexThat { index, count -> count < counts[index] }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            currentCounts[firstToIncrease]++
            currentSize++
            for (index in firstToIncrease+1u ..< counts.size) {
                currentSize -= currentCounts[index]
                currentCounts[index] = 0u
            }
            currentElements[currentSize-1u] = sortedCollection[groupStarts[firstToIncrease] + currentCounts[firstToIncrease]-1u]
        }
    }
}

public fun <E> KoneList<E>.permutationsWithoutRepetitions(k: UInt = size, equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<KoneIterableList<E>> {
    val collection = this

    return sequence {
        if (collection.size < k) return@sequence

        val size = collection.size
        val references = KoneMutableUIntArray(size + 1u) { it + 1u }
        val nextSameElement = KoneMutableUIntArray(size + 1u) { size + 1u }
        scope {
            var indexOfLastNewElement = 0u
            for (i in 1u..size) {
                var j = i-1u
                while (j != 0u) {
                    if (equalityTest(collection[j-1u], collection[i-1u])) break
                    j--
                }
                if (j == 0u) {
                    references[indexOfLastNewElement] = i
                    indexOfLastNewElement = i
                } else {
                    references[i] = j
                    nextSameElement[j] = i
                }
            }
            references[indexOfLastNewElement] = size + 1u
        }
        val currentIndices = KoneMutableUIntArray(k) { it + 1u }
        val currentElements = KoneMutableIterableList(k) { collection[it] }

        fun addMarkAt(index: UInt): UInt {
            val next = references[index]
            if (nextSameElement[next] != size + 1u) {
                references[nextSameElement[next]] = references[next]
                references[index] = nextSameElement[next]
                references[next] = index
            } else {
                references[index] = references[next]
                references[next] = index
            }
            return next
        }
        // FIXME: KT-17579
        fun addStartMark(): UInt = addMarkAt(0u)
        // FIXME: KT-17579
        fun removeMarkFrom(index: UInt) {
            val prev = references[index]
            val next = references[prev]
            if (nextSameElement[index] != size + 1u) {
                check(nextSameElement[index] == next)
                references[prev] = index
                references[index] = references[next]
                references[next] = index
            } else {
                references[prev] = index
                references[index] = next
            }
        }

        for(i in 0u..<k) {
            val index = addStartMark()
            currentIndices[i] = index
            currentElements[i] = collection[index-1u]
        }

        while (true) {
            yield(currentElements.toKoneIterableList())

            val firstToIncrease = scope {
                var current = k - 1u
                while (current != UInt.MAX_VALUE) {
                    val index = currentIndices[current]
                    removeMarkFrom(index)
                    if (references[index] != size + 1u) break
                    current--
                }
                current
            }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            val newIndex = addMarkAt(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1u]

            for (t in firstToIncrease+1u ..< k) {
                val index = addStartMark()
                currentIndices[t] = index
                currentElements[t] = collection[index-1u]
            }
        }
    }
}

public fun <E> KoneList<E>.allPermutationsWithoutRepetitions(equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<KoneIterableList<E>> {
    val collection = this

    return sequence {
        val size = collection.size
        val references = KoneMutableUIntArray(size + 1u) { it + 1u }
        val nextSameElement = KoneMutableUIntArray(size + 1u) { size + 1u }
        scope {
            var indexOfLastNewElement = 0u
            for (i in 1u..size) {
                var j = i-1u
                while (j != 0u) {
                    if (equalityTest(collection[j-1u], collection[i-1u])) break
                    j--
                }
                if (j == 0u) {
                    references[indexOfLastNewElement] = i
                    indexOfLastNewElement = i
                } else {
                    references[i] = j
                    nextSameElement[j] = i
                }
            }
            references[indexOfLastNewElement] = size + 1u
        }
        var currentSize = 0u
        val currentIndices = KoneMutableUIntArray(size) { it + 1u }
        val currentElements = KoneMutableIterableList(size) { collection[it] }

        // FIXME: KT-45725
        fun getElements(): KoneIterableList<E> = currentElements.take(currentSize)

        fun addMarkAt(index: UInt): UInt {
            val next = references[index]
            if (nextSameElement[next] != size + 1u) {
                references[nextSameElement[next]] = references[next]
                references[index] = nextSameElement[next]
                references[next] = index
            } else {
                references[index] = references[next]
                references[next] = index
            }
            return next
        }
        // FIXME: KT-17579
        fun addStartMark(): UInt = addMarkAt(0u)
        // FIXME: KT-17579
        fun removeMarkFrom(index: UInt) {
            val prev = references[index]
            val next = references[prev]
            if (nextSameElement[index] != size + 1u) {
                check(nextSameElement[index] == next)
                references[prev] = index
                references[index] = references[next]
                references[next] = index
            } else {
                references[prev] = index
                references[index] = next
            }
        }

        while (true) {
            yield(getElements())

            if (currentSize != size) {
                val index = addStartMark()
                currentIndices[currentSize] = index
                currentElements[currentSize] = collection[index-1u]
                currentSize++
                continue
            }

            val firstToIncrease = scope {
                var current = size - 1u
                while (current != UInt.MAX_VALUE) {
                    val index = currentIndices[current]
                    removeMarkFrom(index)
                    if (references[index] != size + 1u) break
                    current--
                }
                current
            }
            if (firstToIncrease == UInt.MAX_VALUE) return@sequence

            val newIndex = addMarkAt(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1u]
            currentSize = firstToIncrease + 1u
        }
    }
}