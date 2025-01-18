/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.comparison.Comparator
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.compareByOrdered
import dev.lounres.kone.computationalGeometry.EuclideanKategory2
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Vector2
import dev.lounres.kone.computationalGeometry.comparison.pointEquality
import dev.lounres.kone.computationalGeometry.minus


context(_: Ring<N>)
public infix fun <N> Vector2<N>.cross(other: Vector2<N>): N = this.x * other.y - this.y * other.x

context(_: A, _: EuclideanKategory2<N>)
@Suppress("LocalVariableName")
public fun <N, A> Point2<N>.inTriangle(P: Point2<N>, Q: Point2<N>, R: Point2<N>): Boolean where A: Ring<N>, A: Order<N> {
    val a = ((this - P) cross (Q - P)).sign
    val b = ((this - Q) cross (R - Q)).sign
    val c = ((this - R) cross (P - R)).sign
    return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0)
}

context(_: A)
internal val <N, A> lexicographic2DComparator: Comparator<Point2<N>> where A: Order<N>
    get() = compareByOrdered({ it.x }, { it.y })

context(_: A)
internal fun <N, A> lexicographicMDComparator(dim: UInt): Comparator<Point<N>> where A: Order<N> =
    compareByOrdered(selectors = Array(dim.toInt()) { { p -> p.coordinates[it.toUInt()] } })

context(order: Order<N>)
internal val <N> lexicographic2DOrder: Order<Point2<N>>
    get() = Order(pointEquality(order), lexicographic2DComparator)

context(order: Order<N>)
internal fun <N> lexicographicMDOrder(dim: UInt): Order<Point<N>> =
    Order(pointEquality(order), lexicographicMDComparator(dim))

//context(EuclideanSpace<N, A>)
//internal fun <N, A> perpendicularTo(dimension: UInt, vectors: KoneArray<Vector<N>>, positiveDirection: Vector<N>): Vector<N> where A: Ring<N>, A: Order<N> {
//    require(dimension >= 1u && vectors.size + 1u == dimension && vectors.all { it.coordinates.size == dimension } && positiveDirection.coordinates.size == dimension)
//
//    val minor = numberRing { this@EuclideanSpace.vectorSpace { Matrix(dimension - 1u, dimension) { rowIndex, columnIndex -> vectors[rowIndex].coordinates[columnIndex] }.minor } }
//
//    val normalVector = Vector(dimension) { coordinate -> minor[KoneUIntArray(dimension - 1u) { it }, KoneUIntArray(dimension - 1u) { if (it < coordinate) it else it + 1u }].let { if (coordinate % 2u != 0u) numberRing { -it } else it } }
//
//    return (normalVector dot positiveDirection).let { if (numberRing { it.isPositive() }) normalVector else -normalVector }
//}
//context(EuclideanSpace<N, A>)
//internal fun <N, A> perpendicularTo(dimension: UInt, vararg vectors: Vector<N>, positiveDirection: Vector<N>): Vector<N> where A: Ring<N>, A: Order<N> = perpendicularTo(dimension, KoneArray(vectors), positiveDirection)