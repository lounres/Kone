/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.implementations.EmptyKoneIterableList
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneSettableArrayList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public fun <E> emptyKoneIterableList(): KoneIterableList<E> = EmptyKoneIterableList

public fun <E> KoneIterableList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneIterableList<E> =
    KoneSettableArrayList(size, context, initializer)
public fun <E> KoneSettableIterableList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneSettableIterableList<E> =
    KoneSettableArrayList(size, context, initializer)
public fun <E> KoneMutableIterableList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneMutableIterableList<E> =
    KoneGrowableArrayList(size, context, initializer)

@Suppress("UNUSED_PARAMETER")
public fun <E> koneIterableListOf(context: Equality<E> = defaultEquality()): KoneIterableList<E> = emptyKoneIterableList()
@Suppress("UNCHECKED_CAST")
public fun <E> koneIterableListOf(vararg elements: E, context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>), context = context)
@Suppress("UNCHECKED_CAST")
public fun <E> koneSettableIterableListOf(vararg elements: E, context: Equality<E> = defaultEquality()): KoneSettableIterableList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>), context = context)
public fun <E> koneMutableIterableListOf(context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> =
    KoneGrowableArrayList(context = context)
public fun <E> koneMutableIterableListOf(vararg elements: E, context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> =
    KoneGrowableArrayList(elements.size.toUInt(), context = context) { elements[it.toInt()] }

public fun <E> KoneIterable<E>.toKoneMutableIterableList(context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableIterableList()

    val result = KoneGrowableArrayList<E>(context = context)
    for (element in this) result.add(element)
    return result
}
public fun <E> Iterable<E>.toKoneMutableIterableList(context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    // TODO: This call resolves to contextual function on `Iterable` instead of one without context and on `Collection`
    if (this is Collection<E>) return this.toKoneMutableIterableList(context = context)

    val result = KoneGrowableArrayList<E>(context = context)
    for (element in this) result.add(element)
    return result
}
public fun <E> KoneIterableCollection<E>.toKoneMutableIterableList(context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneGrowableArrayList(size, context = context) { iterator.next() }
}
public fun <E> Collection<E>.toKoneMutableIterableList(context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneGrowableArrayList(size.toUInt(), context = context) { iterator.next() }
}
public fun <E> KoneList<E>.toKoneMutableIterableList(context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> =
    KoneGrowableArrayList(size, context = context) { this[it] }
public fun <E> KoneIterableList<E>.toKoneMutableIterableList(context: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneGrowableArrayList(size, context = context) { iterator.next() }
}

public fun <E> KoneIterable<E>.toKoneIterableList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(this is KoneIterableCollection<E>) this.toKoneIterableList(context = context)
    else this.toKoneMutableIterableList(context = context)
public fun <E> Iterable<E>.toKoneIterableList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(this is Collection<E>) this.toKoneIterableList(context = context)
    else this.toKoneMutableIterableList(context = context)
public fun <E> KoneIterableCollection<E>.toKoneIterableList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList(context = context)
public fun <E> Collection<E>.toKoneIterableList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0) emptyKoneIterableList()
    else this.toKoneMutableIterableList(context = context)
public fun <E> KoneList<E>.toKoneIterableList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList(context = context)
public fun <E> KoneIterableList<E>.toKoneIterableList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList(context = context)

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableList(context: Equality<E> = defaultEquality(), @BuilderInference builderAction: KoneMutableIterableList<E>.() -> Unit): KoneIterableList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableIterableListOf<E>(context = context).apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableList(initialCapacity: UInt, context: Equality<E> = defaultEquality(), @BuilderInference builderAction: KoneMutableIterableList<E>.() -> Unit): KoneIterableList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneGrowableArrayList<E>(initialCapacity, context = context).apply(builderAction)
}