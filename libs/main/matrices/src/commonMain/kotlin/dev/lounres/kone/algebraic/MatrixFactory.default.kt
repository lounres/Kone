/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.getOrElse
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class MDList2MatrixFactory<Number>(
    private val ring: CommutativeRing<Number>
) : MatrixFactory<Number, MDList2<Number>> {
    override fun generateMatrix(
        rowNumber: UInt,
        columnNumber: UInt,
        generator: (row: UInt, column: UInt) -> Number
    ): MDList2<Number> = MDList2.generate(rowNumber = rowNumber, columnNumber = columnNumber, initializer = generator)
    
    override fun fillMatrix(rowNumber: UInt, columnNumber: UInt, number: Number): MDList2<Number> =
        MDList2.generate(rowNumber = rowNumber, columnNumber = columnNumber) { _, _ ->  number }
    
    override fun mapMatrix(rowNumber: UInt, columnNumber: UInt, numbers: KoneMap<MDIndex, Number>): MDList2<Number> {
        require(numbers.keysView.all { it.size == 2u && it[0u] < rowNumber && it[1u] < columnNumber }) { TODO() }
        return MDList2.generate(rowNumber, columnNumber) { row, column -> numbers.getOrElse(MDIndex.of(row, column)) { ring.zero } }
    }
}

public fun <Number> MatrixFactory.Companion.default(ring: CommutativeRing<Number>): MatrixFactory<Number, MDList2<Number>> =
    MDList2MatrixFactory(ring = ring)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> MatrixFactory.Companion.default(numberType: SuppliedType): MatrixFactory<Number, MDList2<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return default(
        ring = koneContextRegistry.requestFor(CommutativeRing.Key<Number>(numberType = numberType)) {
            "MatrixFactory.default<$numberType>"
        }
    )
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> MatrixFactory.Companion.setDefault(numberType: SuppliedType) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList2",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType
            )
        ),
        isNullable = false,
    )
    MatrixFactory.Key<Number, MDList2<Number>>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        default(numberType = numberType)
    }
}