/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsScalarMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsScalarMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class IsScalarMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsScalarMatrixChecker: IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isScalar(): Boolean =
        properties.getOrElse(IsScalarMatrixKey) {
            with(fallbackIsScalarMatrixChecker) { this@isScalar.isScalar() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.viaProperties(
    fallbackIsScalarMatrixChecker: IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsScalarMatrixCheckerViaProperties(
    fallbackIsScalarMatrixChecker = fallbackIsScalarMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.viaProperties(
    block: IsScalarMatrixChecker.Companion.() -> IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsScalarMatrixChecker = block(),
)

// TODO: Remove the checker when KT-73135 will be fixed
public object IsScalarMatrixCheckerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.setViaProperties(
        fallbackIsScalarMatrixChecker: IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        IsScalarMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackIsScalarMatrixChecker = fallbackIsScalarMatrixChecker,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.setViaProperties(
        block: IsScalarMatrixChecker.Companion.() -> IsScalarMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        IsScalarMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackIsScalarMatrixChecker = block(),
            )
        }
    }
}