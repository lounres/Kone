/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.DeterminantComputer
import dev.lounres.kone.algebraic.algorithms.DeterminantKey
import dev.lounres.kone.algebraic.algorithms.determinant
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class DeterminantComputerViaGaussianElimination<Number, Matrix : MDList2<Number>>(
    private val field: Field<Number>,
) : DeterminantComputer<Number, Matrix> {
    override fun Matrix.determinant(): Number {
        require(rowNumber == columnNumber) { "Cannot compute determinant of matrix with non-equal numbers of rows and columns." }
        
        KoneContext.localUnwrap(field)
        
        val n = rowNumber
        val source = SettableMDList2.generate(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        var result = field.one
        
        for (currentRow in 0u ..< n) {
            val nonZeroRow = scope {
                var nonZeroRow = currentRow
                
                while (nonZeroRow < n) {
                    if (source[currentRow, nonZeroRow].isNotZero()) break
                    nonZeroRow++
                }
                
                if (nonZeroRow == n) return field.zero
                nonZeroRow
            }
            
            if (nonZeroRow != currentRow) for (column in 0u ..< n)
                source[nonZeroRow, column] = source[currentRow, column].also { source[currentRow, column] = source[nonZeroRow, column] }
            
            val nonZeroCoef = source[currentRow, currentRow]
            for (column in 0u ..< n) {
                source[currentRow, column] /= nonZeroCoef
            }
            result *= nonZeroCoef
            
            for (row in currentRow + 1u ..< n) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) {
                    source[row, column] -= source[currentRow, column] * rowCoef
                }
            }
        }
        
        return result
    }
}

public fun <Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaGaussianElimination(
    field: Field<Number>,
): DeterminantComputer<Number, Matrix> = DeterminantComputerViaGaussianElimination(
    field = field,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object DeterminantComputerGaussianEliminationSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.viaGaussianElimination(): DeterminantComputer<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaGaussianElimination(
            field = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "DeterminantComputer.viaGaussianElimination<${suppliedTypeOf<Number>()}, ?>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaGaussianElimination(
        field: Field<Number>,
    ) {
        DeterminantComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaGaussianElimination<Number, Matrix>(
                field = field,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> DeterminantComputer.Companion.setViaGaussianElimination() {
        DeterminantComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaGaussianElimination<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <@Supply Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.useViaGaussianElimination(
        field: Field<Number>,
    ) {
        DeterminantKey<Number>() correspondsTo RegisteredValueProvider.cached {
            val determinantComputer = viaGaussianElimination<Number, MatrixWithProperties<Number, Matrix>>(
                field = field,
            )
            determinantComputer { matrix.get().determinant() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, _: KoneContextRegistry.Provider)
    public fun <@Supply Number, Matrix : MDList2<Number>> DeterminantComputer.Companion.useViaGaussianElimination() {
        DeterminantKey<Number>() correspondsTo RegisteredValueProvider.cached {
            val determinantComputer = viaGaussianElimination<Number, MatrixWithProperties<Number, Matrix>>()
            determinantComputer { matrix.get().determinant() }
        }
    }
}