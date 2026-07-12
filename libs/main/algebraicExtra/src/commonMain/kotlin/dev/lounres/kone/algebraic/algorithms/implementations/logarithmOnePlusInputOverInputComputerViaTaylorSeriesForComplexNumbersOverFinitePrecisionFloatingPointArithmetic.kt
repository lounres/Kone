/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.LogarithmComputer
import dev.lounres.kone.algebraic.algorithms.LogarithmOnePlusInputOverInputComputer
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.logarithm
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContextHolder
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.unwrapLocallyAsExtensionReceivers
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.leq
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class LogarithmOnePlusInputOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
    private val numberRing: CommutativeRing<Number>,
    private val numberOrder: Order<Number>,
    private val complexNumberEquality: Equality<ComplexNumber<Number>>,
    private val complexNumberField: Field<ComplexNumber<Number>>,
    private val numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    private val threshold: Number,
) : LogarithmOnePlusInputOverInputComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.logarithmOnePlusThisOverThis(): ComplexNumber<Number> =
        context(
            numberOrder,
            numberRing,
            complexNumberEquality,
            numberPositiveSquareRootComputer,
            complexNumberLogarithmComputer,
        ) {
            KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberRing, complexNumberField)
            if (this@logarithmOnePlusThisOverThis.absoluteValue() leq threshold) {
                var result = complexNumberField.one
                var stepNumerator = complexNumberField.one
                var stepDenominator = 1u
                while (true) {
                    stepDenominator++
                    stepNumerator *= -this@logarithmOnePlusThisOverThis
                    val step = stepNumerator / stepDenominator
                    if (step.isZero()) break
                    
                    val oldResult = result
                    result += step
                    if (oldResult eq result) break
                }
                result
            } else (this@logarithmOnePlusThisOverThis + 1).logarithm() / this@logarithmOnePlusThisOverThis
        }
}

public fun <Number> LogarithmComputer.Companion.viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberRing: CommutativeRing<Number>,
    numberOrder: Order<Number>,
    complexNumberEquality: Equality<ComplexNumber<Number>>,
    complexNumberField: Field<ComplexNumber<Number>>,
    numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    threshold: Number,
): LogarithmOnePlusInputOverInputComputer<ComplexNumber<Number>> = LogarithmOnePlusInputOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberRing = numberRing,
    numberOrder = numberOrder,
    complexNumberEquality = complexNumberEquality,
    complexNumberField = complexNumberField,
    numberPositiveSquareRootComputer = numberPositiveSquareRootComputer,
    complexNumberLogarithmComputer = complexNumberLogarithmComputer,
    threshold = threshold,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> LogarithmComputer.Companion.viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    threshold: Number,
): LogarithmOnePlusInputOverInputComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
        numberRing = koneContextRegistry[CommutativeRing.Key<Number>()],
        numberOrder = koneContextRegistry[Order.Key<Number>()],
        complexNumberEquality = koneContextRegistry[Equality.Key<ComplexNumber<Number>>()],
        complexNumberField = koneContextRegistry[Field.Key<ComplexNumber<Number>>()],
        numberPositiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>()],
        complexNumberLogarithmComputer = koneContextRegistry[LogarithmComputer.Key<ComplexNumber<Number>>()],
        threshold = threshold,
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> LogarithmComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberRing: CommutativeRing<Number>,
    numberOrder: Order<Number>,
    complexNumberEquality: Equality<ComplexNumber<Number>>,
    complexNumberField: Field<ComplexNumber<Number>>,
    numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    threshold: Number,
) {
    LogarithmOnePlusInputOverInputComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
            numberRing = numberRing,
            numberOrder = numberOrder,
            complexNumberEquality = complexNumberEquality,
            complexNumberField = complexNumberField,
            numberPositiveSquareRootComputer = numberPositiveSquareRootComputer,
            complexNumberLogarithmComputer = complexNumberLogarithmComputer,
            threshold = threshold,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number> LogarithmComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    threshold: Number,
) {
    LogarithmOnePlusInputOverInputComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(threshold = threshold)
    }
}