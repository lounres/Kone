/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextHolder
import dev.lounres.kone.contexts.KoneContextHolderInclude
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface Semigroup<Number> : KoneContextHolder {
    // region Number-Number operations
    @KoneContextHolderInclude
    public val numberPlusNumber: Plus<Number, Number, Number>
    // endregion
    
    public companion object;
    
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