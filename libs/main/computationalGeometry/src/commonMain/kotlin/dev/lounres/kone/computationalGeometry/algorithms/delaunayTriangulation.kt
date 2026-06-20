/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.algorithms

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.basis.ModuleBasis
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public fun interface DelaunayTriangulationOverRingComputer<in Number, Vector, in Point> : KoneContext {
    public fun KoneIterable<Point>.delaunayTriangulation(
        basis: ModuleBasis.Finite<Number, Vector>,
    ): PolytopicConstruction
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector, @Supply Point> : SuppliedTypeRegistryKey<DelaunayTriangulationOverRingComputer<Number, Vector, Point>>() {
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.algorithms.DelaunayTriangulationOverRingComputer.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}, ${suppliedTypeOf<Point>()}>"
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