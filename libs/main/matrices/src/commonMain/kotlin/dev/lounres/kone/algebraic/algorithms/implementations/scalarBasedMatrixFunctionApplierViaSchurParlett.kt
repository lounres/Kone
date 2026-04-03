/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixCategoryOverField
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of


private class ScalarBasedMatrixFunctionApplierViaSchurParlett<Number, Matrix : MDList2<Number>>(
    private val scalarBaseForMatrixFunction: ScalarBaseForMatrixFunction<Number>,
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
) : ScalarBasedMatrixFunctionApplier<Number, Matrix> {
    override fun Matrix.after(scalarBaseForMatrixFunction: ScalarBaseForMatrixFunction<Number>): Matrix {
        require(this.rowNumber == this.columnNumber)
        val n = this.rowNumber
        
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        val t = schurDecomposition.middleUpperTriangular
        if (isDiagonalMatrixChecker { t.isDiagonal() }) {
            val g = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), scalarBaseForMatrixFunction.evaluate(0u, t[i, i]))
                }
            )
            return matrixProductComputer { schurDecomposition.leftUnitary * g * schurDecomposition.rightUnitary }
        }
        TODO()
    }
}

public fun <Number, Matrix : MDList2<Number>> ScalarBasedMatrixFunctionApplier.Companion.viaSchurParlett(
    scalarBaseForMatrixFunction: ScalarBaseForMatrixFunction<Number>,
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
): ScalarBasedMatrixFunctionApplier<Number, Matrix> = ScalarBasedMatrixFunctionApplierViaSchurParlett(
    scalarBaseForMatrixFunction = scalarBaseForMatrixFunction,
    matrixFactory = matrixFactory,
    field = field,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
)