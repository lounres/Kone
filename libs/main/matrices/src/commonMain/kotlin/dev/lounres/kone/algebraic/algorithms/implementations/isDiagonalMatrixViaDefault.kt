/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isDiagonal
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.contexts.KoneContextHolder
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrapLocallyAsExtensionReceivers
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class IsDiagonalMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsDiagonalMatrixChecker<Number, Matrix> {
    override fun Matrix.isDiagonal(): Boolean {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberRing)
        if (rowNumber != columnNumber) return false
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (this@isDiagonal[row, column].isNotZero()) return false
            if (this@isDiagonal[column, row].isNotZero()) return false
        }
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsDiagonalMatrixChecker<Number, Matrix> = IsDiagonalMatrixCheckerViaDefault(
    numberRing = numberRing,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.viaDefault(): IsDiagonalMatrixChecker<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaDefault(
        numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
            "IsDiagonalMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.setViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsDiagonalMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>(
            numberRing = numberRing,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.setViaDefault() {
    IsDiagonalMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaDefault<Number, Matrix>()
    }
}

context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.useViaDefault(
    numberRing: CommutativeRing<Number>,
) {
    IsDiagonalMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isDiagonalMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
            numberRing = numberRing,
        )
        isDiagonalMatrixChecker { matrix.get().isDiagonal() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, Matrix : MDList2<Number>> IsDiagonalMatrixChecker.Companion.useViaDefault() {
    IsDiagonalMatrixKey correspondsTo RegisteredValueProvider.cached {
        val isDiagonalMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>()
        isDiagonalMatrixChecker { matrix.get().isDiagonal() }
    }
}