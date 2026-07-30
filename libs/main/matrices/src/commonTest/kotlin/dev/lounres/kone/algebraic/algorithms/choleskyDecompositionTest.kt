/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.ComplexNumberEqualitySuppliableTopLevelFunctions.setEquality
import dev.lounres.kone.algebraic.ComplexNumberFieldExtensionOverSuppliableTopLevelFunctions.setFieldExtensionOver
import dev.lounres.kone.algebraic.MatrixCategoryOverFieldViaDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.MatrixFactoryViaDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskyBanachiewiczForComplexNumbersSuppliableTopLevelFunctions.setViaCholeskyBanachiewiczForComplexNumbers
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskyBanachiewiczSuppliableTopLevelFunctions.setViaCholeskyBanachiewicz
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskyCroutForComplexNumbersSuppliableTopLevelFunctions.setViaCholeskyCroutForComplexNumbers
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskyCroutSuppliableTopLevelFunctions.setViaCholeskyCrout
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskyForComplexNumbersSuppliableTopLevelFunctions.setViaCholeskyForComplexNumbers
import dev.lounres.kone.algebraic.algorithms.implementations.CholeskyDecompositionComputerCholeskySuppliableTopLevelFunctions.setViaCholesky
import dev.lounres.kone.algebraic.algorithms.implementations.ConjugateTransposeMatrixComputerDefaultSuppliableTopLevelFunctions.setViaDefault
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
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Order


val CholeskyDecompositionImplementationsTests by testSuite {
    testSuite("real case") {
        typealias Number = Double
        typealias Matrix = MDList2<Number>
        
        val inputs = KoneList.of<Matrix>(
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
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<Number>()
                    MatrixCategoryOverField.setViaDefault<Number, Matrix>()
                    MatrixProductComputer.setViaDefault<Number, Matrix>()
                    TransposeMatrixComputer.setViaDefault<Number, Matrix>()
                    CholeskyDecompositionComputer.setViaCholesky<Number, Matrix>()
                },
            ),
            Algorithm(
                name = "via Cholesky and Banachiewicz",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<Number>()
                    MatrixCategoryOverField.setViaDefault<Number, Matrix>()
                    MatrixProductComputer.setViaDefault<Number, Matrix>()
                    TransposeMatrixComputer.setViaDefault<Number, Matrix>()
                    CholeskyDecompositionComputer.setViaCholeskyBanachiewicz<Number, Matrix>()
                },
            ),
            Algorithm(
                name = "via Cholesky and Crout",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<Number>()
                    MatrixCategoryOverField.setViaDefault<Number, Matrix>()
                    MatrixProductComputer.setViaDefault<Number, Matrix>()
                    TransposeMatrixComputer.setViaDefault<Number, Matrix>()
                    CholeskyDecompositionComputer.setViaCholeskyCrout<Number, Matrix>()
                },
            ),
        )
        
        for ((name, koneContextRegistry) in algorithms) testSuite(name) {
            koneContextRegistry.koneLocalUnwrap(
                Field.Key<Number>(),
                Equality.Key<Number>(),
                Order.Key<Number>(),
                MatrixProductComputer.Key<Number, Matrix>(),
                TransposeMatrixComputer.Key<Number, Matrix>(),
                CholeskyDecompositionComputer.Key<Number, Matrix>(),
            )
            for ((index, input = value) in inputs.withIndex()) test("input #$index") {
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
                        val (l = leftLowerTriangular, r = rightUpperTriangular) = exposeValue()
                        withClue(
                            {
                                buildString {
                                    appendLine("Received the following matrices to check.")
                                    appendLine()
                                    appendLine("L:")
                                    appendLine(l.toMatrixString())
                                    appendLine("R (L^*):")
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
                                withClue("R is not L^*.") {
                                    Expect.of(r.transpose()).toBeEqualToWithTolerance(l, 1E-10)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    testSuite("imaginary case") {
        typealias Number = Double
        typealias CNumber = ComplexNumber<Number>
        typealias Matrix = MDList2<CNumber>
        
        val inputs = KoneList.of<Matrix>(
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
                ComplexNumber(0.0, 0.0), ComplexNumber(1.0, 0.0),
            ),
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                ComplexNumber(5.0, 0.0), ComplexNumber(3.0, 0.0),
                ComplexNumber(3.0, 0.0), ComplexNumber(5.0, 0.0),
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                ComplexNumber(32.49, 0.0), ComplexNumber(102.03, 0.0), ComplexNumber(15.4926, 0.0),
                ComplexNumber(102.03, 0.0), ComplexNumber(330.2696, 0.0), ComplexNumber(48.6522, 0.0),
                ComplexNumber(15.4926, 0.0), ComplexNumber(48.6522, 0.0), ComplexNumber(8.387524, 0.0),
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                ComplexNumber(32.49, 0.0), ComplexNumber(57.0, -45.03), ComplexNumber(11.457, -4.0356),
                ComplexNumber(57.0, 45.03), ComplexNumber(172.2696, 0.0), ComplexNumber(25.6932, 8.799),
                ComplexNumber(11.457, 4.0356), ComplexNumber(25.6932, -8.799), ComplexNumber(5.541364, 0.0),
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
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    ComplexNumber.setFieldExtensionOver<Number>()
                    ComplexNumber.setEquality<Number>()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<CNumber>()
                    MatrixCategoryOverField.setViaDefault<CNumber, Matrix>()
                    MatrixProductComputer.setViaDefault<CNumber, Matrix>()
                    ConjugateTransposeMatrixComputer.setViaDefault<Number, Matrix>()
                    CholeskyDecompositionComputer.setViaCholeskyForComplexNumbers<Number, Matrix>()
                },
            ),
            Algorithm(
                name = "via Cholesky and Banachiewicz",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    ComplexNumber.setFieldExtensionOver<Number>()
                    ComplexNumber.setEquality<Number>()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<CNumber>()
                    MatrixCategoryOverField.setViaDefault<CNumber, Matrix>()
                    MatrixProductComputer.setViaDefault<CNumber, Matrix>()
                    ConjugateTransposeMatrixComputer.setViaDefault<Number, Matrix>()
                    CholeskyDecompositionComputer.setViaCholeskyBanachiewiczForComplexNumbers<Number, Matrix>()
                },
            ),
            Algorithm(
                name = "via Cholesky and Crout",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    ComplexNumber.setFieldExtensionOver<Number>()
                    ComplexNumber.setEquality<Number>()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<CNumber>()
                    MatrixCategoryOverField.setViaDefault<CNumber, Matrix>()
                    MatrixProductComputer.setViaDefault<CNumber, Matrix>()
                    ConjugateTransposeMatrixComputer.setViaDefault<Number, Matrix>()
                    CholeskyDecompositionComputer.setViaCholeskyCroutForComplexNumbers<Number, Matrix>()
                },
            ),
        )
        
        for ((name, koneContextRegistry) in algorithms) testSuite(name) {
            koneContextRegistry.koneLocalUnwrap(
                Field.Key<Number>(),
                Order.Key<Number>(),
                Equality.Key<CNumber>(),
                FieldExtension.Key<Number, CNumber>(),
                PositiveSquareRootComputer.Key<Number>(),
                MatrixProductComputer.Key<CNumber, Matrix>(),
                ConjugateTransposeMatrixComputer.Key<Number, Matrix>(),
                CholeskyDecompositionComputer.Key<CNumber, Matrix>(),
            )
            for ((index, input = value) in inputs.withIndex()) test("input #$index") {
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
                        val (l = leftLowerTriangular, r = rightUpperTriangular) = exposeValue()
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
                                withClue("R is not L^*.") {
                                    Expect.of(r.conjugateTranspose()).toBeEqualToWithTolerance(l, 1E-10)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}