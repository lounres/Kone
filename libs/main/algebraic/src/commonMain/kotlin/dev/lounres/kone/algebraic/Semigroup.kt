/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


/**
 * Describes a context that represents [mathematical semigroup](https://en.wikipedia.org/wiki/Semigroup).
 *
 * @param Number The type of elements of the semigroup.
 */
public interface Semigroup<Number> : KoneContext {
    // region Number-Number operations
    /**
     * The associative binary addition operation on elements of type [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val numberPlusNumber: Plus<Number, Number, Number>
    // endregion
    
    public companion object;
    
    /**
     * Registry key for [Semigroup] interface in [Registry].
     *
     * @param Number The type of elements of the semigroup.
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Semigroup<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Semigroup<Number>> by lazy {
            ImpliedKeysRegistry {
                Plus.Key<Number, Number, Number>() implies { it.numberPlusNumber }
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Semigroup.Key<${suppliedTypeOf<Number>()}>"
    }
}

/**
 * Describes a context that represents [mathematical commutative semigroup](https://en.wikipedia.org/wiki/Semigroup) (a.k.a. abelian semigroup).
 *
 * @param Number The type of elements of the commutative semigroup.
 */
public interface CommutativeSemigroup<Number> : Semigroup<Number> {
    public companion object;
    
    /**
     * Registry key for [CommutativeSemigroup] interface in [Registry].
     *
     * @param Number The type of elements of the commutative semigroup.
     */
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