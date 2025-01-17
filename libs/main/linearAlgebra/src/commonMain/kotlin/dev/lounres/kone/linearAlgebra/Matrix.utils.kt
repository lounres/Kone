/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.columnIndices
import dev.lounres.kone.multidimensionalCollections.rowIndices
import dev.lounres.kone.multidimensionalCollections.shapeMismatchException


public fun <E> Matrix(vararg elements: KoneList<E>): Matrix<E> {
    require(elements.all { it.size == elements[0].size }) { "Cannot construct Matrix from list of list of different sizes" }
    return Matrix(MDList2(*elements))
}
public fun <E> Matrix(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): Matrix<E> =
    Matrix(MDList2(rowNumber, columnNumber, initializer))

public fun requireShapeEquality(left: Matrix<*>, right: Matrix<*>) {
    if (left.rowNumber != right.rowNumber || left.columnNumber != right.columnNumber)
        shapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val Matrix<*>.rowIndices: UIntRange get() = coefficients.rowIndices
public val Matrix<*>.columnIndices: UIntRange get() = coefficients.columnIndices