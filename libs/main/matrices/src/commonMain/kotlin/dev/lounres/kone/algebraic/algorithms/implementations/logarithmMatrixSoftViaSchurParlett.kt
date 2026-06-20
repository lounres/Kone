/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixCategoryOverField
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class LogarithmMatrixSoftComputerViaSchurParlett<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
) : LogarithmSoftComputer<Matrix> {
    override fun Matrix.logarithm(): Matrix {
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        val t = schurDecomposition.middleUpperTriangular
        if (isDiagonalMatrixChecker { t.isDiagonal() }) {
            val g: Matrix = TODO() // G := log(T)
            return matrixProductComputer { schurDecomposition.leftUnitary * g * schurDecomposition.rightUnitary }
        }
        TODO()
    }
    override fun Matrix.logarithmOrNull(): Matrix? {
        TODO("Not yet implemented")
    }
    override fun Matrix.logarithmMaybe(): Maybe<Matrix> {
        TODO("Not yet implemented")
    }
}

public fun <Number, Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.viaSchurParlett(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
): LogarithmSoftComputer<Matrix> = LogarithmMatrixSoftComputerViaSchurParlett(
    matrixFactory = matrixFactory,
    field = field,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.viaSchurParlett(): LogarithmSoftComputer<Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaSchurParlett(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "LogarithmSoftComputer.viaSchurParlett<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "LogarithmSoftComputer.viaSchurParlett<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<Number, Matrix>()) {
            "LogarithmSoftComputer.viaSchurParlett<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<Number, Matrix>()) {
            "LogarithmSoftComputer.viaSchurParlett<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<Number, Matrix>()) {
            "LogarithmSoftComputer.viaSchurParlett<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<Number, Matrix>()) {
            "LogarithmSoftComputer.viaSchurParlett<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.setViaSchurParlett(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
) {
    LogarithmComputer.Key<Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaSchurParlett(
            matrixFactory = matrixFactory,
            field = field,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.setViaSchurParlett() {
    LogarithmSoftComputer.Key<Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaSchurParlett()
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.useViaSchurParlett(
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, MatrixWithProperties<Number, Matrix>>,
    matrixProductComputer: MatrixProductComputer<Number, MatrixWithProperties<Number, Matrix>>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    LogarithmKey<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val logarithmComputer = viaSchurParlett<Number, MatrixWithProperties<Number, Matrix>>(
            matrixFactory = matrixFactory,
            field = field,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
        )
        logarithmComputer { matrix.get().logarithmMaybe() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.useViaSchurParlett() {
    LogarithmKey<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val logarithmComputer = viaSchurParlett<Number, MatrixWithProperties<Number, Matrix>>()
        logarithmComputer { matrix.get().logarithmMaybe() }
    }
}