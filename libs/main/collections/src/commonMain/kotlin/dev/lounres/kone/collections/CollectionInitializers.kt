/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneSettableArrayList


public fun <E> KoneIterableList(size: UInt, initializer: (index: UInt) -> E): KoneIterableList<E> = KoneSettableArrayList(size, initializer)
public fun <E> KoneSettableIterableList(size: UInt, initializer: (index: UInt) -> E): KoneSettableIterableList<E> = KoneSettableArrayList(size, initializer)
public fun <E> KoneMutableIterableList(size: UInt, initializer: (index: UInt) -> E): KoneMutableIterableList<E> = KoneGrowableArrayList(size, initializer)

@Suppress("UNCHECKED_CAST")
public fun <E> koneIterableListOf(vararg elements: E): KoneIterableList<E> = KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))
@Suppress("UNCHECKED_CAST")
public fun <E> koneSettableIterableListOf(vararg elements: E): KoneSettableIterableList<E> = KoneSettableArrayList(KoneMutableArray(elements as Array<Any?>))
public fun <E> koneMutableIterableListOf(vararg elements: E): KoneMutableIterableList<E> = KoneGrowableArrayList(elements.size.toUInt()) { elements[it.toInt()] }