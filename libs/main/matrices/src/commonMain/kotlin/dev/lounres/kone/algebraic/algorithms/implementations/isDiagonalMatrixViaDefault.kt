package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixChecker
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2


private class IsDiagonalMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsDiagonalMatrixChecker<Number, Matrix> {
    override fun Matrix.isDiagonal(): Boolean {
        if (rowNumber != columnNumber) return false
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (numberRing { this[row, column].isNotZero() }) return false
            if (numberRing { this[column, row].isNotZero() }) return false
        }
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsDiagonalMatrixChecker<Number, Matrix> = IsDiagonalMatrixCheckerViaDefault(
    numberRing = numberRing,
)