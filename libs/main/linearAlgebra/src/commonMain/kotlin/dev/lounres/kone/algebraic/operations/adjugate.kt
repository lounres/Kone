/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.operations


//public interface AdjugateMatrixComputer<Number, in InputContent2: MDList2<Number>, out OutputContent2: MDList2<Number>> : KoneContext {
//    public fun Matrix<Number, InputContent2>.adjugate(): Matrix<Number, OutputContent2>
//
//    public companion object;
//
//    public class Key<Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>>(
//        elementType: SuppliedType,
//        inputContent2Type: SuppliedType,
//        outputContent2Type: SuppliedType,
//    ) : RegistryKey<AdjugateMatrixComputer<Number, InputContent2, OutputContent2>> {
//        public val typeKey: SuppliedType.Regular =
//            @OptIn(DelicateSuppliedTypeConstructor::class)
//            SuppliedType.Regular(
//                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.AdjugateMatrixComputer",
//                typeArguments = listOf(
//                    SuppliedProjection.Regular(
//                        variance = INVARIANT,
//                        type = elementType
//                    ),
//                    SuppliedProjection.Regular(
//                        variance = IN,
//                        type = inputContent2Type
//                    ),
//                    SuppliedProjection.Regular(
//                        variance = OUT,
//                        type = outputContent2Type
//                    ),
//                ),
//                isNullable = false
//            )
//        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
//        override fun hashCode(): Int = typeKey.hashCode()
//    }
//}
//
//context(adjugateMatrixComputer: AdjugateMatrixComputer<Number, InputContent2, OutputContent2>)
//public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> Matrix<Number, InputContent2>.adjugate(): Matrix<Number, OutputContent2> =
//    with(adjugateMatrixComputer) { this@adjugate.adjugate() }
//
//private class MinorComputerBasedAdjugateMatrixComputer<Number, in InputContent2: MDList2<Number>, out OutputContent2: MDList2<Number>>(
//    private val minorComputer: MinorComputer<Number, InputContent2>,
//    private val outputContent2Producer: (rowNumber: UInt, colNumber: UInt, (row: UInt, column: UInt) -> Number) -> OutputContent2,
//) : AdjugateMatrixComputer<Number, InputContent2, OutputContent2> {
//    override fun Matrix<Number, InputContent2>.adjugate(): Matrix<Number, OutputContent2> = minorComputer {
//        require(this.rowNumber == this.columnNumber) { "Cannot adjugate non-square matrix." }
//        Matrix(
//            outputContent2Producer(columnNumber, rowNumber) { column, row ->
//                this.cofactor(row, column)
//            }
//        )
//    }
//}
//
//public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> AdjugateMatrixComputer.Companion.default(
//    minorComputer: MinorComputer<Number, InputContent2>,
//    outputContent2Producer: (rowNumber: UInt, colNumber: UInt, (row: UInt, column: UInt) -> Number) -> OutputContent2,
//): AdjugateMatrixComputer<Number, InputContent2, OutputContent2> = MinorComputerBasedAdjugateMatrixComputer(minorComputer, outputContent2Producer)
//
//public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setMinorComputerBasedAdjugateMatrixComputer(
//    numberType: SuppliedType,
//    inputContent2Type: SuppliedType,
//    outputContent2Type: SuppliedType,
//    outputContent2Producer: (rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> Number) -> OutputContent2,
//) {
//    AdjugateMatrixComputer.Key<Number, InputContent2, OutputContent2>(numberType, inputContent2Type, outputContent2Type) correspondsTo
//            AdjugateMatrixComputer.default<Number, InputContent2, OutputContent2>(
//                minorComputer = this[MinorComputer.Key(numberType, inputContent2Type)],
//                outputContent2Producer = outputContent2Producer,
//            )
//}