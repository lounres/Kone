package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey


public object IsZeroMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsZeroMatrixKey"
}

public fun interface IsZeroMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isZero(): Boolean
    
    public companion object;
}

context(isZeroMatrixChecker: IsZeroMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isZero(): Boolean =
    with(isZeroMatrixChecker) { this@isZero.isZero() }