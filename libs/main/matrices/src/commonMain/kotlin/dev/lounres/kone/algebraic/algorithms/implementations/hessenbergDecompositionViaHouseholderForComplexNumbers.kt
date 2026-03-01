package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
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


private class HessenbergDecompositionComputerViaHouseholderForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
) : HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.hessenbergDecomposition(): HessenbergDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute QR decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return HessenbergDecomposition(
            leftUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            middleUpperHessenberg = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        var q = matrixFactory.mapMatrix(
            rowNumber = n,
            columnNumber = n,
            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                for (i in 0u ..< n) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
            }
        )
        var r = this
        
        for (k in 0u ..< n - 2u) context(
            numberField,
            complexNumberFieldExtension,
            numberOrder,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            conjugateTransposeMatrixComputer,
        ) {
            val xElementNormsSquared = KoneList.generate(k + 1u ..< n) { index -> r[index, k].let { it.realPart * it.realPart + it.imaginaryPart * it.imaginaryPart } } // TODO: Replace with complex number norm
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
            } + k + 1u
            
            val u = matrixFactory.generateMatrix(rowNumber = n, columnNumber = 1u) { row, _ ->
                when {
                    row < k + 1u -> complexNumberFieldExtension.zero
                    row == k + 1u -> r[k + 1u, k] + (xNorm * r[k + 1u, k] / r[k + 1u, k].let { it.realPart * it.realPart + it.imaginaryPart * it.imaginaryPart }.positiveSquareRoot()) // TODO: Add corrections for small r[k + 1u, k]
                    else -> r[row, k]
                }
            }
            val v = u / complexNumberFieldExtension.valueOf((k + 1u ..< n).asKoneSequence().let { numberField { it.sumOf { index -> u[index, 0u].let { it.realPart * it.realPart + it.imaginaryPart * it.imaginaryPart } } } }.positiveSquareRoot())
            val qk = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
                }
            ) - 2 * v * v.conjugateTranspose()
            val qkInverse = qk.conjugateTranspose()
            
            r = qk * r * qkInverse
            q *= qkInverse
        }
        
        return HessenbergDecomposition(
            leftUnitary = q,
            middleUpperHessenberg = matrixFactory.generateMatrix(r.rowNumber, r.columnNumber) { row, column ->
                if (row > column + 1u) complexNumberFieldExtension.zero else r[row, column]
            },
            rightUnitary = conjugateTransposeMatrixComputer { q.conjugateTranspose() },
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.viaHouseholderForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
): HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix> = HessenbergDecompositionComputerViaHouseholderForComplexNumbers(
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.viaHouseholderForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
): HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix> {
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
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
        numberOrder = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>(numberType = numberType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
        conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.setViaHouseholderForComplexNumbers(
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
) {
    HessenbergDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaHouseholderForComplexNumbers(
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.setViaHouseholderForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
) {
    HessenbergDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaHouseholderForComplexNumbers<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.useViaHouseholderForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
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
    HessenbergDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val hessenbergDecompositionComputer = viaHouseholderForComplexNumbers(
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
        )
        hessenbergDecompositionComputer { matrix.get().hessenbergDecomposition() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> HessenbergDecompositionComputer.Companion.useViaHouseholderForComplexNumbers(
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
    HessenbergDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val hessenbergDecompositionComputer = viaHouseholderForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
        )
        hessenbergDecompositionComputer { matrix.get().hessenbergDecomposition() }
    }
}