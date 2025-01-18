/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.comparison

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.comparison.hash
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.comparison.columnVectorEquality
import dev.lounres.kone.linearAlgebra.comparison.columnVectorHashing
import kotlin.jvm.JvmName


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