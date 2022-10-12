/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.numberTheory

import kotlin.math.*


// region Int

/**
 * Trivial [primality test](https://en.wikipedia.org/wiki/Primality_test) by checking its divisibility by every integer
 * between its square root and 2.
 */
public fun Int.isPrime(): Boolean {
    val n = abs(this)

    if (n <= 1) return false

    val sqRoot = sqrt(n.toDouble()).toInt()

    val primeNumbers = MutableList(sqRoot + 1) { true }

    for (i in 2..sqRoot) {
        if (primeNumbers[i]) {
            if (n % i == 0) return false
            for (j in 0..sqRoot / i) {
                primeNumbers[i * j] = false
            }
        }
    }
    return true
}

/**
 * Test for not primality of [this].
 */
public fun Int.isNotPrime(): Boolean = !isPrime()

// endregion

// region Long

/**
 * Trivial [primality test](https://en.wikipedia.org/wiki/Primality_test) by checking its divisibility by every integer
 * between its square root and 2.
 */
public fun Long.isPrime(): Boolean {
    val n = abs(this)

    if (n <= 1L) return false

    val sqRoot = sqrt(n.toDouble()).toLong()

    val primeNumbers = MutableList((sqRoot + 1L).toInt()) { true }

    for (i in 2L..sqRoot) {
        if (primeNumbers[i.toInt()]) {
            if (n % i == 0L) return false
            for (j in 0..sqRoot / i) {
                primeNumbers[(i * j).toInt()] = false
            }
        }
    }
    return true
}

/**
 * Test for not primality of [this].
 */
public fun Long.isNotPrime(): Boolean = !isPrime()

// endregion