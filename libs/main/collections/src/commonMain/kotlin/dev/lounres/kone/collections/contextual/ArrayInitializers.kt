/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual


public inline fun <reified E> KoneContextualArray(size: UInt, init: (UInt) -> E): KoneContextualArray<E> = KoneContextualArray(Array(size.toInt()) { init(it.toUInt()) })
public fun <E> koneContextualArrayOf(vararg elements: E): KoneContextualArray<E> = KoneContextualArray(elements)
public inline fun <reified E> KoneContextualMutableArray(size: UInt, init: (UInt) -> E): KoneContextualMutableArray<E> = KoneContextualMutableArray(Array(size.toInt()) { init(it.toUInt()) })
@Suppress("UNCHECKED_CAST")
public fun <E> koneContextualMutableArrayOf(vararg elements: E): KoneContextualMutableArray<E> = KoneContextualMutableArray(elements as Array<E>)

public inline fun KoneContextualUIntArray(size: UInt, init: (UInt) -> UInt): KoneContextualUIntArray = KoneContextualUIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })
public fun KoneContextualUIntArray(size: UInt): KoneContextualUIntArray = KoneContextualUIntArray(UIntArray(size.toInt()))
public fun koneContextualUIntArrayOf(vararg elements: UInt): KoneContextualUIntArray = KoneContextualUIntArray(elements)
public inline fun KoneContextualMutableUIntArray(size: UInt, init: (UInt) -> UInt): KoneContextualMutableUIntArray = KoneContextualMutableUIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })
public fun KoneContextualMutableUIntArray(size: UInt): KoneContextualMutableUIntArray = KoneContextualMutableUIntArray(UIntArray(size.toInt()))
public fun koneContextualMutableUIntArrayOf(vararg elements: UInt): KoneContextualMutableUIntArray = KoneContextualMutableUIntArray(elements)