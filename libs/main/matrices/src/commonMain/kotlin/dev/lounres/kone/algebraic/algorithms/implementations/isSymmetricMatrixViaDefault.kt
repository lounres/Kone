package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.algorithms.IsSymmetricMatrixChecker
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.relations.neq


private class IsSymmetricMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsSymmetricMatrixChecker<Number, Matrix> {
    override fun Matrix.isSymmetric(): Boolean {
        if (rowNumber != columnNumber) return false
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (numberRing { this[row, column] neq this[column, row] }) return false
        }
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsSymmetricMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsSymmetricMatrixChecker<Number, Matrix> = IsSymmetricMatrixCheckerViaDefault(
    numberRing = numberRing,
)