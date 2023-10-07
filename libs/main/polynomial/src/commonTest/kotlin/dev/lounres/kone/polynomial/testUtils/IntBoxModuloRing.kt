/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial.testUtils

import dev.lounres.kone.algebraic.Ring
import kotlin.jvm.JvmInline


@JvmInline
value class IntBox(
    val value: Int
)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
class IntBoxModuloRing(modulus: Int) : Ring<IntBox> {

    val modulus: Int

    init {
        require(modulus != 0) { "modulus can not be zero" }
        this.modulus = if (modulus < 0) -modulus else modulus
    }
    override val zero: IntBox = IntBox(0)
    override val one: IntBox = IntBox(1)

    override fun valueOf(arg: Int): IntBox = IntBox(arg % modulus)
    override fun valueOf(arg: Long): IntBox = IntBox((arg % modulus).toInt())

    // TODO: Upgrade operations. Fix cases of big numbers.

    override inline operator fun IntBox.unaryMinus(): IntBox = IntBox(if (value == 0) 0 else modulus - value)
    override inline operator fun IntBox.plus(other: IntBox): IntBox = IntBox((value + other.value) % modulus)
    override inline operator fun IntBox.minus(other: IntBox): IntBox = IntBox((value - other.value) % modulus)
    override inline operator fun IntBox.times(other: IntBox): IntBox = IntBox((value * other.value) % modulus)

    override operator fun IntBox.plus(other: Int): IntBox = IntBox(((value + other) % modulus))
    override operator fun IntBox.minus(other: Int): IntBox = IntBox(((value - other) % modulus))
    override operator fun IntBox.times(other: Int): IntBox = IntBox(((value * other) % modulus))

    override operator fun IntBox.plus(other: Long): IntBox = IntBox(((value + other) % modulus).toInt())
    override operator fun IntBox.minus(other: Long): IntBox = IntBox(((value - other) % modulus).toInt())
    override operator fun IntBox.times(other: Long): IntBox = IntBox(((value * other) % modulus).toInt())

    override operator fun Int.plus(other: IntBox): IntBox = IntBox(((this + other.value) % modulus))
    override operator fun Int.minus(other: IntBox): IntBox = IntBox(((this - other.value) % modulus))
    override operator fun Int.times(other: IntBox): IntBox = IntBox(((this * other.value) % modulus))

    override operator fun Long.plus(other: IntBox): IntBox = IntBox(((this + other.value) % modulus).toInt())
    override operator fun Long.minus(other: IntBox): IntBox = IntBox(((this - other.value) % modulus).toInt())
    override operator fun Long.times(other: IntBox): IntBox = IntBox(((this * other.value) % modulus).toInt())
}