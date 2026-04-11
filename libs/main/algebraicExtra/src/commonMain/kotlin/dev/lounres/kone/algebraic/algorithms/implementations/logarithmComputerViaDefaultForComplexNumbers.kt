/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.absoluteValue
import dev.lounres.kone.algebraic.algorithms.LogarithmComputer
import dev.lounres.kone.algebraic.algorithms.PlanarVectorArgumentComputer
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.logarithm
import dev.lounres.kone.algebraic.argument
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class LogarithmComputerViaDefaultForComplexNumbers<Number>(
    private val numberRing: CommutativeRing<Number>,
    private val numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val numberLogarithmComputer: LogarithmComputer<Number>,
    private val numberPlanarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
) : LogarithmComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.logarithm(): ComplexNumber<Number> =
        context(
            numberRing,
            numberPositiveSquareRootComputer,
            numberLogarithmComputer,
            numberPlanarVectorArgumentComputer,
        ) {
            ComplexNumber(
                realPart = this.absoluteValue().logarithm(),
                imaginaryPart = this.argument(),
            )
        }
}

public fun <Number> LogarithmComputer.Companion.viaDefaultForComplexNumbers(
    numberRing: CommutativeRing<Number>,
    numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    numberLogarithmComputer: LogarithmComputer<Number>,
    numberPlanarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
): LogarithmComputer<ComplexNumber<Number>> = LogarithmComputerViaDefaultForComplexNumbers(
    numberRing = numberRing,
    numberPositiveSquareRootComputer = numberPositiveSquareRootComputer,
    numberLogarithmComputer = numberLogarithmComputer,
    numberPlanarVectorArgumentComputer = numberPlanarVectorArgumentComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> LogarithmComputer.Companion.viaDefaultForComplexNumbers(
    numberType: SuppliedType,
): LogarithmComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefaultForComplexNumbers(
        numberRing = koneContextRegistry[CommutativeRing.Key<Number>(numberType = numberType)],
        numberPositiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)],
        numberLogarithmComputer = koneContextRegistry[LogarithmComputer.Key<Number>(numberType = numberType)],
        numberPlanarVectorArgumentComputer = koneContextRegistry[PlanarVectorArgumentComputer.Key<Number>(numberType = numberType)],
    )
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number> LogarithmComputer.Companion.setViaDefaultForComplexNumbers(
    numberType: SuppliedType,
    numberRing: CommutativeRing<Number>,
    numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    numberLogarithmComputer: LogarithmComputer<Number>,
    numberPlanarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
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
    LogarithmComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(
            numberRing = numberRing,
            numberPositiveSquareRootComputer = numberPositiveSquareRootComputer,
            numberLogarithmComputer = numberLogarithmComputer,
            numberPlanarVectorArgumentComputer = numberPlanarVectorArgumentComputer,
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number> LogarithmComputer.Companion.setViaDefaultForComplexNumbers(
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
    LogarithmComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(numberType = numberType)
    }
}