/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


@Serializable
@JvmInline
public value class PointWrapper<out Vector>(public val vector: Vector) {
    override fun toString(): String = vector.toString()
    
    public companion object
}

private class PointWrapperEquality<Vector>(private val vectorEquality: Equality<Vector>): Equality<PointWrapper<Vector>> {
    override fun PointWrapper<Vector>.equalsTo(other: PointWrapper<Vector>): Boolean =
        vectorEquality { this.vector eq other.vector }
}

public fun <Vector> PointWrapper.Companion.equality(vectorEquality: Equality<Vector>): Equality<PointWrapper<Vector>> =
    PointWrapperEquality(vectorEquality)

private class PointWrapperHashing<Vector>(private val vectorHashing: Hashing<Vector>): Hashing<PointWrapper<Vector>> {
    override fun PointWrapper<Vector>.hash(): Int = vectorHashing { this.vector.hash() }
}

public fun <Vector> PointWrapper.Companion.hashing(vectorHashing: Hashing<Vector>): Hashing<PointWrapper<Vector>> =
    PointWrapperHashing(vectorHashing)

private class PointWrapperOrder<Vector>(private val vectorOrder: Order<Vector>): Order<PointWrapper<Vector>> {
    override fun PointWrapper<Vector>.compareWith(other: PointWrapper<Vector>): ComparisonResult =
        vectorOrder { this.vector compareWith other.vector }
}

public fun <Vector> PointWrapper.Companion.order(vectorOrder: Order<Vector>): Order<PointWrapper<Vector>> =
    PointWrapperOrder(vectorOrder)