/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.asComparisonResult
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImplied
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedType


// TODO: Add other safe contexts

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object SafeLongContext: Reification<Long>, EuclideanRing<Long>, Order<Long>, Hashing<Long> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Long
    override fun reifyMaybe(element: Any?): Maybe<Long> = if (element is Long) Some(element) else None
    override fun reifyOrNull(element: Any?): Long? = element as? Long
    override fun reify(element: Any?): Long = element as? Long ?: reificationException()
    // endregion

    // region Order
    override fun Long.compareWith(other: Long): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Long get() = 0L
    override val one: Long get() = 1L
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Long = arg.toLong()
    override fun valueOf(arg: UInt): Long = arg.toLong()
    override fun valueOf(arg: Long): Long = arg
    override fun valueOf(arg: ULong): Long {
        if (arg and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        return arg.toLong()
    }
    // endregion

    // region Long-Long operations
    override operator fun Long.unaryMinus(): Long {
        if (this == Long.MIN_VALUE) overflow()
        return -this
    }
    override operator fun Long.plus(other: Long): Long {
        val result = this + other
        if (other > 0L != result > this) overflow()
        return result
    }
    override operator fun Long.minus(other: Long): Long {
        val result = this - other
        if (other < 0L != result > this) overflow()
        return result
    }
    override operator fun Long.times(other: Long): Long {
        if (other == 0L) return 0L
        val result = this * other
        if (result / other != this) overflow()
        return result
    }
    override fun Long.divrem(other: Long): EuclideanDivisionResult<Long> =
        if (other == 0L) divisionByZero()
        else EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Long.div(other: Long): Long = if (other == 0L) divisionByZero() else this / other
    override fun Long.rem(other: Long): Long = if (other == 0L) divisionByZero() else this % other
    // endregion

    // region Long-Int operations
    override operator fun Long.plus(other: Int): Long {
        val result = this + other
        if (other > 0L != result > this) overflow()
        return result
    }
    override operator fun Long.minus(other: Int): Long {
        val result = this - other
        if (other < 0L != result > this) overflow()
        return result
    }
    override operator fun Long.times(other: Int): Long {
        if (other == 0) return 0L
        val result = this * other
        if (result / other != this) overflow()
        return result
    }
    // endregion

    // region Long-UInt operations
    override operator fun Long.plus(other: UInt): Long {
        val result = this + other.toLong()
        if (result < this) overflow()
        return result
    }
    override operator fun Long.minus(other: UInt): Long {
        val result = this - other.toLong()
        if (result > this) overflow()
        return result
    }
    override operator fun Long.times(other: UInt): Long {
        if (other == 0u) return 0L
        val other = other.toLong()
        val result = this * other
        if (result / other != this) overflow()
        return result
    }
    // endregion

    // region Long-ULong operations
    override operator fun Long.plus(other: ULong): Long {
        val result = this + other.toLong()
        if (result < this) overflow()
        return result
    }
    override operator fun Long.minus(other: ULong): Long {
        val result = this - other.toLong()
        if (result > this) overflow()
        return result
    }
    override operator fun Long.times(other: ULong): Long {
        if (this == 0L) return 0L
        if (other and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val other = other.toLong()
        val result = this * other
        if (result / this != other) overflow()
        return result
    }
    // endregion

    // region Int-Long operations
    override operator fun Int.plus(other: Long): Long {
        val result = this + other
        if (other > 0L != result > this) overflow()
        return result
    }
    override operator fun Int.minus(other: Long): Long {
        val result = this - other
        if (other < 0L != result > this) overflow()
        return result
    }
    override operator fun Int.times(other: Long): Long {
        if (other == 0L) return 0L
        val result = this * other
        if (result / other != this.toLong()) overflow()
        return result
    }
    // endregion

    // region UInt-Long operations
    override operator fun UInt.plus(other: Long): Long {
        val result = this.toLong() + other
        if (result < other) overflow()
        return result
    }
    override operator fun UInt.minus(other: Long): Long {
        val that = this.toLong()
        val result = that - other
        if (other < 0L != result > that) overflow()
        return result
    }
    override operator fun UInt.times(other: Long): Long {
        if (other == 0L) return 0L
        val result = this.toLong() * other
        if (result / other != this.toLong()) overflow()
        return result
    }
    // endregion

    // region ULong-Long operations
    override operator fun ULong.plus(other: Long): Long {
        val result = this.toLong() + other
        if (result < other) overflow()
        return result
    }
    override operator fun ULong.minus(other: Long): Long {
        TODO()
    }
    override operator fun ULong.times(other: Long): Long {
        if (other == 0L) return 0L
        if (this and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val that = this.toLong()
        val result = that * other
        if (result / other != that) overflow()
        return result
    }
    // endregion
}

public val Long.Companion.safeContext: SafeLongContext get() = SafeLongContext

context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun SafeLongContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val longSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Long",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in SafeLongContext>>(
        Reification.Key(longSuppliedType),
        EuclideanRing.Key(longSuppliedType),
        Order.Key(longSuppliedType),
        Hashing.Key(longSuppliedType),
    ).forEach {
        it.withImplied correspondsTo SafeLongContext
    }
}