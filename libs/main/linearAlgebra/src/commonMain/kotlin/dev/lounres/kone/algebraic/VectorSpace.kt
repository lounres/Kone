/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlin.jvm.JvmName


@Suppress("INAPPLICABLE_JVM_NAME")
public interface VectorSpace<Number, Vector> : Module<Number, Vector> {
    @JvmName("divVectorNumber")
    public operator fun Vector.div(other: Number): Vector
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<VectorSpace<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<VectorSpace<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                Module.Key<Number, Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.VectorSpace.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
    
    public interface FiniteDimensional<Number, Vector> : VectorSpace<Number, Vector> {
        public val dimension: UInt
        
        public companion object;
        
        @Suppliable
        public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<FiniteDimensional<Number, Vector>>() {
            override val impliedKeys: ImpliedKeysRegistry<FiniteDimensional<Number, Vector>> by lazy {
                ImpliedKeysRegistry {
                    VectorSpace.Key<Number, Vector>().impliesSame()
                }
            }
            override fun toString(): String = "dev.lounres.kone.algebraic.VectorSpace.FiniteDimensional.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
        }
    }
}

context(vectorSpace: VectorSpace<Number, Vector>)
public operator fun <Number, Vector> Vector.div(other: Number): Vector = with(vectorSpace) { this@div / other }