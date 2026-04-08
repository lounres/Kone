/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class HyperbolicSineOverInputComputerViaDefaultForComplexNumbers<Number>(
    private val field: Field<Number>,
    private val complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val cosineComputer: CosineComputer<Number>,
    private val sineComputer: SineComputer<Number>,
    private val hyperbolicCosineComputer: HyperbolicCosineComputer<Number>,
    private val hyperbolicSineComputer: HyperbolicSineComputer<Number>,
) : HyperbolicSineOverInputComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.sinhOverThis(): ComplexNumber<Number> =
        context(
            field,
            complexNumbersFieldExtension,
            positiveSquareRootComputer,
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
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    cosineComputer: CosineComputer<Number>,
    sineComputer: SineComputer<Number>,
    hyperbolicCosineComputer: HyperbolicCosineComputer<Number>,
    hyperbolicSineComputer: HyperbolicSineComputer<Number>,
): HyperbolicSineOverInputComputer<ComplexNumber<Number>> = HyperbolicSineOverInputComputerViaDefaultForComplexNumbers(
    field = field,
    complexNumbersFieldExtension = complexNumbersFieldExtension,
    positiveSquareRootComputer = positiveSquareRootComputer,
    cosineComputer = cosineComputer,
    sineComputer = sineComputer,
    hyperbolicCosineComputer = hyperbolicCosineComputer,
    hyperbolicSineComputer = hyperbolicSineComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> HyperbolicSineOverInputComputer.Companion.viaDefaultForComplexNumbers(
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
    return viaDefaultForComplexNumbers<Number>(
        field = koneContextRegistry[Field.Key<Number>(numberType = numberType)],
        complexNumbersFieldExtension = koneContextRegistry[FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)],
        positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)],
        cosineComputer = koneContextRegistry[CosineComputer.Key<Number>(numberType = numberType)],
        sineComputer = koneContextRegistry[SineComputer.Key<Number>(numberType = numberType)],
        hyperbolicCosineComputer = koneContextRegistry[HyperbolicCosineComputer.Key<Number>(numberType = numberType)],
        hyperbolicSineComputer = koneContextRegistry[HyperbolicSineComputer.Key<Number>(numberType = numberType)],
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> HyperbolicSineOverInputComputer.Companion.setViaDefaultForComplexNumbers(
    numberType: SuppliedType,
    field: Field<Number>,
    complexNumbersFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    cosineComputer: CosineComputer<Number>,
    sineComputer: SineComputer<Number>,
    hyperbolicCosineComputer: HyperbolicCosineComputer<Number>,
    hyperbolicSineComputer: HyperbolicSineComputer<Number>,
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
        viaDefaultForComplexNumbers<Number>(
            field = field,
            complexNumbersFieldExtension = complexNumbersFieldExtension,
            positiveSquareRootComputer = positiveSquareRootComputer,
            cosineComputer = cosineComputer,
            sineComputer = sineComputer,
            hyperbolicCosineComputer = hyperbolicCosineComputer,
            hyperbolicSineComputer = hyperbolicSineComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> HyperbolicSineOverInputComputer.Companion.setViaDefaultForComplexNumbers(
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
        viaDefaultForComplexNumbers<Number>(numberType = numberType)
    }
}