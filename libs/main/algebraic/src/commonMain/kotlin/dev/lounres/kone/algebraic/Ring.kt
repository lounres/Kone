/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
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
 * Describes a context that represents [mathematical commutative ring](https://en.wikipedia.org/wiki/Ring_(mathematics)).
 * It means that it provides operations like `+`, `-` (both unary and binary), `*`, `power`,
 * and some other that satisfy axioms of ring like `a + b == b + a` or `a * (b * c) == (a * b) * c`.
 *
 * Usual structures like integers or real numbers satisfy the axioms (one says that they *form commutative rings*),
 * so you don't need to worry about fully understanding the concept of commutative ring.
 * It won't be true only the moment you introduce such structures as a ring of matrices 2⨯2 with usual operations
 * you learn on any course of linear algebra.
 *
 * Obviously, the [Ring] interface extends the [Equality] interface (via [Semiring] interface)
 * because otherwise there is no understanding of the mathematical operations.
 */
public interface Ring<Number> : Semiring<Number>, CommutativeGroup<Number> {
    // region Integers conversion
    /**
     * Converts instance of [Int] to an element of the [Ring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     */
    public fun valueOf(arg: Int): Number = one doublingTimes arg
    /**
     * Converts instance of [Long] to an element of the [Ring] it is equal to.
     *
     * The result is equal to a sum of [arg] number of units.
     */
    public fun valueOf(arg: Long): Number = one doublingTimes arg
    // endregion

    // region Number-Int operations
    @KoneContextHolderInclude
    public val numberPlusInt: Plus<Number, Int, Number> get() = Plus { other -> numberPlusNumber { this + valueOf(other) } }
    @KoneContextHolderInclude
    public val numberMinusInt: Minus<Number, Int, Number> get() = Minus { other -> numberMinusNumber { this - valueOf(other) } }
    @KoneContextHolderInclude
    public override val numberTimesInt: Times<Number, Int, Number> get() = Times { other -> numberTimesNumber { this * valueOf(other) } }
    // endregion

    // region Number-UInt operations
    @KoneContextHolderInclude
    public override val numberPlusUInt: Plus<Number, UInt, Number> get() = Plus { other -> numberPlusNumber { this + valueOf(other) } }
    @KoneContextHolderInclude
    public val numberMinusUInt: Minus<Number, UInt, Number> get() = Minus { other -> numberMinusNumber { this - valueOf(other) } }
    @KoneContextHolderInclude
    public override val numberTimesUInt: Times<Number, UInt, Number> get() = Times { other -> numberTimesNumber { this * valueOf(other) } }
    // endregion

    // region Number-Long operations
    @KoneContextHolderInclude
    public val numberPlusLong: Plus<Number, Long, Number> get() = Plus { other -> numberPlusNumber { this + valueOf(other) } }
    @KoneContextHolderInclude
    public val numberMinusLong: Minus<Number, Long, Number> get() = Minus { other -> numberMinusNumber { this - valueOf(other) } }
    @KoneContextHolderInclude
    public override val numberTimesLong: Times<Number, Long, Number> get() = Times { other -> numberTimesNumber { this * valueOf(other) } }
    // endregion

    // region Number-ULong operations
    @KoneContextHolderInclude
    public override val numberPlusULong: Plus<Number, ULong, Number> get() = Plus { other -> numberPlusNumber { this + valueOf(other) } }
    @KoneContextHolderInclude
    public val numberMinusULong: Minus<Number, ULong, Number> get() = Minus { other -> numberMinusNumber { this - valueOf(other) } }
    @KoneContextHolderInclude
    public override val numberTimesULong: Times<Number, ULong, Number> get() = Times { other -> numberTimesNumber { this * valueOf(other) } }
    // endregion

    // region Int-Number operations
    @KoneContextHolderInclude
    public val intPlusNumber: Plus<Int, Number, Number> get() = Plus { other -> numberPlusNumber { valueOf(this) + other } }
    @KoneContextHolderInclude
    public val intMinusNumber: Minus<Int, Number, Number> get() = Minus { other -> numberMinusNumber { valueOf(this) - other } }
    @KoneContextHolderInclude
    public override val intTimesNumber: Times<Int, Number, Number> get() = Times { other -> numberTimesNumber { valueOf(this) * other } }
    // endregion

    // region UInt-Number operations
    @KoneContextHolderInclude
    public override val uIntPlusNumber: Plus<UInt, Number, Number> get() = Plus { other -> numberPlusNumber { valueOf(this) + other } }
    @KoneContextHolderInclude
    public val uIntMinusNumber: Minus<UInt, Number, Number> get() = Minus { other -> numberMinusNumber { valueOf(this) - other } }
    @KoneContextHolderInclude
    public override val uIntTimesNumber: Times<UInt, Number, Number> get() = Times { other -> numberTimesNumber { valueOf(this) * other } }
    // endregion

    // region Long-Number operations
    @KoneContextHolderInclude
    public val longPlusNumber: Plus<Long, Number, Number> get() = Plus { other -> numberPlusNumber { valueOf(this) + other } }
    @KoneContextHolderInclude
    public val longMinusNumber: Minus<Long, Number, Number> get() = Minus { other -> numberMinusNumber { valueOf(this) - other } }
    @KoneContextHolderInclude
    public override val longTimesNumber: Times<Long, Number, Number> get() = Times { other -> numberTimesNumber { valueOf(this) * other } }
    // endregion

    // region ULong-Number operations
    @KoneContextHolderInclude
    public override val uLongPlusNumber: Plus<ULong, Number, Number> get() = Plus { other -> numberPlusNumber { valueOf(this) + other } }
    @KoneContextHolderInclude
    public val uLongMinusNumber: Minus<ULong, Number, Number> get() = Minus { other -> numberMinusNumber { valueOf(this) - other } }
    @KoneContextHolderInclude
    public override val uLongTimesNumber: Times<ULong, Number, Number> get() = Times { other -> numberTimesNumber { valueOf(this) * other } }
    // endregion

    // region Number-Number operations
    @KoneContextHolderInclude
    public override val numberUnaryMinus: UnaryMinus<Number, Number>
    @KoneContextHolderInclude
    public override val numberMinusNumber: Minus<Number, Number, Number>
    // endregion
    
    public companion object;
    
    /**
     * Registry key for [Ring] interface in [KoneContextRegistry].
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

public interface CommutativeRing<Number> : Ring<Number>, CommutativeSemiring<Number> {
    public companion object;
    
    /**
     * Registry key for [CommutativeRing] interface in [KoneContextRegistry].
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