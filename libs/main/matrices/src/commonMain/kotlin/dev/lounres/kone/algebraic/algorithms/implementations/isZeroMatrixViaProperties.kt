/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsZeroMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsZeroMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class IsZeroMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsZeroMatrixChecker: IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isZero(): Boolean =
        properties.getOrElse(IsZeroMatrixKey) {
            with(fallbackIsZeroMatrixChecker) { this@isZero.isZero() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.viaProperties(
    fallbackIsZeroMatrixChecker: IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsZeroMatrixCheckerViaProperties(
    fallbackIsZeroMatrixChecker = fallbackIsZeroMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.viaProperties(
    block: IsZeroMatrixChecker.Companion.() -> IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsZeroMatrixChecker = block(),
)

// TODO: Remove the checker when KT-73135 will be fixed
public object IsZeroMatrixCheckerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.setViaProperties(
        fallbackIsZeroMatrixChecker: IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        IsZeroMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackIsZeroMatrixChecker = fallbackIsZeroMatrixChecker,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.setViaProperties(
        block: IsZeroMatrixChecker.Companion.() -> IsZeroMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        IsZeroMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackIsZeroMatrixChecker = block(),
            )
        }
    }
}