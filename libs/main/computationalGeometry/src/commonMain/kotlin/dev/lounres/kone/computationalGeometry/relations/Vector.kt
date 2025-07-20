/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.relations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.relations.columnVectorEquality
import dev.lounres.kone.linearAlgebra.relations.columnVectorHashing
import kotlin.jvm.JvmName


internal class VectorEquality<N>(val columnVectorEquality: Equality<ColumnVector<N>>) : Equality<Vector<N>> {
    override fun Vector<N>.equalsTo(other: Vector<N>): Boolean = columnVectorEquality { this.coordinates eq other.coordinates }
}

@JvmName("vectorEqualityForColumnVector")
public fun <N> vectorEquality(columnVectorEquality: Equality<ColumnVector<N>>): Equality<Vector<N>> =
    VectorEquality(columnVectorEquality)

@JvmName("vectorEqualityForNumber")
public fun <N> vectorEquality(numberEquality: Equality<N>): Equality<Vector<N>> =
    VectorEquality(columnVectorEquality(numberEquality))

internal class VectorHashing<N>(val columnVectorHashing: Hashing<ColumnVector<N>>) : Hashing<Vector<N>> {
    override fun Vector<N>.hash(): Int = columnVectorHashing { this.coordinates.hash() }
}

@JvmName("vectorHashingForColumnVector")
public fun <N> vectorHashing(columnVectorHashing: Hashing<ColumnVector<N>>): Hashing<Vector<N>> =
    VectorHashing(columnVectorHashing)

@JvmName("vectorHashingForNumber")
public fun <N> vectorHashing(numberHashing: Hashing<N>): Hashing<Vector<N>> =
    VectorHashing(columnVectorHashing(numberHashing))