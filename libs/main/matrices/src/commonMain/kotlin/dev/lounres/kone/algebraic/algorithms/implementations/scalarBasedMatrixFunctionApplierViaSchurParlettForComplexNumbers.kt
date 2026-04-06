/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.collections.array.*
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.collections.list.induce
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.multidimensionalCollections.utils.forEachIndexed
import dev.lounres.kone.multidimensionalCollections.utils.sumOf
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
import kotlin.jvm.JvmInline
import kotlin.reflect.KVariance.OUT


private class ScalarBasedMatrixFunctionApplierViaSchurParlettForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    private val atomicBlockImageComputationTolerance: Number,
    private val blockingParameter: Number,
) : ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> {
    private val atomicBlockImageComputationToleranceSquared by lazy {
        field {
            atomicBlockImageComputationTolerance * atomicBlockImageComputationTolerance
        }
    }
    
    private fun Matrix.atomicBlockImage(function: ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>): Matrix =
        context(
            field,
            order,
            complexNumberFieldExtension,
            matrixCategoryOverField,
            matrixProductComputer,
        ) {
            require(this.rowNumber == this.columnNumber) { TODO() }
            val size = this.rowNumber
            val spectre = KoneList.generate(size) { this[it, it] }
            val sigma = spectre.sum() / size
            val m = this - matrixFactory.mapMatrix(
                columnNumber = size,
                rowNumber = size,
                numbers = KoneMap.build(
                    keyEquality = MDIndex.equality(),
                    keyHashing = MDIndex.hashing(),
                ) {
                    for (i in 0u ..< size) set(MDIndex.of(i, i), sigma)
                }
            )
            val mu: Number = scope {
                val y = KoneArrayFixedCapacityList<ComplexNumber<Number>>(size)
                for (i in 0u ..< size) {
                    y.add(
                        (0u ..< i)
                            .asKoneSequence()
                            .fold(complexNumberFieldExtension.one) { accumulator, j -> accumulator + y[j] * this[size - 1u - i, size - 1u - j] }
                    )
                }
                y.maxOf<_, Number> { it.norm() }
            }
            val muSquared = mu * mu
            
            var s = 0u
            var f = function.evaluate(0u, sigma).let {
                matrixFactory.mapMatrix(
                    columnNumber = size,
                    rowNumber = size,
                    numbers = KoneMap.build(
                        keyEquality = MDIndex.equality(),
                        keyHashing = MDIndex.hashing(),
                    ) {
                        for (i in 0u ..< size) set(MDIndex.of(i, i), it)
                    }
                )
            }
            var p = m
            while (true) {
                s++
                val fNextSummand = function.evaluate(s, sigma) * p
                f += fNextSummand
                p *= m / (s + 1u)
                
                val fNormSquared = f.sumOf<_, Number> { it.norm().let { it * it } }
                val fNextSummandNormSquared = fNextSummand.sumOf<_, Number> { it.norm().let { it * it } }
                if (fNextSummandNormSquared leq atomicBlockImageComputationToleranceSquared * fNormSquared) {
                    val factorials = KoneList.induce(size, field.one) { index, previous -> previous * index }
                    val delta = (0u ..< size).asKoneSequence().maxOf<_, Number> { function.bound(it + s, spectre) / factorials[it] }
                    val pNormSquared = p.sumOf<_, Number> { it.norm().let { it * it } }
                    if (muSquared * delta * delta * pNormSquared leq atomicBlockImageComputationToleranceSquared * fNormSquared) break
                }
            }
            
            f
        }
    
    // Left * X + X * Right = Result
    // Left and Right are square and upper-triangular!
    private fun solveSylvesterEquation(leftSylvester: Matrix, rightSylvester: Matrix, resultSylvester: Matrix): Matrix =
        context(
            matrixCategoryOverField,
            matrixProductComputer,
            inverseMatrixComputer,
        ) {
            require(leftSylvester.rowNumber == leftSylvester.columnNumber) { TODO() }
            require(rightSylvester.rowNumber == rightSylvester.columnNumber) { TODO() }
            val leftSize = leftSylvester.rowNumber
            val rightSize = rightSylvester.rowNumber
            
            val solution = SettableMDList2.generate<ComplexNumber<Number>?>(
                rowNumber = leftSize,
                columnNumber = rightSize,
            ) { _, _ -> null }
            
            for (currentColumn in 0u ..< rightSize) {
                val left = leftSylvester + matrixFactory.mapMatrix(
                    rowNumber = leftSize,
                    columnNumber = leftSize,
                    numbers = KoneMap.build(
                        keyEquality = MDIndex.equality(),
                        keyHashing = MDIndex.hashing(),
                    ) {
                        for (i in 0u ..< leftSize) set(MDIndex.of(i, i), rightSylvester[currentColumn, currentColumn])
                    }
                )
                val right = matrixFactory.generateMatrix(
                    rowNumber = leftSize,
                    columnNumber = 1u,
                ) { row, _ -> resultSylvester[row, currentColumn] } - matrixFactory.generateMatrix(
                    rowNumber = leftSize,
                    columnNumber = currentColumn,
                ) { row, column -> solution[row, column]!! } * matrixFactory.generateMatrix(
                    rowNumber = currentColumn,
                    columnNumber = 1u,
                ) { row, _ -> rightSylvester[row, currentColumn] }
                val columnSolution = left.invert()!! * right
                for (i in 0u ..< leftSize) solution[i, currentColumn] = columnSolution[i, 0u]
            }
            
            matrixFactory.generateMatrix(
                columnNumber = solution.columnNumber,
                rowNumber = solution.rowNumber,
            ) { row, column -> solution[row, column]!! }
        }
    
    private data class Blocked<Matrix>(
        val blocksOnDiagonalNumber: UInt,
        val blocks: KoneArraySettableList<Matrix>,
    ) {
        operator fun get(row: UInt, column: UInt): Matrix {
            require(column >= row) { TODO() }
            return blocks[blocksOnDiagonalNumber * row - row * (row - 1u) / 2u + (column - row)]
        }
        operator fun set(row: UInt, column: UInt, value: Matrix) {
            require(column >= row) { TODO() }
            blocks[blocksOnDiagonalNumber * row - row * (row - 1u) / 2u + (column - row)] = value
        }
    }
    
    private fun Blocked<Matrix>.image(n: UInt, function: ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>): Matrix =
        context(
            matrixCategoryOverField,
            matrixProductComputer,
        ) {
            val input = this
            val blocksOnDiagonalNumber = input.blocksOnDiagonalNumber
            val result = Blocked<Matrix?>(
                blocksOnDiagonalNumber = blocksOnDiagonalNumber,
                blocks = KoneArraySettableList.generate(blocksOnDiagonalNumber * (blocksOnDiagonalNumber + 1u) / 2u) { null }
            )
            
            for (i in 0u ..< blocksOnDiagonalNumber)
                result[i, i] = input[i, i].atomicBlockImage(function = function)
            
            for (d in 1u ..< blocksOnDiagonalNumber) for (i in 0u ..< blocksOnDiagonalNumber - d) {
                val j = i + d
                result[i, j] = solveSylvesterEquation(
                    leftSylvester = input[i, i],
                    rightSylvester = -input[j, j],
                    resultSylvester = (i + 1u ..< j).fold((result[i, i]!! * input[i, j] - input[i, j] * result[j, j]!!)) { acc, k ->
                        acc + (result[i, k]!! * input[k, j] - input[i, k] * result[k, j]!!)
                    },
                )
            }
            
            val resultMatrixElements = SettableMDList2.generate<ComplexNumber<Number>?>(rowNumber = n, columnNumber = n) { _, _ -> null }
            scope {
                var outerStartRow = 0u
                var outerStartColumn = 0u
                for (block in result.blocks) {
                    block!!
                    block.forEachIndexed { (row, column), value ->
                        resultMatrixElements[row + outerStartRow, column + outerStartColumn] = value
                    }
                    outerStartColumn += block.columnNumber
                    if (outerStartColumn == n) {
                        outerStartRow += block.rowNumber
                        outerStartColumn = outerStartRow
                    }
                }
            }
            
            matrixFactory.generateMatrix(rowNumber = n, columnNumber = n) { row, column ->
                if (row > column) complexNumberFieldExtension.zero else resultMatrixElements[row, column]!!
            }
        }
    
    @JvmInline
    private value class BlockPattern(val blockIndices: KoneUIntArray)
    
    private val blockingParameterSquared by lazy {
        field {
            blockingParameter * blockingParameter
        }
    }
    
    private fun KoneList<ComplexNumber<Number>>.blockPattern(): BlockPattern =
        context(
            field,
            order,
            complexNumberFieldExtension,
        ) {
            val blockIndices = KoneMutableUIntArray.fill(this.size, UInt.MAX_VALUE)
            var setsNumber = 0u
            for (i in 0u ..< this.size) {
                if (blockIndices[i] == UInt.MAX_VALUE) {
                    blockIndices[i] = setsNumber.also { setsNumber++ }
                }
                for (j in i + 1u ..< this.size) {
                    if (blockIndices[j] != blockIndices[i] && (this[j] - this[i]).norm() leq blockingParameterSquared) {
                        if (blockIndices[j] == UInt.MAX_VALUE) {
                            blockIndices[j] = blockIndices[i]
                        } else {
                            val maxIndex = maxOf(blockIndices[i], blockIndices[j])
                            val minIndex = minOf(blockIndices[i], blockIndices[j])
                            for (t in 0u ..< this.size)
                                when {
                                    blockIndices[t] == UInt.MAX_VALUE -> {}
                                    blockIndices[t] > maxIndex -> blockIndices[t]--
                                    blockIndices[t] == maxIndex -> blockIndices[t] = minIndex
                                }
                            setsNumber--
                        }
                    }
                }
            }
            BlockPattern(blockIndices.asKoneUIntArray())
        }
    
    private fun BlockPattern.blockSizes(): KoneUIntArray {
        val result = KoneMutableUIntArray.fill(this.blockIndices.size)
        for (blockIndex in this.blockIndices) {
            result[blockIndex]++
        }
        val actualSize = (UInt.equality()) { result.firstIndexOf(0u) }
        return KoneUIntArray.generate(actualSize) { result[it] }
    }
    
    @JvmInline
    private value class Permutation(private val newIndex: KoneUIntArray) {
        val size: UInt get() = newIndex.size
        operator fun get(index: UInt): UInt = newIndex[index]
    }
    
    private fun permutationBy(blockPattern: BlockPattern, blockSizes: KoneUIntArray): Permutation {
        val blockNextIndices = KoneMutableUIntArray.induce(blockSizes.size, 0u) { index, previous -> blockSizes[index - 1u] + previous }
        val result = KoneMutableUIntArray.fill(blockPattern.blockIndices.size)
        for ((index, blockIndex) in blockPattern.blockIndices.withIndex()) {
            result[index] = blockNextIndices[blockIndex].also { blockNextIndices[blockIndex] = it + 1u }
        }
        return Permutation(result.asKoneUIntArray())
    }
    
    private fun Permutation.reverse(): Permutation {
        val reverse = KoneMutableUIntArray.fill(this.size)
        for (i in 0u ..< this.size) reverse[this[i]] = i
        return Permutation(reverse.asKoneUIntArray())
    }
    
    override fun Matrix.after(function: ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>): Matrix {
        require(this.rowNumber == this.columnNumber)
        val n = this.rowNumber
        
        val schurDecomposition = schurDecompositionComputer { this.schurDecomposition() }
        val t = schurDecomposition.middleUpperTriangular
        
        if (isDiagonalMatrixChecker { t.isDiagonal() }) {
            val g = matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(
                    keyEquality = MDIndex.equality(),
                    keyHashing = MDIndex.hashing(),
                ) {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), function.evaluate(0u, t[i, i]))
                }
            )
            return matrixProductComputer { schurDecomposition.leftUnitary * g * schurDecomposition.rightUnitary }
        }
        
        val blockPattern = KoneList.generate(n) { t[it, it] }.blockPattern()
        val blockSizes = blockPattern.blockSizes()
        val blocksOnDiagonalNumber = blockSizes.size
        val blockPermutation = permutationBy(blockPattern = blockPattern, blockSizes = blockSizes)
        val blockReversePermutation = blockPermutation.reverse()
        
        val blocked: Blocked<Matrix> = Blocked(
            blocksOnDiagonalNumber = blocksOnDiagonalNumber,
            blocks = scope {
                var blockRow = 0u
                var blockColumn = 0u
                var outerRow = 0u
                var outerColumn = 0u
                KoneArraySettableList.generate<Matrix>(blocksOnDiagonalNumber * (blocksOnDiagonalNumber + 1u) / 2u) {
                    matrixFactory.generateMatrix(
                        rowNumber = blockSizes[blockRow],
                        columnNumber = blockSizes[blockColumn],
                    ) { row, column ->
                        t[blockReversePermutation[row + outerRow], blockReversePermutation[column + outerColumn]]
                    }.also {
                        outerColumn += blockSizes[blockColumn]
                        blockColumn++
                        if (blockColumn == blocksOnDiagonalNumber) {
                            outerRow += blockSizes[blockRow]
                            blockRow++
                            outerColumn = outerRow
                            blockColumn = blockRow
                        }
                    }
                }
            }
        )
        
        val blockImage = blocked.image(n = n, function = function)
        val permutatedBackImage = matrixFactory.generateMatrix(
            columnNumber = n,
            rowNumber = n,
        ) { row, column -> blockImage[blockPermutation[row], blockPermutation[column]] }
        return matrixProductComputer { schurDecomposition.leftUnitary * permutatedBackImage * schurDecomposition.rightUnitary }
    }
}

public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.viaSchurParlettForComplexNumbers(
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
): ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, Function> = ScalarBasedMatrixFunctionApplierViaSchurParlettForComplexNumbers(
    matrixFactory = matrixFactory,
    field = field,
    order = order,
    complexNumberFieldExtension = complexNumberFieldExtension,
    matrixCategoryOverField = matrixCategoryOverField,
    matrixProductComputer = matrixProductComputer,
    inverseMatrixComputer = inverseMatrixComputer,
    schurDecompositionComputer = schurDecompositionComputer,
    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
    atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
    blockingParameter = blockingParameter,
)

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.viaSchurParlettForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    functionType: SuppliedType,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
): ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, Function> {
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
    return viaSchurParlettForComplexNumbers(
        matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        field = koneContextRegistry.requestFor(Field.Key<Number>(numberType = numberType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        order = koneContextRegistry.requestFor(Order.Key<Number>(elementType = numberType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        inverseMatrixComputer = koneContextRegistry.requestFor(InverseMatrixComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<ComplexNumber<Number>, Matrix>(matrixType = matrixType)) {
            "ScalarBasedMatrixFunctionApplier.viaSchurParlettForComplexNumbers<$numberType, $matrixType, $functionType>"
        },
        atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
        blockingParameter = blockingParameter,
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.setViaSchurParlettForComplexNumbers(
    matrixType: SuppliedType,
    functionType: SuppliedType,
    matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    field: Field<Number>,
    order: Order<Number>,
    complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ScalarBasedMatrixFunctionApplier.Key<ComplexNumber<Number>, Matrix, Function>(matrixType = matrixType, functionType = functionType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettForComplexNumbers<Number, Matrix, Function>(
            matrixFactory = matrixFactory,
            field = field,
            order = order,
            complexNumberFieldExtension = complexNumberFieldExtension,
            matrixCategoryOverField = matrixCategoryOverField,
            matrixProductComputer = matrixProductComputer,
            inverseMatrixComputer = inverseMatrixComputer,
            schurDecompositionComputer = schurDecompositionComputer,
            isDiagonalMatrixChecker = isDiagonalMatrixChecker,
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
    }
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, _: KoneContextRegistry.Provider)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>, Function : ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> ScalarBasedMatrixFunctionApplier.Companion.setViaSchurParlettForComplexNumbers(
    numberType: SuppliedType,
    matrixType: SuppliedType,
    functionType: SuppliedType,
    atomicBlockImageComputationTolerance: Number,
    blockingParameter: Number,
) {
    ScalarBasedMatrixFunctionApplier.Key<ComplexNumber<Number>, Matrix, Function>(matrixType = matrixType, functionType = functionType) correspondsTo RegisteredValueProvider.cached {
        viaSchurParlettForComplexNumbers<Number, Matrix, Function>(
            numberType = numberType,
            matrixType = matrixType,
            functionType = functionType,
            atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
            blockingParameter = blockingParameter,
        )
    }
}