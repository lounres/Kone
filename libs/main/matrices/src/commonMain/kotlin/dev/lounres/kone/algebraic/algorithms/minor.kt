/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms



//public interface MinorComputer<out Number, in Matrix> : KoneContext {
//    public fun Matrix.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number
//    public fun Matrix.firstMinor(rowIndex: UInt, columnIndex: UInt): Number
//    public fun Matrix.cofactor(rowIndex: UInt, columnIndex: UInt): Number
//
//    public companion object;
//
//    public class Key<Number, Matrix>(
//        elementType: SuppliedType,
//        matrixType: SuppliedType,
//    ) : RegistryKey<MinorComputer<Number, Matrix>> {
//        public val typeKey: SuppliedType.Regular =
//            @OptIn(DelicateSuppliedTypeConstructor::class)
//            SuppliedType.Regular(
//                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.MinorComputer",
//                typeArguments = listOf(
//                    SuppliedProjection.Regular(
//                        variance = OUT,
//                        type = elementType
//                    ),
//                    SuppliedProjection.Regular(
//                        variance = IN,
//                        type = matrixType
//                    ),
//                ),
//                isNullable = false
//            )
//        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
//        override fun hashCode(): Int = typeKey.hashCode()
//    }
//}
//
//context(minorComputer: MinorComputer<Number, Matrix>)
//public fun <Number, Matrix> Matrix.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number =
//    with(minorComputer) { this@minor.minor(rowIndices, columnIndices) }
//
//context(minorComputer: MinorComputer<Number, Matrix>)
//public fun <Number, Matrix> Matrix.firstMinor(rowIndex: UInt, columnIndex: UInt): Number =
//    with(minorComputer) { this@firstMinor.firstMinor(rowIndex, columnIndex) }
//
//context(minorComputer: MinorComputer<Number, Matrix>)
//public fun <Number, Matrix> Matrix.cofactor(rowIndex: UInt, columnIndex: UInt): Number =
//    with(minorComputer) { this@cofactor.cofactor(rowIndex, columnIndex) }
//
//private class MinorViaLeibnizFormulaComputer<Number>(
//    val ring: CommutativeRing<Number>,
//) : MinorComputer<Number, MDList2<Number>> {
//    override fun MDList2<Number>.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number = ring {
//        require(rowIndices.size == columnIndices.size) { TODO("Error message is not specified") }
//        val minorSize = rowIndices.size
//        if (rowIndices.hasDuplicates() || columnIndices.hasDuplicates()) return zero
//
//        (0u ..< minorSize).toKoneList().permutations().fold(zero) { result, permutation ->
//            val permutationIsEven = permutation.isEvenPermutation()
//
//            result + permutation.foldIndexed(one) { row, product, column -> product * this[rowIndices[row], columnIndices[column]] }.let { if (permutationIsEven) it else -it }
//        }
//    }
//    override fun MDList2<Number>.firstMinor(rowIndex: UInt, columnIndex: UInt): Number {
//        require(this.rowNumber == this.columnNumber) { TODO("Error message is not specified") }
//
//        return minor(
//            KoneUIntArray(this.rowNumber - 1u) { if (it < rowIndex) it else it + 1u },
//            KoneUIntArray(this.columnNumber - 1u) { if (it < columnIndex) it else it + 1u },
//        )
//    }
//    override fun MDList2<Number>.cofactor(rowIndex: UInt, columnIndex: UInt): Number = ring {
//        firstMinor(rowIndex, columnIndex).let { if ((rowIndex + columnIndex) % 2u == 0u) it else -it }
//    }
//}
//
//public fun <Number> MinorComputer.Companion.viaLeibnizFormula(
//    ring: CommutativeRing<Number>,
//): MinorComputer<Number, MDList2<Number>> = MinorViaLeibnizFormulaComputer(ring)
//
//context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
//public fun <Number> MinorComputer.Companion.setViaLeibnizFormulaFor(
//    numberType: SuppliedType,
//) {
//    koneContextRegistryBuilder[MinorComputer.Key<Number, MDList2<Number>>(numberType, content2Type)] =
//        MinorComputer.viaLeibnizFormula(koneContextRegistryBuilder[CommutativeRing.Key<Number>(numberType)])
//}
//
//private class MinorViaGaussianEliminationComputer<Number>(
//    val field: Field<Number>,
//) : MinorComputer<Number, MDList2<Number>> {
//    override fun MDList2<Number>.minor(rowIndices: KoneUIntArray, columnIndices: KoneUIntArray): Number = field {
//        require(rowIndices.size == columnIndices.size) { TODO("Error message is not specified") }
//        val minorSize = rowIndices.size
//        if (rowIndices.hasDuplicates(elementEquality = Equality.defaultFor(), elementHashing = Hashing.defaultFor(), elementOrder = Order.defaultFor())) return zero
//        if (columnIndices.hasDuplicates(elementEquality = Equality.defaultFor(), elementHashing = Hashing.defaultFor(), elementOrder = Order.defaultFor())) return zero
//
//        val matrix = ArrayMDList2(minorSize, minorSize) { rowIndex, columnIndex -> this[rowIndices[rowIndex], columnIndices[columnIndex]] }
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
//        (0u ..< matrix.rowNumber).fold(if (applyMinus) -one else one) { acc, index -> acc * matrix[index, index] }
//    }
//    override fun MDList2<Number>.firstMinor(rowIndex: UInt, columnIndex: UInt): Number {
//        require(this.rowNumber == this.columnNumber) { TODO("Error message is not specified") }
//
//        return minor(
//            KoneUIntArray(this.rowNumber - 1u) { if (it < rowIndex) it else it + 1u },
//            KoneUIntArray(this.columnNumber - 1u) { if (it < columnIndex) it else it + 1u },
//        )
//    }
//    override fun MDList2<Number>.cofactor(rowIndex: UInt, columnIndex: UInt): Number = field {
//        firstMinor(rowIndex, columnIndex).let { if ((rowIndex + columnIndex) % 2u == 0u) it else -it }
//    }
//}
//
//public fun <Number, Content2: MDList2<Number>> MinorComputer.Companion.minorViaGaussianEliminationComputer(
//    field: Field<Number>,
//): MinorComputer<Number, Content2> = MinorViaGaussianEliminationComputer(field)
//
//public fun <Number, Content2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setMinorViaGaussianEliminationComputer(
//    numberType: SuppliedType,
//    content2Type: SuppliedType,
//) {
//    this[MinorComputer.Key<Number, Content2>(numberType, content2Type)] =
//        MinorComputer.minorViaGaussianEliminationComputer(this[Field.Key<Number>(numberType)])
//}