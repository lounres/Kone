/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.collections.array.*
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.iterator.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.collections.list.induce
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.relations.equality
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.multidimensionalCollections.utils.forEachIndexed
import dev.lounres.kone.multidimensionalCollections.utils.sumOf
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.leq
import dev.lounres.kone.scope
import kotlin.jvm.JvmInline


// TODO: Clean up and move the API to public state
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class ParlettRecurrenceInternalApi

@ParlettRecurrenceInternalApi
public fun interface ParlettRecurrenceAtomicBlockImageComputer<Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.atomicBlockImage(): Matrix
}

@ParlettRecurrenceInternalApi
context(parlettRecurrenceAtomicBlockImageComputer: ParlettRecurrenceAtomicBlockImageComputer<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.atomicBlockImage(): Matrix =
    with(parlettRecurrenceAtomicBlockImageComputer) { this@atomicBlockImage.atomicBlockImage() }

@ParlettRecurrenceInternalApi
public class ParlettRecurrenceAtomicBlockImageComputerViaTaylorSeriesForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val complexNumberField: Field<ComplexNumber<Number>>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val atomicBlockImageComputationTolerance: Number,
    private val function: ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>,
) : ParlettRecurrenceAtomicBlockImageComputer<ComplexNumber<Number>, Matrix> {
    private val atomicBlockImageComputationToleranceSquared by lazy {
        field.numberTimesNumber {
            atomicBlockImageComputationTolerance * atomicBlockImageComputationTolerance
        }
    }
    
    override fun Matrix.atomicBlockImage(): Matrix {
        require(this.rowNumber == this.columnNumber) { TODO() }
        val size = this.rowNumber
        
        KoneContext.localUnwrap(
            field,
            order,
            complexNumberField,
            matrixCategoryOverField,
            matrixProductComputer,
        )
        
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
                        .fold(complexNumberField.one) { accumulator, j -> accumulator + y[j] * this[size - 1u - i, size - 1u - j] }
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
        
        return f
    }
}

@ParlettRecurrenceInternalApi
public class ParlettRecurrence<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val complexNumberField: Field<ComplexNumber<Number>>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val inverseMatrixComputer: InverseMatrixComputer<ComplexNumber<Number>, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
    private val parlettRecurrenceAtomicBlockImageComputer: ParlettRecurrenceAtomicBlockImageComputer<ComplexNumber<Number>, Matrix>,
    private val blockingParameter: Number,
) : KoneContext {
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
            
            KoneContext.localUnwrap(matrixCategoryOverField)
            
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
        public operator fun get(row: UInt, column: UInt): Matrix {
            require(column >= row) { TODO() }
            return blocks[blocksOnDiagonalNumber * row - row * (row - 1u) / 2u + (column - row)]
        }
        public operator fun set(row: UInt, column: UInt, value: Matrix) {
            require(column >= row) { TODO() }
            blocks[blocksOnDiagonalNumber * row - row * (row - 1u) / 2u + (column - row)] = value
        }
    }
    @JvmInline
    private value class BlockPattern(val blockIndices: KoneUIntArray)
    
    private val blockingParameterSquared by lazy {
        field.numberTimesNumber {
            blockingParameter * blockingParameter
        }
    }
    
    private fun KoneList<ComplexNumber<Number>>.blockPattern(): BlockPattern {
        KoneContext.localUnwrap(
            field,
            order,
            complexNumberField,
        )
    
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
        return BlockPattern(blockIndices.asKoneUIntArray())
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
        for ((val index, val blockIndex = value) in blockPattern.blockIndices.withIndex()) {
            result[index] = blockNextIndices[blockIndex].also { blockNextIndices[blockIndex] = it + 1u }
        }
        return Permutation(result.asKoneUIntArray())
    }
    
    private fun Permutation.reverse(): Permutation {
        val reverse = KoneMutableUIntArray.fill(this.size)
        for (i in 0u ..< this.size) reverse[this[i]] = i
        return Permutation(reverse.asKoneUIntArray())
    }
    
    private fun Blocked<Matrix>.image(totalSize: UInt): Matrix =
        context(
            matrixCategoryOverField,
            matrixProductComputer,
            parlettRecurrenceAtomicBlockImageComputer,
        ) {
            KoneContext.localUnwrap(matrixCategoryOverField)
            
            val input = this
            val blocksOnDiagonalNumber = input.blocksOnDiagonalNumber
            val result = Blocked<Matrix?>(
                blocksOnDiagonalNumber = blocksOnDiagonalNumber,
                blocks = KoneArraySettableList.generate(blocksOnDiagonalNumber * (blocksOnDiagonalNumber + 1u) / 2u) { null }
            )
            
            for (i in 0u ..< blocksOnDiagonalNumber)
                result[i, i] = input[i, i].atomicBlockImage()
            
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
            
            val resultMatrixElements = SettableMDList2.generate<ComplexNumber<Number>?>(rowNumber = totalSize, columnNumber = totalSize) { _, _ -> null }
            scope {
                var outerStartRow = 0u
                var outerStartColumn = 0u
                for (block in result.blocks) {
                    block!!
                    block.forEachIndexed { [val row, val column], value ->
                        resultMatrixElements[row + outerStartRow, column + outerStartColumn] = value
                    }
                    outerStartColumn += block.columnNumber
                    if (outerStartColumn == totalSize) {
                        outerStartRow += block.rowNumber
                        outerStartColumn = outerStartRow
                    }
                }
            }
            
            matrixFactory.generateMatrix(rowNumber = totalSize, columnNumber = totalSize) { row, column ->
                if (row > column) complexNumberField.zero else resultMatrixElements[row, column]!!
            }
        }
    
    public fun Matrix.image(): Matrix {
        val n = this.rowNumber
        
        if (isDiagonalMatrixChecker { this.isDiagonal() }) {
            return matrixFactory.mapMatrix(
                rowNumber = n,
                columnNumber = n,
                numbers = KoneMap.build(
                    keyEquality = MDIndex.equality(),
                    keyHashing = MDIndex.hashing(),
                ) {
                    for (i in 0u ..< n)
                        set(
                            MDIndex.of(i, i),
                            parlettRecurrenceAtomicBlockImageComputer {
                                matrixFactory.generateMatrix(1u, 1u) { _, _ ->  this@image[i, i] }.atomicBlockImage()[0u, 0u]
                            }
                        )
                }
            )
        }
        
        val blockPattern = KoneList.generate(n) { this[it, it] }.blockPattern()
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
                        this[blockReversePermutation[row + outerRow], blockReversePermutation[column + outerColumn]]
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
        
        val blockedMatrixImage = blocked.image(totalSize = n)
        return matrixFactory.generateMatrix(
            columnNumber = n,
            rowNumber = n,
        ) { row, column -> blockedMatrixImage[blockPermutation[row], blockPermutation[column]] }
    }
}

@ParlettRecurrenceInternalApi
context(parlettRecurrence: ParlettRecurrence<Number, Matrix>)
public fun <Number, Matrix : MDList2<ComplexNumber<Number>>> Matrix.image(): Matrix =
    with(parlettRecurrence) { this@image.image() }