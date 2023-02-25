/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic


import com.lounres.kone.algebraic.util.rightMultiplyByDoubling
import kotlin.jvm.JvmInline


// TODO: Add other boxed primitives

@JvmInline
public value class IntBox(public val value: Int) {
    override fun toString(): String = "$value (Box)"
}
public typealias BInt = IntBox
public inline val Int.box : IntBox get() = IntBox(this)

public object BIntRing: Ring<IntBox> {

    // region Constants
    override val zero: IntBox = 0.box
    override val one: IntBox = 1.box
    // endregion

    // region Integers conversion
    public override fun valueOf(arg: Int): IntBox = arg.box
    public override fun valueOf(arg: Long): IntBox = arg.toInt().box
    // endregion

    // region IntBox-Int operations
    public override operator fun IntBox.plus(other: Int): IntBox = (this.value + other).box
    public override operator fun IntBox.minus(other: Int): IntBox = (this.value - other).box
    public override operator fun IntBox.times(other: Int): IntBox = (this.value * other).box
    // endregion

    // region IntBox-Long operations
    public override operator fun IntBox.plus(other: Long): IntBox = (this.value + other.toInt()).box
    public override operator fun IntBox.minus(other: Long): IntBox = (this.value - other.toInt()).box
    public override operator fun IntBox.times(other: Long): IntBox = (this.value * other.toInt()).box
    // endregion

    // region Int-IntBox operations
    public override operator fun Int.plus(other: IntBox): IntBox = (this + other.value).box
    public override operator fun Int.minus(other: IntBox): IntBox = (this - other.value).box
    public override operator fun Int.times(other: IntBox): IntBox = (this * other.value).box
    // endregion

    // region Long-IntBox operations
    public override operator fun Long.plus(other: IntBox): IntBox = (this.toInt() + other.value).box
    public override operator fun Long.minus(other: IntBox): IntBox = (this.toInt() - other.value).box
    public override operator fun Long.times(other: IntBox): IntBox = (this.toInt() * other.value).box
    // endregion

    // region IntBox-IntBox operations
    public override operator fun IntBox.unaryPlus(): IntBox = this
    public override operator fun IntBox.unaryMinus(): IntBox = (-value).box
    public override operator fun IntBox.plus(other: IntBox): IntBox = (this.value + other.value).box
    public override operator fun IntBox.minus(other: IntBox): IntBox = (this.value - other.value).box
    public override operator fun IntBox.times(other: IntBox): IntBox = (this.value * other.value).box
    public override fun power(base: IntBox, exponent: UInt): IntBox = rightMultiplyByDoubling(base.value, exponent, { 1 }, { left, right -> left * right }).box
    public override fun power(base: IntBox, exponent: ULong): IntBox = rightMultiplyByDoubling(base.value, exponent, { 1 }, { left, right -> left * right }).box
    // endregion
}

public inline val Int.Companion.boxRing: BIntRing get() = BIntRing

@JvmInline
public value class LongBox(public val value: Long) {
    override fun toString(): String = "$value (Box)"
}
public typealias BLong = LongBox
public inline val Long.box : LongBox get() = LongBox(this)

public object BLongRing: Ring<LongBox> {

    // region Constants
    override val zero: LongBox = 0L.box
    override val one: LongBox = 1L.box
    // endregion

    // region Integers conversion
    public override fun valueOf(arg: Int): LongBox = arg.toLong().box
    public override fun valueOf(arg: Long): LongBox = arg.box
    // endregion

    // region LongBox-Int operations
    public override operator fun LongBox.plus(other: Int): LongBox = (this.value + other).box
    public override operator fun LongBox.minus(other: Int): LongBox = (this.value - other).box
    public override operator fun LongBox.times(other: Int): LongBox = (this.value * other).box
    // endregion

    // region LongBox-Long operations
    public override operator fun LongBox.plus(other: Long): LongBox = (this.value + other).box
    public override operator fun LongBox.minus(other: Long): LongBox = (this.value - other).box
    public override operator fun LongBox.times(other: Long): LongBox = (this.value * other).box
    // endregion

    // region Int-LongBox operations
    public override operator fun Int.plus(other: LongBox): LongBox = (this + other.value).box
    public override operator fun Int.minus(other: LongBox): LongBox = (this - other.value).box
    public override operator fun Int.times(other: LongBox): LongBox = (this * other.value).box
    // endregion

    // region Long-LongBox operations
    public override operator fun Long.plus(other: LongBox): LongBox = (this + other.value).box
    public override operator fun Long.minus(other: LongBox): LongBox = (this - other.value).box
    public override operator fun Long.times(other: LongBox): LongBox = (this * other.value).box
    // endregion

    // region LongBox-LongBox operations
    public override operator fun LongBox.unaryPlus(): LongBox = this
    public override operator fun LongBox.unaryMinus(): LongBox = (-value).box
    public override operator fun LongBox.plus(other: LongBox): LongBox = (this.value + other.value).box
    public override operator fun LongBox.minus(other: LongBox): LongBox = (this.value - other.value).box
    public override operator fun LongBox.times(other: LongBox): LongBox = (this.value * other.value).box
    public override fun power(base: LongBox, exponent: UInt): LongBox = rightMultiplyByDoubling(base.value, exponent, { 1L }, { left, right -> left * right }).box
    public override fun power(base: LongBox, exponent: ULong): LongBox = rightMultiplyByDoubling(base.value, exponent, { 1L }, { left, right -> left * right }).box
    // endregion
}

public inline val Long.Companion.boxRing: BLongRing get() = BLongRing