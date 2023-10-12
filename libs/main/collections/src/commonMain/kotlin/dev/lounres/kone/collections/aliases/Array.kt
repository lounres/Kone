/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases

import dev.lounres.kone.collections.KoneArray
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneMutableUIntArray
import dev.lounres.kone.collections.KoneUIntArray


public typealias Array<E> = KoneArray<E>
public inline fun <reified E> Array(size: UInt, init: (UInt) -> E): Array<E> = Array(Array(size.toInt()) { init(it.toUInt()) })
public typealias MutableArray<E> = KoneMutableArray<E>
public inline fun <reified E> MutableArray(size: UInt, init: (UInt) -> E): MutableArray<E> = MutableArray(Array(size.toInt()) { init(it.toUInt()) })

public typealias UIntArray = KoneUIntArray
public inline fun UIntArray(size: UInt, init: (UInt) -> UInt): UIntArray = UIntArray(IntArray(size.toInt()) { init(it.toUInt()).toInt() })
public typealias MutableUIntArray = KoneMutableUIntArray
public inline fun MutableUIntArray(size: UInt, init: (UInt) -> UInt): MutableUIntArray = KoneMutableUIntArray(IntArray(size.toInt()) { init(it.toUInt()).toInt() })