package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.IsSquareMatrixChecker
import dev.lounres.kone.multidimensionalCollections.MDList2


private object IsSquareMatrixCheckerViaDefault : IsSquareMatrixChecker<Nothing, MDList2<Nothing>> {
    override fun MDList2<Nothing>.isSquare(): Boolean = rowNumber == columnNumber
}

@Suppress("UNCHECKED_CAST")
public fun <Number, Matrix : MDList2<Number>> IsSquareMatrixChecker.Companion.default(): IsSquareMatrixChecker<Number, Matrix> =
    IsSquareMatrixCheckerViaDefault as IsSquareMatrixChecker<Number, Matrix>