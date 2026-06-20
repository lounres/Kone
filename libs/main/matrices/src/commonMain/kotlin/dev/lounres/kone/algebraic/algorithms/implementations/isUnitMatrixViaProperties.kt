/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsUnitMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsUnitMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class IsUnitMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsUnitMatrixChecker: IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isOne(): Boolean =
        properties.getOrElse(IsUnitMatrixKey) {
            with(fallbackIsUnitMatrixChecker) { this@isOne.isOne() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.viaProperties(
    fallbackIsUnitMatrixChecker: IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsUnitMatrixCheckerViaProperties(
    fallbackIsUnitMatrixChecker = fallbackIsUnitMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.viaProperties(
    block: IsUnitMatrixChecker.Companion.() -> IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsUnitMatrixCheckerViaProperties(
    fallbackIsUnitMatrixChecker = block(),
)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.setViaProperties(
    fallbackIsUnitMatrixChecker: IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    IsUnitMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsUnitMatrixChecker = fallbackIsUnitMatrixChecker,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.setViaProperties(
    block: IsUnitMatrixChecker.Companion.() -> IsUnitMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    IsUnitMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsUnitMatrixChecker = block(),
        )
    }
}