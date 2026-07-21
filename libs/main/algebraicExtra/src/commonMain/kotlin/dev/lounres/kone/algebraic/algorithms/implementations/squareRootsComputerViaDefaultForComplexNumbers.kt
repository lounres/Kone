/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.SquareRootsComputer
import dev.lounres.kone.algebraic.algorithms.positiveSquareRoot
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.localContexts
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


private class SquareRootsComputerViaDefaultForComplexNumbers<Number>(
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) : SquareRootsComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.squareRoots(): KoneList<ComplexNumber<Number>> {
        KoneContext.unwrap(field, order, positiveSquareRootComputer)
        localContexts(field.numberDivideInt)
        val absoluteValue = this@squareRoots.absoluteValue()
        if (absoluteValue.isZero()) return KoneList.of(ComplexNumber(field.zero, field.zero))
        val cosWhole = this@squareRoots.realPart
        val sinWhole = this@squareRoots.imaginaryPart
        val cosHalf: Number
        val sinHalf: Number
        if (cosWhole.isNonNegative()) {
            cosHalf = ((absoluteValue + cosWhole) / 2).positiveSquareRoot()
            sinHalf = sinWhole / 2 / cosHalf
        } else {
            sinHalf = ((absoluteValue - cosWhole) / 2).positiveSquareRoot()
            cosHalf = sinWhole / 2 / sinHalf
        }
        return KoneList.of(
            ComplexNumber(cosHalf, sinHalf),
            ComplexNumber(-cosHalf, -sinHalf)
        )
    }
}

public fun <Number> SquareRootsComputer.Companion.viaDefaultForComplexNumbers(
    field: Field<Number>,
    order: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
): SquareRootsComputer<ComplexNumber<Number>> = SquareRootsComputerViaDefaultForComplexNumbers(
    field = field,
    order = order,
    positiveSquareRootComputer = positiveSquareRootComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> SquareRootsComputer.Companion.viaDefaultForComplexNumbers(): SquareRootsComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefaultForComplexNumbers(
        field = koneContextRegistry[Field.Key<Number>()], // TODO: Replace with 'getOrElse(key) { error("${requester()} requested absent key $key") }'
        order = koneContextRegistry[Order.Key<Number>()], // TODO: Replace with 'getOrElse(key) { error("${requester()} requested absent key $key") }'
        positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>()], // TODO: Replace with 'getOrElse(key) { error("${requester()} requested absent key $key") }'
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> SquareRootsComputer.Companion.setViaDefaultForComplexNumbers(
    field: Field<Number>,
    order: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) {
    SquareRootsComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers(
            field = field,
            order = order,
            positiveSquareRootComputer = positiveSquareRootComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> SquareRootsComputer.Companion.setViaDefaultForComplexNumbers() {
    SquareRootsComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>()
    }
}