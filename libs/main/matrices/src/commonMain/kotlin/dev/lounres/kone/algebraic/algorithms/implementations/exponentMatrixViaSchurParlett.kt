/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixCategoryOverField
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixChecker
import dev.lounres.kone.algebraic.algorithms.ExponentComputer
import dev.lounres.kone.algebraic.algorithms.ExponentKey
import dev.lounres.kone.algebraic.algorithms.MatrixProductComputer
import dev.lounres.kone.algebraic.algorithms.SchurDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.exponent
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isDiagonal
import dev.lounres.kone.algebraic.algorithms.schurDecomposition
import dev.lounres.kone.algebraic.algorithms.times
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class ExponentMatrixComputerViaSchurParlett<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
) : ExponentComputer<Matrix> {
    override fun Matrix.exponent(): Matrix {
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        val t = schurDecomposition.middleUpperTriangular
        if (isDiagonalMatrixChecker { t.isDiagonal() }) {
            val g: Matrix = TODO() // G := log(T)
            return matrixProductComputer { schurDecomposition.leftUnitary * g * schurDecomposition.rightUnitary }
        }
        TODO()
    }
}

public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.viaSchurParlett(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
): ExponentComputer<Matrix> = ExponentMatrixComputerViaSchurParlett(
    matrixFactory = matrixFactory,
    field = field,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.viaSchurParlett(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): ExponentComputer<Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaSchurParlett(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<Number, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<Number, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaSchurParlett<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.setViaSchurParlett(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
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

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.setViaSchurParlett(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlett(
            numberType = numberType,
            matrixType = matrixType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.useViaSchurParlett(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, MatrixWithProperties<Number, Matrix>>,
    matrixProductComputer: MatrixProductComputer<Number, MatrixWithProperties<Number, Matrix>>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    ExponentKey<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val exponentComputer = viaSchurParlett<Number, MatrixWithProperties<Number, Matrix>>(
            matrixFactory = matrixFactory,
            field = field,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
        )
        exponentComputer { matrix.get().exponent() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.useViaSchurParlett(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    ExponentKey<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val exponentComputer = viaSchurParlett<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
        )
        exponentComputer { matrix.get().exponent() }
    }
}