/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.InverseMatrixComputer
import dev.lounres.kone.algebraic.algorithms.InverseMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class InverseMatrixComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackInverseMatrixComputer: InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.invert(): MatrixWithProperties<Number, Matrix>? =
        properties.getOrElse(InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>()) {
            with(fallbackInverseMatrixComputer) { this@invert.invert() }
        }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object InverseMatrixComputerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaProperties(
        fallbackInverseMatrixComputer: InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ): InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = InverseMatrixComputerViaProperties(
        fallbackInverseMatrixComputer = fallbackInverseMatrixComputer,
    )
    
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaProperties(
        block: InverseMatrixComputer.Companion.() -> InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ): InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
        fallbackInverseMatrixComputer = block(),
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaProperties(
        fallbackInverseMatrixComputer: InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        InverseMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackInverseMatrixComputer = fallbackInverseMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaProperties(
        block: InverseMatrixComputer.Companion.() -> InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        InverseMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackInverseMatrixComputer = block(),
            )
        }
    }
}