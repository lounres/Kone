/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.eq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class HyperbolicSineOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
    private val complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
) : HyperbolicSineOverInputComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.sinhOverThis(): ComplexNumber<Number> =
        context(
            complexNumbersFieldExtension,
        ) {
            var result = complexNumbersFieldExtension.one
            var step = complexNumbersFieldExtension.one
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
    complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
): HyperbolicSineOverInputComputer<ComplexNumber<Number>> = HyperbolicSineOverInputComputerViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    complexNumbersFieldExtension = complexNumbersFieldExtension,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> HyperbolicSineOverInputComputer.Companion.viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberType: SuppliedType,
): HyperbolicSineOverInputComputer<ComplexNumber<Number>> {
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
    return viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
        complexNumbersFieldExtension = koneContextRegistry[FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)],
    )
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number> HyperbolicSineOverInputComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberType: SuppliedType,
    complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
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
    HyperbolicSineOverInputComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(
            complexNumbersFieldExtension = complexNumbersFieldExtension,
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> HyperbolicSineOverInputComputer.Companion.setViaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic(
    numberType: SuppliedType,
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
    HyperbolicSineOverInputComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaTaylorSeriesForComplexNumbersOverFinitePrecisionFloatingPointArithmetic<Number>(numberType = numberType)
    }
}