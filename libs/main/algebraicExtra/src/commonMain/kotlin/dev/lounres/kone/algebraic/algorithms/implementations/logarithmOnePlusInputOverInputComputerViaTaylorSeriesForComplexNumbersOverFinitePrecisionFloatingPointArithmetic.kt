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
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.leq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class LogarithmOnePlusInputOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
    private val numberRing: CommutativeRing<Number>,
    private val numberOrder: Order<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    private val threshold: Number,
) : LogarithmOnePlusInputOverInputComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.logarithmOnePlusThisOverThis(): ComplexNumber<Number> =
        context(
            numberRing,
            numberOrder,
            complexNumberFieldExtension,
            numberPositiveSquareRootComputer,
            complexNumberLogarithmComputer,
        ) {
            if (this.absoluteValue() leq threshold) {
                var result = complexNumberFieldExtension.one
                var stepNumerator = complexNumberFieldExtension.one
                var stepDenominator = 1u
                while (true) {
                    stepDenominator++
                    stepNumerator *= -this
                    val step = stepNumerator / stepDenominator
                    if (step.isZero()) break
                    
                    val oldResult = result
                    result += step
                    if (oldResult eq result) break
                }
                result
            } else (this + 1).logarithm() / this
        }
}

public fun <Number> LogarithmComputer.Companion.viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberRing: CommutativeRing<Number>,
    numberOrder: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    threshold: Number,
): LogarithmOnePlusInputOverInputComputer<ComplexNumber<Number>> = LogarithmOnePlusInputOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberRing = numberRing,
    numberOrder = numberOrder,
    complexNumberFieldExtension = complexNumberFieldExtension,
    numberPositiveSquareRootComputer = numberPositiveSquareRootComputer,
    complexNumberLogarithmComputer = complexNumberLogarithmComputer,
    threshold = threshold,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> LogarithmComputer.Companion.viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberType: SuppliedType,
    threshold: Number,
): LogarithmOnePlusInputOverInputComputer<ComplexNumber<Number>> {
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
    return viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
        numberRing = koneContextRegistry[CommutativeRing.Key<Number>(numberType = numberType)],
        numberOrder = koneContextRegistry[Order.Key<Number>(elementType = numberType)],
        complexNumberFieldExtension = koneContextRegistry[FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)],
        numberPositiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)],
        complexNumberLogarithmComputer = koneContextRegistry[LogarithmComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType)],
        threshold = threshold,
    )
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number> LogarithmComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberType: SuppliedType,
    numberRing: CommutativeRing<Number>,
    numberOrder: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    complexNumberLogarithmComputer: LogarithmComputer<ComplexNumber<Number>>,
    threshold: Number,
) {
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
    LogarithmOnePlusInputOverInputComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
            numberRing = numberRing,
            numberOrder = numberOrder,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberPositiveSquareRootComputer = numberPositiveSquareRootComputer,
            complexNumberLogarithmComputer = complexNumberLogarithmComputer,
            threshold = threshold,
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> LogarithmComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberType: SuppliedType,
    threshold: Number,
) {
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
    LogarithmOnePlusInputOverInputComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(numberType = numberType, threshold = threshold)
    }
}