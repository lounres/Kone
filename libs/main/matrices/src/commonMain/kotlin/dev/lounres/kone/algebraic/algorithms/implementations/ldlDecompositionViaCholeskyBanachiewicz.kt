/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.map.associate
import dev.lounres.kone.collections.map.mapsTo
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.collections.utils.withIndex
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


private class LDLDecompositionViaCholeskyBanachiewicz<Number, Matrix : MDList2<Number>>(
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
        
        val d = KoneSettableList.generate(n) { _ -> numberField.one }
        val l = SettableMDList2.generate(n, n) { row, column -> if (row == column) numberField.one else numberField.zero }
        
        for (i in 0u ..< n) for (j in 0u .. i) {
            val sum = (0u ..< j).asKoneSequence().sumOf { l[i, it] * l[j, it] * d[it] }
            
            if (i == j)
                d[i] = (this[i, i] - sum)
            else
                l[i, j] = (this[i, j] - sum) / d[j]
        }
        
        val dResult = matrixFactory.mapMatrix(n, n, d.withIndex().associate(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) { (index, value) -> MDIndex.of(index, index) mapsTo value })
        val lResult = matrixFactory.generateMatrix(n, n) { row, column -> l[row, column] }
        
        return LDLDecomposition(
            leftLowerTriangular = lResult,
            middleDiagonal = dResult,
            rightUpperTriangular = lResult.transpose(),
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.viaCholeskyBanachiewicz(
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
): LDLDecompositionComputer<Number, Matrix> = LDLDecompositionViaCholeskyBanachiewicz(
    matrixFactory = matrixFactory,
    numberField = numberField,
    transposeMatrixComputer = transposeMatrixComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object LDLDecompositionComputerLDLBanachiewiczSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.viaCholeskyBanachiewicz(): LDLDecompositionComputer<Number, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaCholeskyBanachiewicz(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
                "LDLDecompositionComputer.viaCholeskyBanachiewicz<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "LDLDecompositionComputer.viaCholeskyBanachiewicz<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, Matrix>()) {
                "LDLDecompositionComputer.viaCholeskyBanachiewicz<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.setViaCholeskyBanachiewicz(
        matrixFactory: MatrixFactory<Number, Matrix>,
        numberField: Field<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    ) {
        LDLDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyBanachiewicz(
                matrixFactory = matrixFactory,
                numberField = numberField,
                transposeMatrixComputer = transposeMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.setViaCholeskyBanachiewicz() {
        LDLDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyBanachiewicz<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.useViaCholeskyBanachiewicz(
        matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
        numberField: Field<Number>,
        transposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    ) {
        LDLDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val ldlDecompositionComputer = viaCholeskyBanachiewicz(
                matrixFactory = matrixFactory,
                numberField = numberField,
                transposeMatrixComputer = transposeMatrixComputer,
            )
            ldlDecompositionComputer { matrix.get().ldlDecomposition() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<Number>> LDLDecompositionComputer.Companion.useViaCholeskyBanachiewicz() {
        LDLDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val ldlDecompositionComputer = viaCholeskyBanachiewicz<Number, MatrixWithProperties<Number, Matrix>>()
            ldlDecompositionComputer { matrix.get().ldlDecomposition() }
        }
    }
}