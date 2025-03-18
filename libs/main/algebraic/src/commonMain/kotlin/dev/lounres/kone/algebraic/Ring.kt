/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


// TODO: Move the reasoning of the interface to docs and leave only a link to it here at the end.
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
 * Obviously, the [Ring] interface extends the [Equality] interface (via [Semiring] interface) because otherwise there is no understanding
 * of the mathematical operations.
 *
 * Such contexts are used instead of usual [plus], [minus], [times] and other overloads for several reasons.
 * Some of them are:
 * - Following the structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context.
 *   (For example, summing two integers together, we assume that we are summing them as two integers,
 *   but not as residues of some modulo.
 *   It means that there is a context that says that for this moment we sum them in this way and not another.
 *   So it should not be part of the context entity (instance) then part of elements themselves.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 */
public interface Ring<Number> : Semiring<Number> {
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
    /**
     * Converts instance of [Int] to an element of the [Ring] it is equal to.
     *
     * The result is equal to a sum of [this] number of units.
     */
    public val Int.value: Number get() = valueOf(this)
    /**
     * Converts instance of [Long] to an element of the [Ring] it is equal to.
     *
     * The result is equal to a sum of [this] number of units.
     */
    public val Long.value: Number get() = valueOf(this)
    // endregion

    // region Number-Int operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`.
     */
    public operator fun Number.plus(other: Int): Number = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`.
     */
    public operator fun Number.minus(other: Int): Number = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`
     */
    public operator fun Number.times(other: Int): Number = this * other.value
    // endregion

    // region Number-UInt operations
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`.
     */
    public operator fun Number.minus(other: UInt): Number = this - other.value
    // endregion

    // region Number-Long operations
    /**
     * Sums [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this + other.value`.
     */
    public operator fun Number.plus(other: Long): Number = this + other.value
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`.
     */
    public operator fun Number.minus(other: Long): Number = this - other.value
    /**
     * Multiplies [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this * other.value`.
     */
    public operator fun Number.times(other: Long): Number = this * other.value
    // endregion

    // region Number-ULong operations
    /**
     * Subtracts [this] number and the [other] integer as elements of the [Ring].
     *
     * The result is equal to `this - other.value`.
     */
    public operator fun Number.minus(other: ULong): Number = this - other.value
    // endregion

    // region Int-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`.
     */
    public operator fun Int.plus(other: Number): Number = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`.
     */
    public operator fun Int.minus(other: Number): Number = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`.
     */
    public operator fun Int.times(other: Number): Number = this.value * other
    // endregion

    // region UInt-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`.
     */
    public operator fun UInt.minus(other: Number): Number = this.value - other
    // endregion

    // region Long-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value + other`.
     */
    public operator fun Long.plus(other: Number): Number = this.value + other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`.
     */
    public operator fun Long.minus(other: Number): Number = this.value - other
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value * other`.
     */
    public operator fun Long.times(other: Number): Number = this.value * other
    // endregion

    // region ULong-Number operations
    /**
     * Sums [this] integer and the [other] number as elements of the [Ring].
     *
     * The result is equal to `this.value - other`.
     */
    public operator fun ULong.minus(other: Number): Number = this.value - other
    // endregion

    // region Number-Number operations
    /**
     * Inverses [this] value in terms of the [Ring].
     */
    public operator fun Number.unaryMinus(): Number
    /**
     * Subtracts [this] and the [other] numbers in terms of the [Ring].
     */
    public operator fun Number.minus(other: Number): Number
    // endregion
    
    /**
     * Registry key for [Ring] interface in [KoneContextRegistry].
     */
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<Ring<Number>> {
        override val typeKey: SuppliedType.Regular<Ring<Number>> =
            SuppliedType.Regular(
                kClass = Ring::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
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
/**
 * Converts instance of [Int] to an element of the [Ring] it is equal to.
 *
 * The result is equal to a sum of [this] number of units.
 *
 * A bridge contextual property for [Ring.value].
 */
context(ring: Ring<Number>)
public val <Number> Int.value: Number get() = with(ring) { this@value.value }
/**
 * Converts instance of [Long] to an element of the [Ring] it is equal to.
 *
 * The result is equal to a sum of [this] number of units.
 *
 * A bridge contextual property for [Ring.value].
 */
context(ring: Ring<Number>)
public val <Number> Long.value: Number get() = with(ring) { this@value.value }
// endregion

// region Number-Int operations
/**
 * Sums [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this + other.value`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: Int): Number = with(ring) { this@plus + other }
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - other.value`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Int): Number = with(ring) { this@minus - other }
/**
 * Multiplies [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this * other.value`
 *
 * A bridge contextual function for [Ring.times].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.times(other: Int): Number = with(ring) { this@times * other }
// endregion

// region Number-UInt operations
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - other.value`.
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
 * The result is equal to `this + other.value`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.plus(other: Long): Number = with(ring) { this@plus + other }
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - other.value`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Long): Number = with(ring) { this@minus - other }
/**
 * Multiplies [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this * other.value`.
 *
 * A bridge contextual function for [Ring.times].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.times(other: Long): Number = with(ring) { this@times * other }
// endregion

// region Number-ULong operations
/**
 * Subtracts [this] number and the [other] integer as elements of the [Ring].
 *
 * The result is equal to `this - other.value`.
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
 * The result is equal to `this.value + other`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Int.plus(other: Number): Number = with(ring) { this@plus + other }
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `this.value - other`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Int.minus(other: Number): Number = with(ring) { this@minus - other }
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `this.value * other`.
 *
 * A bridge contextual function for [Ring.times].
 */
context(ring: Ring<Number>)
public operator fun <Number> Int.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region UInt-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `this.value - other`.
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
 * The result is equal to `this.value + other`.
 *
 * A bridge contextual function for [Ring.plus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Long.plus(other: Number): Number = with(ring) { this@plus + other }
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `this.value - other`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Long.minus(other: Number): Number = with(ring) { this@minus - other }
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `this.value * other`.
 *
 * A bridge contextual function for [Ring.times].
 */
context(ring: Ring<Number>)
public operator fun <Number> Long.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region ULong-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Ring].
 *
 * The result is equal to `this.value - other`.
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> ULong.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region Number-Number operations
/**
 * Inverses [this] value in terms of the [Ring].
 *
 * A bridge contextual function for [Ring.unaryMinus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.unaryMinus(): Number = with(ring) { -this@unaryMinus }
/**
 * Subtracts [this] and the [other] numbers in terms of the [Ring].
 *
 * A bridge contextual function for [Ring.minus].
 */
context(ring: Ring<Number>)
public operator fun <Number> Number.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion