/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class ExponentComputerViaDefaultForComplexNumbers<Number>(
    private val numberRing: CommutativeRing<Number>,
    private val numberExponentComputer: ExponentComputer<Number>,
    private val numberCosineComputer: CosineComputer<Number>,
    private val numberSineComputer: SineComputer<Number>,
) : ExponentComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.exponent(): ComplexNumber<Number> {
        val absoluteValue = numberExponentComputer { realPart.exponent() }
        val cos = numberCosineComputer { imaginaryPart.cos() }
        val sin = numberSineComputer { imaginaryPart.sin() }
        return numberRing.numberTimesNumber { ComplexNumber(cos * absoluteValue, sin * absoluteValue) }
    }
}

public fun <Number> ExponentComputer.Companion.viaDefaultForComplexNumbers(
    numberRing: CommutativeRing<Number>,
    numberExponentComputer: ExponentComputer<Number>,
    numberCosineComputer: CosineComputer<Number>,
    numberSineComputer: SineComputer<Number>,
): ExponentComputer<ComplexNumber<Number>> = ExponentComputerViaDefaultForComplexNumbers(
    numberRing = numberRing,
    numberExponentComputer = numberExponentComputer,
    numberCosineComputer = numberCosineComputer,
    numberSineComputer = numberSineComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> ExponentComputer.Companion.viaDefaultForComplexNumbers(): ExponentComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefaultForComplexNumbers(
        numberRing = koneContextRegistry[CommutativeRing.Key<Number>()],
        numberExponentComputer = koneContextRegistry[ExponentComputer.Key<Number>()],
        numberCosineComputer = koneContextRegistry[CosineComputer.Key<Number>()],
        numberSineComputer = koneContextRegistry[SineComputer.Key<Number>()],
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> ExponentComputer.Companion.setViaDefaultForComplexNumbers(
    numberRing: CommutativeRing<Number>,
    numberExponentComputer: ExponentComputer<Number>,
    numberCosineComputer: CosineComputer<Number>,
    numberSineComputer: SineComputer<Number>,
) {
    ExponentComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(
            numberRing = numberRing,
            numberExponentComputer = numberExponentComputer,
            numberCosineComputer = numberCosineComputer,
            numberSineComputer = numberSineComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number> ExponentComputer.Companion.setViaDefaultForComplexNumbers() {
    ExponentComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>()
    }
}