/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.EmptyKoneList
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneResizableArrayList
import dev.lounres.kone.collections.implementations.KoneSettableArrayList
import dev.lounres.kone.collections.implementations.SingletonList
import dev.lounres.kone.collections.utils.toOptimizedList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add converters for `KoneLinkedSet`

public fun <E> emptyKoneList(): KoneList<E> = EmptyKoneList

public inline fun <E> KoneList(size: UInt, initializer: (index: UInt) -> E): KoneList<E> =
    KoneSettableArrayList(size, initializer)

public inline fun <E> KoneSettableList(size: UInt, initializer: (index: UInt) -> E): KoneSettableList<E> =
    KoneSettableArrayList(size, initializer)

public inline fun <E> KoneMutableList(size: UInt, initializer: (index: UInt) -> E): KoneMutableList<E> =
    KoneResizableArrayList(size, initializer)

public fun <E> koneListOf(): KoneList<E> = emptyKoneList()

public fun <E> koneListOf(element: E): KoneList<E> = SingletonList(element)

public fun <E> koneListOf(vararg elements: E): KoneList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))

public fun <E> koneSettableListOf(vararg elements: E): KoneSettableList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))

public fun <E> koneMutableListOf(): KoneMutableList<E> =
    KoneResizableArrayList()

public fun <E> koneMutableListOf(vararg elements: E): KoneMutableList<E> =
    KoneResizableArrayList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <E> Iterable<E>.toKoneMutableList(): KoneMutableList<E> {
    if (this is Collection<E>) return this.toKoneMutableList()
    
    val result = KoneResizableArrayList<E>()
    for (element in this) result.add(element)
    return result
}

public fun <E> KoneIterable<E>.toKoneMutableList(): KoneMutableList<E> {
    if (this is KoneList<E>) return this.toKoneMutableList()
    if (this is KoneSet<E>) return this.toKoneMutableList()

    val result = KoneResizableArrayList<E>()
    for (element in this) result.add(element)
    return result
}

public fun <E> Collection<E>.toKoneMutableList(): KoneMutableList<E> {
    val iterator = iterator()
    return KoneResizableArrayList(size.toUInt(), ) { iterator.next() }
}

public fun <E> KoneList<E>.toKoneMutableList(): KoneMutableList<E> {
    val iterator = iterator()
    return KoneResizableArrayList(size) { iterator.getAndMoveNext() }
}

public fun <E> KoneSet<E>.toKoneMutableList(): KoneMutableList<E> {
    val iterator = iterator()
    return KoneResizableArrayList(size) { iterator.getAndMoveNext() }
}

public fun <E> Iterable<E>.toKoneSettableList(): KoneSettableList<E> {
    if (this is Collection<E>) return this.toKoneSettableList()
    
    val result = KoneResizableArrayList<E>()
    for (element in this) result.add(element)
    return KoneSettableList(result.size) { result[it] }
}

public fun <E> KoneIterable<E>.toKoneSettableList(): KoneSettableList<E> {
    if (this is KoneList<E>) return this.toKoneSettableList()
    if (this is KoneSet<E>) return this.toKoneSettableList()

    val result = KoneResizableArrayList<E>()
    for (element in this) result.add(element)
    return KoneSettableList(result.size) { result[it] }
}

public fun <E> Collection<E>.toKoneSettableList(): KoneSettableList<E> {
    val iterator = iterator()
    return KoneSettableList(size.toUInt(), ) { iterator.next() }
}

public fun <E> KoneList<E>.toKoneSettableList(): KoneSettableList<E> {
    val iterator = iterator()
    return KoneSettableList(size) { iterator.getAndMoveNext() }
}

public fun <E> KoneSet<E>.toKoneSettableList(): KoneSettableList<E> {
    val iterator = iterator()
    return KoneSettableList(size) { iterator.getAndMoveNext() }
}

public fun <E> Iterable<E>.toKoneList(): KoneList<E> =
    if(this is Collection<E>) this.toKoneList()
    else this.toKoneMutableList()

public fun <E> KoneIterable<E>.toKoneList(): KoneList<E> =
    when {
        this is KoneList<E> -> this.toKoneList()
        this is KoneSet<E> -> this.toKoneList()
        else -> this.toKoneMutableList().toOptimizedList()
    }

public fun <E> Collection<E>.toKoneList(): KoneList<E> =
    if (size == 0) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

public fun <E> KoneList<E>.toKoneList(): KoneList<E> =
    if (size == 0u) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

public fun <E> KoneSet<E>.toKoneList(): KoneList<E> =
    if (size == 0u) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneList(@BuilderInference builderAction: KoneMutableList<E>.() -> Unit): KoneList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableListOf<E>().apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneList(initialCapacity: UInt, @BuilderInference builderAction: KoneMutableList<E>.() -> Unit): KoneList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneGrowableArrayList<E>(initialCapacity).apply(builderAction)
}