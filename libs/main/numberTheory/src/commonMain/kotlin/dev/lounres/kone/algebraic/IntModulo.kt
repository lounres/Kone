/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.algebraic

import dev.lounres.kone.comparison.Hashing


@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public class IntModuloRing(modulus: Int) : Ring<Int>, Hashing<Int> {

    public val modulus: Int

    init {
        require(modulus != 0) { "modulus can not be zero" }
        this.modulus = if (modulus < 0) -modulus else modulus
    }

    override fun Int.hash(): Int = this.mod(modulus)

    public override val zero: Int = 0
    public override val one: Int = 1

    // TODO: Replace `%` with `mod`

    public override fun valueOf(arg: Int): Int = arg % modulus
    public override fun valueOf(arg: Long): Int = (arg % modulus).toInt()

    // TODO: Upgrade operations. Fix cases of big numbers.

    override inline operator fun Int.unaryMinus(): Int = if (this == 0) 0 else modulus - this
    override inline operator fun Int.plus(other: Int): Int = (this + other) % modulus
    override inline operator fun Int.minus(other: Int): Int = (this - other) % modulus
    override inline operator fun Int.times(other: Int): Int = (this * other) % modulus

    override operator fun Int.plus(other: Long): Int = ((this + other) % modulus).toInt()
    override operator fun Int.minus(other: Long): Int = ((this - other) % modulus).toInt()
    override operator fun Int.times(other: Long): Int = ((this * other) % modulus).toInt()

    override operator fun Long.plus(other: Int): Int = ((this + other) % modulus).toInt()
    override operator fun Long.minus(other: Int): Int = ((this - other) % modulus).toInt()
    override operator fun Long.times(other: Int): Int = ((this * other) % modulus).toInt()
}