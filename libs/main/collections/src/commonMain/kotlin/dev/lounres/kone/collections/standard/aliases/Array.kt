/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard.aliases

import dev.lounres.kone.collections.standard.KoneArray
import dev.lounres.kone.collections.standard.KoneMutableArray
import dev.lounres.kone.collections.standard.KoneMutableUIntArray
import dev.lounres.kone.collections.standard.KoneUIntArray


public typealias Array<E> = KoneArray<E>
public inline fun <reified E> Array(size: UInt, init: (UInt) -> E): Array<E> = Array(Array(size.toInt()) { init(it.toUInt()) })
public fun <E> arrayOf(vararg elements: E): Array<E> = Array(elements)
public typealias MutableArray<E> = KoneMutableArray<E>
public inline fun <reified E> MutableArray(size: UInt, init: (UInt) -> E): MutableArray<E> = MutableArray(Array(size.toInt()) { init(it.toUInt()) })
@Suppress("UNCHECKED_CAST")
public fun <E> mutableArrayOf(vararg elements: E): MutableArray<E> = MutableArray(elements as kotlin.Array<E>)

public typealias UIntArray = KoneUIntArray
public inline fun UIntArray(size: UInt, init: (UInt) -> UInt): UIntArray = UIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })
public fun UIntArray(size: UInt): UIntArray = UIntArray(UIntArray(size.toInt()))
public fun uintArrayOf(vararg elements: UInt): UIntArray = UIntArray(elements)
public typealias MutableUIntArray = KoneMutableUIntArray
public inline fun MutableUIntArray(size: UInt, init: (UInt) -> UInt): MutableUIntArray = MutableUIntArray(UIntArray(size.toInt()) { init(it.toUInt()) })
public fun MutableUIntArray(size: UInt): MutableUIntArray = MutableUIntArray(UIntArray(size.toInt()))
public fun mutableUIntArrayOf(vararg elements: UInt): MutableUIntArray = MutableUIntArray(elements)