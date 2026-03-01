package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.FieldExtension
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.QRDecomposition
import dev.lounres.kone.algebraic.algorithms.QRDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.positiveSquareRoot
import dev.lounres.kone.algebraic.algorithms.qrDecomposition
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


// TODO: Move conjugation out
private class QRDecompositionComputerViaGramSchmidtForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) : QRDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.qrDecomposition(): QRDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        
        val qBuilder = SettableMDList2(rowNumber = n, columnNumber = n) { row, column -> this[row, column] }
        
        fun ComplexNumber<Number>.conjugate(): ComplexNumber<Number> =
            ComplexNumber(realPart = realPart, imaginaryPart = numberField { -imaginaryPart })
        
        for (i in 0u ..< n) context(numberField, complexNumberFieldExtension) {
            for (j in 0u ..< i) {
                var scalarProduct = complexNumberFieldExtension.zero
                for (t in 0u ..< n) {
                    scalarProduct += qBuilder[t, j].conjugate() * qBuilder[t, i]
                }
                for (t in 0u ..< n) qBuilder[t, i] -= scalarProduct * qBuilder[t, j]
            }
            
            var normSquared = numberField.zero
            for (t in 0u ..< n) normSquared += qBuilder[t, i].let { it.realPart * it.realPart + it.imaginaryPart * it.imaginaryPart }
            val norm = positiveSquareRootComputer { normSquared.positiveSquareRoot() }
            for (t in 0u ..< n) qBuilder[t, i] /= norm
        }
        
        val q = matrixFactory.generateMatrix(rowNumber = n, columnNumber = n) { row, column -> qBuilder[row, column] }
        val r = matrixFactory.generateMatrix(rowNumber = n, columnNumber = n) { row, column ->
            if (row > column) return@generateMatrix complexNumberFieldExtension.zero
            var scalarProduct = complexNumberFieldExtension.zero
            for (t in 0u ..< n) complexNumberFieldExtension {
                scalarProduct += qBuilder[t, row].conjugate() * this[t, column]
            }
            scalarProduct
        }
        
        return QRDecomposition(
            leftUnitary = q,
            rightUpperTriangular = r,
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.viaGramSchmidtForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
): QRDecompositionComputer<ComplexNumber<Number>, Matrix> = QRDecompositionComputerViaGramSchmidtForComplexNumbers(
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    positiveSquareRootComputer = positiveSquareRootComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.viaGramSchmidtForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): QRDecompositionComputer<ComplexNumber<Number>, Matrix> {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            ),
        ),
        isNullable = false,
    )
    val koneContextRegistry = koneContextRegistry.get()
    return viaGramSchmidtForComplexNumbers(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<$numberType, $matrixType>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<$numberType, $matrixType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)) {
            "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<$numberType, $matrixType>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>(numberType = numberType)) {
            "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.setViaGramSchmidtForComplexNumbers(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) {
    QRDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGramSchmidtForComplexNumbers<Number, Matrix>(
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            positiveSquareRootComputer = positiveSquareRootComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.setViaGramSchmidtForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    QRDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGramSchmidtForComplexNumbers<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.useViaGramSchmidtForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            ),
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = complexNumberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    QRDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaGramSchmidtForComplexNumbers(
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            positiveSquareRootComputer = positiveSquareRootComputer,
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.useViaGramSchmidtForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            ),
        ),
        isNullable = false,
    )
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val matrixWithPropertiesType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.MatrixWithProperties",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = complexNumberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    QRDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val qrDecompositionComputer = viaGramSchmidtForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}