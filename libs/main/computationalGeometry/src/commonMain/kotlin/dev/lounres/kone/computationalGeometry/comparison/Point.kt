/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.comparison

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.context
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.comparison.columnVectorEquality
import dev.lounres.kone.linearAlgebra.comparison.columnVectorHashing
import kotlin.jvm.JvmName


internal class PointEquality<N>(val columnVectorEquality: Equality<ColumnVector<N>>) : Equality<Point<N>> {
    override fun Point<N>.equalsTo(other: Point<N>): Boolean = context(columnVectorEquality) { this.coordinates eq other.coordinates }
}

@JvmName("pointEqualityForColumnVector")
public fun <N> pointEquality(columnVectorEquality: Equality<ColumnVector<N>>): Equality<Point<N>> =
    PointEquality(columnVectorEquality)

@JvmName("pointEqualityForNumber")
public fun <N> pointEquality(numberEquality: Equality<N>): Equality<Point<N>> =
    PointEquality(columnVectorEquality(numberEquality))

internal class PointHashing<N>(val columnVectorHashing: Hashing<ColumnVector<N>>) : Hashing<Point<N>> {
    override fun Point<N>.hash(): Int = context(columnVectorHashing) { this.coordinates.hash() }
}

@JvmName("pointHashingForColumnVector")
public fun <N> pointHashing(columnVectorContext: Hashing<ColumnVector<N>>): Hashing<Point<N>> =
    PointHashing(columnVectorContext)

@JvmName("pointHashingForNumber")
public fun <N> pointHashing(numberContext: Hashing<N>): Hashing<Point<N>> =
    PointHashing(columnVectorHashing(numberContext))