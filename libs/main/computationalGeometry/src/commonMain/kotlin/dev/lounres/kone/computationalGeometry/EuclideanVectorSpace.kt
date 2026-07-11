/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.contexts.KoneContextHolderContext
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface EuclideanVectorSpaceOverRing<Number, Vector> : Module<Number, Vector> {
    @KoneContextHolderContext
    public val vectorDotVector: Dot<Vector, Vector, Number>
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<EuclideanVectorSpaceOverRing<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanVectorSpaceOverRing<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                Module.Key<Number, Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverRing.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}

public interface EuclideanVectorSpaceOverField<Number, Vector> : VectorSpace<Number, Vector>, EuclideanVectorSpaceOverRing<Number, Vector> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<EuclideanVectorSpaceOverField<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanVectorSpaceOverField<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                VectorSpace.Key<Number, Vector>().impliesSame()
                EuclideanVectorSpaceOverRing.Key<Number, Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverField.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}