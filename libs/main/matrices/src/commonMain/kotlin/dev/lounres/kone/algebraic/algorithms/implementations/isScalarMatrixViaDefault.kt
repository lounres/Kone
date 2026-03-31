/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsScalarMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsScalarMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isScalar
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.neq
import dev.lounres.kone.suppliedTypes.SuppliedType


private class IsScalarMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsScalarMatrixChecker<Number, Matrix> {
    override fun Matrix.isScalar(): Boolean {
        if (rowNumber != columnNumber) return false
        if (rowNumber == 0u) return true
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (numberRing { this[row, column].isNotZero() }) return false
            if (numberRing { this[column, row].isNotZero() }) return false
        }
        val scalar = this[0u, 0u]
        for (row in 1u ..< rowNumber)
            if (numberRing { this[row, row] neq scalar }) return false
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsScalarMatrixChecker<Number, Matrix> = IsScalarMatrixCheckerViaDefault(
    numberRing = numberRing,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.viaDefault(
    numberType: SuppliedType,
): IsScalarMatrixChecker<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>(numberType = numberType)) {
            "IsScalarMatrixChecker.viaDefault<$numberType, ?>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.setViaDefault(
    matrixType: SuppliedType,
    numberRing: CommutativeRing<Number>,
) {
    IsScalarMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberRing = numberRing,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.setViaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    IsScalarMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberType = numberType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.useViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsScalarMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isScalarMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberRing = numberRing,
        )
        isScalarMatrixChecker { matrix.get().isScalar() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.useViaDefault(
    numberType: SuppliedType,
) {
    IsScalarMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isScalarMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
        )
        isScalarMatrixChecker { matrix.get().isScalar() }
    }
}