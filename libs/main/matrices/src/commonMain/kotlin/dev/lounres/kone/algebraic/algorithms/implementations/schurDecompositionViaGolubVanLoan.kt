package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.abs
import dev.lounres.kone.algebraic.algorithms.HessenbergDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.SchurDecomposition
import dev.lounres.kone.algebraic.algorithms.SchurDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.hessenbergDecomposition
import dev.lounres.kone.algebraic.algorithms.transpose
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.leq


private class SchurDecompositionComputerViaGolubVanLoan<Number, Matrix : MDList2<Number>>(
    private val tolerance: Number,
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val numberField: Field<Number>,
    private val numberOrder: Order<Number>,
    private val transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    private val hessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, Matrix>,
) : SchurDecompositionComputer<Number, Matrix> {
    override fun Matrix.schurDecomposition(): SchurDecomposition<Number, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute Schur decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return SchurDecomposition(
            leftUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            middleUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        val (q0, h0) = hessenbergDecompositionComputer { this.hessenbergDecomposition() }
        
        var q = q0
        val h = SettableMDList2(n, n) { row, column -> h0[row, column] }
        
        context(
            numberField,
            numberOrder,
        ) {
            var k = 0u
            
            while (true) {
                for (i in 0u ..< n - 1u)
                    if (abs(h[i + 1u, i]) leq tolerance * (abs(h[i, i]) + abs(h[i + 1u, i + 1u])))
                        h[i + 1u, i] = numberField.zero
                
                while (k < n)
                    when {
                        k + 2u >= n -> k = n
                        h[n - k - 1u, n - k - 2u].isZero() -> k += 1u
                        h[n - k - 2u, n - k - 3u].isZero() -> k += 2u
                        else -> break
                    }
                
                if (k == n) break
                
                var l = 2u
                while (k + l < n && h[n - k - l, n - k - l - 1u].isNotZero()) l++
                
                TODO("Not yet implemented")
            }
        }
        
        return SchurDecomposition(
            leftUnitary = transposeMatrixComputer { q.transpose() },
            middleUpperTriangular = matrixFactory.generateMatrix(rowNumber = h.rowNumber, columnNumber = h.columnNumber) { row, column -> h[row, column] },
            rightUnitary = q,
        )
    }
}