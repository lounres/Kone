/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import java.math.BigInteger


public data object BigIntegerRing: Ring<BigInteger>, Order<BigInteger>, Hashing<BigInteger> {
    override fun BigInteger.compareTo(other: BigInteger): Int = this.compareTo(other)

    override val zero: BigInteger = BigInteger.ZERO
    override val one: BigInteger = BigInteger.ONE

    override fun valueOf(arg: Int): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: UInt): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: Long): BigInteger = arg.toBigInteger()
    override fun valueOf(arg: ULong): BigInteger = arg.toBigInteger()

    public override operator fun BigInteger.plus(other: Int): BigInteger = this.add(other.toBigInteger())
    public override operator fun BigInteger.minus(other: Int): BigInteger = this.subtract(other.toBigInteger())
    public override operator fun BigInteger.times(other: Int): BigInteger = this.multiply(other.toBigInteger())
    
    public override operator fun BigInteger.plus(other: UInt): BigInteger = this.add(other.toBigInteger())
    public override operator fun BigInteger.minus(other: UInt): BigInteger = this.subtract(other.toBigInteger())
    public override operator fun BigInteger.times(other: UInt): BigInteger = this.multiply(other.toBigInteger())

    public override operator fun BigInteger.plus(other: Long): BigInteger = this.add(other.toBigInteger())
    public override operator fun BigInteger.minus(other: Long): BigInteger = this.subtract(other.toBigInteger())
    public override operator fun BigInteger.times(other: Long): BigInteger = this.multiply(other.toBigInteger())
    
    public override operator fun BigInteger.plus(other: ULong): BigInteger = this.add(other.toBigInteger())
    public override operator fun BigInteger.minus(other: ULong): BigInteger = this.subtract(other.toBigInteger())
    public override operator fun BigInteger.times(other: ULong): BigInteger = this.multiply(other.toBigInteger())

    public override operator fun Int.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    public override operator fun Int.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    public override operator fun Int.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)
    
    public override operator fun UInt.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    public override operator fun UInt.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    public override operator fun UInt.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)

    public override operator fun Long.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    public override operator fun Long.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    public override operator fun Long.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)
    
    public override operator fun ULong.plus(other: BigInteger): BigInteger = this.toBigInteger().add(other)
    public override operator fun ULong.minus(other: BigInteger): BigInteger = this.toBigInteger().subtract(other)
    public override operator fun ULong.times(other: BigInteger): BigInteger = this.toBigInteger().multiply(other)

    override fun BigInteger.unaryMinus(): BigInteger = this.negate()
    override fun BigInteger.plus(other: BigInteger): BigInteger = this.add(other)
    override fun BigInteger.minus(other: BigInteger): BigInteger = this.subtract(other)
    override fun BigInteger.times(other: BigInteger): BigInteger = this.multiply(other)
}