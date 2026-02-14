package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixBuilder
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.multidimensionalCollections.MDList2


private class TransposeMatrixComputerViaDefault<Number, Matrix : MDList2<Number>>(
    private val matrixBuilder: MatrixBuilder<Number, Matrix>
) : TransposeMatrixComputer<Number, Matrix> {
    override fun Matrix.transpose(): Matrix =
        matrixBuilder.generateMatrix(rowNumber = columnNumber, columnNumber = rowNumber) { row, column -> this[column, row] }
}

@Suppress("UNCHECKED_CAST")
public fun <Number, Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.default(
    matrixBuilder: MatrixBuilder<Number, Matrix>,
): TransposeMatrixComputer<Number, Matrix> = TransposeMatrixComputerViaDefault(
    matrixBuilder = matrixBuilder,
)