/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations


//private class LogarithmMatrixComputerViaSchurParlettUsingInverseScalingAndSquaring<Matrix : MDList2<ComplexNumber<Double>>>(
//    private val matrixFactory: MatrixFactory<ComplexNumber<Double>, Matrix>,
//    private val field: Field<Double>,
//    private val order: Order<Double>,
//    private val complexNumberFieldExtension: FieldExtension<Double, ComplexNumber<Double>>,
//    private val positiveSquareRootComputer: PositiveSquareRootComputer<Double>,
//    private val complexNumberExponentComputer: ExponentComputer<ComplexNumber<Double>>,
//    private val complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Double>>,
//    private val complexNumberLogarithmOnePlusInputOverInputComputer: LogarithmOnePlusInputOverInputComputer<ComplexNumber<Double>>,
//    private val complexNumberHyperbolicSineOverInputComputer: HyperbolicSineOverInputComputer<ComplexNumber<Double>>,
//    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Double>, Matrix>,
//    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Double>, Matrix>,
//    private val inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Double>, Matrix>,
//    private val schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Double>, Matrix>,
//    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Double>, Matrix>,
//    private val blockingParameter: Double,
//) : LogarithmComputer<Matrix> {
//    @OptIn(ParlettRecurrenceInternalApi::class)
//    private val parlettRecurrence =
//        ParlettRecurrence(
//            matrixFactory = matrixFactory,
//            field = field,
//            order = order,
//            complexNumberFieldExtension = complexNumberFieldExtension,
//            matrixCategoryOverField = matrixCategoryOverField,
//            matrixProductComputer = matrixProductComputer,
//            inverseMatrixComputer = inverseMatrixComputer,
//            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
//            parlettRecurrenceAtomicBlockImageComputer = object : ParlettRecurrenceAtomicBlockImageComputer<ComplexNumber<Double>, Matrix> {
//                override fun Matrix.atomicBlockImage(): Matrix =
//                    context(
////                        field,
////                        order,
//                        complexNumberFieldExtension,
////                        positiveSquareRootComputer,
////                        complexNumberExponentComputer,
//                        complexNumberLogarithmComputer,
//                        complexNumberLogarithmOnePlusInputOverInputComputer,
////                        matrixCategoryOverField,
////                        matrixProductComputer,
////                        inverseMatrixComputer,
//                    ) {
//                        val size = this.rowNumber
//                        when (size) {
//                            0u -> error(TODO())
//                            1u -> matrixFactory.generateMatrix(1u, 1u) { _, _ -> this[0u, 0u].logarithm() }
//                            2u -> matrixFactory.mapMatrix(
//                                rowNumber = 2u,
//                                columnNumber = 2u,
//                                numbers = KoneMap.build(
//                                    keyEquality = MDIndex.equality(),
//                                    keyHashing = MDIndex.hashing(),
//                                ) {
//                                    val a1 = this@atomicBlockImage[0u, 0u]
////                                    val a1AbsoluteValue = a1.absoluteValue()
//                                    val a2 = this@atomicBlockImage[1u, 1u]
////                                    val a2AbsoluteValue = a2.absoluteValue()
//                                    val b = this@atomicBlockImage[0u, 1u]
//                                    set(MDIndex.of(0u, 0u), a1.logarithm())
//                                    set(MDIndex.of(1u, 1u), a2.logarithm())
//                                    set(
//                                        MDIndex.of(0u, 1u),
//                                        b * when {
//                                            a1 eq a2 -> a1.reciprocal()
////                                            a1AbsoluteValue lt a2AbsoluteValue / 2 || a2AbsoluteValue lt a1AbsoluteValue / 2 -> (a1.logarithm() - a2.logarithm()) / (a1 - a2)
//                                            else -> ((a2 - a1) / a2).logarithmOnePlusThisOverThis() / a2
//                                        }
//                                    )
//                                }
//                            )
//                            else -> { // TODO
////                                val unitMatrix = matrixFactory.generateMatrix(size, size) { row, column -> if (row == column) complexNumberFieldExtension.one else complexNumberFieldExtension.zero }
////                                val thisOneNorm = this.sumOf<_, Double> { it.absoluteValue() }
////                                // Attempt m = 3
////                                if (thisOneNorm leq 1.5E-2) {
////                                    val this2 = this * this
////                                    val u = this * (this2 + unitMatrix * 60)
////                                    val v = this2 * 12 + unitMatrix * 120
////                                    return (-u + v).invert()!! * (u + v)
////                                }
////                                // Attempt m = 5
////                                if (thisOneNorm leq 2.5E-1) {
////                                    val this2 = this * this
////                                    val this4 = this2 * this2
////                                    val u = this * (this4 + this2 * 420 + unitMatrix * 15120)
////                                    val v = this4 * 30 + this2 * 3360 + unitMatrix * 30240
////                                    return (-u + v).invert()!! * (u + v)
////                                }
////                                // Attempt m = 7
////                                if (thisOneNorm leq 9.5E-1) {
////                                    val this2 = this * this
////                                    val this4 = this2 * this2
////                                    val this6 = this4 * this2
////                                    val u = this * (this6 + this4 * 1512 + this2 * 277200 + unitMatrix * 8648640)
////                                    val v = this6 * 56 + this4 * 25200 + this2 * 1995840 + unitMatrix * 17297280
////                                    return (-u + v).invert()!! * (u + v)
////                                }
////                                // Attempt m = 9
////                                if (thisOneNorm leq 2.1E0) {
////                                    val this2 = this * this
////                                    val this4 = this2 * this2
////                                    val this6 = this4 * this2
////                                    val this8 = this4 * this4
////                                    val u = this * (this8 + this6 * 3960 + this4 * 2162160 + this2 * 302702400 + unitMatrix * 8821612800)
////                                    val v = this8 * 90 + this6 * 110880 + this4 * 30270240 + this2 * 2075673600 + unitMatrix * 17643225600
////                                    return (-u + v).invert()!! * (u + v)
////                                }
////                                // m = 13 with scaling and squaring
////                                val s = ceil(log2(thisOneNorm / 5.4E0)).toUInt()
////                                val a = this / ComplexNumber(2.0.pow(s), 0.0)
////                                val a2 = a * a
////                                val a4 = a2 * a2
////                                val a6 = a2 *  a4
////                                val u = a * (a6 * (a6 + a4 * 16380 + a2 * 40840800) + a6 * 33522128640 + a4 * 10559470521600 + a2 * 1187353796428800 + unitMatrix * 32382376266240000)
////                                val v = a6 * (a6 * 182 + a4 * 960960 + a2 * 1323241920) + a6 * 670442572800 + a4 * 129060195264000 + a2 * 7771770303897600 + unitMatrix * 64764752532480000
////                                var r13 = (-u + v).invert()!! * (u + v)
////                                repeat(s) { r13 *= r13 }
////                                r13
//                                TODO()
//                            }
//                        }
//                    }
//            },
//            blockingParameter = blockingParameter,
//        )
//
//    override fun Matrix.logarithm(): Matrix {
//        require(this.rowNumber == this.columnNumber)
//        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
//        @OptIn(ParlettRecurrenceInternalApi::class)
//        val image = parlettRecurrence { schurDecomposition.middleUpperTriangular.image() }
//        return matrixProductComputer { schurDecomposition.leftUnitary * image * schurDecomposition.rightUnitary }
//    }
//}
//
//public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.viaSchurParlett(
//    matrixFactory: MatrixFactory<Number, Matrix>,
//    field: Field<Number>,
//    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
//    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
//    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
//    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
//): LogarithmComputer<Matrix> = LogarithmMatrixComputerViaSchurParlettUsingInverseScalingAndSquaring(
//    matrixFactory = matrixFactory,
//    field = field,
//    matrixCategoryOverField = matrixCategoryOverField,
//    matrixProductComputer = matrixProductComputer,
//    schurDecompositionComputer = schurDecompositionComputer,
//    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
//)
//
//context(koneContextRegistry: KoneContextRegistry.Provider)
//public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.viaSchurParlett(
//    numberType: SuppliedType,
//    matrixType: SuppliedType,
//): LogarithmComputer<Matrix> {
//    val koneContextRegistry = koneContextRegistry.get()
//    return viaSchurParlett(
//        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)) {
//            "LogarithmComputer.viaSchurParlett<$numberType, $matrixType>"
//        },
//        field = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
//            "LogarithmComputer.viaSchurParlett<$numberType, $matrixType>"
//        },
//        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<Number, Matrix>(matrixType = matrixType)) {
//            "LogarithmComputer.viaSchurParlett<$numberType, $matrixType>"
//        },
//        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<Number, Matrix>(matrixType = matrixType)) {
//            "LogarithmComputer.viaSchurParlett<$numberType, $matrixType>"
//        },
//        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType)) {
//            "LogarithmComputer.viaSchurParlett<$numberType, $matrixType>"
//        },
//        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<Number, Matrix>(matrixType = matrixType)) {
//            "LogarithmComputer.viaSchurParlett<$numberType, $matrixType>"
//        },
//    )
//}
//
//context(_: MutableOwnedRegistry<KoneContextRegistry>)
//public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaSchurParlettUsingInverseScalingAndSquaring(
//    matrixType: SuppliedType,
//    matrixFactory: MatrixFactory<Number, Matrix>,
//    field: Field<Number>,
//    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
//    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
//    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
//    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
//) {
//    LogarithmComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
//        viaSchurParlett(
//            matrixFactory = matrixFactory,
//            field = field,
//            matrixCategoryOverField = matrixCategoryOverField,
//            matrixProductComputer = matrixProductComputer,
//            schurDecompositionComputer = schurDecompositionComputer,
//            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
//        )
//    }
//}
//
//context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
//public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaSchurParlettUsingInverseScalingAndSquaring(
//    numberType: SuppliedType,
//    matrixType: SuppliedType,
//) {
//    LogarithmComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
//        viaSchurParlett(
//            numberType = numberType,
//            matrixType = matrixType,
//        )
//    }
//}
//
//context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
//public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.useViaSchurParlettUsingInverseScalingAndSquaring(
//    numberType: SuppliedType,
//    matrixType: SuppliedType,
//    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
//    field: Field<Number>,
//    matrixCategoryOverField: MatrixCategoryOverField<Number, MatrixWithProperties<Number, Matrix>>,
//    matrixProductComputer: MatrixProductComputer<Number, MatrixWithProperties<Number, Matrix>>,
//    schurDecompositionComputer: SchurDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
//    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
//) {
//    @OptIn(DelicateSuppliedTypeConstructor::class)
//    val matrixWithPropertiesType = SuppliedType.Regular(
//        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
//        typeArguments = listOf(
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = numberType,
//            ),
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = matrixType,
//            ),
//        ),
//        isNullable = false,
//    )
//    LogarithmKey<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
//        val logarithmComputer = viaSchurParlett<Number, MatrixWithProperties<Number, Matrix>>(
//            matrixFactory = matrixFactory,
//            field = field,
//            matrixCategoryOverField = matrixCategoryOverField,
//            matrixProductComputer = matrixProductComputer,
//            schurDecompositionComputer = schurDecompositionComputer,
//            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
//        )
//        logarithmComputer { Some(matrix.get().logarithm()) }
//    }
//}
//
//context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
//public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.useViaSchurParlettUsingInverseScalingAndSquaring(
//    numberType: SuppliedType,
//    matrixType: SuppliedType,
//) {
//    @OptIn(DelicateSuppliedTypeConstructor::class)
//    val matrixWithPropertiesType = SuppliedType.Regular(
//        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
//        typeArguments = listOf(
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = numberType,
//            ),
//            SuppliedProjection.Regular(
//                variance = INVARIANT,
//                type = matrixType,
//            ),
//        ),
//        isNullable = false,
//    )
//    LogarithmKey<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
//        val logarithmComputer = viaSchurParlett<Number, MatrixWithProperties<Number, Matrix>>(
//            numberType = numberType,
//            matrixType = matrixWithPropertiesType,
//        )
//        logarithmComputer { Some(matrix.get().logarithm()) }
//    }
//}