package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixCategoryOverField
import dev.lounres.kone.algebraic.algorithms.IsDiagonalMatrixChecker
import dev.lounres.kone.algebraic.algorithms.LogarithmMatrixComputer
import dev.lounres.kone.algebraic.algorithms.MatrixProductComputer
import dev.lounres.kone.algebraic.algorithms.SchurDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.isDiagonal
import dev.lounres.kone.algebraic.algorithms.schurDecomposition
import dev.lounres.kone.algebraic.algorithms.times
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2


private class LogarithmMatrixComputerViaSchurParlett<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
) : LogarithmMatrixComputer<Number, Matrix> {
    override fun Matrix.logarithm(): Matrix? {
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        val t = schurDecomposition.middleUpperTriangular
        if (isDiagonalMatrixChecker { t.isDiagonal() }) {
            val g: Matrix = TODO() // G := log(T)
            return matrixProductComputer { schurDecomposition.leftUnitary * g * schurDecomposition.rightUnitary }
        }
        TODO()
    }
}

public fun <Number, Matrix : MDList2<Number>> LogarithmMatrixComputer.Companion.viaGaussianElimination(
    matrixFactory: MatrixFactory<Number, Matrix>,
    field: Field<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
): LogarithmMatrixComputer<Number, Matrix> = LogarithmMatrixComputerViaSchurParlett(
    matrixFactory = matrixFactory,
    field = field,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
)