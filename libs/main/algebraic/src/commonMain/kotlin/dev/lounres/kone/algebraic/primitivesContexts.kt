/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.contexts.KoneContextRegistry
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
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import kotlin.math.pow as kpow


private data object ByteContext: Reification<Byte>, Equality<Byte>, Order<Byte>, Hashing<Byte>, EuclideanRing<Byte> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Byte
    override fun reifyMaybe(element: Any?): Maybe<Byte> = if (element is Byte) Some(element) else None
    override fun reifyOrNull(element: Any?): Byte? = element as? Byte
    override fun reify(element: Any?): Byte = element as? Byte ?: reificationException()
    // endregion
    
    // region Order
    override fun Byte.compareWith(other: Byte): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Byte get() = 0
    override val one: Byte get() = 1
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Byte = arg.toByte()
    override fun valueOf(arg: UInt): Byte = arg.toByte()
    override fun valueOf(arg: Long): Byte = arg.toByte()
    override fun valueOf(arg: ULong): Byte = arg.toByte()
    // endregion
    
    // region Byte-Int operations
    override val numberPlusInt: Plus<Byte, Int, Byte> = Plus { left, right -> (left + right).toByte() }
    override val numberMinusInt: Minus<Byte, Int, Byte> = Minus { left, right -> (left - right).toByte() }
    override val numberTimesInt: Times<Byte, Int, Byte> = Times { left, right -> (left * right).toByte() }
    // endregion
    
    // region Byte-UInt operations
    override val numberPlusUInt: Plus<Byte, UInt, Byte> = Plus { left, right -> (left + right.toByte()).toByte() }
    override val numberMinusUInt: Minus<Byte, UInt, Byte> = Minus { left, right -> (left - right.toByte()).toByte() }
    override val numberTimesUInt: Times<Byte, UInt, Byte> = Times { left, right -> (left * right.toByte()).toByte() }
    // endregion

    // region Byte-Long operations
    override val numberPlusLong: Plus<Byte, Long, Byte> = Plus { left, right -> (left + right).toByte() }
    override val numberMinusLong: Minus<Byte, Long, Byte> = Minus { left, right -> (left - right).toByte() }
    override val numberTimesLong: Times<Byte, Long, Byte> = Times { left, right -> (left * right).toByte() }
    // endregion
    
    // region Byte-ULong operations
    override val numberPlusULong: Plus<Byte, ULong, Byte> = Plus { left, right -> (left + right.toByte()).toByte() }
    override val numberMinusULong: Minus<Byte, ULong, Byte> = Minus { left, right -> (left - right.toByte()).toByte() }
    override val numberTimesULong: Times<Byte, ULong, Byte> = Times { left, right -> (left * right.toByte()).toByte() }
    // endregion
    
    // region Int-Byte operations
    override val intPlusNumber: Plus<Int, Byte, Byte> = Plus { left, right -> (left + right).toByte() }
    override val intMinusNumber: Minus<Int, Byte, Byte> = Minus { left, right -> (left - right).toByte() }
    override val intTimesNumber: Times<Int, Byte, Byte> = Times { left, right -> (left * right).toByte() }
    // endregion
    
    // region UInt-Byte operations
    override val uIntPlusNumber: Plus<UInt, Byte, Byte> = Plus { left, right -> (left.toByte() + right).toByte() }
    override val uIntMinusNumber: Minus<UInt, Byte, Byte> = Minus { left, right -> (left.toByte() - right).toByte() }
    override val uIntTimesNumber: Times<UInt, Byte, Byte> = Times { left, right -> (left.toByte() * right).toByte() }
    // endregion

    // region Long-Byte operations
    override val longPlusNumber: Plus<Long, Byte, Byte> = Plus { left, right -> (left + right).toByte() }
    override val longMinusNumber: Minus<Long, Byte, Byte> = Minus { left, right -> (left - right).toByte() }
    override val longTimesNumber: Times<Long, Byte, Byte> = Times { left, right -> (left * right).toByte() }
    // endregion
    
    // region ULong-Byte operations
    override val uLongPlusNumber: Plus<ULong, Byte, Byte> = Plus { left, right -> (left.toByte() + right).toByte() }
    override val uLongMinusNumber: Minus<ULong, Byte, Byte> = Minus { left, right -> (left.toByte() - right).toByte() }
    override val uLongTimesNumber: Times<ULong, Byte, Byte> = Times { left, right -> (left.toByte() * right).toByte() }
    // endregion
    
    // region Byte-Byte operations
    override val numberUnaryMinus: UnaryMinus<Byte, Byte> = UnaryMinus { (-it).toByte() }
    override val numberPlusNumber: Plus<Byte, Byte, Byte> = Plus { left, right -> (left + right).toByte() }
    override val numberMinusNumber: Minus<Byte, Byte, Byte> = Minus { left, right -> (left - right).toByte() }
    override val numberTimesNumber: Times<Byte, Byte, Byte> = Times { left, right -> (left * right).toByte() }
    override val numberDivideRemainderNumber: DivideRemainder<Byte, Byte, EuclideanDivisionResult<Byte>> =
        DivideRemainder { left, right ->
            if (right == 0.toByte()) divisionByZero()
            else EuclideanDivisionResult(quotient = (left / right).toByte(), remainder = (left % right).toByte())
        }
    override val numberDivideNumber: Divide<Byte, Byte, Byte> = Divide { left, right -> if (right == 0.toByte()) divisionByZero() else (left / right).toByte() }
    override val numberRemainderNumber: Remainder<Byte, Byte, Byte> = Remainder { left, right -> if (right == 0.toByte()) divisionByZero() else (left % right).toByte() }
    // endregion
}

/**
 * Returns the [Byte] reification context (a singleton implementing [Reification] for [Byte]).
 *
 * @return The [Reification] context for [Byte].
 */
public fun Byte.Companion.reification(): Reification<Byte> = ByteContext
/**
 * Returns the [Byte] equality context (a singleton implementing [Equality] for [Byte]).
 *
 * @return The [Equality] context for [Byte].
 */
public fun Byte.Companion.equality(): Equality<Byte> = ByteContext
/**
 * Returns the [Byte] order context (a singleton implementing [Order] for [Byte]).
 *
 * @return The [Order] context for [Byte].
 */
public fun Byte.Companion.order(): Order<Byte> = ByteContext
/**
 * Returns the [Byte] hashing context (a singleton implementing [Hashing] for [Byte]).
 *
 * @return The [Hashing] context for [Byte].
 */
public fun Byte.Companion.hashing(): Hashing<Byte> = ByteContext
/**
 * Returns the [Byte] euclidean ring context (a singleton implementing [EuclideanRing] for [Byte]).
 *
 * @return The [EuclideanRing] context for [Byte].
 */
public fun Byte.Companion.euclideanRing(): EuclideanRing<Byte> = ByteContext
/**
 * Returns the [Byte] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [Byte]).
 *
 * @return The [EuclideanSemiring] context for [Byte].
 */
public fun Byte.Companion.euclideanSemiring(): EuclideanSemiring<Byte> = ByteContext
/**
 * Returns the [Byte] commutative ring context (a singleton implementing [CommutativeRing] for [Byte]).
 *
 * @return The [CommutativeRing] context for [Byte].
 */
public fun Byte.Companion.commutativeRing(): CommutativeRing<Byte> = ByteContext
/**
 * Returns the [Byte] ring context (a singleton implementing [Ring] for [Byte]).
 *
 * @return The [Ring] context for [Byte].
 */
public fun Byte.Companion.ring(): Ring<Byte> = ByteContext
/**
 * Returns the [Byte] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Byte]).
 *
 * @return The [CommutativeSemiring] context for [Byte].
 */
public fun Byte.Companion.commutativeSemiring(): CommutativeSemiring<Byte> = ByteContext
/**
 * Returns the [Byte] semiring context (a singleton implementing [Semiring] for [Byte]).
 *
 * @return The [Semiring] context for [Byte].
 */
public fun Byte.Companion.semiring(): Semiring<Byte> = ByteContext
/**
 * Returns the [Byte] commutative group context (a singleton implementing [CommutativeGroup] for [Byte]).
 *
 * @return The [CommutativeGroup] context for [Byte].
 */
public fun Byte.Companion.commutativeGroup(): CommutativeGroup<Byte> = ByteContext
/**
 * Returns the [Byte] group context (a singleton implementing [Group] for [Byte]).
 *
 * @return The [Group] context for [Byte].
 */
public fun Byte.Companion.group(): Group<Byte> = ByteContext
/**
 * Returns the [Byte] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Byte]).
 *
 * @return The [CommutativeMonoid] context for [Byte].
 */
public fun Byte.Companion.commutativeMonoid(): CommutativeMonoid<Byte> = ByteContext
/**
 * Returns the [Byte] monoid context (a singleton implementing [Monoid] for [Byte]).
 *
 * @return The [Monoid] context for [Byte].
 */
public fun Byte.Companion.monoid(): Monoid<Byte> = ByteContext
/**
 * Returns the [Byte] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Byte]).
 *
 * @return The [CommutativeSemigroup] context for [Byte].
 */
public fun Byte.Companion.commutativeSemigroup(): CommutativeSemigroup<Byte> = ByteContext
/**
 * Returns the [Byte] semigroup context (a singleton implementing [Semigroup] for [Byte]).
 *
 * @return The [Semigroup] context for [Byte].
 */
public fun Byte.Companion.semigroup(): Semigroup<Byte> = ByteContext

/**
 * Registers the [Reification] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setReification() {
    Reification.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Equality] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setEquality() {
    Equality.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Order] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setOrder() {
    Order.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Hashing] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setHashing() {
    Hashing.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [EuclideanRing] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setEuclideanRing() {
    EuclideanRing.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [EuclideanSemiring] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [CommutativeRing] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeRing() {
    CommutativeRing.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Ring] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setRing() {
    Ring.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [CommutativeSemiring] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Semiring] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setSemiring() {
    Semiring.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [CommutativeGroup] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Group] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setGroup() {
    Group.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [CommutativeMonoid] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Monoid] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setMonoid() {
    Monoid.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [CommutativeSemigroup] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}
/**
 * Registers the [Semigroup] context for [Byte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Byte.Companion.setSemigroup() {
    Semigroup.Key<Byte>().withImpliedUsingFirst correspondsTo ByteContext
}

private data object ShortContext: Reification<Short>, Equality<Short>, Order<Short>, Hashing<Short>, EuclideanRing<Short> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Short
    override fun reifyMaybe(element: Any?): Maybe<Short> = if (element is Short) Some(element) else None
    override fun reifyOrNull(element: Any?): Short? = element as? Short
    override fun reify(element: Any?): Short = element as? Short ?: reificationException()
    // endregion
    
    // region Order
    override fun Short.compareWith(other: Short): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: Short get() = 0
    override val one: Short get() = 1
    // endregion
    
    // region Conversion
    override fun valueOf(arg: Int): Short = arg.toShort()
    override fun valueOf(arg: UInt): Short = arg.toShort()
    override fun valueOf(arg: Long): Short = arg.toShort()
    override fun valueOf(arg: ULong): Short = arg.toShort()
    // endregion
    
    // region Short-Int operations
    override val numberPlusInt: Plus<Short, Int, Short> = Plus { left, right -> (left + right).toShort() }
    override val numberMinusInt: Minus<Short, Int, Short> = Minus { left, right -> (left - right).toShort() }
    override val numberTimesInt: Times<Short, Int, Short> = Times { left, right -> (left * right).toShort() }
    // endregion
    
    // region Short-UInt operations
    override val numberPlusUInt: Plus<Short, UInt, Short> = Plus { left, right -> (left + right.toShort()).toShort() }
    override val numberMinusUInt: Minus<Short, UInt, Short> = Minus { left, right -> (left - right.toShort()).toShort() }
    override val numberTimesUInt: Times<Short, UInt, Short> = Times { left, right -> (left * right.toShort()).toShort() }
    // endregion
    
    // region Short-Long operations
    override val numberPlusLong: Plus<Short, Long, Short> = Plus { left, right -> (left + right).toShort() }
    override val numberMinusLong: Minus<Short, Long, Short> = Minus { left, right -> (left - right).toShort() }
    override val numberTimesLong: Times<Short, Long, Short> = Times { left, right -> (left * right).toShort() }
    // endregion
    
    // region Short-ULong operations
    override val numberPlusULong: Plus<Short, ULong, Short> = Plus { left, right -> (left + right.toShort()).toShort() }
    override val numberMinusULong: Minus<Short, ULong, Short> = Minus { left, right -> (left - right.toShort()).toShort() }
    override val numberTimesULong: Times<Short, ULong, Short> = Times { left, right -> (left * right.toShort()).toShort() }
    // endregion
    
    // region Int-Short operations
    override val intPlusNumber: Plus<Int, Short, Short> = Plus { left, right -> (left + right).toShort() }
    override val intMinusNumber: Minus<Int, Short, Short> = Minus { left, right -> (left - right).toShort() }
    override val intTimesNumber: Times<Int, Short, Short> = Times { left, right -> (left * right).toShort() }
    // endregion
    
    // region UInt-Short operations
    override val uIntPlusNumber: Plus<UInt, Short, Short> = Plus { left, right -> (left.toShort() + right).toShort() }
    override val uIntMinusNumber: Minus<UInt, Short, Short> = Minus { left, right -> (left.toShort() - right).toShort() }
    override val uIntTimesNumber: Times<UInt, Short, Short> = Times { left, right -> (left.toShort() * right).toShort() }
    // endregion
    
    // region Long-Short operations
    override val longPlusNumber: Plus<Long, Short, Short> = Plus { left, right -> (left + right).toShort() }
    override val longMinusNumber: Minus<Long, Short, Short> = Minus { left, right -> (left - right).toShort() }
    override val longTimesNumber: Times<Long, Short, Short> = Times { left, right -> (left * right).toShort() }
    // endregion
    
    // region ULong-Short operations
    override val uLongPlusNumber: Plus<ULong, Short, Short> = Plus { left, right -> (left.toShort() + right).toShort() }
    override val uLongMinusNumber: Minus<ULong, Short, Short> = Minus { left, right -> (left.toShort() - right).toShort() }
    override val uLongTimesNumber: Times<ULong, Short, Short> = Times { left, right -> (left.toShort() * right).toShort() }
    // endregion
    
    // region Short-Short operations
    override val numberUnaryMinus: UnaryMinus<Short, Short> = UnaryMinus { (-it).toShort() }
    override val numberPlusNumber: Plus<Short, Short, Short> = Plus { left, right -> (left + right).toShort() }
    override val numberMinusNumber: Minus<Short, Short, Short> = Minus { left, right -> (left - right).toShort() }
    override val numberTimesNumber: Times<Short, Short, Short> = Times { left, right -> (left * right).toShort() }
    override val numberDivideRemainderNumber: DivideRemainder<Short, Short, EuclideanDivisionResult<Short>> =
        DivideRemainder { left, right ->
            if (right == 0.toShort()) divisionByZero()
            else EuclideanDivisionResult(quotient = (left / right).toShort(), remainder = (left % right).toShort())
        }
    override val numberDivideNumber: Divide<Short, Short, Short> = Divide { left, right -> if (right == 0.toShort()) divisionByZero() else (left / right).toShort() }
    override val numberRemainderNumber: Remainder<Short, Short, Short> = Remainder { left, right -> if (right == 0.toShort()) divisionByZero() else (left % right).toShort() }
    // endregion
}

/**
 * Returns the [Short] reification context (a singleton implementing [Reification] for [Short]).
 *
 * @return The [Reification] context for [Short].
 */
public fun Short.Companion.reification(): Reification<Short> = ShortContext
/**
 * Returns the [Short] equality context (a singleton implementing [Equality] for [Short]).
 *
 * @return The [Equality] context for [Short].
 */
public fun Short.Companion.equality(): Equality<Short> = ShortContext
/**
 * Returns the [Short] order context (a singleton implementing [Order] for [Short]).
 *
 * @return The [Order] context for [Short].
 */
public fun Short.Companion.order(): Order<Short> = ShortContext
/**
 * Returns the [Short] hashing context (a singleton implementing [Hashing] for [Short]).
 *
 * @return The [Hashing] context for [Short].
 */
public fun Short.Companion.hashing(): Hashing<Short> = ShortContext
/**
 * Returns the [Short] euclidean ring context (a singleton implementing [EuclideanRing] for [Short]).
 *
 * @return The [EuclideanRing] context for [Short].
 */
public fun Short.Companion.euclideanRing(): EuclideanRing<Short> = ShortContext
/**
 * Returns the [Short] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [Short]).
 *
 * @return The [EuclideanSemiring] context for [Short].
 */
public fun Short.Companion.euclideanSemiring(): EuclideanSemiring<Short> = ShortContext
/**
 * Returns the [Short] commutative ring context (a singleton implementing [CommutativeRing] for [Short]).
 *
 * @return The [CommutativeRing] context for [Short].
 */
public fun Short.Companion.commutativeRing(): CommutativeRing<Short> = ShortContext
/**
 * Returns the [Short] ring context (a singleton implementing [Ring] for [Short]).
 *
 * @return The [Ring] context for [Short].
 */
public fun Short.Companion.ring(): Ring<Short> = ShortContext
/**
 * Returns the [Short] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Short]).
 *
 * @return The [CommutativeSemiring] context for [Short].
 */
public fun Short.Companion.commutativeSemiring(): CommutativeSemiring<Short> = ShortContext
/**
 * Returns the [Short] semiring context (a singleton implementing [Semiring] for [Short]).
 *
 * @return The [Semiring] context for [Short].
 */
public fun Short.Companion.semiring(): Semiring<Short> = ShortContext
/**
 * Returns the [Short] commutative group context (a singleton implementing [CommutativeGroup] for [Short]).
 *
 * @return The [CommutativeGroup] context for [Short].
 */
public fun Short.Companion.commutativeGroup(): CommutativeGroup<Short> = ShortContext
/**
 * Returns the [Short] group context (a singleton implementing [Group] for [Short]).
 *
 * @return The [Group] context for [Short].
 */
public fun Short.Companion.group(): Group<Short> = ShortContext
/**
 * Returns the [Short] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Short]).
 *
 * @return The [CommutativeMonoid] context for [Short].
 */
public fun Short.Companion.commutativeMonoid(): CommutativeMonoid<Short> = ShortContext
/**
 * Returns the [Short] monoid context (a singleton implementing [Monoid] for [Short]).
 *
 * @return The [Monoid] context for [Short].
 */
public fun Short.Companion.monoid(): Monoid<Short> = ShortContext
/**
 * Returns the [Short] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Short]).
 *
 * @return The [CommutativeSemigroup] context for [Short].
 */
public fun Short.Companion.commutativeSemigroup(): CommutativeSemigroup<Short> = ShortContext
/**
 * Returns the [Short] semigroup context (a singleton implementing [Semigroup] for [Short]).
 *
 * @return The [Semigroup] context for [Short].
 */
public fun Short.Companion.semigroup(): Semigroup<Short> = ShortContext

/**
 * Registers the [Reification] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setReification() {
    Reification.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Equality] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setEquality() {
    Equality.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Order] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setOrder() {
    Order.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Hashing] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setHashing() {
    Hashing.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [EuclideanRing] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setEuclideanRing() {
    EuclideanRing.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [EuclideanSemiring] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [CommutativeRing] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeRing() {
    CommutativeRing.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Ring] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setRing() {
    Ring.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [CommutativeSemiring] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Semiring] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setSemiring() {
    Semiring.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [CommutativeGroup] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Group] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setGroup() {
    Group.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [CommutativeMonoid] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Monoid] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setMonoid() {
    Monoid.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [CommutativeSemigroup] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}
/**
 * Registers the [Semigroup] context for [Short] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Short.Companion.setSemigroup() {
    Semigroup.Key<Short>().withImpliedUsingFirst correspondsTo ShortContext
}

private data object IntContext: Reification<Int>, Equality<Int>, Order<Int>, Hashing<Int>, EuclideanRing<Int> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Int
    override fun reifyMaybe(element: Any?): Maybe<Int> = if (element is Int) Some(element) else None
    override fun reifyOrNull(element: Any?): Int? = element as? Int
    override fun reify(element: Any?): Int = element as? Int ?: reificationException()
    // endregion
    
    // region Order
    override fun Int.compareWith(other: Int): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: Int get() = 0
    override val one: Int get() = 1
    // endregion
    
    // region Conversion
    override fun valueOf(arg: Int): Int = arg
    override fun valueOf(arg: UInt): Int = arg.toInt()
    override fun valueOf(arg: Long): Int = arg.toInt()
    override fun valueOf(arg: ULong): Int = arg.toInt()
    // endregion
    
    // region Int-Int operations
    override val numberPlusInt: Plus<Int, Int, Int> = Plus { left, right -> (left + right) }
    override val numberMinusInt: Minus<Int, Int, Int> = Minus { left, right -> (left - right) }
    override val numberTimesInt: Times<Int, Int, Int> = Times { left, right -> (left * right) }
    // endregion
    
    // region Int-UInt operations
    override val numberPlusUInt: Plus<Int, UInt, Int> = Plus { left, right -> (left + right.toInt()) }
    override val numberMinusUInt: Minus<Int, UInt, Int> = Minus { left, right -> (left - right.toInt()) }
    override val numberTimesUInt: Times<Int, UInt, Int> = Times { left, right -> (left * right.toInt()) }
    // endregion
    
    // region Int-Long operations
    override val numberPlusLong: Plus<Int, Long, Int> = Plus { left, right -> (left + right).toInt() }
    override val numberMinusLong: Minus<Int, Long, Int> = Minus { left, right -> (left - right).toInt() }
    override val numberTimesLong: Times<Int, Long, Int> = Times { left, right -> (left * right).toInt() }
    // endregion
    
    // region Int-ULong operations
    override val numberPlusULong: Plus<Int, ULong, Int> = Plus { left, right -> (left + right.toInt()) }
    override val numberMinusULong: Minus<Int, ULong, Int> = Minus { left, right -> (left - right.toInt()) }
    override val numberTimesULong: Times<Int, ULong, Int> = Times { left, right -> (left * right.toInt()) }
    // endregion
    
    // region Int-Int operations
    override val intPlusNumber: Plus<Int, Int, Int> = Plus { left, right -> (left + right) }
    override val intMinusNumber: Minus<Int, Int, Int> = Minus { left, right -> (left - right) }
    override val intTimesNumber: Times<Int, Int, Int> = Times { left, right -> (left * right) }
    // endregion
    
    // region UInt-Int operations
    override val uIntPlusNumber: Plus<UInt, Int, Int> = Plus { left, right -> (left.toInt() + right) }
    override val uIntMinusNumber: Minus<UInt, Int, Int> = Minus { left, right -> (left.toInt() - right) }
    override val uIntTimesNumber: Times<UInt, Int, Int> = Times { left, right -> (left.toInt() * right) }
    // endregion
    
    // region Long-Int operations
    override val longPlusNumber: Plus<Long, Int, Int> = Plus { left, right -> (left + right).toInt() }
    override val longMinusNumber: Minus<Long, Int, Int> = Minus { left, right -> (left - right).toInt() }
    override val longTimesNumber: Times<Long, Int, Int> = Times { left, right -> (left * right).toInt() }
    // endregion
    
    // region ULong-Int operations
    override val uLongPlusNumber: Plus<ULong, Int, Int> = Plus { left, right -> (left.toInt() + right) }
    override val uLongMinusNumber: Minus<ULong, Int, Int> = Minus { left, right -> (left.toInt() - right) }
    override val uLongTimesNumber: Times<ULong, Int, Int> = Times { left, right -> (left.toInt() * right) }
    // endregion
    
    // region Int-Int operations
    override val numberUnaryMinus: UnaryMinus<Int, Int> = UnaryMinus { -it }
    override val numberPlusNumber: Plus<Int, Int, Int> = Plus { left, right -> (left + right) }
    override val numberMinusNumber: Minus<Int, Int, Int> = Minus { left, right -> (left - right) }
    override val numberTimesNumber: Times<Int, Int, Int> = Times { left, right -> (left * right) }
    override val numberDivideRemainderNumber: DivideRemainder<Int, Int, EuclideanDivisionResult<Int>> =
        DivideRemainder { left, right ->
            if (right == 0.toInt()) divisionByZero()
            else EuclideanDivisionResult(quotient = (left / right), remainder = (left % right))
        }
    override val numberDivideNumber: Divide<Int, Int, Int> = Divide { left, right -> if (right == 0.toInt()) divisionByZero() else (left / right) }
    override val numberRemainderNumber: Remainder<Int, Int, Int> = Remainder { left, right -> if (right == 0.toInt()) divisionByZero() else (left % right) }
    // endregion
}

/**
 * Returns the [Int] reification context (a singleton implementing [Reification] for [Int]).
 *
 * @return The [Reification] context for [Int].
 */
public fun Int.Companion.reification(): Reification<Int> = IntContext
/**
 * Returns the [Int] equality context (a singleton implementing [Equality] for [Int]).
 *
 * @return The [Equality] context for [Int].
 */
public fun Int.Companion.equality(): Equality<Int> = IntContext
/**
 * Returns the [Int] order context (a singleton implementing [Order] for [Int]).
 *
 * @return The [Order] context for [Int].
 */
public fun Int.Companion.order(): Order<Int> = IntContext
/**
 * Returns the [Int] hashing context (a singleton implementing [Hashing] for [Int]).
 *
 * @return The [Hashing] context for [Int].
 */
public fun Int.Companion.hashing(): Hashing<Int> = IntContext
/**
 * Returns the [Int] euclidean ring context (a singleton implementing [EuclideanRing] for [Int]).
 *
 * @return The [EuclideanRing] context for [Int].
 */
public fun Int.Companion.euclideanRing(): EuclideanRing<Int> = IntContext
/**
 * Returns the [Int] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [Int]).
 *
 * @return The [EuclideanSemiring] context for [Int].
 */
public fun Int.Companion.euclideanSemiring(): EuclideanSemiring<Int> = IntContext
/**
 * Returns the [Int] commutative ring context (a singleton implementing [CommutativeRing] for [Int]).
 *
 * @return The [CommutativeRing] context for [Int].
 */
public fun Int.Companion.commutativeRing(): CommutativeRing<Int> = IntContext
/**
 * Returns the [Int] ring context (a singleton implementing [Ring] for [Int]).
 *
 * @return The [Ring] context for [Int].
 */
public fun Int.Companion.ring(): Ring<Int> = IntContext
/**
 * Returns the [Int] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Int]).
 *
 * @return The [CommutativeSemiring] context for [Int].
 */
public fun Int.Companion.commutativeSemiring(): CommutativeSemiring<Int> = IntContext
/**
 * Returns the [Int] semiring context (a singleton implementing [Semiring] for [Int]).
 *
 * @return The [Semiring] context for [Int].
 */
public fun Int.Companion.semiring(): Semiring<Int> = IntContext
/**
 * Returns the [Int] commutative group context (a singleton implementing [CommutativeGroup] for [Int]).
 *
 * @return The [CommutativeGroup] context for [Int].
 */
public fun Int.Companion.commutativeGroup(): CommutativeGroup<Int> = IntContext
/**
 * Returns the [Int] group context (a singleton implementing [Group] for [Int]).
 *
 * @return The [Group] context for [Int].
 */
public fun Int.Companion.group(): Group<Int> = IntContext
/**
 * Returns the [Int] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Int]).
 *
 * @return The [CommutativeMonoid] context for [Int].
 */
public fun Int.Companion.commutativeMonoid(): CommutativeMonoid<Int> = IntContext
/**
 * Returns the [Int] monoid context (a singleton implementing [Monoid] for [Int]).
 *
 * @return The [Monoid] context for [Int].
 */
public fun Int.Companion.monoid(): Monoid<Int> = IntContext
/**
 * Returns the [Int] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Int]).
 *
 * @return The [CommutativeSemigroup] context for [Int].
 */
public fun Int.Companion.commutativeSemigroup(): CommutativeSemigroup<Int> = IntContext
/**
 * Returns the [Int] semigroup context (a singleton implementing [Semigroup] for [Int]).
 *
 * @return The [Semigroup] context for [Int].
 */
public fun Int.Companion.semigroup(): Semigroup<Int> = IntContext

/**
 * Registers the [Reification] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setReification() {
    Reification.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Equality] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setEquality() {
    Equality.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Order] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setOrder() {
    Order.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Hashing] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setHashing() {
    Hashing.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [EuclideanRing] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setEuclideanRing() {
    EuclideanRing.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [EuclideanSemiring] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [CommutativeRing] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeRing() {
    CommutativeRing.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Ring] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setRing() {
    Ring.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [CommutativeSemiring] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Semiring] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setSemiring() {
    Semiring.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [CommutativeGroup] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Group] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setGroup() {
    Group.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [CommutativeMonoid] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Monoid] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setMonoid() {
    Monoid.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [CommutativeSemigroup] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}
/**
 * Registers the [Semigroup] context for [Int] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Int.Companion.setSemigroup() {
    Semigroup.Key<Int>().withImpliedUsingFirst correspondsTo IntContext
}

private data object LongContext: Reification<Long>, Equality<Long>, Order<Long>, Hashing<Long>, EuclideanRing<Long> {
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
    override val zero: Long get() = 0
    override val one: Long get() = 1
    // endregion
    
    // region Conversion
    override fun valueOf(arg: Int): Long = arg.toLong()
    override fun valueOf(arg: UInt): Long = arg.toLong()
    override fun valueOf(arg: Long): Long = arg
    override fun valueOf(arg: ULong): Long = arg.toLong()
    // endregion
    
    // region Long-Int operations
    override val numberPlusInt: Plus<Long, Int, Long> = Plus { left, right -> (left + right) }
    override val numberMinusInt: Minus<Long, Int, Long> = Minus { left, right -> (left - right) }
    override val numberTimesInt: Times<Long, Int, Long> = Times { left, right -> (left * right) }
    // endregion
    
    // region Long-UInt operations
    override val numberPlusUInt: Plus<Long, UInt, Long> = Plus { left, right -> (left + right.toLong()) }
    override val numberMinusUInt: Minus<Long, UInt, Long> = Minus { left, right -> (left - right.toLong()) }
    override val numberTimesUInt: Times<Long, UInt, Long> = Times { left, right -> (left * right.toLong()) }
    // endregion
    
    // region Long-Long operations
    override val numberPlusLong: Plus<Long, Long, Long> = Plus { left, right -> (left + right) }
    override val numberMinusLong: Minus<Long, Long, Long> = Minus { left, right -> (left - right) }
    override val numberTimesLong: Times<Long, Long, Long> = Times { left, right -> (left * right) }
    // endregion
    
    // region Long-ULong operations
    override val numberPlusULong: Plus<Long, ULong, Long> = Plus { left, right -> (left + right.toLong()) }
    override val numberMinusULong: Minus<Long, ULong, Long> = Minus { left, right -> (left - right.toLong()) }
    override val numberTimesULong: Times<Long, ULong, Long> = Times { left, right -> (left * right.toLong()) }
    // endregion
    
    // region Int-Long operations
    override val intPlusNumber: Plus<Int, Long, Long> = Plus { left, right -> (left + right) }
    override val intMinusNumber: Minus<Int, Long, Long> = Minus { left, right -> (left - right) }
    override val intTimesNumber: Times<Int, Long, Long> = Times { left, right -> (left * right) }
    // endregion
    
    // region UInt-Long operations
    override val uIntPlusNumber: Plus<UInt, Long, Long> = Plus { left, right -> (left.toLong() + right) }
    override val uIntMinusNumber: Minus<UInt, Long, Long> = Minus { left, right -> (left.toLong() - right) }
    override val uIntTimesNumber: Times<UInt, Long, Long> = Times { left, right -> (left.toLong() * right) }
    // endregion
    
    // region Long-Long operations
    override val longPlusNumber: Plus<Long, Long, Long> = Plus { left, right -> (left + right) }
    override val longMinusNumber: Minus<Long, Long, Long> = Minus { left, right -> (left - right) }
    override val longTimesNumber: Times<Long, Long, Long> = Times { left, right -> (left * right) }
    // endregion
    
    // region ULong-Long operations
    override val uLongPlusNumber: Plus<ULong, Long, Long> = Plus { left, right -> (left.toLong() + right) }
    override val uLongMinusNumber: Minus<ULong, Long, Long> = Minus { left, right -> (left.toLong() - right) }
    override val uLongTimesNumber: Times<ULong, Long, Long> = Times { left, right -> (left.toLong() * right) }
    // endregion
    
    // region Long-Long operations
    override val numberUnaryMinus: UnaryMinus<Long, Long> = UnaryMinus { it }
    override val numberPlusNumber: Plus<Long, Long, Long> = Plus { left, right -> (left + right) }
    override val numberMinusNumber: Minus<Long, Long, Long> = Minus { left, right -> (left - right) }
    override val numberTimesNumber: Times<Long, Long, Long> = Times { left, right -> (left * right) }
    override val numberDivideRemainderNumber: DivideRemainder<Long, Long, EuclideanDivisionResult<Long>> =
        DivideRemainder { left, right ->
            if (right == 0.toLong()) divisionByZero()
            else EuclideanDivisionResult(quotient = (left / right), remainder = (left % right))
        }
    override val numberDivideNumber: Divide<Long, Long, Long> = Divide { left, right -> if (right == 0.toLong()) divisionByZero() else (left / right) }
    override val numberRemainderNumber: Remainder<Long, Long, Long> = Remainder { left, right -> if (right == 0.toLong()) divisionByZero() else (left % right) }
    // endregion
}

/**
 * Returns the [Long] reification context (a singleton implementing [Reification] for [Long]).
 *
 * @return The [Reification] context for [Long].
 */
public fun Long.Companion.reification(): Reification<Long> = LongContext
/**
 * Returns the [Long] equality context (a singleton implementing [Equality] for [Long]).
 *
 * @return The [Equality] context for [Long].
 */
public fun Long.Companion.equality(): Equality<Long> = LongContext
/**
 * Returns the [Long] order context (a singleton implementing [Order] for [Long]).
 *
 * @return The [Order] context for [Long].
 */
public fun Long.Companion.order(): Order<Long> = LongContext
/**
 * Returns the [Long] hashing context (a singleton implementing [Hashing] for [Long]).
 *
 * @return The [Hashing] context for [Long].
 */
public fun Long.Companion.hashing(): Hashing<Long> = LongContext
/**
 * Returns the [Long] euclidean ring context (a singleton implementing [EuclideanRing] for [Long]).
 *
 * @return The [EuclideanRing] context for [Long].
 */
public fun Long.Companion.euclideanRing(): EuclideanRing<Long> = LongContext
/**
 * Returns the [Long] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [Long]).
 *
 * @return The [EuclideanSemiring] context for [Long].
 */
public fun Long.Companion.euclideanSemiring(): EuclideanSemiring<Long> = LongContext
/**
 * Returns the [Long] commutative ring context (a singleton implementing [CommutativeRing] for [Long]).
 *
 * @return The [CommutativeRing] context for [Long].
 */
public fun Long.Companion.commutativeRing(): CommutativeRing<Long> = LongContext
/**
 * Returns the [Long] ring context (a singleton implementing [Ring] for [Long]).
 *
 * @return The [Ring] context for [Long].
 */
public fun Long.Companion.ring(): Ring<Long> = LongContext
/**
 * Returns the [Long] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Long]).
 *
 * @return The [CommutativeSemiring] context for [Long].
 */
public fun Long.Companion.commutativeSemiring(): CommutativeSemiring<Long> = LongContext
/**
 * Returns the [Long] semiring context (a singleton implementing [Semiring] for [Long]).
 *
 * @return The [Semiring] context for [Long].
 */
public fun Long.Companion.semiring(): Semiring<Long> = LongContext
/**
 * Returns the [Long] commutative group context (a singleton implementing [CommutativeGroup] for [Long]).
 *
 * @return The [CommutativeGroup] context for [Long].
 */
public fun Long.Companion.commutativeGroup(): CommutativeGroup<Long> = LongContext
/**
 * Returns the [Long] group context (a singleton implementing [Group] for [Long]).
 *
 * @return The [Group] context for [Long].
 */
public fun Long.Companion.group(): Group<Long> = LongContext
/**
 * Returns the [Long] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Long]).
 *
 * @return The [CommutativeMonoid] context for [Long].
 */
public fun Long.Companion.commutativeMonoid(): CommutativeMonoid<Long> = LongContext
/**
 * Returns the [Long] monoid context (a singleton implementing [Monoid] for [Long]).
 *
 * @return The [Monoid] context for [Long].
 */
public fun Long.Companion.monoid(): Monoid<Long> = LongContext
/**
 * Returns the [Long] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Long]).
 *
 * @return The [CommutativeSemigroup] context for [Long].
 */
public fun Long.Companion.commutativeSemigroup(): CommutativeSemigroup<Long> = LongContext
/**
 * Returns the [Long] semigroup context (a singleton implementing [Semigroup] for [Long]).
 *
 * @return The [Semigroup] context for [Long].
 */
public fun Long.Companion.semigroup(): Semigroup<Long> = LongContext

/**
 * Registers the [Reification] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setReification() {
    Reification.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Equality] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setEquality() {
    Equality.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Order] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setOrder() {
    Order.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Hashing] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setHashing() {
    Hashing.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [EuclideanRing] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setEuclideanRing() {
    EuclideanRing.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [EuclideanSemiring] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [CommutativeRing] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeRing() {
    CommutativeRing.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Ring] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setRing() {
    Ring.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [CommutativeSemiring] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Semiring] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSemiring() {
    Semiring.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [CommutativeGroup] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Group] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setGroup() {
    Group.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [CommutativeMonoid] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Monoid] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setMonoid() {
    Monoid.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [CommutativeSemigroup] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}
/**
 * Registers the [Semigroup] context for [Long] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSemigroup() {
    Semigroup.Key<Long>().withImpliedUsingFirst correspondsTo LongContext
}

private data object UByteContext: Reification<UByte>, Equality<UByte>, Order<UByte>, Hashing<UByte>, EuclideanSemiring<UByte>, ExtendedSemiring<UByte> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UByte
    override fun reifyMaybe(element: Any?): Maybe<UByte> = if (element is UByte) Some(element) else None
    override fun reifyOrNull(element: Any?): UByte? = element as? UByte
    override fun reify(element: Any?): UByte = element as? UByte ?: reificationException()
    // endregion
    
    // region Order
    override fun UByte.compareWith(other: UByte): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: UByte get() = 0.toUByte()
    override val one: UByte get() = 1.toUByte()
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): UByte = arg.toUByte()
    override fun valueOf(arg: ULong): UByte = arg.toUByte()
    // endregion
    
    // region UByte-UInt operations
    override val numberPlusUInt: Plus<UByte, UInt, UByte> = Plus { left, right -> (left + right).toUByte() }
    override val numberMinusUInt: Minus<UByte, UInt, UByte> = Minus { left, right -> (left - right).toUByte() }
    override val numberTimesUInt: Times<UByte, UInt, UByte> = Times { left, right -> (left * right).toUByte() }
    // endregion
    
    // region UByte-ULong operations
    override val numberPlusULong: Plus<UByte, ULong, UByte> = Plus { left, right -> (left + right).toUByte() }
    override val numberMinusULong: Minus<UByte, ULong, UByte> = Minus { left, right -> (left - right).toUByte() }
    override val numberTimesULong: Times<UByte, ULong, UByte> = Times { left, right -> (left * right).toUByte() }
    // endregion
    
    // region UInt-UByte operations
    override val uIntPlusNumber: Plus<UInt, UByte, UByte> = Plus { left, right -> (left + right).toUByte() }
    override val uIntMinusNumber: Minus<UInt, UByte, UByte> = Minus { left, right -> (left - right).toUByte() }
    override val uIntTimesNumber: Times<UInt, UByte, UByte> = Times { left, right -> (left * right).toUByte() }
    // endregion
    
    // region ULong-UByte operations
    override val uLongPlusNumber: Plus<ULong, UByte, UByte> = Plus { left, right -> (left + right).toUByte() }
    override val uLongMinusNumber: Minus<ULong, UByte, UByte> = Minus { left, right -> (left - right).toUByte() }
    override val uLongTimesNumber: Times<ULong, UByte, UByte> = Times { left, right -> (left * right).toUByte() }
    // endregion
    
    // region UByte-UByte operations
    override val numberPlusNumber: Plus<UByte, UByte, UByte> = Plus { left, right -> (left + right).toUByte() }
    override val numberMinusNumber: Minus<UByte, UByte, UByte> = Minus { left, right -> (left - right).toUByte() }
    override val numberTimesNumber: Times<UByte, UByte, UByte> = Times { left, right -> (left * right).toUByte() }
    override val numberDivideRemainderNumber: DivideRemainder<UByte, UByte, EuclideanDivisionResult<UByte>> = DivideRemainder { left, right ->
        if (right == 0.toUByte()) divisionByZero()
        else EuclideanDivisionResult(
            quotient = (left / right).toUByte(),
            remainder = (left % right).toUByte(),
        )
    }
    override val numberDivideNumber: Divide<UByte, UByte, UByte> = Divide { left, right -> if (right == 0.toUByte()) divisionByZero() else (left / right).toUByte() }
    override val numberRemainderNumber: Remainder<UByte, UByte, UByte> = Remainder { left, right -> if (right == 0.toUByte()) divisionByZero() else (left % right).toUByte() }
    override val powerNumberUInt: Power<UByte, UInt, UByte> = Power { base, exponent -> base squaringPower exponent }
    override val powerNumberULong: Power<UByte, ULong, UByte> = Power { base, exponent -> base squaringPower exponent }
    // endregion
}

/**
 * Returns the [UByte] reification context (a singleton implementing [Reification] for [UByte]).
 *
 * @return The [Reification] context for [UByte].
 */
public fun UByte.Companion.reification(): Reification<UByte> = UByteContext
/**
 * Returns the [UByte] equality context (a singleton implementing [Equality] for [UByte]).
 *
 * @return The [Equality] context for [UByte].
 */
public fun UByte.Companion.equality(): Equality<UByte> = UByteContext
/**
 * Returns the [UByte] order context (a singleton implementing [Order] for [UByte]).
 *
 * @return The [Order] context for [UByte].
 */
public fun UByte.Companion.order(): Order<UByte> = UByteContext
/**
 * Returns the [UByte] hashing context (a singleton implementing [Hashing] for [UByte]).
 *
 * @return The [Hashing] context for [UByte].
 */
public fun UByte.Companion.hashing(): Hashing<UByte> = UByteContext
/**
 * Returns the [UByte] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [UByte]).
 *
 * @return The [EuclideanSemiring] context for [UByte].
 */
public fun UByte.Companion.euclideanSemiring(): EuclideanSemiring<UByte> = UByteContext
/**
 * Returns the [UByte] commutative semiring context (a singleton implementing [CommutativeSemiring] for [UByte]).
 *
 * @return The [CommutativeSemiring] context for [UByte].
 */
public fun UByte.Companion.commutativeSemiring(): CommutativeSemiring<UByte> = UByteContext
/**
 * Returns the [UByte] semiring context (a singleton implementing [Semiring] for [UByte]).
 *
 * @return The [Semiring] context for [UByte].
 */
public fun UByte.Companion.semiring(): Semiring<UByte> = UByteContext
/**
 * Returns the [UByte] commutative monoid context (a singleton implementing [CommutativeMonoid] for [UByte]).
 *
 * @return The [CommutativeMonoid] context for [UByte].
 */
public fun UByte.Companion.commutativeMonoid(): CommutativeMonoid<UByte> = UByteContext
/**
 * Returns the [UByte] monoid context (a singleton implementing [Monoid] for [UByte]).
 *
 * @return The [Monoid] context for [UByte].
 */
public fun UByte.Companion.monoid(): Monoid<UByte> = UByteContext
/**
 * Returns the [UByte] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [UByte]).
 *
 * @return The [CommutativeSemigroup] context for [UByte].
 */
public fun UByte.Companion.commutativeSemigroup(): CommutativeSemigroup<UByte> = UByteContext
/**
 * Returns the [UByte] semigroup context (a singleton implementing [Semigroup] for [UByte]).
 *
 * @return The [Semigroup] context for [UByte].
 */
public fun UByte.Companion.semigroup(): Semigroup<UByte> = UByteContext

/**
 * Registers the [Reification] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setReification() {
    Reification.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [Equality] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setEquality() {
    Equality.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [Order] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setOrder() {
    Order.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [Hashing] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setHashing() {
    Hashing.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [EuclideanSemiring] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [CommutativeSemiring] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [Semiring] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setSemiring() {
    Semiring.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [CommutativeMonoid] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [Monoid] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setMonoid() {
    Monoid.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [CommutativeSemigroup] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}
/**
 * Registers the [Semigroup] context for [UByte] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UByte.Companion.setSemigroup() {
    Semigroup.Key<UByte>().withImpliedUsingFirst correspondsTo UByteContext
}

private data object UShortContext: Reification<UShort>, Equality<UShort>, Order<UShort>, Hashing<UShort>, EuclideanSemiring<UShort>, ExtendedSemiring<UShort> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UShort
    override fun reifyMaybe(element: Any?): Maybe<UShort> = if (element is UShort) Some(element) else None
    override fun reifyOrNull(element: Any?): UShort? = element as? UShort
    override fun reify(element: Any?): UShort = element as? UShort ?: reificationException()
    // endregion
    
    // region Order
    override fun UShort.compareWith(other: UShort): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: UShort get() = 0.toUShort()
    override val one: UShort get() = 1.toUShort()
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): UShort = arg.toUShort()
    override fun valueOf(arg: ULong): UShort = arg.toUShort()
    // endregion
    
    // region UShort-UInt operations
    override val numberPlusUInt: Plus<UShort, UInt, UShort> = Plus { left, right -> (left + right).toUShort() }
    override val numberMinusUInt: Minus<UShort, UInt, UShort> = Minus { left, right -> (left - right).toUShort() }
    override val numberTimesUInt: Times<UShort, UInt, UShort> = Times { left, right -> (left * right).toUShort() }
    // endregion
    
    // region UShort-ULong operations
    override val numberPlusULong: Plus<UShort, ULong, UShort> = Plus { left, right -> (left + right).toUShort() }
    override val numberMinusULong: Minus<UShort, ULong, UShort> = Minus { left, right -> (left - right).toUShort() }
    override val numberTimesULong: Times<UShort, ULong, UShort> = Times { left, right -> (left * right).toUShort() }
    // endregion
    
    // region UInt-UShort operations
    override val uIntPlusNumber: Plus<UInt, UShort, UShort> = Plus { left, right -> (left + right).toUShort() }
    override val uIntMinusNumber: Minus<UInt, UShort, UShort> = Minus { left, right -> (left - right).toUShort() }
    override val uIntTimesNumber: Times<UInt, UShort, UShort> = Times { left, right -> (left * right).toUShort() }
    // endregion
    
    // region ULong-UShort operations
    override val uLongPlusNumber: Plus<ULong, UShort, UShort> = Plus { left, right -> (left + right).toUShort() }
    override val uLongMinusNumber: Minus<ULong, UShort, UShort> = Minus { left, right -> (left - right).toUShort() }
    override val uLongTimesNumber: Times<ULong, UShort, UShort> = Times { left, right -> (left * right).toUShort() }
    // endregion
    
    // region UShort-UShort operations
    override val numberPlusNumber: Plus<UShort, UShort, UShort> = Plus { left, right -> (left + right).toUShort() }
    override val numberMinusNumber: Minus<UShort, UShort, UShort> = Minus { left, right -> (left - right).toUShort() }
    override val numberTimesNumber: Times<UShort, UShort, UShort> = Times { left, right -> (left * right).toUShort() }
    override val numberDivideRemainderNumber: DivideRemainder<UShort, UShort, EuclideanDivisionResult<UShort>> = DivideRemainder { left, right ->
        if (right == 0.toUShort()) divisionByZero()
        else EuclideanDivisionResult(
            quotient = (left / right).toUShort(),
            remainder = (left % right).toUShort(),
        )
    }
    override val numberDivideNumber: Divide<UShort, UShort, UShort> = Divide { left, right -> if (right == 0.toUShort()) divisionByZero() else (left / right).toUShort() }
    override val numberRemainderNumber: Remainder<UShort, UShort, UShort> = Remainder { left, right -> if (right == 0.toUShort()) divisionByZero() else (left % right).toUShort() }
    override val powerNumberUInt: Power<UShort, UInt, UShort> = Power { base, exponent -> base squaringPower exponent }
    override val powerNumberULong: Power<UShort, ULong, UShort> = Power { base, exponent -> base squaringPower exponent }
    // endregion
}

/**
 * Returns the [UShort] reification context (a singleton implementing [Reification] for [UShort]).
 *
 * @return The [Reification] context for [UShort].
 */
public fun UShort.Companion.reification(): Reification<UShort> = UShortContext
/**
 * Returns the [UShort] equality context (a singleton implementing [Equality] for [UShort]).
 *
 * @return The [Equality] context for [UShort].
 */
public fun UShort.Companion.equality(): Equality<UShort> = UShortContext
/**
 * Returns the [UShort] order context (a singleton implementing [Order] for [UShort]).
 *
 * @return The [Order] context for [UShort].
 */
public fun UShort.Companion.order(): Order<UShort> = UShortContext
/**
 * Returns the [UShort] hashing context (a singleton implementing [Hashing] for [UShort]).
 *
 * @return The [Hashing] context for [UShort].
 */
public fun UShort.Companion.hashing(): Hashing<UShort> = UShortContext
/**
 * Returns the [UShort] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [UShort]).
 *
 * @return The [EuclideanSemiring] context for [UShort].
 */
public fun UShort.Companion.euclideanSemiring(): EuclideanSemiring<UShort> = UShortContext
/**
 * Returns the [UShort] commutative semiring context (a singleton implementing [CommutativeSemiring] for [UShort]).
 *
 * @return The [CommutativeSemiring] context for [UShort].
 */
public fun UShort.Companion.commutativeSemiring(): CommutativeSemiring<UShort> = UShortContext
/**
 * Returns the [UShort] semiring context (a singleton implementing [Semiring] for [UShort]).
 *
 * @return The [Semiring] context for [UShort].
 */
public fun UShort.Companion.semiring(): Semiring<UShort> = UShortContext
/**
 * Returns the [UShort] commutative monoid context (a singleton implementing [CommutativeMonoid] for [UShort]).
 *
 * @return The [CommutativeMonoid] context for [UShort].
 */
public fun UShort.Companion.commutativeMonoid(): CommutativeMonoid<UShort> = UShortContext
/**
 * Returns the [UShort] monoid context (a singleton implementing [Monoid] for [UShort]).
 *
 * @return The [Monoid] context for [UShort].
 */
public fun UShort.Companion.monoid(): Monoid<UShort> = UShortContext
/**
 * Returns the [UShort] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [UShort]).
 *
 * @return The [CommutativeSemigroup] context for [UShort].
 */
public fun UShort.Companion.commutativeSemigroup(): CommutativeSemigroup<UShort> = UShortContext
/**
 * Returns the [UShort] semigroup context (a singleton implementing [Semigroup] for [UShort]).
 *
 * @return The [Semigroup] context for [UShort].
 */
public fun UShort.Companion.semigroup(): Semigroup<UShort> = UShortContext

/**
 * Registers the [Reification] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setReification() {
    Reification.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [Equality] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setEquality() {
    Equality.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [Order] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setOrder() {
    Order.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [Hashing] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setHashing() {
    Hashing.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [EuclideanSemiring] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [CommutativeSemiring] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [Semiring] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setSemiring() {
    Semiring.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [CommutativeMonoid] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [Monoid] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setMonoid() {
    Monoid.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [CommutativeSemigroup] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}
/**
 * Registers the [Semigroup] context for [UShort] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UShort.Companion.setSemigroup() {
    Semigroup.Key<UShort>().withImpliedUsingFirst correspondsTo UShortContext
}

private data object UIntContext: Reification<UInt>, Equality<UInt>, Order<UInt>, Hashing<UInt>, EuclideanSemiring<UInt>, ExtendedSemiring<UInt> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UInt
    override fun reifyMaybe(element: Any?): Maybe<UInt> = if (element is UInt) Some(element) else None
    override fun reifyOrNull(element: Any?): UInt? = element as? UInt
    override fun reify(element: Any?): UInt = element as? UInt ?: reificationException()
    // endregion
    
    // region Order
    override fun UInt.compareWith(other: UInt): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: UInt get() = 0.toUInt()
    override val one: UInt get() = 1.toUInt()
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): UInt = arg
    override fun valueOf(arg: ULong): UInt = arg.toUInt()
    // endregion
    
    // region UInt-UInt operations
    override val numberPlusUInt: Plus<UInt, UInt, UInt> = Plus { left, right -> (left + right) }
    override val numberMinusUInt: Minus<UInt, UInt, UInt> = Minus { left, right -> (left - right) }
    override val numberTimesUInt: Times<UInt, UInt, UInt> = Times { left, right -> (left * right) }
    // endregion
    
    // region UInt-ULong operations
    override val numberPlusULong: Plus<UInt, ULong, UInt> = Plus { left, right -> (left + right).toUInt() }
    override val numberMinusULong: Minus<UInt, ULong, UInt> = Minus { left, right -> (left - right).toUInt() }
    override val numberTimesULong: Times<UInt, ULong, UInt> = Times { left, right -> (left * right).toUInt() }
    // endregion
    
    // region UInt-UInt operations
    override val uIntPlusNumber: Plus<UInt, UInt, UInt> = Plus { left, right -> (left + right) }
    override val uIntMinusNumber: Minus<UInt, UInt, UInt> = Minus { left, right -> (left - right) }
    override val uIntTimesNumber: Times<UInt, UInt, UInt> = Times { left, right -> (left * right) }
    // endregion
    
    // region ULong-UInt operations
    override val uLongPlusNumber: Plus<ULong, UInt, UInt> = Plus { left, right -> (left + right).toUInt() }
    override val uLongMinusNumber: Minus<ULong, UInt, UInt> = Minus { left, right -> (left - right).toUInt() }
    override val uLongTimesNumber: Times<ULong, UInt, UInt> = Times { left, right -> (left * right).toUInt() }
    // endregion
    
    // region UInt-UInt operations
    override val numberPlusNumber: Plus<UInt, UInt, UInt> = Plus { left, right -> (left + right) }
    override val numberMinusNumber: Minus<UInt, UInt, UInt> = Minus { left, right -> (left - right) }
    override val numberTimesNumber: Times<UInt, UInt, UInt> = Times { left, right -> (left * right) }
    override val numberDivideRemainderNumber: DivideRemainder<UInt, UInt, EuclideanDivisionResult<UInt>> = DivideRemainder { left, right ->
        if (right == 0.toUInt()) divisionByZero()
        else EuclideanDivisionResult(
            quotient = (left / right),
            remainder = (left % right),
        )
    }
    override val numberDivideNumber: Divide<UInt, UInt, UInt> = Divide { left, right -> if (right == 0.toUInt()) divisionByZero() else (left / right) }
    override val numberRemainderNumber: Remainder<UInt, UInt, UInt> = Remainder { left, right -> if (right == 0.toUInt()) divisionByZero() else (left % right) }
    override val powerNumberUInt: Power<UInt, UInt, UInt> = Power { base, exponent -> base squaringPower exponent }
    override val powerNumberULong: Power<UInt, ULong, UInt> = Power { base, exponent -> base squaringPower exponent }
    // endregion
}

/**
 * Returns the [UInt] reification context (a singleton implementing [Reification] for [UInt]).
 *
 * @return The [Reification] context for [UInt].
 */
public fun UInt.Companion.reification(): Reification<UInt> = UIntContext
/**
 * Returns the [UInt] equality context (a singleton implementing [Equality] for [UInt]).
 *
 * @return The [Equality] context for [UInt].
 */
public fun UInt.Companion.equality(): Equality<UInt> = UIntContext
/**
 * Returns the [UInt] order context (a singleton implementing [Order] for [UInt]).
 *
 * @return The [Order] context for [UInt].
 */
public fun UInt.Companion.order(): Order<UInt> = UIntContext
/**
 * Returns the [UInt] hashing context (a singleton implementing [Hashing] for [UInt]).
 *
 * @return The [Hashing] context for [UInt].
 */
public fun UInt.Companion.hashing(): Hashing<UInt> = UIntContext
/**
 * Returns the [UInt] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [UInt]).
 *
 * @return The [EuclideanSemiring] context for [UInt].
 */
public fun UInt.Companion.euclideanSemiring(): EuclideanSemiring<UInt> = UIntContext
/**
 * Returns the [UInt] commutative semiring context (a singleton implementing [CommutativeSemiring] for [UInt]).
 *
 * @return The [CommutativeSemiring] context for [UInt].
 */
public fun UInt.Companion.commutativeSemiring(): CommutativeSemiring<UInt> = UIntContext
/**
 * Returns the [UInt] semiring context (a singleton implementing [Semiring] for [UInt]).
 *
 * @return The [Semiring] context for [UInt].
 */
public fun UInt.Companion.semiring(): Semiring<UInt> = UIntContext
/**
 * Returns the [UInt] commutative monoid context (a singleton implementing [CommutativeMonoid] for [UInt]).
 *
 * @return The [CommutativeMonoid] context for [UInt].
 */
public fun UInt.Companion.commutativeMonoid(): CommutativeMonoid<UInt> = UIntContext
/**
 * Returns the [UInt] monoid context (a singleton implementing [Monoid] for [UInt]).
 *
 * @return The [Monoid] context for [UInt].
 */
public fun UInt.Companion.monoid(): Monoid<UInt> = UIntContext
/**
 * Returns the [UInt] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [UInt]).
 *
 * @return The [CommutativeSemigroup] context for [UInt].
 */
public fun UInt.Companion.commutativeSemigroup(): CommutativeSemigroup<UInt> = UIntContext
/**
 * Returns the [UInt] semigroup context (a singleton implementing [Semigroup] for [UInt]).
 *
 * @return The [Semigroup] context for [UInt].
 */
public fun UInt.Companion.semigroup(): Semigroup<UInt> = UIntContext

/**
 * Registers the [Reification] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setReification() {
    Reification.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [Equality] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setEquality() {
    Equality.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [Order] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setOrder() {
    Order.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [Hashing] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setHashing() {
    Hashing.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [EuclideanSemiring] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [CommutativeSemiring] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [Semiring] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setSemiring() {
    Semiring.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [CommutativeMonoid] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [Monoid] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setMonoid() {
    Monoid.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [CommutativeSemigroup] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}
/**
 * Registers the [Semigroup] context for [UInt] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun UInt.Companion.setSemigroup() {
    Semigroup.Key<UInt>().withImpliedUsingFirst correspondsTo UIntContext
}

private data object ULongContext: Reification<ULong>, Equality<ULong>, Order<ULong>, Hashing<ULong>, EuclideanSemiring<ULong>, ExtendedSemiring<ULong> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is ULong
    override fun reifyMaybe(element: Any?): Maybe<ULong> = if (element is ULong) Some(element) else None
    override fun reifyOrNull(element: Any?): ULong? = element as? ULong
    override fun reify(element: Any?): ULong = element as? ULong ?: reificationException()
    // endregion
    
    // region Order
    override fun ULong.compareWith(other: ULong): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: ULong get() = 0.toULong()
    override val one: ULong get() = 1.toULong()
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): ULong = arg.toULong()
    override fun valueOf(arg: ULong): ULong = arg
    // endregion
    
    // region ULong-UInt operations
    override val numberPlusUInt: Plus<ULong, UInt, ULong> = Plus { left, right -> (left + right) }
    override val numberMinusUInt: Minus<ULong, UInt, ULong> = Minus { left, right -> (left - right) }
    override val numberTimesUInt: Times<ULong, UInt, ULong> = Times { left, right -> (left * right) }
    // endregion
    
    // region ULong-ULong operations
    override val numberPlusULong: Plus<ULong, ULong, ULong> = Plus { left, right -> (left + right) }
    override val numberMinusULong: Minus<ULong, ULong, ULong> = Minus { left, right -> (left - right) }
    override val numberTimesULong: Times<ULong, ULong, ULong> = Times { left, right -> (left * right) }
    // endregion
    
    // region UInt-ULong operations
    override val uIntPlusNumber: Plus<UInt, ULong, ULong> = Plus { left, right -> (left + right) }
    override val uIntMinusNumber: Minus<UInt, ULong, ULong> = Minus { left, right -> (left - right) }
    override val uIntTimesNumber: Times<UInt, ULong, ULong> = Times { left, right -> (left * right) }
    // endregion
    
    // region ULong-ULong operations
    override val uLongPlusNumber: Plus<ULong, ULong, ULong> = Plus { left, right -> (left + right) }
    override val uLongMinusNumber: Minus<ULong, ULong, ULong> = Minus { left, right -> (left - right) }
    override val uLongTimesNumber: Times<ULong, ULong, ULong> = Times { left, right -> (left * right) }
    // endregion
    
    // region ULong-ULong operations
    override val numberPlusNumber: Plus<ULong, ULong, ULong> = Plus { left, right -> (left + right) }
    override val numberMinusNumber: Minus<ULong, ULong, ULong> = Minus { left, right -> (left - right) }
    override val numberTimesNumber: Times<ULong, ULong, ULong> = Times { left, right -> (left * right) }
    override val numberDivideRemainderNumber: DivideRemainder<ULong, ULong, EuclideanDivisionResult<ULong>> = DivideRemainder { left, right ->
        if (right == 0.toULong()) divisionByZero()
        else EuclideanDivisionResult(
            quotient = (left / right),
            remainder = (left % right),
        )
    }
    override val numberDivideNumber: Divide<ULong, ULong, ULong> = Divide { left, right -> if (right == 0.toULong()) divisionByZero() else (left / right) }
    override val numberRemainderNumber: Remainder<ULong, ULong, ULong> = Remainder { left, right -> if (right == 0.toULong()) divisionByZero() else (left % right) }
    override val powerNumberUInt: Power<ULong, UInt, ULong> = Power { base, exponent -> base squaringPower exponent }
    override val powerNumberULong: Power<ULong, ULong, ULong> = Power { base, exponent -> base squaringPower exponent }
    // endregion
}

/**
 * Returns the [ULong] reification context (a singleton implementing [Reification] for [ULong]).
 *
 * @return The [Reification] context for [ULong].
 */
public fun ULong.Companion.reification(): Reification<ULong> = ULongContext
/**
 * Returns the [ULong] equality context (a singleton implementing [Equality] for [ULong]).
 *
 * @return The [Equality] context for [ULong].
 */
public fun ULong.Companion.equality(): Equality<ULong> = ULongContext
/**
 * Returns the [ULong] order context (a singleton implementing [Order] for [ULong]).
 *
 * @return The [Order] context for [ULong].
 */
public fun ULong.Companion.order(): Order<ULong> = ULongContext
/**
 * Returns the [ULong] hashing context (a singleton implementing [Hashing] for [ULong]).
 *
 * @return The [Hashing] context for [ULong].
 */
public fun ULong.Companion.hashing(): Hashing<ULong> = ULongContext
/**
 * Returns the [ULong] euclidean semiring context (a singleton implementing [EuclideanSemiring] for [ULong]).
 *
 * @return The [EuclideanSemiring] context for [ULong].
 */
public fun ULong.Companion.euclideanSemiring(): EuclideanSemiring<ULong> = ULongContext
/**
 * Returns the [ULong] commutative semiring context (a singleton implementing [CommutativeSemiring] for [ULong]).
 *
 * @return The [CommutativeSemiring] context for [ULong].
 */
public fun ULong.Companion.commutativeSemiring(): CommutativeSemiring<ULong> = ULongContext
/**
 * Returns the [ULong] semiring context (a singleton implementing [Semiring] for [ULong]).
 *
 * @return The [Semiring] context for [ULong].
 */
public fun ULong.Companion.semiring(): Semiring<ULong> = ULongContext
/**
 * Returns the [ULong] commutative monoid context (a singleton implementing [CommutativeMonoid] for [ULong]).
 *
 * @return The [CommutativeMonoid] context for [ULong].
 */
public fun ULong.Companion.commutativeMonoid(): CommutativeMonoid<ULong> = ULongContext
/**
 * Returns the [ULong] monoid context (a singleton implementing [Monoid] for [ULong]).
 *
 * @return The [Monoid] context for [ULong].
 */
public fun ULong.Companion.monoid(): Monoid<ULong> = ULongContext
/**
 * Returns the [ULong] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [ULong]).
 *
 * @return The [CommutativeSemigroup] context for [ULong].
 */
public fun ULong.Companion.commutativeSemigroup(): CommutativeSemigroup<ULong> = ULongContext
/**
 * Returns the [ULong] semigroup context (a singleton implementing [Semigroup] for [ULong]).
 *
 * @return The [Semigroup] context for [ULong].
 */
public fun ULong.Companion.semigroup(): Semigroup<ULong> = ULongContext

/**
 * Registers the [Reification] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setReification() {
    Reification.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [Equality] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setEquality() {
    Equality.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [Order] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setOrder() {
    Order.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [Hashing] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setHashing() {
    Hashing.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [EuclideanSemiring] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [CommutativeSemiring] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [Semiring] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setSemiring() {
    Semiring.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [CommutativeMonoid] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [Monoid] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setMonoid() {
    Monoid.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [CommutativeSemigroup] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}
/**
 * Registers the [Semigroup] context for [ULong] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ULong.Companion.setSemigroup() {
    Semigroup.Key<ULong>().withImpliedUsingFirst correspondsTo ULongContext
}

private data object DoubleContext: Reification<Double>, Equality<Double>, Order<Double>, Hashing<Double>, Field<Double> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Double
    override fun reifyMaybe(element: Any?): Maybe<Double> = if (element is Double) Some(element) else None
    override fun reifyOrNull(element: Any?): Double? = element as? Double
    override fun reify(element: Any?): Double = element as? Double ?: reificationException()
    // endregion
    
    // region Order
    override fun Double.compareWith(other: Double): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Double get() = 0.0
    override val one: Double get() = 1.0
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Double = arg.toDouble()
    override fun valueOf(arg: UInt): Double = arg.toDouble()
    override fun valueOf(arg: Long): Double = arg.toDouble()
    override fun valueOf(arg: ULong): Double = arg.toDouble()
    // endregion
    
    // region Double-Int operations
    override val numberPlusInt: Plus<Double, Int, Double> = Plus { left, right -> (left + right) }
    override val numberMinusInt: Minus<Double, Int, Double> = Minus { left, right -> (left - right) }
    override val numberTimesInt: Times<Double, Int, Double> = Times { left, right -> (left * right) }
    // endregion
    
    // region Double-UInt operations
    override val numberPlusUInt: Plus<Double, UInt, Double> = Plus { left, right -> (left + right.toDouble()) }
    override val numberMinusUInt: Minus<Double, UInt, Double> = Minus { left, right -> (left - right.toDouble()) }
    override val numberTimesUInt: Times<Double, UInt, Double> = Times { left, right -> (left * right.toDouble()) }
    // endregion
    
    // region Double-Long operations
    override val numberPlusLong: Plus<Double, Long, Double> = Plus { left, right -> (left + right) }
    override val numberMinusLong: Minus<Double, Long, Double> = Minus { left, right -> (left - right) }
    override val numberTimesLong: Times<Double, Long, Double> = Times { left, right -> (left * right) }
    // endregion
    
    // region Double-ULong operations
    override val numberPlusULong: Plus<Double, ULong, Double> = Plus { left, right -> (left + right.toDouble()) }
    override val numberMinusULong: Minus<Double, ULong, Double> = Minus { left, right -> (left - right.toDouble()) }
    override val numberTimesULong: Times<Double, ULong, Double> = Times { left, right -> (left * right.toDouble()) }
    // endregion
    
    // region Int-Double operations
    override val intPlusNumber: Plus<Int, Double, Double> = Plus { left, right -> (left + right) }
    override val intMinusNumber: Minus<Int, Double, Double> = Minus { left, right -> (left - right) }
    override val intTimesNumber: Times<Int, Double, Double> = Times { left, right -> (left * right) }
    // endregion
    
    // region UInt-Double operations
    override val uIntPlusNumber: Plus<UInt, Double, Double> = Plus { left, right -> (left.toDouble() + right) }
    override val uIntMinusNumber: Minus<UInt, Double, Double> = Minus { left, right -> (left.toDouble() - right) }
    override val uIntTimesNumber: Times<UInt, Double, Double> = Times { left, right -> (left.toDouble() * right) }
    // endregion
    
    // region Long-Double operations
    override val longPlusNumber: Plus<Long, Double, Double> = Plus { left, right -> (left + right) }
    override val longMinusNumber: Minus<Long, Double, Double> = Minus { left, right -> (left - right) }
    override val longTimesNumber: Times<Long, Double, Double> = Times { left, right -> (left * right) }
    // endregion
    
    // region ULong-Double operations
    override val uLongPlusNumber: Plus<ULong, Double, Double> = Plus { left, right -> (left.toDouble() + right) }
    override val uLongMinusNumber: Minus<ULong, Double, Double> = Minus { left, right -> (left.toDouble() - right) }
    override val uLongTimesNumber: Times<ULong, Double, Double> = Times { left, right -> (left.toDouble() * right) }
    // endregion
    
    // region Double-Double operations
    override val numberUnaryMinus: UnaryMinus<Double, Double> = UnaryMinus { -it }
    override val numberPlusNumber: Plus<Double, Double, Double> = Plus { left, right -> left + right }
    override val numberMinusNumber: Minus<Double, Double, Double> = Minus { left, right -> left - right }
    override val numberTimesNumber: Times<Double, Double, Double> = Times { left, right -> left * right }
    override val numberDivideNumber: Divide<Double, Double, Double> = Divide { left, right -> left / right }
    override val powerNumberUInt: Power<Double, UInt, Double> = Power { base, exponent -> base.kpow(exponent.toDouble()) }
    override val powerNumberULong: Power<Double, ULong, Double> = Power { base, exponent -> base.kpow(exponent.toDouble()) }
    override val powerNumberInt: Power<Double, Int, Double> = Power { base, exponent -> base.kpow(exponent) }
    override val powerNumberLong: Power<Double, Long, Double> = Power { base, exponent -> base.kpow(exponent.toDouble()) }
    // endregion
}

/**
 * Returns the [Double] reification context (a singleton implementing [Reification] for [Double]).
 *
 * @return The [Reification] context for [Double].
 */
public fun Double.Companion.reification(): Reification<Double> = DoubleContext
/**
 * Returns the [Double] equality context (a singleton implementing [Equality] for [Double]).
 *
 * @return The [Equality] context for [Double].
 */
public fun Double.Companion.equality(): Equality<Double> = DoubleContext
/**
 * Returns the [Double] order context (a singleton implementing [Order] for [Double]).
 *
 * @return The [Order] context for [Double].
 */
public fun Double.Companion.order(): Order<Double> = DoubleContext
/**
 * Returns the [Double] hashing context (a singleton implementing [Hashing] for [Double]).
 *
 * @return The [Hashing] context for [Double].
 */
public fun Double.Companion.hashing(): Hashing<Double> = DoubleContext
/**
 * Returns the [Double] field context (a singleton implementing [Field] for [Double]).
 *
 * @return The [Field] context for [Double].
 */
public fun Double.Companion.field(): Field<Double> = DoubleContext
/**
 * Returns the [Double] commutative ring context (a singleton implementing [CommutativeRing] for [Double]).
 *
 * @return The [CommutativeRing] context for [Double].
 */
public fun Double.Companion.commutativeRing(): CommutativeRing<Double> = DoubleContext
/**
 * Returns the [Double] ring context (a singleton implementing [Ring] for [Double]).
 *
 * @return The [Ring] context for [Double].
 */
public fun Double.Companion.ring(): Ring<Double> = DoubleContext
/**
 * Returns the [Double] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Double]).
 *
 * @return The [CommutativeSemiring] context for [Double].
 */
public fun Double.Companion.commutativeSemiring(): CommutativeSemiring<Double> = DoubleContext
/**
 * Returns the [Double] semiring context (a singleton implementing [Semiring] for [Double]).
 *
 * @return The [Semiring] context for [Double].
 */
public fun Double.Companion.semiring(): Semiring<Double> = DoubleContext
/**
 * Returns the [Double] commutative group context (a singleton implementing [CommutativeGroup] for [Double]).
 *
 * @return The [CommutativeGroup] context for [Double].
 */
public fun Double.Companion.commutativeGroup(): CommutativeGroup<Double> = DoubleContext
/**
 * Returns the [Double] group context (a singleton implementing [Group] for [Double]).
 *
 * @return The [Group] context for [Double].
 */
public fun Double.Companion.group(): Group<Double> = DoubleContext
/**
 * Returns the [Double] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Double]).
 *
 * @return The [CommutativeMonoid] context for [Double].
 */
public fun Double.Companion.commutativeMonoid(): CommutativeMonoid<Double> = DoubleContext
/**
 * Returns the [Double] monoid context (a singleton implementing [Monoid] for [Double]).
 *
 * @return The [Monoid] context for [Double].
 */
public fun Double.Companion.monoid(): Monoid<Double> = DoubleContext
/**
 * Returns the [Double] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Double]).
 *
 * @return The [CommutativeSemigroup] context for [Double].
 */
public fun Double.Companion.commutativeSemigroup(): CommutativeSemigroup<Double> = DoubleContext
/**
 * Returns the [Double] semigroup context (a singleton implementing [Semigroup] for [Double]).
 *
 * @return The [Semigroup] context for [Double].
 */
public fun Double.Companion.semigroup(): Semigroup<Double> = DoubleContext

/**
 * Registers the [Reification] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setReification() {
    Reification.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Equality] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setEquality() {
    Equality.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Order] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setOrder() {
    Order.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Hashing] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setHashing() {
    Hashing.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Field] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setField() {
    Field.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [CommutativeRing] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeRing() {
    CommutativeRing.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Ring] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setRing() {
    Ring.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [CommutativeSemiring] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Semiring] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSemiring() {
    Semiring.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [CommutativeGroup] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Group] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setGroup() {
    Group.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [CommutativeMonoid] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Monoid] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setMonoid() {
    Monoid.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [CommutativeSemigroup] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}
/**
 * Registers the [Semigroup] context for [Double] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSemigroup() {
    Semigroup.Key<Double>().withImpliedUsingFirst correspondsTo DoubleContext
}

private data object FloatContext: Reification<Float>, Equality<Float>, Order<Float>, Hashing<Float>, Field<Float> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Float
    override fun reifyMaybe(element: Any?): Maybe<Float> = if (element is Float) Some(element) else None
    override fun reifyOrNull(element: Any?): Float? = element as? Float
    override fun reify(element: Any?): Float = element as? Float ?: reificationException()
    // endregion
    
    // region Order
    override fun Float.compareWith(other: Float): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: Float get() = 0f
    override val one: Float get() = 1f
    // endregion
    
    // region Equality
    override fun Float.equalsTo(other: Float): Boolean = this == other
    
    // region Conversion
    override fun valueOf(arg: Int): Float = arg.toFloat()
    override fun valueOf(arg: UInt): Float = arg.toFloat()
    override fun valueOf(arg: Long): Float = arg.toFloat()
    override fun valueOf(arg: ULong): Float = arg.toFloat()
    // endregion
    
    // region Float-Int operations
    override val numberPlusInt: Plus<Float, Int, Float> = Plus { left, right -> (left + right) }
    override val numberMinusInt: Minus<Float, Int, Float> = Minus { left, right -> (left - right) }
    override val numberTimesInt: Times<Float, Int, Float> = Times { left, right -> (left * right) }
    // endregion
    
    // region Float-UInt operations
    override val numberPlusUInt: Plus<Float, UInt, Float> = Plus { left, right -> (left + right.toFloat()) }
    override val numberMinusUInt: Minus<Float, UInt, Float> = Minus { left, right -> (left - right.toFloat()) }
    override val numberTimesUInt: Times<Float, UInt, Float> = Times { left, right -> (left * right.toFloat()) }
    // endregion
    
    // region Float-Long operations
    override val numberPlusLong: Plus<Float, Long, Float> = Plus { left, right -> (left + right) }
    override val numberMinusLong: Minus<Float, Long, Float> = Minus { left, right -> (left - right) }
    override val numberTimesLong: Times<Float, Long, Float> = Times { left, right -> (left * right) }
    // endregion
    
    // region Float-ULong operations
    override val numberPlusULong: Plus<Float, ULong, Float> = Plus { left, right -> (left + right.toFloat()) }
    override val numberMinusULong: Minus<Float, ULong, Float> = Minus { left, right -> (left - right.toFloat()) }
    override val numberTimesULong: Times<Float, ULong, Float> = Times { left, right -> (left * right.toFloat()) }
    // endregion
    
    // region Int-Float operations
    override val intPlusNumber: Plus<Int, Float, Float> = Plus { left, right -> (left + right) }
    override val intMinusNumber: Minus<Int, Float, Float> = Minus { left, right -> (left - right) }
    override val intTimesNumber: Times<Int, Float, Float> = Times { left, right -> (left * right) }
    // endregion
    
    // region UInt-Float operations
    override val uIntPlusNumber: Plus<UInt, Float, Float> = Plus { left, right -> (left.toFloat() + right) }
    override val uIntMinusNumber: Minus<UInt, Float, Float> = Minus { left, right -> (left.toFloat() - right) }
    override val uIntTimesNumber: Times<UInt, Float, Float> = Times { left, right -> (left.toFloat() * right) }
    // endregion
    
    // region Long-Float operations
    override val longPlusNumber: Plus<Long, Float, Float> = Plus { left, right -> (left + right) }
    override val longMinusNumber: Minus<Long, Float, Float> = Minus { left, right -> (left - right) }
    override val longTimesNumber: Times<Long, Float, Float> = Times { left, right -> (left * right) }
    // endregion
    
    // region ULong-Float operations
    override val uLongPlusNumber: Plus<ULong, Float, Float> = Plus { left, right -> (left.toFloat() + right) }
    override val uLongMinusNumber: Minus<ULong, Float, Float> = Minus { left, right -> (left.toFloat() - right) }
    override val uLongTimesNumber: Times<ULong, Float, Float> = Times { left, right -> (left.toFloat() * right) }
    // endregion
    
    // region Float-Float operations
    override val numberUnaryMinus: UnaryMinus<Float, Float> = UnaryMinus { -it }
    override val numberPlusNumber: Plus<Float, Float, Float> = Plus { left, right -> left + right }
    override val numberMinusNumber: Minus<Float, Float, Float> = Minus { left, right -> left - right }
    override val numberTimesNumber: Times<Float, Float, Float> = Times { left, right -> left * right }
    override val numberDivideNumber: Divide<Float, Float, Float> = Divide { left, right -> left / right }
    override val powerNumberUInt: Power<Float, UInt, Float> = Power { base, exponent -> base.kpow(exponent.toFloat()) }
    override val powerNumberULong: Power<Float, ULong, Float> = Power { base, exponent -> base.kpow(exponent.toFloat()) }
    override val powerNumberInt: Power<Float, Int, Float> = Power { base, exponent -> base.kpow(exponent) }
    override val powerNumberLong: Power<Float, Long, Float> = Power { base, exponent -> base.kpow(exponent.toFloat()) }
    // endregion
}

/**
 * Returns the [Float] reification context (a singleton implementing [Reification] for [Float]).
 *
 * @return The [Reification] context for [Float].
 */
public fun Float.Companion.reification(): Reification<Float> = FloatContext
/**
 * Returns the [Float] equality context (a singleton implementing [Equality] for [Float]).
 *
 * @return The [Equality] context for [Float].
 */
public fun Float.Companion.equality(): Equality<Float> = FloatContext
/**
 * Returns the [Float] order context (a singleton implementing [Order] for [Float]).
 *
 * @return The [Order] context for [Float].
 */
public fun Float.Companion.order(): Order<Float> = FloatContext
/**
 * Returns the [Float] hashing context (a singleton implementing [Hashing] for [Float]).
 *
 * @return The [Hashing] context for [Float].
 */
public fun Float.Companion.hashing(): Hashing<Float> = FloatContext
/**
 * Returns the [Float] field context (a singleton implementing [Field] for [Float]).
 *
 * @return The [Field] context for [Float].
 */
public fun Float.Companion.field(): Field<Float> = FloatContext
/**
 * Returns the [Float] commutative ring context (a singleton implementing [CommutativeRing] for [Float]).
 *
 * @return The [CommutativeRing] context for [Float].
 */
public fun Float.Companion.commutativeRing(): CommutativeRing<Float> = FloatContext
/**
 * Returns the [Float] ring context (a singleton implementing [Ring] for [Float]).
 *
 * @return The [Ring] context for [Float].
 */
public fun Float.Companion.ring(): Ring<Float> = FloatContext
/**
 * Returns the [Float] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Float]).
 *
 * @return The [CommutativeSemiring] context for [Float].
 */
public fun Float.Companion.commutativeSemiring(): CommutativeSemiring<Float> = FloatContext
/**
 * Returns the [Float] semiring context (a singleton implementing [Semiring] for [Float]).
 *
 * @return The [Semiring] context for [Float].
 */
public fun Float.Companion.semiring(): Semiring<Float> = FloatContext
/**
 * Returns the [Float] commutative group context (a singleton implementing [CommutativeGroup] for [Float]).
 *
 * @return The [CommutativeGroup] context for [Float].
 */
public fun Float.Companion.commutativeGroup(): CommutativeGroup<Float> = FloatContext
/**
 * Returns the [Float] group context (a singleton implementing [Group] for [Float]).
 *
 * @return The [Group] context for [Float].
 */
public fun Float.Companion.group(): Group<Float> = FloatContext
/**
 * Returns the [Float] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Float]).
 *
 * @return The [CommutativeMonoid] context for [Float].
 */
public fun Float.Companion.commutativeMonoid(): CommutativeMonoid<Float> = FloatContext
/**
 * Returns the [Float] monoid context (a singleton implementing [Monoid] for [Float]).
 *
 * @return The [Monoid] context for [Float].
 */
public fun Float.Companion.monoid(): Monoid<Float> = FloatContext
/**
 * Returns the [Float] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Float]).
 *
 * @return The [CommutativeSemigroup] context for [Float].
 */
public fun Float.Companion.commutativeSemigroup(): CommutativeSemigroup<Float> = FloatContext
/**
 * Returns the [Float] semigroup context (a singleton implementing [Semigroup] for [Float]).
 *
 * @return The [Semigroup] context for [Float].
 */
public fun Float.Companion.semigroup(): Semigroup<Float> = FloatContext

/**
 * Registers the [Reification] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setReification() {
    Reification.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Equality] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setEquality() {
    Equality.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Order] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setOrder() {
    Order.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Hashing] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setHashing() {
    Hashing.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Field] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setField() {
    Field.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [CommutativeRing] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeRing() {
    CommutativeRing.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Ring] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setRing() {
    Ring.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [CommutativeSemiring] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Semiring] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setSemiring() {
    Semiring.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [CommutativeGroup] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Group] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setGroup() {
    Group.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [CommutativeMonoid] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Monoid] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setMonoid() {
    Monoid.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [CommutativeSemigroup] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}
/**
 * Registers the [Semigroup] context for [Float] in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Float.Companion.setSemigroup() {
    Semigroup.Key<Float>().withImpliedUsingFirst correspondsTo FloatContext
}