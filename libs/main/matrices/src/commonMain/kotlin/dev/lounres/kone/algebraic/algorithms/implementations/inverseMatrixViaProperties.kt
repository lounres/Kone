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
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class InverseMatrixComputerViaProperties<Number, Matrix : MDList2<Number>>(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    private val fallbackInverseMatrixComputer: InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> {
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
    override fun MatrixWithProperties<Number, Matrix>.invert(): MatrixWithProperties<Number, Matrix>? =
        properties.getOrElse(InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType)) {
            with(fallbackInverseMatrixComputer) { this@invert.invert() }
        }
}

public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackInverseMatrixComputer: InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = InverseMatrixComputerViaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackInverseMatrixComputer = fallbackInverseMatrixComputer,
)

public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: InverseMatrixComputer.Companion.() -> InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    numberType = numberType,
    matrixType = matrixType,
    fallbackInverseMatrixComputer = block(),
)

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    fallbackInverseMatrixComputer: InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    InverseMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackInverseMatrixComputer = fallbackInverseMatrixComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaProperties(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    block: InverseMatrixComputer.Companion.() -> InverseMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    InverseMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            numberType = numberType,
            matrixType = matrixType,
            fallbackInverseMatrixComputer = block(),
        )
    }
}