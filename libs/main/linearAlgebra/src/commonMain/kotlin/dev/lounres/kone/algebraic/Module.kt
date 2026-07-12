/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextHolderInclude
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface LeftModule<Number, Vector> : CommutativeGroup<Vector> {
    @KoneContextHolderInclude
    public val numberTimesVector: Times<Number, Vector, Vector>
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<LeftModule<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<LeftModule<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                CommutativeGroup.Key<Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.LeftModule.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}

public interface RightModule<Number, Vector> : CommutativeGroup<Vector> {
    @KoneContextHolderInclude
    public val vectorTimesNumber: Times<Vector, Number, Vector>
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<RightModule<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<RightModule<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                CommutativeGroup.Key<Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.RightModule.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}

// The underlying ring is commutative
public interface Module<Number, Vector> : LeftModule<Number, Vector>, RightModule<Number, Vector> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<Module<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<Module<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                LeftModule.Key<Number, Vector>().impliesSame()
                RightModule.Key<Number, Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Module.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}