/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard


public inline fun <reified E> KoneArray(size: UInt, init: (UInt) -> E): KoneArray<E> = KoneArray(Array(size.toInt()) { init(it.toUInt()) })
public fun <E> koneArrayOf(vararg elements: E): KoneArray<E> = KoneArray(elements)
public inline fun <reified E> KoneMutableArray(size: UInt, init: (UInt) -> E): KoneMutableArray<E> = KoneMutableArray(Array(size.toInt()) { init(it.toUInt()) })
@Suppress("UNCHECKED_CAST")
public fun <E> koneMutableArrayOf(vararg elements: E): KoneMutableArray<E> = KoneMutableArray(elements as Array<E>)

public inline fun KoneUIntArray(size: UInt, init: (UInt) -> UInt): KoneUIntArray = KoneUIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })
public fun KoneUIntArray(size: UInt): KoneUIntArray = KoneUIntArray(UIntArray(size.toInt()))
public fun koneUIntArrayOf(vararg elements: UInt): KoneUIntArray = KoneUIntArray(elements)
public inline fun KoneMutableUIntArray(size: UInt, init: (UInt) -> UInt): KoneMutableUIntArray = KoneMutableUIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })
public fun KoneMutableUIntArray(size: UInt): KoneMutableUIntArray = KoneMutableUIntArray(UIntArray(size.toInt()))
public fun koneMutableUIntArrayOf(vararg elements: UInt): KoneMutableUIntArray = KoneMutableUIntArray(elements)