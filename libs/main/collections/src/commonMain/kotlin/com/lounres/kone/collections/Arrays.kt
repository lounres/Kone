/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections

import com.lounres.kone.collections.wrappers.asKone
import kotlin.jvm.JvmInline


@Suppress("UNCHECKED_CAST")
@JvmInline
public value class KoneArray<out E>(private val value: Array<Any?>) {
    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public val size: UInt get() = value.size.toUInt()
    public operator fun get(index: UInt): E = value.get(index.toInt()) as E
    public operator fun iterator(): KoneIterator<E> = (value.iterator() as Iterator<E>).asKone()
}

@Suppress("UNCHECKED_CAST")
@JvmInline
public value class KoneMutableArray<E>(private val value: Array<Any?>) {
    public constructor(size: UInt, init: (UInt) -> E): this(Array(size.toInt()) { init(it.toUInt()) })

    public val size: UInt get() = value.size.toUInt()
    public operator fun get(index: UInt): E = value.get(index.toInt()) as E
    public operator fun set(index: UInt, element: E) { value.set(index.toInt(), element) }
    public operator fun iterator(): KoneIterator<E> = (value.iterator() as Iterator<E>).asKone()
}