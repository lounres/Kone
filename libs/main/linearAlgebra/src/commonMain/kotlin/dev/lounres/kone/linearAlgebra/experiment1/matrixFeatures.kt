/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.common.KoneMutableArray
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.*
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.feature.FeatureProvider
import dev.lounres.kone.misc.scope
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDListTransformer
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDListTransformer
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.rowIndices
import kotlin.reflect.KClass


public data class ZeroMatrixFeature(val value: Boolean)

public class ZeroMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == ZeroMatrixFeature::class) ZeroMatrixFeature(mdListTransformer { this.coefficients.all { ring { it.isZero() } } }) as F else null
}

public data class OneMatrixFeature(val value: Boolean)

public class OneMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == OneMatrixFeature::class) OneMatrixFeature(mdListTransformer { this.coefficients.allIndexed { index, it -> ring { if (index[0u] == index[1u]) it.isOne() else it.isZero() } } }) as F else null
}

public data class SquareMatrixFeature(val value: Boolean)

public class SquareMatrixFeatureProvider: FeatureProvider<Matrix<*, *>> {
    override fun <F : Any> Matrix<*, *>.getFeature(type: KClass<F>): F? =
        if (type == SquareMatrixFeature::class) SquareMatrixFeature(columnNumber == rowNumber) as F else null
}

public data class TransposeMatrixFeature<N, in NE: Equality<N>>(public val transpose: Matrix<N, NE>)

public class TransposeMatrixFeatureProvider<N, in NE: Equality<N>>(private val mdListTransformer: ContextualMDListTransformer<N, NE>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == TransposeMatrixFeature::class && columnNumber == rowNumber) TransposeMatrixFeature(Matrix(mdListTransformer.mdList2(columnNumber, rowNumber) { column, row -> this[row, column] })) as F else null
}

public data class SymmetricMatrixFeature(val value: Boolean)

public class SymmetricMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == SymmetricMatrixFeature::class) SymmetricMatrixFeature(mdListTransformer { this.coefficients.allIndexed { (row, column), coef -> row <= column || ring { coef eq this[column, row] } }}) as F else null
}

public data class AntisymmetricMatrixFeature(val value: Boolean)

public class AntisymmetricMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == SymmetricMatrixFeature::class) SymmetricMatrixFeature(mdListTransformer { this.coefficients.allIndexed { (row, column), coef -> (row == column && ring { coef.isZero() }) || row < column || ring { coef eq -this[column, row] } } }) as F else null
}

// TODO: Finish the implementation
public data class DiagonalMatrixFeature(val value: Boolean)

public class DiagonalMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == DiagonalMatrixFeature::class) DiagonalMatrixFeature(columnNumber == rowNumber && mdListTransformer { this.coefficients.allIndexed { index, it -> if (index[0u] == index[1u]) true else ring { it.isZero() } } }) as F else null
}

public data class ScalarMatrixFeature(val value: Boolean)

public class ScalarMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == ScalarMatrixFeature::class) ScalarMatrixFeature(columnNumber == rowNumber && mdListTransformer { this.coefficients.allIndexed { index, it -> if (index[0u] == index[1u]) ring { it eq this[0u, 0u] } else ring { it.isZero() } } }) as F else null
}

public data class AdjugateMatrixFeature<N, in NE: Equality<N>>(val adjugateMatrix: Matrix<N, NE>)

public class AdjugateMatrixFeatureProvider<N, NE: Equality<N>>(private val vectorSpace: VectorSpace<N, Ring<N>>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? = vectorSpace {
        if (type == MatrixMinorComputerFeature::class) TODO() else null
    }
}

public data class InvertibleMatrixFeature<N, NE: Equality<N>>(val inverseMatrix: Matrix<N, NE>)

public class InvertibleMatrixViaGaussianAlgorithmFeatureProvider<N>(private val field: Field<N>, private val mdListTransformer: ContextualSettableMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? {
        if (type != DeterminantMatrixFeature::class || rowNumber != columnNumber) return null

        val matrix = mdListTransformer.settableMdList2(rowNumber, columnNumber) { rowIndex, columnIndex -> this.coefficients[rowIndex, columnIndex] }
        val inverseMatrix = mdListTransformer.settableMdList2(rowNumber, columnNumber) { rowIndex, columnIndex -> if (rowIndex == columnIndex) field.one else field.zero }
        var matrixDeterminant = field.one

        var fromRow = 0u
        while (fromRow < matrix.columnNumber && fromRow < matrix.rowNumber) {
            var coolRow = fromRow
            while (coolRow < matrix.rowNumber) {
                if (field { matrix[coolRow, fromRow].isNotZero() }) break
                coolRow++
            }
            if (coolRow == matrix.rowNumber) {
                return null
            }

            if (coolRow != fromRow) for (column in 0u ..< matrix.columnNumber) {
                matrix[coolRow, column] = matrix[fromRow, column].also { matrix[fromRow, column] = matrix[coolRow, column] }
                inverseMatrix[coolRow, column] = inverseMatrix[fromRow, column].also { matrix[fromRow, column] = matrix[coolRow, column] }
            }

            val coolCoef = matrix[fromRow, fromRow]

            for (column in 0u ..< matrix.columnNumber) {
                field { matrix[coolRow, column] /= coolCoef }
                field { inverseMatrix[coolRow, column] /= coolCoef }
            }
            field { matrixDeterminant *= coolCoef }

            for (row in matrix.rowIndices) {
                if (row == fromRow) continue
                val rowCoef = matrix[row, fromRow]
                for (column in fromRow + 1u .. matrix.columnNumber) {
                    matrix[row, column] = field { matrix[row, column] - matrix[fromRow, column] * rowCoef }
                    inverseMatrix[row, column] = field { inverseMatrix[row, column] - inverseMatrix[fromRow, column] * rowCoef }
                }
            }

            fromRow++
        }

        val result = Matrix(inverseMatrix)

        this.storeFeature(field, DeterminantMatrixFeature::class, DeterminantMatrixFeature(matrixDeterminant))
        result.storeFeature(field, DeterminantMatrixFeature::class, DeterminantMatrixFeature(field { matrixDeterminant.reciprocal }))
        result.storeFeature(field, InvertibleMatrixFeature::class, InvertibleMatrixFeature(this))

        return InvertibleMatrixFeature(result) as F
    }
}

public data class DeterminantMatrixFeature<out N>(val determinant: N)

public class DeterminantMatrixViaGaussianAlgorithmFeatureProvider<N>(private val field: Field<N>, private val mdListTransformer: ContextualSettableMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? {
        if (type != DeterminantMatrixFeature::class || rowNumber != columnNumber) return null

        val matrix = mdListTransformer.settableMdList2(rowNumber, columnNumber) { rowIndex, columnIndex -> this.coefficients[rowIndex, columnIndex] }

        var fromRow = 0u
        while (fromRow < matrix.columnNumber && fromRow < matrix.rowNumber) {
            var coolRow = fromRow
            while (coolRow < matrix.rowNumber) {
                if (field { matrix[coolRow, fromRow].isNotZero() }) break
                coolRow++
            }
            if (coolRow == matrix.rowNumber) {
                return DeterminantMatrixFeature(field.zero) as F
            }

            if (coolRow != fromRow) for (column in 0u ..< matrix.columnNumber) {
                matrix[coolRow, column] = matrix[fromRow, column].also { matrix[fromRow, column] = matrix[coolRow, column] }
            }

            val coolCoef = matrix[fromRow, fromRow]

            for (row in fromRow + 1u ..< matrix.rowNumber) {
                val rowCoef = matrix[row, fromRow]
                for (column in fromRow + 1u .. matrix.columnNumber)
                    matrix[row, column] = field { matrix[row, column] - matrix[fromRow, column] / coolCoef * rowCoef }
            }

            fromRow++
        }

        return DeterminantMatrixFeature((0u ..< matrix.rowNumber).fold(field.one) { acc, index -> field { acc * matrix[index, index] } }) as F
    }
}

public data class RankMatrixFeature(val rank: UInt)

public class RankMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualSettableMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? {
        if (type != DeterminantMatrixFeature::class) return null

        val matrix = mdListTransformer.settableMdList2(rowNumber, columnNumber) { rowIndex, columnIndex -> this.coefficients[rowIndex, columnIndex] }

        var columnNow = 0u
        var fromRow = 0u
        while (columnNow < matrix.columnNumber && fromRow < matrix.rowNumber) {
            var coolRow = fromRow
            while (coolRow < matrix.rowNumber) {
                if (ring { matrix[coolRow, columnNow].isNotZero() }) break
                coolRow++
            }
            if (coolRow == matrix.rowNumber) {
                columnNow++
                continue
            }

            if (coolRow != fromRow) for (column in 0u ..< matrix.columnNumber) {
                matrix[coolRow, column] = matrix[fromRow, column].also { matrix[fromRow, column] = matrix[coolRow, column] }
            }

            val coolCoef = matrix[fromRow, columnNow]

            for (row in fromRow + 1u ..< matrix.rowNumber) {
                val rowCoef = matrix[row, columnNow]
                for (column in columnNow + 1u .. matrix.columnNumber)
                    matrix[row, column] = ring { matrix[row, column] * coolCoef - matrix[fromRow, column] * rowCoef }
            }

            columnNow++
            fromRow++
        }

        return RankMatrixFeature(fromRow) as F
    }
}

public data class MatrixMinorComputerFeature<N>(private val matrix: Matrix<N, *>, private val minorComputer: (rowIndices: KoneUIntArray, columnIndices: KoneUIntArray) -> N) {
    public operator fun get(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): N = minorComputer(rowIndices, columnIndices)
    public fun first(rowIndex: UInt, columnIndex: UInt): N =
        minorComputer(
            KoneUIntArray(matrix.rowNumber - 1u) { if (it < rowIndex) it else it + 1u },
            KoneUIntArray(matrix.columnNumber - 1u) { if (it < columnIndex) it else it + 1u }
        )
}

public class MatrixMinorComputerViaBruteForceFeatureProvider<N>(private val ring: Ring<N>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == MatrixMinorComputerFeature::class) MatrixMinorComputerFeature(this) { rowIndices, columnIndices ->
            require(rowIndices.size == columnIndices.size)
            val minorSize = rowIndices.size
            if ((defaultEquality<UInt>()) { rowIndices.hasDuplicates() || columnIndices.hasDuplicates() }) return@MatrixMinorComputerFeature ring.zero

            (0u ..< minorSize).toKoneIterableList().permutations().fold(ring.zero) { result, permutation ->
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
                ring { result + permutation.foldIndexed(ring.one) { row, product, column -> product * this@getFeature[rowIndices[row], columnIndices[column]] }.let { if (permutationIsEven) it else -it } }
            }
        } as F else null
}

public class MatrixMinorComputerViaGaussianAlgorithmFeatureProvider<N>(private val field: Field<N>, private val mdListTransformer: ContextualSettableMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == MatrixMinorComputerFeature::class) MatrixMinorComputerFeature(this) { rowIndices, columnIndices ->
            require(rowIndices.size == columnIndices.size)
            val minorSize = rowIndices.size
            if ((defaultEquality<UInt>()) { rowIndices.hasDuplicates() || columnIndices.hasDuplicates() }) return@MatrixMinorComputerFeature field.zero

            val minor = mdListTransformer.settableMdList2(minorSize, minorSize) { row, column -> this[rowIndices[row], columnIndices[column]] }

            var fromRow = 0u
            var changeSign = false
            while (fromRow < minor.columnNumber && fromRow < minor.rowNumber) {
                var coolRow = fromRow
                while (coolRow < minor.rowNumber) {
                    if (field { minor[coolRow, fromRow].isNotZero() }) break
                    coolRow++
                }
                if (coolRow == minor.rowNumber) {
                    return@MatrixMinorComputerFeature field.zero
                }

                if (coolRow != fromRow) {
                    changeSign = !changeSign
                    for (column in 0u..<minor.columnNumber) {
                        minor[coolRow, column] =
                            minor[fromRow, column].also { minor[fromRow, column] = minor[coolRow, column] }
                    }
                }

                val coolCoef = minor[fromRow, fromRow]

                for (row in fromRow + 1u ..< minor.rowNumber) {
                    val rowCoef = minor[row, fromRow]
                    for (column in fromRow + 1u .. minor.columnNumber)
                        minor[row, column] = field { minor[row, column] - minor[fromRow, column] / coolCoef * rowCoef }
                }

                fromRow++
            }

            (0u ..< minor.rowNumber).fold(field.one.let { if (!changeSign) it else field { -it } }) { acc, index -> field { acc * minor[index, index] } }
        } as F else null
}

public data class UpperTriangleMatrixFeature(val value: Boolean)

public class UpperTriangleMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == UpperTriangleMatrixFeature::class) UpperTriangleMatrixFeature(mdListTransformer { this.coefficients.allIndexed { index, it -> if (index[0u] > index[1u]) ring { it.isZero() } else true } }) as F else null
}

public data class LowerTriangleMatrixFeature(val value: Boolean)

public class LowerTriangleMatrixFeatureProvider<N>(private val ring: Ring<N>, private val mdListTransformer: ContextualMDListTransformer<N, *>): FeatureProvider<Matrix<N, *>> {
    override fun <F : Any> Matrix<N, *>.getFeature(type: KClass<F>): F? =
        if (type == LowerTriangleMatrixFeature::class) LowerTriangleMatrixFeature(mdListTransformer { this.coefficients.allIndexed { index, it -> if (index[0u] < index[1u]) ring { it.isZero() } else true } }) as F else null
}