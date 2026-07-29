/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic


/**
 * Throws [ArithmeticException] with message "Division by zero".
 * Used when division by zero is attempted.
 *
 * @throws ArithmeticException Always thrown with message "Division by zero".
 * @return Nothing (always throws).
 */
public fun divisionByZero(): Nothing = throw ArithmeticException("Division by zero")

/**
 * Throws [ArithmeticException] with message "Undefined subtraction result in extended semiring".
 * Used when undefined subtraction is attempted.
 *
 * @throws ArithmeticException Always thrown with message "Undefined subtraction result in extended semiring".
 * @return Nothing (always throws).
 */
public fun negativeSubtractionResultInExtendedSemiring(): Nothing = throw ArithmeticException("Undefined subtraction result in extended semiring")

/**
 * Describes that overflow happened during arithmetic operation.
 * For example, overflow in [Int] or [Long] operations like addition or multiplication.
 *
 * @param message The detail message describing the overflow.
 */
public class OverflowException(message: String? = "Overflow happened") : ArithmeticException(message)

/**
 * Throws [OverflowException] with message "Overflow happened".
 * Used when overflow happened during arithmetic operation.
 *
 * @throws OverflowException Always thrown with message "Overflow happened".
 * @return Nothing (always throws).
 */
public fun overflow(): Nothing = throw OverflowException("Overflow happened")

/**
 * Throws [NumberFormatException] with a message that the [input]'s format is incorrect for the [radix].
 *
 * @param input The input string that has incorrect format.
 * @param radix The radix under which the input is interpreted.
 * @throws NumberFormatException Always thrown with a formatted message including [input] and [radix].
 * @return Nothing (always throws).
 */
public fun numberFormatException(input: String, radix: UInt): Nothing = throw NumberFormatException("For input string \"$input\" under radix $radix")