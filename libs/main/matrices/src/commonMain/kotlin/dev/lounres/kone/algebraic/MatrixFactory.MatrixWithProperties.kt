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
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class MatrixWithPropertiesFactory<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
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
    propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit,
): MatrixFactory<Number, MatrixWithProperties<Number, Matrix>> =
    MatrixWithPropertiesFactory(
        matrixFactory = matrixFactory,
        propertiesBuilder = propertiesBuilder,
    )

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> MatrixFactory.Companion.setForMatrixWithProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    propertiesBuilder: context(MatrixWithProperties.Provider<Number, Matrix>) MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>.() -> Unit = {},
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    MatrixFactory.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        forMatrixWithProperties(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)) {
                "MatrixFactory.setForMatrixWithProperties<$numberType, $matrixType>"
            },
            propertiesBuilder = propertiesBuilder,
        )
    }
}