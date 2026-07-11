/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextHolderContext
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


// unital, associative
public interface Algebra<Number, Vector> : Module<Number, Vector>, Ring<Vector> {
    // region Number conversion
    public fun valueOf(arg: Number): Vector
    // endregion
    
    // region Vector-Number operations
    @KoneContextHolderContext
    public val vectorPlusNumber: Plus<Vector, Number, Vector> get() = Plus { other -> numberPlusNumber { this + valueOf(other) } }
    @KoneContextHolderContext
    public val vectorMinusNumber: Minus<Vector, Number, Vector> get() = Minus { other -> numberMinusNumber { this - valueOf(other) } }
    @KoneContextHolderContext
    public override val vectorTimesNumber: Times<Vector, Number, Vector> get() = Times { other -> numberTimesNumber { this * valueOf(other) } }
    // endregion
    
    // region Number-Vector operations
    @KoneContextHolderContext
    public val numberPlusVector: Plus<Number, Vector, Vector> get() = Plus { other -> numberPlusNumber { valueOf(this) + other } }
    @KoneContextHolderContext
    public val numberMinusVector: Minus<Number, Vector, Vector> get() = Minus { other -> numberMinusNumber { valueOf(this) - other } }
    @KoneContextHolderContext
    public override val numberTimesVector: Times<Number, Vector, Vector> get() = Times { other -> numberTimesNumber { valueOf(this) * other } }
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