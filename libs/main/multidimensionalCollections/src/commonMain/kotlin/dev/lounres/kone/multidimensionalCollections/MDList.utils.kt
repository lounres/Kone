/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.algebraic.semiring
import dev.lounres.kone.collections.utils.product
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList
import dev.lounres.kone.multidimensionalCollections.implementations.generate


public fun <E> MDList.Companion.generate(
    size: MDSize,
    offsetting: MDSizeOffsetting = MDSizeStrides(size),
    initializer: (index: MDIndex) -> E
): MDList<E> = ArrayMDList.generate(size = size, offsetting = offsetting, initializer = initializer)

public val MDList<*>.dimension: UInt get() = size.size
public val MDList<*>.contentSize: UInt get() = (UInt.semiring()) { size.product() }

public operator fun <E> MDList<E>.get(vararg index: UInt): E = get(MDIndex.of(dims = index))

public operator fun <E> SettableMDList<E>.set(vararg index: UInt, element: E) {
    set(MDIndex.of(dims = index), element)
}