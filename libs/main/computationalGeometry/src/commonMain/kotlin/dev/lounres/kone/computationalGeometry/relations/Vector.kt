/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.relations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.eq
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.relations.hash
import kotlin.jvm.JvmName


internal class VectorEquality<N, Content: MDList1<N>>(val contentEquality: Equality<Content>) : Equality<Vector<N, Content>> {
    override fun Vector<N, Content>.equalsTo(other: Vector<N, Content>): Boolean = contentEquality { this.coordinates eq other.coordinates }
}

@JvmName("vectorEqualityForColumnVector")
public fun <N, Content: MDList1<N>> vectorEquality(contentEquality: Equality<Content>): Equality<Vector<N, Content>> =
    VectorEquality(contentEquality)

@JvmName("vectorEqualityForNumber")
public fun <N> vectorEquality(numberEquality: Equality<N>): Equality<Vector<N, MDList1<N>>> =
    VectorEquality(MDList1.equality(numberEquality))

internal class VectorHashing<N, Content: MDList1<N>>(val contentHashing: Hashing<Content>) : Hashing<Vector<N, Content>> {
    override fun Vector<N, Content>.hash(): Int = contentHashing { this.coordinates.hash() }
}

@JvmName("vectorHashingForColumnVector")
public fun <N, Content: MDList1<N>> vectorHashing(contentHashing: Hashing<Content>): Hashing<Vector<N, Content>> =
    VectorHashing(contentHashing)

@JvmName("vectorHashingForNumber")
public fun <N> vectorHashing(numberHashing: Hashing<N>): Hashing<Vector<N, MDList1<N>>> =
    VectorHashing(MDList1.hashing(numberHashing))