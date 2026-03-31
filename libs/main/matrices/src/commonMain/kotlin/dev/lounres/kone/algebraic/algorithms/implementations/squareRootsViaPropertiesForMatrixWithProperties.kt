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
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class SquareRootsViaPropertiesForMatrixWithProperties<Number, Matrix : MDList2<Number>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackSquareRootComputer: SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
) : SquareRootsComputer<MatrixWithProperties<Number, Matrix>> {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    private val matrixWithPropertiesType = SuppliedType.Regular(
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
    
    override fun MatrixWithProperties<Number, Matrix>.squareRoots(): KoneList<MatrixWithProperties<Number, Matrix>> =
        properties.getOrElse(SquareRootsKey<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType)) {
            with(fallbackSquareRootComputer) { this@squareRoots.squareRoots() }
        }
}

public fun <Number, Matrix : MDList2<Number>> SquareRootsComputer.Companion.viaPropertiesForMatrixWithProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackSquareRootComputer: SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
): SquareRootsComputer<MatrixWithProperties<Number, Matrix>> = SquareRootsViaPropertiesForMatrixWithProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackSquareRootComputer = fallbackSquareRootComputer,
)

public fun <Number, Matrix : MDList2<Number>> SquareRootsComputer.Companion.viaPropertiesForMatrixWithProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: SquareRootsComputer.Companion.() -> SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
): SquareRootsComputer<MatrixWithProperties<Number, Matrix>> = viaPropertiesForMatrixWithProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackSquareRootComputer = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> SquareRootsComputer.Companion.setViaPropertiesForMatrixWithProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackSquareRootComputer: SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
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
    SquareRootsComputer.Key<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaPropertiesForMatrixWithProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackSquareRootComputer = fallbackSquareRootComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> SquareRootsComputer.Companion.setViaPropertiesForMatrixWithProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: SquareRootsComputer.Companion.() -> SquareRootsComputer<MatrixWithProperties<Number, Matrix>>,
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
    SquareRootsComputer.Key<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaPropertiesForMatrixWithProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackSquareRootComputer = block(),
        )
    }
}