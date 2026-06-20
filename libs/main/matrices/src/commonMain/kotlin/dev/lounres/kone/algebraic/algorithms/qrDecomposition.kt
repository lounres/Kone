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


// A = QR where Q is unitary and R is upper triangular
public data class QRDecomposition<out Number, out Matrix : MDList2<Number>>(
    val leftUnitary: Matrix,
    val rightUpperTriangular: Matrix,
) {
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<QRDecomposition<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.QRDecomposition.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

public fun interface QRDecompositionComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.qrDecomposition(): QRDecomposition<Number, Matrix>
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<QRDecompositionComputer<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.QRDecompositionComputer.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

context(qrDecompositionComputer: QRDecompositionComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.qrDecomposition(): QRDecomposition<Number, Matrix> =
    with(qrDecompositionComputer) { this@qrDecomposition.qrDecomposition() }