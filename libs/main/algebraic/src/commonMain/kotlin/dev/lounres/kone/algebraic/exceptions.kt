/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic


/**
 * Throws [ArithmeticException] with message "Division by zero".
 * Used when division by zero is attempted.
 */
public fun divisionByZero(): Nothing = throw ArithmeticException("Division by zero")

/**
 * Throws [ArithmeticException] with message "Undefined subtraction result in extended semiring".
 * Used when undefined subtraction is attempted.
 */
public fun negativeSubtractionResultInExtendedSemiring(): Nothing = throw ArithmeticException("Undefined subtraction result in extended semiring")

/**
 * Describes that overflow happened during arithmetic operation.
 * For example, overflow in [Int] or [Long] operations like addition or multiplication.
 */
public class OverflowException(message: String? = "Overflow happened") : ArithmeticException(message)

/**
 * Throws [OverflowException] with message "Overflow happened".
 * Used when overflow happened during arithmetic operation.
 */
public fun overflow(): Nothing = throw OverflowException("Overflow happened")

/**
 * Throws [NumberFormatException] with a message that the [input]'s format is incorrect for the [radix].
 */
public fun numberFormatException(input: String, radix: UInt): Nothing = throw NumberFormatException("For input string \"$input\" under radix $radix")