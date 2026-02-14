package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class InverseMatrixKey<Number, Matrix : MDList2<Number>>(
    public val matrixType: SuppliedType,
) : RegistryKey<Matrix> {
    override fun equals(other: Any?): Boolean = other is InverseMatrixKey<*, *> && matrixType == other.matrixType
    override fun hashCode(): Int = matrixType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.InverseMatrixKey<?, $matrixType>"
}

public fun interface InverseMatrixComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.invert(): Matrix?
    
    public companion object;
}

context(inverseMatrixComputer: InverseMatrixComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.invert(): Matrix? =
    with(inverseMatrixComputer) { this@invert.invert() }