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
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


private class LDLDecompositionViaCholeskyBanachiewiczForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
) : LDLDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.ldlDecomposition(): LDLDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute LDL decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return LDLDecomposition(
            leftLowerTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            middleDiagonal = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        KoneContext.localUnwrap(
            numberField,
            complexNumberFieldExtension,
            conjugateTransposeMatrixComputer,
        )
        
        val d = KoneSettableList.generate(n) { _ -> complexNumberFieldExtension.one }
        val l = SettableMDList2.generate(n, n) { row, column -> if (row == column) complexNumberFieldExtension.one else complexNumberFieldExtension.zero }
        
        for (i in 0u ..< n) for (j in 0u .. i) {
            val sum = (0u ..< j).asKoneSequence().sumOf<_, ComplexNumber<Number>> { l[i, it] * l[j, it].conjugate() * d[it] }
            
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
            rightUpperTriangular = lResult.conjugateTranspose(),
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> LDLDecompositionComputer.Companion.viaCholeskyBanachiewiczForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
): LDLDecompositionComputer<ComplexNumber<Number>, Matrix> = LDLDecompositionViaCholeskyBanachiewiczForComplexNumbers(
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
)

// TODO: Remove the checker when KT-73135 will be fixed
public object LDLDecompositionComputerLDLBanachiewiczForComplexNumbersSuppliableTopLevelFunctions {
    @Suppliable
    context(koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> LDLDecompositionComputer.Companion.viaCholeskyBanachiewiczForComplexNumbers(): LDLDecompositionComputer<ComplexNumber<Number>, Matrix> {
        val koneContextRegistry = koneContextRegistry.get()
        return viaCholeskyBanachiewiczForComplexNumbers(
            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>()) {
                "LDLDecompositionComputer.viaCholeskyBanachiewiczForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                "LDLDecompositionComputer.viaCholeskyBanachiewiczForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
                "LDLDecompositionComputer.viaCholeskyBanachiewiczForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
            conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, Matrix>()) {
                "LDLDecompositionComputer.viaCholeskyBanachiewiczForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
            },
        )
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> LDLDecompositionComputer.Companion.setViaCholeskyBanachiewiczForComplexNumbers(
        matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
        numberField: Field<Number>,
        complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
        conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
    ) {
        LDLDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyBanachiewiczForComplexNumbers(
                matrixFactory = matrixFactory,
                numberField = numberField,
                complexNumberFieldExtension = complexNumberFieldExtension,
                conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
            )
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> LDLDecompositionComputer.Companion.setViaCholeskyBanachiewiczForComplexNumbers() {
        LDLDecompositionComputer.Key<ComplexNumber<Number>, Matrix>() correspondsTo RegisteredValueProvider.cached {
            viaCholeskyBanachiewiczForComplexNumbers<Number, Matrix>()
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> LDLDecompositionComputer.Companion.useViaCholeskyBanachiewiczForComplexNumbers(
        matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
        numberField: Field<Number>,
        complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
        conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    ) {
        LDLDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val ldlDecompositionComputer = viaCholeskyBanachiewiczForComplexNumbers(
                matrixFactory = matrixFactory,
                numberField = numberField,
                complexNumberFieldExtension = complexNumberFieldExtension,
                conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
            )
            ldlDecompositionComputer { matrix.get().ldlDecomposition() }
        }
    }
    
    @Suppliable
    context(_: MutableOwnedProviderRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
    public fun <@Supply Number, @Supply Matrix : MDList2<ComplexNumber<Number>>> LDLDecompositionComputer.Companion.useViaCholeskyBanachiewiczForComplexNumbers() {
        LDLDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>() correspondsTo RegisteredValueProvider.cached {
            val ldlDecompositionComputer = viaCholeskyBanachiewiczForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>()
            ldlDecompositionComputer { matrix.get().ldlDecomposition() }
        }
    }
}