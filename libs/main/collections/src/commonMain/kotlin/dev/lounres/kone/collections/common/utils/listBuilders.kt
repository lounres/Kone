/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.common.*
import dev.lounres.kone.collections.common.implementations.EmptyKoneIterableList
import dev.lounres.kone.collections.common.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.common.implementations.KoneSettableArrayList
import dev.lounres.kone.collections.next
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public fun <E> emptyKoneIterableList(): KoneIterableList<E> = EmptyKoneIterableList

public fun <E> KoneIterableList(size: UInt, initializer: (index: UInt) -> E): KoneIterableList<E> = KoneSettableArrayList(size, initializer)
public fun <E> KoneSettableIterableList(size: UInt, initializer: (index: UInt) -> E): KoneSettableIterableList<E> = KoneSettableArrayList(size, initializer)
public fun <E> KoneMutableIterableList(size: UInt, initializer: (index: UInt) -> E): KoneMutableIterableList<E> = KoneGrowableArrayList(size, initializer)

public fun <E> koneIterableListOf(): KoneIterableList<E> = emptyKoneIterableList()
@Suppress("UNCHECKED_CAST")
public fun <E> koneIterableListOf(vararg elements: E): KoneIterableList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))
@Suppress("UNCHECKED_CAST")
public fun <E> koneSettableIterableListOf(vararg elements: E): KoneSettableIterableList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))
public fun <E> koneMutableIterableListOf(): KoneMutableIterableList<E> = KoneGrowableArrayList()
public fun <E> koneMutableIterableListOf(vararg elements: E): KoneMutableIterableList<E> =
    KoneGrowableArrayList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <E> KoneIterable<E>.toKoneMutableIterableList(): KoneMutableIterableList<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableIterableList()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result
}
public fun <E> Iterable<E>.toKoneMutableIterableList(): KoneMutableIterableList<E> {
    if (this is Collection<E>) return this.toKoneMutableIterableList()

    val result = KoneGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result
}
public fun <E> KoneIterableCollection<E>.toKoneMutableIterableList(): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneGrowableArrayList(size) { iterator.next() }
}
public fun <E> Collection<E>.toKoneMutableIterableList(): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneGrowableArrayList(size.toUInt()) { iterator.next() }
}
public fun <E> KoneList<E>.toKoneMutableIterableList(): KoneMutableIterableList<E> =
    KoneGrowableArrayList(size) { this[it] }
public fun <E> KoneIterableList<E>.toKoneMutableIterableList(): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneGrowableArrayList(size) { iterator.next() }
}

public fun <E> KoneIterable<E>.toKoneIterableList(): KoneIterableList<E> =
    if(this is KoneIterableCollection<E>) this.toKoneIterableList()
    else this.toKoneMutableIterableList()
public fun <E> Iterable<E>.toKoneIterableList(): KoneIterableList<E> =
    if(this is Collection<E>) this.toKoneIterableList()
    else this.toKoneMutableIterableList()
public fun <E> KoneIterableCollection<E>.toKoneIterableList(): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList()
public fun <E> Collection<E>.toKoneIterableList(): KoneIterableList<E> =
    if(size == 0) emptyKoneIterableList()
    else this.toKoneMutableIterableList()
public fun <E> KoneList<E>.toKoneIterableList(): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList()
public fun <E> KoneIterableList<E>.toKoneIterableList(): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList()

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableList(@BuilderInference builderAction: KoneMutableIterableList<E>.() -> Unit): KoneIterableList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableIterableListOf<E>().apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableList(initialCapacity: UInt, @BuilderInference builderAction: KoneMutableIterableList<E>.() -> Unit): KoneIterableList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneGrowableArrayList<E>(initialCapacity).apply(builderAction)
}