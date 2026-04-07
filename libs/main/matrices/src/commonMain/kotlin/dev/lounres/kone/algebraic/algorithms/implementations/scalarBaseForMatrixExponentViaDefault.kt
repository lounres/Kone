/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.algorithms.ExponentComputer
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentKey
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixFunction
import dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound
import dev.lounres.kone.algebraic.algorithms.exponent
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.utils.maxOf
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class ScalarBaseForMatrixExponentViaDefault<Number>(
    private val exponentComputer: ExponentComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> {
    override fun evaluate(derivativeOrder: UInt, value: Number): Number = exponentComputer { value.exponent() }
}

public fun <Number> ScalarBaseForMatrixFunction.Companion.exponentViaDefault(
    exponentComputer: ExponentComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> = ScalarBaseForMatrixExponentViaDefault(
    exponentComputer = exponentComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunction.Companion.exponentViaDefault(
    numberType: SuppliedType,
) : ScalarBaseForMatrixFunction<Number> {
    val koneContextRegistry = koneContextRegistry.get()
    return exponentViaDefault(
        exponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunction.exponentViaDefault<$numberType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> ScalarBaseForMatrixFunction.Companion.setExponentViaDefault(
    numberType: SuppliedType,
    exponentComputer: ExponentComputer<Number>,
) {
    ScalarBaseForMatrixExponentKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        exponentViaDefault(
            exponentComputer = exponentComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunction.Companion.setExponentViaDefault(
    numberType: SuppliedType,
) {
    ScalarBaseForMatrixExponentKey<Number>(numberType = numberType) correspondsTo RegisteredValueProvider.cached {
        exponentViaDefault<Number>(numberType = numberType)
    }
}

private class ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundViaDefault<Number>(
    private val order: Order<Number>,
    private val exponentComputer: ExponentComputer<Number>,
    private val complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
) : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> {

    override fun evaluate(derivativeOrder: UInt, value: ComplexNumber<Number>): ComplexNumber<Number> =
        complexNumberExponentComputer { value.exponent() }

    override fun bound(derivativeOrder: UInt, convexHullVertices: KoneIterable<ComplexNumber<Number>>): Number =
        context(order, exponentComputer) { convexHullVertices.maxOf<_, Number> { it.realPart }.exponent() }
}

public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.exponentViaDefault(
    order: Order<Number>,
    exponentComputer: ExponentComputer<Number>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
) : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> = ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundViaDefault(
    order = order,
    exponentComputer = exponentComputer,
    complexNumberExponentComputer = complexNumberExponentComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.exponentViaDefault(
    numberType: SuppliedType,
) : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            ),
        ),
        isNullable = false,
    )
    val koneContextRegistry = koneContextRegistry.get()
    return exponentViaDefault(
        order = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "ScalarBaseForMatrixFunction.exponentViaDefault<$numberType>"
        },
        exponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunction.exponentViaDefault<$numberType>"
        },
        complexNumberExponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType)) {
            "ScalarBaseForMatrixFunction.exponentViaDefault<$numberType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setExponentViaDefault(
    numberType: SuppliedType,
    order: Order<Number>,
    exponentComputer: ExponentComputer<Number>,
    complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
) {
    ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<Number>(numberType = numberType).withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        exponentViaDefault(
            order = order,
            exponentComputer = exponentComputer,
            complexNumberExponentComputer = complexNumberExponentComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setExponentViaDefault(
    numberType: SuppliedType,
) {
    ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<Number>(numberType = numberType).withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        exponentViaDefault<Number>(numberType = numberType)
    }
}