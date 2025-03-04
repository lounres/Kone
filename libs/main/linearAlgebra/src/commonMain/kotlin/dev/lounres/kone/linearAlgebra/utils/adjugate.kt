/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.utils

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2


context(_: Ring<Number>)
public fun <Number> Matrix<Number>.adjugateViaLeibnizFormula(): Matrix<Number> {
    require(this.rowNumber == this.columnNumber) { TODO("Error message is not specified") }
    return Matrix(ArrayMDList2(this.columnNumber, this.rowNumber) { column, row -> this.firstCofactorViaLeibnizFormula(row, column) })
}

context(_: Field<Number>)
public fun <Number> Matrix<Number>.adjugateViaGaussianElimination(): Matrix<Number> {
    require(this.rowNumber == this.columnNumber) { TODO("Error message is not specified") }
    return Matrix(ArrayMDList2(this.columnNumber, this.rowNumber) { column, row -> this.firstCofactorViaGaussianElimination(row, column) })
}