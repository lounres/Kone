package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.algorithms.IsZeroMatrixChecker
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.utils.all


private class IsZeroMatrixCheckerViaDefault<out Number, in Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsZeroMatrixChecker<Number, Matrix> {
    override fun Matrix.isZero(): Boolean = this.all { numberRing { it.isZero() } }
}

public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsZeroMatrixChecker<Number, Matrix> = IsZeroMatrixCheckerViaDefault(
    numberRing = numberRing,
)