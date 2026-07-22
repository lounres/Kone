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
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


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

// TODO: Remove the checker when KT-73135 will be fixed
public object ScalarBaseForMatrixFunctionExponentViaDefaultSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> ScalarBaseForMatrixFunction.Companion.exponentViaDefault() : ScalarBaseForMatrixFunction<Number> {
        val koneContextRegistry = koneContextRegistry.get()
        return exponentViaDefault(
            exponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<Number>()) {
                "ScalarBaseForMatrixFunction.exponentViaDefault<${suppliedTypeOf<Number>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number> ScalarBaseForMatrixFunction.Companion.setExponentViaDefault(
        exponentComputer: ExponentComputer<Number>,
    ) {
        ScalarBaseForMatrixExponentKey<Number>() correspondsTo RegisteredValueProvider.cached {
            exponentViaDefault(
                exponentComputer = exponentComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number> ScalarBaseForMatrixFunction.Companion.setExponentViaDefault() {
        ScalarBaseForMatrixExponentKey<Number>() correspondsTo RegisteredValueProvider.cached {
            exponentViaDefault<Number>()
        }
    }
    
    private class ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundViaDefault<Number>(
        private val order: Order<Number>,
        private val exponentComputer: ExponentComputer<Number>,
        private val complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
    ) : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> {
    
        override fun evaluate(derivativeOrder: UInt, value: ComplexNumber<Number>): ComplexNumber<Number> =
            complexNumberExponentComputer { value.exponent() }
    
        override fun bound(derivativeOrder: UInt, convexHullVertices: KoneIterable<ComplexNumber<Number>>): Number {
            KoneContext.localUnwrap(order, exponentComputer)
            return convexHullVertices.maxOf<_, Number> { it.realPart }.exponent()
        }
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
    
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.exponentViaDefault() : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> {
        val koneContextRegistry = koneContextRegistry.get()
        return exponentViaDefault(
            order = koneContextRegistry.requestFor(Order.Key<Number>()) {
                "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.exponentViaDefault<${suppliedTypeOf<Number>()}>"
            },
            exponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<Number>()) {
                "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.exponentViaDefault<${suppliedTypeOf<Number>()}>"
            },
            complexNumberExponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<ComplexNumber<Number>>()) {
                "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.exponentViaDefault<${suppliedTypeOf<Number>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setExponentViaDefault(
        order: Order<Number>,
        exponentComputer: ExponentComputer<Number>,
        complexNumberExponentComputer: ExponentComputer<ComplexNumber<Number>>,
    ) {
        ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            exponentViaDefault(
                order = order,
                exponentComputer = exponentComputer,
                complexNumberExponentComputer = complexNumberExponentComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setExponentViaDefault() {
        ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
            exponentViaDefault<Number>()
        }
    }
}