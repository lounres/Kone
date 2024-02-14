/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.standard.KoneCollection
import dev.lounres.kone.collections.standard.KoneUIntArray
import dev.lounres.kone.collections.standard.aliases.UIntArray
import dev.lounres.kone.collections.standard.utils.fold
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.Shaped


public data class ElementWithMDIndex<out E>(
    val index: UIntArray,
    val element: E,
)

public interface MDList<out E>: Shaped, KoneCollection<E> {
    public val dimension: UInt get() = shape.size
    public operator fun get(index: UIntArray): E

    override val size: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
    override fun contains(element: @UnsafeVariance E): Boolean = ShapeStrides(shape).asSequence().any { element == get(it) }
}

public operator fun <E> MDList<E>.get(vararg index: UInt): E = get(KoneUIntArray(index))

public interface SettableMDList<E>: MDList<E> {
    public operator fun set(index: UIntArray, element: E)
}

public operator fun <E> SettableMDList<E>.set(vararg index: UInt, element: E) {
    set(KoneUIntArray(index), element)
}

