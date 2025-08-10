/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.asComparisonResult
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.suppliedTypes.SuppliedType


//@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
//public data object SafeLongContext: Reification<Long>, EuclideanRing<Long>, Order<Long>, Hashing<Long> {
//    // region Reification
//    override fun contains(element: Any?): Boolean = element is Long
//    override fun reifyMaybe(element: Any?): Maybe<Long> = if (element is Long) Some(element) else None
//    override fun reifyOrNull(element: Any?): Long? = element as? Long
//    override fun reify(element: Any?): Long = element as? Long ?: reificationException()
//    // endregion
//
//    // region Order
//    override fun Long.compareWith(other: Long): ComparisonResult = this.compareTo(other).asComparisonResult()
//    // endregion
//
//    // region Constants
//    override val zero: Long get() = 0L
//    override val one: Long get() = 1L
//    // endregion
//
//    // region Conversion
//    override fun valueOf(arg: Int): Long = arg.toLong()
//    override fun valueOf(arg: UInt): Long = arg.toLong()
//    override fun valueOf(arg: Long): Long = arg
//    override fun valueOf(arg: ULong): Long {
//        if (arg > Long.MAX_VALUE.toULong()) overflow()
//        return arg.toLong()
//    }
//    // endregion
//
//    // region Long-Long operations
//    override operator fun Long.unaryMinus(): Long {
//        if (this == Long.MIN_VALUE) overflow()
//        return -this
//    }
//    override operator fun Long.plus(other: Long): Long {
//        val result = this + other
//        return when {
//            other > 0L && result < this -> overflow()
//            other < 0L && result > this -> overflow()
//            else -> result
//        }
//    }
//    override operator fun Long.minus(other: Long): Long {
//        val result = this - other
//        return when {
//            other > 0L && result > this -> overflow()
//            other < 0L && result < this -> overflow()
//            else -> result
//        }
//    }
//    override operator fun Long.times(other: Long): Long {
//        val result = this * other
//        return if (result / other != this) overflow() else result
//    }
//    override fun Long.divrem(other: Long): EuclideanDivisionResult<Long> =
//        EuclideanDivisionResult(quotient = this / other, remainder = this % other)
//    override fun Long.div(other: Long): Long = this / other
//    override fun Long.rem(other: Long): Long = this % other
//    // endregion
//
//    // region Long-Int operations
//    override operator fun Long.plus(other: Int): Long {
//        val result = this + other
//        return when {
//            other > 0 && result < this -> overflow()
//            other < 0 && result > this -> overflow()
//            else -> result
//        }
//    }
//    override operator fun Long.minus(other: Int): Long = this - other
//    override operator fun Long.times(other: Int): Long = this * other
//    // endregion
//
//    // region Long-UInt operations
//    override operator fun Long.plus(other: UInt): Long {
//        val result = this + other.toLong()
//        return when {
//            result < this -> overflow()
//            else -> result
//        }
//    }
//    override operator fun Long.minus(other: UInt): Long {
//        val result = this - other.toLong()
//        return when {
//            result > this -> overflow()
//            else -> result
//        }
//    }
//    override operator fun Long.times(other: UInt): Long = this * other.toLong()
//    // endregion
//
//    // region Long-ULong operations
//    override operator fun Long.plus(other: ULong): Long {
//        if (other > Long.MAX_VALUE.toULong()) overflow()
//        val result = this + other.toLong()
//        return when {
//            result < this -> overflow()
//            else -> result
//        }
//    }
//    override operator fun Long.minus(other: ULong): Long = this - other.toLong()
//    override operator fun Long.times(other: ULong): Long = this * other.toLong()
//    // endregion
//
//    // region Int-Long operations
//    override operator fun Int.plus(other: Long): Long {
//        val result = this + other
//        return when {
//            this > 0 && result < other -> overflow()
//            this < 0 && result > other -> overflow()
//            else -> result
//        }
//    }
//    override operator fun Int.minus(other: Long): Long = this - other
//    override operator fun Int.times(other: Long): Long = this * other
//    // endregion
//
//    // region UInt-Long operations
//    override operator fun UInt.plus(other: Long): Long {
//        val result = this.toLong() + other
//        return when {
//            result < other -> overflow()
//            else -> result
//        }
//    }
//    override operator fun UInt.minus(other: Long): Long = this.toLong() - other
//    override operator fun UInt.times(other: Long): Long = this.toLong() * other
//    // endregion
//
//    // region ULong-Long operations
//    override operator fun ULong.plus(other: Long): Long {
//        if (this > Long.MAX_VALUE.toULong()) overflow()
//        val result = this.toLong() + other
//        return when {
//            result < other -> overflow()
//            else -> result
//        }
//    }
//    override operator fun ULong.minus(other: Long): Long = this.toLong() - other
//    override operator fun ULong.times(other: Long): Long = this.toLong() * other
//    // endregion
//}

//public val Long.Companion.safeContext: SafeLongContext get() = SafeLongContext
//public fun KoneContextRegistryBuilder.setSafeLongContext() {
//    val longSuppliedType = SuppliedType.Regular<Long>(
//        kClass = Long::class,
//        typeArguments = emptyList(),
//        isNullable = false,
//    )
//    contextsBuilder[Reification.Key(longSuppliedType)] = SafeLongContext
//    contextsBuilder[Equality.Key(longSuppliedType)] = SafeLongContext
//    contextsBuilder[Ring.Key(longSuppliedType)] = SafeLongContext
//    contextsBuilder[EuclideanRing.Key(longSuppliedType)] = SafeLongContext
//    contextsBuilder[Order.Key(longSuppliedType)] = SafeLongContext
//    contextsBuilder[Hashing.Key(longSuppliedType)] = SafeLongContext
//}