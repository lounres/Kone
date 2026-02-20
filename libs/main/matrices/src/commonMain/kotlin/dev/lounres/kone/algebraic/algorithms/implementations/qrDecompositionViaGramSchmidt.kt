package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.QRDecomposition
import dev.lounres.kone.algebraic.algorithms.QRDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.positiveSquareRoot
import dev.lounres.kone.algebraic.algorithms.qrDecomposition
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


private class QRDecompositionComputerViaGramSchmidt<Number, Matrix : MDList2<Number>>(
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val field: Field<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) : QRDecompositionComputer<Number, Matrix> {
    override fun Matrix.qrDecomposition(): QRDecomposition<Number, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non" }
        val n = this.rowNumber
        
        val qBuilder = SettableMDList2(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        
        for (i in 0u ..< n) field {
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
            for (t in 0u ..< n) field {
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

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> QRDecompositionComputer.Companion.viaGramSchmidt(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): QRDecompositionComputer<Number, Matrix> {
    val koneContextRegistry = koneContextRegistry.get()
    return viaGramSchmidt(
        matrixFactory = koneContextRegistry[MatrixFactory.Key<Number, Matrix>(matrixType = matrixType)],
        field = koneContextRegistry[Field.Key<Number>(numberType = numberType)],
        positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)]
    )
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> QRDecompositionComputer.Companion.useViaGramSchmidt(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<Number, MatrixWithProperties<Number, Matrix>>,
    field: Field<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
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
    QRDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaGramSchmidt(
            matrixFactory = matrixFactory,
            field = field,
            positiveSquareRootComputer = positiveSquareRootComputer,
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<Number, Matrix>>, matrix: MatrixWithProperties.Provider<Number, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<Number>> QRDecompositionComputer.Companion.useViaGramSchmidt(
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
    QRDecomposition.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        val qrDecompositionComputer = viaGramSchmidt(
            matrixFactory = koneContextRegistry[MatrixFactory.Key<Number, MatrixWithProperties<Number, Matrix>>(matrixType = matrixWithPropertiesType)],
            field = koneContextRegistry[Field.Key<Number>(numberType = numberType)],
            positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)],
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}