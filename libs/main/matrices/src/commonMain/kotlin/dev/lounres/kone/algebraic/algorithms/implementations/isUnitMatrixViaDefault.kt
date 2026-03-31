/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsUnitMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsUnitMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isOne
import dev.lounres.kone.algebraic.isOne
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.utils.allIndexed
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.SuppliedType


private class IsUnitMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsUnitMatrixChecker<Number, Matrix> {
    override fun Matrix.isOne(): Boolean =
        size[0u] == size[1u] && this.allIndexed { index, value ->
            numberRing { if (index[0u] == index[1u]) value.isOne() else value.isZero() }
        }
}

public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsUnitMatrixChecker<Number, Matrix> = IsUnitMatrixCheckerViaDefault(
    numberRing = numberRing,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.viaDefault(
    numberType: SuppliedType,
): IsUnitMatrixChecker<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>(numberType = numberType)) {
            "IsUnitMatrixChecker.viaDefault<$numberType, ?>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.setViaDefault(
    matrixType: SuppliedType,
    numberRing: CommutativeRing<Number>,
) {
    IsUnitMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberRing = numberRing,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.setViaDefault(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    IsUnitMatrixChecker.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberType = numberType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.useViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsUnitMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isUnitMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberRing = numberRing,
        )
        isUnitMatrixChecker { matrix.get().isOne() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.useViaDefault(
    numberType: SuppliedType,
) {
    IsUnitMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isUnitMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
        )
        isUnitMatrixChecker { matrix.get().isOne() }
    }
}