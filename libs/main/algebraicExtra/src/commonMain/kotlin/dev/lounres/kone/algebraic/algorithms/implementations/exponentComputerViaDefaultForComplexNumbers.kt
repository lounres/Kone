/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.algorithms.CosineComputer
import dev.lounres.kone.algebraic.algorithms.ExponentComputer
import dev.lounres.kone.algebraic.algorithms.SineComputer
import dev.lounres.kone.algebraic.algorithms.cos
import dev.lounres.kone.algebraic.algorithms.exponent
import dev.lounres.kone.algebraic.algorithms.sin
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import dev.lounres.kone.suppliedTypes.suppliedType
import kotlin.reflect.KVariance.OUT


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
        return numberRing { ComplexNumber(cos * absoluteValue, sin * absoluteValue) }
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

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ExponentComputer.Companion.viaDefaultForComplexNumbers(
    numberType: SuppliedType,
): ExponentComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefaultForComplexNumbers(
        numberRing = koneContextRegistry[CommutativeRing.Key<Number>(numberType = numberType)],
        numberExponentComputer = koneContextRegistry[ExponentComputer.Key<Number>(numberType = numberType)],
        numberCosineComputer = koneContextRegistry[CosineComputer.Key<Number>(numberType = numberType)],
        numberSineComputer = koneContextRegistry[SineComputer.Key<Number>(numberType = numberType)],
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> ExponentComputer.Companion.setViaDefaultForComplexNumbers(
    numberType: SuppliedType,
    numberRing: CommutativeRing<Number>,
    numberExponentComputer: ExponentComputer<Number>,
    numberCosineComputer: CosineComputer<Number>,
    numberSineComputer: SineComputer<Number>,
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
    ExponentComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(
            numberRing = numberRing,
            numberExponentComputer = numberExponentComputer,
            numberCosineComputer = numberCosineComputer,
            numberSineComputer = numberSineComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> ExponentComputer.Companion.setViaDefaultForComplexNumbers(
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
    ExponentComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(numberType = numberType)
    }
}