/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.HyperbolicSineOverInputComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.eq
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class HyperbolicSineOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
    private val complexNumbersField: Field<ComplexNumber<Number>>,
) : HyperbolicSineOverInputComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.sinhOverThis(): ComplexNumber<Number> =
        context(
            complexNumbersField,
        ) {
            var result = complexNumbersField.one
            var step = complexNumbersField.one
            var stepNumber = 1u
            while (true) {
                stepNumber++
                step *= this / stepNumber
                stepNumber++
                step *= this / stepNumber
                if (step.isZero()) break
                
                val oldResult = result
                result += step
                if (oldResult eq result) break
            }
            result
        }
}

public fun <Number> HyperbolicSineOverInputComputer.Companion.viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    complexNumbersField: Field<ComplexNumber<Number>>,
): HyperbolicSineOverInputComputer<ComplexNumber<Number>> = HyperbolicSineOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    complexNumbersField = complexNumbersField,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> HyperbolicSineOverInputComputer.Companion.viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(): HyperbolicSineOverInputComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
        complexNumbersField = koneContextRegistry[Field.Key<ComplexNumber<Number>>()],
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> HyperbolicSineOverInputComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    complexNumbersField: Field<ComplexNumber<Number>>,
) {
    HyperbolicSineOverInputComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
            complexNumbersField = complexNumbersField,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number> HyperbolicSineOverInputComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic() {
    HyperbolicSineOverInputComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>()
    }
}