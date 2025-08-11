/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.indices
import dev.lounres.kone.multidimensionalCollections.mdSizeMismatchException


public fun <N> ColumnVector(vararg elements: N): ColumnVector<N> = ColumnVector(MDList1(*elements))
public fun <N> ColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): ColumnVector<N> =
    ColumnVector(MDList1(size, initializer))

public fun <N> SettableColumnVector(vararg elements: N): SettableColumnVector<N> =
    SettableColumnVector(SettableMDList1(*elements))
public fun <N> SettableColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): SettableColumnVector<N> =
    SettableColumnVector(SettableMDList1(size, initializer))

public fun requireMDSizeEquality(left: ColumnVector<*>, right: ColumnVector<*>) {
    if (left.size != right.size) mdSizeMismatchException(left = left.coefficients.size, right = right.coefficients.size)
}

public val ColumnVector<*>.indices: UIntRange get() = coefficients.indices