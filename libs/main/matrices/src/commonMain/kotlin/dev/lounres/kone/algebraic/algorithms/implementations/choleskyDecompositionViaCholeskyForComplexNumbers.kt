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


private class CholeskyDecompositionViaCholeskyForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
) : CholeskyDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.choleskyDecomposition(): CholeskyDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute Cholesky decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return CholeskyDecomposition(
            leftLowerTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        KoneContext.localUnwrap(
            numberField,
            complexNumberFieldExtension,
            positiveSquareRootComputer,
            conjugateTransposeMatrixComputer,
        )

        val a = SettableMDList2.generate(n, n) { row, column -> this[row, column] }
        val l = SettableMDList2.generate(n, n) { row, column -> if (row == column) complexNumberFieldExtension.one else complexNumberFieldExtension.zero }

        for (k in 0u ..< n) {
            val aK = a[k, k]
            val aKSqrt = aK.realPart.positiveSquareRoot()

            for (i in k ..< n)
                l[i, k] = (k .. i).asKoneSequence().sumOf<_, ComplexNumber<Number>> { l[i, it] * a[it, k] } / aKSqrt

            for (i in (k + 1u) ..< n) for (j in (k + 1u) ..< n)
                a[i, j] -= a[i, k] * a[k, j] / aK
        }

        val lResult = matrixFactory.generateMatrix(n, n) { row, column -> l[row, column] }

        return CholeskyDecomposition(
            leftLowerTriangular = lResult,
            rightUpperTriangular = lResult.conjugateTranspose(),
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> CholeskyDecompositionComputer.Companion.viaCholeskyForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
): CholeskyDecompositionComputer<ComplexNumber<Number>, Matrix> = CholeskyDecompositionViaCholeskyForComplexNumbers(
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    positiveSquareRootComputer = positiveSquareRootComputer,
    conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object CholeskyDecompositionComputerCholeskyForComplexNumbersSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> CholeskyDecompositionComputer.Companion.viaCholeskyForComplexNumbers(): CholeskyDecompositionComputer<ComplexNumber<Number>, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaCholeskyForComplexNumbers(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>()) {
                "CholeskyDecompositionComputer.viaCholeskyForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "CholeskyDecompositionComputer.viaCholeskyForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
                "CholeskyDecompositionComputer.viaCholeskyForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                "CholeskyDecompositionComputer.viaCholeskyForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, Matrix>()) {
                "CholeskyDecompositionComputer.viaCholeskyForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }

    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> CholeskyDecompositionComputer.Companion.setViaCholeskyForComplexNumbers(
        matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
        numberField: Field<Number>,
        complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
    ) {
        CholeskyDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyForComplexNumbers(
                matrixFactory = matrixFactory,
                numberField = numberField,
                complexNumberFieldExtension = complexNumberFieldExtension,
                positiveSquareRootComputer = positiveSquareRootComputer,
                conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
            )
        }
    }

    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> CholeskyDecompositionComputer.Companion.setViaCholeskyForComplexNumbers() {
        CholeskyDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyForComplexNumbers<Number, Matrix>()
        }
    }

    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> CholeskyDecompositionComputer.Companion.useViaCholeskyForComplexNumbers(
        matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
        numberField: Field<Number>,
        complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
        positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
        conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    ) {
        CholeskyDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val choleskyDecompositionComputer = viaCholeskyForComplexNumbers(
                matrixFactory = matrixFactory,
                numberField = numberField,
                complexNumberFieldExtension = complexNumberFieldExtension,
                positiveSquareRootComputer = positiveSquareRootComputer,
                conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
            )
            choleskyDecompositionComputer { matrix.get().choleskyDecomposition() }
        }
    }

    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> CholeskyDecompositionComputer.Companion.useViaCholeskyForComplexNumbers() {
        CholeskyDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val choleskyDecompositionComputer = viaCholeskyForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>()
            choleskyDecompositionComputer { matrix.get().choleskyDecomposition() }
        }
    }
}