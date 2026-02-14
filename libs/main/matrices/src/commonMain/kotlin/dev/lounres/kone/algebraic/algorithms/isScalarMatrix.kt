package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey


public object IsScalarMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsScalarMatrixKey"
}

public fun interface IsScalarMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isScalar(): Boolean
    
    public companion object;
}

context(isScalarMatrixChecker: IsScalarMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isScalar(): Boolean =
    with(isScalarMatrixChecker) { this@isScalar.isScalar() }