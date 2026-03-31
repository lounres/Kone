/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class ConjugateTransposeMatrixKey<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    public val matrixType: SuppliedType,
) : RegistryKey<Matrix> {
    override fun equals(other: Any?): Boolean = other is ConjugateTransposeMatrixKey<*, *> && matrixType == other.matrixType
    override fun hashCode(): Int = matrixType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixKey<?, $matrixType>"
}

public fun interface ConjugateTransposeMatrixComputer<out Number, Matrix : MDList2<ComplexNumber<Number>>> : KoneContext {
    public fun Matrix.conjugateTranspose(): Matrix
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<ComplexNumber<Number>>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<ConjugateTransposeMatrixComputer<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixComputer.Key<?, $matrixType>"
    }
}

context(conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> Matrix.conjugateTranspose(): Matrix =
    with(conjugateTransposeMatrixComputer) { this@conjugateTranspose.conjugateTranspose() }