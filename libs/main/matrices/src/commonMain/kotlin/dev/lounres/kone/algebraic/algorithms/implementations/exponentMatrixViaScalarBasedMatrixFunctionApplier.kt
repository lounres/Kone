/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.ExponentComputer
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixFunction
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentKey
import dev.lounres.kone.algebraic.algorithms.ScalarBasedMatrixFunctionApplier
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.invoke
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.SuppliedType


private class ExponentMatrixComputerViaScalarBasedMatrixFunctionApplier<Number, Matrix : MDList2<Number>>(
    private val scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>,
    private val scalarBaseForMatrixExponent: ScalarBaseForMatrixFunction<Number>,
) : ExponentComputer<Matrix> {
    override fun Matrix.exponent(): Matrix = scalarBasedMatrixFunctionApplier { scalarBaseForMatrixExponent(this) }
}

public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.viaScalarBasedMatrixFunctionApplier(
    scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>,
    scalarBaseForMatrixExponent: ScalarBaseForMatrixFunction<Number>,
): ExponentComputer<Matrix> = ExponentMatrixComputerViaScalarBasedMatrixFunctionApplier(
    scalarBasedMatrixFunctionApplier = scalarBasedMatrixFunctionApplier,
    scalarBaseForMatrixExponent = scalarBaseForMatrixExponent,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.viaScalarBasedMatrixFunctionApplier(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): ExponentComputer<Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaScalarBasedMatrixFunctionApplier(
        scalarBasedMatrixFunctionApplier = koneContextRegistry.requestFor(ScalarBasedMatrixFunctionApplier.Key<Number, Matrix>(matrixType = matrixType)) {
            "ExponentComputer.viaScalarBasedMatrixFunctionApplier<$numberType, $matrixType>"
        },
        scalarBaseForMatrixExponent = koneContextRegistry.requestFor(ScalarBaseForMatrixExponentKey<Number>(numberType = numberType)) {
            "ExponentComputer.viaScalarBasedMatrixFunctionApplier<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.setViaScalarBasedMatrixFunctionApplier(
    matrixType: SuppliedType,
    scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>,
    scalarBaseForMatrixExponent: ScalarBaseForMatrixFunction<Number>,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaScalarBasedMatrixFunctionApplier<Number, Matrix>(
            scalarBasedMatrixFunctionApplier = scalarBasedMatrixFunctionApplier,
            scalarBaseForMatrixExponent = scalarBaseForMatrixExponent,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> ExponentComputer.Companion.setViaScalarBasedMatrixFunctionApplier(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    ExponentComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaScalarBasedMatrixFunctionApplier<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
        )
    }
}