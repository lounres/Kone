/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.implementations.KoneEmptySettableNoddedList
import dev.lounres.kone.collections.implementations.KoneArrayResizableList
import dev.lounres.kone.collections.implementations.KoneArraySettableList
import dev.lounres.kone.collections.implementations.KoneSingletonSettableNoddedList
import dev.lounres.kone.collections.utils.toOptimizedList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders for nodded lists

public fun <Element> emptyKoneList(): KoneList<Element> = KoneEmptySettableNoddedList

public inline fun <Element> KoneList(size: UInt, initializer: (index: UInt) -> Element): KoneList<Element> =
    KoneArraySettableList(size, initializer)

public inline fun <Element> KoneSettableList(size: UInt, initializer: (index: UInt) -> Element): KoneSettableList<Element> =
    KoneArraySettableList(size, initializer)

public inline fun <Element> KoneMutableList(size: UInt, initializer: (index: UInt) -> Element): KoneMutableList<Element> =
    KoneArrayResizableList(size, initializer)

public fun <Element> koneListOf(): KoneList<Element> = emptyKoneList()

public fun <Element> koneListOf(element: Element): KoneList<Element> = KoneSingletonSettableNoddedList(element)

@Suppress("UNCHECKED_CAST")
public fun <Element> koneListOf(vararg elements: Element): KoneList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

@Suppress("UNCHECKED_CAST")
public fun <Element> koneSettableListOf(vararg elements: Element): KoneSettableList<Element> =
    KoneArraySettableList(KoneMutableArray(elements as Array<Any?>))

public fun <Element> koneMutableListOf(): KoneMutableList<Element> =
    KoneArrayResizableList()

public fun <Element> koneMutableListOf(vararg elements: Element): KoneMutableList<Element> =
    KoneArrayResizableList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> Iterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    if (this is Collection<Element>) return this.toKoneMutableList()
    
    val result = KoneArrayResizableList<Element>()
    for (element in this) result.add(element)
    return result
}

public fun <Element> Collection<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneArrayResizableList(size.toUInt(), ) { iterator.next() }
}

public fun <Element> KoneIterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneArrayResizableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> Iterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    if (this is Collection<Element>) return this.toKoneSettableList()
    
    val result = KoneArrayResizableList<Element>()
    for (element in this) result.add(element)
    return KoneSettableList(result.size) { result[it] }
}

public fun <Element> Collection<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size.toUInt(), ) { iterator.next() }
}

public fun <Element> KoneIterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> Iterable<Element>.toKoneList(): KoneList<Element> =
    if(this is Collection<Element>) this.toKoneList()
    else this.toKoneMutableList()

public fun <Element> Collection<Element>.toKoneList(): KoneList<Element> =
    if (size == 0) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

public fun <Element> KoneIterable<Element>.toKoneList(): KoneList<Element> =
    if (size == 0u) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneList(@BuilderInference builderAction: KoneMutableList<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableListOf<Element>().apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneList(initialCapacity: UInt, @BuilderInference builderAction: KoneMutableList<Element>.() -> Unit): KoneList<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneArrayGrowableList<Element>(initialCapacity).apply(builderAction)
}