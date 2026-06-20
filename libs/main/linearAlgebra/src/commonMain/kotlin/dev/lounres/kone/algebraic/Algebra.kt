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


// unital, associative
@Suppress("INAPPLICABLE_JVM_NAME")
public interface Algebra<Number, Vector> : Module<Number, Vector>, Ring<Vector> {
    // region Number conversion
    public fun valueOf(arg: Number): Vector
    // endregion
    
    // region Vector-Number operations
    @JvmName("plusVectorNumber")
    public operator fun Vector.plus(other: Number): Vector = this + valueOf(other)
    @JvmName("minusVectorNumber")
    public operator fun Vector.minus(other: Number): Vector = this - valueOf(other)
    @JvmName("timesVectorNumber")
    public override fun Vector.times(other: Number): Vector = this * valueOf(other)
    // endregion
    
    // region Number-Vector operations
    @JvmName("plusNumberVector")
    public operator fun Number.plus(other: Vector): Vector = valueOf(this) + other
    @JvmName("minusNumberVector")
    public operator fun Number.minus(other: Vector): Vector = valueOf(this) - other
    @JvmName("timesNumberVector")
    public override fun Number.times(other: Vector): Vector = valueOf(this) * other
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<Algebra<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<Algebra<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                Module.Key<Number, Vector>().impliesSame()
                Ring.Key<Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Algebra.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}

context(algebra: Algebra<Number, Vector>)
public operator fun <Number, Vector> Vector.plus(other: Number): Vector = with(algebra) { this@plus + other }

context(algebra: Algebra<Number, Vector>)
public operator fun <Number, Vector> Vector.minus(other: Number): Vector = with(algebra) { this@minus - other }

//context(algebra: Algebra<Number, Vector>)
//public operator fun <Number, Vector> Number.plus(other: Vector): Vector = with(algebra) { this@plus + other }
//
//context(algebra: Algebra<Number, Vector>)
//public operator fun <Number, Vector> Number.minus(other: Vector): Vector = with(algebra) { this@minus - other }

public interface CommutativeAlgebra<Number, Vector> : Algebra<Number, Vector>, CommutativeRing<Vector> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector> : SuppliedTypeRegistryKey<CommutativeAlgebra<Number, Vector>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeAlgebra<Number, Vector>> by lazy {
            ImpliedKeysRegistry {
                Algebra.Key<Number, Vector>().impliesSame()
                CommutativeRing.Key<Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeAlgebra.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}>"
    }
}