/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.indices
import dev.lounres.kone.multidimensionalCollections.mdSizeMismatchException


// TODO: Review the utilities
public fun <N> ColumnVector(vararg elements: N): ColumnVector<N, MDList1<N>> =
    ColumnVector(MDList1(*elements))
public inline fun <N> ColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): ColumnVector<N, MDList1<N>> =
    ColumnVector(MDList1(size, initializer))

public fun <N> SettableColumnVector(vararg elements: N): SettableColumnVector<N, SettableMDList1<N>> =
    SettableColumnVector(SettableMDList1(elements.size.toUInt()) { elements[it.toInt()] })
public inline fun <N> SettableColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): SettableColumnVector<N, SettableMDList1<N>> =
    SettableColumnVector(SettableMDList1(size, initializer))

public fun requireMDSizeEquality(left: ColumnVector<*, *>, right: ColumnVector<*, *>) {
    if (left.size != right.size) mdSizeMismatchException(left = left.coefficients.size, right = right.coefficients.size)
}

public val ColumnVector<*, *>.indices: UIntRange get() = coefficients.indices