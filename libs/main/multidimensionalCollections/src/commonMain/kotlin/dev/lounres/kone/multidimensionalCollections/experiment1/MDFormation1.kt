/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.aliases.UIntArray
import dev.lounres.kone.multidimensionalCollections.IndexOutOfShapeException
import dev.lounres.kone.multidimensionalCollections.Shape


public interface MDFormation1<out E>: MDFormation<E> {
    override val dimension: UInt get() = 1u
    public val size: UInt
    override val shape: Shape get() = Shape(intArrayOf(size.toInt())) // FIXME: Replace me with just `Shape(size)`
    public operator fun get(index: UInt): E
    override fun get(index: UIntArray): E {
        if (
            index.size != 1u ||
            index[0u] >= shape[0u]
        ) throw IndexOutOfShapeException(shape = shape, index = index)
        return get(index[0u])
    }
    public operator fun iterator(): Iterator<E> = indexer.asSequence().map(::get).iterator()
    override val elements: Sequence<ElementWithMDIndex<E>> get() = sequence {
        for (i in 0u ..< size) yield(ElementWithMDIndex(index = UIntArray(intArrayOf(i.toInt())), element = get(i)))
    }
}

public interface MutableMDFormation1<E>: MutableMDFormation<E>, MDFormation1<E> {
    public operator fun set(index: UInt, element: E)
    override fun set(index: UIntArray, element: E) {
        if (
            index.size != 1u ||
            index[0u] >= shape[0u]
        ) throw IndexOutOfShapeException(shape = shape, index = index)
        set(index[0u], element)
    }
}