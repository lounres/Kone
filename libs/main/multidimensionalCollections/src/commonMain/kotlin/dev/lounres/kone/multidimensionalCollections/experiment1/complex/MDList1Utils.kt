/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.complex

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.implementations.ArrayMDList1


public fun <E> MDList1(vararg elements: E, context: Equality<E>): MDList1<E> =
    ArrayMDList1(elements.size.toUInt(), context = context) { index -> elements[index.toInt()] }
public fun <E> MDList1(size: UInt, context: Equality<E>, initializer: (coefficient: UInt) -> E): MDList1<E> =
    ArrayMDList1(size = size, context = context, initializer = initializer)

public val MDList1<*>.indices: UIntRange get() = 0u ..< size