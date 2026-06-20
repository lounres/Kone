/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


//public class PlanarVectorArgumentKey<Number>(
//    public val numberType: SuppliedType,
//) : RegistryKey<Number> {
//    override fun equals(other: Any?): Boolean = other is PlanarVectorArgumentKey<*> && numberType == other.numberType
//    override fun hashCode(): Int = numberType.hashCode()
//    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.PlanarVectorArgumentKey<$numberType>"
//}

public fun interface PlanarVectorArgumentComputer<Number> : KoneContext {
    public fun planarVectorArgument(x: Number, y: Number): Number
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<PlanarVectorArgumentComputer<Number>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.PlanarVectorArgumentComputer.Key<${suppliedTypeOf<Number>()}>"
    }
}

context(planarVectorArgumentComputer: PlanarVectorArgumentComputer<Number>)
public fun <Number> planarVectorArgument(x: Number, y: Number): Number =
    planarVectorArgumentComputer.planarVectorArgument(x, y)