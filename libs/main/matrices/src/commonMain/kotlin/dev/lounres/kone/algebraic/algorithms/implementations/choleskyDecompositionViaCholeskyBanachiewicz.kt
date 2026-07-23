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


private class CholeskyDecompositionViaCholeskyBanachiewicz<Number, Matrix : MDList2<Number>>(
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
        
        for (i in 0u ..< n) for (j in 0u .. i) {
            val sum = (0u ..< j).asKoneSequence().sumOf { l[i, it] * l[j, it] }
            
            if (i == j)
                l[i, j] = (this[i, i] - sum).positiveSquareRoot()
            else
                l[i, j] = (this[i, j] - sum) / l[j, j]
        }
        
        val lResult = matrixFactory.generateMatrix(n, n) { row, column -> l[row, column] }
        
        return CholeskyDecomposition(
            leftLowerTriangular = lResult,
            rightUpperTriangular = lResult.transpose(),
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.viaCholeskyBanachiewicz(
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
): CholeskyDecompositionComputer<Number, Matrix> = CholeskyDecompositionViaCholeskyBanachiewicz(
    matrixFactory = matrixFactory,
    numberField = numberField,
    positiveSquareRootComputer = positiveSquareRootComputer,
    transposeMatrixComputer = transposeMatrixComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object CholeskyDecompositionComputerCholeskyBanachiewiczSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.viaCholeskyBanachiewicz(): CholeskyDecompositionComputer<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaCholeskyBanachiewicz(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
                "CholeskyDecompositionComputer.viaCholeskyBanachiewicz<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "CholeskyDecompositionComputer.viaCholeskyBanachiewicz<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                "CholeskyDecompositionComputer.viaCholeskyBanachiewicz<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, Matrix>()) {
                "CholeskyDecompositionComputer.viaCholeskyBanachiewicz<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.setViaCholeskyBanachiewicz(
        matrixFactory: MatrixFactory<Number, Matrix>,
        numberField: Field<Number>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    ) {
        CholeskyDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyBanachiewicz(
                matrixFactory = matrixFactory,
                numberField = numberField,
                positiveSquareRootComputer = positiveSquareRootComputer,
                transposeMatrixComputer = transposeMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.setViaCholeskyBanachiewicz() {
        CholeskyDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyBanachiewicz<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.useViaCholeskyBanachiewicz(
        matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
        numberField: Field<Number>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        CholeskyDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val choleskyDecompositionComputer = viaCholeskyBanachiewicz(
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
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> CholeskyDecompositionComputer.Companion.useViaCholeskyBanachiewicz() {
        CholeskyDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val choleskyDecompositionComputer = viaCholeskyBanachiewicz<Number, MatrixWithProperties<Number, Matrix>>()
            choleskyDecompositionComputer { matrix.get().choleskyDecomposition() }
        }
    }
}