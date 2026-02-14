package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey


public object IsDiagonalMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixKey"
}

public fun interface IsDiagonalMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isDiagonal(): Boolean
    
    public companion object;
}

context(isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isDiagonal(): Boolean =
    with(isDiagonalMatrixChecker) { this@isDiagonal.isDiagonal() }