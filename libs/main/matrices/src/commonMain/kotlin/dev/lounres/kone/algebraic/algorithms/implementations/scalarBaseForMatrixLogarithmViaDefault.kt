/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.FieldExtension
import dev.lounres.kone.algebraic.absoluteValue
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.argument
import dev.lounres.kone.algebraic.norm
import dev.lounres.kone.algebraic.pow
import dev.lounres.kone.algebraic.reciprocal
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.utils.maxOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextHolder
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrapLocallyAsExtensionReceivers
import dev.lounres.kone.contexts.useLocallyAsExtensionReceivers
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class ScalarBaseForMatrixLogarithmViaDefault<Number>(
    private val numberField: Field<Number>,
    private val logarithmComputer: LogarithmComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> {
    override fun evaluate(derivativeOrder: UInt, value: Number): Number =
        if (derivativeOrder == 0u) logarithmComputer { value.logarithm() }
        else context(numberField.numberReciprocal, numberField.powerNumberUInt) { value.reciprocal().pow(derivativeOrder) }
}

public fun <Number> ScalarBaseForMatrixFunction.Companion.logarithmViaDefault(
    numberField: Field<Number>,
    logarithmComputer: LogarithmComputer<Number>,
) : ScalarBaseForMatrixFunction<Number> = ScalarBaseForMatrixLogarithmViaDefault(
    numberField = numberField,
    logarithmComputer = logarithmComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> ScalarBaseForMatrixFunction.Companion.logarithmViaDefault() : ScalarBaseForMatrixFunction<Number> {
    val koneContextRegistry = koneContextRegistry.get()
    return logarithmViaDefault(
        numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "ScalarBaseForMatrixFunction.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
        logarithmComputer = koneContextRegistry.requestFor(LogarithmComputer.Key<Number>()) {
            "ScalarBaseForMatrixFunction.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> ScalarBaseForMatrixFunction.Companion.setLogarithmViaDefault(
    numberField: Field<Number>,
    logarithmComputer: LogarithmComputer<Number>,
) {
    ScalarBaseForMatrixLogarithmKey<Number>() correspondsTo RegisteredValueProvider.cached {
        logarithmViaDefault(
            numberField = numberField,
            logarithmComputer = logarithmComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number> ScalarBaseForMatrixFunction.Companion.setLogarithmViaDefault() {
    ScalarBaseForMatrixLogarithmKey<Number>() correspondsTo RegisteredValueProvider.cached {
        logarithmViaDefault<Number>()
    }
}

private class ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundViaDefault<Number>(
    private val order: Order<Number>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val logarithmComputer: LogarithmComputer<Number>,
    private val complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    private val planarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
) : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> {
    override fun evaluate(derivativeOrder: UInt, value: ComplexNumber<Number>): ComplexNumber<Number> =
        if (derivativeOrder == 0u) complexNumberLogarithmComputer { value.logarithm() }
        else context(complexNumberFieldExtension.numberReciprocal, complexNumberFieldExtension.powerNumberUInt) { value.reciprocal().pow(derivativeOrder) }
    
    override fun bound(derivativeOrder: UInt, convexHullVertices: KoneIterable<ComplexNumber<Number>>): Number {
        KoneContext.useLocallyAsExtensionReceivers(order, numberField, positiveSquareRootComputer, logarithmComputer, planarVectorArgumentComputer)
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        
        return if (derivativeOrder == 0u) convexHullVertices.maxOf<_, Number> { ComplexNumber(it.absoluteValue().logarithm(), it.argument()).norm() }.positiveSquareRoot()
        else convexHullVertices.maxOf<_, Number> { it.norm() }.positiveSquareRoot().reciprocal().pow(derivativeOrder)
    }
}

public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.logarithmViaDefault(
    order: Order<Number>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    logarithmComputer: LogarithmComputer<Number>,
    complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    planarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
) : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> = ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundViaDefault(
    order = order,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    positiveSquareRootComputer = positiveSquareRootComputer,
    logarithmComputer = logarithmComputer,
    complexNumberLogarithmComputer = complexNumberLogarithmComputer,
    planarVectorArgumentComputer = planarVectorArgumentComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.logarithmViaDefault() : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> {
    val koneContextRegistry = koneContextRegistry.get()
    return logarithmViaDefault(
        order = koneContextRegistry.requestFor(Order.Key<Number>()) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
        logarithmComputer = koneContextRegistry.requestFor(LogarithmComputer.Key<Number>()) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
        complexNumberLogarithmComputer = koneContextRegistry.requestFor(LogarithmComputer.Key<ComplexNumber<Number>>()) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        },
        planarVectorArgumentComputer = koneContextRegistry.requestFor(PlanarVectorArgumentComputer.Key<Number>()) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<${suppliedTypeOf<Number>()}>"
        }
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setLogarithmViaDefault(
    order: Order<Number>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    logarithmComputer: LogarithmComputer<Number>,
    complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    planarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
) {
    ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        logarithmViaDefault(
            order = order,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            positiveSquareRootComputer = positiveSquareRootComputer,
            logarithmComputer = logarithmComputer,
            complexNumberLogarithmComputer = complexNumberLogarithmComputer,
            planarVectorArgumentComputer = planarVectorArgumentComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setLogarithmViaDefault() {
    ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<Number>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        logarithmViaDefault<Number>()
    }
}