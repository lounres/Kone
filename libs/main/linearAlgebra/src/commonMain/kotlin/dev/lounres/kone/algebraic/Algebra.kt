/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextInclude
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
    @KoneContextInclude
    public val vectorPlusNumber: Plus<Vector, Number, Vector> get() = Plus { left, right -> numberPlusNumber { left + valueOf(right) } }
    @KoneContextInclude
    public val vectorMinusNumber: Minus<Vector, Number, Vector> get() = Minus { left, right -> numberMinusNumber { left - valueOf(right) } }
    @KoneContextInclude
    public override val vectorTimesNumber: Times<Vector, Number, Vector> get() = Times { left, right -> numberTimesNumber { left * valueOf(right) } }
    // endregion
    
    // region Number-Vector operations
    @KoneContextInclude
    public val numberPlusVector: Plus<Number, Vector, Vector> get() = Plus { left, right -> numberPlusNumber { valueOf(left) + right } }
    @KoneContextInclude
    public val numberMinusVector: Minus<Number, Vector, Vector> get() = Minus { left, right -> numberMinusNumber { valueOf(left) - right } }
    @KoneContextInclude
    public override val numberTimesVector: Times<Number, Vector, Vector> get() = Times { left, right -> numberTimesNumber { valueOf(left) * right } }
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