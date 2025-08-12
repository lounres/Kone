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
public fun <N> RowVector(vararg elements: N): RowVector<N, MDList1<N>> =
    RowVector(MDList1(*elements))
public inline fun <N> RowVector(size: UInt, initializer: (index: UInt) -> N): RowVector<N, MDList1<N>> =
    RowVector(MDList1(size, initializer))

public fun <N> SettableRowVector(vararg elements: N): SettableRowVector<N, SettableMDList1<N>> =
    SettableRowVector(SettableMDList1(elements.size.toUInt()) { elements[it.toInt()] })
public inline fun <N> SettableRowVector(size: UInt, initializer: (coefficient: UInt) -> N): SettableRowVector<N, SettableMDList1<N>> =
    SettableRowVector(SettableMDList1(size, initializer))

public fun requireMDSizeEquality(left: RowVector<*, *>, right: RowVector<*, *>) {
    if (left.size != right.size) mdSizeMismatchException(left = left.coefficients.size, right = right.coefficients.size)
}

public val RowVector<*, *>.indices: UIntRange get() = coefficients.indices