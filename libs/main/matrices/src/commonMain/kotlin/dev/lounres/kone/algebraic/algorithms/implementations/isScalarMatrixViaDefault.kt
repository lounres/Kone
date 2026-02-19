package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.algorithms.IsScalarMatrixChecker
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.relations.neq


private class IsScalarMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsScalarMatrixChecker<Number, Matrix> {
    override fun Matrix.isScalar(): Boolean {
        if (rowNumber != columnNumber) return false
        if (rowNumber == 0u) return true
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (numberRing { this[row, column].isNotZero() }) return false
            if (numberRing { this[column, row].isNotZero() }) return false
        }
        val scalar = this[0u, 0u]
        for (row in 1u ..< rowNumber)
            if (numberRing { this[row, row] neq scalar }) return false
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsScalarMatrixChecker<Number, Matrix> = IsScalarMatrixCheckerViaDefault(
    numberRing = numberRing,
)