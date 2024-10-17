/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.EmptyKoneIterableList
import dev.lounres.kone.collections.implementations.EmptyKoneIterableSet
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneListBackedSet
import dev.lounres.kone.collections.implementations.KoneMutableListBackedSet
import dev.lounres.kone.collections.implementations.KoneResizableArrayList
import dev.lounres.kone.collections.implementations.KoneResizableHashSet
import dev.lounres.kone.collections.implementations.KoneResizableLinkedArrayList
import dev.lounres.kone.collections.implementations.KoneSettableArrayList
import dev.lounres.kone.collections.implementations.SingletonList
import dev.lounres.kone.collections.utils.indices
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// region Lists

public fun <E> emptyKoneIterableList(): KoneIterableList<E> = EmptyKoneIterableList

public inline fun <E> KoneIterableList(size: UInt, elementContext: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneIterableList<E> =
    KoneSettableArrayList(size, elementContext, initializer)

public inline fun <E> KoneSettableIterableList(size: UInt, elementContext: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneSettableIterableList<E> =
    KoneSettableArrayList(size, elementContext, initializer)

public inline fun <E> KoneMutableIterableList(size: UInt, elementContext: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneMutableIterableList<E> =
    KoneResizableArrayList(size, elementContext, initializer)

@Suppress("UNUSED_PARAMETER")
public fun <E> koneIterableListOf(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> = emptyKoneIterableList()

public fun <E> koneIterableListOf(element: E, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> = SingletonList(element, elementContext)

public fun <E> koneIterableListOf(vararg elements: E, elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>), elementContext = elementContext)

public fun <E> koneSettableIterableListOf(vararg elements: E, elementContext: Equality<E> = defaultEquality()): KoneSettableIterableList<E> =
    KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>), elementContext = elementContext)

public fun <E> koneMutableIterableListOf(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> =
    KoneResizableArrayList(elementContext = elementContext)

public fun <E> koneMutableIterableListOf(vararg elements: E, elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> =
    KoneResizableArrayList(elements.size.toUInt(), elementContext = elementContext) { elements[it.toInt()] }

public fun <E> KoneIterable<E>.toKoneMutableIterableList(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableIterableList()

    val result = KoneResizableArrayList(elementContext = elementContext)
    for (element in this) result.add(element)
    return result
}

public fun <E> Iterable<E>.toKoneMutableIterableList(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    if (this is Collection<E>) return this.toKoneMutableIterableList(elementContext = elementContext)

    val result = KoneResizableArrayList(elementContext = elementContext)
    for (element in this) result.add(element)
    return result
}

public fun <E> KoneIterableCollection<E>.toKoneMutableIterableList(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneResizableArrayList(size, elementContext = elementContext) { iterator.next() }
}

public fun <E> Collection<E>.toKoneMutableIterableList(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneResizableArrayList(size.toUInt(), elementContext = elementContext) { iterator.next() }
}

public fun <E> KoneList<E>.toKoneMutableIterableList(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> =
    KoneResizableArrayList(size, elementContext = elementContext) { this[it] }

public fun <E> KoneIterableList<E>.toKoneMutableIterableList(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableList<E> {
    val iterator = iterator()
    return KoneResizableArrayList(size, elementContext = elementContext) { iterator.next() }
}

public fun <E> KoneIterable<E>.toKoneIterableList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(this is KoneIterableCollection<E>) this.toKoneIterableList(elementContext = elementContext)
    else this.toKoneMutableIterableList(elementContext = elementContext)

public fun <E> Iterable<E>.toKoneIterableList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(this is Collection<E>) this.toKoneIterableList(elementContext = elementContext)
    else this.toKoneMutableIterableList(elementContext = elementContext)

public fun <E> KoneIterableCollection<E>.toKoneIterableList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList(elementContext = elementContext)

public fun <E> Collection<E>.toKoneIterableList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0) emptyKoneIterableList()
    else this.toKoneMutableIterableList(elementContext = elementContext)

public fun <E> KoneList<E>.toKoneIterableList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList(elementContext = elementContext)

public fun <E> KoneIterableList<E>.toKoneIterableList(elementContext: Equality<E> = defaultEquality()): KoneIterableList<E> =
    if(size == 0u) emptyKoneIterableList()
    else this.toKoneMutableIterableList(elementContext = elementContext)

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableList(elementContext: Equality<E> = defaultEquality(), @BuilderInference builderAction: KoneMutableIterableList<E>.() -> Unit): KoneIterableList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return koneMutableIterableListOf<E>(elementContext = elementContext).apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableList(initialCapacity: UInt, elementContext: Equality<E> = defaultEquality(), @BuilderInference builderAction: KoneMutableIterableList<E>.() -> Unit): KoneIterableList<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return KoneResizableArrayList(initialCapacity, elementContext = elementContext).apply(builderAction)
}

// endregion

// region Sets

public fun <E> emptyKoneIterableSet(): KoneIterableSet<E> = EmptyKoneIterableSet

@Suppress("UNUSED_PARAMETER")
public fun <E> koneIterableSetOf(elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> = emptyKoneIterableSet()

// TODO: Add single-element implementations

public fun <E> koneIterableSetOf(vararg elements: E, elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext).apply { addAll(*elements) }
    else {
        val backingList = KoneGrowableArrayList(elementContext = elementContext)
        for (element in elements) if (element !in backingList) backingList.add(element)
        KoneListBackedSet(elementContext, backingList)
    }

public fun <E> koneMutableIterableSetOf(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> =
    if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext)
    else KoneMutableListBackedSet(elementContext = elementContext)

public fun <E> koneMutableIterableSetOf(vararg elements: E, elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> =
    if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext).apply { addAll(*elements) }
    else {
        val backingList = KoneResizableLinkedArrayList(elementContext = elementContext)
        for (element in elements) if (element !in backingList) backingList.add(element)
        KoneMutableListBackedSet(elementContext, backingList)
    }

public fun <E> KoneIterable<E>.toKoneMutableIterableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> {
    if (this is KoneIterableCollection<E>) return this.toKoneMutableIterableSet(elementContext = elementContext)

    val result = koneMutableIterableSetOf<E>(elementContext = elementContext)
    for (element in this) result.add(element)
    return result
}

public fun <E> Iterable<E>.toKoneMutableIterableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> {
    if (this is Collection<E>) return this.toKoneMutableIterableSet(elementContext = elementContext)

    val result = koneMutableIterableSetOf<E>(elementContext = elementContext)
    for (element in this) result.add(element)
    return result
}

public fun <E> KoneIterableCollection<E>.toKoneMutableIterableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> =
    if (elementContext is Hashing<E>)
        KoneResizableHashSet(elementContext = elementContext).apply { addAllFrom(this@toKoneMutableIterableSet) }
    else {
        val backingList = KoneResizableArrayList(elementContext = elementContext)
        for (element in this) if (element !in backingList) backingList.add(element)
        KoneMutableListBackedSet(elementContext, KoneResizableLinkedArrayList(elementContext).apply { addAllFrom(backingList) })
    }

public fun <E> Collection<E>.toKoneMutableIterableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> =
    if (elementContext is Hashing<E>)
        KoneResizableHashSet(elementContext = elementContext).apply {
            for (element in this@toKoneMutableIterableSet) add(element)
        }
    else {
        val backingList = KoneResizableArrayList(elementContext = elementContext)
        for (element in this) if (element !in backingList) backingList.add(element)
        KoneMutableListBackedSet(elementContext, KoneResizableLinkedArrayList(elementContext).apply { addAllFrom(backingList) })
    }

public fun <E> KoneList<E>.toKoneMutableIterableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> =
    if (elementContext is Hashing<E>)
        KoneResizableHashSet(elementContext = elementContext).apply {
            for (index in 0u ..< this@toKoneMutableIterableSet.size) this.add(this@toKoneMutableIterableSet[index])
        }
    else {
        val backingList = KoneResizableArrayList(elementContext = elementContext)
        for (index in indices) {
            val element = this[index]
            if (element !in backingList) backingList.add(element)
        }
        KoneMutableListBackedSet(elementContext, backingList)
    }

public fun <E> KoneIterableList<E>.toKoneMutableIterableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableIterableSet<E> =
    if (elementContext is Hashing<E>)
        KoneResizableHashSet(elementContext = elementContext).apply { addAllFrom(this@toKoneMutableIterableSet) }
    else {
        val backingList = KoneResizableArrayList(elementContext = elementContext)
        for (element in this) if (element !in backingList) backingList.add(element)
        KoneMutableListBackedSet(elementContext, backingList)
    }

// TODO: Replace inner implementation of `toKoneIterableSet`
//  via KoneGrowableArrayList and KoneResizableHashSet
//  with KoneFixedCapacityList and fixed capacity analogue of KoneResizableHashSet
public fun <E> KoneIterable<E>.toKoneIterableSet(elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if (this is KoneIterableCollection<E>) this.toKoneIterableSet(elementContext = elementContext)
    else this.toKoneMutableIterableSet(elementContext = elementContext)

public fun <E> Iterable<E>.toKoneIterableSet(elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if (this is Collection<E>) this.toKoneIterableSet(elementContext = elementContext)
    else this.toKoneMutableIterableSet(elementContext = elementContext)

public fun <E> KoneIterableCollection<E>.toKoneIterableSet(elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if (size == 0u) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet(elementContext = elementContext)

public fun <E> Collection<E>.toKoneIterableSet(elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if (size == 0) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet(elementContext = elementContext)

public fun <E> KoneList<E>.toKoneIterableSet(elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if (size == 0u) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet(elementContext = elementContext)

public fun <E> KoneIterableList<E>.toKoneIterableSet(elementContext: Equality<E> = defaultEquality()): KoneIterableSet<E> =
    if (size == 0u) emptyKoneIterableSet()
    else this.toKoneMutableIterableSet(elementContext = elementContext)

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(elementContext: Equality<E> = defaultEquality(), @BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext)
        else KoneMutableListBackedSet(
            elementContext = elementContext,
            backingList = KoneGrowableArrayList(
                elementContext = elementContext
            )
        )
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneIterableSet(elementContext: Equality<E> = defaultEquality(), initialCapacity: UInt, @BuilderInference builderAction: KoneMutableIterableSet<E>.() -> Unit): KoneIterableSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    return KoneMutableListBackedSet(
        elementContext = elementContext,
        backingList = KoneGrowableArrayList(
            initialCapacity = initialCapacity,
            elementContext = elementContext
        )
    ).apply(builderAction)
}

// endregion