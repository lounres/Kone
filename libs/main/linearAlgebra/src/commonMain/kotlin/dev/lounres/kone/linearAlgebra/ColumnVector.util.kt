/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.indices
import dev.lounres.kone.multidimensionalCollections.shapeMismatchException


public fun <N> ColumnVector(vararg elements: N): ColumnVector<N> = ColumnVector(MDList1(*elements))
public fun <N> ColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): ColumnVector<N> =
    ColumnVector(MDList1(size, initializer))

public fun <N> SettableColumnVector(vararg elements: N): SettableColumnVector<N> =
    SettableColumnVector(SettableMDList1(*elements))
public fun <N> SettableColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): SettableColumnVector<N> =
    SettableColumnVector(SettableMDList1(size, initializer))

public fun requireShapeEquality(left: ColumnVector<*>, right: ColumnVector<*>) {
    if (left.size != right.size) shapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val ColumnVector<*>.indices: UIntRange get() = coefficients.indices