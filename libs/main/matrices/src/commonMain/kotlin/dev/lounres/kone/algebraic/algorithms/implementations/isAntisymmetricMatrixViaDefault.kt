/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isAntisymmetric
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class IsAntisymmetricMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsAntisymmetricMatrixChecker<Number, Matrix> {
    override fun Matrix.isAntisymmetric(): Boolean {
        if (rowNumber != columnNumber) return false
        for (row in 0u ..< rowNumber) if (numberRing { this[row, row].isNotZero() }) return false
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (numberRing { (this[row, column] + this[column, row]).isNotZero() }) return false
        }
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsAntisymmetricMatrixChecker<Number, Matrix> = IsAntisymmetricMatrixCheckerViaDefault(
    numberRing = numberRing,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaDefault(): IsAntisymmetricMatrixChecker<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
            "IsAntisymmetricMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsAntisymmetricMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberRing = numberRing,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaDefault() {
    IsAntisymmetricMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>()
    }
}

context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.useViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsAntisymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isAntisymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberRing = numberRing,
        )
        isAntisymmetricMatrixChecker { matrix.get().isAntisymmetric() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.useViaDefault() {
    IsAntisymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isAntisymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>()
        isAntisymmetricMatrixChecker { matrix.get().isAntisymmetric() }
    }
}