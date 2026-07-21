/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface Monoid<Number> : Semigroup<Number> {
    // region Constants
    public val zero: Number
    // endregion
    
    // region Equality
    @KoneContextInclude
    public val numberIsZero: IsZero<Number>
    // endregion
    
    // region Number-UInt operations
    @KoneContextInclude
    public val numberTimesUInt: Times<Number, UInt, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region Number-ULong operations
    @KoneContextInclude
    public val numberTimesULong: Times<Number, ULong, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region UInt-Number operations
    @KoneContextInclude
    public val uIntTimesNumber: Times<UInt, Number, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region ULong-Number operations
    @KoneContextInclude
    public val uLongTimesNumber: Times<ULong, Number, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Monoid<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Monoid<Number>> by lazy {
            ImpliedKeysRegistry {
                IsZero.Key<Number>() implies { it.numberIsZero }
                Times.Key<Number, UInt, Number>() implies { it.numberTimesUInt }
                Times.Key<Number, ULong, Number>() implies { it.numberTimesULong }
                Times.Key<UInt, Number, Number>() implies { it.uIntTimesNumber }
                Times.Key<ULong, Number, Number>() implies { it.uLongTimesNumber }
                Semigroup.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Monoid.Key<${suppliedTypeOf<Number>()}>"
    }
}

public interface CommutativeMonoid<Number> : Monoid<Number>, CommutativeSemigroup<Number> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<CommutativeMonoid<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeMonoid<Number>> by lazy {
            ImpliedKeysRegistry {
                Monoid.Key<Number>().impliesSame()
                CommutativeSemigroup.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeMonoid.Key<${suppliedTypeOf<Number>()}>"
    }
}