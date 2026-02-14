package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey


public object IsAntisymmetricMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixKey"
}

public fun interface IsAntisymmetricMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isAntisymmetric(): Boolean
    
    public companion object;
}

context(isAntisymmetricMatrixChecker: IsAntisymmetricMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isAntisymmetric(): Boolean =
    with(isAntisymmetricMatrixChecker) { this@isAntisymmetric.isAntisymmetric() }