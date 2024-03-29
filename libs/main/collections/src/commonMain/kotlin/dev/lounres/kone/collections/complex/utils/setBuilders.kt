/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.complex.*
import dev.lounres.kone.collections.complex.implementations.EmptyKoneIterableSet
import dev.lounres.kone.collections.complex.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.complex.implementations.KoneListBackedSet
import dev.lounres.kone.collections.complex.implementations.KoneMutableListBackedSet
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


public fun <E> emptyKoneIterableSet(context: Equality<E>): KoneIterableSet<E> =
    EmptyKoneIterableSet(context = context)
context(Equality<E>)
public fun <E> emptyKoneIterableSet(): KoneIterableSet<E> = EmptyKoneIterableSet(context = this@Equality)

// TODO: Replace with Kone own implementations
public fun <E> koneIterableSetOf(context: Equality<E>): KoneIterableSet<E> = emptyKoneIterableSet(context = context)
context(Equality<E>)
public fun <E> koneIterableSetOf(): KoneIterableSet<E> = emptyKoneIterableSet(context = this@Equality)
public fun <E> koneIterableSetOf(vararg elements: E, context: Equality<E>): KoneIterableSet<E> {
    // TODO: Uncomment when hashing sets will be ready
//    if (context is Hashing<E>) return koneIterhableSetOf(*elements, context = context)
    val backingList = KoneGrowableArrayList<E>(context = context)
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneListBackedSet(context = context, backingList)
}
context(Equality<E>)
public fun <E> koneIterableSetOf(vararg elements: E): KoneIterableSet<E> {
    // TODO: Uncomment when hashing sets will be ready
//    if (this@Equality is Hashing<E>) return koneIterableSetOf(*elements, context = this@Equality)
    val backingList = KoneGrowableArrayList<E>(context = this@Equality)
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneListBackedSet(context = this@Equality, backingList)
}
public fun <E> koneIterableSetOf(vararg elements: E, context: Hashing<E>): KoneIterableSet<E> {
    TODO("Not yet implemented")
}
context(Hashing<E>)
public fun <E> koneIterableSetOf(vararg elements: E): KoneIterableSet<E> {
    TODO("Not yet implemented")
}
public fun <E> koneMutableIterableSetOf(context: Equality<E>): KoneMutableIterableSet<E> =
    KoneMutableListBackedSet(context = context)
context(Equality<E>)
public fun <E> koneMutableIterableSetOf(): KoneMutableIterableSet<E> =
    KoneMutableListBackedSet(context = this@Equality)
public fun <E> koneMutableIterableSetOf(context: Hashing<E>): KoneMutableIterableSet<E> = KoneMutableListBackedSet(context = context)
context(Hashing<E>)
public fun <E> koneMutableIterableSetOf(): KoneMutableIterableSet<E> = KoneMutableListBackedSet(context = this@Hashing)
public fun <E> koneMutableIterableSetOf(vararg elements: E, context: Equality<E>): KoneMutableIterableSet<E> {
    val backingList = KoneGrowableArrayList<E>(context = context)
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneMutableListBackedSet(context = context, backingList)
}
context(Equality<E>)
public fun <E> koneMutableIterableSetOf(vararg elements: E): KoneMutableIterableSet<E> {
    val backingList = KoneGrowableArrayList<E>(context = this@Equality)
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneMutableListBackedSet(context = this@Equality, backingList)
}
public fun <E> koneMutableIterableSetOf(vararg elements: E, context: Hashing<E>): KoneMutableIterableSet<E> {
    val backingList = KoneGrowableArrayList<E>(context = context)
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneMutableListBackedSet(context = context, backingList)
}
context(Hashing<E>)
public fun <E> koneMutableIterableSetOf(vararg elements: E): KoneMutableIterableSet<E> {
    val backingList = KoneGrowableArrayList<E>(context = this@Hashing)
    for (element in elements) if (element !in backingList) backingList.add(element)
    return KoneMutableListBackedSet(context = this@Hashing, backingList)
}

public fun <E> KoneIterable<E>.toKoneMutableIterableSet(context: Equality<E>): KoneMutableIterableSet<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableIterableSet(context = context)

    val result = koneMutableIterableSetOf<E>(context = context)
    for (element in this) result.add(element)
    return result
}
public fun <E> KoneIterable<E>.toKoneMutableIterableSet(context: Hashing<E>): KoneMutableIterableSet<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableIterableSet(context = context)

    val result = koneMutableIterableSetOf<E>(context = context)
    for (element in this) result.add(element)
    return result
}
public fun <E> Iterable<E>.toKoneMutableIterableSet(context: Equality<E>): KoneMutableIterableSet<E> {
    if (this is Collection<E>) return this.toKoneMutableIterableSet(context = context)

    val result = koneMutableIterableSetOf<E>(context = context)
    for (element in this) result.add(element)
    return result
}
public fun <E> Iterable<E>.toKoneMutableIterableSet(context: Hashing<E>): KoneMutableIterableSet<E> {
    if (this is Collection<E>) return this.toKoneMutableIterableSet(context = context)

    val result = koneMutableIterableSetOf<E>(context = context)
    for (element in this) result.add(element)
    return result
}
// TODO: Replace with Kone own implementations
public fun <E> KoneIterableCollection<E>.toKoneMutableIterableSet(context: Equality<E> = this.context): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")
public fun <E> KoneIterableCollection<E>.toKoneMutableIterableSet(context: Hashing<E> = TODO("Define receiver's context")): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")
public fun <E> Collection<E>.toKoneMutableIterableSet(context: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")
public fun <E> Collection<E>.toKoneMutableIterableSet(context: Hashing<E> = defaultHashing()): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")
public fun <E> KoneList<E>.toKoneMutableIterableSet(context: Equality<E> = this.context): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")
public fun <E> KoneList<E>.toKoneMutableIterableSet(context: Hashing<E> = TODO("Define receiver's context")): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")
public fun <E> KoneIterableList<E>.toKoneMutableIterableSet(context: Equality<E> = this.context): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")
public fun <E> KoneIterableList<E>.toKoneMutableIterableSet(context: Hashing<E> = TODO("Define receiver's context")): KoneMutableIterableSet<E> =
    TODO("Not yet implemented")

public fun <E> KoneIterable<E>.toKoneIterableSet(context: Equality<E>): KoneIterableSet<E> =
    if(this is KoneIterableCollection<E>) this.toKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> KoneIterable<E>.toKoneIterableSet(context: Hashing<E>): KoneIterableSet<E> =
    if(this is KoneIterableCollection<E>) this.toKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> Iterable<E>.toKoneIterableSet(context: Equality<E>): KoneIterableSet<E> =
    if(this is Collection<E>) this.toKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> Iterable<E>.toKoneIterableSet(context: Hashing<E>): KoneIterableSet<E> =
    if(this is Collection<E>) this.toKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> KoneIterableCollection<E>.toKoneIterableSet(context: Equality<E> = this.context): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> KoneIterableCollection<E>.toKoneIterableSet(context: Hashing<E> = TODO("Define receiver's context")): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> Collection<E>.toKoneIterableSet(context: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if(size == 0) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> Collection<E>.toKoneIterableSet(context: Hashing<E> = defaultHashing()): KoneIterableSet<E> =
    if(size == 0) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> KoneList<E>.toKoneIterableSet(context: Equality<E> = this.context): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> KoneList<E>.toKoneIterableSet(context: Hashing<E> = TODO("Define receiver's context")): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> KoneIterableList<E>.toKoneIterableSet(context: Equality<E> = this.context): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)
public fun <E> KoneIterableList<E>.toKoneIterableSet(context: Hashing<E> = TODO("Define receiver's context")): KoneIterableSet<E> =
    if(size == 0u) emptyKoneIterableSet(context = context)
    else this.toKoneMutableIterableSet(context = context)

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(context: Equality<E>, @BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableIterableSetOf<E>(context = context).apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(context: Hashing<E>, @BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableIterableSetOf<E>(context = context).apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(context: Equality<E>, initialCapacity: UInt, @BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Replace with Kone own implementations
    return TODO()
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(context: Hashing<E>, initialCapacity: UInt, @BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Replace with Kone own implementations
    return TODO()
}