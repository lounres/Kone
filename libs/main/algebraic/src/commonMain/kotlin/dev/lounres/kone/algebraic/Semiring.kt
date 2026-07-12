/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.contexts.KoneContextHolderInclude
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


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
    @KoneContextHolderInclude
    public override val numberIsZero: IsZero<Number>
    @KoneContextHolderInclude
    public val numberIsOne: IsOne<Number>
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
    @KoneContextHolderInclude
    public val numberPlusUInt: Plus<Number, UInt, Number> get() = Plus { other -> numberPlusNumber { this + valueOf(other) } }
    @KoneContextHolderInclude
    public override val numberTimesUInt: Times<Number, UInt, Number> get() = Times { other -> numberTimesNumber { this * valueOf(other) } }
    // endregion
    
    // region Number-ULong operations
    @KoneContextHolderInclude
    public val numberPlusULong: Plus<Number, ULong, Number> get() = Plus { other -> numberPlusNumber { this + valueOf(other) } }
    @KoneContextHolderInclude
    public override val numberTimesULong: Times<Number, ULong, Number> get() = Times { other -> numberTimesNumber { this * valueOf(other) } }
    // endregion
    
    // region UInt-Number operations
    @KoneContextHolderInclude
    public val uIntPlusNumber: Plus<UInt, Number, Number> get() = Plus { other -> numberPlusNumber { valueOf(this) + other } }
    @KoneContextHolderInclude
    public override val uIntTimesNumber: Times<UInt, Number, Number> get() = Times { other -> numberTimesNumber { valueOf(this) * other } }
    // endregion
    
    // region ULong-Number operations
    @KoneContextHolderInclude
    public val uLongPlusNumber: Plus<ULong, Number, Number> get() = Plus { other -> numberPlusNumber { valueOf(this) + other } }
    @KoneContextHolderInclude
    public override val uLongTimesNumber: Times<ULong, Number, Number> get() = Times { other -> numberTimesNumber { valueOf(this) * other } }
    // endregion
    
    // region Number-Number operations
    @KoneContextHolderInclude
    public override val numberPlusNumber: Plus<Number, Number, Number>
    @KoneContextHolderInclude
    public val numberTimesNumber: Times<Number, Number, Number>
    @KoneContextHolderInclude
    public val powerNumberUInt: Power<Number, UInt, Number> get() = Power { base, exponent -> base squaringPower exponent }
    @KoneContextHolderInclude
    public val powerNumberULong: Power<Number, ULong, Number> get() = Power { base, exponent -> base squaringPower exponent }
    // endregion
    
    public companion object;
    
    /**
     * Registry key for [Semiring] interface in [KoneContextRegistry].
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

public interface CommutativeSemiring<Number> : Semiring<Number> {
    public companion object;
    
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
    @KoneContextHolderInclude
    public val numberMinusUInt: Minus<Number, UInt, Number> get() = Minus { other -> numberMinusNumber { this - valueOf(other) } }
    // endregion
    
    // region Number-ULong operations
    @KoneContextHolderInclude
    public val numberMinusULong: Minus<Number, ULong, Number> get() = Minus { other -> numberMinusNumber { this - valueOf(other) } }
    // endregion
    
    // region UInt-Number operations
    @KoneContextHolderInclude
    public val uIntMinusNumber: Minus<UInt, Number, Number> get() = Minus { other -> numberMinusNumber { valueOf(this) - other } }
    // endregion
    
    // region ULong-Number operations
    @KoneContextHolderInclude
    public val uLongMinusNumber: Minus<ULong, Number, Number> get() = Minus { other -> numberMinusNumber { valueOf(this) - other } }
    // endregion
    
    // region Number-Number operations
    @KoneContextHolderInclude
    public val numberMinusNumber: Minus<Number, Number, Number>
    // endregion
    
    public companion object;
}