/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.contexts.invoke


public interface AffineSpaceOverRing<Number, Vector, Point> : Module<Number, Vector> {
    public operator fun Point.plus(other: Vector): Point
    public operator fun Point.minus(other: Vector): Point
    public operator fun Vector.plus(other: Point): Point
    public operator fun Point.minus(other: Point): Vector
}

context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.plus(other: Vector): Point = with(affineSpace) { this@plus + other }

// FIXME: KT-79139
//context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
//public operator fun <Vector, Point> Point.minus(other: Vector): Point = with(affineSpace) { this@minus - other }

// FIXME: KT-79139
//context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
//public operator fun <Vector, Point> Vector.plus(other: Point): Point = with(affineSpace) { this@plus + other }

context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.minus(other: Point): Vector = with(affineSpace) { this@minus - other }

public interface AffineSpaceOverField<Number, Vector, Point> : VectorSpace<Number, Vector>, AffineSpaceOverRing<Number, Vector, Point> {
    public companion object
}

private class AffineSpaceOverFieldViaVectorSpace<Number, Vector>(
    private val vectorSpace: VectorSpace<Number, Vector>,
) : VectorSpace<Number, Vector> by vectorSpace, AffineSpaceOverField<Number, Vector, VectorSpacePoint<Vector>> {
    override fun VectorSpacePoint<Vector>.plus(other: Vector): VectorSpacePoint<Vector> =
        vectorSpace { VectorSpacePoint(this.vector + other) }
    
    override fun VectorSpacePoint<Vector>.minus(other: Vector): VectorSpacePoint<Vector> =
        vectorSpace { VectorSpacePoint(this.vector - other) }
    
    override fun Vector.plus(other: VectorSpacePoint<Vector>): VectorSpacePoint<Vector> =
        vectorSpace { VectorSpacePoint(this + other.vector) }
    
    override fun VectorSpacePoint<Vector>.minus(other: VectorSpacePoint<Vector>): Vector =
        vectorSpace { this.vector - other.vector }
}

public fun <Number, Vector> AffineSpaceOverField.Companion.viaVectorSpace(vectorSpace: VectorSpace<Number, Vector>): AffineSpaceOverField<Number, Vector, VectorSpacePoint<Vector>> =
    AffineSpaceOverFieldViaVectorSpace(vectorSpace)