package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.algorithms.InverseMatrixComputer
import dev.lounres.kone.multidimensionalCollections.MDList2


private class InverseMatrixComputerViaAdjugateMatrixAndDeterminant<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>
) : InverseMatrixComputer<Number, Matrix> {
    override fun Matrix.invert(): Matrix? = TODO()
}

@Suppress("UNCHECKED_CAST")
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaAdjugateMatrixAndDeterminant(
    matrixFactory: MatrixFactory<Number, Matrix>,
): InverseMatrixComputer<Number, Matrix> = InverseMatrixComputerViaAdjugateMatrixAndDeterminant(
    matrixFactory = matrixFactory,
)