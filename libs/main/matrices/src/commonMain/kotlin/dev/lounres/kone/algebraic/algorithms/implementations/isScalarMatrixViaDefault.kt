/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsScalarMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsScalarMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isScalar
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.neq
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class IsScalarMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberEquality: Equality<Number>,
    private val numberRing: CommutativeRing<Number>,
) : IsScalarMatrixChecker<Number, Matrix> {
    override fun Matrix.isScalar(): Boolean {
        KoneContext.localUnwrap(numberEquality, numberRing)
        if (rowNumber != columnNumber) return false
        if (rowNumber == 0u) return true
        for (row in 1u ..< rowNumber) for (column in 0u ..< row) {
            if (this[row, column].isNotZero()) return false
            if (this[column, row].isNotZero()) return false
        }
        val scalar = this[0u, 0u]
        for (row in 1u ..< rowNumber)
            if (this[row, row] neq scalar) return false
        return true
    }
}

public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.viaDefault(
    numberEquality: Equality<Number>,
    numberRing: CommutativeRing<Number>,
): IsScalarMatrixChecker<Number, Matrix> = IsScalarMatrixCheckerViaDefault(
    numberEquality = numberEquality,
    numberRing = numberRing,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object IsScalarMatrixCheckerDefaultSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.viaDefault(): IsScalarMatrixChecker<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaDefault(
            numberEquality = koneContextRegistry.requestFor(Equality.Key<Number>()) {
                "IsScalarMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
            },
            numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
                "IsScalarMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.setViaDefault(
        numberEquality: Equality<Number>,
        numberRing: CommutativeRing<Number>,
    ) {
        IsScalarMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number, Matrix>(
                numberEquality = numberEquality,
                numberRing = numberRing,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.setViaDefault() {
        IsScalarMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number, Matrix>()
        }
    }
    
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.useViaDefault(
        numberEquality: Equality<Number>,
        numberRing: CommutativeRing<Number>,
    ) {
        IsScalarMatrixKey correspondsTo RegisteredValueProvider.cached {
            val isScalarMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
                numberEquality = numberEquality,
                numberRing = numberRing,
            )
            isScalarMatrixChecker { matrix.get().isScalar() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> IsScalarMatrixChecker.Companion.useViaDefault() {
        IsScalarMatrixKey correspondsTo RegisteredValueProvider.cached {
            val isScalarMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>()
            isScalarMatrixChecker { matrix.get().isScalar() }
        }
    }
}