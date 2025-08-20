/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.context
import dev.lounres.kone.algebraic.set
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.computationalGeometry.VectorWrapper
import dev.lounres.kone.computationalGeometry.setMDList1For
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.build
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


@PublishedApi
internal val doubleSuppliedType: SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Double",
        typeArguments = emptyList(),
        isNullable = false,
    )

@PublishedApi
internal val doubleMDList1SuppliedType: SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList1",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = doubleSuppliedType,
            )
        ),
        isNullable = false,
    )

@PublishedApi
internal val doubleMDList1VectorWrapperSuppliedType: SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.VectorWrapper",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = doubleMDList1SuppliedType,
            )
        ),
        isNullable = false,
    )

@PublishedApi
internal val doubleMDList1PointWrapperSuppliedType: SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.PointWrapper",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = doubleMDList1SuppliedType,
            )
        ),
        isNullable = false,
    )

@PublishedApi
internal val koneCanvasContextRegistry: KoneContextRegistry = KoneContextRegistry.build {
    Double.context.set()
    EuclideanSpaceOverField.setMDList1For<Double>(doubleSuppliedType, 2u)
}

public inline fun <Result> inKoneCanvasEuclideanSpace(block: context(Field<Double>, EuclideanSpaceOverField<Double, VectorWrapper<MDList1<Double>>, PointWrapper<MDList1<Double>>>) () -> Result): Result =
    block(
        koneCanvasContextRegistry[Field.Key<Double>(doubleSuppliedType)],
        koneCanvasContextRegistry[EuclideanSpaceOverField.Key<Double, VectorWrapper<MDList1<Double>>, PointWrapper<MDList1<Double>>>(doubleSuppliedType, doubleMDList1VectorWrapperSuppliedType, doubleMDList1PointWrapperSuppliedType)]
    )