/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial.testUtils

import dev.lounres.kone.polynomial.LabeledPolynomial
import dev.lounres.kone.polynomial.LabeledRationalFunction
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


fun <T> assertContentEquals(
    expected: Map<T, Double>,
    actual: Map<T, Double>,
    absoluteTolerance: Double,
    message: String? = null
) {
    assertEquals(expected.keys, actual.keys, message)
    for ((key, expectedValue) in expected) assertEquals(expectedValue, actual[key]!!, absoluteTolerance, message)
}

fun assertEquals(
    expected: LabeledPolynomial<Double>,
    actual: LabeledPolynomial<Double>,
    absoluteTolerance: Double,
    message: String? = null
) {
    assertContentEquals(
        expected.coefficients,
        actual.coefficients,
        absoluteTolerance,
        message
    )
}

fun assertEquals(
    expected: LabeledRationalFunction<Double>,
    actual: LabeledRationalFunction<Double>,
    absoluteTolerance: Double,
    message: String? = null
) {
    assertEquals(
        expected.numerator,
        actual.numerator,
        absoluteTolerance,
        message
    )
    assertEquals(
        expected.denominator,
        actual.denominator,
        absoluteTolerance,
        message
    )
}

inline fun <reified T : Throwable> assertFailsWithTypeAndMessage(
    expectedMessage: String? = null,
    assertionMessage: String? = null,
    block: () -> Unit
) {
    assertEquals(
        expectedMessage,
        assertFailsWith(T::class, assertionMessage, block).message,
        assertionMessage
    )
}