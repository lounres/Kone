package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixBuilder
import dev.lounres.kone.algebraic.algorithms.InverseMatrixComputer
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.scope


private class InverseMatrixComputerViaGaussianElimination<Number, Matrix : MDList2<Number>>(
    private val matrixBuilder: MatrixBuilder<Number, Matrix>,
    private val field: Field<Number>,
) : InverseMatrixComputer<Number, Matrix> {
    override fun Matrix.invert(): Matrix? {
        if (rowNumber != columnNumber) return null
        
        val n = rowNumber
        val source = SettableMDList2(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        val result = SettableMDList2(rowNumber = n, columnNumber = n) { row, column ->
            if (row == column) field.one else field.zero
        }
        
        for (currentRow in 0u ..< n) {
            val nonZeroRow = scope {
                var nonZeroRow = currentRow
                
                while (nonZeroRow < n) {
                    if (field { source[currentRow, nonZeroRow].isNotZero() }) break
                    nonZeroRow++
                }
                
                if (nonZeroRow == n) return null
                nonZeroRow
            }
            
            if (nonZeroRow != currentRow) for (column in 0u ..< n) {
                source[nonZeroRow, column] = source[currentRow, column].also { source[currentRow, column] = source[nonZeroRow, column] }
                result[nonZeroRow, column] = result[currentRow, column].also { result[currentRow, column] = result[nonZeroRow, column] }
            }
            
            val nonZeroCoef = source[currentRow, currentRow]
            for (column in 0u ..< n) field {
                source[currentRow, column] /= nonZeroCoef
                result[currentRow, column] /= nonZeroCoef
            }
            
            for (row in currentRow + 1u ..< n) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) field {
                    source[row, column] -= source[currentRow, column] * rowCoef
                    result[row, column] -= result[currentRow, column] * rowCoef
                }
            }
        }
        
        for (currentRow in 1u ..< n) {
            for (row in 0u ..< currentRow) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) field {
                    source[row, column] -= source[currentRow, column] * rowCoef
                    result[row, column] -= result[currentRow, column] * rowCoef
                }
            }
        }
        
        return matrixBuilder.generateMatrix(rowNumber = n, columnNumber = n) { row, column -> result[row, column] }
    }
}

@Suppress("UNCHECKED_CAST")
public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaGaussianElimination(
    matrixBuilder: MatrixBuilder<Number, Matrix>,
    field: Field<Number>,
): InverseMatrixComputer<Number, Matrix> = InverseMatrixComputerViaGaussianElimination(
    matrixBuilder = matrixBuilder,
    field = field,
)