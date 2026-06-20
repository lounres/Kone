/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.InverseMatrixComputer
import dev.lounres.kone.algebraic.algorithms.InverseMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.invert
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class InverseMatrixComputerViaAdjugateMatrixAndDeterminant<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>
) : InverseMatrixComputer<Number, Matrix> {
    override fun Matrix.invert(): Matrix? = TODO()
}

public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaAdjugateMatrixAndDeterminant(
    matrixFactory: MatrixFactory<Number, Matrix>,
): InverseMatrixComputer<Number, Matrix> = InverseMatrixComputerViaAdjugateMatrixAndDeterminant(
    matrixFactory = matrixFactory,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaAdjugateMatrixAndDeterminant(): InverseMatrixComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaAdjugateMatrixAndDeterminant(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "InverseMatrixComputer.viaAdjugateMatrixAndDeterminant<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaAdjugateMatrixAndDeterminant(
    matrixFactory: MatrixFactory<Number, Matrix>,
) {
    InverseMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaAdjugateMatrixAndDeterminant<Number, Matrix>(
            matrixFactory = matrixFactory,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaAdjugateMatrixAndDeterminant() {
    InverseMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaAdjugateMatrixAndDeterminant<Number, Matrix>()
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaAdjugateMatrixAndDeterminant(
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
) {
    InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val inverseMatrixComputer = viaAdjugateMatrixAndDeterminant<Number, MatrixWithProperties<Number, Matrix>>(
            matrixFactory = matrixFactory,
        )
        inverseMatrixComputer { matrix.get().invert() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaAdjugateMatrixAndDeterminant() {
    InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val inverseMatrixComputer = viaAdjugateMatrixAndDeterminant<Number, MatrixWithProperties<Number, Matrix>>()
        inverseMatrixComputer { matrix.get().invert() }
    }
}