/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.algebraic

import com.lounres.kone.algebraic.util.rightMultiplyByDoubling
import com.lounres.kone.numberTheory.bezoutIdentityWithGCD
import com.lounres.kone.numberTheory.isPrime


@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public open class IntModuloRing(modulus: Int) : Ring<IntBox> {
    public constructor(modulus: IntBox): this(modulus.value)

    public val modulus: Int

    init {
        require(modulus != 0) { "modulus can not be zero" }
        this.modulus = if (modulus < 0) -modulus else modulus
        require(modulus != 1) { "modulus can not be one" }
    }

    protected val IntBox.rem: Int get() = this.value.mod(modulus)
    protected val Int.rem: Int get() = this.mod(modulus)
    protected val Long.rem: Int get() = this.mod(modulus)

    // region Constants
    override val zero: IntBox = 0.box
    override val one: IntBox = 1.box
    // endregion

    // region Integers conversion
    public override fun valueOf(arg: Int): IntBox = arg.rem.box
    public override fun valueOf(arg: Long): IntBox = arg.rem.box
    // endregion

    // TODO: Upgrade operations. Fix cases of big numbers.
    //   As a matter of fact, the class works correctly for modulus <= 2^15.

    // region IntBox-Int operations
    public override operator fun IntBox.plus(other: Int): IntBox = (this.rem + other.rem).rem.box
    public override operator fun IntBox.minus(other: Int): IntBox = (this.rem - other.rem).rem.box
    public override operator fun IntBox.times(other: Int): IntBox = (this.rem * other.rem).rem.box
    // endregion

    // region IntBox-Long operations
    public override operator fun IntBox.plus(other: Long): IntBox = (this.rem + other.rem).rem.box
    public override operator fun IntBox.minus(other: Long): IntBox = (this.rem - other.rem).rem.box
    public override operator fun IntBox.times(other: Long): IntBox = (this.rem * other.rem).rem.box
    // endregion

    // region Int-IntBox operations
    public override operator fun Int.plus(other: IntBox): IntBox = (this.rem + other.rem).rem.box
    public override operator fun Int.minus(other: IntBox): IntBox = (this.rem - other.rem).rem.box
    public override operator fun Int.times(other: IntBox): IntBox = (this.rem * other.rem).rem.box
    // endregion

    // region Long-IntBox operations
    public override operator fun Long.plus(other: IntBox): IntBox = (this.rem + other.rem).rem.box
    public override operator fun Long.minus(other: IntBox): IntBox = (this.rem - other.rem).rem.box
    public override operator fun Long.times(other: IntBox): IntBox = (this.rem * other.rem).rem.box
    // endregion

    // region IntBox-IntBox operations
    public override operator fun IntBox.unaryPlus(): IntBox = this.rem.box
    public override operator fun IntBox.unaryMinus(): IntBox = (-value).rem.box
    public override operator fun IntBox.plus(other: IntBox): IntBox = (this.rem + other.rem).rem.box
    public override operator fun IntBox.minus(other: IntBox): IntBox = (this.rem - other.rem).rem.box
    public override operator fun IntBox.times(other: IntBox): IntBox = (this.rem * other.rem).rem.box
    public override fun power(base: IntBox, exponent: UInt): IntBox = rightMultiplyByDoubling(base.rem, exponent, { 1 }, { left, right -> (left * right).rem }).box
    public override fun power(base: IntBox, exponent: ULong): IntBox = rightMultiplyByDoubling(base.rem, exponent, { 1 }, { left, right -> (left * right).rem }).box
    // endregion
}

public open class IntModuloField(modulus: Int) : Field<IntBox>, IntModuloRing(modulus) {
    public constructor(modulus: IntBox): this(modulus.value)

    init {
        require(this.modulus.isPrime()) { "integers over non-prime modulo (${this.modulus}) are not a field" }
    }
    
    public override operator fun IntBox.div(other: IntBox): IntBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other.value, modulus)
        require(bezoutIdentity.gcd == 1) { "/ by zero" }
        return (this.value.rem * bezoutIdentity.first).rem.box
    }
    public override val IntBox.reciprocal: IntBox get() {
        val bezoutIdentity = bezoutIdentityWithGCD(this.value, modulus)
        require(bezoutIdentity.gcd == 1) { "/ by zero" }
        return bezoutIdentity.first.rem.box
    }
    public override operator fun IntBox.div(other: Int): IntBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other, modulus)
        require(bezoutIdentity.gcd == 1) { "/ by zero" }
        return (this.value.rem * bezoutIdentity.first).rem.box
    }
    public override operator fun IntBox.div(other: Long): IntBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other, modulus.toLong())
        require(bezoutIdentity.gcd == 1L) { "/ by zero" }
        return (this.value.rem * bezoutIdentity.first.toInt()).rem.box
    }
    public override operator fun Int.div(other: IntBox): IntBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other.value, modulus)
        require(bezoutIdentity.gcd == 1) { "/ by zero" }
        return (this.rem * bezoutIdentity.first).rem.box
    }
    public override operator fun Long.div(other: IntBox): IntBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other.value, modulus)
        require(bezoutIdentity.gcd == 1) { "/ by zero" }
        return (this.rem * bezoutIdentity.first).rem.box
    }
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public open class LongModuloRing(modulus: Long) : Ring<LongBox> {
    public constructor(modulus: LongBox): this(modulus.value)

    public val modulus: Long

    init {
        require(modulus != 0L) { "modulus can not be zero" }
        this.modulus = if (modulus < 0L) -modulus else modulus
        require(modulus != 1L) { "modulus can not be one" }
    }

    protected val LongBox.rem: Long get() = this.value.mod(modulus)
    protected val Int.rem: Long get() = this.mod(modulus)
    protected val Long.rem: Long get() = this.mod(modulus)

    // region Constants
    override val zero: LongBox = 0L.box
    override val one: LongBox = 1L.box
    // endregion

    // region Integers conversion
    public override fun valueOf(arg: Int): LongBox = arg.rem.box
    public override fun valueOf(arg: Long): LongBox = arg.rem.box
    // endregion

    // TODO: Upgrade operations. Fix cases of big numbers.
    //   As a matter of fact, the class works correctly for modulus <= 2^15.

    // region LongBox-Int operations
    public override operator fun LongBox.plus(other: Int): LongBox = (this.rem + other.rem).rem.box
    public override operator fun LongBox.minus(other: Int): LongBox = (this.rem - other.rem).rem.box
    public override operator fun LongBox.times(other: Int): LongBox = (this.rem * other.rem).rem.box
    // endregion

    // region LongBox-Long operations
    public override operator fun LongBox.plus(other: Long): LongBox = (this.rem + other.rem).rem.box
    public override operator fun LongBox.minus(other: Long): LongBox = (this.rem - other.rem).rem.box
    public override operator fun LongBox.times(other: Long): LongBox = (this.rem * other.rem).rem.box
    // endregion

    // region Int-LongBox operations
    public override operator fun Int.plus(other: LongBox): LongBox = (this.rem + other.rem).rem.box
    public override operator fun Int.minus(other: LongBox): LongBox = (this.rem - other.rem).rem.box
    public override operator fun Int.times(other: LongBox): LongBox = (this.rem * other.rem).rem.box
    // endregion

    // region Long-LongBox operations
    public override operator fun Long.plus(other: LongBox): LongBox = (this.rem + other.rem).rem.box
    public override operator fun Long.minus(other: LongBox): LongBox = (this.rem - other.rem).rem.box
    public override operator fun Long.times(other: LongBox): LongBox = (this.rem * other.rem).rem.box
    // endregion

    // region LongBox-LongBox operations
    public override operator fun LongBox.unaryPlus(): LongBox = this.rem.box
    public override operator fun LongBox.unaryMinus(): LongBox = (-value).rem.box
    public override operator fun LongBox.plus(other: LongBox): LongBox = (this.rem + other.rem).rem.box
    public override operator fun LongBox.minus(other: LongBox): LongBox = (this.rem - other.rem).rem.box
    public override operator fun LongBox.times(other: LongBox): LongBox = (this.rem * other.rem).rem.box
    public override fun power(base: LongBox, exponent: UInt): LongBox = rightMultiplyByDoubling(base.rem, exponent, { 1L }, { left, right -> (left * right).rem }).box
    public override fun power(base: LongBox, exponent: ULong): LongBox = rightMultiplyByDoubling(base.rem, exponent, { 1L }, { left, right -> (left * right).rem }).box
    // endregion
}

public open class LongModuloField(modulus: Long) : Field<LongBox>, LongModuloRing(modulus) {
    public constructor(modulus: LongBox): this(modulus.value)

    init {
        require(this.modulus.isPrime()) { "integers over non-prime modulo (${this.modulus}) are not a field" }
    }

    public override operator fun LongBox.div(other: LongBox): LongBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other.value, modulus)
        require(bezoutIdentity.gcd == 1L) { "/ by zero" }
        return (this.value.rem * bezoutIdentity.first).rem.box
    }
    public override val LongBox.reciprocal: LongBox get() {
        val bezoutIdentity = bezoutIdentityWithGCD(this.value, modulus)
        require(bezoutIdentity.gcd == 1L) { "/ by zero" }
        return bezoutIdentity.first.rem.box
    }
    public override operator fun LongBox.div(other: Int): LongBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other.toLong(), modulus)
        require(bezoutIdentity.gcd == 1L) { "/ by zero" }
        return (this.value.rem * bezoutIdentity.first).rem.box
    }
    public override operator fun LongBox.div(other: Long): LongBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other, modulus)
        require(bezoutIdentity.gcd == 1L) { "/ by zero" }
        return (this.value.rem * bezoutIdentity.first.toInt()).rem.box
    }
    public override operator fun Int.div(other: LongBox): LongBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other.value, modulus)
        require(bezoutIdentity.gcd == 1L) { "/ by zero" }
        return (this.rem * bezoutIdentity.first).rem.box
    }
    public override operator fun Long.div(other: LongBox): LongBox {
        val bezoutIdentity = bezoutIdentityWithGCD(other.value, modulus)
        require(bezoutIdentity.gcd == 1L) { "/ by zero" }
        return (this.rem * bezoutIdentity.first).rem.box
    }
}