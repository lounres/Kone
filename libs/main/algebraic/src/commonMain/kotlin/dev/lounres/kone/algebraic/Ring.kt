/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.contexts.KoneContextInclude
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
    @KoneContextInclude
    public val numberPlusInt: Plus<Number, Int, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    @KoneContextInclude
    public val numberMinusInt: Minus<Number, Int, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    @KoneContextInclude
    public override val numberTimesInt: Times<Number, Int, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
    // endregion

    // region Number-UInt operations
    @KoneContextInclude
    public override val numberPlusUInt: Plus<Number, UInt, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    @KoneContextInclude
    public val numberMinusUInt: Minus<Number, UInt, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    @KoneContextInclude
    public override val numberTimesUInt: Times<Number, UInt, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
    // endregion

    // region Number-Long operations
    @KoneContextInclude
    public val numberPlusLong: Plus<Number, Long, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    @KoneContextInclude
    public val numberMinusLong: Minus<Number, Long, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    @KoneContextInclude
    public override val numberTimesLong: Times<Number, Long, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
    // endregion

    // region Number-ULong operations
    @KoneContextInclude
    public override val numberPlusULong: Plus<Number, ULong, Number> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    @KoneContextInclude
    public val numberMinusULong: Minus<Number, ULong, Number> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    @KoneContextInclude
    public override val numberTimesULong: Times<Number, ULong, Number> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
    // endregion

    // region Int-Number operations
    @KoneContextInclude
    public val intPlusNumber: Plus<Int, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    @KoneContextInclude
    public val intMinusNumber: Minus<Int, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    @KoneContextInclude
    public override val intTimesNumber: Times<Int, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
    // endregion

    // region UInt-Number operations
    @KoneContextInclude
    public override val uIntPlusNumber: Plus<UInt, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    @KoneContextInclude
    public val uIntMinusNumber: Minus<UInt, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    @KoneContextInclude
    public override val uIntTimesNumber: Times<UInt, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
    // endregion

    // region Long-Number operations
    @KoneContextInclude
    public val longPlusNumber: Plus<Long, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    @KoneContextInclude
    public val longMinusNumber: Minus<Long, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    @KoneContextInclude
    public override val longTimesNumber: Times<Long, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
    // endregion

    // region ULong-Number operations
    @KoneContextInclude
    public override val uLongPlusNumber: Plus<ULong, Number, Number> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    @KoneContextInclude
    public val uLongMinusNumber: Minus<ULong, Number, Number> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    @KoneContextInclude
    public override val uLongTimesNumber: Times<ULong, Number, Number> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
    // endregion

    // region Number-Number operations
    @KoneContextInclude
    public override val numberUnaryMinus: UnaryMinus<Number, Number>
    @KoneContextInclude
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