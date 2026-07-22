/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.SquareRootsComputer
import dev.lounres.kone.algebraic.algorithms.SquareRootsKey
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class SquareRootsViaPropertiesForMatrixWithProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackSquareRootComputer: SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
) : SquareRootsComputer<MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.squareRoots(): KoneList<MatrixWithProperties<Number, Matrix>> =
        properties.getOrElse(SquareRootsKey<MatrixWithProperties<Number, Matrix>>()) {
            with(fallbackSquareRootComputer) { this@squareRoots.squareRoots() }
        }
}

// TODO: Remove the checker when KT-73135 will be fixed
public object SquareRootsComputerPropertiesForMatrixWithPropertiesSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> SquareRootsComputer.Companion.viaPropertiesForMatrixWithProperties(
        fallbackSquareRootComputer: SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
    ): SquareRootsComputer<MatrixWithProperties<Number, Matrix>> = SquareRootsViaPropertiesForMatrixWithProperties(
        fallbackSquareRootComputer = fallbackSquareRootComputer,
    )
    
    @Suppliable
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> SquareRootsComputer.Companion.viaPropertiesForMatrixWithProperties(
        block: SquareRootsComputer.Companion.() -> SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
    ): SquareRootsComputer<MatrixWithProperties<Number, Matrix>> = viaPropertiesForMatrixWithProperties(
        fallbackSquareRootComputer = block(),
    )
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> SquareRootsComputer.Companion.setViaPropertiesForMatrixWithProperties(
        fallbackSquareRootComputer: SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        SquareRootsComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaPropertiesForMatrixWithProperties(
                fallbackSquareRootComputer = fallbackSquareRootComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> SquareRootsComputer.Companion.setViaPropertiesForMatrixWithProperties(
        block: SquareRootsComputer.Companion.() -> SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
    ) {
        SquareRootsComputer.Key<MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            viaPropertiesForMatrixWithProperties(
                fallbackSquareRootComputer = block(),
            )
        }
    }
}