package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixBuilder
import dev.lounres.kone.algebraic.algorithms.InverseMatrixComputer
import dev.lounres.kone.multidimensionalCollections.MDList2


private class InverseMatrixComputerViaAdjugateMatrixAndDeterminant<Number, Matrix : MDList2<Number>>(
    private val matrixBuilder: MatrixBuilder<Number, Matrix>
) : InverseMatrixComputer<Number, Matrix> {
    override fun Matrix.invert(): Matrix? = TODO()
}

@Suppress("UNCHECKED_CAST")
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaAdjugateMatrixAndDeterminant(
    matrixBuilder: MatrixBuilder<Number, Matrix>,
): InverseMatrixComputer<Number, Matrix> = InverseMatrixComputerViaAdjugateMatrixAndDeterminant(
    matrixBuilder = matrixBuilder,
)