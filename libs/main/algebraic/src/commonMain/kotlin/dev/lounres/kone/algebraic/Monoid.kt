/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


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
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<Monoid<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Monoid",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    )
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<Monoid<Number>> = ImpliedKeysRegistry {
            Semigroup.Key<Number>(numberType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.Monoid.Key<$numberType>"
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
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<CommutativeMonoid<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.CommutativeMonoid",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    )
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<CommutativeMonoid<Number>> = ImpliedKeysRegistry {
            Monoid.Key<Number>(numberType) implies { it }
            CommutativeSemigroup.Key<Number>(numberType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeMonoid.Key<$numberType>"
    }
}