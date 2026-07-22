/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.LogarithmComputer
import dev.lounres.kone.algebraic.algorithms.LogarithmKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.orThrow
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.provideOrNull
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class LogarithmComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackLogarithmMatrixComputer: LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
) : LogarithmComputer<MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.logarithm(): MatrixWithProperties<Number, Matrix> {
        val key = LogarithmKey<MatrixWithProperties<Number, Matrix>>()
        val provider = properties.provideOrNull(key) ?: return with(fallbackLogarithmMatrixComputer) { this@logarithm.logarithm() }
        return provider.get().orThrow { IllegalArgumentException("Cannot compute logarithm of $this: it has a property $key that corresponds to 'None'") }
    }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object LogarithmComputerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmComputer.Companion.viaProperties(
        fallbackLogarithmMatrixComputer: LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
    ): LogarithmComputer<MatrixWithProperties<Number, Matrix>> = LogarithmComputerViaProperties(
        fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
    )
    
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmComputer.Companion.viaProperties(
        block: LogarithmComputer.Companion.() -> LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
    ): LogarithmComputer<MatrixWithProperties<Number, Matrix>> = viaProperties(
        fallbackLogarithmMatrixComputer = block(),
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaProperties(
        fallbackLogarithmMatrixComputer: LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        LogarithmComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaProperties(
        block: LogarithmComputer.Companion.() -> LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        LogarithmComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackLogarithmMatrixComputer = block(),
            )
        }
    }
}