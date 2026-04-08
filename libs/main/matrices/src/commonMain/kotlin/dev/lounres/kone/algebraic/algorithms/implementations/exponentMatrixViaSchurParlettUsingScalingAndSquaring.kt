/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class ExponentMatrixComputerViaSchurParlettUsingScalingAndSquaring<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val exponentComputer: ExponentComputer<Number>,
    private val complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    private val atomicBlockImageComputationTolerance: Number,
    private val blockingParameter: Number,
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
            parlettRecurrenceAtomicBlockImageComputer = object : ParlettRecurrenceAtomicBlockImageComputer<ComplexNumber<Number>, Matrix> {
                override fun Matrix.atomicBlockImage(): Matrix {
                    TODO("Not yet implemented")
                }
            },
            blockingParameter = blockingParameter,
        )
    
    override fun Matrix.exponent(): Matrix {
        require(this.rowNumber == this.columnNumber)
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        @OptIn(ParlettRecurrenceInternalApi::class)
        val image = context(parlettRecurrence) { schurDecomposition.middleUpperTriangular.image() }
        return matrixProductComputer { schurDecomposition.leftUnitary * image * schurDecomposition.rightUnitary }
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ExponentComputer.Companion.viaSchurParlettUsingScalingAndSquaring(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    exponentComputer: ExponentComputer<Number>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
): ExponentComputer<Matrix> = ExponentMatrixComputerViaSchurParlettUsingScalingAndSquaring(
    matrixFactory = matrixFactory,
    field = field,
    order = order,
    complexNumberFieldExtension = complexNumberFieldExtension,
    exponentComputer = exponentComputer,
    complexNumberExponentComputer = complexNumberExponentComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    inverseMatrixComputer = inverseMatrixComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
    atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
    blockingParameter = blockingParameter,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ExponentComputer.Companion.viaSchurParlettUsingScalingAndSquaring(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
): ExponentComputer<Matrix> {
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
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        order = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        exponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<Number>(numberType = numberType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        complexNumberExponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        inverseMatrixComputer = koneContextRegistry.requestFor(InverseMatrixComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
        blockingParameter = blockingParameter,
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ExponentComputer.Companion.setViaSchurParlettUsingScalingAndSquaring(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    exponentComputer: ExponentComputer<Number>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettUsingScalingAndSquaring(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberFieldExtension = complexNumberFieldExtension,
            exponentComputer = exponentComputer,
            complexNumberExponentComputer = complexNumberExponentComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            inverseMatrixComputer = inverseMatrixComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ExponentComputer.Companion.setViaSchurParlettUsingScalingAndSquaring(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettUsingScalingAndSquaring(
            numberType = numberType,
            matrixType = matrixType,
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ExponentComputer.Companion.useViaSchurParlettUsingScalingAndSquaring(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    exponentComputer: ExponentComputer<Number>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
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
    ExponentKey<MatrixWithProperties<ComplexNumber<Number>, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val exponentComputer = viaSchurParlettUsingScalingAndSquaring<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberFieldExtension = complexNumberFieldExtension,
            exponentComputer = exponentComputer,
            complexNumberExponentComputer = complexNumberExponentComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            inverseMatrixComputer = inverseMatrixComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
        exponentComputer { matrix.get().exponent() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> ExponentComputer.Companion.useViaSchurParlettUsingScalingAndSquaring(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
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
    ExponentKey<MatrixWithProperties<ComplexNumber<Number>, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val exponentComputer = viaSchurParlettUsingScalingAndSquaring<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
        exponentComputer { matrix.get().exponent() }
    }
}