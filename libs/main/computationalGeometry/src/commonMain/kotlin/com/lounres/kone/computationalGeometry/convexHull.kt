/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.computationalGeometry

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.order.Order
import com.lounres.kone.order.compareByOrder

context(Ring<E>, Order<E>)
public val <E> E.sign: Int get() {
    val compareResult = this.compareTo(zero)
    return when {
        compareResult > 0 -> 1
        compareResult < 0 -> -1
        else -> 0
    }
}
public data class Vector<out E>(val x: E, val y: E)
public data class Point<out E>(val x: E, val y: E)
context(Ring<E>)
public operator fun <E> Point<E>.minus(other: Point<E>): Vector<E> = Vector(this.x - other.x, this.y - other.y)
context(Ring<E>)
public infix fun <E> Vector<E>.cross(other: Vector<E>): E = (this.x * other.y - this.y * other.x)
context(Ring<E>, Order<E>)
@Suppress("LocalVariableName")
public fun <E> Point<E>.inTriangle(A: Point<E>, B: Point<E>, C: Point<E>): Boolean {
    val a = ((this - A) cross (B - A)).sign
    val b = ((this - B) cross (C - B)).sign
    val c = ((this - C) cross (A - C)).sign
    return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0)
}

context(Order<E>)
private val <E> lexicographicComparator: Comparator<Point<E>>
    get() = compareByOrder({ it.x }, { it.y })

/**
 * See [here](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm) for more.
 */
context(R)
public fun <E, R> Collection<Point<E>>.convexHullByGiftWrapping(): List<Point<E>> where R : Ring<E>, R: Order<E> {
    when (size) {
        0 -> return emptyList()
        1 -> return toList()
    }

    val startPoint = this.minWith(lexicographicComparator)
    var currentPoint = startPoint
    val result = mutableListOf<Point<E>>()
    do {
        val iterator = this.iterator()
        var nextPoint = iterator.next()
        if (nextPoint === currentPoint) nextPoint = iterator.next()
        val nextPoints = mutableListOf(nextPoint)
        for (p in iterator) {
            if (p === currentPoint) continue
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
    } while (currentPoint !== startPoint)
    return result
}

/**
 * See [here](https://en.wikipedia.org/wiki/Graham_scan) for more.
 */
context(R)
public fun <E, R> Collection<Point<E>>.convexHullByGrahamScan(): List<Point<E>> where R : Ring<E>, R: Order<E> {
    when (size) {
        0 -> return emptyList()
        1 -> return toList()
    }


    val centralPoint = this.minWith(lexicographicComparator)
    val points = this.toMutableList()
    points -= centralPoint
    points.sortWith(
        Comparator<Point<E>> { p1, p2 -> (p1 - centralPoint) cross (p2 - centralPoint) compareTo zero }
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
context(R)
public fun <E, R> Collection<Point<E>>.convexHullByQuickhull(): List<Point<E>> where R : Ring<E>, R: Order<E> {
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
context(R)
internal fun <E, R> convexHullByQuickhullInternalLogic(leftPoint: Point<E>, rightPoint: Point<E>, points: Collection<Point<E>>): List<Point<E>> where R : Ring<E>, R: Order<E> {
    val v = rightPoint - leftPoint
    if (points.none { v cross (it - rightPoint) > zero }) return points.toList()
    val nextPoint = points.maxWith(compareByOrder({ v cross (it - rightPoint) }))
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
context(R)
public fun <E, R> Collection<Point<E>>.convexHullByMonotoneChain(): List<Point<E>> where R : Ring<E>, R: Order<E> {
    when (size) {
        0 -> return emptyList()
        1, 2 -> return toList()
    }

    val points = this.sortedWith(lexicographicComparator)

    fun Iterator<Point<E>>.generateHalfHull(): List<Point<E>> {
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