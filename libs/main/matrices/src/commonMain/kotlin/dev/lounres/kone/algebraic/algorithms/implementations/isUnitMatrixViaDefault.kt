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
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.utils.allIndexed
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class IsUnitMatrixCheckerViaDefault<Number, Matrix : MDList2<Number>>(
    private val numberEquality: Equality<Number>,
    private val numberRing: CommutativeRing<Number>,
) : IsUnitMatrixChecker<Number, Matrix> {
    override fun Matrix.isOne(): Boolean {
        KoneContext.localUnwrap(numberEquality, numberRing)
        return rowNumber == columnNumber && this.allIndexed { index, value -> if (index[0u] == index[1u]) value.isOne() else value.isZero() }
    }
}

public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.viaDefault(
    numberEquality: Equality<Number>,
    numberRing: CommutativeRing<Number>,
): IsUnitMatrixChecker<Number, Matrix> = IsUnitMatrixCheckerViaDefault(
    numberEquality = numberEquality,
    numberRing = numberRing,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object IsUnitMatrixCheckerDefaultSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.viaDefault(): IsUnitMatrixChecker<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaDefault(
            numberEquality = koneContextRegistry.requestFor(Equality.Key<Number>()) {
                "IsUnitMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
            },
            numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
                "IsUnitMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.setViaDefault(
        numberEquality: Equality<Number>,
        numberRing: CommutativeRing<Number>,
    ) {
        IsUnitMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number, Matrix>(
                numberEquality = numberEquality,
                numberRing = numberRing,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.setViaDefault() {
        IsUnitMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number, Matrix>()
        }
    }
    
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.useViaDefault(
        numberEquality: Equality<Number>,
        numberRing: CommutativeRing<Number>,
    ) {
        IsUnitMatrixKey correspondsTo RegisteredValueProvider.cached {
            val isUnitMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
                numberEquality = numberEquality,
                numberRing = numberRing,
            )
            isUnitMatrixChecker { matrix.get().isOne() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> IsUnitMatrixChecker.Companion.useViaDefault() {
        IsUnitMatrixKey correspondsTo RegisteredValueProvider.cached {
            val isUnitMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>()
            isUnitMatrixChecker { matrix.get().isOne() }
        }
    }
}