/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
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


private class QRDecompositionComputerViaGramSchmidt<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) : QRDecompositionComputer<Number, Matrix> {
    override fun Matrix.qrDecomposition(): QRDecomposition<Number, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        
        KoneContext.localUnwrap(field)
        
        val qBuilder = SettableMDList2.generate(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        
        for (i in 0u ..< n) {
            for (j in 0u ..< i) {
                var scalarProduct = field.zero
                for (t in 0u ..< n) scalarProduct += qBuilder[t, i] * qBuilder[t, j]
                for (t in 0u ..< n) qBuilder[t, i] -= qBuilder[t, j] * scalarProduct
            }
            
            var normSquared = field.zero
            for (t in 0u ..< n) normSquared += qBuilder[t, i].let { it * it }
            val norm = positiveSquareRootComputer { normSquared.positiveSquareRoot() }
            for (t in 0u ..< n) qBuilder[t, i] /= norm
        }
        
        val q = matrixFactory.generateMatrix(rowNumber = n, columnNumber = n) { row, column -> qBuilder[row, column] }
        val r = matrixFactory.generateMatrix(rowNumber = n, columnNumber = n) { row, column ->
            if (row > column) return@generateMatrix field.zero
            var scalarProduct = field.zero
            for (t in 0u ..< n) {
                scalarProduct += qBuilder[t, row] * this[t, column]
            }
            scalarProduct
        }
        
        return QRDecomposition(
            leftUnitary = q,
            rightUpperTriangular = r,
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> QRDecompositionComputer.Companion.viaGramSchmidt(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
): QRDecompositionComputer<Number, Matrix> = QRDecompositionComputerViaGramSchmidt(
    matrixFactory = matrixFactory,
    field = field,
    positiveSquareRootComputer = positiveSquareRootComputer,
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.viaGramSchmidt(): QRDecompositionComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaGramSchmidt(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>()) {
            "QRDecompositionComputer.viaGramSchmidt<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>()) {
            "QRDecompositionComputer.viaGramSchmidt<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
            "QRDecompositionComputer.viaGramSchmidt<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
        },
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.setViaGramSchmidt(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) {
    QRDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaGramSchmidt(
            matrixFactory = matrixFactory,
            field = field,
            positiveSquareRootComputer = positiveSquareRootComputer,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.setViaGramSchmidt() {
    QRDecompositionComputer.Key<Number, Matrix>() correspondsTo RegisteredValueProvider.cached {
        viaGramSchmidt<Number, Matrix>()
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.useViaGramSchmidt(
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    field: Field<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) {
    QRDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaGramSchmidt(
            matrixFactory = matrixFactory,
            field = field,
            positiveSquareRootComputer = positiveSquareRootComputer,
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Matrix : MDList2<Number>> QRDecompositionComputer.Companion.useViaGramSchmidt() {
    QRDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>() correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaGramSchmidt<Number, MatrixWithProperties<Number, Matrix>>()
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}