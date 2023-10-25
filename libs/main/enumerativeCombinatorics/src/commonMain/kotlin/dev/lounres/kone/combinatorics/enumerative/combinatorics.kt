/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.combinatorics.enumerative

import dev.lounres.kone.util.collectionOperations.lastIndexThat


public fun <E1, E2> cartesianProduct(
    collection1: Iterable<E1>,
    collection2: Iterable<E2>
): Sequence<Pair<E1, E2>> = sequence {
    for (e1 in collection1) for (e2 in collection2) yield(Pair(e1, e2))
}

public fun <E1, E2, E3> cartesianProduct(
    collection1: Iterable<E1>,
    collection2: Iterable<E2>,
    collection3: Iterable<E3>
): Sequence<Triple<E1, E2, E3>> = sequence {
    for (e1 in collection1) for (e2 in collection2) for (e3 in collection3) yield(Triple(e1, e2, e3))
}

public fun <E> cartesianProduct(collections: List<List<E>>): Sequence<List<E>> = sequence {
    if (collections.any { it.isEmpty() }) return@sequence

    val size = collections.size
    val lastIndices = IntArray(size) { collections[it].lastIndex }
    val firstElements = List(size) { collections[it].first() }
    val currentIndices = IntArray(size) { 0 }
    val currentElements = firstElements.toMutableList()

    while (true) {
        yield(currentElements.toList())

        val firstToIncrease = currentIndices.lastIndexThat { k, index -> index != lastIndices[k] }
        if (firstToIncrease == -1) return@sequence

        val newIndex = ++currentIndices[firstToIncrease]
        currentElements[firstToIncrease] = collections[firstToIncrease][newIndex]

        for (k in (firstToIncrease+1)..<size) {
            currentIndices[k] = 0
            currentElements[k] = firstElements[k]
        }
    }
}

public fun <E> cartesianProduct(vararg collections: List<E>): Sequence<List<E>> = cartesianProduct(collections.toList())

public infix fun <E> List<E>.cartesianPower(power: Int): Sequence<List<E>> {
    require(power >= 0) { "Cartesian power arity cannot be negative" }

    val collection = this

    return sequence {
        if (collection.isEmpty()) return@sequence

        val lastIndex = collection.lastIndex
        val firstElement = collection.first()
        val currentIndices = IntArray(power) { 0 }
        val currentElements = MutableList(power) { firstElement }

        while (true) {
            yield(currentElements.toList())

            val firstToIncrease = currentIndices.indexOfLast { it != lastIndex }
            if (firstToIncrease == -1) return@sequence

            val newIndex = ++currentIndices[firstToIncrease]
            currentElements[firstToIncrease] = collection[newIndex]

            for (k in (firstToIncrease + 1) ..< power) {
                currentIndices[k] = 0
                currentElements[k] = firstElement
            }
        }
    }
}

public fun <E> List<E>.combinations(k: Int = size): Sequence<List<E>> {
    require(k >= 0) { "Size of combinations must be non-negative" }

    val collection = this

    return sequence {
        if (collection.size < k) return@sequence

        val addition = collection.size - k
        val currentIndices = IntArray(k) { it }
        val currentElements = MutableList(k) { collection[it] }

        while (true) {
            yield(currentElements.toList())

            val firstToIncrease = currentIndices.lastIndexThat { t, index -> index < addition + t }
            if (firstToIncrease == -1) return@sequence

            var index = currentIndices[firstToIncrease]
            for (t in firstToIncrease ..< k) {
                currentIndices[t] = ++index
                currentElements[t] = collection[index]
            }
        }
    }
}

public fun <E> List<E>.allCombinations(): Sequence<List<E>> {
    val collection = this

    return sequence {
        val size = collection.size
        val currentState = BooleanArray(size) { false }
        var currentElements = emptyList<E>()

        while (true) {
            yield(currentElements)

            val firstToIncrease = currentState.indexOfFirst { !it }
            if (firstToIncrease == -1) return@sequence

            currentState[firstToIncrease] = true
            for (i in 0 ..< firstToIncrease) currentState[i] = false
            currentElements = buildList {
                add(collection[firstToIncrease])
                addAll(currentElements.drop(firstToIncrease))
            }

        }
    }
}

public fun <E> List<E>.permutations(k: Int = size): Sequence<List<E>> {
    require(k >= 0) { "Size of permutations must be non-negative" }

    val collection = this

    return sequence {
        if (collection.size < k) return@sequence

        val size = collection.size
        val references = IntArray(size + 1) { it + 1 }.apply {
            this[0] = k+1
            for (t in 1..k) this[t] = 0
        }
        val currentIndices = IntArray(k) { it + 1 }
        val currentElements = MutableList(k) { collection[it] }

        // FIXME: KT-17579
        fun addStartMark(): Int {
            val index = references[0]
            references[0] = references[index]
            references[index] = 0
            return index
        }
        // FIXME: KT-17579
        fun removeMark(index: Int) {
            val prev = references[index]
            val next = references[prev]
            references[prev] = index
            references[index] = next
        }
        // FIXME: KT-17579
        fun moveToNextMark(index: Int): Int {
            val prev = references[index]
            val next = references[prev]
            val next2 = references[next]
            references[prev] = index
            references[index] = next2
            references[next] = index
            return next
        }

        while (true) {
            yield(currentElements.toList())

            val firstToIncrease = run {
                var current = k - 1
                var index = currentIndices[current]
                while (references[references[index]] == size + 1) {
                    removeMark(index)
                    current--
                    if (current == -1) break
                    index = currentIndices[current]
                }
                current
            }
            if (firstToIncrease == -1) return@sequence

            val newIndex = moveToNextMark(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1]

            for (t in firstToIncrease+1 ..< k) {
                val index = addStartMark()
                currentIndices[t] = index
                currentElements[t] = collection[index-1]
            }
        }
    }
}

public fun <E> List<E>.allPermutations(): Sequence<List<E>> {
    val collection = this

    return sequence {
        val size = collection.size
        val references = IntArray(size + 1) { it+1 }
        var currentSize = 0
        val currentIndices = IntArray(size) { 0 }
        val currentElements = MutableList<E?>(size) { null }

        // FIXME: KT-45725
        fun getElements(): List<E> = @Suppress("UNCHECKED_CAST") (currentElements.take(currentSize) as List<E>)

        // FIXME: KT-17579
        fun addStartMark(): Int {
            val index = references[0]
            references[0] = references[index]
            references[index] = 0
            return index
        }
        // FIXME: KT-17579
        fun removeMark(index: Int) {
            val prev = references[index]
            val next = references[prev]
            references[prev] = index
            references[index] = next
        }
        // FIXME: KT-17579
        fun moveToNextMark(index: Int): Int {
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
                currentElements[currentSize] = collection[index-1]
                currentSize++
                continue
            }

            val firstToIncrease = run {
                var current = size - 1
                var collectionIndex = currentIndices[current]
                while (references[references[collectionIndex]] == size + 1) {
                    removeMark(collectionIndex)
                    currentElements[current] = null
                    current--
                    if (current == -1) break
                    collectionIndex = currentIndices[current]
                }
                current
            }
            if (firstToIncrease == -1) return@sequence

            val newIndex = moveToNextMark(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1]
            currentSize = firstToIncrease + 1
        }
    }
}

public fun <E> List<E>.combinationsWithoutRepetitions(k: Int = size, equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<List<E>> {
    require(k >= 0) { "Size of combinations must be non-negative" }

    val collection = this

    return sequence {
        if (collection.size < k) return@sequence
        if (collection.size == 0) {
            yield(emptyList())
            return@sequence
        }

        val size = collection.size
        val sortedCollection: List<E>
        val counts: IntArray
        val groupStarts: IntArray
        run {
            val references = IntArray(size + 1) { size + 1 }
            val countsBuilder = ArrayList<Int>()
            run {
                val countIndices = IntArray(size)
                var indexOfLastElement = 0
                for (i in 1..size) {
                    var j = i - 1
                    while (j != 0) {
                        if (equalityTest(collection[j - 1], collection[i - 1])) break
                        j--
                    }
                    if (j == 0) {
                        references[indexOfLastElement] = i
                        indexOfLastElement = i

                        countIndices[i - 1] = countsBuilder.size
                        countsBuilder.add(1)
                    } else {
                        references[i] = references[j]
                        references[j] = i
                        if (indexOfLastElement == j) indexOfLastElement = i

                        countIndices[i - 1] = countIndices[j - 1]
                        countsBuilder[countIndices[j - 1]]++
                    }
                }
            }

            counts = countsBuilder.toIntArray()
            sortedCollection = run {
                var lastIndex = 0
                List(size) {
                    lastIndex = references[lastIndex]
                    collection[lastIndex-1]
                }
            }
            groupStarts = run {
                var lastStart = 0
                IntArray(countsBuilder.size) { index ->
                    lastStart.also { lastStart += countsBuilder[index] }
                }
            }
        }

        val currentCounts = IntArray(counts.size)
        val restCounts = IntArray(counts.size) { 0 }
        val currentElements = MutableList<E>(k) { sortedCollection[0] }

        fun reinitializeCurrentCountsFrom(startIndex: Int, restCount: Int) {
            if (startIndex == counts.size) return
            var currentIndex = startIndex
            var currentElementStart = k - restCount
            var rest = restCount
            while (true) {
                if (counts[currentIndex] >= rest) {
                    currentCounts[currentIndex] = rest
                    for (i in 0 ..< rest) {
                        currentElements[currentElementStart + i] = sortedCollection[groupStarts[currentIndex] + i]
                    }
                    for (i in currentIndex ..< counts.size) restCounts[i] = 0
                    break
                } else {
                    currentCounts[currentIndex] = counts[currentIndex]
                    rest -= currentCounts[currentIndex]
                    restCounts[currentIndex] = rest
                    for (i in 0 ..< currentCounts[currentIndex]) {
                        currentElements[currentElementStart + i] = sortedCollection[groupStarts[currentIndex] + i]
                    }
                    currentElementStart += currentCounts[currentIndex]
                    currentIndex++
                }
            }
        }

        reinitializeCurrentCountsFrom(0, k)

        while (true) {
            yield(currentElements.toList())

            val firstToDecrease = currentCounts.lastIndexThat { index, count ->
                index < counts.size - 1 &&
                        currentCounts[index + 1] < counts[index + 1] &&
                        count > 0
            }
            if (firstToDecrease == -1) return@sequence

            currentCounts[firstToDecrease]--
            restCounts[firstToDecrease]++
            reinitializeCurrentCountsFrom(startIndex = firstToDecrease + 1, restCount = restCounts[firstToDecrease])
        }
    }
}

public fun <E> List<E>.allCombinationsWithoutRepetitions(equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<List<E>> {
    val collection = this

    return sequence {
        if (collection.size == 0) {
            yield(emptyList())
            return@sequence
        }

        val size = collection.size
        val sortedCollection: List<E>
        val counts: IntArray
        val groupStarts: IntArray
        run {
            val references = IntArray(size + 1) { size + 1 }
            val countsBuilder = ArrayList<Int>()
            run {
                val countIndices = IntArray(size)
                var indexOfLastElement = 0
                for (i in 1..size) {
                    var j = i - 1
                    while (j != 0) {
                        if (equalityTest(collection[j - 1], collection[i - 1])) break
                        j--
                    }
                    if (j == 0) {
                        references[indexOfLastElement] = i
                        indexOfLastElement = i

                        countIndices[i - 1] = countsBuilder.size
                        countsBuilder.add(1)
                    } else {
                        references[i] = references[j]
                        references[j] = i
                        if (indexOfLastElement == j) indexOfLastElement = i

                        countIndices[i - 1] = countIndices[j - 1]
                        countsBuilder[countIndices[j - 1]]++
                    }
                }
            }

            counts = countsBuilder.toIntArray()
            sortedCollection = run {
                var lastIndex = 0
                List(size) {
                    lastIndex = references[lastIndex]
                    collection[lastIndex-1]
                }
            }
            groupStarts = run {
                var lastStart = 0
                IntArray(countsBuilder.size) { index ->
                    lastStart.also { lastStart += countsBuilder[index] }
                }
            }
        }

        val currentCounts = IntArray(counts.size) { 0 }
        val currentElements = MutableList<E?>(size) { null }
        var currentSize = 0

        while (true) {
            @Suppress("UNCHECKED_CAST")
            yield(currentElements.take(currentSize) as List<E>)

            val firstToIncrease = currentCounts.lastIndexThat { index, count -> count < counts[index] }
            if (firstToIncrease == -1) return@sequence

            currentCounts[firstToIncrease]++
            currentSize++
            for (index in firstToIncrease+1 ..< counts.size) {
                currentSize -= currentCounts[index]
                currentCounts[index] = 0
            }
            currentElements[currentSize-1] = sortedCollection[groupStarts[firstToIncrease] + currentCounts[firstToIncrease]-1]
        }
    }
}

public fun <E> List<E>.permutationsWithoutRepetitions(k: Int = size, equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<List<E>> {
    require(k >= 0) { "Size of permutations must be non-negative" }

    val collection = this

    return sequence {
        if (collection.size < k) return@sequence

        val size = collection.size
        val references = IntArray(size + 1) { it + 1 }
        val nextSameElement = IntArray(size + 1) { size + 1 }
        run {
            var indexOfLastNewElement = 0
            for (i in 1..size) {
                var j = i-1
                while (j != 0) {
                    if (equalityTest(collection[j-1], collection[i-1])) break
                    j--
                }
                if (j == 0) {
                    references[indexOfLastNewElement] = i
                    indexOfLastNewElement = i
                } else {
                    references[i] = j
                    nextSameElement[j] = i
                }
            }
            references[indexOfLastNewElement] = size + 1
        }
        val currentIndices = IntArray(k) { it + 1 }
        val currentElements = MutableList(k) { collection[it] }

        fun addMarkAt(index: Int): Int {
            val next = references[index]
            if (nextSameElement[next] != size + 1) {
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
        fun addStartMark(): Int = addMarkAt(0)
        // FIXME: KT-17579
        fun removeMarkFrom(index: Int) {
            val prev = references[index]
            val next = references[prev]
            if (nextSameElement[index] != size + 1) {
                check(nextSameElement[index] == next)
                references[prev] = index
                references[index] = references[next]
                references[next] = index
            } else {
                references[prev] = index
                references[index] = next
            }
        }

        repeat(k) {
            val index = addStartMark()
            currentIndices[it] = index
            currentElements[it] = collection[index-1]
        }

        while (true) {
            yield(currentElements.toList())

            val firstToIncrease = run {
                var current = k - 1
                while (current >= 0) {
                    val index = currentIndices[current]
                    removeMarkFrom(index)
                    if (references[index] != size + 1) break
                    current--
                }
                current
            }
            if (firstToIncrease == -1) return@sequence

            val newIndex = addMarkAt(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1]

            for (t in firstToIncrease+1 ..< k) {
                val index = addStartMark()
                currentIndices[t] = index
                currentElements[t] = collection[index-1]
            }
        }
    }
}

public fun <E> List<E>.allPermutationsWithoutRepetitions(equalityTest: (E, E) -> Boolean = { e1, e2 -> e1 == e2 }): Sequence<List<E>> {
    val collection = this

    return sequence {
        val size = collection.size
        val references = IntArray(size + 1) { it + 1 }
        val nextSameElement = IntArray(size + 1) { size + 1 }
        run {
            var indexOfLastNewElement = 0
            for (i in 1..size) {
                var j = i-1
                while (j != 0) {
                    if (equalityTest(collection[j-1], collection[i-1])) break
                    j--
                }
                if (j == 0) {
                    references[indexOfLastNewElement] = i
                    indexOfLastNewElement = i
                } else {
                    references[i] = j
                    nextSameElement[j] = i
                }
            }
            references[indexOfLastNewElement] = size + 1
        }
        var currentSize = 0
        val currentIndices = IntArray(size) { it + 1 }
        val currentElements = MutableList(size) { collection[it] }

        // FIXME: KT-45725
        fun getElements(): List<E> = currentElements.take(currentSize)

        fun addMarkAt(index: Int): Int {
            val next = references[index]
            if (nextSameElement[next] != size + 1) {
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
        fun addStartMark(): Int = addMarkAt(0)
        // FIXME: KT-17579
        fun removeMarkFrom(index: Int) {
            val prev = references[index]
            val next = references[prev]
            if (nextSameElement[index] != size + 1) {
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
                currentElements[currentSize] = collection[index-1]
                currentSize++
                continue
            }

            val firstToIncrease = run {
                var current = size - 1
                while (current >= 0) {
                    val index = currentIndices[current]
                    removeMarkFrom(index)
                    if (references[index] != size + 1) break
                    current--
                }
                current
            }
            if (firstToIncrease == -1) return@sequence

            val newIndex = addMarkAt(currentIndices[firstToIncrease])
            currentIndices[firstToIncrease] = newIndex
            currentElements[firstToIncrease] = collection[newIndex-1]
            currentSize = firstToIncrease + 1
        }
    }
}