/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1
import dev.lounres.kone.multidimensionalCollections.implementations.generate


public fun <E> MDList1.Companion.of(vararg elements: E): MDList1<E> =
    ArrayMDList1.generate(elements.size.toUInt()) { index -> elements[index.toInt()] }

public inline fun <E> MDList1.Companion.generate(contentSize: UInt, initializer: (index: UInt) -> E): MDList1<E> =
    ArrayMDList1.generate(contentSize = contentSize, initializer = initializer)

public fun <E> SettableMDList1.Companion.of(vararg elements: E): SettableMDList1<E> =
    ArrayMDList1.generate(elements.size.toUInt()) { index -> elements[index.toInt()] }

public inline fun <E> SettableMDList1.Companion.generate(contentSize: UInt, initializer: (index: UInt) -> E): SettableMDList1<E> =
    ArrayMDList1.generate(contentSize = contentSize, initializer = initializer)

public val MDList1<*>.indices: UIntRange get() = 0u ..< size[0u]

/*@JvmInline*/
internal open /*value*/ class MDList1Wrapper<out E>(open val list: MDList<E>): MDList1<E> {
    init {
        require(list.size.size == 1u) { "Cannot wrap MDList with MD size ${list.size} as a MDList1" }
    }
    
    override val size: MDSize get() = list.size
    override fun get(index: UInt): E = list[MDIndex.of(index)]
}

/*@JvmInline*/
internal /*value*/ class SettableMDList1Wrapper<E>(override val list: SettableMDList<E>): MDList1Wrapper<E>(list), SettableMDList1<E> {
    override fun set(index: UInt, element: E) {
        list[MDIndex.of(index)] = element
    }
}

public fun <E> MDList<E>.as1D(): MDList1<E> =
    when {
        this is MDList1<E> -> this
        dimension == 1u -> MDList1Wrapper(this)
        else -> throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of MD size $size")
    }

public fun <E> SettableMDList<E>.as1D(): SettableMDList1<E> =
    when {
        this is SettableMDList1<E> -> this
        dimension == 1u -> SettableMDList1Wrapper(this)
        else -> throw IllegalArgumentException("Expected 1-dimensional MD list, got MD list of MD size $size")
    }

public fun <E> MDList1<E>.asMD(): MDList<E> =
    if (this is MDList1Wrapper) list
    else this

public fun <E> SettableMDList1<E>.asMD(): SettableMDList<E> =
    if (this is SettableMDList1Wrapper) list
    else this