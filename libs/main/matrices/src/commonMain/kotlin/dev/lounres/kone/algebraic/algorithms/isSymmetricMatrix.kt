package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public object IsSymmetricMatrixKey : RegistryKey<Boolean> {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixKey"
}

public fun interface IsSymmetricMatrixChecker<out Number, in Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.isSymmetric(): Boolean
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<IsSymmetricMatrixChecker<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixChecker.Key<?, $matrixType>"
    }
}

context(isSymmetricMatrixChecker: IsSymmetricMatrixChecker<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.isSymmetric(): Boolean =
    with(isSymmetricMatrixChecker) { this@isSymmetric.isSymmetric() }