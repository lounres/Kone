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
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Equality
import kotlin.math.pow as kpow


// TODO: Add other safe contexts

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object SafeLongContext: Reification<Long>, Equality<Long>, Order<Long>, Hashing<Long>, EuclideanRing<Long> {
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
    override val numberUnaryMinus: UnaryMinus<Long, Long> = UnaryMinus {
        if (it == Long.MIN_VALUE) overflow()
        -it
    }
    override val numberPlusNumber: Plus<Long, Long, Long> = Plus { left, right ->
        val result = left + right
        if (right > 0L != result > left) overflow()
        result
    }
    override val numberMinusNumber: Minus<Long, Long, Long> = Minus { left, right ->
        val result = left - right
        if (right < 0L != result > left) overflow()
        result
    }
    override val numberTimesNumber: Times<Long, Long, Long> = Times { left, right ->
        if (right == 0L) return@Times 0L
        val result = left * right
        if (result / right != left) overflow()
        result
    }
    override val numberDivideRemainderNumber: DivideRemainder<Long, Long, EuclideanDivisionResult<Long>> = DivideRemainder { left, right ->
        if (right == 0L) divisionByZero()
        else EuclideanDivisionResult(quotient = left / right, remainder = left % right)
    }
    override val numberDivideNumber: Divide<Long, Long, Long> = Divide { left, right -> if (right == 0L) divisionByZero() else left / right }
    override val numberRemainderNumber: Remainder<Long, Long, Long> = Remainder { left, right -> if (right == 0L) divisionByZero() else left % right }
    // endregion
    
    // region Long-Int operations
    override val numberPlusInt: Plus<Long, Int, Long> = Plus { left, right ->
        val result = left + right
        if (right > 0L != result > left) overflow()
        result
    }
    override val numberMinusInt: Minus<Long, Int, Long> = Minus { left, right ->
        val result = left - right
        if (right < 0L != result > left) overflow()
        result
    }
    override val numberTimesInt: Times<Long, Int, Long> = Times { left, right ->
        if (right == 0) return@Times 0L
        val result = left * right
        if (result / right != left) overflow()
        result
    }
    // endregion

    // region Long-UInt operations
    override val numberPlusUInt: Plus<Long, UInt, Long> = Plus { left, right ->
        val result = left + right.toLong()
        if (result < left) overflow()
        result
    }
    override val numberMinusUInt: Minus<Long, UInt, Long> = Minus { left, right ->
        val result = left - right.toLong()
        if (result < left) overflow()
        result
    }
    override val numberTimesUInt: Times<Long, UInt, Long> = Times { left, right ->
        if (right == 0u) return@Times 0L
        val right = right.toLong()
        val result = left * right
        if (result / right != left) overflow()
        result
    }
    // endregion
    
    // region Long-Long operations
    override val numberPlusLong: Plus<Long, Long, Long> = Plus { left, right ->
        val result = left + right
        if (right > 0L != result > left) overflow()
        result
    }
    override val numberMinusLong: Minus<Long, Long, Long> = Minus { left, right ->
        val result = left - right
        if (right < 0L != result > left) overflow()
        result
    }
    override val numberTimesLong: Times<Long, Long, Long> = Times { left, right ->
        if (right == 0L) return@Times 0L
        val result = left * right
        if (result / right != left) overflow()
        result
    }
    // endregion

    // region Long-ULong operations
    override val numberPlusULong: Plus<Long, ULong, Long> = Plus { left, right ->
        val result = left + right.toLong()
        if (result < left) overflow()
        result
    }
    override val numberMinusULong: Minus<Long, ULong, Long> = Minus { left, right ->
        val result = left - right.toLong()
        if (result > left) overflow()
        result
    }
    override val numberTimesULong: Times<Long, ULong, Long> = Times { left, right ->
        if (left == 0L) return@Times 0L
        if (right and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val right = right.toLong()
        val result = left * right
        if (result / left != right) overflow()
        result
    }
    // endregion
    
    // region Int-Long operations
    override val intPlusNumber: Plus<Int, Long, Long> = Plus { left, right ->
        val result = left + right
        if (right > 0L != result > left) overflow()
        result
    }
    override val intMinusNumber: Minus<Int, Long, Long> = Minus { left, right ->
        val result = left - right
        if (right < 0L != result > left) overflow()
        result
    }
    override val intTimesNumber: Times<Int, Long, Long> = Times { left, right ->
        if (right == 0L) return@Times 0L
        val result = left * right
        if (result / right != left.toLong()) overflow()
        result
    }
    // endregion

    // region UInt-Long operations
    override val uIntPlusNumber: Plus<UInt, Long, Long> = Plus { left, right ->
        val result = left.toLong() + right
        if (result < right) overflow()
        result
    }
    override val uIntMinusNumber: Minus<UInt, Long, Long> = Minus { left, right ->
        val that = left.toLong()
        val result = that - right
        if (right < 0L != result > that) overflow()
        result
    }
    override val uIntTimesNumber: Times<UInt, Long, Long> = Times { left, right ->
        if (right == 0L) return@Times 0L
        val result = left.toLong() * right
        if (result / right != left.toLong()) overflow()
        result
    }
    // endregion
    
    // region Long-Long operations
    override val longPlusNumber: Plus<Long, Long, Long> = Plus { left, right ->
        val result = left + right
        if (right > 0L != result > left) overflow()
        result
    }
    override val longMinusNumber: Minus<Long, Long, Long> = Minus { left, right ->
        val result = left - right
        if (right < 0L != result > left) overflow()
        result
    }
    override val longTimesNumber: Times<Long, Long, Long> = Times { left, right ->
        if (right == 0L) return@Times 0L
        val result = left * right
        if (result / right != left) overflow()
        result
    }
    // endregion

    // region ULong-Long operations
    override val uLongPlusNumber: Plus<ULong, Long, Long> = Plus { left, right ->
        val result = left.toLong() + right
        if (result < right) overflow()
        result
    }
    override val uLongMinusNumber: Minus<ULong, Long, Long> = Minus { left, right ->
        val that = left.toLong()
        val result = that - right
        if (right < 0L != result > that) overflow()
        result
    }
    override val uLongTimesNumber: Times<ULong, Long, Long> = Times { left, right ->
        if (right == 0L) return@Times 0L
        if (left and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val that = left.toLong()
        val result = that * right
        if (result / right != that) overflow()
        result
    }
    // endregion
}

/**
 * Returns the safe [Long] reification context (a singleton implementing [Reification] for [Long] with overflow checking).
 *
 * @return The safe [Reification] context for [Long].
 */
public fun Long.Companion.safeReification(): Reification<Long> = SafeLongContext
/**
 * Returns the safe [Long] equality context (a singleton implementing [Equality] for [Long] with overflow checking).
 *
 * @return The safe [Equality] context for [Long].
 */
public fun Long.Companion.safeEquality(): Equality<Long> = SafeLongContext
/**
 * Returns the safe [Long] order context (a singleton implementing [Order] for [Long] with overflow checking).
 *
 * @return The safe [Order] context for [Long].
 */
public fun Long.Companion.safeOrder(): Order<Long> = SafeLongContext
/**
 * Returns the safe [Long] hashing context (a singleton implementing [Hashing] for [Long] with overflow checking).
 *
 * @return The safe [Hashing] context for [Long].
 */
public fun Long.Companion.safeHashing(): Hashing<Long> = SafeLongContext
/**
 * Returns the safe [Long] Euclidean ring context (a singleton implementing [EuclideanRing] for [Long] with overflow checking).
 *
 * @return The safe [EuclideanRing] context for [Long].
 */
public fun Long.Companion.safeEuclideanRing(): EuclideanRing<Long> = SafeLongContext
/**
 * Returns the safe [Long] Euclidean semiring context (a singleton implementing [EuclideanSemiring] for [Long] with overflow checking).
 *
 * @return The safe [EuclideanSemiring] context for [Long].
 */
public fun Long.Companion.safeEuclideanSemiring(): EuclideanSemiring<Long> = SafeLongContext
/**
 * Returns the safe [Long] commutative ring context (a singleton implementing [CommutativeRing] for [Long] with overflow checking).
 *
 * @return The safe [CommutativeRing] context for [Long].
 */
public fun Long.Companion.safeCommutativeRing(): CommutativeRing<Long> = SafeLongContext
/**
 * Returns the safe [Long] ring context (a singleton implementing [Ring] for [Long] with overflow checking).
 *
 * @return The safe [Ring] context for [Long].
 */
public fun Long.Companion.safeRing(): Ring<Long> = SafeLongContext
/**
 * Returns the safe [Long] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Long] with overflow checking).
 *
 * @return The safe [CommutativeSemiring] context for [Long].
 */
public fun Long.Companion.safeCommutativeSemiring(): CommutativeSemiring<Long> = SafeLongContext
/**
 * Returns the safe [Long] semiring context (a singleton implementing [Semiring] for [Long] with overflow checking).
 *
 * @return The safe [Semiring] context for [Long].
 */
public fun Long.Companion.safeSemiring(): Semiring<Long> = SafeLongContext
/**
 * Returns the safe [Long] commutative group context (a singleton implementing [CommutativeGroup] for [Long] with overflow checking).
 *
 * @return The safe [CommutativeGroup] context for [Long].
 */
public fun Long.Companion.safeCommutativeGroup(): CommutativeGroup<Long> = SafeLongContext
/**
 * Returns the safe [Long] group context (a singleton implementing [Group] for [Long] with overflow checking).
 *
 * @return The safe [Group] context for [Long].
 */
public fun Long.Companion.safeGroup(): Group<Long> = SafeLongContext
/**
 * Returns the safe [Long] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Long] with overflow checking).
 *
 * @return The safe [CommutativeMonoid] context for [Long].
 */
public fun Long.Companion.safeCommutativeMonoid(): CommutativeMonoid<Long> = SafeLongContext
/**
 * Returns the safe [Long] monoid context (a singleton implementing [Monoid] for [Long] with overflow checking).
 *
 * @return The safe [Monoid] context for [Long].
 */
public fun Long.Companion.safeMonoid(): Monoid<Long> = SafeLongContext
/**
 * Returns the safe [Long] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Long] with overflow checking).
 *
 * @return The safe [CommutativeSemigroup] context for [Long].
 */
public fun Long.Companion.safeCommutativeSemigroup(): CommutativeSemigroup<Long> = SafeLongContext
/**
 * Returns the safe [Long] semigroup context (a singleton implementing [Semigroup] for [Long] with overflow checking).
 *
 * @return The safe [Semigroup] context for [Long].
 */
public fun Long.Companion.safeSemigroup(): Semigroup<Long> = SafeLongContext

/**
 * Registers the safe [Reification] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeReification() {
    Reification.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Equality] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEquality() {
    Equality.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Order] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeOrder() {
    Order.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Hashing] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeHashing() {
    Hashing.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [EuclideanRing] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEuclideanRing() {
    EuclideanRing.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [EuclideanSemiring] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEuclideanSemiring() {
    EuclideanSemiring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [CommutativeRing] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeRing() {
    CommutativeRing.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Ring] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeRing() {
    Ring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [CommutativeSemiring] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeSemiring() {
    CommutativeSemiring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Semiring] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeSemiring() {
    Semiring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [CommutativeGroup] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeGroup() {
    CommutativeGroup.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Group] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeGroup() {
    Group.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [CommutativeMonoid] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeMonoid() {
    CommutativeMonoid.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Monoid] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeMonoid() {
    Monoid.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [CommutativeSemigroup] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeSemigroup() {
    CommutativeSemigroup.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
/**
 * Registers the safe [Semigroup] context for [Long] (with overflow checking) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeSemigroup() {
    Semigroup.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object SafeDoubleContext: Reification<Double>, Equality<Double>, Order<Double>, Hashing<Double>, Field<Double> {
    private fun Double.validate() {
        when {
            this.isNaN() -> throw IllegalArgumentException("NaN encountered!")
            this.isInfinite() -> throw IllegalArgumentException("Infinity encountered!")
        }
    }
    
    // region Reification
    override fun contains(element: Any?): Boolean =
        if (element is Double) {
            element.validate()
            true
        } else false
    override fun reifyMaybe(element: Any?): Maybe<Double> =
        if (element is Double) {
            element.validate()
            Some(element)
        } else None
    override fun reifyOrNull(element: Any?): Double? =
        if (element is Double) {
            element.validate()
            element
        } else null
    override fun reify(element: Any?): Double =
        if (element is Double) {
            element.validate()
            element
        } else reificationException()
    // endregion
    
    // region Equality
    override fun Double.equalsTo(other: Double): Boolean {
        this.validate()
        other.validate()
        return this == other
    }
    // endregion
    
    // region Order
    override fun Double.compareWith(other: Double): ComparisonResult {
        this.validate()
        other.validate()
        return this.compareTo(other).asComparisonResult()
    }
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
    override val numberPlusInt: Plus<Double, Int, Double> = Plus { left, right ->
        left.validate()
        left + right
    }
    override val numberMinusInt: Minus<Double, Int, Double> = Minus { left, right ->
        left.validate()
        left - right
    }
    override val numberTimesInt: Times<Double, Int, Double> = Times { left, right ->
        left.validate()
        left * right
    }
    override val numberDivideInt: Divide<Double, Int, Double> = Divide { left, right ->
        left.validate()
        if (right == 0) divisionByZero()
        left / right
    }
    // endregion
    
    // region Double-UInt operations
    override val numberPlusUInt: Plus<Double, UInt, Double> = Plus { left, right ->
        left.validate()
        left + right.toDouble()
    }
    override val numberMinusUInt: Minus<Double, UInt, Double> = Minus { left, right ->
        left.validate()
        left - right.toDouble()
    }
    override val numberTimesUInt: Times<Double, UInt, Double> = Times { left, right ->
        left.validate()
        left * right.toDouble()
    }
    override val numberDivideUInt: Divide<Double, UInt, Double> = Divide { left, right ->
        left.validate()
        if (right == 0u) divisionByZero()
        left / right.toDouble()
    }
    // endregion
    
    // region Double-Long operations
    override val numberPlusLong: Plus<Double, Long, Double> = Plus { left, right ->
        left.validate()
        left + right
    }
    override val numberMinusLong: Minus<Double, Long, Double> = Minus { left, right ->
        left.validate()
        left - right
    }
    override val numberTimesLong: Times<Double, Long, Double> = Times { left, right ->
        left.validate()
        left * right
    }
    override val numberDivideLong: Divide<Double, Long, Double> = Divide { left, right ->
        left.validate()
        if (right == 0L) divisionByZero()
        left / right
    }
    // endregion
    
    // region Double-ULong operations
    override val numberPlusULong: Plus<Double, ULong, Double> = Plus { left, right ->
        left.validate()
        left + right.toDouble()
    }
    override val numberMinusULong: Minus<Double, ULong, Double> = Minus { left, right ->
        left.validate()
        left - right.toDouble()
    }
    override val numberTimesULong: Times<Double, ULong, Double> = Times { left, right ->
        left.validate()
        left * right.toDouble()
    }
    override val numberDivideULong: Divide<Double, ULong, Double> = Divide { left, right ->
        left.validate()
        if (right == 0uL) divisionByZero()
        left / right.toDouble()
    }
    // endregion
    
    // region Int-Double operations
    override val intPlusNumber: Plus<Int, Double, Double> = Plus { left, right ->
        right.validate()
        left + right
    }
    override val intMinusNumber: Minus<Int, Double, Double> = Minus { left, right ->
        right.validate()
        left - right
    }
    override val intTimesNumber: Times<Int, Double, Double> = Times { left, right ->
        right.validate()
        left * right
    }
    override val intDivideNumber: Divide<Int, Double, Double> = Divide { left, right ->
        right.validate()
        if (right == 0.0) divisionByZero()
        left / right
    }
    // endregion
    
    // region UInt-Double operations
    override val uIntPlusNumber: Plus<UInt, Double, Double> = Plus { left, right ->
        right.validate()
        left.toDouble() + right
    }
    override val uIntMinusNumber: Minus<UInt, Double, Double> = Minus { left, right ->
        right.validate()
        left.toDouble() - right
    }
    override val uIntTimesNumber: Times<UInt, Double, Double> = Times { left, right ->
        right.validate()
        left.toDouble() * right
    }
    override val uIntDivideNumber: Divide<UInt, Double, Double> = Divide { left, right ->
        right.validate()
        if (right == 0.0) divisionByZero()
        left.toDouble() / right
    }
    // endregion
    
    // region Long-Double operations
    override val longPlusNumber: Plus<Long, Double, Double> = Plus { left, right ->
        right.validate()
        left + right
    }
    override val longMinusNumber: Minus<Long, Double, Double> = Minus { left, right ->
        right.validate()
        left - right
    }
    override val longTimesNumber: Times<Long, Double, Double> = Times { left, right ->
        right.validate()
        left * right
    }
    override val longDivideNumber: Divide<Long, Double, Double> = Divide { left, right ->
        right.validate()
        if (right == 0.0) divisionByZero()
        left / right
    }
    // endregion
    
    // region ULong-Double operations
    override val uLongPlusNumber: Plus<ULong, Double, Double> = Plus { left, right ->
        right.validate()
        left.toDouble() + right
    }
    override val uLongMinusNumber: Minus<ULong, Double, Double> = Minus { left, right ->
        right.validate()
        left.toDouble() - right
    }
    override val uLongTimesNumber: Times<ULong, Double, Double> = Times { left, right ->
        right.validate()
        left.toDouble() * right
    }
    override val uLongDivideNumber: Divide<ULong, Double, Double> = Divide { left, right ->
        right.validate()
        if (right == 0.0) divisionByZero()
        left.toDouble() / right
    }
    // endregion
    
    // region Double-Double operations
    override val numberUnaryMinus: UnaryMinus<Double, Double> = UnaryMinus {
        it.validate()
        -it
    }
    override val numberPlusNumber: Plus<Double, Double, Double> = Plus { left, right ->
        left.validate()
        right.validate()
        left + right
    }
    override val numberMinusNumber: Minus<Double, Double, Double> = Minus { left, right ->
        left.validate()
        right.validate()
        left - right
    }
    override val numberTimesNumber: Times<Double, Double, Double> = Times { left, right ->
        left.validate()
        right.validate()
        left * right
    }
    override val numberDivideNumber: Divide<Double, Double, Double> = Divide { left, right ->
        left.validate()
        right.validate()
        if (right == 0.0) divisionByZero()
        left / right
    }
    override val powerNumberUInt: Power<Double, UInt, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent.toDouble())
    }
    override val powerNumberULong: Power<Double, ULong, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent.toDouble())
    }
    override val powerNumberInt: Power<Double, Int, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent)
    }
    override val powerNumberLong: Power<Double, Long, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent.toDouble())
    }
    // endregion
}

/**
 * Returns the safe [Double] reification context (a singleton implementing [Reification] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Reification] context for [Double].
 */
public fun Double.Companion.safeReification(): Reification<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] equality context (a singleton implementing [Equality] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Equality] context for [Double].
 */
public fun Double.Companion.safeEquality(): Equality<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] order context (a singleton implementing [Order] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Order] context for [Double].
 */
public fun Double.Companion.safeOrder(): Order<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] hashing context (a singleton implementing [Hashing] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Hashing] context for [Double].
 */
public fun Double.Companion.safeHashing(): Hashing<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] field context (a singleton implementing [Field] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Field] context for [Double].
 */
public fun Double.Companion.safeField(): Field<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] commutative ring context (a singleton implementing [CommutativeRing] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [CommutativeRing] context for [Double].
 */
public fun Double.Companion.safeCommutativeRing(): CommutativeRing<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] ring context (a singleton implementing [Ring] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Ring] context for [Double].
 */
public fun Double.Companion.safeRing(): Ring<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] commutative semiring context (a singleton implementing [CommutativeSemiring] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [CommutativeSemiring] context for [Double].
 */
public fun Double.Companion.safeCommutativeSemiring(): CommutativeSemiring<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] semiring context (a singleton implementing [Semiring] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Semiring] context for [Double].
 */
public fun Double.Companion.safeSemiring(): Semiring<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] commutative group context (a singleton implementing [CommutativeGroup] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [CommutativeGroup] context for [Double].
 */
public fun Double.Companion.safeCommutativeGroup(): CommutativeGroup<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] group context (a singleton implementing [Group] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Group] context for [Double].
 */
public fun Double.Companion.safeGroup(): Group<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] commutative monoid context (a singleton implementing [CommutativeMonoid] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [CommutativeMonoid] context for [Double].
 */
public fun Double.Companion.safeCommutativeMonoid(): CommutativeMonoid<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] monoid context (a singleton implementing [Monoid] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Monoid] context for [Double].
 */
public fun Double.Companion.safeMonoid(): Monoid<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] commutative semigroup context (a singleton implementing [CommutativeSemigroup] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [CommutativeSemigroup] context for [Double].
 */
public fun Double.Companion.safeCommutativeSemigroup(): CommutativeSemigroup<Double> = SafeDoubleContext
/**
 * Returns the safe [Double] semigroup context (a singleton implementing [Semigroup] for [Double] with NaN/Infinity validation).
 *
 * @return The safe [Semigroup] context for [Double].
 */
public fun Double.Companion.safeSemigroup(): Semigroup<Double> = SafeDoubleContext

/**
 * Registers the safe [Reification] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeReification() {
    Reification.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Equality] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeEquality() {
    Equality.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Order] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeOrder() {
    Order.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Hashing] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeHashing() {
    Hashing.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Field] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeField() {
    Field.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [CommutativeRing] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeRing() {
    CommutativeRing.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Ring] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeRing() {
    Ring.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [CommutativeSemiring] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeSemiring() {
    CommutativeSemiring.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Semiring] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeSemiring() {
    Semiring.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [CommutativeGroup] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeGroup() {
    CommutativeGroup.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Group] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeGroup() {
    Group.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [CommutativeMonoid] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeMonoid() {
    CommutativeMonoid.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Monoid] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeMonoid() {
    Monoid.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [CommutativeSemigroup] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeSemigroup() {
    CommutativeSemigroup.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
/**
 * Registers the safe [Semigroup] context for [Double] (with NaN/Infinity validation) in the given [registry].
 *
 * @param registry The mutable owned provider registry to register the context into.
 */
context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeSemigroup() {
    Semigroup.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}