/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isSymmetric
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.neq
import dev.lounres.kone.suppliedTypes.SuppliedType


private class IsSymmetricMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsSymmetricMatrixChecker<Number, Matrix> {
    override fun Matrix.isSymmetric(): Boolean {
        if (rowNumber != columnNumber) return false
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (numberRing { this[row, column] neq this[column, row] }) return false
        }
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsSymmetricMatrixChecker<Number, Matrix> = IsSymmetricMatrixCheckerViaDefault(
    numberRing = numberRing,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.viaDefault(
    numberType: SuppliedType,
): IsSymmetricMatrixChecker<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault<Number, Matrix>(
        numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>(numberType = numberType)) {
            "IsSymmetricMatrixChecker.viaDefault<$numberType, ?>"
        },
    )
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.setViaDefault(
    matrixType: SuppliedType,
    numberRing: CommutativeRing<Number>,
) {
    IsSymmetricMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberRing = numberRing,
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.setViaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    IsSymmetricMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberType = numberType,
        )
    }
}

context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.useViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsSymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isSymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberRing = numberRing,
        )
        isSymmetricMatrixChecker { matrix.get().isSymmetric() }
    }
}

context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.useViaDefault(
    numberType: SuppliedType,
) {
    IsSymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isSymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
        )
        isSymmetricMatrixChecker { matrix.get().isSymmetric() }
    }
}