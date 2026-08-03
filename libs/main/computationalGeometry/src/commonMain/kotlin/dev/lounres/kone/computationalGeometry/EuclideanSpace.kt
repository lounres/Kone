/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


// The underlying ring is ordered (thus, is a subring of real numbers and is an integral domain)
@GenerateKoneContextKey
public interface EuclideanSpaceOverRing<Number, Vector, Point> : AffineSpaceOverRing<Number, Vector, Point>, EuclideanVectorSpaceOverRing<Number, Vector> {
    public companion object;
}

context(euclideanSpace: EuclideanSpaceOverRing<Number, Vector, Point>)
public fun <Number, Vector, Point> distanceSquaredBetween(point1: Point, point2: Point): Number {
    KoneContext.localUnwrap(euclideanSpace)
    return (point1 - point2).lengthSquared()
}

@GenerateKoneContextKey
public interface EuclideanSpaceOverField<Number, Vector, Point> : EuclideanSpaceOverRing<Number, Vector, Point>, AffineSpaceOverField<Number, Vector, Point>, EuclideanVectorSpaceOverField<Number, Vector> {
    public companion object;
}