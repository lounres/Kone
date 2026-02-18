package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public fun interface MatrixProductComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public operator fun Matrix.times(other: Matrix): Matrix
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<MatrixProductComputer<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.MatrixProductComputer.Key<?, $matrixType>"
    }
}

context(matrixProductComputer: MatrixProductComputer<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.times(other: Matrix): Matrix =
    with(matrixProductComputer) { this@times * other }