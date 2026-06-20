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
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class HyperbolicSineOverInputComputerViaDefaultForComplexNumbers<Number>(
    private val field: Field<Number>,
    private val complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val cosineComputer: CosineComputer<Number>,
    private val sineComputer: SineComputer<Number>,
    private val hyperbolicCosineComputer: HyperbolicCosineComputer<Number>,
    private val hyperbolicSineComputer: HyperbolicSineComputer<Number>,
) : HyperbolicSineOverInputComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.sinhOverThis(): ComplexNumber<Number> =
        context(
            field,
            complexNumbersFieldExtension,
            cosineComputer,
            sineComputer,
            hyperbolicCosineComputer,
            hyperbolicSineComputer,
        ) {
            if (this.isNotZero())
                ComplexNumber(
                    realPart.sinh() * imaginaryPart.cos(),
                    realPart.cosh() * imaginaryPart.sin(),
                ) / this
            else
                complexNumbersFieldExtension.one
        }
}

public fun <Number> HyperbolicSineOverInputComputer.Companion.viaDefaultForComplexNumbers(
    field: Field<Number>,
    complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    cosineComputer: CosineComputer<Number>,
    sineComputer: SineComputer<Number>,
    hyperbolicCosineComputer: HyperbolicCosineComputer<Number>,
    hyperbolicSineComputer: HyperbolicSineComputer<Number>,
): HyperbolicSineOverInputComputer<ComplexNumber<Number>> = HyperbolicSineOverInputComputerViaDefaultForComplexNumbers(
    field = field,
    complexNumbersFieldExtension = complexNumbersFieldExtension,
    cosineComputer = cosineComputer,
    sineComputer = sineComputer,
    hyperbolicCosineComputer = hyperbolicCosineComputer,
    hyperbolicSineComputer = hyperbolicSineComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> HyperbolicSineOverInputComputer.Companion.viaDefaultForComplexNumbers(): HyperbolicSineOverInputComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefaultForComplexNumbers<Number>(
        field = koneContextRegistry[Field.Key<Number>()],
        complexNumbersFieldExtension = koneContextRegistry[FieldExtension.Key<Number, ComplexNumber<Number>>()],
        cosineComputer = koneContextRegistry[CosineComputer.Key<Number>()],
        sineComputer = koneContextRegistry[SineComputer.Key<Number>()],
        hyperbolicCosineComputer = koneContextRegistry[HyperbolicCosineComputer.Key<Number>()],
        hyperbolicSineComputer = koneContextRegistry[HyperbolicSineComputer.Key<Number>()],
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> HyperbolicSineOverInputComputer.Companion.setViaDefaultForComplexNumbers(
    field: Field<Number>,
    complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    cosineComputer: CosineComputer<Number>,
    sineComputer: SineComputer<Number>,
    hyperbolicCosineComputer: HyperbolicCosineComputer<Number>,
    hyperbolicSineComputer: HyperbolicSineComputer<Number>,
) {
    HyperbolicSineOverInputComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(
            field = field,
            complexNumbersFieldExtension = complexNumbersFieldExtension,
            cosineComputer = cosineComputer,
            sineComputer = sineComputer,
            hyperbolicCosineComputer = hyperbolicCosineComputer,
            hyperbolicSineComputer = hyperbolicSineComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number> HyperbolicSineOverInputComputer.Companion.setViaDefaultForComplexNumbers() {
    HyperbolicSineOverInputComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>()
    }
}