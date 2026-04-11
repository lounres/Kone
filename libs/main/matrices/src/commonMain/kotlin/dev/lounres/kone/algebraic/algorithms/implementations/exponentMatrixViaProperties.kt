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
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.provideOrNull
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class ExponentComputerViaProperties<Number, Matrix : MDList2<Number>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackExponentMatrixComputer: ExponentComputer<MatrixWithProperties<Number, Matrix>>,
) : ExponentComputer<MatrixWithProperties<Number, Matrix>> {
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
    override fun MatrixWithProperties<Number, Matrix>.exponent(): MatrixWithProperties<Number, Matrix> {
        val key = ExponentKey<MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType)
        val provider = properties.provideOrNull(key) ?: return with(fallbackExponentMatrixComputer) { this@exponent.exponent() }
        return provider.get()
    }
}

public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackExponentMatrixComputer: ExponentComputer<MatrixWithProperties<Number, Matrix>>,
): ExponentComputer<MatrixWithProperties<Number, Matrix>> = ExponentComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackExponentMatrixComputer = fallbackExponentMatrixComputer,
)

public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: ExponentComputer.Companion.() -> ExponentComputer<MatrixWithProperties<Number, Matrix>>,
): ExponentComputer<MatrixWithProperties<Number, Matrix>> = viaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackExponentMatrixComputer = block(),
)

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackExponentMatrixComputer: ExponentComputer<MatrixWithProperties<Number, Matrix>>,
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
    ExponentComputer.Key<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackExponentMatrixComputer = fallbackExponentMatrixComputer,
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: ExponentComputer.Companion.() -> ExponentComputer<MatrixWithProperties<Number, Matrix>>,
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
    ExponentComputer.Key<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackExponentMatrixComputer = block(),
        )
    }
}