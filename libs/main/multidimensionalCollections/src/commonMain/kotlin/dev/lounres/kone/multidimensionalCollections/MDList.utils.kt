/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.DelicateImmutableArrayConstructor
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList


public fun <E> MDList(
    shape: MDShape,
    offsetting: MDShapeOffsetting = MDShapeStrides(shape),
    initializer: (index: KoneUIntArray) -> E
): MDList<E> =
    ArrayMDList(shape = shape, offsetting = offsetting, initializer = initializer)

public val MDList<*>.dimension: UInt get() = shape.size

@OptIn(DelicateImmutableArrayConstructor::class)
public operator fun <E> MDList<E>.get(vararg index: UInt): E = get(KoneUIntArray(index))

@OptIn(DelicateImmutableArrayConstructor::class)
public operator fun <E> SettableMDList<E>.set(vararg index: UInt, element: E) {
    set(KoneUIntArray(index), element)
}