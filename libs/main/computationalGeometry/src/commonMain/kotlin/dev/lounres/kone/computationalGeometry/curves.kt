/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry


//@JvmInline
public open /*value*/ class Segment<N>(start: Point<N>, direction: Vector<N>) {
    init {
        require(start.coordinates.size == direction.coordinates.size) { "Start and end point must have the same dimension" }
    }
    public open val start: Point<N> = start
    public open val direction: Vector<N> = direction
    override fun toString() : String = "Segment(${start}, ${direction})"
}
//@JvmInline
public /*value*/ class Segment2<N>(public override val start: Point2<N>, public override val direction: Vector2<N>) : Segment<N>(start, direction) {
    override fun toString() : String = "Segment2(${start}, ${direction})"
}

context(EuclideanKategory<N>)
public val <N> Segment<N>.end: Point<N> get() = this.start + this.direction
context(EuclideanKategory<N>)
public val <N> Segment2<N>.end: Point2<N> get() = this.start + this.direction

context(EuclideanKategory<N>)
public fun <N> Segment(start: Point<N>, end: Point<N>): Segment<N> = Segment(start = start, direction = end - start)
context(EuclideanKategory<N>)
public fun <N> Segment2(start: Point2<N>, end: Point2<N>): Segment2<N> = Segment2(start = start, direction = end - start)

//@JvmInline
public open /*value*/ class Ray<N>(start: Point<N>, direction: Vector<N>) {
    init {
        require(start.coordinates.size == direction.coordinates.size) { "Start and end point must have the same dimension" }
    }
    public open val start: Point<N> = start
    public open val direction: Vector<N> = direction
    override fun toString() : String = "Ray(${start.coordinates}, ${direction.coordinates})"
}
//@JvmInline
public /*value*/ class Ray2<N>(public override val start: Point2<N>, public override val direction: Vector2<N>) : Ray<N>(start, direction) {
    override fun toString() : String = "Ray2(${start.coordinates}, ${direction.coordinates})"
}

context(EuclideanKategory<N>)
public fun <N> Ray(start: Point<N>, end: Point<N>): Ray<N> = Ray(start = start, direction = end - start)
context(EuclideanKategory<N>)
public fun <N> Ray2(start: Point2<N>, end: Point2<N>): Ray2<N> = Ray2(start = start, direction = end - start)

//@JvmInline
public open /*value*/ class Line<N>(start: Point<N>, direction: Vector<N>) {
    init {
        require(start.coordinates.size == direction.coordinates.size) { "Start and end point must have the same dimension" }
    }
    public open val start: Point<N> = start
    public open val direction: Vector<N> = direction
    override fun toString() : String = "Line(${start.coordinates}, ${direction.coordinates})"
}
//@JvmInline
public /*value*/ class Line2<N>(public override val start: Point2<N>, public override val direction: Vector2<N>) : Line<N>(start, direction) {
    override fun toString() : String = "Line2(${start.coordinates}, ${direction.coordinates})"
}

context(EuclideanKategory<N>)
public fun <N> Line(start: Point<N>, end: Point<N>): Line<N> = Line(start = start, direction = end - start)
context(EuclideanKategory<N>)
public fun <N> Line2(start: Point2<N>, end: Point2<N>): Line2<N> = Line2(start = start, direction = end - start)