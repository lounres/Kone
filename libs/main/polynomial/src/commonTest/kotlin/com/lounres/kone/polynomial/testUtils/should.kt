/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.testUtils

import com.lounres.kone.polynomial.*
import com.lounres.kone.utils.mapOperations.mergingAll
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.doubles.plusOrMinus


infix fun <T> Map<T, Double>.plusOrMinus(tolerance: Double): Matcher<Map<T, Double>> = Matcher { other ->
    val passed = mergingAll(this, other, { false }, { false }, { _, expected, actual -> expected.plusOrMinus(tolerance).test(actual).passed() })
    MatcherResult(
        passed,
        { "$other should be equal to $this with tolerance $tolerance" },
        { "$other should not be equal to $this with tolerance $tolerance" }
    )
}

infix fun NumberedPolynomial<Double>.plusOrMinus(tolerance: Double): Matcher<NumberedPolynomial<Double>> {
    val coefficientsMatcher: Matcher<NumberedPolynomialCoefficients<Double>> = coefficients plusOrMinus tolerance
    return Matcher { other ->
        MatcherResult(
            coefficientsMatcher.test(other.coefficients).passed(),
            { "$other should be equal to $this with tolerance $tolerance" },
            { "$other should not be equal to $this with tolerance $tolerance" }
        )
    }
}

infix fun LabeledPolynomial<Double>.plusOrMinus(tolerance: Double): Matcher<LabeledPolynomial<Double>> {
    val coefficientsMatcher: Matcher<LabeledPolynomialCoefficients<Double>> = coefficients plusOrMinus tolerance
    return Matcher { other ->
        MatcherResult(
            coefficientsMatcher.test(other.coefficients).passed(),
            { "$other should be equal to $this with tolerance $tolerance" },
            { "$other should not be equal to $this with tolerance $tolerance" }
        )
    }
}

infix fun NumberedRationalFunction<Double>.plusOrMinus(tolerance: Double): Matcher<NumberedRationalFunction<Double>> =
    Matcher { other ->
        MatcherResult(
            (numerator plusOrMinus tolerance).test(other.numerator).passed() && (denominator plusOrMinus tolerance).test(other.denominator).passed(),
            { "$other should be equal to $this with tolerance $tolerance" },
            { "$other should not be equal to $this with tolerance $tolerance" }
        )
    }

infix fun LabeledRationalFunction<Double>.plusOrMinus(tolerance: Double): Matcher<LabeledRationalFunction<Double>> =
    Matcher { other ->
        MatcherResult(
            (numerator plusOrMinus tolerance).test(other.numerator).passed() && (denominator plusOrMinus tolerance).test(other.denominator).passed(),
            { "$other should be equal to $this with tolerance $tolerance" },
            { "$other should not be equal to $this with tolerance $tolerance" }
        )
    }