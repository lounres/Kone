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
import dev.lounres.kone.computationalGeometry.plus


//@JvmInline
public open /*value*/ class Segment<N>(
    public open val start: Point<N>,
    public open val direction: Vector<N>
) {
    init {
        require(start.coordinates.size == direction.coordinates.size) { "Start and end point must have the same dimension" }
    }
    
    override fun toString() : String = "Segment(${start.coordinates.coefficients}, ${direction.coordinates.coefficients})"
}
//@JvmInline
public /*value*/ class Segment2<N>(
    public override val start: Point2<N>,
    public override val direction: Vector2<N>
) : Segment<N>(start, direction) {
    override fun toString() : String = "Segment2(${start.coordinates.coefficients}, ${direction.coordinates.coefficients})"
}

context(_: EuclideanKategory<N>)
public val <N> Segment<N>.end: Point<N>
    get() = this.start + this.direction
context(_: EuclideanKategory2<N>)
public val <N> Segment2<N>.end: Point2<N>
    get() = this.start + this.direction

context(_: EuclideanKategory<N>)
public fun <N> Segment(start: Point<N>, end: Point<N>): Segment<N> = Segment(start = start, direction = end - start)
context(_: EuclideanKategory2<N>)
public fun <N> Segment2(start: Point2<N>, end: Point2<N>): Segment2<N> = Segment2(start = start, direction = end - start)
