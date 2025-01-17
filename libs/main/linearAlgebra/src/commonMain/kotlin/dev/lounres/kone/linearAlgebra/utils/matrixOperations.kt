/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.collections.utils.hasDuplicates
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.scope
import kotlin.sequences.fold


context(_: Ring<N>)
public val <N> Matrix<N>.isSymmetric: Boolean
    get() {
        if (rowNumber != columnNumber) return false
        
        for (row in 0u ..< rowNumber) for (column in row + 1u ..< columnNumber) if (this[row, column] neq this[column, row]) return false
        
        return true
    }

context(_: Ring<N>)
public val <N> Matrix<N>.isAntisymmetric: Boolean
    get() {
        if (rowNumber != columnNumber) return false
        
        for (row in 0u ..< rowNumber) {
            if (this[row, row].isNotZero()) return false
            for (column in row + 1u..<columnNumber) if ((this[row, column] + this[column, row]).isNotZero()) return false
        }
        
        return true
    }

context(_: A, _: VectorKategory<N>)
public val <N, A> Matrix<N>.transpose: Matrix<N> where A : Ring<N>
    get() = Matrix(this.columnNumber, this.rowNumber) { row, column -> this[column, row] }

context(_: A, _: VectorKategory<N>)
public val <N, A> Matrix<N>.det: N where A : Ring<N>
    get() = TODO("Not yet implemented")

//context(_: VectorSpace<N>)
//public val <N, A> Matrix<N>.reciprocal: Matrix<N> where A : Ring<N>
//    get() = (this.getFeature<_, InvertibleMatrixFeature<N>>() ?: throw IllegalArgumentException("Could not compute reciprocal matrix")).inverseMatrix

context(_: A, _: VectorKategory<N>)
public val <N, A> Matrix<N>.adjugate: Matrix<N> where A : Ring<N>
    get() = TODO("Not yet implemented")

public data class MatrixMinorComputerFeature<N>(private val matrix: Matrix<N>, private val minorComputer: (rowIndices: KoneUIntArray, columnIndices: KoneUIntArray) -> N) {
    public operator fun get(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): N = minorComputer(rowIndices, columnIndices)
    public fun first(rowIndex: UInt, columnIndex: UInt): N =
        minorComputer(
            KoneUIntArray(matrix.rowNumber - 1u) { if (it < rowIndex) it else it + 1u },
            KoneUIntArray(matrix.columnNumber - 1u) { if (it < columnIndex) it else it + 1u }
        )
}

context(_: A, _: VectorKategory<N>)
public val <N, A: Ring<N>> Matrix<N>.minor: MatrixMinorComputerFeature<N>
    get() = MatrixMinorComputerFeature(this) { rowIndices, columnIndices ->
        require(rowIndices.size == columnIndices.size) { TODO("Error message is not specified") }
        val minorSize = rowIndices.size
        if (rowIndices.hasDuplicates() || columnIndices.hasDuplicates()) return@MatrixMinorComputerFeature zero
        
        (0u ..< minorSize).toKoneList().permutations().fold(zero) { result, permutation ->
            val permutationIsEven = scope {
                var permutationIsEven = true
                val visited = KoneMutableArray(minorSize) { false } // TODO: Can be replaced with specialised array
                for (i in 0u ..< minorSize) if (!visited[i]) {
                    var current = i
                    visited[i] = true
                    var cycleIsEven = false
                    while (true) {
                        current = permutation[current]
                        if (current == i) break
                        cycleIsEven = !cycleIsEven
                        visited[current] = true
                    }
                    if (cycleIsEven) permutationIsEven = !permutationIsEven
                }
                permutationIsEven
            }
            result + permutation.foldIndexed(one) { row, product, column -> product * this[rowIndices[row], columnIndices[column]] }.let { if (permutationIsEven) it else -it }
        }
    }