/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneEmptyNoddedList
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneResizableArrayList
import dev.lounres.kone.collections.implementations.KoneSettableArrayList
import dev.lounres.kone.collections.implementations.KoneSingletonNoddedList
import dev.lounres.kone.collections.utils.toOptimizedList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders for nodded lists
// TODO: Add converters for `KoneLinkedSet`

public fun <Element> emptyKoneList(): KoneList<Element> = KoneEmptyNoddedList

public inline fun <Element> KoneList(size: UInt, initializer: (index: UInt) -> Element): KoneList<Element> =
    KoneSettableArrayList(size, initializer)

public inline fun <Element> KoneSettableList(size: UInt, initializer: (index: UInt) -> Element): KoneSettableList<Element> =
    KoneSettableArrayList(size, initializer)

public inline fun <Element> KoneMutableList(size: UInt, initializer: (index: UInt) -> Element): KoneMutableList<Element> =
    KoneResizableArrayList(size, initializer)

public fun <Element> koneListOf(): KoneList<Element> = emptyKoneList()

public fun <Element> koneListOf(element: Element): KoneList<Element> = KoneSingletonNoddedList(element)

@Suppress("UNCHECKED_CAST")
public fun <Element> koneListOf(vararg elements: Element): KoneList<Element> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))

@Suppress("UNCHECKED_CAST")
public fun <Element> koneSettableListOf(vararg elements: Element): KoneSettableList<Element> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))

public fun <Element> koneMutableListOf(): KoneMutableList<Element> =
    KoneResizableArrayList()

public fun <Element> koneMutableListOf(vararg elements: Element): KoneMutableList<Element> =
    KoneResizableArrayList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <Element> Iterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    if (this is Collection<Element>) return this.toKoneMutableList()
    
    val result = KoneResizableArrayList<Element>()
    for (element in this) result.add(element)
    return result
}

public fun <Element> KoneIterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    if (this is KoneList<Element>) return this.toKoneMutableList()
    if (this is KoneSet<Element>) return this.toKoneMutableList()

    val result = KoneResizableArrayList<Element>()
    for (element in this) result.add(element)
    return result
}

public fun <Element> Collection<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneResizableArrayList(size.toUInt(), ) { iterator.next() }
}

public fun <Element> KoneList<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneResizableArrayList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneSet<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneResizableArrayList(size) { iterator.getAndMoveNext() }
}

public fun <Element> Iterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    if (this is Collection<Element>) return this.toKoneSettableList()
    
    val result = KoneResizableArrayList<Element>()
    for (element in this) result.add(element)
    return KoneSettableList(result.size) { result[it] }
}

public fun <Element> KoneIterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    if (this is KoneList<Element>) return this.toKoneSettableList()
    if (this is KoneSet<Element>) return this.toKoneSettableList()

    val result = KoneResizableArrayList<Element>()
    for (element in this) result.add(element)
    return KoneSettableList(result.size) { result[it] }
}

public fun <Element> Collection<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size.toUInt(), ) { iterator.next() }
}

public fun <Element> KoneList<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneSet<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size) { iterator.getAndMoveNext() }
}

public fun <Element> Iterable<Element>.toKoneList(): KoneList<Element> =
    if(this is Collection<Element>) this.toKoneList()
    else this.toKoneMutableList()

public fun <Element> KoneIterable<Element>.toKoneList(): KoneList<Element> =
    when {
        this is KoneList<Element> -> this.toKoneList()
        this is KoneSet<Element> -> this.toKoneList()
        else -> this.toKoneMutableList().toOptimizedList()
    }

public fun <Element> Collection<Element>.toKoneList(): KoneList<Element> =
    if (size == 0) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

public fun <Element> KoneList<Element>.toKoneList(): KoneList<Element> =
    if (size == 0u) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

public fun <Element> KoneSet<Element>.toKoneList(): KoneList<Element> =
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
    return KoneGrowableArrayList<Element>(initialCapacity).apply(builderAction)
}