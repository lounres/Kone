/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.computationalGeometry

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.order.Ordered
import com.lounres.kone.order.compareByOrdered


public data class Vector<out C>(val x: C, val y: C)
public data class Point<out C>(val x: C, val y: C)
context(Ring<C>)
public operator fun <C> Point<C>.minus(other: Point<C>): Vector<C> = Vector(this.x - other.x, this.y - other.y)
context(Ring<C>)
public infix fun <C> Vector<C>.cross(other: Vector<C>): C = (this.x * other.y - this.y * other.x)
//public fun Point.inTriangle(A: Point, B: Point, C: Point): Boolean {
//    val a = ((this - A) cross (B - A)).sign
//    val b = ((this - B) cross (C - B)).sign
//    val c = ((this - C) cross (A - C)).sign
//    return a == b && b == c
//}

context(Ordered<C>)
private val <C> lexicographicComparator: Comparator<Point<C>>
    get() = compareByOrdered({ it.x }, { it.y })

/**
 * See [here](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm) for more.
 */
context(R)
public fun <C, R> Collection<Point<C>>.convexHullByGiftWrapping(): List<Point<C>> where R : Ring<C>, R: Ordered<C> {
    when (size) {
        0 -> return emptyList()
        1 -> return toList()
    }

    val startPoint = this.minWith(lexicographicComparator)
    var currentPoint = startPoint
    val result = mutableListOf<Point<C>>()
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
public fun <C, R> Collection<Point<C>>.convexHullByGrahamScan(): List<Point<C>> where R : Ring<C>, R: Ordered<C> {
    when (size) {
        0 -> return emptyList()
        1 -> return toList()
    }


    val centralPoint = this.minWith(lexicographicComparator)
    val points = this.toMutableList()
    points -= centralPoint
    points.sortWith(
        Comparator<Point<C>> { p1, p2 -> (p1 - centralPoint) cross (p2 - centralPoint) compareTo zero }
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

/**
 * See [here](https://en.wikipedia.org/wiki/Quickhull) for more.
 */
context(R)
public fun <C, R> Collection<Point<C>>.convexHullByQuickhull(): List<Point<C>> where R : Ring<C>, R: Ordered<C> {
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
internal fun <C, R> convexHullByQuickhullInternalLogic(leftPoint: Point<C>, rightPoint: Point<C>, points: Collection<Point<C>>): List<Point<C>> where R : Ring<C>, R: Ordered<C> {
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
context(R)
public fun <C, R> Collection<Point<C>>.convexHullByMonotoneChain(): List<Point<C>> where R : Ring<C>, R: Ordered<C> {
    when (size) {
        0 -> return emptyList()
        1, 2 -> return toList()
    }

    val points = this.sortedWith(lexicographicComparator)

    fun Iterator<Point<C>>.generateHalfHull(): List<Point<C>> {
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