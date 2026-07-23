/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class LDLDecompositionViaCholesky<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val numberField: Field<Number>,
    private val transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
) : LDLDecompositionComputer<Number, Matrix> {
    override fun Matrix.ldlDecomposition(): LDLDecomposition<Number, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute LDL decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return LDLDecomposition(
            leftLowerTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            middleDiagonal = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        KoneContext.localUnwrap(
            numberField,
            transposeMatrixComputer,
        )
        
        val a = SettableMDList2.generate(n, n) { row, column -> this[row, column] }
        val d = KoneMutableMap.of<MDIndex, Number>(
            keyEquality = MDIndex.equality(),
            keyHashing = MDIndex.hashing(),
        )
        val l = SettableMDList2.generate(n, n) { row, column -> if (row == column) numberField.one else numberField.zero }
        
        for (k in 0u ..< n) {
            val aK = a[k, k]
            
            d[MDIndex.of(k, k)] = aK
            
            for (i in k + 1u ..< n)
                l[i, k] = (k .. i).asKoneSequence().sumOf { l[i, it] * a[it, k] } / aK
            
            for (i in k + 1u ..< n) for (j in k + 1u ..< n)
                a[i, j] -= a[i, k] * a[k, j] / aK
        }
        
        val dResult = matrixFactory.mapMatrix(n, n, d)
        val lResult = matrixFactory.generateMatrix(n, n) { row, column -> l[row, column] }
        
        return LDLDecomposition(
            leftLowerTriangular = lResult,
            middleDiagonal = dResult,
            rightUpperTriangular = lResult.transpose(),
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.viaCholesky(
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
): LDLDecompositionComputer<Number, Matrix> = LDLDecompositionViaCholesky(
    matrixFactory = matrixFactory,
    numberField = numberField,
    transposeMatrixComputer = transposeMatrixComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object LDLDecompositionComputerLDLSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.viaCholesky(): LDLDecompositionComputer<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaCholesky(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
                "LDLDecompositionComputer.viaCholesky<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "LDLDecompositionComputer.viaCholesky<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, Matrix>()) {
                "LDLDecompositionComputer.viaCholesky<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.setViaCholesky(
        matrixFactory: MatrixFactory<Number, Matrix>,
        numberField: Field<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    ) {
        LDLDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholesky(
                matrixFactory = matrixFactory,
                numberField = numberField,
                transposeMatrixComputer = transposeMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.setViaCholesky() {
        LDLDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholesky<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.useViaCholesky(
        matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
        numberField: Field<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        LDLDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val ldlDecompositionComputer = viaCholesky(
                matrixFactory = matrixFactory,
                numberField = numberField,
                transposeMatrixComputer = transposeMatrixComputer,
            )
            ldlDecompositionComputer { matrix.get().ldlDecomposition() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.useViaCholesky() {
        LDLDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val ldlDecompositionComputer = viaCholesky<Number, MatrixWithProperties<Number, Matrix>>()
            ldlDecompositionComputer { matrix.get().ldlDecomposition() }
        }
    }
}