package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.multidimensionalCollections.MDList2


private class TransposeMatrixComputerViaDefault<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>
) : TransposeMatrixComputer<Number, Matrix> {
    override fun Matrix.transpose(): Matrix =
        matrixFactory.generateMatrix(rowNumber = columnNumber, columnNumber = rowNumber) { row, column -> this[column, row] }
}

@Suppress("UNCHECKED_CAST")
public fun <Number, Matrix : MDList2<Number>> TransposeMatrixComputer.Companion.default(
    matrixFactory: MatrixFactory<Number, Matrix>,
): TransposeMatrixComputer<Number, Matrix> = TransposeMatrixComputerViaDefault(
    matrixFactory = matrixFactory,
)