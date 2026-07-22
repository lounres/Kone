/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.CommutativeRing
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.IsZeroMatrixChecker
import dev.lounres.kone.algebraic.algorithms.IsZeroMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.isZero
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class IsZeroMatrixCheckerViaDefault<out Number, in Matrix : MDList2<Number>>(
    private val numberRing: CommutativeRing<Number>,
) : IsZeroMatrixChecker<Number, Matrix> {
    override fun Matrix.isZero(): Boolean = this.all { numberRing.numberIsZero { it.isZero() } }
}

public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.viaDefault(
    numberRing: CommutativeRing<Number>,
): IsZeroMatrixChecker<Number, Matrix> = IsZeroMatrixCheckerViaDefault(
    numberRing = numberRing,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object IsZeroMatrixCheckerDefaultSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.viaDefault(): IsZeroMatrixChecker<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaDefault(
            numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
                "IsZeroMatrixChecker.viaDefault<${suppliedTypeOf<Number>()}, ?>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.setViaDefault(
        numberRing: CommutativeRing<Number>,
    ) {
        IsZeroMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number, Matrix>(
                numberRing = numberRing,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.setViaDefault() {
        IsZeroMatrixChecker.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaDefault<Number, Matrix>()
        }
    }
    
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.useViaDefault(
        numberRing: CommutativeRing<Number>,
    ) {
        IsZeroMatrixKey correspondsTo RegisteredValueProvider.cached {
            val isZeroMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>(
                numberRing = numberRing,
            )
            isZeroMatrixChecker { matrix.get().isZero() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> IsZeroMatrixChecker.Companion.useViaDefault() {
        IsZeroMatrixKey correspondsTo RegisteredValueProvider.cached {
            val isZeroMatrixChecker = viaDefault<Number, MatrixWithProperties<Number, Matrix>>()
            isZeroMatrixChecker { matrix.get().isZero() }
        }
    }
}