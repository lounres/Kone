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
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.context
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.context.loadOrNull
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance
import kotlin.sequences.fold


public interface DeterminantComputer<Number> {
    public val Matrix<Number>.det: Number
    
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<DeterminantComputer<Number>> {
        override val typeKey: SuppliedType.Regular<DeterminantComputer<Number>> =
            SuppliedType.Regular(
                kClass = DeterminantComputer::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}

context(determinantComputer: DeterminantComputer<Number>)
public val <Number> Matrix<Number>.det get() = with(determinantComputer) { this@det.det }

public class NonSquareMatrixDeterminantComputationAttemptException : IllegalArgumentException("Cannot compute determinant of non-square matrix")
public fun nonSquareMatrixDeterminantComputationAttemptException(): Nothing = throw NonSquareMatrixDeterminantComputationAttemptException()

context(_: Ring<Number>)
public val <Number> Matrix<Number>.determinantViaLeibnizFormula: Number
    get() {
        if (rowNumber != columnNumber) nonSquareMatrixDeterminantComputationAttemptException()
        
        return (0u ..< rowNumber).toKoneList().permutations().fold(zero) { result, permutation ->
            val permutationIsEven = permutation.isEvenPermutation()
            
            result + permutation.foldIndexed(one) { row, product, column -> product * this[row, column] }.let { if (permutationIsEven) it else -it }
        }
    }

internal class DeterminantViaLeibnizFormulaComputer<Number>(val numberContext: Ring<Number>) : DeterminantComputer<Number> {
    override val Matrix<Number>.det: Number get() = context(numberContext) { determinantViaLeibnizFormula }
}

public val <Number> Ring<Number>.determinantViaLeibnizFormulaComputer: DeterminantComputer<Number>
    get() = DeterminantViaLeibnizFormulaComputer(this)

public fun <Number> KoneContextRegistryBuilder.installDeterminantViaLeibnizFormulaComputer(
    numberType: SuppliedType<Number>,
) {
    contextsBuilder[DeterminantComputer.Key(numberType)] = contextsBuilder[Ring.Key(numberType)].determinantViaLeibnizFormulaComputer
}

context(_: Field<Number>)
public val <Number> Matrix<Number>.determinantViaGaussianElimination: Number
    get() {
        if (rowNumber != columnNumber) nonSquareMatrixDeterminantComputationAttemptException()
        
        val matrix = ArrayMDList2(rowNumber, columnNumber) { rowIndex, columnIndex -> this.coefficients[rowIndex, columnIndex] }
        
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

internal class DeterminantViaGaussianEliminationComputer<Number>(val numberContext: Field<Number>) : DeterminantComputer<Number> {
    override val Matrix<Number>.det: Number get() = context(numberContext) { determinantViaGaussianElimination }
}

public val <Number> Field<Number>.determinantViaGaussianEliminationComputer: DeterminantComputer<Number>
    get() = DeterminantViaGaussianEliminationComputer(this)

public fun <Number> KoneContextRegistryBuilder.installDeterminantViaGaussianEliminationComputer(
    numberType: SuppliedType<Number>,
) {
    contextsBuilder[DeterminantComputer.Key(numberType)] = contextsBuilder[Field.Key(numberType)].determinantViaGaussianEliminationComputer
}

//context(koneContextRegistry: KoneContextRegistry, _: Ring<Number>)
//public fun <Number> Matrix<Number>.det(numberType: SuppliedType<Number>): Number {
//    val determinantComputer = koneContextRegistry.loadOrNull(DeterminantComputer.Key(numberType))
//    return if (determinantComputer != null) context(determinantComputer) { this.det } else this.determinantViaLeibnizFormula
//}