/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.contexts.KoneContextRegistry
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
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + valueOf(other)`.
     */
    public operator fun Number.plus(other: Int): Number = this + valueOf(other)
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - valueOf(other)`.
     */
    public operator fun Number.minus(other: Int): Number = this - valueOf(other)
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * valueOf(other)`
     */
    public override operator fun Number.times(other: Int): Number = this * valueOf(other)
    // endregion

    // region Number-UInt operations
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - valueOf(other)`.
     */
    public operator fun Number.minus(other: UInt): Number = this - valueOf(other)
    // endregion

    // region Number-Long operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + valueOf(other)`.
     */
    public operator fun Number.plus(other: Long): Number = this + valueOf(other)
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - valueOf(other)`.
     */
    public operator fun Number.minus(other: Long): Number = this - valueOf(other)
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * valueOf(other)`.
     */
    public override operator fun Number.times(other: Long): Number = this * valueOf(other)
    // endregion

    // region Number-ULong operations
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - valueOf(other)`.
     */
    public operator fun Number.minus(other: ULong): Number = this - valueOf(other)
    // endregion

    // region Int-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) + other`.
     */
    public operator fun Int.plus(other: Number): Number = valueOf(this) + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) - other`.
     */
    public operator fun Int.minus(other: Number): Number = valueOf(this) - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) * other`.
     */
    public override operator fun Int.times(other: Number): Number = valueOf(this) * other
    // endregion

    // region UInt-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) - other`.
     */
    public operator fun UInt.minus(other: Number): Number = valueOf(this) - other
    // endregion

    // region Long-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) + other`.
     */
    public operator fun Long.plus(other: Number): Number = valueOf(this) + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) - other`.
     */
    public operator fun Long.minus(other: Number): Number = valueOf(this) - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) * other`.
     */
    public override operator fun Long.times(other: Number): Number = valueOf(this) * other
    // endregion

    // region ULong-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `valueOf(this) - other`.
     */
    public operator fun ULong.minus(other: Number): Number = valueOf(this) - other
    // endregion

    // region Number-Number operations
    /**
     * Inverses [this] value in terms of the [Ring].
     */
    public override operator fun Number.unaryMinus(): Number
    /**
     * Subtracts [this] and the [other] numbers in terms of the [Ring].
     */
    public override operator fun Number.minus(other: Number): Number
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


// region Integers conversion
/**
 * Converts instance of [Int] to an element of the [Ring] it is equal to.
 *
 * The result is equal to a sum of [arg] number of units.
 *
 * A bridge contextual function for [Ring.valueOf].
 */
context(ring: Ring<Number>)
public fun <Number> valueOf(arg: Int): Number = ring.valueOf(arg)
/**
 * Converts instance of [Long] to an element of the [Ring] it is equal to.
 *
 * The result is equal to a sum of [arg] number of units.
 *
 * A bridge contextual function for [Ring.valueOf].
 */
context(ring: Ring<Number>)
public fun <Number> valueOf(arg: Long): Number = ring.valueOf(arg)
// endregion

// region Number-Int operations
/**
 * Sums [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this + valueOf(other)`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: Int): Number = with(ring) { this@plus + other }
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - valueOf(other)`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Int): Number = with(ring) { this@minus - other }
// endregion

// region Number-UInt operations
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - valueOf(other)`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: UInt): Number = with(ring) { this@minus - other }
// endregion

// region Number-Long operations
/**
 * Sums [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this + valueOf(other)`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: Long): Number = with(ring) { this@plus + other }
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - valueOf(other)`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Long): Number = with(ring) { this@minus - other }
// endregion

// region Number-ULong operations
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - valueOf(other)`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: ULong): Number = with(ring) { this@minus - other }
// endregion

// region Int-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) + other`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Int.plus(other: Number): Number = with(ring) { this@plus + other }
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) - other`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Int.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region UInt-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) - other`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> UInt.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region Long-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) + other`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Long.plus(other: Number): Number = with(ring) { this@plus + other }
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) - other`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Long.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region ULong-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `valueOf(this) - other`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> ULong.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion


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