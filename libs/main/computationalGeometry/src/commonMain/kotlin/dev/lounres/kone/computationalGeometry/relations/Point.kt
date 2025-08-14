/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.relations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.eq
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.relations.hash
import kotlin.jvm.JvmName


private class PointEquality<N, Content: MDList1<N>>(val contentEquality: Equality<Content>) : Equality<Point<N, Content>> {
    override fun Point<N, Content>.equalsTo(other: Point<N, Content>): Boolean = contentEquality { this.coordinates eq other.coordinates }
}

@JvmName("pointEqualityForColumnVector")
public fun <N, Content: MDList1<N>> Point.Companion.equality(contentEquality: Equality<Content>): Equality<Point<N, Content>> =
    PointEquality(contentEquality)

@JvmName("pointEqualityForNumber")
public fun <N> Point.Companion.equality(numberEquality: Equality<N>): Equality<Point<N, MDList1<N>>> =
    PointEquality(MDList1.equality(numberEquality))

private class PointHashing<N, Content: MDList1<N>>(val contentHashing: Hashing<Content>) : Hashing<Point<N, Content>> {
    override fun Point<N, Content>.hash(): Int = contentHashing { this.coordinates.hash() }
}

@JvmName("pointHashingForColumnVector")
public fun <N, Content: MDList1<N>> Point.Companion.hashing(contentHashing: Hashing<Content>): Hashing<Point<N, Content>> =
    PointHashing(contentHashing)

@JvmName("pointHashingForNumber")
public fun <N> Point.Companion.hashing(numberHashing: Hashing<N>): Hashing<Point<N, MDList1<N>>> =
    PointHashing(MDList1.hashing(numberHashing))