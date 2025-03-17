/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public interface Semiring<Number> : Equality<Number> {
    // region Constants
    public val zero: Number
    public val one: Number
    // endregion
    
    // region Equality
    public fun Number.isZero(): Boolean = this equalsTo zero
    public fun Number.isOne(): Boolean = this equalsTo one
    // FIXME: KT-5351
    public fun Number.isNotZero(): Boolean = !isZero()
    // FIXME: KT-5351
    public fun Number.isNotOne(): Boolean = !isOne()
    // endregion
    
    // region Integers conversion
    public fun valueOf(arg: UInt): Number = one doublingTimes arg
    public fun valueOf(arg: ULong): Number = one doublingTimes arg
    public val UInt.value: Number get() = valueOf(this)
    public val ULong.value: Number get() = valueOf(this)
    // endregion
    
    // region Number-UInt operations
    public operator fun Number.plus(other: UInt): Number = this + other.value
    public operator fun Number.times(other: UInt): Number = this * other.value
    // endregion
    
    // region Number-ULong operations
    public operator fun Number.plus(other: ULong): Number = this + other.value
    public operator fun Number.times(other: ULong): Number = this * other.value
    // endregion
    
    // region UInt-Number operations
    public operator fun UInt.plus(other: Number): Number = this.value + other
    public operator fun UInt.times(other: Number): Number = this.value * other
    // endregion
    
    // region ULong-Number operations
    public operator fun ULong.plus(other: Number): Number = this.value + other
    public operator fun ULong.times(other: Number): Number = this.value * other
    // endregion
    
    // region Number-Number operations
    public operator fun Number.plus(other: Number): Number
    public operator fun Number.times(other: Number): Number
    public fun power(base: Number, exponent: UInt): Number = base squaringPower exponent
    public fun power(base: Number, exponent: ULong): Number = base squaringPower exponent
    public infix fun Number.pow(exponent: UInt): Number = power(this, exponent)
    public infix fun Number.pow(exponent: ULong): Number = power(this, exponent)
    // endregion
    
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<Semiring<Number>> {
        override val typeKey: SuppliedType.Regular<Semiring<Number>> =
            SuppliedType.Regular(
                kClass = Semiring::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}


// region Constants
context(ring: Semiring<Number>)
public val <Number> zero: Number get() = ring.zero
context(ring: Semiring<Number>)
public val <Number> one: Number get() = ring.one
// endregion

// region Equality
context(ring: Semiring<Number>)
public fun <Number> Number.isZero(): Boolean = with(ring) { this@isZero.isZero() }
context(ring: Semiring<Number>)
public fun <Number> Number.isOne(): Boolean = with(ring) { this@isOne.isOne() }
// FIXME: KT-5351
context(ring: Semiring<Number>)
public fun <Number> Number.isNotZero(): Boolean = with(ring) { this@isNotZero.isNotZero() }
// FIXME: KT-5351
context(ring: Semiring<Number>)
public fun <Number> Number.isNotOne(): Boolean = with(ring) { this@isNotOne.isNotOne() }
// endregion

// region Integers conversion
context(ring: Semiring<Number>)
public fun <Number> valueOf(arg: UInt): Number = ring.valueOf(arg)
context(ring: Semiring<Number>)
public fun <Number> valueOf(arg: ULong): Number = ring.valueOf(arg)
context(ring: Semiring<Number>)
public val <Number> UInt.value: Number get() = with(ring) { this@value.value }
context(ring: Semiring<Number>)
public val <Number> ULong.value: Number get() = with(ring) { this@value.value }
// endregion

// region Number-UInt operations
context(ring: Semiring<Number>)
public operator fun <Number> Number.plus(other: UInt): Number = with(ring) { this@plus + other }
context(ring: Semiring<Number>)
public operator fun <Number> Number.times(other: UInt): Number = with(ring) { this@times * other }
// endregion

// region Number-ULong operations
context(ring: Semiring<Number>)
public operator fun <Number> Number.plus(other: ULong): Number = with(ring) { this@plus + other }
context(ring: Semiring<Number>)
public operator fun <Number> Number.times(other: ULong): Number = with(ring) { this@times * other }
// endregion

// region UInt-Number operations
context(ring: Semiring<Number>)
public operator fun <Number> UInt.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Semiring<Number>)
public operator fun <Number> UInt.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region ULong-Number operations
context(ring: Semiring<Number>)
public operator fun <Number> ULong.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Semiring<Number>)
public operator fun <Number> ULong.times(other: Number): Number = with(ring) { this@times * other }
// endregion

// region Number-Number operations
context(ring: Semiring<Number>)
public operator fun <Number> Number.plus(other: Number): Number = with(ring) { this@plus + other }
context(ring: Semiring<Number>)
public operator fun <Number> Number.times(other: Number): Number = with(ring) { this@times * other }
context(ring: Semiring<Number>)
public fun <Number> power(base: Number, exponent: UInt): Number = ring.power(base, exponent)
context(ring: Semiring<Number>)
public fun <Number> power(base: Number, exponent: ULong): Number = ring.power(base, exponent)
context(ring: Semiring<Number>)
public infix fun <Number> Number.pow(exponent: UInt): Number = with(ring) { this@pow pow exponent }
context(ring: Semiring<Number>)
public infix fun <Number> Number.pow(exponent: ULong): Number = with(ring) { this@pow pow exponent }
// endregion

public interface ExtendedSemiring<Number> : Semiring<Number> {
    // region Number-UInt operations
    public operator fun Number.minus(other: UInt): Number = this - other.value
    // endregion
    
    // region Number-ULong operations
    public operator fun Number.minus(other: ULong): Number = this - other.value
    // endregion
    
    // region UInt-Number operations
    public operator fun UInt.minus(other: Number): Number = this.value - other
    // endregion
    
    // region ULong-Number operations
    public operator fun ULong.minus(other: Number): Number = this.value - other
    // endregion
    
    // region Number-Number operations
    public operator fun Number.minus(other: Number): Number
    // endregion
    
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<Ring<Number>> {
        override val typeKey: SuppliedType.Regular<Ring<Number>> =
            SuppliedType.Regular(
                kClass = Ring::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}

// region Number-UInt operations
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> Number.minus(other: UInt): Number = with(ring) { this@minus - other }
// endregion

// region Number-ULong operations
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> Number.minus(other: ULong): Number = with(ring) { this@minus - other }
// endregion

// region UInt-Number operations
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> UInt.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region ULong-Number operations
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> ULong.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion

// region Number-Number operations
context(ring: ExtendedSemiring<Number>)
public operator fun <Number> Number.minus(other: Number): Number = with(ring) { this@minus - other }
// endregion