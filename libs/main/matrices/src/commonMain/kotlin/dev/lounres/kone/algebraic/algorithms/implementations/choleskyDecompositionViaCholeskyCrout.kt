/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.utils.sumOf
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
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class CholeskyDecompositionViaCholeskyCrout<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val numberField: Field<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
) : CholeskyDecompositionComputer<Number, Matrix> {
    override fun Matrix.choleskyDecomposition(): CholeskyDecomposition<Number, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute Cholesky decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return CholeskyDecomposition(
            leftLowerTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        KoneContext.localUnwrap(
            numberField,
            positiveSquareRootComputer,
            transposeMatrixComputer,
        )
        
        val l = SettableMDList2.generate(n, n) { row, column -> if (row == column) numberField.one else numberField.zero }
        
        for (j in 0u ..< n) {
            val sum = (0u ..< j).asKoneSequence().sumOf { l[j, it].let { it * it } }
            
            l[j, j] = (this[j, j] - sum).positiveSquareRoot()
            
            for (i in j + 1u ..< n) {
                val sum = (0u ..< j).asKoneSequence().sumOf { l[i, it] * l[j, it] }
                l[i, j] = (this[i, j] - sum) / l[j, j]
            }
        }
        
        val lResult = matrixFactory.generateMatrix(n, n) { row, column -> l[row, column] }
        
        return CholeskyDecomposition(
            leftLowerTriangular = lResult,
            rightUpperTriangular = lResult.transpose(),
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.viaCholeskyCrout(
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
): CholeskyDecompositionComputer<Number, Matrix> = CholeskyDecompositionViaCholeskyCrout(
    matrixFactory = matrixFactory,
    numberField = numberField,
    positiveSquareRootComputer = positiveSquareRootComputer,
    transposeMatrixComputer = transposeMatrixComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object CholeskyDecompositionComputerCholeskyCroutSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.viaCholeskyCrout(): CholeskyDecompositionComputer<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaCholeskyCrout(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
                "CholeskyDecompositionComputer.viaCholeskyCrout<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "CholeskyDecompositionComputer.viaCholeskyCrout<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                "CholeskyDecompositionComputer.viaCholeskyCrout<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, Matrix>()) {
                "CholeskyDecompositionComputer.viaCholeskyCrout<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.setViaCholeskyCrout(
        matrixFactory: MatrixFactory<Number, Matrix>,
        numberField: Field<Number>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    ) {
        CholeskyDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyCrout(
                matrixFactory = matrixFactory,
                numberField = numberField,
                positiveSquareRootComputer = positiveSquareRootComputer,
                transposeMatrixComputer = transposeMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.setViaCholeskyCrout() {
        CholeskyDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyCrout<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.useViaCholeskyCrout(
        matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
        numberField: Field<Number>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        CholeskyDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val choleskyDecompositionComputer = viaCholeskyCrout(
                matrixFactory = matrixFactory,
                numberField = numberField,
                positiveSquareRootComputer = positiveSquareRootComputer,
                transposeMatrixComputer = transposeMatrixComputer,
            )
            choleskyDecompositionComputer { matrix.get().choleskyDecomposition() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.useViaCholeskyCrout() {
        CholeskyDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val choleskyDecompositionComputer = viaCholeskyCrout<Number, MatrixWithProperties<Number, Matrix>>()
            choleskyDecompositionComputer { matrix.get().choleskyDecomposition() }
        }
    }
}