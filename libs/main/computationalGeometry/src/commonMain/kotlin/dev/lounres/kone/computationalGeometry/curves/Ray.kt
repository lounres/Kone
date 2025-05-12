/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.curves

import dev.lounres.kone.computationalGeometry.EuclideanKategory
import dev.lounres.kone.computationalGeometry.EuclideanKategory2
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.computationalGeometry.Vector2
import dev.lounres.kone.computationalGeometry.minus


//@JvmInline
public open /*value*/ class Ray<N>(
    public open val start: Point<N>,
    public open val direction: Vector<N>
) {
    init {
        require(start.coordinates.size == direction.coordinates.size) { "Start and end point must have the same dimension" }
    }
    
    override fun toString() : String = "Ray(${start.coordinates.coefficients}, ${direction.coordinates.coefficients})"
}
//@JvmInline
public /*value*/ class Ray2<N>(
    public override val start: Point2<N>,
    public override val direction: Vector2<N>
) : Ray<N>(start, direction) {
    override fun toString() : String = "Ray2(${start.coordinates.coefficients}, ${direction.coordinates.coefficients})"
}

context(_: EuclideanKategory<N>)
public fun <N> Ray(start: Point<N>, end: Point<N>): Ray<N> = Ray(start = start, direction = end - start)
context(_: EuclideanKategory2<N>)
public fun <N> Ray2(start: Point2<N>, end: Point2<N>): Ray2<N> = Ray2(start = start, direction = end - start)