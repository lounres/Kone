package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey


public object IsUnitMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsUnitMatrixKey"
}

public fun interface IsUnitMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isOne(): Boolean
    
    public companion object;
}

context(isUnitMatrixChecker: IsUnitMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isOne(): Boolean =
    with(isUnitMatrixChecker) { this@isOne.isOne() }