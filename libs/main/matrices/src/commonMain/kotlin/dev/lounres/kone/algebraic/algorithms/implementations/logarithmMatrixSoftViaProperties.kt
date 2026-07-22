/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.LogarithmKey
import dev.lounres.kone.algebraic.algorithms.LogarithmSoftComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.orNull
import dev.lounres.kone.maybe.orThrow
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.registry.provideOrNull
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class LogarithmSoftComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackLogarithmMatrixComputer: LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>>,
) : LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.logarithm(): MatrixWithProperties<Number, Matrix> {
        val key = LogarithmKey<MatrixWithProperties<Number, Matrix>>()
        val provider = properties.provideOrNull(key) ?: return with(fallbackLogarithmMatrixComputer) { this@logarithm.logarithm() }
        return provider.get().orThrow { IllegalArgumentException("Cannot compute logarithm of $this: it has a property $key that corresponds to 'None'") }
    }
    override fun MatrixWithProperties<Number, Matrix>.logarithmOrNull(): MatrixWithProperties<Number, Matrix>? {
        val key = LogarithmKey<MatrixWithProperties<Number, Matrix>>()
        val provider = properties.provideOrNull(key) ?: return with(fallbackLogarithmMatrixComputer) { this@logarithmOrNull.logarithmOrNull() }
        return provider.get().orNull()
    }
    override fun MatrixWithProperties<Number, Matrix>.logarithmMaybe(): Maybe<MatrixWithProperties<Number, Matrix>> =
        properties.getOrElse(LogarithmKey<MatrixWithProperties<Number, Matrix>>()) {
            with(fallbackLogarithmMatrixComputer) {
                this@logarithmMaybe.logarithmMaybe()
            }
        }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object LogarithmSoftComputerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.viaProperties(
        fallbackLogarithmMatrixComputer: LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>>,
    ): LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>> = LogarithmSoftComputerViaProperties(
        fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
    )
    
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.viaProperties(
        block: LogarithmSoftComputer.Companion.() -> LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>>,
    ): LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>> = viaProperties(
        fallbackLogarithmMatrixComputer = block(),
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.setViaProperties(
        fallbackLogarithmMatrixComputer: LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        LogarithmSoftComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LogarithmSoftComputer.Companion.setViaProperties(
        block: LogarithmSoftComputer.Companion.() -> LogarithmSoftComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        LogarithmSoftComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackLogarithmMatrixComputer = block(),
            )
        }
    }
}