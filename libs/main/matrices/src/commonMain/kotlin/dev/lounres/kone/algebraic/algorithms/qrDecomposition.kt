/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


// A = QR where Q is unitary and R is upper triangular
public data class QRDecomposition<out Number, out Matrix : MDList2<Number>>(
    val leftUnitary: Matrix,
    val rightUpperTriangular: Matrix,
) {
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<QRDecomposition<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.QRDecomposition.Key<?, $matrixType>"
    }
}

public fun interface QRDecompositionComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.qrDecomposition(): QRDecomposition<Number, Matrix>
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<QRDecompositionComputer<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.QRDecompositionComputer.Key<?, $matrixType>"
    }
}

context(qrDecompositionComputer: QRDecompositionComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.qrDecomposition(): QRDecomposition<Number, Matrix> =
    with(qrDecompositionComputer) { this@qrDecomposition.qrDecomposition() }