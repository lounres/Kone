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
import dev.lounres.kone.multidimensionalCollections.MDList1


//@JvmInline
public open /*value*/ class Segment<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>>(
    public open val start: Point<N, PointContent>,
    public open val direction: Vector<N, VectorContent>
) {
    init {
        require(start.coordinates.size == direction.coordinates.size) { "Start and end point must have the same dimension" }
    }
    
    override fun toString() : String = "Segment(${start.coordinates}, ${direction.coordinates})"
}
//@JvmInline
public /*value*/ class Segment2<out N, out VectorContent: MDList1<N>, out PointContent: MDList1<N>>(
    public override val start: Point2<N, PointContent>,
    public override val direction: Vector2<N, VectorContent>
) : Segment<N, VectorContent, PointContent>(start, direction) {
    override fun toString() : String = "Segment2(${start.coordinates}, ${direction.coordinates})"
}

context(_: EuclideanKategory<N, VectorContent, PointContent>)
public val <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment<N, VectorContent, PointContent>.end: Point<N, PointContent>
    get() = this.start + this.direction
context(_: EuclideanKategory2<N, VectorContent, PointContent>)
public val <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment2<N, VectorContent, PointContent>.end: Point2<N, PointContent>
    get() = this.start + this.direction

context(_: EuclideanKategory<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment(start: Point<N, PointContent>, end: Point<N, PointContent>): Segment<N, VectorContent, PointContent> = Segment(start = start, direction = end - start)
context(_: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Segment2(start: Point2<N, PointContent>, end: Point2<N, PointContent>): Segment2<N, VectorContent, PointContent> = Segment2(start = start, direction = end - start)
