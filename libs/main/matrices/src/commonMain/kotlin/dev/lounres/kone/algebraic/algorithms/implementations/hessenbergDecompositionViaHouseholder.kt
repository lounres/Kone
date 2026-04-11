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
import dev.lounres.kone.collections.utils.max
import dev.lounres.kone.collections.utils.sum
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
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
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


private class HessenbergDecompositionComputerViaHouseholder<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val numberField: Field<Number>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    private val transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
) : HessenbergDecompositionComputer<Number, Matrix> {
    override fun Matrix.hessenbergDecomposition(): HessenbergDecomposition<Number, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n <= 1u) return HessenbergDecomposition(
            leftUnitary = matrixFactory.generateMatrix(n, n) { _, _ -> numberField.one },
            middleUpperHessenberg = this,
            rightUnitary = matrixFactory.generateMatrix(n, n) { _, _ -> numberField.one },
        )
        
        var q = matrixFactory.mapMatrix(
            rowNumber = n,
            columnNumber = n,
            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                for (i in 0u ..< n) set(MDIndex.of(i, i), numberField.one)
            }
        )
        var r = this
        
        for (k in 0u ..< n - 2u) context(
            numberField,
            numberOrder,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            transposeMatrixComputer,
        ) {
            val xElementNormsSquared = KoneList.generate(k + 1u ..< n) { index -> r[index, k].let { it * it } }
            val xNorm = xElementNormsSquared.sum().positiveSquareRoot()
            if (xElementNormsSquared.max().isZero()) continue
            
            val u = matrixFactory.generateMatrix(rowNumber = n, columnNumber = 1u) { row, _ ->
                when {
                    row < k + 1u -> numberField.zero
                    row == k + 1u -> r[k + 1u, k] + (xNorm * r[k + 1u, k].signInt()) // TODO: Add corrections for small r[k + 1u, k]
                    else -> r[row, k]
                }
            }
            val v = u / (k + 1u ..< n).asKoneSequence().sumOf { index -> u[index, 0u].let { it * it } }.positiveSquareRoot()
            val qk = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), numberField.one)
                }
            ) - 2 * v * v.transpose()
            val qkInverse = qk.transpose()
            
            r = qk * r * qkInverse
            q *= qkInverse
        }
        
        return HessenbergDecomposition(
            leftUnitary = q,
            middleUpperHessenberg = matrixFactory.generateMatrix(r.rowNumber, r.columnNumber) { row, column ->
                if (row > column + 1u) numberField.zero else r[row, column]
            },
            rightUnitary = transposeMatrixComputer { q.transpose() },
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.viaHouseholder(
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
): HessenbergDecompositionComputer<Number, Matrix> = HessenbergDecompositionComputerViaHouseholder(
    matrixFactory = matrixFactory,
    numberField = numberField,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    transposeMatrixComputer = transposeMatrixComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.viaHouseholder(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): HessenbergDecompositionComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaHouseholder(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholder<$numberType, $matrixType>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "HessenbergDecompositionComputer.viaHouseholder<$numberType, $matrixType>"
        },
        numberOrder = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "HessenbergDecompositionComputer.viaHouseholder<$numberType, $matrixType>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>(numberType = numberType)) {
            "HessenbergDecompositionComputer.viaHouseholder<$numberType, $matrixType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<Number, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholder<$numberType, $matrixType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholder<$numberType, $matrixType>"
        },
        transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholder<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.setViaHouseholder(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
) {
    HessenbergDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
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

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.setViaHouseholder(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    HessenbergDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaHouseholder<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType
        )
    }
}

context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.useViaHouseholder(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, MatrixWithProperties<Number, Matrix>>,
    matrixProductComputer: MatrixProductComputer<Number, MatrixWithProperties<Number, Matrix>>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    HessenbergDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val hessenbergDecompositionComputer = viaHouseholder(
            matrixFactory = matrixFactory,
            numberField = numberField,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            transposeMatrixComputer = transposeMatrixComputer,
        )
        hessenbergDecompositionComputer { matrix.get().hessenbergDecomposition() }
    }
}

context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> HessenbergDecompositionComputer.Companion.useViaHouseholder(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    HessenbergDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val hessenbergDecompositionComputer = viaHouseholder<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
        )
        hessenbergDecompositionComputer { matrix.get().hessenbergDecomposition() }
    }
}