/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.numberTheory

import kotlin.math.abs


internal data class BezoutIdentityWithGCD<T>(val first: T, val second: T, val gcd: T)

internal tailrec fun gcd(a: Long, b: Long): Long = if (a == 0L) abs(b) else gcd(b % a, a)

internal fun bezoutIdentityWithGCD(a: Int, b: Int): BezoutIdentityWithGCD<Int> =
    when {
        a < 0 && b < 0 -> with(bezoutIdentityWithGCDInternalLogic(-a, -b, 1, 0, 0, 1)) { BezoutIdentityWithGCD(-first, -second, gcd) }
        a < 0 -> with(bezoutIdentityWithGCDInternalLogic(-a, b, 1, 0, 0, 1)) { BezoutIdentityWithGCD(-first, second, gcd) }
        b < 0 -> with(bezoutIdentityWithGCDInternalLogic(a, -b, 1, 0, 0, 1)) { BezoutIdentityWithGCD(first, -second, gcd) }
        else -> bezoutIdentityWithGCDInternalLogic(a, b, 1, 0, 0, 1)
    }

internal tailrec fun bezoutIdentityWithGCDInternalLogic(a: Int, b: Int, m1: Int, m2: Int, m3: Int, m4: Int): BezoutIdentityWithGCD<Int> =
    if (b == 0) BezoutIdentityWithGCD(m1, m3, a)
    else {
        val quotient = a / b
        val reminder = a % b
        bezoutIdentityWithGCDInternalLogic(b, reminder, m2, m1 - quotient * m2, m4, m3 - quotient * m4)
    }