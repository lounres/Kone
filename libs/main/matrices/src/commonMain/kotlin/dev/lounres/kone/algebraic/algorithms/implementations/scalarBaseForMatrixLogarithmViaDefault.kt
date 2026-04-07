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
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


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
        else complexNumberFieldExtension { value.reciprocal().pow(derivativeOrder) }
    
    override fun bound(derivativeOrder: UInt, convexHullVertices: KoneIterable<ComplexNumber<Number>>): Number =
        if (derivativeOrder == 0u)
            context(order, numberField, positiveSquareRootComputer, logarithmComputer, planarVectorArgumentComputer) {
                convexHullVertices.maxOf<_, Number> { ComplexNumber(it.absoluteValue().logarithm(), it.argument()).norm() }.positiveSquareRoot()
            }
        else
            context(order, numberField, positiveSquareRootComputer) { convexHullVertices.maxOf<_, Number> { it.norm() }.positiveSquareRoot().reciprocal().pow(derivativeOrder) }
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

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.logarithmViaDefault(
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
    return logarithmViaDefault(
        order = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<$numberType>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<$numberType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<$numberType>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<$numberType>"
        },
        logarithmComputer = koneContextRegistry.requestFor(LogarithmComputer.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<$numberType>"
        },
        complexNumberLogarithmComputer = koneContextRegistry.requestFor(LogarithmComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType)) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<$numberType>"
        },
        planarVectorArgumentComputer = koneContextRegistry.requestFor(PlanarVectorArgumentComputer.Key<Number>(numberType = numberType)) {
            "ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.logarithmViaDefault<$numberType>"
        }
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setLogarithmViaDefault(
    numberType: SuppliedType,
    order: Order<Number>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    logarithmComputer: LogarithmComputer<Number>,
    complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    planarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
) {
    ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<Number>(numberType = numberType).withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
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

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound.Companion.setLogarithmViaDefault(
    numberType: SuppliedType,
) {
    ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<Number>(numberType = numberType).withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        logarithmViaDefault<Number>(numberType = numberType)
    }
}