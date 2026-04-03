/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.algorithms.ExponentComputer
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixFunction
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.exponent
import dev.lounres.kone.algebraic.pow
import dev.lounres.kone.algebraic.reciprocal
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.SuppliedType


private class ScalarBaseForMatrixExponentViaDefault<Number>(
    private val numberField: Field<Number>,
    private val exponentComputer: ExponentComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> {
    override fun evaluate(derivativeOrder: UInt, value: Number): Number =
        if (derivativeOrder == 0u) exponentComputer { value.exponent() }
        else numberField { value.reciprocal().pow(derivativeOrder) }
}

public fun <Number> ScalarBaseForMatrixFunction.Companion.exponentViaDefault(
    numberField: Field<Number>,
    exponentComputer: ExponentComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> = ScalarBaseForMatrixExponentViaDefault(
    numberField = numberField,
    exponentComputer = exponentComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunction.Companion.exponentViaDefault(
    numberType: SuppliedType,
) : ScalarBaseForMatrixFunction<Number> {
    val koneContextRegistry = koneContextRegistry.get()
    return exponentViaDefault(
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunction.exponentViaDefault<$numberType>"
        },
        exponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunction.exponentViaDefault<$numberType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> ScalarBaseForMatrixFunction.Companion.setExponentViaDefault(
    numberType: SuppliedType,
    numberField: Field<Number>,
    exponentComputer: ExponentComputer<Number>,
) {
    ScalarBaseForMatrixExponentKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        exponentViaDefault(
            numberField = numberField,
            exponentComputer = exponentComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunction.Companion.setExponentViaDefault(
    numberType: SuppliedType,
) {
    ScalarBaseForMatrixExponentKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        exponentViaDefault<Number>(numberType = numberType)
    }
}