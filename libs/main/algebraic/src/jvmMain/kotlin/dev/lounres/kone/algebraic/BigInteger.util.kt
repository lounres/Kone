/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import java.math.BigInteger


private val bigTwoIn31th: BigInteger = BigInteger.ONE shl 31
private val bigTwoIn63th: BigInteger = BigInteger.ONE shl 63

// TODO: Check correctness. The solution was made in a spur of time.
/**
 * Converts [this] value to [BigInteger].
 */
public fun UInt.toBigInteger(): BigInteger =
    if (this xor 0b10000000000000000000000000000000u == 0u) BigInteger.valueOf(this.toInt().toLong())
    else BigInteger.valueOf(this.toInt().toLong()) + bigTwoIn31th
/**
 * Converts [this] value to [BigInteger].
 */
public fun ULong.toBigInteger(): BigInteger =
    if (this xor 0b1000000000000000000000000000000000000000000000000000000000000000u == 0uL) BigInteger.valueOf(this.toLong())
    else BigInteger.valueOf(this.toLong()) + bigTwoIn63th