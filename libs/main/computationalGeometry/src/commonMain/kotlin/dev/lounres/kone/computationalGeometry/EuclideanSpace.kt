/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry


// The underlying ring is ordered (thus, is a subring of real numbers and is an integral domain)
public interface EuclideanSpaceOverRing<Number, Vector, Point> : AffineSpaceOverRing<Number, Vector, Point> {
    public infix fun Vector.dot(other: Vector): Number
}

public interface EuclideanSpaceOverField<Number, Vector, Point> : EuclideanSpaceOverRing<Number, Vector, Point>, AffineSpaceOverField<Number, Vector, Point>