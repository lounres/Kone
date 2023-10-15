/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.KoneUIntArray
import dev.lounres.kone.collections.aliases.*


public data class ElementWithIndexND<out E>(
    val index: UIntArray,
    val element: E,
)

public interface FormationND<out E>: WithShape {
    public val dimension: UInt get() = shape.size
    public operator fun get(index: UIntArray): E
    public fun elements(): Sequence<ElementWithIndexND<E>> = indexer.asSequence().map { ElementWithIndexND(it, get(it)) }
}

public operator fun <E> FormationND<E>.get(vararg index: UInt): E = get(KoneUIntArray(index.toIntArray()))

public interface MutableFormationND<E>: FormationND<E> {
    public operator fun set(index: UIntArray, element: E): E
}

public operator fun <E> MutableFormationND<E>.set(vararg index: UInt, element: E) {
    set(KoneUIntArray(index.toIntArray()), element)
}