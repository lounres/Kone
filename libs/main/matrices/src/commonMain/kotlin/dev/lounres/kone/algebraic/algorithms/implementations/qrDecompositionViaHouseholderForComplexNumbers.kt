package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.utils.sum
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.lt
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


private class QRDecompositionComputerViaHouseholderForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val transposeMatrixComputer: TransposeMatrixComputer<ComplexNumber<Number>, Matrix>,
) : QRDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.qrDecomposition(): QRDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        
        var q = matrixFactory.mapMatrix(
            rowNumber = n,
            columnNumber = n,
            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                for (i in 0u ..< n) set(MDIndex.of(i, i), ComplexNumber(numberField.one, numberField.zero))
            }
        )
        var r = this
        
        for (k in 0u ..< n) context(
            numberField,
            complexNumberFieldExtension,
            numberOrder,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            transposeMatrixComputer,
        ) {
            val xElementNormsSquared = KoneList.generate(k ..< n) { index -> r[index, k].let { it.realPart * it.realPart + it.imaginaryPart * it.imaginaryPart } } // TODO: Replace with complex number norm
            val xNorm = xElementNormsSquared.sum().positiveSquareRoot()
            val maxXElementIndex = scope { // TODO: Move to collections module
                val iterator = xElementNormsSquared.iterator()
                if (!iterator.hasNext()) throw NoSuchElementException()
                var maxIndex = iterator.nextIndex()
                var maxElement = iterator.getAndMoveNext()
                if (!iterator.hasNext()) return@scope maxIndex
                do {
                    val nextIndex = iterator.nextIndex()
                    val nextElement = iterator.getAndMoveNext()
                    if (maxElement lt nextElement) {
                        maxIndex = nextIndex
                        maxElement = nextElement
                    }
                } while (iterator.hasNext())
                if (maxElement.isZero()) continue
                return@scope maxIndex
            } + k
            if (maxXElementIndex != k) {
                val permutation = matrixFactory.mapMatrix(
                    rowNumber = n,
                    columnNumber = n,
                    numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                        for (i in 0u ..< n) {
                            if (i == k || i == maxXElementIndex) continue
                            set(MDIndex.of(i, i), ComplexNumber(numberField.one, numberField.zero))
                        }
                        set(MDIndex.of(k, maxXElementIndex), ComplexNumber(numberField.one, numberField.zero))
                        set(MDIndex.of(maxXElementIndex, k), ComplexNumber(numberField.one, numberField.zero))
                    }
                )
                r = permutation * r
                q *= permutation
            }
            
            val u = matrixFactory.generateMatrix(rowNumber = n, columnNumber = 1u) { row, _ ->
                when {
                    row < k -> ComplexNumber(numberField.zero, numberField.zero)
                    row == k -> r[k, k] + (xNorm * r[k, k] / r[k, k].let { it.realPart * it.realPart + it.imaginaryPart * it.imaginaryPart })
                    else -> r[row, k]
                }
            }
            val v = u / complexNumberFieldExtension.valueOf((k ..< n).asKoneSequence().let { context(numberField) { it.sumOf { index -> u[index, 0u].let { it.realPart * it.realPart + it.imaginaryPart * it.imaginaryPart } } } }.positiveSquareRoot()) // TODO: Replace with complex number norm
            val qk = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
                }
            ) - 2 * v * matrixFactory.generateMatrix(rowNumber = 1u, columnNumber = n) { _, column ->
                v[column, 0u].let { ComplexNumber(realPart = it.realPart, imaginaryPart = -it.imaginaryPart) }
            }
            
            r = qk * r
            q *= matrixFactory.generateMatrix(rowNumber = qk.columnNumber, columnNumber = qk.rowNumber) { row, column ->
                qk[column, row].let { ComplexNumber(realPart = it.realPart, imaginaryPart = -it.imaginaryPart) }
            }
        }
        
        return QRDecomposition(
            leftUnitary = q,
            rightUpperTriangular = r,
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.viaHouseholderForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    transposeMatrixComputer: TransposeMatrixComputer<ComplexNumber<Number>, Matrix>,
): QRDecompositionComputer<ComplexNumber<Number>, Matrix> = QRDecompositionComputerViaHouseholderForComplexNumbers(
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    transposeMatrixComputer = transposeMatrixComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.viaHouseholderForComplexNumbers(
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
    return viaHouseholderForComplexNumbers(
        matrixFactory = koneContextRegistry[MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)],
        numberField = koneContextRegistry[Field.Key<Number>(numberType = numberType)],
        complexNumberFieldExtension = koneContextRegistry[FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)],
        numberOrder = koneContextRegistry[Order.Key<Number>(elementType = numberType)],
        positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)],
        matrixCategoryOverField = koneContextRegistry[MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)],
        matrixProductComputer = koneContextRegistry[MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)],
        transposeMatrixComputer = koneContextRegistry[TransposeMatrixComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)],
    )
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.useViaHouseholderForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    transposeMatrixComputer: TransposeMatrixComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
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
        val qrDecompositionComputer = viaHouseholderForComplexNumbers(
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            transposeMatrixComputer = transposeMatrixComputer,
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> QRDecompositionComputer.Companion.useViaHouseholderForComplexNumbers(
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
                type = numberType,
            ),
            SuppliedProjection.Regular(
                variance = INVARIANT,
                type = matrixType,
            ),
        ),
        isNullable = false,
    )
    QRDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        val qrDecompositionComputer = viaHouseholderForComplexNumbers(
            matrixFactory = koneContextRegistry[MatrixFactory.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType)],
            numberField = koneContextRegistry[Field.Key<Number>(numberType = numberType)],
            complexNumberFieldExtension = koneContextRegistry[FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)],
            numberOrder = koneContextRegistry[Order.Key<Number>(elementType = numberType)],
            positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>(numberType = numberType)],
            matrixCategoryOverField = koneContextRegistry[MatrixCategoryOverField.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType)],
            matrixProductComputer = koneContextRegistry[MatrixProductComputer.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType)],
            transposeMatrixComputer = koneContextRegistry[TransposeMatrixComputer.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType)],
        )
        qrDecompositionComputer { matrix.get().qrDecomposition() }
    }
}