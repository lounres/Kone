/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.list.empty.KoneEmptySettableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableList
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableList
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableNoddedList
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.collections.list.implementations.induce
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableNoddedList
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

public fun <Element> KoneList.Companion.of(): KoneList<Element> = KoneList.empty()

public fun <Element> KoneList.Companion.of(element: Element): KoneList<Element> = KoneSingletonSettableList(element)

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneList.Companion.of(vararg elements: Element): KoneList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

public fun <Element> KoneNoddedList.Companion.of(): KoneNoddedList<Element> = KoneNoddedList.empty()

public fun <Element> KoneNoddedList.Companion.of(element: Element): KoneNoddedList<Element> =
    KoneSingletonSettableNoddedList(element)

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneNoddedList.Companion.of(vararg elements: Element): KoneNoddedList<Element> =
    KoneArraySettableNoddedList.generate(elements.size.toUInt()) { elements[it.toInt()] }

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableList.Companion.of(vararg elements: Element): KoneSettableList<Element> =
    if (elements.isEmpty()) KoneSettableList.empty()
    else KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableNoddedList.Companion.of(vararg elements: Element): KoneSettableNoddedList<Element> =
    if (elements.isEmpty()) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList.generate(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneMutableList.Companion.of(): KoneMutableList<Element> =
    KoneArrayResizableList()

public fun <Element> KoneMutableList.Companion.of(vararg elements: Element): KoneMutableList<Element> =
    KoneArrayResizableList.generate(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneMutableNoddedList.Companion.of(): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList()

public fun <Element> KoneMutableNoddedList.Companion.of(vararg elements: Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList.generate(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneIterator<Element>.toKoneMutableList(): KoneMutableList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneMutableList.generate(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneMutableList.generate(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneSequence<Element>.toKoneMutableList(): KoneMutableList<Element> =
    iterator().toKoneMutableList()

public fun <Element> KoneIterator<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneMutableNoddedList.generate(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> {
    val iterator = iterator()
    return KoneMutableNoddedList.generate(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneSequence<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> =
    iterator().toKoneMutableNoddedList()

public fun <Element> KoneIterator<Element>.toKoneSettableList(): KoneSettableList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneSettableList.generate(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList.generate(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneSequence<Element>.toKoneSettableList(): KoneSettableList<Element> =
    iterator().toKoneSettableList()

public fun <Element> KoneIterator<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneSettableNoddedList.generate(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> {
    val iterator = iterator()
    return KoneSettableNoddedList.generate(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneSequence<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> =
    iterator().toKoneSettableNoddedList()

public fun <Element> KoneIterator<Element>.toKoneList(): KoneList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.toOptimizedList()

public fun <Element> KoneIterable<Element>.toKoneList(): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else this.toKoneMutableList().toOptimizedList()

public fun <Element> KoneSequence<Element>.toKoneList(): KoneList<Element> =
    iterator().toKoneList()

public fun <Element> KoneIterator<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.toOptimizedNoddedList()

public fun <Element> KoneIterable<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    if (size == 0u) KoneNoddedList.empty()
    else this.toKoneMutableList().toOptimizedNoddedList()

public fun <Element> KoneSequence<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    iterator().toKoneNoddedList()

/**
 * A builder for constructing [KoneList] instances using a builder DSL.
 *
 * @param Element The element type of the list.
 */
@OptIn(DelicateCollectionsInheritanceAPI::class)
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
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.addSeveralAt(index, number, builder)
    }
    
    override fun removeAt(index: UInt) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAt(index)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllThatIndexed(predicate: (UInt, Element) -> Boolean) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAllThatIndexed(predicate)
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
     * Adds the given [element] to the builder.
     *
     * @receiver The element to add.
     */
    public operator fun Element.unaryPlus() {
        val result = result ?: error("This KoneList builder is already used")
        result.add(this)
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
@OptIn(DelicateCollectionsInheritanceAPI::class)
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
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        val result = result ?: error("This KoneList builder is already used")
        result.addSeveralAt(index, number, builder)
    }
    
    override fun removeAt(index: UInt) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAt(index)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllThatIndexed(predicate: (UInt, Element) -> Boolean) {
        val result = result ?: error("This KoneList builder is already used")
        result.removeAllThatIndexed(predicate)
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
     * Adds the given [element] to the builder.
     *
     * @receiver The element to add.
     */
    public operator fun Element.unaryPlus() {
        val result = result ?: error("This KoneList builder is already used")
        result.add(this)
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