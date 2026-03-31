/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.LogarithmComputer
import dev.lounres.kone.algebraic.algorithms.LogarithmKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.orNull
import dev.lounres.kone.maybe.orThrow
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class LogarithmComputerViaProperties<Number, Matrix : MDList2<Number>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackLogarithmMatrixComputer: LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
) : LogarithmComputer<MatrixWithProperties<Number, Matrix>> {
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
    override fun MatrixWithProperties<Number, Matrix>.logarithm(): MatrixWithProperties<Number, Matrix> {
        val key = LogarithmKey<MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType)
        val provider = properties.provideOrNull(key) ?: return with(fallbackLogarithmMatrixComputer) { this@logarithm.logarithm() }
        return provider.get().orThrow { IllegalArgumentException("Cannot compute logarithm of $this: it has a property $key that corresponds to 'None'") }
    }
    override fun MatrixWithProperties<Number, Matrix>.logarithmOrNull(): MatrixWithProperties<Number, Matrix>? {
        val key = LogarithmKey<MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType)
        val provider = properties.provideOrNull(key) ?: return with(fallbackLogarithmMatrixComputer) { this@logarithmOrNull.logarithmOrNull() }
        return provider.get().orNull()
    }
    override fun MatrixWithProperties<Number, Matrix>.logarithmMaybe(): Maybe<MatrixWithProperties<Number, Matrix>> =
        properties.getOrElse(LogarithmKey<MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType)) {
            with(fallbackLogarithmMatrixComputer) {
                this@logarithmMaybe.logarithmMaybe()
            }
        }
}

public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackLogarithmMatrixComputer: LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
): LogarithmComputer<MatrixWithProperties<Number, Matrix>> = LogarithmComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
)

public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: LogarithmComputer.Companion.() -> LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
): LogarithmComputer<MatrixWithProperties<Number, Matrix>> = viaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackLogarithmMatrixComputer = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackLogarithmMatrixComputer: LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
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
    LogarithmComputer.Key<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackLogarithmMatrixComputer = fallbackLogarithmMatrixComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: LogarithmComputer.Companion.() -> LogarithmComputer<MatrixWithProperties<Number, Matrix>>,
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
    LogarithmComputer.Key<MatrixWithProperties<Number, Matrix>>(numberType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackLogarithmMatrixComputer = block(),
        )
    }
}