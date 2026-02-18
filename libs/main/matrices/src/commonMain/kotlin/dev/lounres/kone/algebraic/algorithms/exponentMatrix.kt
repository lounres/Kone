package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class ExponentMatrixKey<Number, Matrix : MDList2<Number>>(
    public val matrixType: SuppliedType,
) : RegistryKey<Matrix> {
    override fun equals(other: Any?): Boolean = other is ExponentMatrixKey<*, *> && matrixType == other.matrixType
    override fun hashCode(): Int = matrixType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ExponentMatrixKey<?, $matrixType>"
}

public fun interface ExponentMatrixComputer<out Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.exponent(): Matrix
    
    public companion object;
}

context(exponentMatrixComputer: ExponentMatrixComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.exponent(): Matrix =
    with(exponentMatrixComputer) { this@exponent.exponent() }