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


public interface Group<Number> : Monoid<Number> {
    // region Number-Int operations
    public operator fun Number.times(other: Int): Number = this doublingTimes other
    // endregion
    
    // region Number-Long operations
    public operator fun Number.times(other: Long): Number = this doublingTimes other
    // endregion
    
    // region Int-Number operations
    public operator fun Int.times(other: Number): Number = this doublingTimes other
    // endregion
    
    // region Long-Number operations
    public operator fun Long.times(other: Number): Number = this doublingTimes other
    // endregion
    
    // region Number-Number operations
    public operator fun Number.unaryMinus(): Number
    public operator fun Number.minus(other: Number): Number
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Group<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Group<Number>> by lazy {
            ImpliedKeysRegistry {
                Monoid.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Group.Key<${suppliedTypeOf<Number>()}>"
    }
}


// region Number-Int operations
/**
 * Multiplies [this] number and the [other] integer as elements of the [Group].
 *
 * A bridge contextual function for [Group.times].
 */
context(group: Group<Number>)
public operator fun <Number> Number.times(other: Int): Number = with(group) { this@times * other }
// endregion

// region Number-Long operations
/**
 * Multiplies [this] number and the [other] integer as elements of the [Group].
 *
 * A bridge contextual function for [Group.times].
 */
context(group: Group<Number>)
public operator fun <Number> Number.times(other: Long): Number = with(group) { this@times * other }
// endregion

// region Int-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Group].
 *
 * A bridge contextual function for [Group.times].
 */
context(group: Group<Number>)
public operator fun <Number> Int.times(other: Number): Number = with(group) { this@times * other }
// endregion

// region Long-Number operations
/**
 * Sums [this] integer and the [other] number as elements of the [Group].
 *
 * A bridge contextual function for [Group.times].
 */
context(group: Group<Number>)
public operator fun <Number> Long.times(other: Number): Number = with(group) { this@times * other }
// endregion

// region Number-Number operations
/**
 * Inverses [this] value in terms of the [Group].
 *
 * A bridge contextual function for [Group.unaryMinus].
 */
context(group: Group<Number>)
public operator fun <Number> Number.unaryMinus(): Number = with(group) { -this@unaryMinus }
/**
 * Subtracts [this] and the [other] numbers in terms of the [Group].
 *
 * A bridge contextual function for [Group.minus].
 */
context(group: Group<Number>)
public operator fun <Number> Number.minus(other: Number): Number = with(group) { this@minus - other }
// endregion


public interface CommutativeGroup<Number> : Group<Number>, CommutativeMonoid<Number> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<CommutativeGroup<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeGroup<Number>> by lazy {
            ImpliedKeysRegistry {
                Group.Key<Number>().impliesSame()
                CommutativeMonoid.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Group.Key<${suppliedTypeOf<Number>()}>"
    }
}

public typealias AbelianGroup<Number> = CommutativeGroup<Number>