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
import dev.lounres.kone.context
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


private class QRDecompositionComputerViaHouseholderForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
) : QRDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.qrDecomposition(): QRDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return QRDecomposition(
            leftUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") }
        )
        
        var q = matrixFactory.mapMatrix(
            rowNumber = n,
            columnNumber = n,
            numbers = KoneMap.build(keyEquality = MDIndex.equality( ), keyHashing = MDIndex.hashing()) {
                for (i in 0u ..< n) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
            }
        )
        var r = this
        
        KoneContext.unwrap(
            numberField,
            complexNumberFieldExtension,
            numberOrder,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            conjugateTransposeMatrixComputer,
        )
        
        for (k in 0u ..< n - 1u) {
            val xElementNormsSquared = KoneList.generate(k ..< n) { index -> r[index, k].norm() }
            val xNorm = xElementNormsSquared.sum().positiveSquareRoot()
            val maxXElementIndex = xElementNormsSquared.maxIndex().also { if (xElementNormsSquared[it].isZero()) continue } + k
            if (maxXElementIndex != k) {
                val permutation = matrixFactory.mapMatrix(
                    rowNumber = n,
                    columnNumber = n,
                    numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                        for (i in 0u ..< n) {
                            if (i == k || i == maxXElementIndex) continue
                            set(MDIndex.of(i, i), complexNumberFieldExtension.one)
                        }
                        set(MDIndex.of(k, maxXElementIndex), complexNumberFieldExtension.one)
                        set(MDIndex.of(maxXElementIndex, k), complexNumberFieldExtension.one)
                    }
                )
                r = permutation * r
                q *= permutation
            }
            
            val u = matrixFactory.generateMatrix(rowNumber = n, columnNumber = 1u) { row, _ ->
                when {
                    row < k -> ComplexNumber(numberField.zero, numberField.zero)
                    row == k -> r[k, k] + (xNorm * r[k, k] / r[k, k].absoluteValue())
                    else -> r[row, k]
                }
            }
            val v = u / complexNumberFieldExtension.valueOf((k ..< n).asKoneSequence().let { it.sumOf<_, Number> { index -> u[index, 0u].norm() } }.positiveSquareRoot())
            val qk = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
                }
            ) - 2 * v * v.conjugateTranspose()
            
            r = qk * r
            q *= qk.conjugateTranspose()
        }
        
        return QRDecomposition(
            leftUnitary = q,
            rightUpperTriangular = matrixFactory.generateMatrix(columnNumber = r.columnNumber, rowNumber = r.rowNumber) { row, column ->
                if (row > column) complexNumberFieldExtension.zero else r[row, column]
            },
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.viaHouseholderForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
): QRDecompositionComputer<ComplexNumber<Number>, Matrix> = QRDecompositionComputerViaHouseholderForComplexNumbers(
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.viaHouseholderForComplexNumbers(): QRDecompositionComputer<ComplexNumber<Number>, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaHouseholderForComplexNumbers(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        numberOrder = koneContextRegistry.requestFor(Order.Key<Number>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, Matrix>()) {
            "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.setViaHouseholderForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
) {
    QRDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaHouseholderForComplexNumbers<Number, Matrix>(
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.setViaHouseholderForComplexNumbers() {
    QRDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaHouseholderForComplexNumbers<Number, Matrix>()
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.useViaHouseholderForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
) {
    QRDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaHouseholderForComplexNumbers(
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.useViaHouseholderForComplexNumbers() {
    QRDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaHouseholderForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>()
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}