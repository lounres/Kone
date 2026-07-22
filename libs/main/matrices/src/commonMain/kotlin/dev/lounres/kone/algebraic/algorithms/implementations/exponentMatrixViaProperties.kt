/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.ExponentComputer
import dev.lounres.kone.algebraic.algorithms.ExponentKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class ExponentComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackExponentMatrixComputer: ExponentComputer<MatrixWithProperties<Number, Matrix>>,
) : ExponentComputer<MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.exponent(): MatrixWithProperties<Number, Matrix> {
        val key = ExponentKey<MatrixWithProperties<Number, Matrix>>()
        val provider = properties.provideOrNull(key) ?: return with(fallbackExponentMatrixComputer) { this@exponent.exponent() }
        return provider.get()
    }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object ExponentComputerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> ExponentComputer.Companion.viaProperties(
        fallbackExponentMatrixComputer: ExponentComputer<MatrixWithProperties<Number, Matrix>>,
    ): ExponentComputer<MatrixWithProperties<Number, Matrix>> = ExponentComputerViaProperties(
        fallbackExponentMatrixComputer = fallbackExponentMatrixComputer,
    )
    
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> ExponentComputer.Companion.viaProperties(
        block: ExponentComputer.Companion.() -> ExponentComputer<MatrixWithProperties<Number, Matrix>>,
    ): ExponentComputer<MatrixWithProperties<Number, Matrix>> = viaProperties(
        fallbackExponentMatrixComputer = block(),
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> ExponentComputer.Companion.setViaProperties(
        fallbackExponentMatrixComputer: ExponentComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        ExponentComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackExponentMatrixComputer = fallbackExponentMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> ExponentComputer.Companion.setViaProperties(
        block: ExponentComputer.Companion.() -> ExponentComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        ExponentComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackExponentMatrixComputer = block(),
            )
        }
    }
}