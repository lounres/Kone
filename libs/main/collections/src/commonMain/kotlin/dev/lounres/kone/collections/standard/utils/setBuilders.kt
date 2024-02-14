/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.standard.*
import dev.lounres.kone.collections.standard.implementations.EmptyKoneIterableSet
import dev.lounres.kone.collections.standard.wrappers.asKone
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public fun <E> emptyKoneIterableSet(): KoneIterableSet<E> = EmptyKoneIterableSet

// TODO: Replace with Kone own implementations
public fun <E> koneIterableSetOf(): KoneIterableSet<E> = emptyKoneIterableSet()
public fun <E> koneIterableSetOf(vararg elements: E): KoneIterableSet<E> = setOf(*elements).asKone()
public fun <E> koneMutableIterableSetOf(): KoneMutableIterableSet<E> = mutableSetOf<E>().asKone()
public fun <E> koneMutableIterableSetOf(vararg elements: E): KoneMutableIterableSet<E> = mutableSetOf(*elements).asKone()

public fun <E> KoneIterable<E>.toKoneMutableIterableSet(): KoneMutableIterableSet<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableIterableSet()

    val result = koneMutableIterableSetOf<E>()
    for (element in this) result.add(element)
    return result
}
public fun <E> Iterable<E>.toKoneMutableIterableSet(): KoneMutableIterableSet<E> {
    if (this is Collection<E>) return this.toKoneMutableIterableSet()

    val result = koneMutableIterableSetOf<E>()
    for (element in this) result.add(element)
    return result
}
// TODO: Replace with Kone own implementations
public fun <E> KoneIterableCollection<E>.toKoneMutableIterableSet(): KoneMutableIterableSet<E> =
    LinkedHashSet<E>(size.toInt()).asKone().apply { for (element in this@toKoneMutableIterableSet) add(element) }
public fun <E> Collection<E>.toKoneMutableIterableSet(): KoneMutableIterableSet<E> =
    LinkedHashSet<E>(size).asKone().apply { for (element in this@toKoneMutableIterableSet) add(element) }
public fun <E> KoneList<E>.toKoneMutableIterableSet(): KoneMutableIterableSet<E> =
    LinkedHashSet<E>(size.toInt()).asKone().apply { for (index in this@toKoneMutableIterableSet.indices) add(this@toKoneMutableIterableSet[index]) }
public fun <E> KoneIterableList<E>.toKoneMutableIterableSet(): KoneMutableIterableSet<E> =
    LinkedHashSet<E>(size.toInt()).asKone().apply { for (element in this) add(element) }

public fun <E> KoneIterable<E>.toKoneIterableSet(): KoneIterableSet<E> =
    if(this is KoneIterableCollection<E>) this.toKoneIterableSet()
    else this.toKoneMutableIterableSet()
public fun <E> Iterable<E>.toKoneIterableSet(): KoneIterableSet<E> =
    if(this is Collection<E>) this.toKoneIterableSet()
    else this.toKoneMutableIterableSet()
public fun <E> KoneIterableCollection<E>.toKoneIterableSet(): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet()
public fun <E> Collection<E>.toKoneIterableSet(): KoneIterableSet<E> =
    if(size == 0) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet()
public fun <E> KoneList<E>.toKoneIterableSet(): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet()
public fun <E> KoneIterableList<E>.toKoneIterableSet(): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet()

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(@BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableIterableSetOf<E>().apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(initialCapacity: UInt, @BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Replace with Kone own implementations
    return LinkedHashSet<E>(initialCapacity.toInt()).asKone().apply(builderAction)
}