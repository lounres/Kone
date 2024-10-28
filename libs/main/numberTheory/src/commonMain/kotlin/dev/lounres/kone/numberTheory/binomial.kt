/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.numberTheory

import dev.lounres.kone.algebraic.Field


// region Int

/**
 * Computes [binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient).
 */
public fun binomial(n: Int, k: Int): Int {
    require(k >= 0) { "Binomial coefficient C(n, k) is undefined for negative k=$k"}
    if (n < k) return 0
    val k = if (k <= n / 2) k else n-k
    var acc = 1
    for (i in 1..k) acc = acc * (n - (i - 1)) / i
    return acc
}

/**
 * Computes [binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Binomial_coefficients_as_polynomials).
 */
context(Field<N>)
public fun <N> binomialPolynomial(n: N, k: Int): N {
    require(k >= 0) { "Binomial coefficient C(n, k) is undefined for negative k=$k"}
    var acc = one
    for (i in 1..k) acc = acc * (n - (i - 1)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
public fun risingBinomial(n: Int, k: Int): Int {
    require(k >= 0) { "Rising binomial coefficient C(n+k, k) is undefined for negative k=$k"}
    var acc = 1
    for (i in 1..k) acc = acc * (n + (i - 1)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
context(Field<N>)
public fun <N> risingBinomialPolynomial(n: N, k: Int): N {
    require(k >= 0) { "Binomial coefficient C(n, k) is undefined for negative k=$k"}
    var acc = one
    for (i in 1..k) acc = acc * (n + (i - 1)) / i
    return acc
}

// endregion

// region UInt

/**
 * Computes [binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient).
 */
public fun binomial(n: UInt, k: UInt): UInt {
    if (n < k) return 0u
    val k = if (k <= n / 2u) k else n-k
    var acc = 1u
    for (i in 1u..k) acc = acc * (n - (i - 1u)) / i
    return acc
}

/**
 * Computes [binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Binomial_coefficients_as_polynomials).
 */
context(Field<N>)
public fun <N> binomialPolynomial(n: N, k: UInt): N {
    var acc = one
    for (i in 1u..k) acc = acc * (n - (i - 1u)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
public fun risingBinomial(n: UInt, k: UInt): UInt {
    var acc = 1u
    for (i in 1u..k) acc = acc * (n + (i - 1u)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
context(Field<N>)
public fun <N> risingBinomialPolynomial(n: N, k: UInt): N {
    var acc = one
    for (i in 1u..k) acc = acc * (n + (i - 1u)) / i
    return acc
}

// endregion

// region Long

/**
 * Computes [binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient).
 */
public fun binomial(n: Long, k: Long): Long {
    require(k >= 0L) { "Binomial coefficient C(n, k) is undefined for negative k=$k"}
    if (n < k) return 0L
    val k = if (k <= n / 2L) k else n-k
    var acc = 1L
    for (i in 1L..k) acc = acc * (n - (i - 1L)) / i
    return acc
}

/**
 * Computes [binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Binomial_coefficients_as_polynomials).
 */
context(Field<N>)
public fun <N> binomialPolynomial(n: N, k: Long): N {
    require(k >= 0L) { "Binomial coefficient C(n, k) is undefined for negative k=$k"}
    var acc = one
    for (i in 1L..k) acc = acc * (n - (i - 1L)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
public fun risingBinomial(n: Long, k: Long): Long {
    require(k >= 0L) { "Rising binomial coefficient C(n+k, k) is undefined for negative k=$k"}
    var acc = 1L
    for (i in 1L..k) acc = acc * (n + (i - 1L)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
context(Field<N>)
public fun <N> risingBinomialPolynomial(n: N, k: Long): N {
    require(k >= 0L) { "Binomial coefficient C(n, k) is undefined for negative k=$k"}
    var acc = one
    for (i in 1L..k) acc = acc * (n + (i - 1L)) / i
    return acc
}

// endregion

// region ULong

/**
 * Computes [binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient).
 */
public fun binomial(n: ULong, k: ULong): ULong {
    if (n < k) return 0uL
    val k = if (k <= n / 2uL) k else n-k
    var acc = 1uL
    for (i in 1uL..k) acc = acc * (n - (i - 1uL)) / i
    return acc
}

/**
 * Computes [binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Binomial_coefficients_as_polynomials).
 */
context(Field<N>)
public fun <N> binomialPolynomial(n: N, k: ULong): N {
    require(k >= 0uL) { "Binomial coefficient C(n, k) is undefined for negative k=$k"}
    var acc = one
    for (i in 1uL..k) acc = acc * (n - (i - 1uL)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k)](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
public fun risingBinomial(n: ULong, k: ULong): ULong {
    var acc = 1uL
    for (i in 1uL..k) acc = acc * (n + (i - 1uL)) / i
    return acc
}

/**
 * Computes [rising binomial coefficient C(n, k) as polynomial of n](https://en.wikipedia.org/wiki/Binomial_coefficient#Multiset_(rising)_binomial_coefficient).
 */
context(Field<N>)
public fun <N> risingBinomialPolynomial(n: N, k: ULong): N {
    var acc = one
    for (i in 1uL..k) acc = acc * (n + (i - 1uL)) / i
    return acc
}

// endregion