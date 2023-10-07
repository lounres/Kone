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