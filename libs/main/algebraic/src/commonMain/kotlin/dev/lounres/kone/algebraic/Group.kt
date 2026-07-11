/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.contexts.KoneContextHolderContext
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface Group<Number> : Monoid<Number> {
    // region Number-Int operations
    @KoneContextHolderContext
    public val numberTimesInt: Times<Number, Int, Number> get() = Times { other -> this doublingTimes other }
    // endregion
    
    // region Number-Long operations
    @KoneContextHolderContext
    public val numberTimesLong: Times<Number, Long, Number> get() = Times { other -> this doublingTimes other }
    // endregion
    
    // region Int-Number operations
    @KoneContextHolderContext
    public val intTimesNumber: Times<Int, Number, Number> get() = Times { other -> this doublingTimes other }
    // endregion
    
    // region Long-Number operations
    @KoneContextHolderContext
    public val longTimesNumber: Times<Long, Number, Number> get() = Times { other -> this doublingTimes other }
    // endregion
    
    // region Number-Number operations
    @KoneContextHolderContext
    public val numberUnaryMinus: UnaryMinus<Number, Number>
    @KoneContextHolderContext
    public val numberMinusNumber: Minus<Number, Number, Number>
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<Group<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<Group<Number>> by lazy {
            ImpliedKeysRegistry {
                UnaryMinus.Key<Number, Number>() implies { it.numberUnaryMinus }
                Minus.Key<Number, Number, Number>() implies { it.numberMinusNumber }
                Times.Key<Number, Int, Number>() implies { it.numberTimesInt }
                Times.Key<Number, Long, Number>() implies { it.numberTimesLong }
                Times.Key<Int, Number, Number>() implies { it.intTimesNumber }
                Times.Key<Long, Number, Number>() implies { it.longTimesNumber }
                Monoid.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Group.Key<${suppliedTypeOf<Number>()}>"
    }
}

public interface CommutativeGroup<Number> : Group<Number>, CommutativeMonoid<Number> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<CommutativeGroup<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<CommutativeGroup<Number>> by lazy {
            ImpliedKeysRegistry {
                Group.Key<Number>().impliesSame()
                CommutativeMonoid.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.Group.Key<${suppliedTypeOf<Number>()}>"
    }
}

public typealias AbelianGroup<Number> = CommutativeGroup<Number>