/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public class ConjugateTransposeMatrixKey<@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> : SuppliedTypeRegistryKey<Matrix>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixKey<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
}

public fun interface ConjugateTransposeMatrixComputer<out Number, Matrix : MDList2<ComplexNumber<Number>>> : KoneContext {
    public fun Matrix.conjugateTranspose(): Matrix
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> : SuppliedTypeRegistryKey<ConjugateTransposeMatrixComputer<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ConjugateTransposeMatrixComputer.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

context(conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> Matrix.conjugateTranspose(): Matrix =
    with(conjugateTransposeMatrixComputer) { this@conjugateTranspose.conjugateTranspose() }