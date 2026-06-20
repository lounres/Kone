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
public class TransposeMatrixKey<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<Matrix>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.TransposeMatrixKey<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
}

public fun interface TransposeMatrixComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.transpose(): Matrix
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<TransposeMatrixComputer<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

context(transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.transpose(): Matrix =
    with(transposeMatrixComputer) { this@transpose.transpose() }