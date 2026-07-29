/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


/**
 * Describes a context that represents [mathematical ring](https://en.wikipedia.org/wiki/Ring_(mathematics)).
 *
 * @param Number The type of elements of the ring.
 */
public interface Ring<Number> : Semiring<Number>, CommutativeGroup<Number> {
    // region Integers conversion
    /**
     * Converts instance of [Int] to an element of the [Ring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     *
     * @param arg The integer value to convert.
     * @return The ring element corresponding to [arg].
     */
    public fun valueOf(arg: Int): Number = one doublingTimes arg
    /**
     * Converts instance of [Long] to an element of the [Ring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     *
     * @param arg The long value to convert.
     * @return The ring element corresponding to [arg].
     */
    public fun valueOf(arg: Long): Number = one doublingTimes arg
    // endregion

    // region Number-Int operations
    /**
     * The addition operation on a [Number] and an [Int].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val numberPlusInt: Plus<Number, Int, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    /**
     * The subtraction operation on a [Number] and an [Int].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusInt: Minus<Number, Int, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    /**
     * The multiplication operation on a [Number] and an [Int].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val numberTimesInt: Times<Number, Int, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
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
    public override val numberPlusUInt: Plus<Number, UInt, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    /**
     * The subtraction operation on a [Number] and an [UInt].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusUInt: Minus<Number, UInt, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
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

    // region Number-Long operations
    /**
     * The addition operation on a [Number] and an [Long].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val numberPlusLong: Plus<Number, Long, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    /**
     * The subtraction operation on a [Number] and an [Long].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusLong: Minus<Number, Long, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    /**
     * The multiplication operation on a [Number] and an [Long].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val numberTimesLong: Times<Number, Long, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
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
    public override val numberPlusULong: Plus<Number, ULong, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    /**
     * The subtraction operation on a [Number] and an [ULong].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusULong: Minus<Number, ULong, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
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

    // region Int-Number operations
    /**
     * The addition operation on an [Int] and a [Number].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val intPlusNumber: Plus<Int, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    /**
     * The subtraction operation on an [Int] and a [Number].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val intMinusNumber: Minus<Int, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    /**
     * The multiplication operation on an [Int] and a [Number].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [Int] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val intTimesNumber: Times<Int, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
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
    public override val uIntPlusNumber: Plus<UInt, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    /**
     * The subtraction operation on an [UInt] and a [Number].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [UInt] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val uIntMinusNumber: Minus<UInt, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
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

    // region Long-Number operations
    /**
     * The addition operation on an [Long] and a [Number].
     *
     * Default implementation uses addition operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val longPlusNumber: Plus<Long, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    /**
     * The subtraction operation on an [Long] and a [Number].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val longMinusNumber: Minus<Long, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    /**
     * The multiplication operation on an [Long] and a [Number].
     *
     * Default implementation uses multiplication operation on two [Number]s and conversion of the [Long] to a [Number].
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public override val longTimesNumber: Times<Long, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
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
    public override val uLongPlusNumber: Plus<ULong, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    /**
     * The subtraction operation on an [ULong] and a [Number].
     *
     * Default implementation uses subtraction operation on two [Number]s and conversion of the [ULong] to a [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val uLongMinusNumber: Minus<ULong, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
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
     * The negation operation on a [Number].
     *
     * @return The negation context represented as [UnaryMinus] instance.
     */
    @KoneContextInclude
    public override val numberUnaryMinus: UnaryMinus<Number, Number>
    /**
     * The subtraction operation on elements of type [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public override val numberMinusNumber: Minus<Number, Number, Number>
    // endregion
    
    public companion object;
    
    /**
     * Registry key for [Ring] interface in [Registry].
     *
     * @param Number The type of elements of the ring.
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Ring<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Ring<Number>> by lazy {
            ImpliedKeysRegistry {
                Semigroup.Key<Number>().impliesSame()
                CommutativeGroup.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Ring.Key<${suppliedTypeOf<Number>()}>"
    }
}

/**
 * Describes a context that represents [mathematical commutative ring](https://en.wikipedia.org/wiki/Ring_(mathematics)).
 *
 * @param Number The type of elements of the ring.
 */
public interface CommutativeRing<Number> : Ring<Number>, CommutativeSemiring<Number> {
    public companion object;
    
    /**
     * Registry key for [CommutativeRing] interface in [Registry].
     *
     * @param Number The type of elements of the commutative ring.
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<CommutativeRing<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeRing<Number>> by lazy {
            ImpliedKeysRegistry {
                Ring.Key<Number>().impliesSame()
                CommutativeSemiring.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeRing.Key<${suppliedTypeOf<Number>()}>"
    }
}