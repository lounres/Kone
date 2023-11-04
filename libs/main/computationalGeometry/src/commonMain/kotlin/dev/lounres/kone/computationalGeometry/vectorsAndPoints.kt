/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.linearAlgebra.experiment1.LinearSpace
import dev.lounres.kone.multidimensionalCollections.experiment1.MDFormation1
import dev.lounres.kone.order.Order
import dev.lounres.kone.order.compareByOrdered


// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Vector<out N>(public val coordinates: MDFormation1<N>)
//@JvmInline
public /*value*/ class Vector2<out N>(coordinates: MDFormation1<N>): Vector<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { /*TODO*/ }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
}

// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Point<out N>(public val coordinates: MDFormation1<N>)
//@JvmInline
public /*value*/ class Point2<out N>(coordinates: MDFormation1<N>): Point<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { /*TODO*/ }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
}

context(LinearSpace<N, *>)
public operator fun <N> Vector<N>.unaryMinus(): Vector<N> = Vector(-this.coordinates)
context(LinearSpace<N, *>)
public operator fun <N> Vector2<N>.unaryMinus(): Vector2<N> = Vector2(-this.coordinates)

context(LinearSpace<N, *>)
public operator fun <N> Vector<N>.plus(other: Vector<N>): Vector<N> = Vector(this.coordinates + other.coordinates)
context(LinearSpace<N, *>)
public operator fun <N> Vector2<N>.plus(other: Vector2<N>): Vector2<N> = Vector2(this.coordinates + other.coordinates)

context(LinearSpace<N, *>)
public operator fun <N> Vector<N>.minus(other: Vector<N>): Vector<N> = Vector(this.coordinates - other.coordinates)
context(LinearSpace<N, *>)
public operator fun <N> Vector2<N>.minus(other: Vector2<N>): Vector2<N> = Vector2(this.coordinates - other.coordinates)

context(LinearSpace<N, *>)
public operator fun <N> Vector<N>.times(other: N): Vector<N> = Vector(this.coordinates * other)
context(LinearSpace<N, *>)
public operator fun <N> Vector2<N>.times(other: N): Vector2<N> = Vector2(this.coordinates * other)

context(LinearSpace<N, *>)
public operator fun <N> Point<N>.plus(other: Vector<N>): Point<N> = Point(this.coordinates + other.coordinates)
context(LinearSpace<N, *>)
public operator fun <N> Point2<N>.plus(other: Vector2<N>): Point2<N> = Point2(this.coordinates + other.coordinates)

context(LinearSpace<N, *>)
public operator fun <N> Point<N>.minus(other: Point<N>): Vector<N> = Vector(this.coordinates - other.coordinates)
context(LinearSpace<N, *>)
public operator fun <N> Point2<N>.minus(other: Point2<N>): Vector2<N> = Vector2(this.coordinates - other.coordinates)

context(Ring<N>)
public infix fun <N> Vector2<N>.cross(other: Vector2<N>): N = (this.x * other.y - this.y * other.x)
context(Ring<N>, Order<N>, LinearSpace<N, *>)
@Suppress("LocalVariableName")
public fun <N> Point2<N>.inTriangle(A: Point2<N>, B: Point2<N>, C: Point2<N>): Boolean {
    val a = ((this - A) cross (B - A)).sign
    val b = ((this - B) cross (C - B)).sign
    val c = ((this - C) cross (A - C)).sign
    return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0)
}

context(Order<N>)
internal val <N> lexicographic2DComparator: Comparator<Point2<N>>
    get() = compareByOrdered({ it.x }, { it.y })

context(Order<N>)
internal fun <N> lexicographicMDComparator(dim: UInt): Comparator<Point<N>> =
    compareByOrdered(*Array(dim.toInt()) { { p -> p.coordinates[it.toUInt()] } })