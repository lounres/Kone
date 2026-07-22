/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class IsAntisymmetricMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsAntisymmetricMatrixChecker: IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isAntisymmetric(): Boolean =
        properties.getOrElse(IsAntisymmetricMatrixKey) {
            with(fallbackIsAntisymmetricMatrixChecker) { this@isAntisymmetric.isAntisymmetric() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaProperties(
    fallbackIsAntisymmetricMatrixChecker: IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsAntisymmetricMatrixCheckerViaProperties(
    fallbackIsAntisymmetricMatrixChecker = fallbackIsAntisymmetricMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaProperties(
    block: IsAntisymmetricMatrixChecker.Companion.() -> IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsAntisymmetricMatrixChecker = block(),
)

// TODO: Remove the checker when KT-73135 will be fixed
public object IsAntisymmetricMatrixCheckerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaProperties(
        fallbackIsAntisymmetricMatrixChecker: IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        IsAntisymmetricMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackIsAntisymmetricMatrixChecker = fallbackIsAntisymmetricMatrixChecker,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaProperties(
        block: IsAntisymmetricMatrixChecker.Companion.() -> IsAntisymmetricMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        IsAntisymmetricMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackIsAntisymmetricMatrixChecker = block(),
            )
        }
    }
}