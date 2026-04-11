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
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class ScalarBasedMatrixFunctionApplierViaSchurParlettForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    private val atomicBlockImageComputationTolerance: Number,
    private val blockingParameter: Number,
) : ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> {
    override fun Matrix.after(function: ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>): Matrix {
        require(this.rowNumber == this.columnNumber)
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        @OptIn(ParlettRecurrenceInternalApi::class)
        val image = context(
            ParlettRecurrence(
                matrixFactory = matrixFactory,
                field = field,
                order = order,
                complexNumberFieldExtension = complexNumberFieldExtension,
                matrixCategoryOverField = matrixCategoryOverField,
                matrixProductComputer = matrixProductComputer,
                inverseMatrixComputer = inverseMatrixComputer,
                isDiagonalMatrixChecker = isDiagonalMatrixChecker,
                parlettRecurrenceAtomicBlockImageComputer = ParlettRecurrenceAtomicBlockImageComputerViaTaylorSeriesForComplexNumbers(
                    matrixFactory = matrixFactory,
                    field = field,
                    order = order,
                    complexNumberFieldExtension = complexNumberFieldExtension,
                    matrixCategoryOverField = matrixCategoryOverField,
                    matrixProductComputer = matrixProductComputer,
                    atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
                    function = function,
                ),
                blockingParameter = blockingParameter,
            )
        ) { schurDecomposition.middleUpperTriangular.image() }
        return matrixProductComputer { schurDecomposition.leftUnitary * image * schurDecomposition.rightUnitary }
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.viaSchurParlettForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
): ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, Function> = ScalarBasedMatrixFunctionApplierViaSchurParlettForComplexNumbers(
    matrixFactory = matrixFactory,
    field = field,
    order = order,
    complexNumberFieldExtension = complexNumberFieldExtension,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    inverseMatrixComputer = inverseMatrixComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
    atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
    blockingParameter = blockingParameter,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.viaSchurParlettForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    functionType: SuppliedType,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
): ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, Function> {
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
    return viaSchurParlettForComplexNumbers(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        order = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        inverseMatrixComputer = koneContextRegistry.requestFor(InverseMatrixComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
        blockingParameter = blockingParameter,
    )
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.setViaSchurParlettForComplexNumbers(
    matrixType: SuppliedType,
    functionType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ScalarBasedMatrixFunctionApplier.Key<ComplexNumber<Number>, Matrix, Function>(matrixType = matrixType, functionType = functionType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettForComplexNumbers<Number, Matrix, Function>(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberFieldExtension = complexNumberFieldExtension,
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

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.setViaSchurParlettForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    functionType: SuppliedType,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ScalarBasedMatrixFunctionApplier.Key<ComplexNumber<Number>, Matrix, Function>(matrixType = matrixType, functionType = functionType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettForComplexNumbers<Number, Matrix, Function>(
            numberType = numberType,
            matrixType = matrixType,
            functionType = functionType,
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
    }
}