/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.numberTheory

import kotlin.math.abs


// region Int

/**
 * Computes [Kronecker symbol](https://en.wikipedia.org/wiki/Kronecker_symbol) of entries [a] and [b].
 */
@Suppress("NAME_SHADOWING")
public fun kroneckerSymbol(a: Int, b: Int): Int {
    // Simple cases: when
    // `b` is `0`,
    if (b == 0) return if (a == 1 || a == -1) 1 else 0
    // `a` is `0`,
    if (a == 0) return if (b == 1 || b == -1) 1 else 0
    // `b` is `1`,
    if (b == 1) return 1
    // or `b` is `-1`.
    if (b == -1) return if (a >= 0) 1 else -1


    var a = a
    var b = b
    var result = 1

    // When 'b' is even
    if (b % 2 == 0) {
        // case of even 'a' is obvious,
        if (a % 2 == 0) return 0

        // otherwise, eliminate all possible 4s (pairs of 2s in 'b')
        while (b % 4 == 0) b /= 4

        // and if 'b' is still even
        if (b % 2 == 0) {
            // correct the result
            if ((abs(a) % 8).let { it == 3 || it == 5 }) result = -result
            // and divide 'b' by the only rest 2.
            b /= 2
        }
    }

    // Now `a` is not zero, and `b` is not `0`, `1`, or `-1` and is odd.
    // We'll be updating the `result` preserving the conditions.

    // Each step is
    while (true) {
        // 1. Substitution of 'a' by its reminder of division of 'a' by 'b' preserving sign of 'a'. (Be aware that
        // trivial 'a % b' works only because of definition in kotlin standard library. See second paragraph in
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/rem.html .) We can do it because 'b' is odd.
        a %= b

        // 2. If 'a' is '0', then the result is obvious.
        if (a == 0) return 0
        // So now 'a' is still not '0', and 'b' is the same. But 'a' can be even.

        // 2. Removing 2s from 'a' in the same way as from 'b' before the cycle.
        if (a % 2 == 0) {
            while (a % 4 == 0) a /= 4

            if (a % 2 == 0) {
                if ((abs(b) % 8).let { it == 3 || it == 5 }) result = -result
                a /= 2
            }
        }
        // So now 'a' is not '0' and is odd, and 'b' is too.

        // 3. Now 'a' and 'b' are odd, so we can apply quadratic reciprocity law in a very simple way.
        a = b.also { b = a }
        if (a % 4 == 3 && b % 4 == 3) result = -result
        if (a < 0 && b < 0) result = -result
        // Now 'a' and 'b' are not '0' and are odd.

        // 4. If 'b' is '1' or '-1', the result is obvious and should be returned immediately.
        if (b == 1) return result
        if (b == -1) return result.let { if (a >= 0) it else -it }
        // Otherwise, we have that 'a' is not '0' and is odd, and 'b' is not '0', '1', or '-1' and is odd.
    }

    // With each step absolute value of 'a' drastically decreases, and it's swapped with 'b'. Thus, the process is
    // finite and fast. At least it's not slower than Euclidean algorithm, which time complexity is
    // \(O(\log(\max(a, b)))\).
}

// endregion

// region Long

/**
 * Computes [Kronecker symbol](https://en.wikipedia.org/wiki/Kronecker_symbol) of entries [a] and [b].
 */
@Suppress("NAME_SHADOWING")
public fun kroneckerSymbol(a: Long, b: Long): Int {
    // Simple cases: when
    // `b` is `0`,
    if (b == 0L) return if (a == 1L || a == -1L) 1 else 0
    // `a` is `0`,
    if (a == 0L) return if (b == 1L || b == -1L) 1 else 0
    // `b` is `1`,
    if (b == 1L) return 1
    // or `b` is `-1`.
    if (b == -1L) return if (a >= 0L) 1 else -1


    var a = a
    var b = b
    var result = 1

    // When 'b' is even
    if (b % 2L == 0L) {
        // case of even 'a' is obvious,
        if (a % 2L == 0L) return 0

        // otherwise, eliminate all possible 4s (pairs of 2s in 'b')
        while (b % 4L == 0L) b /= 4L

        // and if 'b' is still even
        if (b % 2L == 0L) {
            // correct the result
            if ((abs(a) % 8L).let { it == 3L || it == 5L }) result = -result
            // and divide 'b' by the only rest 2.
            b /= 2L
        }
    }

    // Now `a` is not zero, and `b` is not `0`, `1`, or `-1` and is odd.
    // We'll be updating the `result` preserving the conditions.

    // Each step is
    while (true) {
        // 1. Substitution of 'a' by its reminder of division of 'a' by 'b' preserving sign of 'a'. (Be aware that
        // trivial 'a % b' works only because of definition in kotlin standard library. See second paragraph in
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/rem.html .) We can do it because 'b' is odd.
        a %= b

        // 2. If 'a' is '0', then the result is obvious.
        if (a == 0L) return 0
        // So now 'a' is still not '0', and 'b' is the same. But 'a' can be even.

        // 2. Removing 2s from 'a' in the same way as from 'b' before the cycle.
        if (a % 2L == 0L) {
            while (a % 4L == 0L) a /= 4L

            if (a % 2L == 0L) {
                if ((abs(b) % 8L).let { it == 3L || it == 5L }) result = -result
                a /= 2L
            }
        }
        // So now 'a' is not '0' and is odd, and 'b' is too.

        // 3. Now 'a' and 'b' are odd, so we can apply quadratic reciprocity law in a very simple way.
        a = b.also { b = a }
        if (a % 4L == 3L && b % 4L == 3L) result = -result
        if (a < 0 && b < 0) result = -result
        // Now 'a' and 'b' are not '0' and are odd.

        // 4. If 'b' is '1' or '-1', the result is obvious and should be returned immediately.
        if (b == 1L) return result
        if (b == -1L) return result.let { if (a >= 0L) it else -it }
        // Otherwise, we have that 'a' is not '0' and is odd, and 'b' is not '0', '1', or '-1' and is odd.
    }

    // With each step absolute value of 'a' drastically decreases, and it's swapped with 'b'. Thus, the process is
    // finite and fast. At least it's not slower than Euclidean algorithm, which time complexity is
    // \(O(\log(\max(a, b)))\).
}

// endregion