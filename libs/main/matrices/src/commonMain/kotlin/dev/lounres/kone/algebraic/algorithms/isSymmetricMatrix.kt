package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey


public object IsSymmetricMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixKey"
}

public fun interface IsSymmetricMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isSymmetric(): Boolean
    
    public companion object;
}

context(isSymmetricMatrixChecker: IsSymmetricMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isSymmetric(): Boolean =
    with(isSymmetricMatrixChecker) { this@isSymmetric.isSymmetric() }