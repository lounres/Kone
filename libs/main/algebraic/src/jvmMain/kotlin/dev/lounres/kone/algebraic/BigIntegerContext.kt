/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.*
import java.math.BigInteger
import kotlin.toBigInteger


private data object BigIntegerContext : Reification<BigInteger>, Equality<BigInteger>, Order<BigInteger>, Hashing<BigInteger>, EuclideanRing<BigInteger> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is BigInteger
    override fun reifyMaybe(element: Any?): Maybe<BigInteger> = if (element is BigInteger) Some(element) else None
    override fun reifyOrNull(element: Any?): BigInteger? = element as? BigInteger
    override fun reify(element: Any?): BigInteger = element as? BigInteger ?: reificationException()
    // endregion
    
    override val numberIsZero: IsZero<BigInteger> = IsZero { it == BigInteger.ZERO }
    override val numberIsOne: IsOne<BigInteger> = IsOne { it == BigInteger.ONE }
    
    override fun BigInteger.compareWith(other: BigInteger): ComparisonResult = this.compareTo(other).asComparisonResult()

    override val zero: BigInteger = BigInteger.ZERO
    override val one: BigInteger = BigInteger.ONE

    override fun valueOf(arg: Int): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: UInt): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: Long): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: ULong): BigInteger = arg.toBigInteger()
    
    override val numberPlusInt: Plus<BigInteger, Int, BigInteger> = Plus { left, right -> left.add(right.toBigInteger()) }
    override val numberMinusInt: Minus<BigInteger, Int, BigInteger> = Minus { left, right -> left.subtract(right.toBigInteger()) }
    override val numberTimesInt: Times<BigInteger, Int, BigInteger> = Times { left, right -> left.multiply(right.toBigInteger()) }
    
    override val numberPlusUInt: Plus<BigInteger, UInt, BigInteger> = Plus { left, right -> left.add(right.toBigInteger()) }
    override val numberMinusUInt: Minus<BigInteger, UInt, BigInteger> = Minus { left, right -> left.subtract(right.toBigInteger()) }
    override val numberTimesUInt: Times<BigInteger, UInt, BigInteger> = Times { left, right -> left.multiply(right.toBigInteger()) }
    
    override val numberPlusLong: Plus<BigInteger, Long, BigInteger> = Plus { left, right -> left.add(right.toBigInteger()) }
    override val numberMinusLong: Minus<BigInteger, Long, BigInteger> = Minus { left, right -> left.subtract(right.toBigInteger()) }
    override val numberTimesLong: Times<BigInteger, Long, BigInteger> = Times { left, right -> left.multiply(right.toBigInteger()) }
    
    override val numberPlusULong: Plus<BigInteger, ULong, BigInteger> = Plus { left, right -> left.add(right.toBigInteger()) }
    override val numberMinusULong: Minus<BigInteger, ULong, BigInteger> = Minus { left, right -> left.subtract(right.toBigInteger()) }
    override val numberTimesULong: Times<BigInteger, ULong, BigInteger> = Times { left, right -> left.multiply(right.toBigInteger()) }
    
    override val intPlusNumber: Plus<Int, BigInteger, BigInteger> = Plus { left, right -> left.toBigInteger().add(right) }
    override val intMinusNumber: Minus<Int, BigInteger, BigInteger> = Minus { left, right -> left.toBigInteger().subtract(right) }
    override val intTimesNumber: Times<Int, BigInteger, BigInteger> = Times { left, right -> left.toBigInteger().multiply(right) }
    
    override val uIntPlusNumber: Plus<UInt, BigInteger, BigInteger> = Plus { left, right -> left.toBigInteger().add(right) }
    override val uIntMinusNumber: Minus<UInt, BigInteger, BigInteger> = Minus { left, right -> left.toBigInteger().subtract(right) }
    override val uIntTimesNumber: Times<UInt, BigInteger, BigInteger> = Times { left, right -> left.toBigInteger().multiply(right) }
    
    override val longPlusNumber: Plus<Long, BigInteger, BigInteger> = Plus { left, right -> left.toBigInteger().add(right) }
    override val longMinusNumber: Minus<Long, BigInteger, BigInteger> = Minus { left, right -> left.toBigInteger().subtract(right) }
    override val longTimesNumber: Times<Long, BigInteger, BigInteger> = Times { left, right -> left.toBigInteger().multiply(right) }
    
    override val uLongPlusNumber: Plus<ULong, BigInteger, BigInteger> = Plus { left, right -> left.toBigInteger().add(right) }
    override val uLongMinusNumber: Minus<ULong, BigInteger, BigInteger> = Minus { left, right -> left.toBigInteger().subtract(right) }
    override val uLongTimesNumber: Times<ULong, BigInteger, BigInteger> = Times { left, right -> left.toBigInteger().multiply(right) }
    
    override val numberUnaryMinus: UnaryMinus<BigInteger, BigInteger> = UnaryMinus { it.negate() }
    override val numberPlusNumber: Plus<BigInteger, BigInteger, BigInteger> = Plus { left, right -> left.add(right) }
    override val numberMinusNumber: Minus<BigInteger, BigInteger, BigInteger> = Minus { left, right -> left.subtract(right) }
    override val numberTimesNumber: Times<BigInteger, BigInteger, BigInteger> = Times { left, right -> left.multiply(right) }
    override val numberDivideRemainderNumber: DivideRemainder<BigInteger, BigInteger, EuclideanDivisionResult<BigInteger>> = DivideRemainder { left, right ->
        val [quotient, remainder] = left.divideAndRemainder(right)
        EuclideanDivisionResult(quotient = quotient, remainder = remainder)
    }
    override val numberDivideNumber: Divide<BigInteger, BigInteger, BigInteger> = Divide { left, right -> left.divide(right) }
    override val numberRemainderNumber: Remainder<BigInteger, BigInteger, BigInteger> = Remainder { left, right -> left.remainder(right) }
}

// TODO: KT-11968
public data object BigIntegerCompanion

public fun BigIntegerCompanion.reification(): Reification<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.equality(): Equality<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.order(): Order<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.hashing(): Hashing<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.euclideanRing(): EuclideanRing<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.euclideanSemiring(): EuclideanSemiring<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.commutativeRing(): CommutativeRing<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.ring(): Ring<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.commutativeSemiring(): CommutativeSemiring<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.semiring(): Semiring<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.commutativeGroup(): CommutativeGroup<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.group(): Group<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.commutativeMonoid(): CommutativeMonoid<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.monoid(): Monoid<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.commutativeSemigroup(): CommutativeSemigroup<BigInteger> = BigIntegerContext
public fun BigIntegerCompanion.semigroup(): Semigroup<BigInteger> = BigIntegerContext

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setReification() {
    Reification.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setEquality() {
    Equality.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setOrder() {
    Order.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setHashing() {
    Hashing.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setEuclideanRing() {
    EuclideanRing.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setEuclideanSemiring() {
    EuclideanSemiring.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setCommutativeRing() {
    CommutativeRing.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setRing() {
    Ring.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setCommutativeSemiring() {
    CommutativeSemiring.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setSemiring() {
    Semiring.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setCommutativeGroup() {
    CommutativeGroup.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setGroup() {
    Group.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setCommutativeMonoid() {
    CommutativeMonoid.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setMonoid() {
    Monoid.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun BigIntegerCompanion.setSemigroup() {
    Semigroup.Key<BigInteger>().withImpliedUsingFirst correspondsTo BigIntegerContext
}