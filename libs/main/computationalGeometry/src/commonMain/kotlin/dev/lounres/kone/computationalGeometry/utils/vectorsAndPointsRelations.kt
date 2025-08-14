/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.utils

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.computationalGeometry.*
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.ComparisonResult.LeftIsGreaterThanRight
import dev.lounres.kone.relations.ComparisonResult.LeftIsLessThanRight
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareByOrdered
import dev.lounres.kone.relations.compareWith


context(_: Ring<N>)
public infix fun <N> Vector2<N, *>.cross(other: Vector2<N, *>): N = this.x * other.y - this.y * other.x

@Suppress("LocalVariableName")
context(_: Field<N>, _: Order<N>, _: EuclideanKategory2<N, VectorContent, PointContent>)
public fun <N, VectorContent: MDList1<N>, PointContent: MDList1<N>> Point2<N, PointContent>.inTriangle(P: Point2<N, PointContent>, Q: Point2<N, PointContent>, R: Point2<N, PointContent>): Boolean {
    val a = ((this - P) cross (Q - P)) compareWith zero
    val b = ((this - Q) cross (R - Q)) compareWith zero
    val c = ((this - R) cross (P - R)) compareWith zero
    return (a != LeftIsLessThanRight && b != LeftIsLessThanRight && c != LeftIsLessThanRight)
            || (a != LeftIsGreaterThanRight && b != LeftIsGreaterThanRight && c != LeftIsGreaterThanRight)
}

context(_: Order<N>)
internal val <N> lexicographic2DComparator: Comparator<Point2<N, *>>
    get() = compareByOrdered({ it.x }, { it.y })

context(_: Order<N>)
internal fun <N> lexicographicMDComparator(dim: UInt): Comparator<Point<N, *>> =
    compareByOrdered(selectors = Array(dim.toInt()) { { p -> p.coordinates[it.toUInt()] } })

context(order: Order<N>)
internal val <N> lexicographic2DOrder: Order<Point2<N, *>>
    get() = Order(lexicographic2DComparator)

context(order: Order<N>)
internal fun <N> lexicographicMDOrder(dim: UInt): Order<Point<N, *>> =
    Order(lexicographicMDComparator(dim))

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