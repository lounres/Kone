/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixKey
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


@Suppliable
private class TransposeMatrixComputerViaProperties<@Supply Number, @Supply Matrix : MDList2<Number>>(
    private val fallbackTransposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) : TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> {
    override fun MatrixWithProperties<Number, Matrix>.transpose(): MatrixWithProperties<Number, Matrix> =
        properties.getOrElse(TransposeMatrixKey<Number, MatrixWithProperties<Number, Matrix>>()) {
            with(fallbackTransposeMatrixComputer) { this@transpose.transpose() }
        }
}

@Suppliable
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.viaProperties(
    fallbackTransposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = TransposeMatrixComputerViaProperties(
    fallbackTransposeMatrixComputer = fallbackTransposeMatrixComputer,
)

@Suppliable
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.viaProperties(
    block: TransposeMatrixComputer.Companion.() -> TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
): TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>> = viaProperties(
    fallbackTransposeMatrixComputer = block()
)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.setViaProperties(
    fallbackTransposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) {
    TransposeMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackTransposeMatrixComputer = fallbackTransposeMatrixComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.setViaProperties(
    block: TransposeMatrixComputer.Companion.() -> TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) {
    TransposeMatrixComputer.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        viaProperties(
            fallbackTransposeMatrixComputer = block(),
        )
    }
}