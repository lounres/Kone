/*
 * Copyright © 2025 Gleb Minaev
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
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableNoddedList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableNoddedList
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableNoddedList
import dev.lounres.kone.collections.utils.toOptimizedList
import dev.lounres.kone.collections.utils.toOptimizedNoddedList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders for nodded lists

/**
 * Returns empty list.
 */
public fun <Element> KoneList.Companion.empty(): KoneList<Element> = KoneEmptySettableNoddedList

public fun <Element> KoneNoddedList.Companion.empty(): KoneNoddedList<Element> = KoneEmptySettableNoddedList

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableList.Companion.empty(): KoneSettableList<Element> = KoneEmptySettableNoddedList as KoneSettableList<Element>

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableNoddedList.Companion.empty(): KoneSettableNoddedList<Element> = KoneEmptySettableNoddedList as KoneSettableNoddedList<Element>

/**
 * Returns a list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneList(size: UInt, initializer: (index: UInt) -> Element): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else KoneArraySettableList(size, initializer)

public inline fun <Element> KoneNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneNoddedList<Element> =
    if (size == 0u) KoneNoddedList.empty()
    else KoneArraySettableNoddedList(size, initializer)

/**
 * Returns a settable list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneSettableList(size: UInt, initializer: (index: UInt) -> Element): KoneSettableList<Element> =
    if (size == 0u) KoneSettableList.empty()
    else KoneArraySettableList(size, initializer)

public inline fun <Element> KoneSettableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneSettableNoddedList<Element> =
    if (size == 0u) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList(size, initializer)

/**
 * Returns a mutable list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneMutableList(size: UInt, initializer: (index: UInt) -> Element): KoneMutableList<Element> =
    KoneArrayResizableList(size, initializer)

public inline fun <Element> KoneMutableNoddedList(size: UInt, initializer: (index: UInt) -> Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList(size, initializer)

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
    KoneArraySettableNoddedList(elements.size.toUInt()) { elements[it.toInt()] }

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableList.Companion.of(vararg elements: Element): KoneSettableList<Element> =
    if (elements.isEmpty()) KoneSettableList.empty()
    else KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableNoddedList.Companion.of(vararg elements: Element): KoneSettableNoddedList<Element> =
    if (elements.isEmpty()) KoneSettableNoddedList.empty()
    else KoneArraySettableNoddedList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneMutableList.Companion.of(): KoneMutableList<Element> =
    KoneArrayResizableList()

public fun <Element> KoneMutableList.Companion.of(vararg elements: Element): KoneMutableList<Element> =
    KoneArrayResizableList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneMutableNoddedList.Companion.of(): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList()

public fun <Element> KoneMutableNoddedList.Companion.of(vararg elements: Element): KoneMutableNoddedList<Element> =
    KoneArrayResizableNoddedList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneIterator<Element>.toKoneMutableList(): KoneMutableList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneMutableList(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneMutableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneIterator<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneMutableNoddedList(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneMutableNoddedList(): KoneMutableNoddedList<Element> {
    val iterator = iterator()
    return KoneMutableNoddedList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneIterator<Element>.toKoneSettableList(): KoneSettableList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneSettableList(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneIterator<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.let {
        KoneSettableNoddedList(it.size) { index -> it[index] }
    }

public fun <Element> KoneIterable<Element>.toKoneSettableNoddedList(): KoneSettableNoddedList<Element> {
    val iterator = iterator()
    return KoneSettableNoddedList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneIterator<Element>.toKoneList(): KoneList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.toOptimizedList()

public fun <Element> KoneIterable<Element>.toKoneList(): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else this.toKoneMutableList().toOptimizedList()

public fun <Element> KoneIterator<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    KoneArrayGrowableList<Element>().apply {
        while (hasNext()) add(getAndMoveNext())
    }.toOptimizedNoddedList()

public fun <Element> KoneIterable<Element>.toKoneNoddedList(): KoneNoddedList<Element> =
    if (size == 0u) KoneNoddedList.empty()
    else this.toKoneMutableList().toOptimizedNoddedList()

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneListBuilder<Element> @PublishedApi internal constructor(result: KoneMutableList<Element>) : KoneMutableList<Element> {
    private var result: KoneMutableList<Element>? = result
    
    override val size: UInt get() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.size
    }
    
    override fun get(index: UInt): Element {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result[index]
    }
    
    override fun set(index: UInt, element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result[index] = element
    }
    
    override fun add(element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(element)
    }
    
    override fun addAt(index: UInt, element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addAt(index, element)
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addSeveralAt(index, number, builder)
    }
    
    override fun removeAt(index: UInt) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAt(index)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllThatIndexed(predicate: (UInt, Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAllThatIndexed(predicate)
    }
    
    override fun iterator(): KoneMutableListIterator<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.iterator()
    }
    
    override fun iteratorFrom(index: UInt): KoneMutableListIterator<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.iteratorFrom(index)
    }
    
    public operator fun Element.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(this)
    }
    
    public operator fun KoneIterable<Element>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneList<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneList.Companion.build(@BuilderInference builderAction: KoneListBuilder<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneListBuilder(KoneMutableList.of<Element>()).apply(builderAction).build().toOptimizedList()
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneList.Companion.build(initialCapacity: UInt, @BuilderInference builderAction: KoneListBuilder<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneListBuilder(KoneArrayGrowableList<Element>(initialCapacity)).apply(builderAction).build().toOptimizedList()
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneNoddedListBuilder<Element> @PublishedApi internal constructor(result: KoneMutableNoddedList<Element>) : KoneMutableNoddedList<Element> {
    private var result: KoneMutableNoddedList<Element>? = result
    
    override val size: UInt get() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.size
    }
    
    override fun get(index: UInt): Element {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result[index]
    }
    
    override fun getNode(index: UInt): KoneMutableListNode<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.getNode(index)
    }
    
    override fun set(index: UInt, element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result[index] = element
    }
    
    override fun add(element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(element)
    }
    
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.addNode(element)
    }
    
    override fun addAt(index: UInt, element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addAt(index, element)
    }
    
    override fun addNodeAt(index: UInt, element: Element): KoneMutableListNode<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.addNodeAt(index, element)
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun addSeveralAt(index: UInt, number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addSeveralAt(index, number, builder)
    }
    
    override fun removeAt(index: UInt) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAt(index)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllThatIndexed(predicate: (UInt, Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAllThatIndexed(predicate)
    }
    
    override fun iterator(): KoneMutableNoddedListIterator<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.iterator()
    }
    
    override fun iteratorFrom(index: UInt): KoneMutableNoddedListIterator<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.iteratorFrom(index)
    }
    
    public operator fun Element.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(this)
    }
    
    public operator fun KoneIterable<Element>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneList<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneNoddedList.Companion.build(@BuilderInference builderAction: KoneNoddedListBuilder<Element>.() -> Unit): KoneNoddedList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneNoddedListBuilder(KoneMutableNoddedList.of<Element>()).apply(builderAction).build().toOptimizedNoddedList()
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneNoddedList.Companion.build(initialCapacity: UInt, @BuilderInference builderAction: KoneNoddedListBuilder<Element>.() -> Unit): KoneNoddedList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneNoddedListBuilder(KoneArrayGrowableNoddedList<Element>(initialCapacity)).apply(builderAction).build().toOptimizedNoddedList()
}