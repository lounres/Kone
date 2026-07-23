/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.MatrixCategoryOverFieldViaDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.MatrixFactoryViaDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskyBanachiewiczSuppliableTopLevelFunctions.setViaCholeskyBanachiewicz
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskyCroutSuppliableTopLevelFunctions.setViaCholeskyCrout
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskySuppliableTopLevelFunctions.setViaCholesky
import dev.lounres.kone.algebraic.algorithms.implementations.MatrixProductComputerViaDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.algorithms.implementations.TransposeMatrixComputerDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.algorithms.implementations.setViaDefaultForDouble
import dev.lounres.kone.algebraic.algorithms.utils.toMatrixString
import dev.lounres.kone.algebraic.assertions.toBeEqualToWithTolerance
import dev.lounres.kone.algebraic.assertions.toBeLowerTriangularMatrix
import dev.lounres.kone.algebraic.assertions.toBeUpperTriangularMatrix
import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.buildWithProvider
import dev.lounres.kone.contexts.koneLocalUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.relations.Order


val CholeskyDecompositionImplementationsTests by testSuite {
    testSuite("real case") {
        typealias Number = Double
        typealias Matrix = MDList2<Number>
        
        val inputs = KoneList.of<MDList2<Number>>(
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                1.0, 0.0,
                0.0, 1.0,
            ),
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                5.0, 3.0,
                3.0, 5.0,
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                32.49, 102.03, 15.4926,
                102.03, 330.2696, 48.6522,
                15.4926, 48.6522, 8.387524,
            ),
        )
        
        data class Algorithm(
            val name: String,
            val koneContextRegistry: KoneContextRegistry,
        )
        
        val algorithms = KoneList.of<Algorithm>(
            Algorithm(
                name = "via Cholesky",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<Number>()
                    MatrixCategoryOverField.setViaDefault<Number, MDList2<Number>>()
                    MatrixProductComputer.setViaDefault<Number, MDList2<Number>>()
                    TransposeMatrixComputer.setViaDefault<Number, MDList2<Number>>()
                    CholeskyDecompositionComputer.setViaCholesky<Number, MDList2<Number>>()
                },
            ),
            Algorithm(
                name = "via Cholesky and Banachiewicz",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<Number>()
                    MatrixCategoryOverField.setViaDefault<Number, MDList2<Number>>()
                    MatrixProductComputer.setViaDefault<Number, MDList2<Number>>()
                    TransposeMatrixComputer.setViaDefault<Number, MDList2<Number>>()
                    CholeskyDecompositionComputer.setViaCholeskyBanachiewicz<Number, MDList2<Number>>()
                },
            ),
            Algorithm(
                name = "via Cholesky and Crout",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<Number>()
                    MatrixCategoryOverField.setViaDefault<Number, MDList2<Number>>()
                    MatrixProductComputer.setViaDefault<Number, MDList2<Number>>()
                    TransposeMatrixComputer.setViaDefault<Number, MDList2<Number>>()
                    CholeskyDecompositionComputer.setViaCholeskyCrout<Number, MDList2<Number>>()
                },
            ),
        )
        
        for ((name, koneContextRegistry) in algorithms) testSuite(name) {
            koneContextRegistry.koneLocalUnwrap(
                Field.Key<Number>(),
                Order.Key<Number>(),
                MatrixProductComputer.Key<Number, Matrix>(),
                TransposeMatrixComputer.Key<Number, Matrix>(),
                CholeskyDecompositionComputer.Key<Number, Matrix>(),
            )
            for ((val index, val input = value) in inputs.withIndex()) test("input #$index") {
                AssertionScope.withClue(
                    {
                        buildString {
                            appendLine("Received the following matrix to decompose.")
                            appendLine()
                            appendLine("Input:")
                            appendLine(input.toMatrixString())
                        }
                    }
                ) {
                    Expect.notToThrow({ input.choleskyDecomposition() }) {
                        (val l = leftLowerTriangular, val r = rightUpperTriangular) = exposeValue()
                        withClue(
                            {
                                buildString {
                                    appendLine("Received the following matrices to check.")
                                    appendLine()
                                    appendLine("L:")
                                    appendLine(l.toMatrixString())
                                    appendLine("R (L^T):")
                                    appendLine(r.toMatrixString())
                                }
                            }
                        ) {
                            softly {
                                withClue("Input differs from LR.") {
                                    Expect.of(l * r).toBeEqualToWithTolerance(input, 1E-10)
                                }
                                withClue("L is not lower-triangular.") {
                                    Expect.of(l).toBeLowerTriangularMatrix()
                                }
                                withClue("R is not upper-triangular.") {
                                    Expect.of(r).toBeUpperTriangularMatrix()
                                }
                                withClue("R is not L^T.") {
                                    Expect.of(r.transpose()).toBeEqualToWithTolerance(l, 1E-10)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}