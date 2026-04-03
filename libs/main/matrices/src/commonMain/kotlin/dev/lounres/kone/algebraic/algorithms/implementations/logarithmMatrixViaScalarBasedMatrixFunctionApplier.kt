/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.LogarithmComputer
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixFunction
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixLogarithmKey
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


private class LogarithmMatrixComputerViaScalarBasedMatrixFunctionApplier<Number, Matrix : MDList2<Number>>(
    private val scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>,
    private val scalarBaseForMatrixLogarithm: ScalarBaseForMatrixFunction<Number>,
) : LogarithmComputer<Matrix> {
    override fun Matrix.logarithm(): Matrix = scalarBasedMatrixFunctionApplier { scalarBaseForMatrixLogarithm(this) }
}

public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.viaScalarBasedMatrixFunctionApplier(
    scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>,
    scalarBaseForMatrixLogarithm: ScalarBaseForMatrixFunction<Number>,
): LogarithmComputer<Matrix> = LogarithmMatrixComputerViaScalarBasedMatrixFunctionApplier(
    scalarBasedMatrixFunctionApplier = scalarBasedMatrixFunctionApplier,
    scalarBaseForMatrixLogarithm  = scalarBaseForMatrixLogarithm,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.viaScalarBasedMatrixFunctionApplier(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): LogarithmComputer<Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaScalarBasedMatrixFunctionApplier(
        scalarBasedMatrixFunctionApplier = koneContextRegistry.requestFor(ScalarBasedMatrixFunctionApplier.Key<Number, Matrix>(matrixType = matrixType)) {
            "LogarithmComputer.viaScalarBasedMatrixFunctionApplier<$numberType, $matrixType>"
        },
        scalarBaseForMatrixLogarithm = koneContextRegistry.requestFor(ScalarBaseForMatrixLogarithmKey<Number>(numberType = numberType)) {
            "LogarithmComputer.viaScalarBasedMatrixFunctionApplier<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaScalarBasedMatrixFunctionApplier(
    matrixType: SuppliedType,
    scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>,
    scalarBaseForMatrixLogarithm: ScalarBaseForMatrixFunction<Number>,
) {
    LogarithmComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaScalarBasedMatrixFunctionApplier<Number, Matrix>(
            scalarBasedMatrixFunctionApplier = scalarBasedMatrixFunctionApplier,
            scalarBaseForMatrixLogarithm = scalarBaseForMatrixLogarithm,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> LogarithmComputer.Companion.setViaScalarBasedMatrixFunctionApplier(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    LogarithmComputer.Key<Matrix>(numberType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaScalarBasedMatrixFunctionApplier<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
        )
    }
}