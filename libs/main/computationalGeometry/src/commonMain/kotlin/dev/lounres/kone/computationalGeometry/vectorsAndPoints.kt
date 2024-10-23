/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.*
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList1
import kotlin.jvm.JvmName


// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Vector<out N>(public val coordinates: ColumnVector<N>) {
    override fun toString(): String = "Vector${coordinates.coefficients}"
}
//@JvmInline
public /*value*/ class Vector2<out N>(coordinates: ColumnVector<N>): Vector<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { "Cannot create a euclidean vector of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]

    override fun toString(): String = "Vector2${coordinates.coefficients}"
}

// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Point<out N>(public open val coordinates: ColumnVector<N>) {
    override fun toString(): String = "Point${coordinates.coefficients}"
}
//@JvmInline
public /*value*/ class Point2<out N>(coordinates: ColumnVector<N>): Point<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { "Cannot create a euclidean point of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]

    override fun toString(): String = "Point2${coordinates.coefficients}"
}

public fun <N> Vector(coordinates: MDList1<N>): Vector<N> = Vector(ColumnVector(coordinates))
public fun <N> Vector(vararg coordinates: N): Vector<N> = Vector(ColumnVector(*coordinates))
public fun <N> Vector(size: UInt, initializer: (coordinate: UInt) -> N): Vector<N> = Vector(ColumnVector(size, initializer))

public fun <N> Vector2(coordinates: MDList1<N>): Vector2<N> = Vector2(ColumnVector(coordinates))
public fun <N> Vector2(x: N, y: N): Vector2<N> = Vector2(ColumnVector(x, y))

public fun <N> Point(coordinates: MDList1<N>): Point<N> = Point(ColumnVector(coordinates))
public fun <N> Point(vararg coordinates: N): Point<N> = Point(ColumnVector(*coordinates))
public fun <N> Point(size: UInt, initializer: (coordinate: UInt) -> N): Point<N> = Point(ColumnVector(size, initializer))

public fun <N> Point2(coordinates: MDList1<N>): Point2<N> = Point2(ColumnVector(coordinates))
public fun <N> Point2(x: N, y: N): Point2<N> = Point2(ColumnVector(x, y))

context(EuclideanKategory<N>)
public operator fun <N> Vector2<N>.unaryPlus(): Vector2<N> = Vector2(((this as Vector<N>).unaryPlus()).coordinates)
context(EuclideanKategory<N>)
public operator fun <N> Vector2<N>.unaryMinus(): Vector2<N> = Vector2((this as Vector<N>).unaryMinus().coordinates)
context(EuclideanKategory<N>)
public operator fun <N> Vector2<N>.plus(other: Vector2<N>): Vector2<N> = Vector2((this as Vector<N>).plus(other as Vector<N>).coordinates)
context(EuclideanKategory<N>)
public operator fun <N> Vector2<N>.minus(other: Vector2<N>): Vector2<N> = Vector2((this as Vector<N>).minus(other as Vector<N>).coordinates)

context(EuclideanKategory<N>)
public operator fun <N> Vector2<N>.times(other: N): Vector2<N> = Vector2((this as Vector<N>).times(other).coordinates)
context(EuclideanKategory<N>)
public operator fun <N> N.times(other: Vector2<N>): Vector2<N> = Vector2(this.times(other as Vector<N>).coordinates)

context(EuclideanKategory<N>)
public operator fun <N> Point2<N>.plus(other: Vector2<N>): Point2<N> = Point2((this as Point<N>).plus(other as Vector<N>).coordinates)
context(EuclideanKategory<N>)
public operator fun <N> Vector2<N>.plus(other: Point2<N>): Point2<N> = Point2((this as Vector<N>).plus(other as Point<N>).coordinates)
context(EuclideanKategory<N>)
public operator fun <N> Point2<N>.minus(other: Point2<N>): Vector2<N> = Vector2((this as Point<N>).minus(other as Point<N>).coordinates)

context(Ring<N>)
public infix fun <N> Vector2<N>.cross(other: Vector2<N>): N = this.x * other.y - this.y * other.x
context(A, EuclideanKategory<N>)
@Suppress("LocalVariableName")
public fun <N, A> Point2<N>.inTriangle(P: Point2<N>, Q: Point2<N>, R: Point2<N>): Boolean where A: Ring<N>, A: Order<N> {
    val a = ((this - P) cross (Q - P)).sign
    val b = ((this - Q) cross (R - Q)).sign
    val c = ((this - R) cross (P - R)).sign
    return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0)
}

internal class PointEquality<N>(val columnVectorContext: Equality<ColumnVector<N>>) : Equality<Point<N>> {
    override fun Point<N>.equalsTo(other: Point<N>): Boolean = columnVectorContext { this.coordinates eq other.coordinates }
}

@JvmName("pointEqualityForColumnVector")
public fun <N> pointEquality(columnVectorContext: Equality<ColumnVector<N>>): Equality<Point<N>> =
    if (columnVectorContext is Hashing<ColumnVector<N>>) PointHashing(columnVectorContext)
    else PointEquality(columnVectorContext)

@JvmName("pointEqualityForNumber")
public fun <N> pointEquality(numberContext: Equality<N>): Equality<Point<N>> =
    if (numberContext is Hashing<N>) PointHashing(columnVectorHashing(numberContext))
    else PointEquality(columnVectorEquality(numberContext))

internal class PointHashing<N>(val columnVectorContext: Hashing<ColumnVector<N>>) : Hashing<Point<N>> {
    override fun Point<N>.equalsTo(other: Point<N>): Boolean = columnVectorContext { this.coordinates eq other.coordinates }
    override fun Point<N>.hash(): Int = columnVectorContext { this.coordinates.hash() }
}

@JvmName("pointHashingForColumnVector")
public fun <N> pointHashing(columnVectorContext: Hashing<ColumnVector<N>>): Hashing<Point<N>> =
    PointHashing(columnVectorContext)

@JvmName("pointHashingForNumber")
public fun <N> pointHashing(numberContext: Hashing<N>): Hashing<Point<N>> =
    PointHashing(columnVectorHashing(numberContext))

internal class VectorEquality<N>(val columnVectorContext: Equality<ColumnVector<N>>) : Equality<Vector<N>> {
    override fun Vector<N>.equalsTo(other: Vector<N>): Boolean = columnVectorContext { this.coordinates eq other.coordinates }
}

@JvmName("vectorEqualityForColumnVector")
public fun <N> vectorEquality(columnVectorContext: Equality<ColumnVector<N>>): Equality<Vector<N>> =
    if (columnVectorContext is Hashing<ColumnVector<N>>) VectorHashing(columnVectorContext)
    else VectorEquality(columnVectorContext)

@JvmName("vectorEqualityForNumber")
public fun <N> vectorEquality(numberContext: Equality<N>): Equality<Vector<N>> =
    if (numberContext is Hashing<N>) VectorHashing(columnVectorHashing(numberContext))
    else VectorEquality(columnVectorEquality(numberContext))

internal class VectorHashing<N>(val columnVectorContext: Hashing<ColumnVector<N>>) : Hashing<Vector<N>> {
    override fun Vector<N>.equalsTo(other: Vector<N>): Boolean = columnVectorContext { this.coordinates eq other.coordinates }
    override fun Vector<N>.hash(): Int = columnVectorContext { this.coordinates.hash() }
}

@JvmName("vectorHashingForColumnVector")
public fun <N> vectorHashing(columnVectorContext: Hashing<ColumnVector<N>>): Hashing<Vector<N>> =
    VectorHashing(columnVectorContext)

@JvmName("vectorHashingForNumber")
public fun <N> vectorHashing(numberContext: Hashing<N>): Hashing<Vector<N>> =
    VectorHashing(columnVectorHashing(numberContext))

//context(EuclideanSpace<N, A>)
//internal val <N, A> lexicographic2DComparator: Comparator<Point2<N>> where A: Ring<N>, A: Order<N>
//    get() = numberRing { compareByOrdered({ it.x }, { it.y }) }
//context(EuclideanSpace<N, A>)
//internal fun <N, A> lexicographicMDComparator(dim: UInt): Comparator<Point<N>> where A: Ring<N>, A: Order<N> =
//    numberRing { compareByOrdered(*Array(dim.toInt()) { { p -> p.coordinates[it.toUInt()] } }) }
//
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