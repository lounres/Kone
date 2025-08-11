/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.algebraic.setDoubleContext
import dev.lounres.kone.computationalGeometry.setEuclideanKategory2For
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.linearAlgebra.setDefaultVectorKategoryFor
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public val doubleSuppliedType: SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Double",
        typeArguments = emptyList(),
        isNullable = false,
    )

public val abstractPolytopicConstructionPolytopeSuppliedType: SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstructionPolytope",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = doubleSuppliedType,
            )
        ),
        isNullable = false,
    )

public val abstractPolytopicConstructionVertexSuppliedType: SuppliedType =
    @OptIn(DelicateSuppliedTypeConstructor::class)
    SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstructionVertex",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = doubleSuppliedType,
            )
        ),
        isNullable = false,
    )

public val koneCanvasContextRegistry: KoneContextRegistry = KoneContextRegistry {
    setDoubleContext()
    setDefaultVectorKategoryFor<Double>(doubleSuppliedType)
    setEuclideanKategory2For<Double>(doubleSuppliedType)
}