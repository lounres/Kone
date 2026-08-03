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


// X = L L^*, L - lower triangular with real and positive diagonal entries
public data class CholeskyDecomposition<out Number, out Matrix : MDList2<Number>>(
    val leftLowerTriangular: Matrix,
    val rightUpperTriangular: Matrix,
) {
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<CholeskyDecomposition<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.CholeskyDecomposition.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

@GenerateKoneContextKey
public fun interface CholeskyDecompositionComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.choleskyDecomposition(): CholeskyDecomposition<Number, Matrix>
    
    public companion object;
}

context(choleskyDecompositionComputer: CholeskyDecompositionComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.choleskyDecomposition(): CholeskyDecomposition<Number, Matrix> =
    with(choleskyDecompositionComputer) { this@choleskyDecomposition.choleskyDecomposition() }