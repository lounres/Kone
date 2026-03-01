package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsAntisymmetricMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isAntisymmetric
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.SuppliedType


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

public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsAntisymmetricMatrixChecker<Number, Matrix> = IsAntisymmetricMatrixCheckerViaDefault(
    numberRing = numberRing,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.viaDefault(
    numberType: SuppliedType,
): IsAntisymmetricMatrixChecker<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>(numberType = numberType)) {
            "IsAntisymmetricMatrixChecker.viaDefault<$numberType, ?>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaDefault(
    matrixType: SuppliedType,
    numberRing: CommutativeRing<Number>,
) {
    IsAntisymmetricMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberRing = numberRing,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.setViaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    IsAntisymmetricMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberType = numberType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.useViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsAntisymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isAntisymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberRing = numberRing,
        )
        isAntisymmetricMatrixChecker { matrix.get().isAntisymmetric() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsAntisymmetricMatrixChecker.Companion.useViaDefault(
    numberType: SuppliedType,
) {
    IsAntisymmetricMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isAntisymmetricMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
        )
        isAntisymmetricMatrixChecker { matrix.get().isAntisymmetric() }
    }
}