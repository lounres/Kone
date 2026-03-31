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
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


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

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaAdjugateMatrixAndDeterminant(
    matrixType: SuppliedType,
): InverseMatrixComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaAdjugateMatrixAndDeterminant(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)) {
            "InverseMatrixComputer.viaAdjugateMatrixAndDeterminant<?, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaAdjugateMatrixAndDeterminant(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, Matrix>,
) {
    InverseMatrixComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaAdjugateMatrixAndDeterminant<Number, Matrix>(
            matrixFactory = matrixFactory,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaAdjugateMatrixAndDeterminant(
    matrixType: SuppliedType,
) {
    InverseMatrixComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaAdjugateMatrixAndDeterminant<Number, Matrix>(
            matrixType = matrixType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaAdjugateMatrixAndDeterminant(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
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
    InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val inverseMatrixComputer = viaAdjugateMatrixAndDeterminant<Number, MatrixWithProperties<Number, Matrix>>(
            matrixFactory = matrixFactory,
        )
        inverseMatrixComputer { matrix.get().invert() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaAdjugateMatrixAndDeterminant(
    numberType: SuppliedType,
    matrixType: SuppliedType,
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
    InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val inverseMatrixComputer = viaAdjugateMatrixAndDeterminant<Number, MatrixWithProperties<Number, Matrix>>(
            matrixType = matrixWithPropertiesType,
        )
        inverseMatrixComputer { matrix.get().invert() }
    }
}