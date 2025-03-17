/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic


public fun divisionByZero(): Nothing = throw ArithmeticException("Division by zero")

public fun negativeSubtractionResultInExtendedSemiring(): Nothing = throw ArithmeticException("Negative subtraction result in extended semiring")

public object OverflowException : ArithmeticException("Overflow happened")

public fun overflow(): Nothing = throw OverflowException

public fun numberFormatException(input: String, radix: UInt): Nothing = throw NumberFormatException("For input string \"$input\" under radix $radix")