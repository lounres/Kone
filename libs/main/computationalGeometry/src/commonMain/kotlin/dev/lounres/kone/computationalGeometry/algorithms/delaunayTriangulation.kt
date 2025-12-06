/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.basis.ModuleBasis
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


public fun interface DelaunayTriangulationOverRingComputer<in Number, Vector, in Point> : KoneContext {
    public fun KoneIterable<Point>.delaunayTriangulation(
        basis: ModuleBasis.Finite<Number, Vector>,
    ): PolytopicConstruction
    
    public companion object;
    
    public class Key<Number, Vector, Point>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
        public val pointType: SuppliedType,
    ) : RegistryKey<DelaunayTriangulationOverRingComputer<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.algorithms.DelaunayTriangulationOverRingComputer",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = IN,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                    SuppliedProjection.Regular(
                        variance = IN,
                        type = pointType
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.algorithms.DelaunayTriangulationOverRingComputer.Key<$numberType, $vectorType, $pointType>"
    }
}

context(delaunayTriangulationComputer: DelaunayTriangulationOverRingComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneIterable<Point>.delaunayTriangulation(
    basis: ModuleBasis.Finite<Number, Vector>,
): PolytopicConstruction = with(delaunayTriangulationComputer) {
    this@delaunayTriangulation.delaunayTriangulation(basis)
}

context(_: Ring<Number>, _: EuclideanSpaceOverRing<Number, Vector, Point>, delaunayTriangulationComputer: DelaunayTriangulationOverRingComputer<Number, Vector, Point>)
public fun <Number, Vector, Point> KoneIterable<Point>.delaunayTriangulation(): PolytopicConstruction =
    delaunayTriangulation(pointsetBasis())