/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


// X = Q H Q^*, Q - unitary, H - upper Hessenberg
public data class HessenbergDecomposition<out Number, out Matrix : MDList2<Number>>(
    val leftUnitary: Matrix,
    val middleUpperHessenberg: Matrix,
    val rightUnitary: Matrix,
) {
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<HessenbergDecomposition<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HessenbergDecomposition.Key<?, $matrixType>"
    }
}

public fun interface HessenbergDecompositionComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.hessenbergDecomposition(): HessenbergDecomposition<Number, Matrix>
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<HessenbergDecompositionComputer<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.HessenbergDecompositionComputer.Key<?, $matrixType>"
    }
}

context(hessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.hessenbergDecomposition(): HessenbergDecomposition<Number, Matrix> =
    with(hessenbergDecompositionComputer) { this@hessenbergDecomposition.hessenbergDecomposition() }