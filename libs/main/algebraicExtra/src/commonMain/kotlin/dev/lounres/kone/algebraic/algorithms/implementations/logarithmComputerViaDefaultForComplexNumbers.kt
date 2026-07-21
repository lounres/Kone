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
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class LogarithmComputerViaDefaultForComplexNumbers<Number>(
    private val numberRing: CommutativeRing<Number>,
    private val numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val numberLogarithmComputer: LogarithmComputer<Number>,
    private val numberPlanarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
) : LogarithmComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.logarithm(): ComplexNumber<Number> {
        KoneContext.unwrap(
            numberRing,
            numberPositiveSquareRootComputer,
            numberLogarithmComputer,
            numberPlanarVectorArgumentComputer,
        )
        return ComplexNumber(
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

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> LogarithmComputer.Companion.viaDefaultForComplexNumbers(): LogarithmComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefaultForComplexNumbers(
        numberRing = koneContextRegistry[CommutativeRing.Key<Number>()],
        numberPositiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>()],
        numberLogarithmComputer = koneContextRegistry[LogarithmComputer.Key<Number>()],
        numberPlanarVectorArgumentComputer = koneContextRegistry[PlanarVectorArgumentComputer.Key<Number>()],
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> LogarithmComputer.Companion.setViaDefaultForComplexNumbers(
    numberRing: CommutativeRing<Number>,
    numberPositiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    numberLogarithmComputer: LogarithmComputer<Number>,
    numberPlanarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>,
) {
    LogarithmComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(
            numberRing = numberRing,
            numberPositiveSquareRootComputer = numberPositiveSquareRootComputer,
            numberLogarithmComputer = numberLogarithmComputer,
            numberPlanarVectorArgumentComputer = numberPlanarVectorArgumentComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <@Supply Number> LogarithmComputer.Companion.setViaDefaultForComplexNumbers() {
    LogarithmComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>()
    }
}