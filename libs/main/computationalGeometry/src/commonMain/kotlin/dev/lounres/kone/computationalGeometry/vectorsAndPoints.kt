/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.collections.KoneArray
import dev.lounres.kone.collections.KoneUIntArray
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.linearAlgebra.experiment1.ColumnVector
import dev.lounres.kone.linearAlgebra.experiment1.minor
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList1
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.compareByOrdered
import dev.lounres.kone.context.invoke


// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Vector<out N>(public val coordinates: ColumnVector<N>) {
    override fun toString(): String = "Vector($coordinates)"
}
//@JvmInline
public /*value*/ class Vector2<out N>(coordinates: ColumnVector<N>): Vector<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { /*TODO*/ }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]

    override fun toString(): String = "Vector2($coordinates)"
}

// FIXME: KT-42977
//@JvmInline
public open /*value*/ class Point<out N>(public open val coordinates: ColumnVector<N>) {
    override fun toString(): String = "Point($coordinates)"
}
//@JvmInline
public /*value*/ class Point2<out N>(override val coordinates: ColumnVector<N>): Point<N>(coordinates) {
    init {
        require(coordinates.size == 2u) { /*TODO*/ }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]

    override fun toString(): String = "Point2($coordinates)"
}

public fun <N> Vector(coordinates: MDList1<N>): Vector<N> = Vector(ColumnVector(coordinates))
public fun <N> Vector(vararg coordinates: N): Vector<N> = Vector(ColumnVector(*coordinates))
public fun <N> Vector(size: UInt, initializer: (coordinate: UInt) -> N): Vector<N> = Vector(ColumnVector(size, initializer))

public fun <N> Point(coordinates: MDList1<N>): Point<N> = Point(ColumnVector(coordinates))
public fun <N> Point(vararg coordinates: N): Point<N> = Point(ColumnVector(*coordinates))
public fun <N> Point(size: UInt, initializer: (coordinate: UInt) -> N): Point<N> = Point(ColumnVector(size, initializer))

context(EuclideanSpace<N, *>)
public operator fun <N> Vector<N>.unaryMinus(): Vector<N> = vectorSpace { Vector(-this.coordinates) }
context(EuclideanSpace<N, *>)
public operator fun <N> Vector2<N>.unaryMinus(): Vector2<N> = vectorSpace { Vector2(-this.coordinates) }

context(EuclideanSpace<N, *>)
public operator fun <N> Vector<N>.plus(other: Vector<N>): Vector<N> = vectorSpace { Vector(this.coordinates + other.coordinates) }
context(EuclideanSpace<N, *>)
public operator fun <N> Vector2<N>.plus(other: Vector2<N>): Vector2<N> = vectorSpace { Vector2(this.coordinates + other.coordinates) }

context(EuclideanSpace<N, *>)
public operator fun <N> Vector<N>.minus(other: Vector<N>): Vector<N> = vectorSpace { Vector(this.coordinates - other.coordinates) }
context(EuclideanSpace<N, *>)
public operator fun <N> Vector2<N>.minus(other: Vector2<N>): Vector2<N> = vectorSpace { Vector2(this.coordinates - other.coordinates) }

context(EuclideanSpace<N, *>)
public operator fun <N> Vector<N>.times(other: N): Vector<N> = vectorSpace { Vector(this.coordinates * other) }
context(EuclideanSpace<N, *>)
public operator fun <N> Vector2<N>.times(other: N): Vector2<N> = vectorSpace { Vector2(this.coordinates * other) }

context(EuclideanSpace<N, *>)
public operator fun <N> Point<N>.plus(other: Vector<N>): Point<N> = vectorSpace { Point(this.coordinates + other.coordinates) }
context(EuclideanSpace<N, *>)
public operator fun <N> Point2<N>.plus(other: Vector2<N>): Point2<N> = vectorSpace { Point2(this.coordinates + other.coordinates) }

context(EuclideanSpace<N, *>)
public operator fun <N> Point<N>.minus(other: Point<N>): Vector<N> = vectorSpace { Vector(this.coordinates - other.coordinates) }
context(EuclideanSpace<N, *>)
public operator fun <N> Point2<N>.minus(other: Point2<N>): Vector2<N> = vectorSpace { Vector2(this.coordinates - other.coordinates) }

context(EuclideanSpace<N, A>)
public infix fun <N, A> Vector<N>.dot(other: Vector<N>): N where A: Ring<N>, A: Order<N> = numberRing {
    require(this.coordinates.size == other.coordinates.size)
    return (0u ..< this.coordinates.size).fold(zero) { acc, index -> acc + this.coordinates[index] * other.coordinates[index] }
}

context(EuclideanSpace<N, A>)
public infix fun <N, A> Vector2<N>.cross(other: Vector2<N>): N where A: Ring<N>, A: Order<N> = numberRing { this.x * other.y - this.y * other.x }
context(EuclideanSpace<N, A>)
@Suppress("LocalVariableName")
public fun <N, A> Point2<N>.inTriangle(P: Point2<N>, Q: Point2<N>, R: Point2<N>): Boolean where A: Ring<N>, A: Order<N> = numberRing {
    val a = ((this - P) cross (Q - P)).sign
    val b = ((this - Q) cross (R - Q)).sign
    val c = ((this - R) cross (P - R)).sign
    return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0)
}

context(EuclideanSpace<N, A>)
internal val <N, A> lexicographic2DComparator: Comparator<Point2<N>> where A: Ring<N>, A: Order<N>
    get() = numberRing { compareByOrdered({ it.x }, { it.y }) }
context(EuclideanSpace<N, A>)
internal fun <N, A> lexicographicMDComparator(dim: UInt): Comparator<Point<N>> where A: Ring<N>, A: Order<N> =
    numberRing { compareByOrdered(*Array(dim.toInt()) { { p -> p.coordinates[it.toUInt()] } }) }

context(EuclideanSpace<N, A>)
internal fun <N, A> perpendicularTo(dimension: UInt, vectors: KoneArray<Vector<N>>, positiveDirection: Vector<N>): Vector<N> where A: Ring<N>, A: Order<N> {
    require(dimension >= 1u && vectors.size + 1u == dimension && vectors.all { it.coordinates.size == dimension } && positiveDirection.coordinates.size == dimension)

    val minor = vectorSpace { matrix(dimension - 1u, dimension) { rowIndex, columnIndex -> vectors[rowIndex].coordinates[columnIndex] }.minor }

    val normalVector = Vector(dimension) { coordinate -> minor[KoneUIntArray(dimension - 1u) { it }, KoneUIntArray(dimension - 1u) { if (it < coordinate) it else it + 1u }].let { if (coordinate % 2u != 0u) numberRing { -it } else it } }

    return (normalVector dot positiveDirection).let { if (numberRing { it.isPositive() }) normalVector else -normalVector }
}
context(EuclideanSpace<N, A>)
internal fun <N, A> perpendicularTo(dimension: UInt, vararg vectors: Vector<N>, positiveDirection: Vector<N>): Vector<N> where A: Ring<N>, A: Order<N> = perpendicularTo(dimension, KoneArray(vectors), positiveDirection)