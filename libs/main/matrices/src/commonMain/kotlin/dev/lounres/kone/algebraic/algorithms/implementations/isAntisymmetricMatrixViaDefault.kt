package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixChecker
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2


private class IsAntisymmetricMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsAntisymmetricMatrixChecker<Number, Matrix> {
    override fun Matrix.isAntisymmetric(): Boolean {
        if (rowNumber != columnNumber) return false
        for (row in 0u ..< rowNumber) if (numberRing { this[row, row].isNotZero() }) return false
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (numberRing { (this[row, column] + this[column, row]).isNotZero() }) return false
        }
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.default(
    numberRing: CommutativeRing<Number>,
): IsAntisymmetricMatrixChecker<Number, Matrix> = IsAntisymmetricMatrixCheckerViaDefault(
    numberRing = numberRing,
)