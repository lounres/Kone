package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class LogarithmMatrixKey<Number, Matrix : MDList2<Number>>(
    public val matrixType: SuppliedType,
) : RegistryKey<Matrix> {
    override fun equals(other: Any?): Boolean = other is LogarithmMatrixKey<*, *> && matrixType == other.matrixType
    override fun hashCode(): Int = matrixType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.LogarithmMatrixKey<?, $matrixType>"
}

public fun interface LogarithmMatrixComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.logarithm(): Matrix?
    
    public companion object;
}

context(logarithmMatrixComputer: LogarithmMatrixComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.logarithm(): Matrix? =
    with(logarithmMatrixComputer) { this@logarithm.logarithm() }