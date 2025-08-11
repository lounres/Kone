/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1Producer
import dev.lounres.kone.multidimensionalCollections.indices
import dev.lounres.kone.multidimensionalCollections.producers.MDList1Producer
import dev.lounres.kone.multidimensionalCollections.mdSizeMismatchException


public fun <N> RowVector(vararg elements: N, mdList1Producer: MDList1Producer = ArrayMDList1Producer): RowVector<N> =
    RowVector(mdList1Producer.produceBy(elements.size.toUInt()) { elements[it.toInt()] })
public fun <N> RowVector(size: UInt, mdList1Producer: MDList1Producer = ArrayMDList1Producer, initializer: (index: UInt) -> N): RowVector<N> =
    RowVector(mdList1Producer.produceBy(size, initializer))

public fun requireMDSizeEquality(left: RowVector<*>, right: RowVector<*>) {
    if (left.size != right.size) mdSizeMismatchException(left = left.coefficients.size, right = right.coefficients.size)
}

public val RowVector<*>.indices: UIntRange get() = coefficients.indices