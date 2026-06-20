/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class MatrixWithPropertiesFactory<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
) : MatrixFactory<Number, MatrixWithProperties<Number, Matrix>> {
    override fun generateMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        generator: (row: UInt, column: UInt) -> Number,
    ): MatrixWithProperties<Number, Matrix> =
        MatrixWithProperties(
            matrix = matrixFactory.generateMatrix(rowNumber, columnNumber, generator),
            propertiesBuilder = { propertiesBuilder() },
        )
    
    override fun fillMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        number: Number,
    ): MatrixWithProperties<Number, Matrix> =
        MatrixWithProperties(
            matrix = matrixFactory.fillMatrix(rowNumber, columnNumber, number),
            propertiesBuilder = { propertiesBuilder() },
        )
    
    override fun mapMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        numbers: KoneMap<MDIndex, Number>,
    ): MatrixWithProperties<Number, Matrix> =
        MatrixWithProperties(
            matrix = matrixFactory.mapMatrix(rowNumber, columnNumber, numbers),
            propertiesBuilder = { propertiesBuilder() },
        )
}

public fun <Number, Matrix : MDList2<Number>> MatrixFactory.Companion.forMatrixWithProperties(
    matrixFactory: MatrixFactory<Number, Matrix>,
    propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
): MatrixFactory<Number, MatrixWithProperties<Number, Matrix>> =
    MatrixWithPropertiesFactory(
        matrixFactory = matrixFactory,
        propertiesBuilder = propertiesBuilder,
    )

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> MatrixFactory.Companion.setForMatrixWithProperties(
    propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit = {},
) {
    MatrixFactory.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        forMatrixWithProperties(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
                "MatrixFactory.setForMatrixWithProperties<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            propertiesBuilder = propertiesBuilder,
        )
    }
}