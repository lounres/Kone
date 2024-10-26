/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import java.math.BigInteger


private val bigTwoIn32th: BigInteger = BigInteger.ONE shl 32
private val bigTwoIn64th: BigInteger = BigInteger.ONE shl 64

// TODO: Check correctness. The solution was made in a spur of time.
public fun UInt.toBigInteger(): BigInteger =
    if (this xor 0b10000000000000000000000000000000u == 0u) BigInteger.valueOf(this.toLong())
    else BigInteger.valueOf(this.toLong()) + bigTwoIn32th
public fun ULong.toBigInteger(): BigInteger =
    if (this xor 0b1000000000000000000000000000000000000000000000000000000000000000u == 0uL) BigInteger.valueOf(this.toLong())
    else BigInteger.valueOf(this.toLong()) + bigTwoIn64th