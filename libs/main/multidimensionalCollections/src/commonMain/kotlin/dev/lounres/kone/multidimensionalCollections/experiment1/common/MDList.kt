/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.common

import dev.lounres.kone.collections.common.KoneCollection
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.fold
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.Shaped


public interface MDList<out E>: Shaped, KoneCollection<E> {
    public val dimension: UInt get() = shape.size
    public operator fun get(index: KoneUIntArray): E

    override val size: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
    override fun contains(element: @UnsafeVariance E): Boolean = ShapeStrides(shape).asSequence().any { element == get(it) }
}

public operator fun <E> MDList<E>.get(vararg index: UInt): E = get(KoneUIntArray(index))

public interface SettableMDList<E>: MDList<E> {
    public operator fun set(index: KoneUIntArray, element: E)
}

public operator fun <E> SettableMDList<E>.set(vararg index: UInt, element: E) {
    set(KoneUIntArray(index), element)
}

