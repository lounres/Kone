/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.collections.contextual.implementations.EmptyKoneContextualIterableList
import dev.lounres.kone.collections.contextual.implementations.KoneContextualGrowableArrayList
import dev.lounres.kone.collections.contextual.implementations.KoneContextualSettableArrayList
import dev.lounres.kone.comparison.Equality
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public fun <E> emptyKoneContextualIterableList(): KoneContextualIterableList<E, Equality<E>> = EmptyKoneContextualIterableList

public fun <E> KoneContextualIterableList(size: UInt, initializer: (index: UInt) -> E): KoneContextualIterableList<E, Equality<E>> = KoneContextualSettableArrayList(size, initializer)
public fun <E> KoneContextualSettableIterableList(size: UInt, initializer: (index: UInt) -> E): KoneContextualSettableIterableList<E, Equality<E>> = KoneContextualSettableArrayList(size, initializer)
public fun <E> KoneContextualMutableIterableList(size: UInt, initializer: (index: UInt) -> E): KoneContextualMutableIterableList<E, Equality<E>> = KoneContextualGrowableArrayList(size, initializer)

public fun <E> koneContextualIterableListOf(): KoneContextualIterableList<E, Equality<E>> = emptyKoneContextualIterableList()
@Suppress("UNCHECKED_CAST")
public fun <E> koneContextualIterableListOf(vararg elements: E): KoneContextualIterableList<E, Equality<E>> =
    KoneContextualSettableArrayList(KoneContextualMutableArray(elements as Array<Any?>))
@Suppress("UNCHECKED_CAST")
public fun <E> koneContextualSettableIterableListOf(vararg elements: E): KoneContextualSettableIterableList<E, Equality<E>> =
    KoneContextualSettableArrayList(KoneContextualMutableArray(elements as Array<Any?>))
public fun <E> koneContextualMutableIterableListOf(): KoneContextualMutableIterableList<E, Equality<E>> = KoneContextualGrowableArrayList()
public fun <E> koneContextualMutableIterableListOf(vararg elements: E): KoneContextualMutableIterableList<E, Equality<E>> =
    KoneContextualGrowableArrayList(elements.size.toUInt()) { elements[it.toInt()] }

public fun <E> KoneIterable<E>.toKoneContextualMutableIterableList(): KoneContextualMutableIterableList<E, Equality<E>> {
    if (this is KoneContextualIterableCollection<E, *>) return this.toKoneContextualMutableIterableList()

    val result = KoneContextualGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result
}
public fun <E> Iterable<E>.toKoneContextualMutableIterableList(): KoneContextualMutableIterableList<E, Equality<E>> {
    if (this is Collection<E>) return this.toKoneContextualMutableIterableList()

    val result = KoneContextualGrowableArrayList<E>()
    for (element in this) result.addAtTheEnd(element)
    return result
}
public fun <E> KoneContextualIterableCollection<E, *>.toKoneContextualMutableIterableList(): KoneContextualMutableIterableList<E, Equality<E>> {
    val iterator = iterator()
    return KoneContextualGrowableArrayList(size) { iterator.next() }
}
public fun <E> Collection<E>.toKoneContextualMutableIterableList(): KoneContextualMutableIterableList<E, Equality<E>> {
    val iterator = iterator()
    return KoneContextualGrowableArrayList(size.toUInt()) { iterator.next() }
}
public fun <E> KoneContextualList<E, *>.toKoneContextualMutableIterableList(): KoneContextualMutableIterableList<E, Equality<E>> =
    KoneContextualGrowableArrayList(size) { this[it] }
public fun <E> KoneContextualIterableList<E, *>.toKoneContextualMutableIterableList(): KoneContextualMutableIterableList<E, Equality<E>> {
    val iterator = iterator()
    return KoneContextualGrowableArrayList(size) { iterator.next() }
}

public fun <E> KoneIterable<E>.toKoneContextualIterableList(): KoneContextualIterableList<E, Equality<E>> =
    if(this is KoneContextualIterableCollection<E, *>) this.toKoneContextualIterableList()
    else this.toKoneContextualMutableIterableList()
public fun <E> Iterable<E>.toKoneContextualIterableList(): KoneContextualIterableList<E, Equality<E>> =
    if(this is Collection<E>) this.toKoneContextualIterableList()
    else this.toKoneContextualMutableIterableList()
public fun <E> KoneContextualIterableCollection<E, *>.toKoneContextualIterableList(): KoneContextualIterableList<E, Equality<E>> =
    if(size == 0u) emptyKoneContextualIterableList()
    else this.toKoneContextualMutableIterableList()
public fun <E> Collection<E>.toKoneContextualIterableList(): KoneContextualIterableList<E, Equality<E>> =
    if(size == 0) emptyKoneContextualIterableList()
    else this.toKoneContextualMutableIterableList()
public fun <E> KoneContextualList<E, *>.toKoneContextualIterableList(): KoneContextualIterableList<E, Equality<E>> =
    if(size == 0u) emptyKoneContextualIterableList()
    else this.toKoneContextualMutableIterableList()
public fun <E> KoneContextualIterableList<E, *>.toKoneContextualIterableList(): KoneContextualIterableList<E, Equality<E>> =
    if(size == 0u) emptyKoneContextualIterableList()
    else this.toKoneContextualMutableIterableList()

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneContextualIterableList(@BuilderInference builderAction: KoneContextualMutableIterableList<E, Equality<E>>.() -> Unit): KoneContextualIterableList<E, Equality<E>> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneContextualMutableIterableListOf<E>().apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneContextualIterableList(initialCapacity: UInt, @BuilderInference builderAction: KoneContextualMutableIterableList<E, Equality<E>>.() -> Unit): KoneContextualIterableList<E, Equality<E>> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneContextualGrowableArrayList<E>(initialCapacity).apply(builderAction)
}