/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.contextual.*
import dev.lounres.kone.collections.contextual.implementations.EmptyKoneContextualIterableSet
import dev.lounres.kone.collections.contextual.implementations.KoneContextualGrowableArrayList
import dev.lounres.kone.collections.contextual.implementations.KoneContextualListBackedSet
import dev.lounres.kone.collections.contextual.implementations.KoneContextualMutableListBackedSet
import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public fun <E> emptyKoneContextualIterableSet(): KoneContextualIterableSet<E, Equality<E>> = EmptyKoneContextualIterableSet

// TODO: Find out how to introduce hashing sets and maps
// TODO: Replace with Kone own implementations
public fun <E> koneContextualIterableSetOf(): KoneContextualIterableSet<E, Equality<E>> = emptyKoneContextualIterableSet()
context(Equality<E>)
public fun <E> koneContextualIterableSetOf(vararg elements: E): KoneContextualIterableSet<E, Equality<E>> {
    val backingList = KoneContextualGrowableArrayList<E>()
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneContextualListBackedSet(backingList)
}
//context(Hashing<E>)
//public fun <E> koneIterableSetOf(vararg elements: E): KoneIterableSet<E, Hashing<E>> = TODO()
public fun <E> koneContextualMutableIterableSetOf(): KoneContextualMutableIterableSet<E, Equality<E>> = KoneContextualMutableListBackedSet()
context(Equality<E>)
public fun <E> koneContextualMutableIterableSetOf(vararg elements: E): KoneContextualMutableIterableSet<E, Equality<E>> {
    val backingList = KoneContextualGrowableArrayList<E>()
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneContextualMutableListBackedSet(backingList)
}

context(Equality<E>)
public fun <E> KoneIterable<E>.toKoneContextualMutableIterableSet(): KoneContextualMutableIterableSet<E, Equality<E>> {
    if (this is KoneContextualIterableCollection<E, *>) return this.toKoneContextualMutableIterableSet()

    val result = koneContextualMutableIterableSetOf<E>()
    for (element in this) result.add(element)
    return result
}
context(Equality<E>)
public fun <E> Iterable<E>.toKoneContextualMutableIterableSet(): KoneContextualMutableIterableSet<E, Equality<E>> {
    if (this is Collection<E>) return this.toKoneContextualMutableIterableSet()

    val result = koneContextualMutableIterableSetOf<E>()
    for (element in this) result.add(element)
    return result
}
// TODO: Replace with Kone own implementations
context(Equality<E>)
public fun <E> KoneContextualIterableCollection<E, *>.toKoneContextualMutableIterableSet(): KoneContextualMutableIterableSet<E, Equality<E>> {
    val backingList = KoneContextualGrowableArrayList<E>()
    for (element in this) if (element !in backingList) backingList.add(element)
    return KoneContextualMutableListBackedSet(backingList)
}
context(Equality<E>)
public fun <E> Collection<E>.toKoneContextualMutableIterableSet(): KoneContextualMutableIterableSet<E, Equality<E>> {
    val backingList = KoneContextualGrowableArrayList<E>()
    for (element in this) if (element !in backingList) backingList.add(element)
    return KoneContextualMutableListBackedSet(backingList)
}
context(Equality<E>)
public fun <E> KoneContextualList<E, *>.toKoneContextualMutableIterableSet(): KoneContextualMutableIterableSet<E, Equality<E>> {
    val backingList = KoneContextualGrowableArrayList<E>()
    for (index in 0u ..< this.size) {
        val element = this[index]
        if (element !in backingList) backingList.add(element)
    }
    return KoneContextualMutableListBackedSet(backingList)
}
context(Equality<E>)
public fun <E> KoneContextualIterableList<E, *>.toKoneContextualMutableIterableSet(): KoneContextualMutableIterableSet<E, Equality<E>> {
    val backingList = KoneContextualGrowableArrayList<E>()
    for (element in this) if (element !in backingList) backingList.add(element)
    return KoneContextualMutableListBackedSet(backingList)
}

context(Equality<E>)
public fun <E> KoneIterable<E>.toKoneContextualIterableSet(): KoneContextualIterableSet<E, Equality<E>> =
    if(this is KoneContextualIterableCollection<E, *>) this.toKoneContextualIterableSet()
    else this.toKoneContextualMutableIterableSet()
context(Equality<E>)
public fun <E> Iterable<E>.toKoneContextualIterableSet(): KoneContextualIterableSet<E, Equality<E>> =
    if(this is Collection<E>) this.toKoneContextualIterableSet()
    else this.toKoneContextualMutableIterableSet()
context(Equality<E>)
public fun <E> KoneContextualIterableCollection<E, *>.toKoneContextualIterableSet(): KoneContextualIterableSet<E, Equality<E>> =
    if(size == 0u) emptyKoneContextualIterableSet()
    else this.toKoneContextualMutableIterableSet()
context(Equality<E>)
public fun <E> Collection<E>.toKoneContextualIterableSet(): KoneContextualIterableSet<E, Equality<E>> =
    if(size == 0) emptyKoneContextualIterableSet()
    else this.toKoneContextualMutableIterableSet()
context(Equality<E>)
public fun <E> KoneContextualList<E, *>.toKoneContextualIterableSet(): KoneContextualIterableSet<E, Equality<E>> =
    if(size == 0u) emptyKoneContextualIterableSet()
    else this.toKoneContextualMutableIterableSet()
context(Equality<E>)
public fun <E> KoneContextualIterableList<E, *>.toKoneContextualIterableSet(): KoneContextualIterableSet<E, Equality<E>> =
    if(size == 0u) emptyKoneContextualIterableSet()
    else this.toKoneContextualMutableIterableSet()

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneContextualIterableSet(@BuilderInference builderAction: KoneContextualMutableIterableSet<E, Equality<E>>.() -> Unit): KoneContextualIterableSet<E, Equality<E>> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneContextualMutableIterableSetOf<E>().apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneContextualIterableSet(initialCapacity: UInt, @BuilderInference builderAction: KoneContextualMutableIterableSet<E, Equality<E>>.() -> Unit): KoneContextualIterableSet<E, Equality<E>> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Replace with Kone own implementations
    return TODO()
}