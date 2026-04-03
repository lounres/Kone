/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.algorithms.LogarithmComputer
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixFunction
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixLogarithmKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.logarithm
import dev.lounres.kone.algebraic.pow
import dev.lounres.kone.algebraic.reciprocal
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.SuppliedType


private class ScalarBaseForMatrixLogarithmViaDefault<Number>(
    private val numberField: Field<Number>,
    private val logarithmComputer: LogarithmComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> {
    override fun evaluate(derivativeOrder: UInt, value: Number): Number =
        if (derivativeOrder == 0u) logarithmComputer { value.logarithm() }
        else numberField { value.reciprocal().pow(derivativeOrder) }
}

public fun <Number> ScalarBaseForMatrixFunction.Companion.logarithmViaDefault(
    numberField: Field<Number>,
    logarithmComputer: LogarithmComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> = ScalarBaseForMatrixLogarithmViaDefault(
    numberField = numberField,
    logarithmComputer = logarithmComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunction.Companion.logarithmViaDefault(
    numberType: SuppliedType,
) : ScalarBaseForMatrixFunction<Number> {
    val koneContextRegistry = koneContextRegistry.get()
    return logarithmViaDefault(
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunction.logarithmViaDefault<$numberType>"
        },
        logarithmComputer = koneContextRegistry.requestFor(LogarithmComputer.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunction.logarithmViaDefault<$numberType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> ScalarBaseForMatrixFunction.Companion.setLogarithmViaDefault(
    numberType: SuppliedType,
    numberField: Field<Number>,
    logarithmComputer: LogarithmComputer<Number>,
) {
    ScalarBaseForMatrixLogarithmKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        logarithmViaDefault(
            numberField = numberField,
            logarithmComputer = logarithmComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunction.Companion.setLogarithmViaDefault(
    numberType: SuppliedType,
) {
    ScalarBaseForMatrixLogarithmKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        logarithmViaDefault<Number>(numberType = numberType)
    }
}