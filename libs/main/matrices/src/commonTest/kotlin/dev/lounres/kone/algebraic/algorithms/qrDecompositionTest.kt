/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.algorithms.implementations.*
import dev.lounres.kone.algebraic.algorithms.implementations.utils.requestFor
import dev.lounres.kone.algebraic.algorithms.utils.toMatrixString
import dev.lounres.kone.algebraic.assertions.toBeEqualToWithTolerance
import dev.lounres.kone.algebraic.assertions.toBeUpperTriangularMatrix
import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.Expect
import dev.lounres.kone.assertions.notToThrow
import dev.lounres.kone.assertions.of
import dev.lounres.kone.assertions.softly
import dev.lounres.kone.assertions.withClue
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.buildWithProvider
import dev.lounres.kone.contexts.koneLocalUnwrap
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


val QRDecompositionImplementationsTests by testSuite {
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
                name = "via Gram-Schmidt",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    val koneContextRegistry by lazy { contextOf<KoneContextRegistry.Provider>().get() }
                    Number.setSafeField()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    // TODO: Workaround for KT-87097
//                    MatrixFactory.setViaDefault<Number>()
                    MatrixFactory.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixFactory.viaDefault(
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) { "MatrixFactory.default<${suppliedTypeOf<Number>()}>" }
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixCategoryOverField.setViaDefault<Number, MDList2<Number>>()
                    MatrixCategoryOverField.Key<Number, MDList2<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
                        MatrixCategoryOverField.viaDefault<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixProductComputer.setViaDefault<Number, MDList2<Number>>()
                    MatrixProductComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixProductComputer.viaDefault(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    TransposeMatrixComputer.setViaDefault<Number, MDList2<Number>>()
                    TransposeMatrixComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        TransposeMatrixComputer.viaDefault<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "TransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            }
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    InverseMatrixComputer.setViaGaussianElimination<Number, MDList2<Number>>()
                    InverseMatrixComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        InverseMatrixComputer.viaGaussianElimination<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    QRDecompositionComputer.setViaGramSchmidt<Number, MDList2<Number>>()
                    QRDecompositionComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        QRDecompositionComputer.viaGramSchmidt<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "QRDecompositionComputer.viaGramSchmidt<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "QRDecompositionComputer.viaGramSchmidt<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                                "QRDecompositionComputer.viaGramSchmidt<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                },
            ),
            Algorithm(
                name = "via Householder",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    val koneContextRegistry by lazy { contextOf<KoneContextRegistry.Provider>().get() }
                    Number.setSafeField()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    // TODO: Workaround for KT-87097
//                    MatrixFactory.setViaDefault<Number>()
                    MatrixFactory.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixFactory.viaDefault(
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) { "MatrixFactory.default<${suppliedTypeOf<Number>()}>" }
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixCategoryOverField.setViaDefault<Number, MDList2<Number>>()
                    MatrixCategoryOverField.Key<Number, MDList2<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
                        MatrixCategoryOverField.viaDefault<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixProductComputer.setViaDefault<Number, MDList2<Number>>()
                    MatrixProductComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixProductComputer.viaDefault(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    TransposeMatrixComputer.setViaDefault<Number, MDList2<Number>>()
                    TransposeMatrixComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        TransposeMatrixComputer.viaDefault<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "TransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            }
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    InverseMatrixComputer.setViaGaussianElimination<Number, MDList2<Number>>()
                    InverseMatrixComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        InverseMatrixComputer.viaGaussianElimination<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    QRDecompositionComputer.setViaHouseholder<Number, MDList2<Number>>()
                    QRDecompositionComputer.Key<Number, MDList2<Number>>() correspondsTo RegisteredValueProvider.cached {
                        QRDecompositionComputer.viaHouseholder<Number, MDList2<Number>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<Number, MDList2<Number>>()) {
                                "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            numberOrder = koneContextRegistry.requestFor(Order.Key<Number>()) {
                                "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                                "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<Number, MDList2<Number>>()) {
                                "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<Number, MDList2<Number>>()) {
                                "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            transposeMatrixComputer = koneContextRegistry.requestFor(TransposeMatrixComputer.Key<Number, MDList2<Number>>()) {
                                "QRDecompositionComputer.viaHouseholder<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                },
            ),
        )
        
        for (algorithm in algorithms) testSuite(algorithm.name) {
            algorithm.koneContextRegistry.koneLocalUnwrap(
                Field.Key<Number>(),
                Order.Key<Number>(),
                MatrixCategoryOverField.Key<Number, MDList2<Number>>(),
                MatrixProductComputer.Key<Number, MDList2<Number>>(),
                TransposeMatrixComputer.Key<Number, MDList2<Number>>(),
                InverseMatrixComputer.Key<Number, MDList2<Number>>(),
                QRDecompositionComputer.Key<Number, MDList2<Number>>(),
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
                    Expect.notToThrow({ input.qrDecomposition() }) {
                        (val q = leftUnitary, val r = rightUpperTriangular) = exposeValue()
                        withClue(
                            {
                                buildString {
                                    appendLine("Received the following matrices to check.")
                                    appendLine()
                                    appendLine("Q:")
                                    appendLine(q.toMatrixString())
                                    appendLine("R:")
                                    appendLine(r.toMatrixString())
                                }
                            }
                        ) {
                            softly {
                                withClue("Input differs from QR.") {
                                    Expect.of(q * r).toBeEqualToWithTolerance(input, 1E-10)
                                }
                                withClue("R is not upper-triangular.") {
                                    Expect.of(r).toBeUpperTriangularMatrix()
                                }
                                withClue("Q is not unitary.") {
                                    Expect.of(q.transpose()).toBeEqualToWithTolerance(q.invert()!!, 1E-10)
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
        
        val inputs = KoneList.of<MDList2<ComplexNumber<Number>>>(
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
                name = "via Gram-Schmidt",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    val koneContextRegistry by lazy { contextOf<KoneContextRegistry.Provider>().get() }
                    Number.setSafeField()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    // TODO: Workaround for KT-87097
//                    ComplexNumber.setFieldExtensionOver<Number>()
                    FieldExtension.Key<Number, ComplexNumber<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
                        ComplexNumber.fieldExtensionOver(koneContextRegistry[Field.Key<Number>()])
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixFactory.setViaDefault<ComplexNumber<Number>>()
                    MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixFactory.viaDefault(
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<ComplexNumber<Number>>()) { "MatrixFactory.default<${suppliedTypeOf<Number>()}>" }
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixCategoryOverField.setViaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()
                    MatrixCategoryOverField.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
                        MatrixCategoryOverField.viaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<ComplexNumber<Number>>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixProductComputer.setViaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()
                    MatrixProductComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixProductComputer.viaDefault(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<ComplexNumber<Number>>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    ConjugateTransposeMatrixComputer.setViaDefault<Number, MDList2<ComplexNumber<Number>>>()
                    ConjugateTransposeMatrixComputer.Key<Number, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        ConjugateTransposeMatrixComputer.viaDefault<Number, MDList2<ComplexNumber<Number>>>(
                            numberCommutativeRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
                                "ConjugateTransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "ConjugateTransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    InverseMatrixComputer.setViaGaussianElimination<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()
                    InverseMatrixComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        InverseMatrixComputer.viaGaussianElimination<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<ComplexNumber<Number>>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<ComplexNumber<Number>>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<ComplexNumber<Number>>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    QRDecompositionComputer.setViaGramSchmidtForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>()
                    QRDecompositionComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        QRDecompositionComputer.viaGramSchmidtForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
                                "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                                "QRDecompositionComputer.viaGramSchmidtForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                },
            ),
            Algorithm(
                name = "via Householder",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    val koneContextRegistry by lazy { contextOf<KoneContextRegistry.Provider>().get() }
                    Number.setSafeField()
                    Number.setSafeOrder()
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    // TODO: Workaround for KT-87097
//                    ComplexNumber.setFieldExtensionOver<Number>()
                    FieldExtension.Key<Number, ComplexNumber<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
                        ComplexNumber.fieldExtensionOver(koneContextRegistry[Field.Key<Number>()])
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixFactory.setViaDefault<ComplexNumber<Number>>()
                    MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixFactory.viaDefault(
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<ComplexNumber<Number>>()) { "MatrixFactory.default<${suppliedTypeOf<Number>()}>" }
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixCategoryOverField.setViaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()
                    MatrixCategoryOverField.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
                        MatrixCategoryOverField.viaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<ComplexNumber<Number>>()) {
                                "MatrixCategoryOverField.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixProductComputer.setViaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()
                    MatrixProductComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixProductComputer.viaDefault(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<ComplexNumber<Number>>()) {
                                "MatrixProductComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<Number>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    ConjugateTransposeMatrixComputer.setViaDefault<Number, MDList2<ComplexNumber<Number>>>()
                    ConjugateTransposeMatrixComputer.Key<Number, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        ConjugateTransposeMatrixComputer.viaDefault<Number, MDList2<ComplexNumber<Number>>>(
                            numberCommutativeRing = koneContextRegistry.requestFor(CommutativeRing.Key<Number>()) {
                                "ConjugateTransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "ConjugateTransposeMatrixComputer.viaDefault<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    InverseMatrixComputer.setViaGaussianElimination<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()
                    InverseMatrixComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        InverseMatrixComputer.viaGaussianElimination<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<ComplexNumber<Number>>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<ComplexNumber<Number>>()) {
                                "InverseMatrixComputer.viaGaussianElimination<${suppliedTypeOf<ComplexNumber<Number>>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    QRDecompositionComputer.setViaHouseholderForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>()
                    QRDecompositionComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        QRDecompositionComputer.viaHouseholderForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            numberOrder = koneContextRegistry.requestFor(Order.Key<Number>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, MDList2<ComplexNumber<Number>>>()) {
                                "QRDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                },
            ),
        )
        
        for (algorithm in algorithms) testSuite(algorithm.name) {
            algorithm.koneContextRegistry.koneLocalUnwrap(
                Field.Key<Number>(),
                Order.Key<Number>(),
                PositiveSquareRootComputer.Key<Number>(),
                FieldExtension.Key<Number, ComplexNumber<Number>>(),
                MatrixCategoryOverField.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(),
                MatrixProductComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(),
                ConjugateTransposeMatrixComputer.Key<Number, MDList2<ComplexNumber<Number>>>(),
                InverseMatrixComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(),
                QRDecompositionComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(),
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
                    Expect.notToThrow({ input.qrDecomposition() }) {
                        (val q = leftUnitary, val r = rightUpperTriangular) = exposeValue()
                        withClue(
                            {
                                buildString {
                                    appendLine("Received the following matrices to check.")
                                    appendLine()
                                    appendLine("Q:")
                                    appendLine(q.toMatrixString())
                                    appendLine("R:")
                                    appendLine(r.toMatrixString())
                                }
                            }
                        ) {
                            softly {
                                withClue("Input differs from QR.") {
                                    Expect.of(q * r).toBeEqualToWithTolerance(input, 1E-10)
                                }
                                withClue("R is not upper-triangular.") {
                                    Expect.of(r).toBeUpperTriangularMatrix()
                                }
                                withClue("Q is not unitary.") {
                                    Expect.of(q.conjugateTranspose()).toBeEqualToWithTolerance(q.invert()!!, 1E-10)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}