/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.HessenbergDecomposition
import dev.lounres.kone.algebraic.algorithms.HessenbergDecompositionComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class HessenbergDecompositionComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackHessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.hessenbergDecomposition(): HessenbergDecomposition<Number, MatrixWithProperties<Number, Matrix>> =
        properties.getOrElse(HessenbergDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>()) {
            with(fallbackHessenbergDecompositionComputer) {
                this@hessenbergDecomposition.hessenbergDecomposition()
            }
        }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object HessenbergDecompositionComputerPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.viaProperties(
        fallbackHessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ): HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> = HessenbergDecompositionComputerViaProperties(
        fallbackHessenbergDecompositionComputer = fallbackHessenbergDecompositionComputer,
    )
    
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.viaProperties(
        block: HessenbergDecompositionComputer.Companion.() -> HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ): HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>> = HessenbergDecompositionComputerViaProperties(
        fallbackHessenbergDecompositionComputer = block(),
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.setViaProperties(
        fallbackHessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        HessenbergDecompositionComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackHessenbergDecompositionComputer = fallbackHessenbergDecompositionComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.setViaProperties(
        block: HessenbergDecompositionComputer.Companion.() -> HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        HessenbergDecompositionComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaProperties(
                fallbackHessenbergDecompositionComputer = block(),
            )
        }
    }
}