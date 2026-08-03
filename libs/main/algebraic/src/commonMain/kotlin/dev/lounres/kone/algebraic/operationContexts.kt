/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


/**
 * An operation context for the unary plus operator.
 *
 * @param Input The type of the operand.
 * @param Result The type of the result.
 */
@GenerateKoneContextKey
public interface UnaryPlus<in Input, out Result> : KoneContext {
    /**
     * Applies unary plus to [this] element.
     *
     * @receiver The operand.
     * @return The result of unary plus.
     */
    public operator fun Input.unaryPlus(): Result
}

/**
 * Creates a [UnaryPlus] instance from a lambda.
 *
 * @param Input The type of the operand.
 * @param Result The type of the result.
 * @param block The function implementing unary plus.
 * @return A new [UnaryPlus] instance.
 */
public inline fun <Input, Result> UnaryPlus(crossinline block: (Input) -> Result): UnaryPlus<Input, Result> = object : UnaryPlus<Input, Result> {
    override fun Input.unaryPlus(): Result = block(this)
}

/**
 * Applies unary plus using the given [UnaryPlus] context.
 *
 * @param unaryPlus The context providing the unary plus operation.
 * @receiver The operand.
 * @param Input The type of the operand.
 * @param Result The type of the result.
 * @return The result of unary plus.
 */
context(unaryPlus: UnaryPlus<Input, Result>)
public operator fun <Input, Result> Input.unaryPlus(): Result = with(unaryPlus) { +this@unaryPlus }

/**
 * An operation context for the unary minus (negation) operator.
 *
 * @param Input The type of the operand.
 * @param Result The type of the result.
 */
@GenerateKoneContextKey
public interface UnaryMinus<in Input, out Result> : KoneContext {
    /**
     * Applies unary minus (negation) to [this] element.
     *
     * @receiver The operand.
     * @return The negated value.
     */
    public operator fun Input.unaryMinus(): Result
}

/**
 * Creates a [UnaryMinus] instance from a lambda.
 *
 * @param Input The type of the operand.
 * @param Result The type of the result.
 * @param block The function implementing unary minus.
 * @return A new [UnaryMinus] instance.
 */
public inline fun <Input, Result> UnaryMinus(crossinline block: (Input) -> Result): UnaryMinus<Input, Result> = object : UnaryMinus<Input, Result> {
    override fun Input.unaryMinus(): Result = block(this)
}

/**
 * Applies unary minus using the given [UnaryMinus] context.
 *
 * @param unaryMinus The context providing the unary minus operation.
 * @receiver The operand.
 * @param Input The type of the operand.
 * @param Result The type of the result.
 * @return The negated value.
 */
context(unaryMinus: UnaryMinus<Input, Result>)
public operator fun <Input, Result> Input.unaryMinus(): Result = with(unaryMinus) { -this@unaryMinus }

/**
 * An operation context for the binary plus (addition) operator.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 */
@GenerateKoneContextKey
public interface Plus<in Left, in Right, out Result> : KoneContext {
    /**
     * Adds [other] to [this] element.
     *
     * @receiver The left operand.
     * @param other The right operand.
     * @return The sum of [this] and [other].
     */
    public operator fun Left.plus(other: Right): Result
}

/**
 * Creates a [Plus] instance from a lambda.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @param block The function implementing addition.
 * @return A new [Plus] instance.
 */
public inline fun <Left, Right, Result> Plus(crossinline block: (left: Left, right: Right) -> Result): Plus<Left, Right, Result> = object : Plus<Left, Right, Result> {
    override fun Left.plus(other: Right): Result = block(this, other)
}

/**
 * Performs addition using the given [Plus] context.
 *
 * @param plus The context providing the addition operation.
 * @receiver The left operand.
 * @param other The right operand.
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @return The sum of [this] and [other].
 */
context(plus: Plus<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.plus(other: Right): Result = with(plus) { this@plus + other }

/**
 * An operation context for the binary minus (subtraction) operator.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 */
@GenerateKoneContextKey
public interface Minus<in Left, in Right, out Result> : KoneContext {
    /**
     * Subtracts [other] from [this] element.
     *
     * @receiver The left operand (minuend).
     * @param other The right operand (subtrahend).
     * @return The difference of [this] and [other].
     */
    public operator fun Left.minus(other: Right): Result
}

/**
 * Creates a [Minus] instance from a lambda.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @param block The function implementing subtraction.
 * @return A new [Minus] instance.
 */
public inline fun <Left, Right, Result> Minus(crossinline block: (left: Left, right: Right) -> Result): Minus<Left, Right, Result> = object : Minus<Left, Right, Result> {
    override fun Left.minus(other: Right): Result = block(this, other)
}

/**
 * Performs subtraction using the given [Minus] context.
 *
 * @param minus The context providing the subtraction operation.
 * @receiver The left operand (minuend).
 * @param other The right operand (subtrahend).
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @return The difference of [this] and [other].
 */
context(minus: Minus<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.minus(other: Right): Result = with(minus) { this@minus - other }

/**
 * An operation context for the multiplication operator.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 */
@GenerateKoneContextKey
public interface Times<in Left, in Right, out Result> : KoneContext {
    /**
     * Multiplies [this] element by [other].
     *
     * @receiver The left operand.
     * @param other The right operand.
     * @return The product of [this] and [other].
     */
    public operator fun Left.times(other: Right): Result
}

/**
 * Creates a [Times] instance from a lambda.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @param block The function implementing multiplication.
 * @return A new [Times] instance.
 */
public inline fun <Left, Right, Result> Times(crossinline block: (left: Left, right: Right) -> Result): Times<Left, Right, Result> = object : Times<Left, Right, Result> {
    override fun Left.times(other: Right): Result = block(this, other)
}

/**
 * Performs multiplication using the given [Times] context.
 *
 * @param times The context providing the multiplication operation.
 * @receiver The left operand.
 * @param other The right operand.
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @return The product of [this] and [other].
 */
context(times: Times<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.times(other: Right): Result = with(times) { this@times * other }

/**
 * An operation context for the division operator.
 *
 * @param Left The type of the left operand (dividend).
 * @param Right The type of the right operand (divisor).
 * @param Result The type of the result (quotient).
 */
@GenerateKoneContextKey
public interface Divide<in Left, in Right, out Result> : KoneContext {
    /**
     * Divides [this] element by [other].
     *
     * @receiver The dividend.
     * @param other The divisor.
     * @return The quotient of [this] divided by [other].
     */
    public operator fun Left.div(other: Right): Result
}

/**
 * Creates a [Divide] instance from a lambda.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @param block The function implementing division.
 * @return A new [Divide] instance.
 */
public inline fun <Left, Right, Result> Divide(crossinline block: (left: Left, right: Right) -> Result): Divide<Left, Right, Result> = object : Divide<Left, Right, Result> {
    override fun Left.div(other: Right): Result = block(this, other)
}

/**
 * Performs division using the given [Divide] context.
 *
 * @param divide The context providing the division operation.
 * @receiver The dividend.
 * @param other The divisor.
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @return The quotient of [this] divided by [other].
 */
context(divide: Divide<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.div(other: Right): Result = with(divide) { this@div / other }

/**
 * An operation context for computing the reciprocal (multiplicative inverse).
 *
 * @param Input The type of the operand.
 * @param Result The type of the result.
 */
@GenerateKoneContextKey
public interface Reciprocal<in Input, out Result> : KoneContext {
    /**
     * Computes the reciprocal of [this] element.
     *
     * @receiver The element to compute the reciprocal of.
     * @return The reciprocal of [this].
     */
    public fun Input.reciprocal(): Result
}

/**
 * Creates a [Reciprocal] instance from a lambda.
 *
 * @param Input The type of the operand.
 * @param Result The type of the result.
 * @param block The function implementing reciprocal.
 * @return A new [Reciprocal] instance.
 */
public inline fun <Input, Result> Reciprocal(crossinline block: (Input) -> Result): Reciprocal<Input, Result> = object : Reciprocal<Input, Result> {
    override fun Input.reciprocal(): Result = block(this)
}

/**
 * Computes the reciprocal using the given [Reciprocal] context.
 *
 * @param divide The context providing the reciprocal operation.
 * @receiver The element to compute the reciprocal of.
 * @param Input The type of the operand.
 * @param Result The type of the result.
 * @return The reciprocal of [this].
 */
context(divide: Reciprocal<Input, Result>)
public fun <Input, Result> Input.reciprocal(): Result = with(divide) { this@reciprocal.reciprocal() }

/**
 * An operation context for the remainder (modulo) operator.
 *
 * @param Left The type of the left operand (dividend).
 * @param Right The type of the right operand (divisor).
 * @param Result The type of the result (remainder).
 */
@GenerateKoneContextKey
public interface Remainder<in Left, in Right, out Result> : KoneContext {
    /**
     * Computes the remainder of [this] element divided by [other].
     *
     * @receiver The dividend.
     * @param other The divisor.
     * @return The remainder of [this] divided by [other].
     */
    public operator fun Left.rem(other: Right): Result
}

/**
 * Creates a [Remainder] instance from a lambda.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @param block The function implementing remainder.
 * @return A new [Remainder] instance.
 */
public inline fun <Left, Right, Result> Remainder(crossinline block: (left: Left, right: Right) -> Result): Remainder<Left, Right, Result> = object : Remainder<Left, Right, Result> {
    override fun Left.rem(other: Right): Result = block(this, other)
}

/**
 * Computes the remainder using the given [Remainder] context.
 *
 * @param remainder The context providing the remainder operation.
 * @receiver The dividend.
 * @param other The divisor.
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @return The remainder of [this] divided by [other].
 */
context(remainder: Remainder<Left, Right, Result>)
public operator fun <Left, Right, Result> Left.rem(other: Right): Result = with(remainder) { this@rem.rem(other) }

/**
 * An operation context for Euclidean division (division with remainder).
 *
 * @param Left The type of the left operand (dividend).
 * @param Right The type of the right operand (divisor).
 * @param Result The type of the result containing both quotient and remainder.
 */
@GenerateKoneContextKey
public interface DivideRemainder<in Left, in Right, out Result> : KoneContext {
    /**
     * Performs Euclidean division of [this] element by [other].
     *
     * @receiver The dividend.
     * @param other The divisor.
     * @return The result of division with remainder.
     */
    public infix fun Left.divrem(other: Right): Result
}

/**
 * Creates a [DivideRemainder] instance from a lambda.
 *
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @param block The function implementing Euclidean division.
 * @return A new [DivideRemainder] instance.
 */
public inline fun <Left, Right, Result> DivideRemainder(crossinline block: (left: Left, right: Right) -> Result): DivideRemainder<Left, Right, Result> = object : DivideRemainder<Left, Right, Result> {
    override fun Left.divrem(other: Right): Result = block(this, other)
}

/**
 * Performs Euclidean division using the given [DivideRemainder] context.
 *
 * @param divideRemainder The context providing the Euclidean division operation.
 * @receiver The dividend.
 * @param other The divisor.
 * @param Left The type of the left operand.
 * @param Right The type of the right operand.
 * @param Result The type of the result.
 * @return The result of division with remainder.
 */
context(divideRemainder: DivideRemainder<Left, Right, Result>)
public infix fun <Left, Right, Result> Left.divrem(other: Right): Result = with(divideRemainder) { this@divrem.divrem(other) }

/**
 * An operation context for exponentiation (power).
 *
 * @param Base The type of the base.
 * @param Exponent The type of the exponent.
 * @param Result The type of the result.
 */
@GenerateKoneContextKey
public interface Power<in Base, in Exponent, out Result> : KoneContext {
    /**
     * Raises [base] to the power of [exponent].
     *
     * @param base The base.
     * @param exponent The exponent.
     * @return The result of [base] raised to [exponent].
     */
    public fun power(base: Base, exponent: Exponent): Result
}

/**
 * Creates a [Power] instance from a lambda.
 *
 * @param Base The type of the base.
 * @param Exponent The type of the exponent.
 * @param Result The type of the result.
 * @param block The function implementing exponentiation.
 * @return A new [Power] instance.
 */
public inline fun <Base, Exponent, Result> Power(crossinline block: (base: Base, exponent: Exponent) -> Result): Power<Base, Exponent, Result> = object : Power<Base, Exponent, Result> {
    override fun power(base: Base, exponent: Exponent): Result = block(base, exponent)
}

/**
 * Raises [base] to the power of [exponent] using the given [Power] context.
 *
 * @param power The context providing the exponentiation operation.
 * @param base The base.
 * @param exponent The exponent.
 * @param Base The type of the base.
 * @param Exponent The type of the exponent.
 * @param Result The type of the result.
 * @return The result of [base] raised to [exponent].
 */
context(power: Power<Base, Exponent, Result>)
public fun <Base, Exponent, Result> power(base: Base, exponent: Exponent): Result = with(power) { power(base, exponent) }

/**
 * Raises [this] element to the power of [exponent] using the given [Power] context (infix notation).
 *
 * @param power The context providing the exponentiation operation.
 * @receiver The base.
 * @param exponent The exponent.
 * @param Base The type of the base.
 * @param Exponent The type of the exponent.
 * @param Result The type of the result.
 * @return The result of [this] raised to [exponent].
 */
context(power: Power<Base, Exponent, Result>)
public infix fun <Base, Exponent, Result> Base.pow(exponent: Exponent): Result = with(power) { power(this@pow, exponent) }