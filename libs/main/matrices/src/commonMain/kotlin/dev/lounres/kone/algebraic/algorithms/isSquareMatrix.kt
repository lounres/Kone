package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey


public object IsSquareMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsSquareMatrixKey"
}

public fun interface IsSquareMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isSquare(): Boolean
    
    public companion object;
}

context(isSquareMatrixChecker: IsSquareMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isSquare(): Boolean =
    with(isSquareMatrixChecker) { this@isSquare.isSquare() }