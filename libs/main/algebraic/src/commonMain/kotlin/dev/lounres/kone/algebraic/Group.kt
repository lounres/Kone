/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


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
    
    public class Key<Number>(
        elementType: SuppliedType,
    ) : RegistryKey<Group<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Group",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    )
                ),
                isNullable = false
            )
        override val superkeys: List<RegistryKey<in Group<Number>>> =
            listOf(
                Monoid.Key(elementType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
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
    
    public class Key<Number>(
        elementType: SuppliedType,
    ) : RegistryKey<CommutativeGroup<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.CommutativeGroup",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    )
                ),
                isNullable = false
            )
        override val superkeys: List<RegistryKey<in CommutativeGroup<Number>>> =
            listOf(
                Group.Key(elementType),
                CommutativeMonoid.Key(elementType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

public typealias AbelianGroup<Number> = CommutativeGroup<Number>