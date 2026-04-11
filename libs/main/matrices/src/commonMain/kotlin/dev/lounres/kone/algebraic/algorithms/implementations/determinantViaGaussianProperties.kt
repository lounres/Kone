/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.DeterminantComputer
import dev.lounres.kone.algebraic.algorithms.DeterminantKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class DeterminantComputerViaProperties<Number, Matrix : MDList2<Number>>(
    private val numberType: SuppliedType,
    private val fallbackDeterminantComputer: DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.determinant(): Number =
        properties.getOrElse(DeterminantKey<Number>(numberType = numberType)) {
            with(fallbackDeterminantComputer) { this@determinant.determinant() }
        }
}

public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaProperties(
    numberType: SuppliedType,
    fallbackDeterminantComputer: DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
): DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>> = DeterminantComputerViaProperties(
    numberType = numberType,
    fallbackDeterminantComputer = fallbackDeterminantComputer,
)

public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaProperties(
    numberType: SuppliedType,
    block: DeterminantComputer.Companion.() -> DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
): DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    numberType = numberType,
    fallbackDeterminantComputer = block(),
)

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackDeterminantComputer: DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    DeterminantComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            fallbackDeterminantComputer = fallbackDeterminantComputer,
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: DeterminantComputer.Companion.() -> DeterminantComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    DeterminantComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            fallbackDeterminantComputer = block(),
        )
    }
}