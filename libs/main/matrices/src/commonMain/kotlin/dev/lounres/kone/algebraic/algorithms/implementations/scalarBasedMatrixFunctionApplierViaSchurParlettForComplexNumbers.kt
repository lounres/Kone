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
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class ScalarBasedMatrixFunctionApplierViaSchurParlettForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val complexNumberField: Field<ComplexNumber<Number>>,
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
                complexNumberField = complexNumberField,
                matrixCategoryOverField = matrixCategoryOverField,
                matrixProductComputer = matrixProductComputer,
                inverseMatrixComputer = inverseMatrixComputer,
                isDiagonalMatrixChecker = isDiagonalMatrixChecker,
                parlettRecurrenceAtomicBlockImageComputer = ParlettRecurrenceAtomicBlockImageComputerViaTaylorSeriesForComplexNumbers(
                    matrixFactory = matrixFactory,
                    field = field,
                    order = order,
                    complexNumberField = complexNumberField,
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
    complexNumberField: Field<ComplexNumber<Number>>,
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
    complexNumberField = complexNumberField,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    inverseMatrixComputer = inverseMatrixComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
    atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
    blockingParameter = blockingParameter,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>, @Supply Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.viaSchurParlettForComplexNumbers(
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
): ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, Function> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaSchurParlettForComplexNumbers(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        order = koneContextRegistry.requestFor(Order.Key<Number>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        complexNumberField = koneContextRegistry.requestFor(Field.Key<ComplexNumber<Number>>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        inverseMatrixComputer = koneContextRegistry.requestFor(InverseMatrixComputer.Key<ComplexNumber<Number>, Matrix>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<ComplexNumber<Number>, Matrix>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<ComplexNumber<Number>, Matrix>()) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}, ${suppliedTypeOf<Function>()}>"
        },
        atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
        blockingParameter = blockingParameter,
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>, @Supply Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.setViaSchurParlettForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberField: Field<ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ScalarBasedMatrixFunctionApplier.Key<ComplexNumber<Number>, Matrix, Function>() correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettForComplexNumbers<Number, Matrix, Function>(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberField = complexNumberField,
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

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>, @Supply Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.setViaSchurParlettForComplexNumbers(
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ScalarBasedMatrixFunctionApplier.Key<ComplexNumber<Number>, Matrix, Function>() correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettForComplexNumbers<Number, Matrix, Function>(
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
    }
}