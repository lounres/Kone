/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.comparison

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.comparison.hash
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.comparison.columnVectorEquality
import dev.lounres.kone.linearAlgebra.comparison.columnVectorHashing
import kotlin.jvm.JvmName


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