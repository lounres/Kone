/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


public interface Semigroup<Number> : Equality<Number> {
    // region Number-Number operations
    public operator fun Number.plus(other: Number): Number
    // endregion
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<Semigroup<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Semigroup",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    )
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<Semigroup<Number>> = ImpliedKeysRegistry {
            Equality.Key<Number>(numberType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.Semigroup.Key<$numberType>"
    }
}

// region Number-Number operations
/**
 * Sums [this] and the [other] numbers in terms of the [Semigroup].
 *
 * A bridge contextual function for [Semigroup.plus].
 */
context(semigroup: Semigroup<Number>)
public operator fun <Number> Number.plus(other: Number): Number = with(semigroup) { this@plus + other }
// endregion

public interface CommutativeSemigroup<Number> : Semigroup<Number> {
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<CommutativeSemigroup<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.CommutativeSemigroup",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    )
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<CommutativeSemigroup<Number>> = ImpliedKeysRegistry {
            Semigroup.Key<Number>(numberType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeSemigroup.Key<$numberType>"
    }
}