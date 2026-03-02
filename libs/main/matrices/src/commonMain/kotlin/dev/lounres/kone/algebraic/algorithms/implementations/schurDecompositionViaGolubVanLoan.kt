package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.abs
import dev.lounres.kone.algebraic.algorithms.HessenbergDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.SchurDecomposition
import dev.lounres.kone.algebraic.algorithms.SchurDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.hessenbergDecomposition
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.schurDecomposition
import dev.lounres.kone.algebraic.algorithms.transpose
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.leq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


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
        val h = SettableMDList2.generate(n, n) { row, column -> h0[row, column] }
        
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

public fun <Number, Matrix : MDList2<Number>> SchurDecompositionComputer.Companion.viaGolubVanLoan(
    tolerance: Number,
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, Matrix>,
): SchurDecompositionComputer<Number, Matrix> = SchurDecompositionComputerViaGolubVanLoan(
    tolerance = tolerance,
    matrixFactory = matrixFactory,
    numberField = numberField,
    numberOrder = numberOrder,
    transposeMatrixComputer = transposeMatrixComputer,
    hessenbergDecompositionComputer = hessenbergDecompositionComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> SchurDecompositionComputer.Companion.viaGolubVanLoan(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
): SchurDecompositionComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaGolubVanLoan(
        tolerance = tolerance,
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoan<$numberType, $matrixType>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "SchurDecompositionComputer.viaGolubVanLoan<$numberType, $matrixType>"
        },
        numberOrder = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "SchurDecompositionComputer.viaGolubVanLoan<$numberType, $matrixType>"
        },
        transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoan<$numberType, $matrixType>"
        },
        hessenbergDecompositionComputer = koneContextRegistry.requestFor(HessenbergDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoan<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<Number>> SchurDecompositionComputer.Companion.setViaGolubVanLoan(
    matrixType: SuppliedType,
    tolerance: Number,
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, Matrix>,
) {
    SchurDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGolubVanLoan<Number, Matrix>(
            tolerance = tolerance,
            matrixFactory = matrixFactory,
            numberField = numberField,
            numberOrder = numberOrder,
            transposeMatrixComputer = transposeMatrixComputer,
            hessenbergDecompositionComputer = hessenbergDecompositionComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> SchurDecompositionComputer.Companion.setViaGolubVanLoan(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
) {
    SchurDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGolubVanLoan<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
            tolerance = tolerance,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> SchurDecompositionComputer.Companion.useViaGolubVanLoan(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, MatrixWithProperties<Number, Matrix>>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
    SchurDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val schurDecompositionComputer = viaGolubVanLoan<Number, MatrixWithProperties<Number, Matrix>>(
            tolerance = tolerance,
            matrixFactory = matrixFactory,
            numberField = numberField,
            numberOrder = numberOrder,
            transposeMatrixComputer = transposeMatrixComputer,
            hessenbergDecompositionComputer = hessenbergDecompositionComputer,
        )
        schurDecompositionComputer { matrix.get().schurDecomposition() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> SchurDecompositionComputer.Companion.useViaGolubVanLoan(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
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
    SchurDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val schurDecompositionComputer = viaGolubVanLoan<Number, MatrixWithProperties<Number, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
            tolerance = tolerance,
        )
        schurDecompositionComputer { matrix.get().schurDecomposition() }
    }
}