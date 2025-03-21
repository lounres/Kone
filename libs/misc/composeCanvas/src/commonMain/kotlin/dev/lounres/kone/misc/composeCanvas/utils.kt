/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.algebraic.installFloatContext
import dev.lounres.kone.computationalGeometry.installEuclideanKategory2For
import dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstructionPolytope
import dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstructionVertex
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.linearAlgebra.installVectorKategoryFor
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public val floatSuppliedType: SuppliedType<Float> =
    SuppliedType.Regular<Float>(
        kClass = Float::class,
        typeArguments = emptyList(),
        isNullable = false,
    )

public val abstractPolytopicConstructionPolytopeSuppliedType: SuppliedType<AbstractPolytopicConstructionPolytope<Float>> =
    SuppliedType.Regular<AbstractPolytopicConstructionPolytope<Float>>(
        kClass = AbstractPolytopicConstructionPolytope::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = floatSuppliedType,
            )
        ),
        isNullable = false,
    )

public val abstractPolytopicConstructionVertexSuppliedType: SuppliedType<AbstractPolytopicConstructionVertex<Float>> =
    SuppliedType.Regular<AbstractPolytopicConstructionVertex<Float>>(
        kClass = AbstractPolytopicConstructionVertex::class,
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = KVariance.INVARIANT,
                type = floatSuppliedType,
            )
        ),
        isNullable = false,
    )

public val koneCanvasContextRegistry: KoneContextRegistry = KoneContextRegistry {
    installFloatContext()
    installVectorKategoryFor(floatSuppliedType)
    installEuclideanKategory2For(floatSuppliedType)
}