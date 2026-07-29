/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


/**
 * Describes a context that represents [mathematical semiring](https://en.wikipedia.org/wiki/Semiring).
 *
 * @param Number The type of elements of the group.
 */
public interface Semiring<Number> : CommutativeMonoid<Number> {
    // region Constants
    /**
     * The neutral additive element (a.k.a. *zero element*) of the semiring.
     */
    override val zero: Number
    /**
     * The neutral multiplicative element (a.k.a. *unit element*) of the semiring.
     */
    public val one: Number
    // endregion
    
    // region Equality
    /**
     * The check on being zero element operation on a [Number].
     *
     * @return The check context represented as [IsZero] instance.
     */
    @KoneContextInclude
    public override val numberIsZero: IsZero<Number>
    /**
     * The check on being unit element operation on a [Number].
     *
     * @return The check context represented as [IsOne] instance.
     */
    @KoneContextInclude
    public val numberIsOne: IsOne<Number>
    // endregion
    
    // region Integers conversion
    /**
     * Converts instance of [UInt] to an element of the [Semiring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     * Default implementation computes the sum via the doubling algorithm.
     *
     * @param arg The unsigned integer value to convert.
     * @return The semiring element corresponding to [arg].
     */
    public fun valueOf(arg: UInt): Number = one doublingTimes arg
    /**
     * Converts instance of [ULong] to an element of the [Semiring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     * Default implementation computes the sum via the doubling algorithm.
     *
     * @param arg The unsigned long value to convert.
     * @return The semiring element corresponding to [arg].
     */
    public fun valueOf(arg: ULong): Number = one doublingTimes arg
    // endregion
    
    // region Number-UInt operations
    /**
     * The addition operation on a [Number] and an [UInt].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val numberPlusUInt: Plus<Number, UInt, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    /**
     * The multiplication operation on a [Number] and an [UInt].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val numberTimesUInt: Times<Number, UInt, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
    // endregion
    
    // region Number-ULong operations
    /**
     * The addition operation on a [Number] and an [ULong].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val numberPlusULong: Plus<Number, ULong, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    /**
     * The multiplication operation on a [Number] and an [ULong].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val numberTimesULong: Times<Number, ULong, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
    // endregion
    
    // region UInt-Number operations
    /**
     * The addition operation on an [UInt] and a [Number].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val uIntPlusNumber: Plus<UInt, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    /**
     * The multiplication operation on an [UInt] and a [Number].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val uIntTimesNumber: Times<UInt, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
    // endregion
    
    // region ULong-Number operations
    /**
     * The addition operation on an [ULong] and a [Number].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val uLongPlusNumber: Plus<ULong, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    /**
     * The multiplication operation on an [ULong] and a [Number].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val uLongTimesNumber: Times<ULong, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
    // endregion
    
    // region Number-Number operations
    /**
     * The associative binary addition operation on elements of type [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public override val numberPlusNumber: Plus<Number, Number, Number>
    /**
     * The associative binary multiplication operation on elements of type [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val numberTimesNumber: Times<Number, Number, Number>
    /**
     * The exponentiation operation on a [Number] base and an [UInt] exponent.
     *
     * Default implementation uses the squaring algorithm.
     *
     * @return The multiplication context represented as [Power] instance.
     */
    @KoneContextInclude
    public val powerNumberUInt: Power<Number, UInt, Number> get() = Power { base, exponent -> base squaringPower exponent }
    /**
     * The exponentiation operation on a [Number] base and an [ULong] exponent.
     *
     * Default implementation uses the squaring algorithm.
     *
     * @return The multiplication context represented as [Power] instance.
     */
    @KoneContextInclude
    public val powerNumberULong: Power<Number, ULong, Number> get() = Power { base, exponent -> base squaringPower exponent }
    // endregion
    
    public companion object;
    
    /**
     * Registry key for [Semiring] interface in [Registry].
     *
     * @param Number The type of elements of the semiring.
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Semiring<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Semiring<Number>> by lazy {
            ImpliedKeysRegistry {
                IsOne.Key<Number>() implies { it.numberIsOne }
                Times.Key<Number, Number, Number>() implies { it.numberTimesNumber }
                Power.Key<Number, UInt, Number>() implies { it.powerNumberUInt }
                Power.Key<Number, ULong, Number>() implies { it.powerNumberULong }
                Plus.Key<Number, UInt, Number>() implies { it.numberPlusUInt }
                Plus.Key<Number, ULong, Number>() implies { it.numberPlusULong }
                Plus.Key<UInt, Number, Number>() implies { it.uIntPlusNumber }
                Plus.Key<ULong, Number, Number>() implies { it.uLongPlusNumber }
                CommutativeMonoid.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Semiring.Key<${suppliedTypeOf<Number>()}>"
    }
}

/**
 * Describes a context that represents [mathematical commutative semiring](https://en.wikipedia.org/wiki/Semiring).
 *
 * @param Number The type of elements of the group.
 */
public interface CommutativeSemiring<Number> : Semiring<Number> {
    public companion object;
    
    /**
     * Registry key for [CommutativeSemiring] interface in [Registry].
     *
     * @param Number The type of elements of the commutative semiring.
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<CommutativeSemiring<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeSemiring<Number>> by lazy {
            ImpliedKeysRegistry {
                Semiring.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeSemiring.Key<${suppliedTypeOf<Number>()}>"
    }
}

// TODO: Think about replacing with "extended" commutative monoid.
/**
 * Describes a context that represents "extended" [mathematical semiring](https://en.wikipedia.org/wiki/Semiring).
 *
 * > **Warning!**
 * Aside from [Semiring]'s operations it also provides partially defined binary `-` operation.
 * That's why it is "extended" [Semiring].
 * This interface is made only to remove duplication of bridge contextual functions
 * for contexts like [UIntContext] and [ULongContext].
 * For the same reason [Ring] does not inherit this interface.
 * Thus, it should not be used in algorithms.
 *
 * @param Number The type of elements of the extended semiring.
 */
public interface ExtendedSemiring<Number> : Semiring<Number> {
    // region Number-UInt operations
    /**
     * The partially defined subtraction operation on a [Number] and an [UInt].
     *
     * Default implementation uses partially defined subtraction operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The addition context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusUInt: Minus<Number, UInt, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    // endregion
    
    // region Number-ULong operations
    /**
     * The partially defined subtraction operation on an [Number] and an [ULong].
     *
     * Default implementation uses partially defined subtraction operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The addition context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusULong: Minus<Number, ULong, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    // endregion
    
    // region UInt-Number operations
    /**
     * The partially defined subtraction operation on an [UInt] and a [Number].
     *
     * Default implementation uses partially defined subtraction operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The addition context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val uIntMinusNumber: Minus<UInt, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    // endregion
    
    // region ULong-Number operations
    /**
     * The partially defined subtraction operation on an [ULong] and a [Number].
     *
     * Default implementation uses partially defined subtraction operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The addition context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val uLongMinusNumber: Minus<ULong, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    // endregion
    
    // region Number-Number operations
    /**
     * The partially defined subtraction operation on elements of type [Number].
     *
     * @return The addition context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusNumber: Minus<Number, Number, Number>
    // endregion
    
    public companion object;
}