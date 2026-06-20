/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface Monoid<Number> : Semigroup<Number> {
    // region Constants
    public val zero: Number
    // endregion
    
    // region Equality
    public fun Number.isZero(): Boolean = this equalsTo zero
    // FIXME: KT-5351
    public fun Number.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Number-UInt operations
    public operator fun Number.times(other: UInt): Number = this doublingTimes other
    // endregion
    
    // region Number-ULong operations
    public operator fun Number.times(other: ULong): Number = this doublingTimes other
    // endregion
    
    // region UInt-Number operations
    public operator fun UInt.times(other: Number): Number = this doublingTimes other
    // endregion
    
    // region ULong-Number operations
    public operator fun ULong.times(other: Number): Number = this doublingTimes other
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Monoid<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Monoid<Number>> = ImpliedKeysRegistry {
            Semigroup.Key<Number>().impliesSame()
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Monoid.Key<${suppliedTypeOf<Number>()}>"
    }
}


// region Equality
/**
 * Checks that [this] number is a zero in the context of the [Monoid].
 *
 * A bridge contextual function for [Monoid.isZero].
 */
context(monoid: Monoid<Number>)
public fun <Number> Number.isZero(): Boolean = with(monoid) { this@isZero.isZero() }
/**
 * Checks that [this] number is not a zero in the context of the [Monoid].
 *
 * A bridge contextual function for [Monoid.isNotZero].
 */
// FIXME: KT-5351
context(monoid: Monoid<Number>)
public fun <Number> Number.isNotZero(): Boolean = with(monoid) { this@isNotZero.isNotZero() }
// endregion

// region Number-UInt operations
/**
 * Multiplies [this] number and the [other] integer as elements of the [Monoid].
 *
 * A bridge contextual function for [Monoid.times].
 */
context(monoid: Monoid<Number>)
public operator fun <Number> Number.times(other: UInt): Number = with(monoid) { this@times * other }
// endregion

// region Number-ULong operations
/**
 * Multiplies [this] number and the [other] integer as elements of the [Monoid].
 *
 * A bridge contextual function for [Monoid.times].
 */
context(monoid: Monoid<Number>)
public operator fun <Number> Number.times(other: ULong): Number = with(monoid) { this@times * other }
// endregion

// region UInt-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Monoid].
 *
 * A bridge contextual function for [Monoid.times].
 */
context(monoid: Monoid<Number>)
public operator fun <Number> UInt.times(other: Number): Number = with(monoid) { this@times * other }
// endregion

// region ULong-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Monoid].
 *
 * A bridge contextual function for [Monoid.times].
 */
context(monoid: Monoid<Number>)
public operator fun <Number> ULong.times(other: Number): Number = with(monoid) { this@times * other }
// endregion


public interface CommutativeMonoid<Number> : Monoid<Number>, CommutativeSemigroup<Number> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<CommutativeMonoid<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeMonoid<Number>> = ImpliedKeysRegistry {
            Monoid.Key<Number>().impliesSame()
            CommutativeSemigroup.Key<Number>().impliesSame()
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeMonoid.Key<${suppliedTypeOf<Number>()}>"
    }
}