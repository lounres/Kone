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
import dev.lounres.kone.algebraic.algorithms.implementations.*
import dev.lounres.kone.algebraic.algorithms.implementations.ConjugateTransposeMatrixComputerDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.algorithms.implementations.HessenbergDecompositionComputerHouseholderForComplexNumbersSuppliableTopLevelFunctions.setViaHouseholderForComplexNumbers
import dev.lounres.kone.algebraic.algorithms.implementations.HessenbergDecompositionComputerouseholderSuppliableTopLevelFunctions.setViaHouseholder
import dev.lounres.kone.algebraic.algorithms.implementations.InverseMatrixComputerGaussianEliminationSuppliableTopLevelFunctions.setViaGaussianElimination
import dev.lounres.kone.algebraic.algorithms.implementations.MatrixProductComputerViaDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.algorithms.implementations.TransposeMatrixComputerDefaultSuppliableTopLevelFunctions.setViaDefault
import dev.lounres.kone.algebraic.algorithms.utils.toMatrixString
import dev.lounres.kone.algebraic.assertions.toBeEqualToWithTolerance
import dev.lounres.kone.algebraic.assertions.toBeUpperHessenbergMatrix
import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.iterator.next
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


val HessenbergDecompositionImplementationsTests by testSuite {
    testSuite("real case") {
        typealias Number = Double
        
        val inputs = KoneList.of<MDList2<Number>>(
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                0.0, 1.0,
                1.0, 0.0,
            ),
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                -0.3887440162876692 , 0.37522780472518924,
                -0.18232685650741207, -0.2287135425824249,
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                0.685478, 0.495653, 0.479176,
                0.224358, 0.906969, 0.876226,
                0.896518, 0.217051, 0.806911,
            ),
            MDList2.of(
                rowNumber = 5u,
                columnNumber = 5u,
                0.6950659135021995, 0.305141029099385, 0.2849495803497484, 0.18924850715094577, 0.06621194540561182,
                0.5427677471662933, 0.3477587854455544, 0.19268984507607056, 0.7192678380872337, 0.9559541349243819,
                0.321505626806055, 0.006884575599147569, 0.6476750621834297, 0.5173372215716103, 0.9725368892817383,
                0.4608372955168507, 0.5288264683170789, 0.1623419993788311, 0.25222970651843246, 0.4140459121616542,
                0.6946824973673238, 0.1703905471138405, 0.7068176866026521, 0.9822287486511017, 0.548470697381017,
            ),
        )
        
        data class Algorithm(
            val name: String,
            val koneContextRegistry: KoneContextRegistry,
        )
        
        val algorithms = KoneList.of<Algorithm>(
            Algorithm(
                name = "via Householder",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    MatrixFactory.setViaDefault<Number>()
                    MatrixCategoryOverField.setViaDefault<Number, MDList2<Number>>()
                    MatrixProductComputer.setViaDefault<Number, MDList2<Number>>()
                    TransposeMatrixComputer.setViaDefault<Number, MDList2<Number>>()
                    InverseMatrixComputer.setViaGaussianElimination<Number, MDList2<Number>>()
                    HessenbergDecompositionComputer.setViaHouseholder<Number, MDList2<Number>>()
                },
            ),
        )
        
        for ((name, koneContextRegistry) in algorithms) testSuite(name) {
            koneContextRegistry.koneLocalUnwrap(
                Field.Key<Number>(),
                Equality.Key<Number>(),
                Order.Key<Number>(),
                MatrixCategoryOverField.Key<Number, MDList2<Number>>(),
                MatrixProductComputer.Key<Number, MDList2<Number>>(),
                TransposeMatrixComputer.Key<Number, MDList2<Number>>(),
                InverseMatrixComputer.Key<Number, MDList2<Number>>(),
                HessenbergDecompositionComputer.Key<Number, MDList2<Number>>(),
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
                    Expect.notToThrow({ input.hessenbergDecomposition() }) {
                        (val q = leftUnitary, val h = middleUpperHessenberg, val p = rightUnitary) = exposeValue()
                        withClue(
                            {
                                buildString {
                                    appendLine("Received the following matrices to check.")
                                    appendLine()
                                    appendLine("Q:")
                                    appendLine(q.toMatrixString())
                                    appendLine("H:")
                                    appendLine(h.toMatrixString())
                                    appendLine("P:")
                                    appendLine(p.toMatrixString())
                                }
                            }
                        ) {
                            softly {
                                withClue("Input differs from QHP.") {
                                    Expect.of(q * h * p).toBeEqualToWithTolerance(input, 1E-10)
                                }
                                withClue("H is not upper-hessenberg.") {
                                    Expect.of(h).toBeUpperHessenbergMatrix()
                                }
                                Expect.of(q.transpose()) {
                                    withClue("Transpose of Q is not P.") {
                                        toBeEqualToWithTolerance(p, 1E-10)
                                    }
                                    withClue("Q is not unitary.") {
                                        toBeEqualToWithTolerance(q.invert()!!, 1E-10)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    testSuite("complex case") {
        typealias Number = Double
        typealias CNumber = ComplexNumber<Number>
        typealias Matrix = MDList2<CNumber>
        
        val inputs = KoneList.of<Matrix>(
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0),
                ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0),
            ),
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                ComplexNumber(1.0E-17, 0.0), ComplexNumber(0.0, 0.0),
                ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
            ),
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
            ),
            MDList2.of(
                rowNumber = 2u,
                columnNumber = 2u,
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0), ComplexNumber(1.0, 1.0),
                ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0), ComplexNumber(1.0, 1.0),
                ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0), ComplexNumber(1.0, 1.0),
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
            ),
            MDList2.of(
                rowNumber = 3u,
                columnNumber = 3u,
                ComplexNumber(1.0, 2.0), ComplexNumber(3.0, 4.0), ComplexNumber(5.0, 6.0),
                ComplexNumber(7.0, 8.0), ComplexNumber(9.0, 10.0), ComplexNumber(11.0, 12.0),
                ComplexNumber(13.0, 14.0), ComplexNumber(15.0, 16.0), ComplexNumber(17.0, 18.0),
            ),
            MDList2.of(
                rowNumber = 5u,
                columnNumber = 5u,
                ComplexNumber(0.6950659135021995, 0.0), ComplexNumber(0.305141029099385, 0.0), ComplexNumber(0.2849495803497484, 0.0), ComplexNumber(0.18924850715094577, 0.0), ComplexNumber(0.06621194540561182, 0.0),
                ComplexNumber(0.5427677471662933, 0.0), ComplexNumber(0.3477587854455544, 0.0), ComplexNumber(0.19268984507607056, 0.0), ComplexNumber(0.7192678380872337, 0.0), ComplexNumber(0.9559541349243819, 0.0),
                ComplexNumber(0.321505626806055, 0.0), ComplexNumber(0.006884575599147569, 0.0), ComplexNumber(0.6476750621834297, 0.0),ComplexNumber(0.5173372215716103, 0.0), ComplexNumber(0.9725368892817383, 0.0),
                ComplexNumber(0.4608372955168507, 0.0), ComplexNumber(0.5288264683170789, 0.0), ComplexNumber(0.1623419993788311, 0.0), ComplexNumber(0.25222970651843246, 0.0), ComplexNumber(0.4140459121616542, 0.0),
                ComplexNumber(0.6946824973673238, 0.0), ComplexNumber(0.1703905471138405, 0.0), ComplexNumber(0.7068176866026521, 0.0), ComplexNumber(0.9822287486511017, 0.0), ComplexNumber(0.548470697381017, 0.0),
            ),
            MDList2.of(
                rowNumber = 5u,
                columnNumber = 5u,
                ComplexNumber(0.2041552220558671, 0.5866685501098761), ComplexNumber(0.5112599358487231, 0.17420380112148948), ComplexNumber(0.9217445556752717, 0.3828888568484645), ComplexNumber(0.9100286997584972, 0.5986569759414775), ComplexNumber(0.38583916709276145, 0.7205812481215996),
                ComplexNumber(0.778156376383673, 0.7872600616143126), ComplexNumber(0.6388734756189403, 0.4887610935815594), ComplexNumber(0.9275738887535621, 0.1711332584747871), ComplexNumber(0.9166751854702677, 0.10453510303783875), ComplexNumber(0.001916933706705759, 0.1933682241838408),
                ComplexNumber(0.4701727299654157, 0.4175481011267064), ComplexNumber(0.657689489698482, 0.3960525078378554), ComplexNumber(0.1558731079735025, 0.9783326856697072), ComplexNumber(0.5728392765445585, 0.3626675884207633), ComplexNumber(0.738292274518362, 0.9725014481296095),
                ComplexNumber(0.17402152415540617, 0.9742618377751635), ComplexNumber(0.19898430468251882, 0.3566217040409283), ComplexNumber(0.9117809493528302, 0.8011895992796609), ComplexNumber(0.07843404522427821, 0.6161701852535584), ComplexNumber(0.5043601050412589, 0.6255483001700457),
                ComplexNumber(0.7141786526450769, 0.8554001835194573), ComplexNumber(0.12351553620989808, 0.572562258823015), ComplexNumber(0.8605571475505056, 0.15854740833089864), ComplexNumber(0.47975242918425565, 0.9148477332331788), ComplexNumber(0.2461780967688325, 0.4466905460173003),
            ),
        )
        
        data class Algorithm(
            val name: String,
            val koneContextRegistry: KoneContextRegistry,
        )
        
        val algorithms = KoneList.of<Algorithm>(
            Algorithm(
                name = "via Householder",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    Number.setSafeField()
                    Number.setSafeEquality()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    ComplexNumber.setFieldExtensionOver<Number>()
                    ComplexNumber.setEquality<Number>()
                    MatrixFactory.setViaDefault<CNumber>()
                    MatrixCategoryOverField.setViaDefault<CNumber, Matrix>()
                    MatrixProductComputer.setViaDefault<CNumber, Matrix>()
                    ConjugateTransposeMatrixComputer.setViaDefault<Number, Matrix>()
                    InverseMatrixComputer.setViaGaussianElimination<CNumber, Matrix>()
                    HessenbergDecompositionComputer.setViaHouseholderForComplexNumbers<Number, Matrix>()
                }
            ),
        )
        
        for ((name, koneContextRegistry) in algorithms) testSuite(name) {
            koneContextRegistry.koneLocalUnwrap(
                Field.Key<Number>(),
                Order.Key<Number>(),
                PositiveSquareRootComputer.Key<Number>(),
                Equality.Key<CNumber>(),
                FieldExtension.Key<Number, CNumber>(),
                MatrixCategoryOverField.Key<CNumber, Matrix>(),
                MatrixProductComputer.Key<CNumber, Matrix>(),
                ConjugateTransposeMatrixComputer.Key<Number, Matrix>(),
                InverseMatrixComputer.Key<CNumber, Matrix>(),
                HessenbergDecompositionComputer.Key<CNumber, Matrix>(),
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
                    Expect.notToThrow({ input.hessenbergDecomposition() }) {
                        (val q = leftUnitary, val h = middleUpperHessenberg, val p = rightUnitary) = exposeValue()
                        withClue(
                            {
                                buildString {
                                    appendLine("Received the following matrices to check.")
                                    appendLine()
                                    appendLine("Q:")
                                    appendLine(q.toMatrixString())
                                    appendLine("H:")
                                    appendLine(h.toMatrixString())
                                    appendLine("P:")
                                    appendLine(p.toMatrixString())
                                }
                            }
                        ) {
                            softly {
                                withClue("Input differs from QHP.") {
                                    Expect.of(q * h * p).toBeEqualToWithTolerance(input, 1E-10)
                                }
                                withClue("H is not upper-hessenberg.") {
                                    Expect.of(h).toBeUpperHessenbergMatrix()
                                }
                                Expect.of(q.conjugateTranspose()) {
                                    withClue("Conjugate transpose of Q is not P.") {
                                        toBeEqualToWithTolerance(p, 1E-10)
                                    }
                                    withClue("Q is not unitary.") {
                                        toBeEqualToWithTolerance(q.invert()!!, 1E-10)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}