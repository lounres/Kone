package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


// X = Q T Q^*, Q - unitary, T - (quasi-)upper-triangular
public data class SchurDecomposition<out Number, out Matrix : MDList2<Number>>(
    val leftUnitary: Matrix,
    val middleUpperTriangular: Matrix,
    val rightUnitary: Matrix,
) {
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<SchurDecomposition<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SchurDecomposition.Key<?, $matrixType>"
    }
}

public fun interface SchurDecompositionComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.schurDecomposition(): SchurDecomposition<Number, Matrix>
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<SchurDecompositionComputer<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SchurDecompositionComputer.Key<?, $matrixType>"
    }
}

context(schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.schurDecomposition(): SchurDecomposition<Number, Matrix> =
    with(schurDecompositionComputer) { this@schurDecomposition.schurDecomposition() }