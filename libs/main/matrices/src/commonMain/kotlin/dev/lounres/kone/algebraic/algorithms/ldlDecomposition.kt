/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


// X = L D L^*, L - lower unitriangular, D - diagonal
public data class LDLDecomposition<out Number, out Matrix : MDList2<Number>>(
    val leftLowerTriangular: Matrix,
    val middleDiagonal: Matrix,
    val rightUpperTriangular: Matrix,
) {
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<LDLDecomposition<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LDLDecomposition.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

@GenerateKoneContextKey
public fun interface LDLDecompositionComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.ldlDecomposition(): LDLDecomposition<Number, Matrix>
    
    public companion object;
}

context(ldlDecompositionComputer: LDLDecompositionComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.ldlDecomposition(): LDLDecomposition<Number, Matrix> =
    with(ldlDecompositionComputer) { this@ldlDecomposition.ldlDecomposition() }