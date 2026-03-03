package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.MatrixCategoryOverField
import dev.lounres.kone.algebraic.MatrixFactory
import dev.lounres.kone.algebraic.MatrixWithProperties
import dev.lounres.kone.algebraic.abs
import dev.lounres.kone.algebraic.algorithms.HessenbergDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.MatrixProductComputer
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.SchurDecomposition
import dev.lounres.kone.algebraic.algorithms.SchurDecompositionComputer
import dev.lounres.kone.algebraic.algorithms.TransposeMatrixComputer
import dev.lounres.kone.algebraic.algorithms.hessenbergDecomposition
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.positiveSquareRoot
import dev.lounres.kone.algebraic.algorithms.schurDecomposition
import dev.lounres.kone.algebraic.algorithms.times
import dev.lounres.kone.algebraic.algorithms.transpose
import dev.lounres.kone.algebraic.default
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.signInt
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.viaDefault
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.generate
import dev.lounres.kone.multidimensionalCollections.of
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


private class SchurDecompositionComputerViaGolubVanLoan<Number, Matrix : MDList2<Number>>(
    private val tolerance: Number,
    private val matrixFactory: MatrixFactory<Number, Matrix>,
    private val numberField: Field<Number>,
    private val numberOrder: Order<Number>,
    private val positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    private val matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<Number, Matrix>,
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
        
        val q = SettableMDList2.generate(n, n) { row, column -> q0[row, column] }
        val h = SettableMDList2.generate(n, n) { row, column -> h0[row, column] }
        
        context(
            numberField,
            numberOrder,
            positiveSquareRootComputer,
            matrixCategoryOverField,
            matrixProductComputer,
            transposeMatrixComputer,
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
                
                var l = 3u
                while (k + l < n && h[n - k - l, n - k - l - 1u].isNotZero()) l++
                val m = n - k - l
                
                var x: Number
                var y: Number
                var z: Number
                
                scope {
                    val aSum = h[m + l - 2u, m + l - 2u] + h[m + l - 1u, m + l - 1u]
                    val aProduct = h[m + l - 2u, m + l - 2u] * h[m + l - 1u, m + l - 1u] - h[m + l - 2u, m + l - 1u] * h[m + l - 1u, m + l - 2u]
                    x = h[m, m] * h[m, m] + h[m, m + 1u] * h[m + 1u, m] - h[m, m] * aSum + aProduct
                    y = h[m + 1u, m] * (h[m, m] + h[m + 1u, m + 1u] - aSum)
                    z = h[m + 1u, m] * h[m + 2u, m + 1u]
                }
                
                for (t in 0u .. l - 3u) {
                    val norm = (x * x + y * y + z * z).positiveSquareRoot()
                    val u = matrixFactory.mapMatrix(
                        rowNumber = 3u,
                        columnNumber = 1u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            this[MDIndex.of(0u, 0u)] = x + norm * x.signInt().let { if (it == 0) 1 else it }
                            this[MDIndex.of(1u, 0u)] = y
                            this[MDIndex.of(2u, 0u)] = z
                        },
                    )
                    val v = u / (0u ..< 3u).asKoneSequence().sumOf { index -> u[index, 0u].let { it * it } }.positiveSquareRoot()
                    val w = matrixFactory.mapMatrix(
                        rowNumber = 3u,
                        columnNumber = 3u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            for (i in 0u ..< 3u) set(MDIndex.of(i, i), numberField.one)
                        }
                    ) - 2 * v * v.transpose()
                    
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
                
                if (x != 0.0 || y != 0.0) {
                    val norm = (x * x + y * y).positiveSquareRoot()
                    val u = matrixFactory.mapMatrix(
                        rowNumber = 2u,
                        columnNumber = 1u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            this[MDIndex.of(0u, 0u)] = x + norm * x.signInt()
                            this[MDIndex.of(1u, 0u)] = y
                        },
                    )
                    val v = u / (0u ..< 2u).asKoneSequence().sumOf { index -> u[index, 0u].let { it * it } }.positiveSquareRoot()
                    val w = matrixFactory.mapMatrix(
                        rowNumber = 2u,
                        columnNumber = 2u,
                        numbers = KoneMap.build(keyEquality = MDIndex.equality(), keyHashing = MDIndex.hashing()) {
                            for (i in 0u ..< 2u) set(MDIndex.of(i, i), numberField.one)
                        }
                    ) - 2 * v * v.transpose()
                    
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
                    
                    for (i in 2u ..< n) for (j in 0u .. i - 2u) h[i, j] = numberField.zero
                }
            }
        }
        
        val qResult = matrixFactory.generateMatrix(rowNumber = q.rowNumber, columnNumber = q.columnNumber) { row, column -> q[row, column] }
        val hResult = matrixFactory.generateMatrix(rowNumber = h.rowNumber, columnNumber = h.columnNumber) { row, column -> h[row, column] }
        
        return SchurDecomposition(
            leftUnitary = qResult,
            middleUpperTriangular = hResult,
            rightUnitary = transposeMatrixComputer { qResult.transpose() },
        )
    }
}

public fun <Number, Matrix : MDList2<Number>> SchurDecompositionComputer.Companion.viaGolubVanLoan(
    tolerance: Number,
    matrixFactory: MatrixFactory<Number, Matrix>,
    numberField: Field<Number>,
    numberOrder: Order<Number>,
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, Matrix>,
): SchurDecompositionComputer<Number, Matrix> = SchurDecompositionComputerViaGolubVanLoan(
    tolerance = tolerance,
    matrixFactory = matrixFactory,
    numberField = numberField,
    numberOrder = numberOrder,
    positiveSquareRootComputer = positiveSquareRootComputer,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
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
        positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>(numberType = numberType)) {
            "SchurDecompositionComputer.viaGolubVanLoan<$numberType, $matrixType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<Number, Matrix>(matrixType = matrixType)) {
            "SchurDecompositionComputer.viaGolubVanLoan<$numberType, $matrixType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<Number, Matrix>(matrixType = matrixType)) {
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
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
    transposeMatrixComputer: TransposeMatrixComputer<Number, Matrix>,
    hessenbergDecompositionComputer: HessenbergDecompositionComputer<Number, Matrix>,
) {
    SchurDecompositionComputer.Key<Number, Matrix>(matrixType = matrixType) correspondsTo RegisteredValueProvider.cached {
        viaGolubVanLoan<Number, Matrix>(
            tolerance = tolerance,
            matrixFactory = matrixFactory,
            numberField = numberField,
            numberOrder = numberOrder,
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
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
    positiveSquareRootComputer: PositiveSquareRootComputer<Number>,
    matrixCategoryOverField: MatrixCategoryOverField<Number, MatrixWithProperties<Number, Matrix>>,
    matrixProductComputer: MatrixProductComputer<Number, MatrixWithProperties<Number, Matrix>>,
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
            positiveSquareRootComputer = positiveSquareRootComputer,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
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