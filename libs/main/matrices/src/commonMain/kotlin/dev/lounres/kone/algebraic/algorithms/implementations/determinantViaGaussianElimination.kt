package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.algorithms.DeterminantComputer
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.scope


private class DeterminantComputerViaGaussianElimination<Number, Matrix : MDList2<Number>>(
    private val field: Field<Number>,
) : DeterminantComputer<Number, Matrix> {
    override fun Matrix.determinant(): Number {
        require(rowNumber == columnNumber) { "Cannot compute determinant of matrix with non-equal numbers of rows and columns." }
        
        val n = rowNumber
        val source = SettableMDList2(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        var result = field.one
        
        for (currentRow in 0u ..< n) {
            val nonZeroRow = scope {
                var nonZeroRow = currentRow
                
                while (nonZeroRow < n) {
                    if (field { source[currentRow, nonZeroRow].isNotZero() }) break
                    nonZeroRow++
                }
                
                if (nonZeroRow == n) return field.zero
                nonZeroRow
            }
            
            if (nonZeroRow != currentRow) for (column in 0u ..< n)
                source[nonZeroRow, column] = source[currentRow, column].also { source[currentRow, column] = source[nonZeroRow, column] }
            
            val nonZeroCoef = source[currentRow, currentRow]
            for (column in 0u ..< n) field {
                source[currentRow, column] /= nonZeroCoef
            }
            field { result *= nonZeroCoef }
            
            for (row in currentRow + 1u ..< n) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) field {
                    source[row, column] -= source[currentRow, column] * rowCoef
                }
            }
        }
        
        return result
    }
}

public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaGaussianElimination(
    field: Field<Number>,
): DeterminantComputer<Number, Matrix> = DeterminantComputerViaGaussianElimination(
    field = field,
)