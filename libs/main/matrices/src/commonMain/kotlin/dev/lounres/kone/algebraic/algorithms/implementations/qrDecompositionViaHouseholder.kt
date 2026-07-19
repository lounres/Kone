/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.utils.maxIndex
import dev.lounres.kone.collections.utils.sum
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class QRDecompositionComputerViaHouseholder<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val numberField: Field<Number>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    private val transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
) : QRDecompositionComputer<Number, Matrix> {
    override fun Matrix.qrDecomposition(): QRDecomposition<Number, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return QRDecomposition(
            leftUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") }
        )
        
        var q = matrixFactory.mapMatrix(
            rowNumber = n,
            columnNumber = n,
            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                for (i in 0u ..< n) set(MDIndex.of(i, i), numberField.one)
            }
        )
        var r = this
        
        for (k in 0u ..< n - 1u) context(
            numberField,
            numberOrder,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            transposeMatrixComputer,
        ) {
            KoneContext.unwrap(numberField, matrixCategoryOverField)
            
            val xElementNormsSquared = KoneList.generate(k ..< n) { index -> r[index, k].let { it * it } }
            val xNorm = xElementNormsSquared.sum().positiveSquareRoot()
            val maxXElementIndex = xElementNormsSquared.maxIndex().also { if (xElementNormsSquared[it].isZero()) continue } + k
            if (maxXElementIndex != k) {
                val permutation = matrixFactory.mapMatrix(
                    rowNumber = n,
                    columnNumber = n,
                    numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                        for (i in 0u ..< n) {
                            if (i == k || i == maxXElementIndex) continue
                            set(MDIndex.of(i, i), numberField.one)
                        }
                        set(MDIndex.of(k, maxXElementIndex), numberField.one)
                        set(MDIndex.of(maxXElementIndex, k), numberField.one)
                    }
                )
                r = permutation * r
                q *= permutation
            }
            
            val u = matrixFactory.generateMatrix(rowNumber = n, columnNumber = 1u) { row, _ ->
                when {
                    row < k -> numberField.zero
                    row == k -> r[k, k] + (xNorm * r[k, k].signInt())
                    else -> r[row, k]
                }
            }
            val v = u / (k ..< n).asKoneSequence().sumOf { index -> u[index, 0u].let { it * it } }.positiveSquareRoot()
            val qk = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), numberField.one)
                }
            ) - 2 * v * v.transpose()
            
            r = qk * r
            q *= qk.transpose()
        }
        
        return QRDecomposition(
            leftUnitary = q,
            rightUpperTriangular = matrixFactory.generateMatrix(rowNumber = r.rowNumber, columnNumber = r.columnNumber) { row, column ->
                if (row > column) numberField.zero else r[row, column]
            },
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> QRDecompositionComputer.Companion.viaHouseholder(
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
): QRDecompositionComputer<Number, Matrix> = QRDecompositionComputerViaHouseholder(
    matrixFactory = matrixFactory,
    numberField = numberField,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    transposeMatrixComputer = transposeMatrixComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.viaHouseholder(): QRDecompositionComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaHouseholder(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        numberOrder = koneContextRegistry.requestFor(Order.Key<Number>()) {
            "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
            "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<Number, Matrix>()) {
            "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<Number, Matrix>()) {
            "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, Matrix>()) {
            "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.setViaHouseholder(
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
) {
    QRDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaHouseholder(
            matrixFactory = matrixFactory,
            numberField = numberField,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            transposeMatrixComputer = transposeMatrixComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.setViaHouseholder() {
    QRDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaHouseholder<Number, Matrix>()
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.useViaHouseholder(
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, MatrixWithProperties<Number, Matrix>>,
    matrixProductComputer: MatrixProductComputer<Number, MatrixWithProperties<Number, Matrix>>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) {
    QRDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaHouseholder(
            matrixFactory = matrixFactory,
            numberField = numberField,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            transposeMatrixComputer = transposeMatrixComputer,
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.useViaHouseholder() {
    QRDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaHouseholder<Number, MatrixWithProperties<Number, Matrix>>()
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}