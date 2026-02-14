package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.algorithms.IsUnitMatrixChecker
import dev.lounres.kone.algebraic.isOne
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.utils.allIndexed


private class IsUnitMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsUnitMatrixChecker<Number, Matrix> {
    override fun Matrix.isOne(): Boolean =
        size[0u] == size[1u] && this.allIndexed { index, value ->
            numberRing { if (index[0u] == index[1u]) value.isOne() else value.isZero() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.default(
    numberRing: CommutativeRing<Number>,
): IsUnitMatrixChecker<Number, Matrix> = IsUnitMatrixCheckerViaDefault(
    numberRing = numberRing,
)