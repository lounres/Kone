/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.utils

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.collections.utils.hasDuplicates
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2
import kotlin.sequences.fold


context(_: Ring<Number>)
public fun <Number> Matrix<Number>.minorViaLeibnizFormula(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number {
    require(rowIndices.size == columnIndices.size) { TODO("Error message is not specified") }
    val minorSize = rowIndices.size
    if (rowIndices.hasDuplicates() || columnIndices.hasDuplicates()) return zero
    
    return (0u ..< minorSize).toKoneList().permutations().fold(zero) { result, permutation ->
        val permutationIsEven = permutation.isEvenPermutation()
        
        result + permutation.foldIndexed(one) { row, product, column -> product * this[rowIndices[row], columnIndices[column]] }.let { if (permutationIsEven) it else -it }
    }
}

context(_: Ring<Number>)
public fun <Number> Matrix<Number>.firstMinorViaLeibnizFormula(rowIndex: UInt, columnIndex: UInt): Number {
    require(this.rowNumber == this.columnNumber) { TODO("Error message is not specified") }
    
    return minorViaLeibnizFormula(
        KoneUIntArray(this.rowNumber - 1u) { if (it < rowIndex) it else it + 1u },
        KoneUIntArray(this.columnNumber - 1u) { if (it < columnIndex) it else it + 1u },
    )
}

context(_: Field<Number>)
public fun <Number> Matrix<Number>.minorViaGaussianElimination(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number {
    require(rowIndices.size == columnIndices.size) { TODO("Error message is not specified") }
    val minorSize = rowIndices.size
    if (rowIndices.hasDuplicates() || columnIndices.hasDuplicates()) return zero
    
    val matrix = ArrayMDList2(minorSize, minorSize) { rowIndex, columnIndex -> this.coefficients[rowIndices[rowIndex], columnIndices[columnIndex]] }
    
    var applyMinus = false
    var fromRow = 0u
    while (fromRow < matrix.rowNumber) {
        var coolRow = fromRow
        while (coolRow < matrix.rowNumber) {
            if (matrix[coolRow, fromRow].isNotZero()) break
            coolRow++
        }
        if (coolRow == matrix.rowNumber) {
            return zero
        }
        
        if (coolRow != fromRow) {
            applyMinus = !applyMinus
            for (column in fromRow ..< matrix.columnNumber) {
                matrix[coolRow, column] = matrix[fromRow, column].also { matrix[fromRow, column] = matrix[coolRow, column] }
            }
        }
        
        val coolCoef = matrix[fromRow, fromRow]
        
        for (row in fromRow + 1u ..< matrix.rowNumber) {
            val rowCoef = matrix[row, fromRow]
            for (column in fromRow + 1u ..< matrix.columnNumber)
                matrix[row, column] = matrix[row, column] - matrix[fromRow, column] / coolCoef * rowCoef
        }
        
        fromRow++
    }
    
    return (0u ..< matrix.rowNumber).fold(if (applyMinus) -one else one) { acc, index -> acc * matrix[index, index] }
}

context(_: Field<Number>)
public fun <Number> Matrix<Number>.firstMinorViaGaussianElimination(rowIndex: UInt, columnIndex: UInt): Number {
    require(this.rowNumber == this.columnNumber) { TODO("Error message is not specified") }
    
    return minorViaGaussianElimination(
        KoneUIntArray(this.rowNumber - 1u) { if (it < rowIndex) it else it + 1u },
        KoneUIntArray(this.columnNumber - 1u) { if (it < columnIndex) it else it + 1u },
    )
}