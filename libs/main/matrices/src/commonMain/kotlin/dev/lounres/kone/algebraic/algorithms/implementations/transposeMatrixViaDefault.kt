/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.transpose
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


private class TransposeMatrixComputerViaDefault<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>
) : TransposeMatrixComputer<Number, Matrix> {
    override fun Matrix.transpose(): Matrix =
        matrixFactory.generateMatrix(rowNumber = columnNumber, columnNumber = rowNumber) { row, column -> this[column, row] }
}

public fun <Number, Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.viaDefault(
    matrixFactory: MatrixFactory<Number, Matrix>,
): TransposeMatrixComputer<Number, Matrix> = TransposeMatrixComputerViaDefault(
    matrixFactory = matrixFactory,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.viaDefault(): TransposeMatrixComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "TransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        }
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.setViaDefault(
    matrixFactory: MatrixFactory<Number, Matrix>,
) {
    TransposeMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            matrixFactory = matrixFactory,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.setViaDefault() {
    TransposeMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>()
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.useViaDefault(
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
) {
    TransposeMatrixKey<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        (viaDefault(matrixFactory = matrixFactory)) {
            matrix.get().transpose()
        }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.useViaDefault() {
    TransposeMatrixKey<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val transposeMatrixComputer = viaDefault<Number, MatrixWithProperties<Number, Matrix>>()
        transposeMatrixComputer {
            matrix.get().transpose()
        }
    }
}