/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class IsSymmetricMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsSymmetricMatrixChecker: IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isSymmetric(): Boolean =
        properties.getOrElse(IsSymmetricMatrixKey) {
            with(fallbackIsSymmetricMatrixChecker) { this@isSymmetric.isSymmetric() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.viaProperties(
    fallbackIsSymmetricMatrixChecker: IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsSymmetricMatrixCheckerViaProperties(
    fallbackIsSymmetricMatrixChecker = fallbackIsSymmetricMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.viaProperties(
    block: IsSymmetricMatrixChecker.Companion.() -> IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsSymmetricMatrixChecker = block(),
)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.setViaProperties(
    fallbackIsSymmetricMatrixChecker: IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    IsSymmetricMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsSymmetricMatrixChecker = fallbackIsSymmetricMatrixChecker,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.setViaProperties(
    block: IsSymmetricMatrixChecker.Companion.() -> IsSymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    IsSymmetricMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsSymmetricMatrixChecker = block(),
        )
    }
}