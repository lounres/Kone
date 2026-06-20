/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface EuclideanVectorSpaceOverRing<Number, Vector> : Module<Number, Vector> {
    public infix fun Vector.dot(other: Vector): Number
    
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

context(euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
public infix fun <Number, Vector> Vector.dot(other: Vector): Number = with(euclideanSpace) { this@dot dot other }

context(euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
public fun <Number, Vector> Vector.lengthSquared(): Number = with(euclideanSpace) { this@lengthSquared dot this@lengthSquared }

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