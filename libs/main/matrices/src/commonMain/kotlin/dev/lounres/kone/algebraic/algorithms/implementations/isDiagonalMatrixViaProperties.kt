/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class IsDiagonalMatrixCheckerViaProperties<Number, Matrix : MDList2<Number>>(
    private val fallbackIsDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) : IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.isDiagonal(): Boolean =
        properties.getOrElse(IsDiagonalMatrixKey) {
            with(fallbackIsDiagonalMatrixChecker) { this@isDiagonal.isDiagonal() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.viaProperties(
    fallbackIsDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = IsDiagonalMatrixCheckerViaProperties(
    fallbackIsDiagonalMatrixChecker = fallbackIsDiagonalMatrixChecker,
)

public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.viaProperties(
    block: IsDiagonalMatrixChecker.Companion.() -> IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
): IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackIsDiagonalMatrixChecker = block(),
)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.setViaProperties(
    fallbackIsDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    IsDiagonalMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsDiagonalMatrixChecker = fallbackIsDiagonalMatrixChecker,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.setViaProperties(
    block: IsDiagonalMatrixChecker.Companion.() -> IsDiagonalMatrixChecker<Number, MatrixWithProperties<Number, Matrix>>,
) {
    IsDiagonalMatrixChecker.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackIsDiagonalMatrixChecker = block(),
        )
    }
}