/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface Semigroup<Number> : Equality<Number> {
    // region Number-Number operations
    public operator fun Number.plus(other: Number): Number
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Semigroup<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Semigroup<Number>> by lazy {
            ImpliedKeysRegistry {
                Equality.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Semigroup.Key<${suppliedTypeOf<Number>()}>"
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
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<CommutativeSemigroup<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeSemigroup<Number>> by lazy {
            ImpliedKeysRegistry {
                Semigroup.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeSemigroup.Key<${suppliedTypeOf<Number>()}>"
    }
}