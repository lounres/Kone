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
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class HessenbergDecompositionComputerViaHouseholderForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val numberEquality: Equality<Number>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
) : HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.hessenbergDecomposition(): HessenbergDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return HessenbergDecomposition(
            leftUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            middleUpperHessenberg = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        var q = matrixFactory.mapMatrix(
            rowNumber = n,
            columnNumber = n,
            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                for (i in 0u ..< n) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
            }
        )
        var r = this
        
        KoneContext.localUnwrap(
            numberField,
            complexNumberFieldExtension,
            numberOrder,
            numberEquality,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            conjugateTransposeMatrixComputer,
        )
        
        for (k in 0u ..< n - 2u) {
            val xElementNormsSquared = KoneList.generate(k + 1u ..< n) { index -> r[index, k].norm() }
            val xNorm = xElementNormsSquared.sum().positiveSquareRoot()
            if (xElementNormsSquared.max().isZero()) continue
            
            val u = matrixFactory.generateMatrix(rowNumber = n, columnNumber = 1u) { row, _ ->
                when {
                    row < k + 1u -> complexNumberFieldExtension.zero
                    row == k + 1u -> r[k + 1u, k] + (xNorm * r[k + 1u, k] / r[k + 1u, k].absoluteValue()) // TODO: Add corrections for small r[k + 1u, k]
                    else -> r[row, k]
                }
            }
            val v = u / complexNumberFieldExtension.valueOf((k + 1u ..< n).asKoneSequence().let { it.sumOf<_, Number> { index -> u[index, 0u].norm() } }.positiveSquareRoot())
            val qk = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
                }
            ) - 2 * v * v.conjugateTranspose()
            val qkInverse = qk.conjugateTranspose()
            
            r = qk * r * qkInverse
            q *= qkInverse
        }
        
        return HessenbergDecomposition(
            leftUnitary = q,
            middleUpperHessenberg = matrixFactory.generateMatrix(r.rowNumber, r.columnNumber) { row, column ->
                if (row > column + 1u) complexNumberFieldExtension.zero else r[row, column]
            },
            rightUnitary = q.conjugateTranspose(),
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.viaHouseholderForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberEquality: Equality<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
): HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix> = HessenbergDecompositionComputerViaHouseholderForComplexNumbers(
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    numberEquality = numberEquality,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object HessenbergDecompositionComputerHouseholderForComplexNumbersSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.viaHouseholderForComplexNumbers(): HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaHouseholderForComplexNumbers(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberEquality = koneContextRegistry.requestFor(Equality.Key<Number>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberOrder = koneContextRegistry.requestFor(Order.Key<Number>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, Matrix>()) {
                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.setViaHouseholderForComplexNumbers(
        matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
        numberField: Field<Number>,
        complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
        numberEquality: Equality<Number>,
        numberOrder: Order<Number>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
        matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
        conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
    ) {
        HessenbergDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaHouseholderForComplexNumbers(
                matrixFactory = matrixFactory,
                numberField = numberField,
                complexNumberFieldExtension = complexNumberFieldExtension,
                numberEquality = numberEquality,
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
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.setViaHouseholderForComplexNumbers() {
        HessenbergDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaHouseholderForComplexNumbers<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.useViaHouseholderForComplexNumbers(
        matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
        numberField: Field<Number>,
        complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
        numberEquality: Equality<Number>,
        numberOrder: Order<Number>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
        matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
        conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    ) {
        HessenbergDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val hessenbergDecompositionComputer = viaHouseholderForComplexNumbers(
                matrixFactory = matrixFactory,
                numberField = numberField,
                complexNumberFieldExtension = complexNumberFieldExtension,
                numberEquality = numberEquality,
                numberOrder = numberOrder,
                positiveSquareRootComputer = positiveSquareRootComputer,
                matrixCategoryOverField = matrixCategoryOverField,
                matrixProductComputer = matrixProductComputer,
                conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
            )
            hessenbergDecompositionComputer { matrix.get().hessenbergDecomposition() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.useViaHouseholderForComplexNumbers() {
        HessenbergDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val hessenbergDecompositionComputer = viaHouseholderForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>()
            hessenbergDecompositionComputer { matrix.get().hessenbergDecomposition() }
        }
    }
}