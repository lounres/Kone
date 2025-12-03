/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebra.operations



//public interface DeterminantComputer<Number, in Content2: MDList2<Number>> : KoneContext {
//    public fun Matrix<Number, Content2>.determinant(): Number
//
//    public companion object;
//
//    public class Key<Number, Content2: MDList2<Number>>(
//        elementType: SuppliedType,
//        content2Type: SuppliedType,
//    ) : RegistryKey<DeterminantComputer<Number, Content2>> {
//        public val typeKey: SuppliedType.Regular =
//            @OptIn(DelicateSuppliedTypeConstructor::class)
//            SuppliedType.Regular(
//                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.DeterminantComputer",
//                typeArguments = listOf(
//                    SuppliedProjection.Regular(
//                        variance = INVARIANT,
//                        type = elementType
//                    ),
//                    SuppliedProjection.Regular(
//                        variance = IN,
//                        type = content2Type
//                    ),
//                ),
//                isNullable = false
//            )
//        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
//        override fun hashCode(): Int = typeKey.hashCode()
//    }
//}
//
//context(determinantComputer: DeterminantComputer<Number, Content2>)
//public fun <Number, Content2: MDList2<Number>> Matrix<Number, Content2>.determinant(): Number =
//    with(determinantComputer) { this@determinant.determinant() }
//
//public class NonSquareMatrixDeterminantComputationAttemptException : IllegalArgumentException("Cannot compute determinant of non-square matrix")
//public fun nonSquareMatrixDeterminantComputationAttemptException(): Nothing = throw NonSquareMatrixDeterminantComputationAttemptException()
//
//private class DeterminantViaLeibnizFormulaComputer<Number>(val numberContext: Ring<Number>) : DeterminantComputer<Number, MDList2<Number>> {
//    override fun Matrix<Number, MDList2<Number>>.determinant(): Number = numberContext {
//        if (rowNumber != columnNumber) nonSquareMatrixDeterminantComputationAttemptException()
//
//        (0u ..< rowNumber).toKoneList().permutations().fold(zero) { result, permutation ->
//            val permutationIsEven = permutation.isEvenPermutation()
//
//            result + permutation.foldIndexed(one) { row, product, column -> product * this[row, column] }.let { if (permutationIsEven) it else -it }
//        }
//    }
//}
//
//public fun <Number, Content2: MDList2<Number>> DeterminantComputer.Companion.viaLeibnizFormula(
//    ring: Ring<Number>,
//): DeterminantComputer<Number, Content2> = DeterminantViaLeibnizFormulaComputer(ring)
//
//public fun <Number, Content2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setDeterminantViaLeibnizFormulaComputer(
//    numberType: SuppliedType,
//    content2Type: SuppliedType,
//) {
//    this[DeterminantComputer.Key<Number, Content2>(numberType, content2Type)] = DeterminantComputer.viaLeibnizFormula(this[Ring.Key<Number>(numberType)])
//}
//
//private class DeterminantViaGaussianEliminationComputer<Number>(val numberContext: Field<Number>) : DeterminantComputer<Number, MDList2<Number>> {
//    override fun Matrix<Number, MDList2<Number>>.determinant(): Number = numberContext {
//        if (rowNumber != columnNumber) nonSquareMatrixDeterminantComputationAttemptException()
//
//        val matrix = ArrayMDList2(rowNumber, columnNumber) { rowIndex, columnIndex -> this.coefficients[rowIndex, columnIndex] }
//
//        var applyMinus = false
//        var fromRow = 0u
//        while (fromRow < matrix.rowNumber) {
//            var coolRow = fromRow
//            while (coolRow < matrix.rowNumber) {
//                if (matrix[coolRow, fromRow].isNotZero()) break
//                coolRow++
//            }
//            if (coolRow == matrix.rowNumber) {
//                return zero
//            }
//
//            if (coolRow != fromRow) {
//                applyMinus = !applyMinus
//                for (column in fromRow ..< matrix.columnNumber) {
//                    matrix[coolRow, column] = matrix[fromRow, column].also { matrix[fromRow, column] = matrix[coolRow, column] }
//                }
//            }
//
//            val coolCoef = matrix[fromRow, fromRow]
//
//            for (row in fromRow + 1u ..< matrix.rowNumber) {
//                val rowCoef = matrix[row, fromRow]
//                for (column in fromRow + 1u ..< matrix.columnNumber)
//                    matrix[row, column] = matrix[row, column] - matrix[fromRow, column] / coolCoef * rowCoef
//            }
//
//            fromRow++
//        }
//
//        return (0u ..< matrix.rowNumber).fold(if (applyMinus) -one else one) { acc, index -> acc * matrix[index, index] }
//    }
//}
//
//public fun <Number, Content2: MDList2<Number>> DeterminantComputer.Companion.viaGaussianElimination(
//    field: Field<Number>,
//): DeterminantComputer<Number, Content2> = DeterminantViaGaussianEliminationComputer(field)
//
//public fun <Number, Content2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setDeterminantViaGaussianEliminationComputer(
//    numberType: SuppliedType,
//    content2Type: SuppliedType,
//) {
//    this[DeterminantComputer.Key<Number, Content2>(numberType, content2Type)] = DeterminantComputer.viaGaussianElimination(this[Field.Key<Number>(numberType)])
//}