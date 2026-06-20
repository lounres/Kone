/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlin.jvm.JvmName


@Suppress("INAPPLICABLE_JVM_NAME")
public interface LeftModule<Number, Vector> : CommutativeGroup<Vector> {
    @JvmName("timesNumberVector")
    public operator fun Number.times(other: Vector): Vector
    
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

context(module: LeftModule<Number, Vector>)
public operator fun <Number, Vector> Number.times(other: Vector): Vector = with(module) { this@times * other }

@Suppress("INAPPLICABLE_JVM_NAME")
public interface RightModule<Number, Vector> : CommutativeGroup<Vector> {
    @JvmName("timesVectorNumber")
    public operator fun Vector.times(other: Number): Vector
    
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

context(module: RightModule<Number, Vector>)
public operator fun <Number, Vector> Vector.times(other: Number): Vector = with(module) { this@times * other }

// The underlying ring is commutative
public interface Module<Number, Vector> : LeftModule<Number, Vector>, RightModule<Number, Vector> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : RegistryKey<Module<Number, Vector>> {
        override val impliedKeys: ImpliedKeysRegistry<Module<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                LeftModule.Key<Number, Vector>().impliesSame()
                RightModule.Key<Number, Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Module.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}