/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.*
import dev.lounres.kone.collections.interop.asKoneSequence
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.induce
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.collections.utils.maxOf
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.multidimensionalCollections.utils.sumOf
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.leq
import dev.lounres.kone.scope


private class ScalarBasedMatrixFunctionApplierViaSchurParlettForComplexNumbers<Number, Matrix : MDList2<ComplexNumber<Number>>>(
    private val atomicBlockImageComputationTolerance: Number,
    private val matrixFactory: MatrixFactory<ComplexNumber<Number>, Matrix>,
    private val field: Field<Number>,
    private val order: Order<Number>,
    private val complexNumberFieldExtension: FieldExtension<Number, ComplexNumber<Number>>,
    private val matrixCategoryOverField: MatrixCategoryOverField<ComplexNumber<Number>, Matrix>,
    private val matrixProductComputer: MatrixProductComputer<ComplexNumber<Number>, Matrix>,
    private val schurDecompositionComputer: SchurDecompositionComputer<ComplexNumber<Number>, Matrix>,
    private val isDiagonalMatrixChecker: IsDiagonalMatrixChecker<ComplexNumber<Number>, Matrix>,
) : ScalarBasedMatrixFunctionApplier<ComplexNumber<Number>, Matrix, ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> {
    private fun Matrix.atomicBlockImage(size: UInt, function: ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>): Matrix =
        context(
            field,
            order,
            complexNumberFieldExtension,
            matrixCategoryOverField,
            matrixProductComputer,
        ) {
            val toleranceSquared = atomicBlockImageComputationTolerance * atomicBlockImageComputationTolerance
            val spectre = KoneList.generate(size) { this[it, it] }
            val sigma = (0u ..< size).asKoneSequence().sumOf<_, ComplexNumber<Number>> { this[it, it] } / size
            val m = this - matrixFactory.mapMatrix(
                columnNumber = size,
                rowNumber = size,
                numbers = KoneMap.build {
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
                    numbers = KoneMap.build {
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
                if (fNextSummandNormSquared leq toleranceSquared * fNormSquared) {
                    val factorials = KoneList.induce(size, field.one) { index, previous -> previous * index }
                    val delta = (0u ..< size).asKoneSequence().maxOf<_, Number> { function.bound(it + s, spectre) / factorials[it] }
                    val pNormSquared = p.sumOf<_, Number> { it.norm().let { it * it } }
                    if (muSquared * delta * delta * pNormSquared leq toleranceSquared * fNormSquared) break
                }
            }
            
            f
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
                numbers = KoneMap.build {
                    for (i in 0u ..< n) set(MDIndex.of(i, i), function.evaluate(0u, t[i, i]))
                }
            )
            return matrixProductComputer { schurDecomposition.leftUnitary * g * schurDecomposition.rightUnitary }
        }
        TODO()
    }
}

//public fun <Number, Matrix : MDList2<Number>> ScalarBasedMatrixFunctionApplier.Companion.viaSchurParlettForComplexNumbers(
//    atomicBlockImageComputationTolerance: Number,
//    scalarBaseForMatrixFunction: ScalarBaseForMatrixFunction<Number>,
//    matrixFactory: MatrixFactory<Number, Matrix>,
//    field: Field<Number>,
//    matrixCategoryOverField: MatrixCategoryOverField<Number, Matrix>,
//    matrixProductComputer: MatrixProductComputer<Number, Matrix>,
//    schurDecompositionComputer: SchurDecompositionComputer<Number, Matrix>,
//    isDiagonalMatrixChecker: IsDiagonalMatrixChecker<Number, Matrix>,
//): ScalarBasedMatrixFunctionApplier<Number, Matrix> = ScalarBasedMatrixFunctionApplierViaSchurParlettForComplexNumbers(
//    atomicBlockImageComputationTolerance = atomicBlockImageComputationTolerance,
//    scalarBaseForMatrixFunction = scalarBaseForMatrixFunction,
//    matrixFactory = matrixFactory,
//    field = field,
//    matrixCategoryOverField = matrixCategoryOverField,
//    matrixProductComputer = matrixProductComputer,
//    schurDecompositionComputer = schurDecompositionComputer,
//    isDiagonalMatrixChecker = isDiagonalMatrixChecker,
//)