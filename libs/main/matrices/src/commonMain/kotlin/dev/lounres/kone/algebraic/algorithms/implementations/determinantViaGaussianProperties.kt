/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.DeterminantComputer
import dev.lounres.kone.algebraic.algorithms.DeterminantKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import dev.lounres.kone.suppliedTypes.Supply
import kotlin.reflect.KVariance.INVARIANT


@Suppliable
private class DeterminantComputerViaProperties<@Supply Number, Matrix : MDList2<Number>>(
    private val fallbackDeterminantComputer: DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.determinant(): Number =
        properties.getOrElse(DeterminantKey<Number>()) {
            with(fallbackDeterminantComputer) { this@determinant.determinant() }
        }
}

@Suppliable
public fun <@Supply Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaProperties(
    fallbackDeterminantComputer: DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
): DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>> = DeterminantComputerViaProperties(
    fallbackDeterminantComputer = fallbackDeterminantComputer,
)

@Suppliable
public fun <@Supply Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaProperties(
    block: DeterminantComputer.Companion.() -> DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
): DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackDeterminantComputer = block(),
)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaProperties(
    fallbackDeterminantComputer: DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
) {
    DeterminantComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackDeterminantComputer = fallbackDeterminantComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaProperties(
    block: DeterminantComputer.Companion.() -> DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
) {
    DeterminantComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackDeterminantComputer = block(),
        )
    }
}