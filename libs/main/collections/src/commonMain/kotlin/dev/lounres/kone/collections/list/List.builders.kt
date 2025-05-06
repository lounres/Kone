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
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.utils.toOptimizedList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName


// TODO: Add builders for nodded lists

/**
 * Returns empty list.
 */
public fun <Element> KoneList.Companion.empty(): KoneList<Element> = KoneEmptySettableNoddedList

/**
 * Returns a list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneList(size: UInt, initializer: (index: UInt) -> Element): KoneList<Element> =
    KoneArraySettableList(size, initializer)

/**
 * Returns a settable list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneSettableList(size: UInt, initializer: (index: UInt) -> Element): KoneSettableList<Element> =
    KoneArraySettableList(size, initializer)

/**
 * Returns a mutable list of provided [size] of elements produced by the [initializer].
 *
 * The element with index `i` (from `0` to [size] exclusive) is `initializer(index)`.
 * All [initializer] invocations are computed consecutively on values from `0` to [size] exclusive
 * in their order starting with `0`.
 */
public inline fun <Element> KoneMutableList(size: UInt, initializer: (index: UInt) -> Element): KoneMutableList<Element> =
    KoneArrayResizableList(size, initializer)

public fun <Element> KoneList.Companion.of(): KoneList<Element> = KoneList.empty()

@Deprecated("", replaceWith = ReplaceWith("KoneList.of()", "dev.lounres.kone.collections.KoneList", "dev.lounres.kone.collections.of"))
public fun <Element> koneListOf(element: Element): KoneList<Element> = KoneSingletonSettableList(element)

public fun <Element> KoneList.Companion.of(element: Element): KoneList<Element> = KoneSingletonSettableList(element)

@Suppress("UNCHECKED_CAST")
@Deprecated("", replaceWith = ReplaceWith("KoneList.of()", "dev.lounres.kone.collections.KoneList", "dev.lounres.kone.collections.of"))
public fun <Element> koneListOf(vararg elements: Element): KoneList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneList.Companion.of(vararg elements: Element): KoneList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

@Suppress("UNCHECKED_CAST")
@Deprecated("", replaceWith = ReplaceWith("KoneSettableList.of()", "dev.lounres.kone.collections.KoneSettableList", "dev.lounres.kone.collections.of"))
public fun <Element> koneSettableListOf(vararg elements: Element): KoneSettableList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSettableList.Companion.of(vararg elements: Element): KoneSettableList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

@Deprecated("", replaceWith = ReplaceWith("KoneMutableList.of()", "dev.lounres.kone.collections.KoneMutableList", "dev.lounres.kone.collections.of"))
public fun <Element> koneMutableListOf(): KoneMutableList<Element> =
    KoneArrayResizableList()

public fun <Element> KoneMutableList.Companion.of(): KoneMutableList<Element> =
    KoneArrayResizableList()

@Deprecated("", replaceWith = ReplaceWith("KoneMutableList.of()", "dev.lounres.kone.collections.KoneMutableList", "dev.lounres.kone.collections.of"))
public fun <Element> koneMutableListOf(vararg elements: Element): KoneMutableList<Element> =
    KoneArrayResizableList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneMutableList.Companion.of(vararg elements: Element): KoneMutableList<Element> =
    KoneArrayResizableList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> KoneIterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneArrayResizableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneIterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneIterable<Element>.toKoneList(): KoneList<Element> =
    if (size == 0u) KoneList.empty()
    else this.toKoneMutableList().toOptimizedList()

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
@Deprecated("", replaceWith = ReplaceWith("KoneList.build(builderAction)", "dev.lounres.kone.collections.KoneList", "dev.lounres.kone.collections.build"))
public inline fun <Element> buildKoneList(@BuilderInference builderAction: KoneMutableList<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneMutableList.of<Element>().apply(builderAction).toOptimizedList()
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <Element> KoneList.Companion.build(@BuilderInference builderAction: KoneMutableList<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneMutableList.of<Element>().apply(builderAction).toOptimizedList()
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneList.Companion.build(@BuilderInference builderAction: KoneListBuilder<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneListBuilder(KoneMutableList.of<Element>()).apply(builderAction).build().toOptimizedList()
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("", replaceWith = ReplaceWith("KoneList.build(initialCapacity, builderAction)", "dev.lounres.kone.collections.KoneList", "dev.lounres.kone.collections.build"))
public inline fun <Element> buildKoneList(initialCapacity: UInt, @BuilderInference builderAction: KoneMutableList<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneArrayGrowableList<Element>(initialCapacity).apply(builderAction).toOptimizedList()
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <Element> KoneList.Companion.build(initialCapacity: UInt, @BuilderInference builderAction: KoneMutableList<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneArrayGrowableList<Element>(initialCapacity).apply(builderAction).toOptimizedList()
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneList.Companion.build(initialCapacity: UInt, @BuilderInference builderAction: KoneListBuilder<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneListBuilder(KoneArrayGrowableList<Element>(initialCapacity)).apply(builderAction).build().toOptimizedList()
}