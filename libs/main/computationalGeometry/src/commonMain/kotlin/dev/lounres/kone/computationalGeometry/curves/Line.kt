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
public open /*value*/ class Line<N>(
    public open val start: Point<N>,
    public open val direction: Vector<N>
) {
    init {
        require(start.coordinates.size == direction.coordinates.size) { "Start and end point must have the same dimension" }
    }
    
    override fun toString() : String = "Line(${start.coordinates}, ${direction.coordinates})"
}
//@JvmInline
public /*value*/ class Line2<N>(
    public override val start: Point2<N>,
    public override val direction: Vector2<N>
) : Line<N>(start, direction) {
    override fun toString() : String = "Line2(${start.coordinates}, ${direction.coordinates})"
}

context(_: EuclideanKategory<N>)
public fun <N> Line(start: Point<N>, end: Point<N>): Line<N> = Line(start = start, direction = end - start)
context(_: EuclideanKategory2<N>)
public fun <N> Line2(start: Point2<N>, end: Point2<N>): Line2<N> = Line2(start = start, direction = end - start)