/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.multidimensionalCollections.utils.sumOf
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.leq
import dev.lounres.kone.repeat
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import dev.lounres.kone.suppliedTypes.suppliedType
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.reflect.KVariance.INVARIANT


private class ExponentMatrixComputerViaSchurParlettUsingScalingAndSquaring<Matrix : MDList2<ComplexNumber<Double>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Double>, Matrix>,
    private val field: Field<Double>,
    private val order: Order<Double>,
    private val complexNumberFieldExtension: FieldExtension<Double, ComplexNumber<Double>>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Double>,
    private val complexNumberExponentComputer: ExponentComputer<ComplexNumber<Double>>,
    private val complexNumberHyperbolicSineOverInputComputer: HyperbolicSineOverInputComputer<ComplexNumber<Double>>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Double>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Double>, Matrix>,
    private val inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Double>, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Double>, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Double>, Matrix>,
    private val blockingParameter: Double,
) : ExponentComputer<Matrix> {
    @OptIn(ParlettRecurrenceInternalApi::class)
    private val parlettRecurrence =
        ParlettRecurrence(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberFieldExtension = complexNumberFieldExtension,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            inverseMatrixComputer = inverseMatrixComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
            parlettRecurrenceAtomicBlockImageComputer = object : ParlettRecurrenceAtomicBlockImageComputer<ComplexNumber<Double>, Matrix> {
                override fun Matrix.atomicBlockImage(): Matrix =
                    context(
                        field,
                        order,
                        complexNumberFieldExtension,
                        positiveSquareRootComputer,
                        complexNumberExponentComputer,
                        complexNumberHyperbolicSineOverInputComputer,
                        matrixCategoryOverField,
                        matrixProductComputer,
                        inverseMatrixComputer,
                    ) {
                        val size = this.rowNumber
                        when (size) {
                            0u -> error(TODO())
                            1u -> matrixFactory.generateMatrix(1u, 1u) { _, _ -> this[0u, 0u].exponent() }
                            2u -> matrixFactory.mapMatrix(
                                rowNumber = 2u,
                                columnNumber = 2u,
                                numbers = KoneMap.build(
                                    keyEquality = MDIndex.equality(),
                                    keyHashing = MDIndex.hashing(),
                                ) {
                                    val a1 = this@atomicBlockImage[0u, 0u]
                                    val a2 = this@atomicBlockImage[1u, 1u]
                                    val b = this@atomicBlockImage[0u, 1u]
                                    set(MDIndex.of(0u, 0u), a1.exponent())
                                    set(MDIndex.of(1u, 1u), a2.exponent())
                                    set(MDIndex.of(0u, 1u), b * ((a1 + a2) / 2).exponent() * ((a2 - a1) / 2).sinhOverThis())
                                }
                            )
                            else -> {
                                val unitMatrix = matrixFactory.generateMatrix(size, size) { row, column -> if (row == column) complexNumberFieldExtension.one else complexNumberFieldExtension.zero }
                                val thisOneNorm = this.sumOf<_, Double> { it.absoluteValue() }
                                // Attempt m = 3
                                if (thisOneNorm leq 1.5E-2) {
                                    val this2 = this * this
                                    val u = this * (this2 + unitMatrix * 60)
                                    val v = this2 * 12 + unitMatrix * 120
                                    return (-u + v).invert()!! * (u + v)
                                }
                                // Attempt m = 5
                                if (thisOneNorm leq 2.5E-1) {
                                    val this2 = this * this
                                    val this4 = this2 * this2
                                    val u = this * (this4 + this2 * 420 + unitMatrix * 15120)
                                    val v = this4 * 30 + this2 * 3360 + unitMatrix * 30240
                                    return (-u + v).invert()!! * (u + v)
                                }
                                // Attempt m = 7
                                if (thisOneNorm leq 9.5E-1) {
                                    val this2 = this * this
                                    val this4 = this2 * this2
                                    val this6 = this4 * this2
                                    val u = this * (this6 + this4 * 1512 + this2 * 277200 + unitMatrix * 8648640)
                                    val v = this6 * 56 + this4 * 25200 + this2 * 1995840 + unitMatrix * 17297280
                                    return (-u + v).invert()!! * (u + v)
                                }
                                // Attempt m = 9
                                if (thisOneNorm leq 2.1E0) {
                                    val this2 = this * this
                                    val this4 = this2 * this2
                                    val this6 = this4 * this2
                                    val this8 = this4 * this4
                                    val u = this * (this8 + this6 * 3960 + this4 * 2162160 + this2 * 302702400 + unitMatrix * 8821612800)
                                    val v = this8 * 90 + this6 * 110880 + this4 * 30270240 + this2 * 2075673600 + unitMatrix * 17643225600
                                    return (-u + v).invert()!! * (u + v)
                                }
                                // m = 13 with scaling and squaring
                                val s = ceil(log2(thisOneNorm / 5.4E0)).toUInt()
                                val a = this / ComplexNumber(2.0.pow(s), 0.0)
                                val a2 = a * a
                                val a4 = a2 * a2
                                val a6 = a2 *  a4
                                val u = a * (a6 * (a6 + a4 * 16380 + a2 * 40840800) + a6 * 33522128640 + a4 * 10559470521600 + a2 * 1187353796428800 + unitMatrix * 32382376266240000)
                                val v = a6 * (a6 * 182 + a4 * 960960 + a2 * 1323241920) + a6 * 670442572800 + a4 * 129060195264000 + a2 * 7771770303897600 + unitMatrix * 64764752532480000
                                var r13 = (-u + v).invert()!! * (u + v)
                                repeat(s) { r13 *= r13 }
                                r13
                            }
                        }
                    }
            },
            blockingParameter = blockingParameter,
        )
    
    override fun Matrix.exponent(): Matrix {
        require(this.rowNumber == this.columnNumber)
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        @OptIn(ParlettRecurrenceInternalApi::class)
        val image = parlettRecurrence { schurDecomposition.middleUpperTriangular.image() }
        return matrixProductComputer { schurDecomposition.leftUnitary * image * schurDecomposition.rightUnitary }
    }
}

public fun <Matrix : MDList2<ComplexNumber<Double>>> ExponentComputer.Companion.viaSchurParlettUsingScalingAndSquaring(
    matrixFactory: MatrixFactory<ComplexNumber<Double>, Matrix>,
    field: Field<Double>,
    order: Order<Double>,
    complexNumberFieldExtension: FieldExtension<Double, ComplexNumber<Double>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Double>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Double>>,
    complexNumberHyperbolicSineOverInputComputer: HyperbolicSineOverInputComputer<ComplexNumber<Double>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Double>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Double>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Double>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Double>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Double>, Matrix>,
    blockingParameter: Double,
): ExponentComputer<Matrix> = ExponentMatrixComputerViaSchurParlettUsingScalingAndSquaring(
    matrixFactory = matrixFactory,
    field = field,
    order = order,
    complexNumberFieldExtension = complexNumberFieldExtension,
    positiveSquareRootComputer = positiveSquareRootComputer,
    complexNumberExponentComputer = complexNumberExponentComputer,
    complexNumberHyperbolicSineOverInputComputer = complexNumberHyperbolicSineOverInputComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    inverseMatrixComputer = inverseMatrixComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
    blockingParameter = blockingParameter,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Matrix : MDList2<ComplexNumber<Double>>> ExponentComputer.Companion.viaSchurParlettUsingScalingAndSquaring(
    matrixType: SuppliedType,
    blockingParameter: Double,
): ExponentComputer<Matrix> {
    val numberType = Double.suppliedType
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            ),
        ),
        isNullable = false,
    )
    val koneContextRegistry = koneContextRegistry.get()
    return viaSchurParlettUsingScalingAndSquaring(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Double>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Double>(numberType = numberType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        order = koneContextRegistry.requestFor(Order.Key<Double>(elementType = numberType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Double, ComplexNumber<Double>>(numberType = numberType, vectorType = complexNumberType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Double>(numberType = numberType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        complexNumberExponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<ComplexNumber<Double>>(numberType = complexNumberType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        complexNumberHyperbolicSineOverInputComputer = koneContextRegistry.requestFor(HyperbolicSineOverInputComputer.Key<ComplexNumber<Double>>(numberType = complexNumberType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Double>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Double>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        inverseMatrixComputer = koneContextRegistry.requestFor(InverseMatrixComputer.Key<ComplexNumber<Double>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<ComplexNumber<Double>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<ComplexNumber<Double>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, $matrixType>"
        },
        blockingParameter = blockingParameter,
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Matrix : MDList2<ComplexNumber<Double>>> ExponentComputer.Companion.setViaSchurParlettUsingScalingAndSquaring(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Double>, Matrix>,
    field: Field<Double>,
    order: Order<Double>,
    complexNumberFieldExtension: FieldExtension<Double, ComplexNumber<Double>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Double>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Double>>,
    complexNumberHyperbolicSineOverInputComputer: HyperbolicSineOverInputComputer<ComplexNumber<Double>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Double>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Double>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Double>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Double>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Double>, Matrix>,
    blockingParameter: Double,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettUsingScalingAndSquaring(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberFieldExtension = complexNumberFieldExtension,
            positiveSquareRootComputer = positiveSquareRootComputer,
            complexNumberExponentComputer = complexNumberExponentComputer,
            complexNumberHyperbolicSineOverInputComputer = complexNumberHyperbolicSineOverInputComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            inverseMatrixComputer = inverseMatrixComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
            blockingParameter = blockingParameter,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Matrix : MDList2<ComplexNumber<Double>>> ExponentComputer.Companion.setViaSchurParlettUsingScalingAndSquaring(
    matrixType: SuppliedType,
    blockingParameter: Double,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettUsingScalingAndSquaring(
            matrixType = matrixType,
            blockingParameter = blockingParameter,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Double>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Double>, Matrix>)
public fun <Matrix : MDList2<ComplexNumber<Double>>> ExponentComputer.Companion.useViaSchurParlettUsingScalingAndSquaring(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Double>, MatrixWithProperties<ComplexNumber<Double>, Matrix>>,
    field: Field<Double>,
    order: Order<Double>,
    complexNumberFieldExtension: FieldExtension<Double, ComplexNumber<Double>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Double>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Double>>,
    complexNumberHyperbolicSineOverInputComputer: HyperbolicSineOverInputComputer<ComplexNumber<Double>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Double>, MatrixWithProperties<ComplexNumber<Double>, Matrix>>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Double>, MatrixWithProperties<ComplexNumber<Double>, Matrix>>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Double>, MatrixWithProperties<ComplexNumber<Double>, Matrix>>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Double>, MatrixWithProperties<ComplexNumber<Double>, Matrix>>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Double>, MatrixWithProperties<ComplexNumber<Double>, Matrix>>,
    blockingParameter: Double,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = Double.suppliedType,
            ),
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = complexNumberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    ExponentKey<MatrixWithProperties<ComplexNumber<Double>, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val exponentComputer = viaSchurParlettUsingScalingAndSquaring<MatrixWithProperties<ComplexNumber<Double>, Matrix>>(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberFieldExtension = complexNumberFieldExtension,
            positiveSquareRootComputer = positiveSquareRootComputer,
            complexNumberExponentComputer = complexNumberExponentComputer,
            complexNumberHyperbolicSineOverInputComputer = complexNumberHyperbolicSineOverInputComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            inverseMatrixComputer = inverseMatrixComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
            blockingParameter = blockingParameter,
        )
        exponentComputer { matrix.get().exponent() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Double>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Double>, Matrix>, _: KoneContextRegistry.Provider)
public fun <Matrix : MDList2<ComplexNumber<Double>>> ExponentComputer.Companion.useViaSchurParlettUsingScalingAndSquaring(
    matrixType: SuppliedType,
    blockingParameter: Double,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = Double.suppliedType,
            ),
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = complexNumberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    ExponentKey<MatrixWithProperties<ComplexNumber<Double>, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val exponentComputer = viaSchurParlettUsingScalingAndSquaring<MatrixWithProperties<ComplexNumber<Double>, Matrix>>(
            matrixType = matrixWithPropertiesType,
            blockingParameter = blockingParameter,
        )
        exponentComputer { matrix.get().exponent() }
    }
}