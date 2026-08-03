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


// X = Q T Q^*, Q - unitary, T - (quasi-)upper-triangular
public data class SchurDecomposition<out Number, out Matrix : MDList2<Number>>(
    val leftUnitary: Matrix,
    val middleUpperTriangular: Matrix,
    val rightUnitary: Matrix,
) {
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<SchurDecomposition<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SchurDecomposition.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

@GenerateKoneContextKey
public fun interface SchurDecompositionComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.schurDecomposition(): SchurDecomposition<Number, Matrix>
    
    public companion object;
}

context(schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.schurDecomposition(): SchurDecomposition<Number, Matrix> =
    with(schurDecompositionComputer) { this@schurDecomposition.schurDecomposition() }