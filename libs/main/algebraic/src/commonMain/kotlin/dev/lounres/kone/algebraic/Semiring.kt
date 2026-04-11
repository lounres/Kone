/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


/**
 * Describes a context that represents [mathematical semiring](https://en.wikipedia.org/wiki/Semiring).
 * It means that it provides operations like `+`, `*`, `power`,
 * and some other that satisfy axioms of semiring like `a + b == b + a` or `a * (b * c) == (a * b) * c`.
 *
 * Usual structures like integers or real numbers satisfy the axioms (one says that they *form semirings*),
 * so you don't need to worry about fully understanding the concept of semiring.
 * It won't be true only the moment you introduce such structures as a semiring of matrices 2⨯2 with usual operations
 * you learn on any course of linear algebra.
 *
 * Obviously, the [Semiring] interface extends the [Equality] interface because otherwise there is no understanding
 * of the mathematical operations.
 */
public interface Semiring<Number> : CommutativeMonoid<Number> {
    // region Constants
    /**
     * Represents a zero element (a.k.a. *neutral additive element*).
     */
    public override val zero: Number
    /**
     * Represents a unit element (a.k.a. *neutral multiplicative element*).
     */
    public val one: Number
    // endregion
    
    // region Equality
    /**
     * Checks that [this] number is a zero in the context of the [Semiring].
     */
    public override fun Number.isZero(): Boolean = this equalsTo zero
    /**
     * Checks that [this] number is a one in the context of the [Semiring].
     */
    public fun Number.isOne(): Boolean = this equalsTo one
    /**
     * Checks that [this] number is not a zero in the context of the [Semiring].
     */
    // FIXME: KT-5351
    public override fun Number.isNotZero(): Boolean = !isZero()
    /**
     * Checks that [this] number is not a one in the context of the [Semiring].
     */
    // FIXME: KT-5351
    public fun Number.isNotOne(): Boolean = !isOne()
    // endregion
    
    // region Integers conversion
    /**
     * Converts instance of [UInt] to an element of the [Semiring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     */
    public fun valueOf(arg: UInt): Number = one doublingTimes arg
    /**
     * Converts instance of [ULong] to an element of the [Semiring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     */
    public fun valueOf(arg: ULong): Number = one doublingTimes arg
    // endregion
    
    // region Number-UInt operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Semiring].
     *
     * The result is equal to `this + valueOf(other)`.
     */
    public operator fun Number.plus(other: UInt): Number = this + valueOf(other)
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Semiring].
     *
     * The result is equal to `this * valueOf(other)`.
     */
    public override operator fun Number.times(other: UInt): Number = this * valueOf(other)
    // endregion
    
    // region Number-ULong operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Semiring].
     *
     * The result is equal to `this + valueOf(other)`.
     */
    public operator fun Number.plus(other: ULong): Number = this + valueOf(other)
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Semiring].
     *
     * The result is equal to `this * valueOf(other)`
     */
    public override operator fun Number.times(other: ULong): Number = this * valueOf(other)
    // endregion
    
    // region UInt-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Semiring].
     *
     * The result is equal to `valueOf(this) + other`.
     */
    public operator fun UInt.plus(other: Number): Number = valueOf(this) + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Semiring].
     *
     * The result is equal to `valueOf(this) * other`.
     */
    public override operator fun UInt.times(other: Number): Number = valueOf(this) * other
    // endregion
    
    // region ULong-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Semiring].
     *
     * The result is equal to `valueOf(this) + other`.
     */
    public operator fun ULong.plus(other: Number): Number = valueOf(this) + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Semiring].
     *
     * The result is equal to `valueOf(this) * other`.
     */
    public override operator fun ULong.times(other: Number): Number = valueOf(this) * other
    // endregion
    
    // region Number-Number operations
    /**
     * Sums [this] and the [other] numbers in terms of the [Semiring].
     */
    public override operator fun Number.plus(other: Number): Number
    /**
     * Multiplies [this] and the [other] numbers in terms of the [Semiring].
     */
    public operator fun Number.times(other: Number): Number
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies.
     */
    public fun power(base: Number, exponent: UInt): Number = base squaringPower exponent
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies.
     */
    public fun power(base: Number, exponent: ULong): Number = base squaringPower exponent
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies.
     */
    public infix fun Number.pow(exponent: UInt): Number = power(this, exponent)
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies.
     */
    public infix fun Number.pow(exponent: ULong): Number = power(this, exponent)
    // endregion
    
    public companion object;
    
    /**
     * Registry key for [Semiring] interface in [KoneContextRegistry].
     */
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<Semiring<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Semiring",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    )
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<Semiring<Number>> = ImpliedKeysRegistry {
            CommutativeMonoid.Key<Number>(numberType).impliesSame()
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.Semiring.Key<$numberType>"
    }
}


// region Equality
/**
 * Checks that [this] number is a one in the context of the [Semiring].
 *
 * A bridge contextual function for [Semiring.isOne].
 */
context(ring: Semiring<Number>)
public fun <Number> Number.isOne(): Boolean = with(ring) { this@isOne.isOne() }
/**
 * Checks that [this] number is not a one in the context of the [Semiring].
 *
 * A bridge contextual function for [Semiring.isNotOne].
 */
// FIXME: KT-5351
context(ring: Semiring<Number>)
public fun <Number> Number.isNotOne(): Boolean = with(ring) { this@isNotOne.isNotOne() }
// endregion

// region Integers conversion
/**
 * Converts instance of [UInt] to an element of the [Semiring] it is equal to.
 *
 * The result is equal to a sum of [arg] number of units.
 *
 * A bridge contextual function for [Semiring.valueOf].
 */
context(ring: Semiring<Number>)
public fun <Number> valueOf(arg: UInt): Number = ring.valueOf(arg)
/**
 * Converts instance of [ULong] to an element of the [Semiring] it is equal to.
 *
 * The result is equal to a sum of [arg] number of units.
 *
 * A bridge contextual function for [Semiring.valueOf].
 */
context(ring: Semiring<Number>)
public fun <Number> valueOf(arg: ULong): Number = ring.valueOf(arg)
// endregion

// region Number-UInt operations
/**
 * Sums [this] number and the [other] integer as elements of the [Semiring].
 *
 * The result is equal to `this + valueOf(other)`.
 *
 * A bridge contextual function for [Semiring.plus].
 */
context(ring: Semiring<Number>)
public operator fun <Number> Number.plus(other: UInt): Number = with(ring) { this@plus + other }
// endregion

// region Number-ULong operations
/**
 * Sums [this] number and the [other] integer as elements of the [Semiring].
 *
 * The result is equal to `this + valueOf(other)`.
 *
 * A bridge contextual function for [Semiring.plus].
 */
context(ring: Semiring<Number>)
public operator fun <Number> Number.plus(other: ULong): Number = with(ring) { this@plus + other }
// endregion

// region UInt-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Semiring].
 *
 * The result is equal to `valueOf(this) + other`.
 *
 * A bridge contextual function for [Semiring.plus].
 */
context(ring: Semiring<Number>)
public operator fun <Number> UInt.plus(other: Number): Number = with(ring) { this@plus + other }
// endregion

// region ULong-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Semiring].
 *
 * The result is equal to `valueOf(this) + other`.
 *
 * A bridge contextual function for [Semiring.plus].
 */
context(ring: Semiring<Number>)
public operator fun <Number> ULong.plus(other: Number): Number = with(ring) { this@plus + other }
// endregion

// region Number-Number operations
/**
 * Multiplies [this] and the [other] numbers in terms of the [Semiring].
 *
 * A bridge contextual function for [Semiring.times].
 */
context(ring: Semiring<Number>)
public operator fun <Number> Number.times(other: Number): Number = with(ring) { this@times * other }
/**
 * Raises [base] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [base] copies.
 *
 * A bridge contextual function for [Semiring.power].
 */
context(ring: Semiring<Number>)
public fun <Number> power(base: Number, exponent: UInt): Number = ring.power(base, exponent)
/**
 * Raises [base] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [base] copies.
 *
 * A bridge contextual function for [Semiring.power].
 */
context(ring: Semiring<Number>)
public fun <Number> power(base: Number, exponent: ULong): Number = ring.power(base, exponent)
/**
 * Raises [this] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [this] copies.
 *
 * A bridge contextual function for [Semiring.pow].
 */
context(ring: Semiring<Number>)
public infix fun <Number> Number.pow(exponent: UInt): Number = with(ring) { this@pow pow exponent }
/**
 * Raises [this] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [this] copies.
 *
 * A bridge contextual function for [Semiring.pow].
 */
context(ring: Semiring<Number>)
public infix fun <Number> Number.pow(exponent: ULong): Number = with(ring) { this@pow pow exponent }
// endregion


public interface CommutativeSemiring<Number> : Semiring<Number> {
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<CommutativeSemiring<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.CommutativeSemiring",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    )
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<CommutativeSemiring<Number>> = ImpliedKeysRegistry {
            Semiring.Key<Number>(numberType).impliesSame()
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeSemiring.Key<$numberType>"
    }
}


// TODO: Think about replacing with "extended" commutative monoid.
/**
 * Describes a context that represents "extended" [mathematical semiring](https://en.wikipedia.org/wiki/Semiring).
 * It means that it provides operations like `+`, `*`, `power`,
 * and some other that satisfy axioms of semiring like `a + b == b + a` or `a * (b * c) == (a * b) * c`.
 *
 * > **Warning!**
 * Aside from [Semiring]'s operations it also provides partially defined binary `-` operation.
 * That's why it is "extended" [Semiring].
 * This interface is made only to remove duplication of bridge contextual functions
 * for contexts like [UIntContext] and [ULongContext].
 * For the same reason [Ring] does not inherit this interface.
 * Thus, it should not be used in algorithms.
 *
 * Usual structures like integers or real numbers satisfy the axioms (one says that they *form rings*),
 * so you don't need to worry about fully understanding the concept of ring.
 * It won't be true only the moment you introduce such structures as a semiring of matrices 2⨯2 with usual operations
 * you learn on any course of linear algebra.
 *
 * Obviously, the [Semiring] interface extends the [Equality] interface because otherwise there is no understanding
 * of the mathematical operations.
 */
public interface ExtendedSemiring<Number> : Semiring<Number> {
    // region Number-UInt operations
    /**
     * Subtracts [this] number and the [other] integer as elements of the [ExtendedSemiring].
     *
     * The result is equal to `this - valueOf(other)`.
     *
     * Because it is partially defined, it may throw [ArithmeticException].
     * Be aware of the operation's domain!
     *
     * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
     */
    public operator fun Number.minus(other: UInt): Number = this - valueOf(other)
    // endregion
    
    // region Number-ULong operations
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - valueOf(other)`.
     *
     * Because it is partially defined, it may throw [ArithmeticException].
     * Be aware of the operation's domain!
     *
     * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
     */
    public operator fun Number.minus(other: ULong): Number = this - valueOf(other)
    // endregion
    
    // region UInt-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) - other`.
     *
     * Because it is partially defined, it may throw [ArithmeticException].
     * Be aware of the operation's domain!
     *
     * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
     */
    public operator fun UInt.minus(other: Number): Number = valueOf(this) - other
    // endregion
    
    // region ULong-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) - other`.
     *
     * Because it is partially defined, it may throw [ArithmeticException].
     * Be aware of the operation's domain!
     *
     * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
     */
    public operator fun ULong.minus(other: Number): Number = valueOf(this) - other
    // endregion
    
    // region Number-Number operations
    /**
     * Subtracts [this] and the [other] numbers in terms of the [Ring].
     *
     * Because it is partially defined, it may throw [ArithmeticException].
     * Be aware of the operation's domain!
     *
     * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
     */
    public operator fun Number.minus(other: Number): Number
    // endregion
}

// region Number-UInt operations
/**
 * Subtracts [this] number and the [other] integer as elements of the [ExtendedSemiring].
 *
 * The result is equal to `this - valueOf(other)`.
 *
 * Because it is partially defined, it may throw [ArithmeticException].
 * Be aware of the operation's domain!
 *
 * A bridge contextual function for [ExtendedSemiring.minus].
 *
 * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
 */
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> Number.minus(other: UInt): Number = with(ring) { this@minus - other }
// endregion

// region Number-ULong operations
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - valueOf(other)`.
 *
 * Because it is partially defined, it may throw [ArithmeticException].
 * Be aware of the operation's domain!
 *
 * A bridge contextual function for [ExtendedSemiring.minus].
 *
 * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
 */
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> Number.minus(other: ULong): Number = with(ring) { this@minus - other }
// endregion

// region UInt-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) - other`.
 *
 * Because it is partially defined, it may throw [ArithmeticException].
 * Be aware of the operation's domain!
 *
 * A bridge contextual function for [ExtendedSemiring.minus].
 *
 * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
 */
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> UInt.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region ULong-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) - other`.
 *
 * Because it is partially defined, it may throw [ArithmeticException].
 * Be aware of the operation's domain!
 *
 * A bridge contextual function for [ExtendedSemiring.minus].
 *
 * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
 */
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> ULong.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region Number-Number operations
/**
 * Subtracts [this] and the [other] numbers in terms of the [Ring].
 *
 * Because it is partially defined, it may throw [ArithmeticException].
 * Be aware of the operation's domain!
 *
 * A bridge contextual function for [ExtendedSemiring.minus].
 *
 * @throws ArithmeticException iff the subtraction is not defined for the provided operands.
 */
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> Number.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion