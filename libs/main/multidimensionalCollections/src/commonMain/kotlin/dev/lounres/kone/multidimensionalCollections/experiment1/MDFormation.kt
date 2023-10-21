/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.KoneUIntArray
import dev.lounres.kone.collections.aliases.*
import dev.lounres.kone.multidimensionalCollections.WithShape


public data class ElementWithMDIndex<out E>(
    val index: UIntArray,
    val element: E,
)

public interface MDFormation<out E>: WithShape {
    public val dimension: UInt get() = shape.size
    public operator fun get(index: UIntArray): E
    public val elements: Sequence<ElementWithMDIndex<E>> get() = indexer.asSequence().map { ElementWithMDIndex(it, get(it)) }
}

public operator fun <E> MDFormation<E>.get(vararg index: UInt): E = get(KoneUIntArray(index.toIntArray()))

public interface MutableMDFormation<E>: MDFormation<E> {
    public operator fun set(index: UIntArray, element: E)
}

public operator fun <E> MutableMDFormation<E>.set(vararg index: UInt, element: E) {
    set(KoneUIntArray(index.toIntArray()), element)
}