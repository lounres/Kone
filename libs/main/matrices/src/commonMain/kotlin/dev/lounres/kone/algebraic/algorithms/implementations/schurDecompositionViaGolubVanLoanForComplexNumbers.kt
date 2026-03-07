package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.leq
import dev.lounres.kone.scope
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


private class SchurDecompositionComputerViaGolubVanLoanForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val tolerance: Number,
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val numberField: Field<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
    private val hessenbergDecompositionComputer: HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix>,
) : SchurDecompositionComputer<ComplexNumber<Number>, Matrix> {
    override fun Matrix.schurDecomposition(): SchurDecomposition<ComplexNumber<Number>, Matrix> {
        require(rowNumber == columnNumber) { "Cannot compute Schur decomposition for non-square matrix." }
        val n = this.rowNumber
        if (n == 0u) return SchurDecomposition(
            leftUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            middleUpperTriangular = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
            rightUnitary = matrixFactory.generateMatrix(0u, 0u) { _, _ -> error("Matrix 0✖0 tried to allocate elements") },
        )
        
        val (q0, h0) = hessenbergDecompositionComputer { this.hessenbergDecomposition() }
        
        val q = SettableMDList2.generate(n, n) { row, column -> q0[row, column] }
        val h = SettableMDList2.generate(n, n) { row, column -> h0[row, column] }
        
        context(
            numberField,
            complexNumberFieldExtension,
            numberOrder,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            conjugateTransposeMatrixComputer,
        ) {
            var k = 0u
            
            while (true) {
                for (i in 0u ..< n - 1u)
                    if (h[i + 1u, i].absoluteValue() leq tolerance * (h[i, i].absoluteValue() + h[i + 1u, i + 1u].absoluteValue()))
                        h[i + 1u, i] = complexNumberFieldExtension.zero
                
                while (k + 1u < n && h[n - k - 1u, n - k - 2u].isZero()) k++
                
                if (k + 1u >= n) break
                
                if (k + 2u == n || h[n - k - 2u, n - k - 3u].isZero()) {
                    val lambdaSum = h[n - k - 2u, n - k - 2u] + h[n - k - 1u, n - k - 1u]
                    val lambdaProduct = h[n - k - 2u, n - k - 2u] * h[n - k - 1u, n - k - 1u] - h[n - k - 2u, n - k - 1u] * h[n - k - 1u, n - k - 2u]
                    val lambdaDiscriminant = lambdaSum * lambdaSum - 4 * lambdaProduct
                    val lambdaDiscriminantSquareRoot: ComplexNumber<Number>
                    scope {
                        val absoluteValue = lambdaDiscriminant.absoluteValue()
                        val cosWhole = lambdaDiscriminant.realPart / absoluteValue
                        val sinWhole = lambdaDiscriminant.imaginaryPart / absoluteValue
                        val cosHalf = ((1 + cosWhole) / 2).positiveSquareRoot()
                        val sinHalfAbsoluteValue = ((1 - cosWhole) / 2).positiveSquareRoot()
                        val sinHalf = if (sinWhole.isNonNegative()) sinHalfAbsoluteValue else -sinHalfAbsoluteValue
                        lambdaDiscriminantSquareRoot = ComplexNumber(cosHalf, sinHalf) * absoluteValue.positiveSquareRoot()
                    }
                    
                    val lambda = (lambdaSum + lambdaDiscriminantSquareRoot) / 2
                    val lambdaMinusBottomRight = lambda - h[n - k - 1u, n - k - 1u]
                    val bottomLeft = h[n - k - 1u, n - k - 2u]
                    
                    val uCoefs = MDList2.of(
                        rowNumber = 2u,
                        columnNumber = 2u,
                        lambdaMinusBottomRight, bottomLeft.conjugate(),
                        bottomLeft, -lambdaMinusBottomRight.conjugate(),
                    )
                    
                    val u = matrixFactory.generateMatrix(rowNumber = 2u, columnNumber = 2u) { row, column -> uCoefs[row, column] }
                    val v = u / complexNumberFieldExtension.valueOf((lambdaMinusBottomRight.norm() + bottomLeft.norm()).positiveSquareRoot())
                    val vConjugateTranspose = v.conjugateTranspose()
                    
                    for (s in n - k - 2u ..< n) {
                        val column = matrixFactory.mapMatrix(
                            rowNumber = 2u,
                            columnNumber = 1u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = h[n - k - 2u, s]
                                this[MDIndex.of(1u, 0u)] = h[n - k - 1u, s]
                            },
                        )
                        val newColumn = vConjugateTranspose * column
                        h[n - k - 2u, s] = newColumn[0u, 0u]
                        h[n - k - 1u, s] = newColumn[1u, 0u]
                    }
                    
                    for (s in 0u ..< n - k) {
                        val row = matrixFactory.mapMatrix(
                            rowNumber = 1u,
                            columnNumber = 2u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = h[s, n - k - 2u]
                                this[MDIndex.of(0u, 1u)] = h[s, n - k - 1u]
                            },
                        )
                        val newRow = row * v
                        h[s, n - k - 2u] = newRow[0u, 0u]
                        h[s, n - k - 1u] = newRow[0u, 1u]
                    }
                    
                    for (s in 0u ..< n) {
                        val row = matrixFactory.mapMatrix(
                            rowNumber = 1u,
                            columnNumber = 2u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = q[s, n - k - 2u]
                                this[MDIndex.of(0u, 1u)] = q[s, n - k - 1u]
                            },
                        )
                        val newColumn = row * v
                        q[s, n - k - 2u] = newColumn[0u, 0u]
                        q[s, n - k - 1u] = newColumn[0u, 1u]
                    }
                    
                    continue
                }
                
                var l = 3u
                while (k + l < n && h[n - k - l, n - k - l - 1u].isNotZero()) l++
                val m = n - k - l
                
                var x: ComplexNumber<Number>
                var y: ComplexNumber<Number>
                var z: ComplexNumber<Number>
                
                scope {
                    val aSum = h[m + l - 2u, m + l - 2u] + h[m + l - 1u, m + l - 1u]
                    val aProduct = h[m + l - 2u, m + l - 2u] * h[m + l - 1u, m + l - 1u] - h[m + l - 2u, m + l - 1u] * h[m + l - 1u, m + l - 2u]
                    x = h[m, m] * h[m, m] + h[m, m + 1u] * h[m + 1u, m] - h[m, m] * aSum + aProduct
                    y = h[m + 1u, m] * (h[m, m] + h[m + 1u, m + 1u] - aSum)
                    z = h[m + 1u, m] * h[m + 2u, m + 1u]
                }
                
                for (t in 0u .. l - 3u) {
                    val norm = (x.norm() + y.norm() + z.norm()).positiveSquareRoot()
                    val u = matrixFactory.mapMatrix(
                        rowNumber = 3u,
                        columnNumber = 1u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            this[MDIndex.of(0u, 0u)] = x + norm * x / x.absoluteValue()
                            this[MDIndex.of(1u, 0u)] = y
                            this[MDIndex.of(2u, 0u)] = z
                        },
                    )
                    val v = u / complexNumberFieldExtension.valueOf((0u ..< 3u).asKoneSequence().let { numberField { it.sumOf { index -> u[index, 0u].norm() } } }.positiveSquareRoot())
                    val w = matrixFactory.mapMatrix(
                        rowNumber = 3u,
                        columnNumber = 3u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            for (i in 0u ..< 3u) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
                        }
                    ) - 2 * v * v.conjugateTranspose()
                    
                    for (s in maxOf(1u, t) + m - 1u ..< n) {
                        val column = matrixFactory.mapMatrix(
                            rowNumber = 3u,
                            columnNumber = 1u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = h[t + m, s]
                                this[MDIndex.of(1u, 0u)] = h[t + m + 1u, s]
                                this[MDIndex.of(2u, 0u)] = h[t + m + 2u, s]
                            },
                        )
                        val newColumn = w * column
                        h[t + m, s] = newColumn[0u, 0u]
                        h[t + m + 1u, s] = newColumn[1u, 0u]
                        h[t + m + 2u, s] = newColumn[2u, 0u]
                    }
                    
                    for (s in 0u ..< minOf(t + 4u, l) + m) {
                        val row = matrixFactory.mapMatrix(
                            rowNumber = 1u,
                            columnNumber = 3u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = h[s, t + m]
                                this[MDIndex.of(0u, 1u)] = h[s, t + m + 1u]
                                this[MDIndex.of(0u, 2u)] = h[s, t + m + 2u]
                            },
                        )
                        val newRow = row * w
                        h[s, t + m] = newRow[0u, 0u]
                        h[s, t + m + 1u] = newRow[0u, 1u]
                        h[s, t + m + 2u] = newRow[0u, 2u]
                    }
                    
                    for (s in 0u ..< n) {
                        val row = matrixFactory.mapMatrix(
                            rowNumber = 1u,
                            columnNumber = 3u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = q[s, t + m]
                                this[MDIndex.of(0u, 1u)] = q[s, t + m + 1u]
                                this[MDIndex.of(0u, 2u)] = q[s, t + m + 2u]
                            },
                        )
                        val newRow = row * w
                        q[s, t + m] = newRow[0u, 0u]
                        q[s, t + m + 1u] = newRow[0u, 1u]
                        q[s, t + m + 2u] = newRow[0u, 2u]
                    }
                    
                    x = h[t + m + 1u, t + m]
                    y = h[t + m + 2u, t + m]
                    if (t < l - 3u) {
                        z = h[t + m + 3u, t + m]
                    }
                }
                
                if (x.isNotZero() || y.isNotZero()) {
                    val norm = (x.norm() + y.norm()).positiveSquareRoot()
                    val u = matrixFactory.mapMatrix(
                        rowNumber = 2u,
                        columnNumber = 1u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            this[MDIndex.of(0u, 0u)] = x + norm * x / x.absoluteValue()
                            this[MDIndex.of(1u, 0u)] = y
                        },
                    )
                    val v = u / complexNumberFieldExtension.valueOf((0u ..< 2u).asKoneSequence().let{ numberField { it.sumOf { index -> u[index, 0u].norm() } } }.positiveSquareRoot())
                    val w = matrixFactory.mapMatrix(
                        rowNumber = 2u,
                        columnNumber = 2u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            for (i in 0u ..< 2u) set(MDIndex.of(i, i), complexNumberFieldExtension.one)
                        }
                    ) - 2 * v * v.conjugateTranspose()
                    
                    for (s in m + l - 3u ..< n) {
                        val column = matrixFactory.mapMatrix(
                            rowNumber = 2u,
                            columnNumber = 1u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = h[m + l - 2u, s]
                                this[MDIndex.of(1u, 0u)] = h[m + l - 1u, s]
                            },
                        )
                        val newColumn = w * column
                        h[m + l - 2u, s] = newColumn[0u, 0u]
                        h[m + l - 1u, s] = newColumn[1u, 0u]
                    }
                    
                    for (s in 0u ..< m + l) {
                        val row = matrixFactory.mapMatrix(
                            rowNumber = 1u,
                            columnNumber = 2u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = h[s, m + l - 2u]
                                this[MDIndex.of(0u, 1u)] = h[s, m + l - 1u]
                            },
                        )
                        val newRow = row * w
                        h[s, m + l - 2u] = newRow[0u, 0u]
                        h[s, m + l - 1u] = newRow[0u, 1u]
                    }
                    
                    for (s in 0u ..< n) {
                        val row = matrixFactory.mapMatrix(
                            rowNumber = 1u,
                            columnNumber = 2u,
                            numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                                this[MDIndex.of(0u, 0u)] = q[s, m + l - 2u]
                                this[MDIndex.of(0u, 1u)] = q[s, m + l - 1u]
                            },
                        )
                        val newRow = row * w
                        q[s, m + l - 2u] = newRow[0u, 0u]
                        q[s, m + l - 1u] = newRow[0u, 1u]
                    }
                    
                    
                    for (i in 2u ..< n) h[i, i - 2u] = complexNumberFieldExtension.zero
                    for (i in 3u ..< n) h[i, i - 3u] = complexNumberFieldExtension.zero
                }
            }
        }
        
        val qResult = matrixFactory.generateMatrix(rowNumber = q.rowNumber, columnNumber = q.columnNumber) { row, column -> q[row, column] }
        val hResult = matrixFactory.generateMatrix(rowNumber = h.rowNumber, columnNumber = h.columnNumber) { row, column -> h[row, column] }
        
        return SchurDecomposition(
            leftUnitary = qResult,
            middleUpperTriangular = hResult,
            rightUnitary = conjugateTransposeMatrixComputer { qResult.conjugateTranspose() },
        )
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> SchurDecompositionComputer.Companion.viaGolubVanLoanForComplexNumbers(
    tolerance: Number,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix>,
): SchurDecompositionComputer<ComplexNumber<Number>, Matrix> = SchurDecompositionComputerViaGolubVanLoanForComplexNumbers(
    tolerance = tolerance,
    matrixFactory = matrixFactory,
    numberField = numberField,
    complexNumberFieldExtension = complexNumberFieldExtension,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
    hessenbergDecompositionComputer = hessenbergDecompositionComputer,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> SchurDecompositionComputer.Companion.viaGolubVanLoanForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
): SchurDecompositionComputer<ComplexNumber<Number>, Matrix> {
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
    return viaGolubVanLoanForComplexNumbers(
        tolerance = tolerance,
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        numberField = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        numberOrder = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>(numberType = numberType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
        hessenbergDecompositionComputer = koneContextRegistry.requestFor(HessenbergDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<$numberType, $matrixType>"
        },
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> SchurDecompositionComputer.Companion.setViaGolubVanLoanForComplexNumbers(
    matrixType: SuppliedType,
    tolerance: Number,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, Matrix>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<ComplexNumber<Number>, Matrix>,
) {
    SchurDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGolubVanLoanForComplexNumbers<Number, Matrix>(
            tolerance = tolerance,
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
            hessenbergDecompositionComputer = hessenbergDecompositionComputer,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> SchurDecompositionComputer.Companion.setViaGolubVanLoanForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
) {
    SchurDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGolubVanLoanForComplexNumbers<Number, Matrix>(
            numberType = numberType,
            matrixType = matrixType,
            tolerance = tolerance,
        )
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> SchurDecompositionComputer.Companion.useViaGolubVanLoanForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    numberField: Field<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    conjugateTransposeMatrixComputer: ConjugateTransposeMatrixComputer<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>,
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
    SchurDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val schurDecompositionComputer = viaGolubVanLoanForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(
            tolerance = tolerance,
            matrixFactory = matrixFactory,
            numberField = numberField,
            complexNumberFieldExtension = complexNumberFieldExtension,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            conjugateTransposeMatrixComputer = conjugateTransposeMatrixComputer,
            hessenbergDecompositionComputer = hessenbergDecompositionComputer,
        )
        schurDecompositionComputer { matrix.get().schurDecomposition() }
    }
}

context(_: MutableOwnedRegistry<MatrixWithProperties<ComplexNumber<Number>, Matrix>>, matrix: MatrixWithProperties.Provider<ComplexNumber<Number>, Matrix>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> SchurDecompositionComputer.Companion.useViaGolubVanLoanForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    tolerance: Number,
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
    SchurDecomposition.Key<ComplexNumber<Number>, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(matrixType = matrixWithPropertiesType) correspondsTo RegisteredValueProvider.cached {
        val schurDecompositionComputer = viaGolubVanLoanForComplexNumbers<Number, MatrixWithProperties<ComplexNumber<Number>, Matrix>>(
            numberType = numberType,
            matrixType = matrixWithPropertiesType,
            tolerance = tolerance,
        )
        schurDecompositionComputer { matrix.get().schurDecomposition() }
    }
}