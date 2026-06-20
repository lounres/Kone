/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public data object IsUnitMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsUnitMatrixKey"
}

public fun interface IsUnitMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isOne(): Boolean
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<IsUnitMatrixChecker<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsUnitMatrixChecker.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

context(isUnitMatrixChecker: IsUnitMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isOne(): Boolean =
    with(isUnitMatrixChecker) { this@isOne.isOne() }