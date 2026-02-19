package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class TransposeMatrixKey<Number, Matrix : MDList2<Number>>(
    public val matrixType: SuppliedType,
) : RegistryKey<Matrix> {
    override fun equals(other: Any?): Boolean = other is TransposeMatrixKey<*, *> && matrixType == other.matrixType
    override fun hashCode(): Int = matrixType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.TransposeMatrixKey<?, $matrixType>"
}

public fun interface TransposeMatrixComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.transpose(): Matrix
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<TransposeMatrixComputer<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer.Key<?, $matrixType>"
    }
}

context(transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.transpose(): Matrix =
    with(transposeMatrixComputer) { this@transpose.transpose() }