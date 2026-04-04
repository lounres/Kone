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
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


private class SquareRootsComputerViaDefaultForComplexNumbers<Number>(
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) : SquareRootsComputer<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.squareRoots(): KoneList<ComplexNumber<Number>> =
        context(
            field,
            order,
            positiveSquareRootComputer,
        ) {
            val absoluteValue = this.absoluteValue()
            if (absoluteValue.isZero()) return KoneList.of(ComplexNumber(field.zero, field.zero))
            val cosWhole = this.realPart
            val sinWhole = this.imaginaryPart
            val cosHalf: Number
            val sinHalf: Number
            if (cosWhole.isNonNegative()) {
                cosHalf = ((absoluteValue + cosWhole) / 2).positiveSquareRoot()
                sinHalf = sinWhole / 2 / cosHalf
            } else {
                sinHalf = ((absoluteValue - cosWhole) / 2).positiveSquareRoot()
                cosHalf = sinWhole / 2 / sinHalf
            }
            KoneList.of(
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

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> SquareRootsComputer.Companion.viaDefaultForComplexNumbers(
    numberType: SuppliedType,
): SquareRootsComputer<ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefaultForComplexNumbers(
        field = koneContextRegistry[Field.Key<Number>(numberType = numberType)], // TODO: Replace with 'getOrElse(key) { error("${requester()} requested absent key $key") }'
        order = koneContextRegistry[Order.Key<Number>(elementType = numberType)], // TODO: Replace with 'getOrElse(key) { error("${requester()} requested absent key $key") }'
        positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)], // TODO: Replace with 'getOrElse(key) { error("${requester()} requested absent key $key") }'
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number> SquareRootsComputer.Companion.setViaDefaultForComplexNumbers(
    numberType: SuppliedType,
    field: Field<Number>,
    order: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
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
    SquareRootsComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers(
            field = field,
            order = order,
            positiveSquareRootComputer = positiveSquareRootComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> SquareRootsComputer.Companion.setViaDefaultForComplexNumbers(
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
    SquareRootsComputer.Key<ComplexNumber<Number>>(numberType = complexNumberType) correspondsTo RegisteredValueProvider.cached {
        viaDefaultForComplexNumbers<Number>(numberType = numberType)
    }
}