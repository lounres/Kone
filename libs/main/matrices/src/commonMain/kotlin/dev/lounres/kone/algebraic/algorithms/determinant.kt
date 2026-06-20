/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public class DeterminantKey<@Supply Number> : SuppliedTypeRegistryKey<Number>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.DeterminantKey<${suppliedTypeOf<Number>()}>"
}

public fun interface DeterminantComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.determinant(): Number
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<DeterminantComputer<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.DeterminantComputer.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

context(determinantComputer: DeterminantComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.determinant(): Number =
    with(determinantComputer) { this@determinant.determinant() }