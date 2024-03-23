/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.collections.common.KoneArray
import dev.lounres.kone.collections.common.KoneUIntArray
import dev.lounres.kone.collections.common.utils.all
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.compareByOrdered
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.ColumnVector
import dev.lounres.kone.linearAlgebra.experiment1.columnVectorEquality
import dev.lounres.kone.linearAlgebra.experiment1.columnVectorHashing
import dev.lounres.kone.linearAlgebra.experiment1.minor
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList1


// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Vector<out N, in A: Equality<@UnsafeVariance N>>(public val coordinates: ColumnVector<N, A>) {
    override fun toString(): String = "Vector(${coordinates.coefficients})"
}
//@JvmInline
public /*value*/ class Vector2<out N, in A: Equality<@UnsafeVariance N>>(coordinates: ColumnVector<N, A>): Vector<N, A>(coordinates) {
    init {
        require(coordinates.size == 2u) { "Cannot create a euclidean vector of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]

    override fun toString(): String = "Vector2(${coordinates.coefficients})"
}

// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Point<N, in A: Equality<N>>(public open val coordinates: ColumnVector<N, A>) {
    override fun toString(): String = "Point(${coordinates.coefficients})"
}
//@JvmInline
public /*value*/ class Point2<N, in A: Equality<N>>(coordinates: ColumnVector<N, A>): Point<N, A>(coordinates) {
    init {
        require(coordinates.size == 2u) { "Cannot create a euclidean point of dimension 2 from column vector of size ${coordinates.size}" }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]

    override fun toString(): String = "Point2(${coordinates.coefficients})"
}

public fun <N, NE: Equality<N>> Vector(coordinates: ContextualMDList1<N, NE>): Vector<N, NE> = Vector(ColumnVector(coordinates))
public fun <N> Vector(vararg coordinates: N): Vector<N, Equality<N>> = Vector(ColumnVector(*coordinates))
public fun <N> Vector(size: UInt, initializer: (coordinate: UInt) -> N): Vector<N, Equality<N>> = Vector(ColumnVector(size, initializer))

public fun <N, NE: Equality<N>> Point(coordinates: ContextualMDList1<N, NE>): Point<N, NE> = Point(ColumnVector(coordinates))
public fun <N> Point(vararg coordinates: N): Point<N, Equality<N>> = Point(ColumnVector(*coordinates))
public fun <N> Point(size: UInt, initializer: (coordinate: UInt) -> N): Point<N, Equality<N>> = Point(ColumnVector(size, initializer))

context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector<N, A>.unaryMinus(): Vector<N, A> where A: Ring<N>, A: Order<N> = Vector(vectorSpace { -this.coordinates })
context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector2<N, A>.unaryMinus(): Vector2<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector2(-this.coordinates) }

context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector<N, A>.plus(other: Vector<N, A>): Vector<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector(this.coordinates + other.coordinates) }
context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector2<N, A>.plus(other: Vector2<N, A>): Vector2<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector2(this.coordinates + other.coordinates) }

context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector<N, A>.minus(other: Vector<N, A>): Vector<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector(this.coordinates - other.coordinates) }
context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector2<N, A>.minus(other: Vector2<N, A>): Vector2<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector2(this.coordinates - other.coordinates) }

context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector<N, A>.times(other: N): Vector<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector(this.coordinates * other) }
context(EuclideanSpace<N, A>)
public operator fun <N, A> Vector2<N, A>.times(other: N): Vector2<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector2(this.coordinates * other) }

context(EuclideanSpace<N, A>)
public operator fun <N, A> Point<N, A>.plus(other: Vector<N, A>): Point<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Point(this.coordinates + other.coordinates) }
context(EuclideanSpace<N, A>)
public operator fun <N, A> Point2<N, A>.plus(other: Vector2<N, A>): Point2<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Point2(this.coordinates + other.coordinates) }

context(EuclideanSpace<N, A>)
public operator fun <N, A> Point<N, A>.minus(other: Point<N, A>): Vector<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector(this.coordinates - other.coordinates) }
context(EuclideanSpace<N, A>)
public operator fun <N, A> Point2<N, A>.minus(other: Point2<N, A>): Vector2<N, A> where A: Ring<N>, A: Order<N> = vectorSpace { Vector2(this.coordinates - other.coordinates) }

context(EuclideanSpace<N, A>)
public infix fun <N, A> Vector<N, A>.dot(other: Vector<N, A>): N where A: Ring<N>, A: Order<N> = numberRing {
    require(this.coordinates.size == other.coordinates.size)
    return (0u ..< this.coordinates.size).fold(zero) { acc, index -> acc + this.coordinates[index] * other.coordinates[index] }
}

context(EuclideanSpace<N, A>)
public infix fun <N, A> Vector2<N, A>.cross(other: Vector2<N, A>): N where A: Ring<N>, A: Order<N> = numberRing { this.x * other.y - this.y * other.x }
context(EuclideanSpace<N, A>)
@Suppress("LocalVariableName")
public fun <N, A> Point2<N, A>.inTriangle(P: Point2<N, A>, Q: Point2<N, A>, R: Point2<N, A>): Boolean where A: Ring<N>, A: Order<N> = numberRing {
    val a = ((this - P) cross (Q - P)).sign
    val b = ((this - Q) cross (R - Q)).sign
    val c = ((this - R) cross (P - R)).sign
    return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0)
}

internal class PointEquality<N, NE: Equality<N>>(numberEquality: NE) : Equality<Point<N, NE>> {
    val columnVectorEquality = columnVectorEquality(numberEquality)
    override fun Point<N, NE>.equalsTo(other: Point<N, NE>): Boolean = columnVectorEquality.invoke { this.coordinates eq other.coordinates }
}

public fun <E, EE: Equality<E>> pointEquality(elementEquality: EE): Equality<Point<E, EE>> =
    PointEquality(elementEquality)

internal class PointHashing<N, NE: Hashing<N>>(numberHashing: NE) : Hashing<Point<N, NE>> {
    val columnVectorHashing = columnVectorHashing(numberHashing)
    override fun Point<N, NE>.equalsTo(other: Point<N, NE>): Boolean = columnVectorHashing.invoke { this.coordinates eq other.coordinates }
    override fun Point<N, NE>.hash(): Int = columnVectorHashing.invoke { this.coordinates.hash() }
}

public fun <E, EE: Hashing<E>> pointHashing(elementHashing: EE): Hashing<Point<E, EE>> =
    PointHashing(elementHashing)

internal class VectorEquality<N, NE: Equality<N>>(numberEquality: NE) : Equality<Vector<N, NE>> {
    val columnVectorEquality = columnVectorEquality(numberEquality)
    override fun Vector<N, NE>.equalsTo(other: Vector<N, NE>): Boolean = columnVectorEquality.invoke { this.coordinates eq other.coordinates }
}

public fun <E, EE: Equality<E>> vectorEquality(elementEquality: EE): Equality<Vector<E, EE>> =
    VectorEquality(elementEquality)

internal class VectorHashing<N, NE: Hashing<N>>(numberHashing: NE) : Hashing<Vector<N, NE>> {
    val columnVectorHashing = columnVectorHashing(numberHashing)
    override fun Vector<N, NE>.equalsTo(other: Vector<N, NE>): Boolean = columnVectorHashing.invoke { this.coordinates eq other.coordinates }
    override fun Vector<N, NE>.hash(): Int = columnVectorHashing.invoke { this.coordinates.hash() }
}

public fun <E, EE: Hashing<E>> vectorHashing(elementHashing: EE): Hashing<Vector<E, EE>> =
    VectorHashing(elementHashing)

context(EuclideanSpace<N, A>)
internal val <N, A> lexicographic2DComparator: Comparator<Point2<N, *>> where A: Ring<N>, A: Order<N>
    get() = numberRing { compareByOrdered({ it.x }, { it.y }) }
context(EuclideanSpace<N, A>)
internal fun <N, A> lexicographicMDComparator(dim: UInt): Comparator<Point<N, *>> where A: Ring<N>, A: Order<N> =
    numberRing { compareByOrdered(*Array(dim.toInt()) { { p -> p.coordinates[it.toUInt()] } }) }

context(EuclideanSpace<N, A>)
internal fun <N, A> perpendicularTo(dimension: UInt, vectors: KoneArray<Vector<N, A>>, positiveDirection: Vector<N, A>): Vector<N, A> where A: Ring<N>, A: Order<N> {
    require(dimension >= 1u && vectors.size + 1u == dimension && vectors.all { it.coordinates.size == dimension } && positiveDirection.coordinates.size == dimension)

    val minor = vectorSpace { matrix(dimension - 1u, dimension) { rowIndex, columnIndex -> vectors[rowIndex].coordinates[columnIndex] }.minor }

    val normalVector = Vector(dimension) { coordinate -> minor[KoneUIntArray(dimension - 1u) { it }, KoneUIntArray(dimension - 1u) { if (it < coordinate) it else it + 1u }].let { if (coordinate % 2u != 0u) numberRing { -it } else it } }

    return (normalVector dot positiveDirection).let { if (numberRing { it.isPositive() }) normalVector else -normalVector }
}
context(EuclideanSpace<N, A>)
internal fun <N, A> perpendicularTo(dimension: UInt, vararg vectors: Vector<N, A>, positiveDirection: Vector<N, A>): Vector<N, A> where A: Ring<N>, A: Order<N> = perpendicularTo(dimension, KoneArray(vectors), positiveDirection)