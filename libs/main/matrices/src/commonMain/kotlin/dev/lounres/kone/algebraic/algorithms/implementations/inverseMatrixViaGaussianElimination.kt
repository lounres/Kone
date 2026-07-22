/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.InverseMatrixComputer
import dev.lounres.kone.algebraic.algorithms.InverseMatrixKey
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.invert
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.registry.*
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class InverseMatrixComputerViaGaussianElimination<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
) : InverseMatrixComputer<Number, Matrix> {
    override fun Matrix.invert(): Matrix? {
        KoneContext.localUnwrap(field)
        
        if (rowNumber != columnNumber) return null
        
        val n = rowNumber
        val source = SettableMDList2.generate(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        val result = SettableMDList2.generate(rowNumber = n, columnNumber = n) { row, column ->
            if (row == column) field.one else field.zero
        }
        
        for (currentRow in 0u ..< n) {
            val nonZeroRow = scope {
                var nonZeroRow = currentRow
                
                while (nonZeroRow < n) {
                    if (source[currentRow, nonZeroRow].isNotZero()) break
                    nonZeroRow++
                }
                
                if (nonZeroRow == n) return null
                nonZeroRow
            }
            
            if (nonZeroRow != currentRow) for (column in 0u ..< n) {
                source[nonZeroRow, column] = source[currentRow, column].also { source[currentRow, column] = source[nonZeroRow, column] }
                result[nonZeroRow, column] = result[currentRow, column].also { result[currentRow, column] = result[nonZeroRow, column] }
            }
            
            val nonZeroCoef = source[currentRow, currentRow]
            for (column in 0u ..< n) {
                source[currentRow, column] /= nonZeroCoef
                result[currentRow, column] /= nonZeroCoef
            }
            
            for (row in currentRow + 1u ..< n) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) {
                    source[row, column] -= source[currentRow, column] * rowCoef
                    result[row, column] -= result[currentRow, column] * rowCoef
                }
            }
        }
        
        for (currentRow in 1u ..< n) {
            for (row in 0u ..< currentRow) {
                val rowCoef = source[row, currentRow]
                for (column in 0u ..< n) {
                    source[row, column] -= source[currentRow, column] * rowCoef
                    result[row, column] -= result[currentRow, column] * rowCoef
                }
            }
        }
        
        return matrixFactory.generateMatrix(rowNumber = n, columnNumber = n) { row, column -> result[row, column] }
    }
}

public fun <Number, Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaGaussianElimination(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
): InverseMatrixComputer<Number, Matrix> = InverseMatrixComputerViaGaussianElimination(
    matrixFactory = matrixFactory,
    field = field,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object InverseMatrixComputerGaussianEliminationSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.viaGaussianElimination(): InverseMatrixComputer<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaGaussianElimination(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            field = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaGaussianElimination(
        matrixFactory: MatrixFactory<Number, Matrix>,
        field: Field<Number>,
    ) {
        InverseMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaGaussianElimination<Number, Matrix>(
                matrixFactory = matrixFactory,
                field = field,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.setViaGaussianElimination() {
        InverseMatrixComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaGaussianElimination<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaGaussianElimination(
        matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
        field: Field<Number>,
    ) {
        InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val inverseMatrixComputer = viaGaussianElimination(matrixFactory = matrixFactory, field = field)
            inverseMatrixComputer {
                matrix.get().invert()
            }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> InverseMatrixComputer.Companion.useViaGaussianElimination() {
        InverseMatrixKey<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val inverseMatrixComputer = viaGaussianElimination<Number, MatrixWithProperties<Number, Matrix>>()
            inverseMatrixComputer {
                matrix.get().invert()
            }
        }
    }
}