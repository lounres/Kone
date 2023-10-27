/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.delegates.KoneSetAction
import dev.lounres.kone.collections.implementations.KoneResizableArrayList
import dev.lounres.kone.hooks.*
import dev.lounres.kone.linearAlgebra.experiment1.LinearSpace
import dev.lounres.kone.multidimensionalCollections.experiment1.MDFormation1
import dev.lounres.kone.order.Order
import dev.lounres.kone.order.compareByOrdered
import dev.lounres.kone.order.geq


// FIXME: KT-42977
@JvmInline
public value class Vector<out N>(public val coordinates: MDFormation1<N>)
@JvmInline
public value class Vector2<out N>(public val coordinates: MDFormation1<N>) {
    init {
        require(coordinates.size == 2u) { /*TODO*/ }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
}

// FIXME: KT-42977
@JvmInline
public value class Point<out N>(public val coordinates: MDFormation1<N>)
@JvmInline
public value class Point2<out N>(public val coordinates: MDFormation1<N>) {
    init {
        require(coordinates.size == 2u) { /*TODO*/ }
    }
    public val x: N get() = coordinates[0u]
    public val y: N get() = coordinates[1u]
}

context(LinearSpace<N, *>)
public operator fun <N> Point<N>.minus(other: Point<N>): Vector<N> = Vector(this.coordinates + other.coordinates)
context(LinearSpace<N, *>)
public operator fun <N> Point2<N>.minus(other: Point2<N>): Vector2<N> = Vector2(this.coordinates + other.coordinates)
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

context(Order<E>)
private val <E> lexicographicComparator: Comparator<Point2<E>>
    get() = compareByOrdered({ it.x }, { it.y })

/**
 * See [here](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm) for more.
 */
context(Ring<N>, Order<N>, LinearSpace<N, *>)
public fun <N> Collection<Point2<N>>.convexHullByGiftWrapping(): List<Point2<N>> {
    when (size) {
        0 -> return emptyList()
        1 -> return toList()
    }

    val startPoint = this.minWith(lexicographicComparator)
    var currentPoint = startPoint
    val result = mutableListOf<Point2<N>>()
    do {
        val iterator = this.iterator()
        var nextPoint = iterator.next()
        if (nextPoint == currentPoint) nextPoint = iterator.next()
        val nextPoints = mutableListOf(nextPoint)
        for (p in iterator) {
            if (p == currentPoint) continue
            val product = (nextPoint - currentPoint) cross (p - currentPoint)
            when {
                product >= zero -> {
                    nextPoints.clear()
                    nextPoint = p
                    nextPoints += p
                }
                product eq zero -> {
                    nextPoints += p
                }
            }
        }
        nextPoints.sortWith(lexicographicComparator)
        result += currentPoint
        currentPoint = nextPoints.removeAt(nextPoints.lastIndex)
        result += nextPoints
    } while (currentPoint != startPoint)
    return result
}

/**
 * See [here](https://en.wikipedia.org/wiki/Graham_scan) for more.
 */
context(Ring<N>, Order<N>, LinearSpace<N, *>)
public fun <N> Collection<Point2<N>>.convexHullByGrahamScan(): List<Point2<N>> {
    when (size) {
        0 -> return emptyList()
        1 -> return toList()
    }


    val centralPoint = this.minWith(lexicographicComparator)
    val points = this.toMutableList()
    points -= centralPoint
    points.sortWith(
        Comparator<Point2<N>> { p1, p2 -> (p1 - centralPoint) cross (p2 - centralPoint) compareTo zero }
            .then(lexicographicComparator)
    )

    val iterator = points.iterator()
    val result = mutableListOf(centralPoint, iterator.next())
    for (nextPoint in iterator) {
        while (true) {
            val v1 = nextPoint - result[result.lastIndex]
            val v2 = result[result.lastIndex] - result[result.lastIndex - 1]
            val crossProduct = v1 cross v2
            if (crossProduct >= zero) break
            result.removeAt(result.lastIndex)
        }
        result += nextPoint
    }
    return result
}

//context(R)
//public fun <C, R> Collection<Point<C>>.convexHullByDivideAndConquer(): List<Point<C>> where R : Ring<C>, R: Order<C> {
//    when (size) {
//        0 -> return emptyList()
//        1, 2 -> return this.toList()
//    }
//
//    val points = this.sortedWith(lexicographicComparator)
//    val upperHull = points.upperConvexHullByDivideAndConquer()
//    val lowerHull = points.asReversed().upperConvexHullByDivideAndConquer()
//    return upperHull + lowerHull
//}
//
//context(R)
//internal fun <C, R> List<Point<C>>.upperConvexHullByDivideAndConquer(): List<Point<C>> where R : Ring<C>, R: Order<C> {
//    when (size) {
//        0 -> return emptyList()
//        1, 2 -> return this
//    }
//
//    val leftUpperHull: List<Point<C>> = this.subList(0, size / 2).upperConvexHullByDivideAndConquer()
//    val rightUpperHull: List<Point<C>> = this.subList(size / 2, size).upperConvexHullByDivideAndConquer()
//    var
//}

/**
 * See [here](https://en.wikipedia.org/wiki/Quickhull) for more.
 */
context(Ring<N>, Order<N>, LinearSpace<N, *>)
public fun <N> Collection<Point2<N>>.convexHullByQuickhull(): List<Point2<N>> {
    when (size) {
        0 -> return emptyList()
        1 -> return toList()
    }

    val leftPoint = this.minWith(lexicographicComparator)
    val rightPoint = this.maxWith(lexicographicComparator)
    val points = buildList {
        addAll(this@convexHullByQuickhull)
        remove(leftPoint)
        remove(rightPoint)
    }
    val v = rightPoint - leftPoint
    return buildList {
        add(leftPoint)
        addAll(convexHullByQuickhullInternalLogic(leftPoint, rightPoint, points.filter { v cross (it - leftPoint) >= zero }))
        add(rightPoint)
        addAll(convexHullByQuickhullInternalLogic(rightPoint, leftPoint, points.filter { v cross (it - leftPoint) <= zero }))
    }
}

/**
 * See [here](https://en.wikipedia.org/wiki/Quickhull) for more.
 */
context(Ring<N>, Order<N>, LinearSpace<N, *>)
internal fun <N> convexHullByQuickhullInternalLogic(leftPoint: Point2<N>, rightPoint: Point2<N>, points: Collection<Point2<N>>): List<Point2<N>> {
    val v = rightPoint - leftPoint
    if (points.none { v cross (it - rightPoint) > zero }) return points.toList()
    val nextPoint = points.maxWith(compareByOrdered({ v cross (it - rightPoint) }))
    val newPoints = points - nextPoint

    val leftV = nextPoint - leftPoint
    val rightV = rightPoint - nextPoint
    return buildList {
        addAll(convexHullByQuickhullInternalLogic(leftPoint, nextPoint, newPoints.filter { leftV cross (it - leftPoint) >= zero }))
        add(nextPoint)
        addAll(convexHullByQuickhullInternalLogic(nextPoint, rightPoint, newPoints.filter { rightV cross (it - nextPoint) >= zero }))
    }
}

/**
 * See [here](https://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain) for more.
 */
context(Ring<N>, Order<N>, LinearSpace<N, *>)
public fun <N> Collection<Point2<N>>.convexHullByMonotoneChain(): List<Point2<N>> {
    when (size) {
        0 -> return emptyList()
        1, 2 -> return toList()
    }

    val points = this.sortedWith(lexicographicComparator)

    fun Iterator<Point2<N>>.generateHalfHull(): List<Point2<N>> {
        val halfHull = mutableListOf(next(), next())
        for (p in this) {
            while (halfHull.size >= 2) {
                val last = halfHull.last()
                val beforeLast = halfHull[halfHull.lastIndex - 1]
                if ((p - last) cross (last - beforeLast) >= zero) break
                halfHull.removeAt(halfHull.lastIndex)
            }
            halfHull += p
        }
        return halfHull
    }

    val upperHull = points.iterator().generateHalfHull()
    val lowerHull = points.asReversed().iterator().generateHalfHull()

    return upperHull.subList(0, upperHull.lastIndex) + lowerHull.subList(0, lowerHull.lastIndex)
}

//public fun Collection<Point>.aklToussaintHeuristic(): Collection<Point> {
//    val leftPoint = this.minWith(compareBy({ it.x }, { it.y }))
//    val rightPoint = this.maxWith(compareBy({ it.x }, { it.y }))
//    val topPoint = this.minWith(compareBy({ it.x }, { -it.y }))
//    val bottomPoint = this.maxWith(compareBy({ it.x }, { -it.y }))
//
//    return this.filter { !it.inTriangle(leftPoint, topPoint, rightPoint) && !it.inTriangle(leftPoint, bottomPoint, rightPoint) }
//}

context(LinearSpace<N, *>)
public fun <N> MutableSetHookable<Point2<N>>.convexHullBy(): ListHookable<Point2<N>> {
    TODO()
}

context(Ring<N>, Order<N>, LinearSpace<N, *>)
internal fun <N> ExtendableSetHookable<Point2<N>>.upperConvexHullBySweepingLine(): UpdateHookable<KoneIterableList<Point2<N>>> =
    UpdateHooker(KoneResizableArrayList<Point2<N>>()).also {
        var outputList by it
        val upperHull = KoneResizableArrayList<Point2<N>>()

        fun processPoint(point: Point2<N>) {
            if (upperHull.size <= 1u) {
                upperHull.add(point)
                return
            }
            while (outputList.size > 1u) {
                val last = outputList[outputList.size - 1u]
                val preLast = outputList[outputList.size - 2u]
                if (((point - last) cross (last - preLast)) geq zero) break
                outputList.removeAt(outputList.size - 1u)
            }
        }

        hookUp(
            ResponseBeforeAction { _, action ->
                when(action) {
                    is KoneSetAction.Add -> {
                        processPoint(action.element)
                    }

                    is KoneSetAction.AddAll -> {
                        for (point in action.elements) processPoint(point)
                    }
                }
                outputList = upperHull
            }
        )
    }
