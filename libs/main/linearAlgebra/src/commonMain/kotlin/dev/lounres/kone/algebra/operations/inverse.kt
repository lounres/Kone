/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebra.operations


//public interface InverseMatrixComputer<Number, in InputContent2: MDList2<Number>, out OutputContent2: MDList2<Number>> : KoneContext {
//    public fun Matrix<Number, InputContent2>.inverse(): Matrix<Number, OutputContent2>
//
//    public companion object;
//
//    public class Key<Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>>(
//        elementType: SuppliedType,
//        inputContent2Type: SuppliedType,
//        outputContent2Type: SuppliedType,
//    ) : RegistryKey<InverseMatrixComputer<Number, InputContent2, OutputContent2>> {
//        public val typeKey: SuppliedType.Regular =
//            @OptIn(DelicateSuppliedTypeConstructor::class)
//            SuppliedType.Regular(
//                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.InverseMatrixComputer",
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
//context(inverseMatrixComputer: InverseMatrixComputer<Number, InputContent2, OutputContent2>)
//public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> Matrix<Number, InputContent2>.inverse(): Matrix<Number, OutputContent2> =
//    with(inverseMatrixComputer) { this@inverse.inverse() }
//
//private class InverseMatrixViaAdjugateMatrixComputer<Number, in InputContent2: MDList2<Number>, out OutputContent2: MDList2<Number>>(
//    private val field: Field<Number>,
//    private val vectorKategory: VectorKategory<Number, *, OutputContent2>,
//    private val adjugateMatrixComputer: AdjugateMatrixComputer<Number, InputContent2, OutputContent2>,
//    private val determinantComputer: DeterminantComputer<Number, InputContent2>,
//) : InverseMatrixComputer<Number, InputContent2, OutputContent2> {
//    override fun Matrix<Number, InputContent2>.inverse(): Matrix<Number, OutputContent2> = context(field, vectorKategory, adjugateMatrixComputer, determinantComputer) {
//        val determinant = this.determinant()
//        require(field { determinant.isNotZero() }) { "Cannot inverse matrix with zero determinant" }
//        this.adjugate() / determinant
//    }
//}
//
//public fun <Number, InputContent2: MDList2<Number>, OutputContent2: MDList2<Number>> InverseMatrixComputer.Companion.viaAdjugation(
//    field: Field<Number>,
//    vectorKategory: VectorKategory<Number, *, OutputContent2>,
//    adjugateMatrixComputer: AdjugateMatrixComputer<Number, InputContent2, OutputContent2>,
//    determinantComputer: DeterminantComputer<Number, InputContent2>,
//): InverseMatrixComputer<Number, InputContent2, OutputContent2> = InverseMatrixViaAdjugateMatrixComputer(
//    field = field,
//    vectorKategory = vectorKategory,
//    adjugateMatrixComputer = adjugateMatrixComputer,
//    determinantComputer = determinantComputer,
//)
//
//public fun <Number, InputContent2: MDList2<Number>, OutputContent1: MDList1<Number>, OutputContent2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setInverseMatrixViaAdjugateMatrixComputer(
//    numberType: SuppliedType,
//    inputContent2Type: SuppliedType,
//    outputContent1Type: SuppliedType,
//    outputContent2Type: SuppliedType,
//) {
//    this[InverseMatrixComputer.Key<Number, InputContent2, OutputContent2>(numberType, inputContent2Type, outputContent2Type)] =
//        InverseMatrixComputer.viaAdjugation(
//            field = this[Field.Key<Number>(numberType)],
//            vectorKategory = this[VectorKategory.Key<Number, OutputContent1, OutputContent2>(numberType, outputContent1Type, outputContent2Type)],
//            adjugateMatrixComputer = this[AdjugateMatrixComputer.Key<Number, InputContent2, OutputContent2>(numberType, inputContent2Type, outputContent2Type)],
//            determinantComputer = this[DeterminantComputer.Key<Number, InputContent2>(numberType, inputContent2Type)],
//        )
//}