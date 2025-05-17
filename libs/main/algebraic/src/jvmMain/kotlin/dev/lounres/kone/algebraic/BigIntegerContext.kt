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
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedType
import java.math.BigInteger


/**
 * Default ring for [BigInteger] type which values are seen as integers.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
 */
public data object BigIntegerContext : Reification<BigInteger>, EuclideanRing<BigInteger>, Order<BigInteger>, Hashing<BigInteger> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is BigInteger
    override fun reifyMaybe(element: Any?): Maybe<BigInteger> = if (element is BigInteger) Some(element) else None
    override fun reifyOrNull(element: Any?): BigInteger? = element as? BigInteger
    override fun reify(element: Any?): BigInteger = element as? BigInteger ?: reificationException()
    // endregion
    
    override fun BigInteger.compareWith(other: BigInteger): ComparisonResult = this.compareTo(other).asComparisonResult()

    override val zero: BigInteger = BigInteger.ZERO
    override val one: BigInteger = BigInteger.ONE

    override fun valueOf(arg: Int): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: UInt): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: Long): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: ULong): BigInteger = arg.toBigInteger()

    override operator fun BigInteger.plus(other: Int): BigInteger = this.add(other.toBigInteger())
    override operator fun BigInteger.minus(other: Int): BigInteger = this.subtract(other.toBigInteger())
    override operator fun BigInteger.times(other: Int): BigInteger = this.multiply(other.toBigInteger())
    
    override operator fun BigInteger.plus(other: UInt): BigInteger = this.add(other.toBigInteger())
    override operator fun BigInteger.minus(other: UInt): BigInteger = this.subtract(other.toBigInteger())
    override operator fun BigInteger.times(other: UInt): BigInteger = this.multiply(other.toBigInteger())

    override operator fun BigInteger.plus(other: Long): BigInteger = this.add(other.toBigInteger())
    override operator fun BigInteger.minus(other: Long): BigInteger = this.subtract(other.toBigInteger())
    override operator fun BigInteger.times(other: Long): BigInteger = this.multiply(other.toBigInteger())
    
    override operator fun BigInteger.plus(other: ULong): BigInteger = this.add(other.toBigInteger())
    override operator fun BigInteger.minus(other: ULong): BigInteger = this.subtract(other.toBigInteger())
    override operator fun BigInteger.times(other: ULong): BigInteger = this.multiply(other.toBigInteger())

    override operator fun Int.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    override operator fun Int.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    override operator fun Int.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)
    
    override operator fun UInt.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    override operator fun UInt.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    override operator fun UInt.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)

    override operator fun Long.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    override operator fun Long.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    override operator fun Long.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)
    
    override operator fun ULong.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    override operator fun ULong.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    override operator fun ULong.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)

    override fun BigInteger.unaryMinus(): BigInteger = this.negate()
    override fun BigInteger.plus(other: BigInteger): BigInteger = this.add(other)
    override fun BigInteger.minus(other: BigInteger): BigInteger = this.subtract(other)
    override fun BigInteger.times(other: BigInteger): BigInteger = this.multiply(other)
    override fun BigInteger.divrem(other: BigInteger): EuclideanDivisionResult<BigInteger> {
        val (quotient, remainder) = this.divideAndRemainder(other)
        return EuclideanDivisionResult(quotient = quotient, remainder = remainder)
    }
    override fun BigInteger.div(other: BigInteger): BigInteger = this.divide(other)
    override fun BigInteger.rem(other: BigInteger): BigInteger = this.remainder(other)
}

// TODO: KT-11968
///**
// * Default context of the [BigInteger] type. See [BigIntegerContext] for more.
// */
//public val BigInteger.Companion.context: BigIntegerContext get() = BigIntegerContext

/**
 * Installs default [BigInteger] context (see [BigIntegerContext])
 * as the following type of contexts with [BigInteger] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [Ring],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installBigIntegerContext() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val bigIntegerSuppliedType = SuppliedType.Regular<BigInteger>(
        fullyQualifiedName = "java.math.BigInteger",
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(bigIntegerSuppliedType)] = BigIntegerContext
    contextsBuilder[Equality.Key(bigIntegerSuppliedType)] = BigIntegerContext
    contextsBuilder[Semiring.Key(bigIntegerSuppliedType)] = BigIntegerContext
    contextsBuilder[Ring.Key(bigIntegerSuppliedType)] = BigIntegerContext
    contextsBuilder[EuclideanSemiring.Key(bigIntegerSuppliedType)] = BigIntegerContext
    contextsBuilder[EuclideanRing.Key(bigIntegerSuppliedType)] = BigIntegerContext
    contextsBuilder[Order.Key(bigIntegerSuppliedType)] = BigIntegerContext
    contextsBuilder[Hashing.Key(bigIntegerSuppliedType)] = BigIntegerContext
}