/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


// The underlying ring is ordered (thus, is a subring of real numbers and is an integral domain)
public interface EuclideanSpaceOverRing<Number, Vector, Point> : AffineSpaceOverRing<Number, Vector, Point>, EuclideanVectorSpaceOverRing<Number, Vector> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector, @Supply Point> : SuppliedTypeRegistryKey<EuclideanSpaceOverRing<Number, Vector, Point>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpaceOverRing<Number, Vector, Point>> by lazy {
            ImpliedKeysRegistry {
                EuclideanVectorSpaceOverRing.Key<Number, Vector>().impliesSame()
                AffineSpaceOverRing.Key<Number, Vector, Point>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}, ${suppliedTypeOf<Point>()}>"
    }
}

context(euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>)
public fun <Number, Vector, Point> distanceSquaredBetween(point1: Point, point2: Point): Number {
    KoneContext.localUnwrap(euclideanSpace)
    return (point1 - point2).lengthSquared()
}

public interface EuclideanSpaceOverField<Number, Vector, Point> : EuclideanSpaceOverRing<Number, Vector, Point>, AffineSpaceOverField<Number, Vector, Point>, EuclideanVectorSpaceOverField<Number, Vector> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector, @Supply Point> : SuppliedTypeRegistryKey<EuclideanSpaceOverField<Number, Vector, Point>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpaceOverField<Number, Vector, Point>> by lazy {
            ImpliedKeysRegistry {
                EuclideanVectorSpaceOverField.Key<Number, Vector>().impliesSame()
                AffineSpaceOverField.Key<Number, Vector, Point>().impliesSame()
                EuclideanSpaceOverRing.Key<Number, Vector, Point>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}, ${suppliedTypeOf<Point>()}>"
    }
}