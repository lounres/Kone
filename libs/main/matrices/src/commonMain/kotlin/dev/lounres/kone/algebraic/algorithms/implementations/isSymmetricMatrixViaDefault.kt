/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isSymmetric
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localContexts
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.neq
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class IsSymmetricMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberEquality: Equality<Number>,
) : IsSymmetricMatrixChecker<Number, Matrix> {
    override fun Matrix.isSymmetric(): Boolean {
        localContexts(numberEquality)
        if (rowNumber != columnNumber) return false
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (this@isSymmetric[row, column] neq this@isSymmetric[column, row]) return false
        }
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.viaDefault(
    numberEquality: Equality<Number>,
): IsSymmetricMatrixChecker<Number, Matrix> = IsSymmetricMatrixCheckerViaDefault(
    numberEquality = numberEquality,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.viaDefault(): IsSymmetricMatrixChecker<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault<Number, Matrix>(
        numberEquality = koneContextRegistry.requestFor(Equality.Key<Number>()) {
            "IsSymmetricMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.setViaDefault(
    numberEquality: Equality<Number>,
) {
    IsSymmetricMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberEquality = numberEquality,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.setViaDefault() {
    IsSymmetricMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>()
    }
}

context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.useViaDefault(
    numberEquality: Equality<Number>,
) {
    IsSymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isSymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberEquality = numberEquality,
        )
        isSymmetricMatrixChecker { matrix.get().isSymmetric() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.useViaDefault() {
    IsSymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isSymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>()
        isSymmetricMatrixChecker { matrix.get().isSymmetric() }
    }
}