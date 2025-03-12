/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.utils

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.linearAlgebra.Matrix


// TODO: What is a general definition of cofactor?
//context(_: Ring<Number>)
//public fun <Number> Matrix<Number>.cofactorViaLeibnizFormula(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number =
//    minorViaLeibnizFormula(rowIndices, columnIndices).let { if((rowIndices.size + columnIndices.size) % 2u == 0u) it else -it }

context(_: Ring<Number>)
public fun <Number> Matrix<Number>.firstCofactorViaLeibnizFormula(rowIndex: UInt, columnIndex: UInt): Number =
    firstMinorViaLeibnizFormula(rowIndex, columnIndex).let { if((rowIndex + columnIndex) % 2u == 0u) it else -it }

//context(_: Field<Number>)
//public fun <Number> Matrix<Number>.cofactorViaGaussianElimination(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number =
//    minorViaGaussianElimination(rowIndices, columnIndices).let { if((rowIndices.size + columnIndices.size) % 2u == 0u) it else -it }

context(_: Field<Number>)
public fun <Number> Matrix<Number>.firstCofactorViaGaussianElimination(rowIndex: UInt, columnIndex: UInt): Number =
    firstMinorViaGaussianElimination(rowIndex, columnIndex).let { if((rowIndex + columnIndex) % 2u == 0u) it else -it }