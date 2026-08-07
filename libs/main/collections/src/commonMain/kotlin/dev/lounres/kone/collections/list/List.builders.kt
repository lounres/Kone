/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.DelicateBulkElementsRemoverAPI
import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.DelicateSeveralElementsInserterAPI
import dev.lounres.kone.collections.KoneBulkElementsRemover
import dev.lounres.kone.collections.KoneSeveralElementsInserter
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.empty.KoneEmptySettableNoddedList
import dev.lounres.kone.collections.list.implementations.*
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableList
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableNoddedList
import dev.lounres.kone.collections.sequence.KoneSequence
import dev.lounres.kone.collections.utils.toOptimizedList
import dev.lounres.kone.collections.utils.toOptimizedNoddedList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// TODO: Add builders for nodded lists

/**
 * Returns empty list.
 */
public fun <Element> KoneList.Companion.empty(): KoneList<Element> = KoneEmptySettableNoddedList

/**
 * Returns empty nodded list.
 */
public fun <Element> KoneNoddedList.Companion.empty(): KoneNoddedList<Element> = KoneEmptySettableNoddedList

/**
 * Returns empty settable list.
 */
@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableList.Companion.empty(): KoneSettableList<Element> = KoneEmptySettableNoddedList as KoneSettableList<Element>

/**
 * Returns empty settable nodded list.
 */
@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableNoddedList.Companion.empty(): KoneSettableNoddedList<Element> = KoneEmptySettableNoddedList as KoneSettableNoddedList<Element>

/**
 * Returns a list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `index` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else KoneArraySettableList.generate(size, initializer)

/**
 * Returns a list of elements over the provided [indices] range produced by the [initializer].
 *
 * The element with index `index` (from [indices] range) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values in the range
 * in their order starting with the first.
 */
public inline fun <Element> KoneList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneList<Element> =
    if (indices.isEmpty()) KoneList.empty()
    else KoneArraySettableList.generate(indices, initializer)

/**
 * Returns a nodded list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `index` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneNoddedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneNoddedList<Element> =
    if (size == 0u) KoneNoddedList.empty()
    else KoneArraySettableNoddedList.generate(size, initializer)

/**
 * Returns a nodded list of elements over the provided [indices] range produced by the [initializer].
 *
 * The element with index `index` (from [indices] range) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values in the range
 * in their order starting with the first.
 */
public inline fun <Element> KoneNoddedList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneNoddedList<Element> =
    if (indices.isEmpty()) KoneNoddedList.empty()
    else KoneArraySettableNoddedList.generate(indices, initializer)

/**
 * Returns a settable list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `index` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneSettableList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneSettableList<Element> =
    if (size == 0u) KoneSettableList.empty()
    else KoneArraySettableList.generate(size, initializer)

/**
 * Returns a settable list of elements over the provided [indices] range produced by the [initializer].
 *
 * The element with index `index` (from [indices] range) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values in the range
 * in their order starting with the first.
 */
public inline fun <Element> KoneSettableList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneSettableList<Element> =
    if (indices.isEmpty()) KoneSettableList.empty()
    else KoneArraySettableList.generate(indices, initializer)

/**
 * Returns a settable nodded list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `index` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneSettableNoddedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneSettableNoddedList<Element> =
    if (size == 0u) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList.generate(size, initializer)

/**
 * Returns a settable nodded list of elements over the provided [indices] range produced by the [initializer].
 *
 * The element with index `index` (from [indices] range) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values in the range
 * in their order starting with the first.
 */
public inline fun <Element> KoneSettableNoddedList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneSettableNoddedList<Element> =
    if (indices.isEmpty()) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList.generate(indices, initializer)

/**
 * Returns a mutable list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `index` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneMutableList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneMutableList<Element> =
    KoneArrayResizableList.generate(size, initializer)

/**
 * Returns a mutable list of elements over the provided [indices] range produced by the [initializer].
 *
 * The element with index `index` (from [indices] range) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values in the range
 * in their order starting with the first.
 */
public inline fun <Element> KoneMutableList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneMutableList<Element> =
    KoneArrayResizableList.generate(indices, initializer)

/**
 * Returns a mutable nodded list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `index` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneMutableNoddedList.Companion.generate(size: UInt, initializer: (index: UInt) -> Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList.generate(size, initializer)

/**
 * Returns a mutable nodded list of elements over the provided [indices] range produced by the [initializer].
 *
 * The element with index `index` (from [indices] range) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values in the range
 * in their order starting with the first.
 */
public inline fun <Element> KoneMutableNoddedList.Companion.generate(indices: UIntRange, initializer: (index: UInt) -> Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList.generate(indices, initializer)

/**
 * Returns a list of provided [size] where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` from `1` to [size] exclusive, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices starting with `1`.
 */
public inline fun <Element> KoneList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else KoneArraySettableList.induce(size, initialElement, inducer)

/**
 * Returns a list of elements over the provided [indices] range where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` in the [indices] range after the first, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices.
 */
public inline fun <Element> KoneList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneList<Element> =
    if (indices.isEmpty()) KoneList.empty()
    else KoneArraySettableList.induce(indices, initialElement, inducer)

/**
 * Returns a nodded list of provided [size] where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` from `1` to [size] exclusive, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices starting with `1`.
 */
public inline fun <Element> KoneNoddedList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneNoddedList<Element> =
    if (size == 0u) KoneNoddedList.empty()
    else KoneArraySettableNoddedList.induce(size, initialElement, inducer)

/**
 * Returns a nodded list of elements over the provided [indices] range where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` in the [indices] range after the first, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices.
 */
public inline fun <Element> KoneNoddedList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneNoddedList<Element> =
    if (indices.isEmpty()) KoneNoddedList.empty()
    else KoneArraySettableNoddedList.induce(indices, initialElement, inducer)

/**
 * Returns a settable list of provided [size] where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` from `1` to [size] exclusive, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices starting with `1`.
 */
public inline fun <Element> KoneSettableList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneSettableList<Element> =
    if (size == 0u) KoneSettableList.empty()
    else KoneArraySettableList.induce(size, initialElement, inducer)

/**
 * Returns a settable list of elements over the provided [indices] range where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` in the [indices] range after the first, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices.
 */
public inline fun <Element> KoneSettableList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneSettableList<Element> =
    if (indices.isEmpty()) KoneSettableList.empty()
    else KoneArraySettableList.induce(indices, initialElement, inducer)

/**
 * Returns a settable nodded list of provided [size] where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` from `1` to [size] exclusive, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices starting with `1`.
 */
public inline fun <Element> KoneSettableNoddedList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneSettableNoddedList<Element> =
    if (size == 0u) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList.induce(size, initialElement, inducer)

/**
 * Returns a settable nodded list of elements over the provided [indices] range where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` in the [indices] range after the first, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices.
 */
public inline fun <Element> KoneSettableNoddedList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneSettableNoddedList<Element> =
    if (indices.isEmpty()) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList.induce(indices, initialElement, inducer)

/**
 * Returns a mutable list of provided [size] where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` from `1` to [size] exclusive, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices starting with `1`.
 */
public inline fun <Element> KoneMutableList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneMutableList<Element> =
    KoneArrayResizableList.induce(size, initialElement, inducer)

/**
 * Returns a mutable list of elements over the provided [indices] range where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` in the [indices] range after the first, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices.
 */
public inline fun <Element> KoneMutableList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneMutableList<Element> =
    KoneArrayResizableList.induce(indices, initialElement, inducer)

/**
 * Returns a mutable nodded list of provided [size] where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` from `1` to [size] exclusive, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices starting with `1`.
 */
public inline fun <Element> KoneMutableNoddedList.Companion.induce(size: UInt, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList.induce(size, initialElement, inducer)

/**
 * Returns a mutable nodded list of elements over the provided [indices] range where each next element is computed from the previous one.
 *
 * The first element is [initialElement].
 * For each next index `i` in the [indices] range after the first, the element is `inducer(i, previousElement)`.
 * All [inducer] invocations are computed consecutively in the order of indices.
 */
public inline fun <Element> KoneMutableNoddedList.Companion.induce(indices: UIntRange, initialElement: Element, inducer: (index: UInt, previous: Element) -> Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList.induce(indices, initialElement, inducer)

/**
 * Returns a list of provided [size] filled with the given [element].
 */
public fun <Element> KoneList.Companion.fill(size: UInt, element: Element): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else KoneArraySettableList.generate(size) { element }

/**
 * Returns a nodded list of provided [size] filled with the given [element].
 */
public fun <Element> KoneNoddedList.Companion.fill(size: UInt, element: Element): KoneNoddedList<Element> =
    if (size == 0u) KoneNoddedList.empty()
    else KoneArraySettableNoddedList.generate(size) { element }

/**
 * Returns a settable list of provided [size] filled with the given [element].
 */
public fun <Element> KoneSettableList.Companion.fill(size: UInt, element: Element): KoneSettableList<Element> =
    if (size == 0u) KoneSettableList.empty()
    else KoneArraySettableList.generate(size) { element }

/**
 * Returns a settable nodded list of provided [size] filled with the given [element].
 */
public fun <Element> KoneSettableNoddedList.Companion.fill(size: UInt, element: Element): KoneSettableNoddedList<Element> =
    if (size == 0u) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList.generate(size) { element }

/**
 * Returns a mutable list of provided [size] filled with the given [element].
 */
public fun <Element> KoneMutableList.Companion.fill(size: UInt, element: Element): KoneMutableList<Element> =
    KoneArrayResizableList.generate(size) { element }

/**
 * Returns a mutable nodded list of provided [size] filled with the given [element].
 */
public fun <Element> KoneMutableNoddedList.Companion.fill(size: UInt, element: Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList.generate(size) { element }

/**
 * Returns an empty list.
 */
public fun <Element> KoneList.Companion.of(): KoneList<Element> = KoneList.empty()

/**
 * Returns a singleton list containing the given [element].
 */
public fun <Element> KoneList.Companion.of(element: Element): KoneList<Element> = KoneSingletonSettableList(element)

/**
 * Returns a list containing the given [elements].
 */
@Suppress("UNCHECKED_CAST")
public fun <Element> KoneList.Companion.of(vararg elements: Element): KoneList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

/**
 * Returns an empty nodded list.
 */
public fun <Element> KoneNoddedList.Companion.of(): KoneNoddedList<Element> = KoneNoddedList.empty()

/**
 * Returns a singleton nodded list containing the given [element].
 */
public fun <Element> KoneNoddedList.Companion.of(element: Element): KoneNoddedList<Element> =
    KoneSingletonSettableNoddedList(element)

/**
 * Returns a nodded list containing the given [elements].
 */
@Suppress("UNCHECKED_CAST")
public fun <Element> KoneNoddedList.Companion.of(vararg elements: Element): KoneNoddedList<Element> =
    KoneArraySettableNoddedList.generate(elements.size.toUInt()) { elements[it.toInt()] }

/**
 * Returns a settable list containing the given [elements].
 */
@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableList.Companion.of(vararg elements: Element): KoneSettableList<Element> =
    if (elements.isEmpty()) KoneSettableList.empty()
    else KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

/**
 * Returns a settable nodded list containing the given [elements].
 */
@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableNoddedList.Companion.of(vararg elements: Element): KoneSettableNoddedList<Element> =
    if (elements.isEmpty()) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList.generate(elements.size.toUInt()) { elements[it.toInt()] }

/**
 * Returns an empty mutable list.
 */
public fun <Element> KoneMutableList.Companion.of(): KoneMutableList<Element> =
    KoneArrayResizableList()

/**
 * Returns a mutable list containing the given [elements].
 */
public fun <Element> KoneMutableList.Companion.of(vararg elements: Element): KoneMutableList<Element> =
    KoneArrayResizableList.generate(elements.size.toUInt()) { elements[it.toInt()] }

/**
 * Returns an empty mutable nodded list.
 */
public fun <Element> KoneMutableNoddedList.Companion.of(): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList()

/**
 * Returns a mutable nodded list containing the given [elements].
 */
public fun <Element> KoneMutableNoddedList.Companion.of(vararg elements: Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList.generate(elements.size.toUInt()) { elements[it.toInt()] }

/**
 * Converts this iterator to a [KoneMutableList].
 */
public fun <Element> KoneIterator<Element>.toKoneMutableList(): KoneMutableList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneMutableList.generate(it.size) { index -> it[index] }
    }

/**
 * Converts this iterable to a [KoneMutableList].
 */
public fun <Element> KoneIterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneMutableList.generate(size) { iterator.getAndMoveNext() }
}

/**
 * Converts this sequence to a [KoneMutableList].
 */
public fun <Element> KoneSequence<Element>.toKoneMutableList(): KoneMutableList<Element> =
    iterator().toKoneMutableList()

/**
 * Converts this iterator to a [KoneMutableNoddedList].
 */
public fun <Element> KoneIterator<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneMutableNoddedList.generate(it.size) { index -> it[index] }
    }

/**
 * Converts this iterable to a [KoneMutableNoddedList].
 */
public fun <Element> KoneIterable<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> {
    val iterator = iterator()
    return KoneMutableNoddedList.generate(size) { iterator.getAndMoveNext() }
}

/**
 * Converts this sequence to a [KoneMutableNoddedList].
 */
public fun <Element> KoneSequence<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> =
    iterator().toKoneMutableNoddedList()

/**
 * Converts this iterator to a [KoneSettableList].
 */
public fun <Element> KoneIterator<Element>.toKoneSettableList(): KoneSettableList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneSettableList.generate(it.size) { index -> it[index] }
    }

/**
 * Converts this iterable to a [KoneSettableList].
 */
public fun <Element> KoneIterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList.generate(size) { iterator.getAndMoveNext() }
}

/**
 * Converts this sequence to a [KoneSettableList].
 */
public fun <Element> KoneSequence<Element>.toKoneSettableList(): KoneSettableList<Element> =
    iterator().toKoneSettableList()

/**
 * Converts this iterator to a [KoneSettableNoddedList].
 */
public fun <Element> KoneIterator<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneSettableNoddedList.generate(it.size) { index -> it[index] }
    }

/**
 * Converts this iterable to a [KoneSettableNoddedList].
 */
public fun <Element> KoneIterable<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> {
    val iterator = iterator()
    return KoneSettableNoddedList.generate(size) { iterator.getAndMoveNext() }
}

/**
 * Converts this sequence to a [KoneSettableNoddedList].
 */
public fun <Element> KoneSequence<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> =
    iterator().toKoneSettableNoddedList()

/**
 * Converts this iterator to a [KoneList].
 */
public fun <Element> KoneIterator<Element>.toKoneList(): KoneList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.toOptimizedList()

/**
 * Converts this iterable to a [KoneList].
 */
public fun <Element> KoneIterable<Element>.toKoneList(): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else this.toKoneMutableList().toOptimizedList()

/**
 * Converts this sequence to a [KoneList].
 */
public fun <Element> KoneSequence<Element>.toKoneList(): KoneList<Element> =
    iterator().toKoneList()

/**
 * Converts this iterator to a [KoneNoddedList].
 */
public fun <Element> KoneIterator<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.toOptimizedNoddedList()

/**
 * Converts this iterable to a [KoneNoddedList].
 */
public fun <Element> KoneIterable<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    if (size == 0u) KoneNoddedList.empty()
    else this.toKoneMutableList().toOptimizedNoddedList()

/**
 * Converts this sequence to a [KoneNoddedList].
 */
public fun <Element> KoneSequence<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    iterator().toKoneNoddedList()

/**
 * A builder for constructing [KoneList] instances using a builder DSL.
 *
 * @param Element The element type of the list.
 */
@OptIn(DelicateCollectionsInheritanceAPI::class, DelicateSeveralElementsInserterAPI::class, DelicateBulkElementsRemoverAPI::class)
public class KoneListBuilder<Element> @PublishedApi internal constructor(result: KoneMutableList<Element>) : KoneMutableList<Element> {
    private var result: KoneMutableList<Element>? = result
    
    override val size: UInt get() {
        val result = result ?: error("This KoneList builder is already used")
        return result.size
    }
    
    override fun get(index: UInt): Element {
        val result = result ?: error("This KoneList builder is already used")
        return result[index]
    }
    
    override fun set(index: UInt, element: Element) {
        val result = result ?: error("This KoneList builder is already used")
        result[index] = element
    }
    
    override fun add(element: Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.add(element)
    }
    
    override fun addAt(index: UInt, element: Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.addAt(index, element)
    }
    
    override fun startAddingSeveralAt(index: UInt, number: UInt): KoneSeveralElementsInserter<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.startAddingSeveralAt(index, number)
    }
    
    override fun removeAt(index: UInt) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAt(index)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun startBulkyRemoving(): KoneBulkElementsRemover<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.startBulkyRemoving()
    }
    
    override fun iterator(): KoneMutableListIterator<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.iterator()
    }
    
    override fun iteratorFrom(index: UInt): KoneMutableListIterator<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.iteratorFrom(index)
    }
    
    /**
     * Adds the [this] element to the builder.
     *
     * @receiver The element to add.
     */
    public operator fun Element.unaryPlus() {
        val result = result ?: error("This KoneList builder is already used")
        result.add(this)
    }
    
    override fun equals(other: Any?): Boolean {
        val result = result ?: error("This KoneList builder is already used")
        return result == other
    }
    override fun hashCode(): Int {
        val result = result ?: error("This KoneList builder is already used")
        return result.hashCode()
    }
    override fun toString(): String {
        val result = result ?: error("This KoneList builder is already used")
        return result.toString()
    }
    
    @PublishedApi
    internal fun build(): KoneList<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

/**
 * Builds a [KoneList] using the provided [builderAction] DSL.
 *
 * @param Element The element type of the list.
 * @param builderAction The builder DSL action.
 * @return The built list.
 */
public inline fun <Element> KoneList.Companion.build(builderAction: KoneListBuilder<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneListBuilder(KoneMutableList.of<Element>()).apply(builderAction).build().toOptimizedList()
}

/**
 * Builds a [KoneList] with the given [initialCapacity] using the provided [builderAction] DSL.
 *
 * @param Element The element type of the list.
 * @param initialCapacity The initial capacity of the underlying storage.
 * @param builderAction The builder DSL action.
 * @return The built list.
 */
public inline fun <Element> KoneList.Companion.build(initialCapacity: UInt, builderAction: KoneListBuilder<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneListBuilder(KoneArrayGrowableList<Element>(initialCapacity)).apply(builderAction).build().toOptimizedList()
}

/**
 * A builder for constructing [KoneNoddedList] instances using a builder DSL.
 *
 * @param Element The element type of the list.
 */
@OptIn(DelicateCollectionsInheritanceAPI::class, DelicateSeveralElementsInserterAPI::class, DelicateBulkElementsRemoverAPI::class)
public class KoneNoddedListBuilder<Element> @PublishedApi internal constructor(result: KoneMutableNoddedList<Element>) : KoneMutableNoddedList<Element> {
    private var result: KoneMutableNoddedList<Element>? = result
    
    override val size: UInt get() {
        val result = result ?: error("This KoneList builder is already used")
        return result.size
    }
    
    override fun get(index: UInt): Element {
        val result = result ?: error("This KoneList builder is already used")
        return result[index]
    }
    
    override fun getNode(index: UInt): KoneMutableListNode<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.getNode(index)
    }
    
    override fun set(index: UInt, element: Element) {
        val result = result ?: error("This KoneList builder is already used")
        result[index] = element
    }
    
    override fun add(element: Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.add(element)
    }
    
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.addNode(element)
    }
    
    override fun addAt(index: UInt, element: Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.addAt(index, element)
    }
    
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.addNodeAt(index, element)
    }
    
    override fun startAddingSeveralAt(index: UInt, number: UInt): KoneSeveralElementsInserter<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.startAddingSeveralAt(index, number)
    }
    
    override fun removeAt(index: UInt) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAt(index)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun startBulkyRemoving(): KoneBulkElementsRemover<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.startBulkyRemoving()
    }
    
    override fun iterator(): KoneMutableNoddedListIterator<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.iterator()
    }
    
    override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.iteratorFrom(index)
    }
    
    /**
     * Adds the [this] element to the builder.
     *
     * @receiver The element to add.
     */
    public operator fun Element.unaryPlus() {
        val result = result ?: error("This KoneList builder is already used")
        result.add(this)
    }
    
    override fun equals(other: Any?): Boolean {
        val result = result ?: error("This KoneList builder is already used")
        return result == other
    }
    override fun hashCode(): Int {
        val result = result ?: error("This KoneList builder is already used")
        return result.hashCode()
    }
    override fun toString(): String {
        val result = result ?: error("This KoneList builder is already used")
        return result.toString()
    }
    
    @PublishedApi
    internal fun build(): KoneList<Element> {
        val result = result ?: error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

/**
 * Builds a [KoneNoddedList] using the provided [builderAction] DSL.
 *
 * @param Element The element type of the list.
 * @param builderAction The builder DSL action.
 * @return The built list.
 */
public inline fun <Element> KoneNoddedList.Companion.build(builderAction: KoneNoddedListBuilder<Element>.() -> Unit): KoneNoddedList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneNoddedListBuilder(KoneMutableNoddedList.of<Element>()).apply(builderAction).build().toOptimizedNoddedList()
}

/**
 * Builds a [KoneNoddedList] with the given [initialCapacity] using the provided [builderAction] DSL.
 *
 * @param Element The element type of the list.
 * @param initialCapacity The initial capacity of the underlying storage.
 * @param builderAction The builder DSL action.
 * @return The built list.
 */
public inline fun <Element> KoneNoddedList.Companion.build(initialCapacity: UInt, builderAction: KoneNoddedListBuilder<Element>.() -> Unit): KoneNoddedList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneNoddedListBuilder(KoneArrayGrowableNoddedList<Element>(initialCapacity)).apply(builderAction).build().toOptimizedNoddedList()
}