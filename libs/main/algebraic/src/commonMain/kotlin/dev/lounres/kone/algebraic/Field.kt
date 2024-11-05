/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic


/**
 * Describes a context that represents [mathematical field](https://en.wikipedia.org/wiki/Field_(mathematics)).
 * It means that it is an extension of [Ring] interface that also provides division and exponentiation to the negative
 * integer power. See docs of [Ring] for a full description and docs of the [Field] interface's operations.
 */
public interface Field<Number>: Ring<Number> {
    /**
     * Divides [this] number by [other] number in terms of the [Field].
     */
    public operator fun Number.div(other: Number): Number
    /**
     * Finds reciprocal of [this] number in terms of the [Field].
     *
     * The result is equal to `one / this`.
     */
    public val Number.reciprocal: Number get() = one / this
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun Number.div(other: Int): Number = this / other.value
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun Number.div(other: UInt): Number = this / other.value
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun Number.div(other: Long): Number = this / other.value
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun Number.div(other: ULong): Number = this / other.value
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun Int.div(other: Number): Number = this.value / other
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun UInt.div(other: Number): Number = this.value / other
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun Long.div(other: Number): Number = this.value / other
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `this / other.value`.
     */
    public operator fun ULong.div(other: Number): Number = this.value / other
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [base] copies otherwise.
     */
    public fun power(base: Number, exponent: Int): Number =
        if (exponent >= 0) power(base, exponent.toUInt())
        else one / power(base, (-exponent).toUInt())
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [base] copies otherwise.
     */
    public fun power(base: Number, exponent: Long): Number =
        if (exponent >= 0) power(base, exponent.toULong())
        else one / power(base, (-exponent).toULong())
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [this] copies otherwise.
     */
    public infix fun Number.pow(exponent: Int): Number = power(this, exponent)
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [this] copies otherwise.
     */
    public infix fun Number.pow(exponent: Long): Number = power(this, exponent)
}