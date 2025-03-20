/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.neq
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.columnIndices
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2
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

// TODO: Think about custom `MDList2Producer`
// TODO: Think about custom operations providers.

public fun <Number> Matrix<Number>.transpose(): Matrix<Number> =
    Matrix(ArrayMDList2(columnNumber, rowNumber) { row, column -> this[column, row] })

context(_: Equality<Number>)
public val <Number> Matrix<Number>.isSymmetric: Boolean
    get() {
        if (rowNumber != columnNumber) return false
        
        for (row in 0u..<rowNumber) for (column in 0u..<row) if (this[row, column] neq this[column, row]) return false
        
        return true
    }

context(_: Ring<Number>)
public val <Number> Matrix<Number>.isAntisymmetric: Boolean
    get() {
        if (rowNumber != columnNumber) return false
        
        for (row in 0u..<rowNumber) {
            if (this[row, row].isNotZero()) return false
            for (column in 0u ..< row) if ((this[row, column] + this[column, row]).isNotZero()) return false
        }
        
        return true
    }

context(_: Ring<Number>)
public val <Number> Matrix<Number>.isDiagonal: Boolean
    get() {
        for (row in 0u..<rowNumber) for (column in 0u..<columnNumber) if (row != column && this[row, column].isNotZero()) return false
        
        return true
    }

context(_: Ring<Number>)
public val <Number> Matrix<Number>.isScalar: Boolean
    get() {
        if (rowNumber != columnNumber) return false
        if (rowNumber == 0u) return true
        
        val scalar = this[0u, 0u]
        for (row in 0u..<rowNumber) for (column in 0u..<columnNumber) {
            if (row != column) {
                if (this[row, column].isNotZero()) return false
            } else {
                if (this[row, column] neq scalar) return false
            }
        }
        
        return true
    }