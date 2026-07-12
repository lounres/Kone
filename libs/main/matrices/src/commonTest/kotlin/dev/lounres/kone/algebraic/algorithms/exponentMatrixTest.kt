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
import dev.lounres.kone.algebraic.assertions.toBeEqualToWithLinearTolerance
import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.buildWithProvider
import dev.lounres.kone.contexts.koneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Order
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


val ExponentMatrixTests by testSuite {
//    testSuite("real case") {
//        typealias Number = Double
//        val numberType = Number.suppliedType
//
//        @OptIn(DelicateSuppliedTypeConstructor::class)
//        val matrixType = SuppliedType.Regular(
//            fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList2",
//            typeArguments = listOf(
//                SuppliedProjection.Regular(
//                    variance = OUT,
//                    type = numberType
//                )
//            ),
//            isNullable = false,
//        )
//
//        val inputs = KoneList.of<MDList2<Double>>(
//            MDList2.of(
//                rowNumber = 2u,
//                columnNumber = 2u,
//                0.0, 1.0,
//                1.0, 0.0,
//            ),
//            MDList2.of(
//                rowNumber = 2u,
//                columnNumber = 2u,
//                -0.3887440162876692 , 0.37522780472518924,
//                -0.18232685650741207, -0.2287135425824249,
//            ),
//            MDList2.of(
//                rowNumber = 3u,
//                columnNumber = 3u,
//                1.0, 0.0, 0.0,
//                2.0, 3.0, 0.0,
//                0.0, 4.0, 5.0,
//            ),
//            MDList2.of(
//                rowNumber = 3u,
//                columnNumber = 3u,
//                1.0, 0.0, 0.0,
//                1.0, 1.001, 0.0,
//                0.0, 1.0, 1.002,
//            ),
//            MDList2.of(
//                rowNumber = 3u,
//                columnNumber = 3u,
//                1.0, 0.0, 0.0,
//                1.0, 1.0, 0.0,
//                0.0, 1.0, 1.0,
//            ),
//            MDList2.of(
//                rowNumber = 3u,
//                columnNumber = 3u,
//                0.685478, 0.495653, 0.479176,
//                0.224358, 0.906969, 0.876226,
//                0.896518, 0.217051, 0.806911,
//            ),
//            MDList2.of(
//                rowNumber = 5u,
//                columnNumber = 5u,
//                0.6950659135021995, 0.305141029099385, 0.2849495803497484, 0.18924850715094577, 0.06621194540561182,
//                0.5427677471662933, 0.3477587854455544, 0.19268984507607056, 0.7192678380872337, 0.9559541349243819,
//                0.321505626806055, 0.006884575599147569, 0.6476750621834297, 0.5173372215716103, 0.9725368892817383,
//                0.4608372955168507, 0.5288264683170789, 0.1623419993788311, 0.25222970651843246, 0.4140459121616542,
//                0.6946824973673238, 0.1703905471138405, 0.7068176866026521, 0.9822287486511017, 0.548470697381017,
//            ),
//            MDList2.of(
//                rowNumber = 5u,
//                columnNumber = 5u,
//                1.0, 0.0, 0.0, 0.0, 0.0,
//                1.0, 1.0001, 0.0, 0.0, 0.0,
//                0.0, 1.0, 1.0002, 0.0, 0.0,
//                0.0, 0.0, 1.0, 1.0003, 0.0,
//                0.0, 0.0, 0.0, 1.0, 1.0004,
//            ),
//        )
//
//        data class Algorithm(
//            val name: String,
//            val koneContextRegistry: KoneContextRegistry,
//        )
//
//        val algorithms = KoneList.of<Algorithm>(
//            Algorithm(
//                name = "via Golub and Van Loan",
//                koneContextRegistry = KoneContextRegistry.buildWithProvider {
//                    Number.setSafeField()
//                    Number.setSafeOrder()
//                    PositiveSquareRootComputer.setViaDefaultForDouble()
//                    MatrixFactory.setDefault<Number>(numberType = numberType)
//                    MatrixCategoryOverField.setViaDefault<Number, MDList2<Number>>(numberType = numberType, matrixType = matrixType)
//                    MatrixProductComputer.setViaDefault<Number, MDList2<Number>>(numberType = numberType, matrixType = matrixType)
//                    TransposeMatrixComputer.setViaDefault<Number, MDList2<Number>>(matrixType = matrixType)
//                    HessenbergDecompositionComputer.setViaHouseholder<Number, MDList2<Number>>(numberType = numberType, matrixType = matrixType)
//                    SchurDecompositionComputer.setViaGolubVanLoan<Number, MDList2<Number>>(numberType = numberType, matrixType = matrixType, tolerance = 1E-10)
//                }
//            )
//        )
//
//        for (algorithm in algorithms) testSuite(algorithm.name) {
//            algorithm.koneContextRegistry.koneContext(
//                Field.Key<Number>(numberType = numberType),
//                Order.Key<Number>(elementType = numberType),
//                MatrixCategoryOverField.Key<Number, MDList2<Number>>(matrixType = matrixType),
//                MatrixProductComputer.Key<Number, MDList2<Number>>(matrixType = matrixType),
//                TransposeMatrixComputer.Key<Number, MDList2<Number>>(matrixType = matrixType),
//                SchurDecompositionComputer.Key<Number, MDList2<Number>>(matrixType = matrixType),
//            ) {
//                for ((index, input) in inputs.withIndex()) test("input #$index") {
//                    AssertionScope.withClue(
//                        {
//                            buildString {
//                                appendLine("Received the following matrix to decompose.")
//                                appendLine()
//                                appendLine("Input:")
//                                appendLine(input.toMatrixString())
//                            }
//                        }
//                    ) {
//                        Expect.notToThrow({ input.schurDecomposition() }) {
//                            val (q, t, p) = exposeValue()
//                            withClue(
//                                {
//                                    buildString {
//                                        appendLine("Received the following matrices to check.")
//                                        appendLine()
//                                        appendLine("Input:")
//                                        appendLine(input.toMatrixString())
//                                        appendLine("Q:")
//                                        appendLine(q.toMatrixString())
//                                        appendLine("T:")
//                                        appendLine(t.toMatrixString())
//                                        appendLine("P:")
//                                        appendLine(p.toMatrixString())
//                                    }
//                                }
//                            ) {
//                                softly {
//                                    withClue("Input differs from QTP.") {
//                                        Expect.of(q * t * p).toBeEqualToWithTolerance(input, 1E-10)
//                                    }
//                                    withClue("T is not quasi-upper-triangular.") {
//                                        Expect.of(t).toBeQuasiUpperTriangularMatrix()
//                                    }
//                                    withClue("Transpose of Q is not P.") {
//                                        Expect.of(q.transpose()).toBeEqualToWithTolerance(p, 1E-10)
//                                    }
//                                    withClue("Q is not unitary.") {
//                                        Expect.of(q * q.transpose()).toBeUnitMatrixWithTolerance(1E-10)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    
    testSuite("complex case") {
        typealias Number = Double
        
        data class TestData(
            val input: MDList2<ComplexNumber<Number>>,
            val exponentOutput: MDList2<ComplexNumber<Number>>,
        )
        
        val inputs = KoneList.of<TestData>(
            TestData(
                input = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0),
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(2.378024613547364, 0.9093306736314786), ComplexNumber(-0.9093306736314786, 1.3780246135473637),
                    ComplexNumber(1.3780246135473637, 0.9093306736314786), ComplexNumber(0.09066932636852143, 1.3780246135473637),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(1.0E-17, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(1.0, 0.0), ComplexNumber(1.0, 0.0),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(-0.45969769413186023, 0.8414709848078965),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.5403023058681398, 0.8414709848078965),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(1.0, 0.0),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(0.0, 0.0), ComplexNumber(-1.0, 0.0),
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(0.5403023058681398, 0.0), ComplexNumber(-0.8414709848078965, 0.0),
                    ComplexNumber(0.8414709848078965, 0.0), ComplexNumber(0.5403023058681398, 0.0),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(1.0, 0.0), ComplexNumber(1.0, 0.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 2u,
                    columnNumber = 2u,
                    ComplexNumber(2.718281828459045, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(2.718281828459045, 0.0), ComplexNumber(2.718281828459045, 0.0),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0), ComplexNumber(1.0, 1.0),
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0), ComplexNumber(1.0, 1.0),
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 1.0), ComplexNumber(1.0, 1.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(1.6609793441972227, 2.6984455045169025), ComplexNumber(-2.6984455045169025, 0.6609793441972227), ComplexNumber(-2.0374661603196795, 3.359424848714125),
                    ComplexNumber(0.6609793441972227, 2.6984455045169025), ComplexNumber(-1.6984455045169022, 0.6609793441972227), ComplexNumber(-2.0374661603196795, 3.359424848714125),
                    ComplexNumber(0.6609793441972227, 2.6984455045169025), ComplexNumber(-2.6984455045169025, 0.6609793441972227), ComplexNumber(-1.0374661603196795, 3.359424848714125),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 1.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(-0.45969769413186023, 0.8414709848078965),
                    ComplexNumber(0.0, 0.0), ComplexNumber(1.0, 0.0), ComplexNumber(-0.45969769413186023, 0.8414709848078965),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.5403023058681398, 0.8414709848078965),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(1.0, 0.0), ComplexNumber(0.0, 0.0),
                    ComplexNumber(0.0, 0.0), ComplexNumber(0.0, 0.0), ComplexNumber(1.0, 0.0),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(1.0, 2.0), ComplexNumber(3.0, 4.0), ComplexNumber(5.0, 6.0),
                    ComplexNumber(7.0, 8.0), ComplexNumber(9.0, 10.0), ComplexNumber(11.0, 12.0),
                    ComplexNumber(13.0, 14.0), ComplexNumber(15.0, 16.0), ComplexNumber(17.0, 18.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 3u,
                    columnNumber = 3u,
                    ComplexNumber(3.999763357482291E11, 4.933477797905842E11), ComplexNumber(5.029960477248986E11, 6.067731612589069E11), ComplexNumber(6.060157597025681E11, 7.201985427272295E11),
                    ComplexNumber(1.028900143601709E12, 1.117105909054648E12), ComplexNumber(1.2918521915411143E12, 1.3722722829525188E12), ComplexNumber(1.5548042394785193E12, 1.6274386568503892E12),
                    ComplexNumber(1.657823951456189E12, 1.740864038318712E12), ComplexNumber(2.0807083353553298E12, 2.1377714046461309E12), ComplexNumber(2.5035927192554707E12, 2.534678770973549E12),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 5u,
                    columnNumber = 5u,
                    ComplexNumber(0.6950659135021995, 0.0), ComplexNumber(0.305141029099385, 0.0), ComplexNumber(0.2849495803497484, 0.0), ComplexNumber(0.18924850715094577, 0.0), ComplexNumber(0.06621194540561182, 0.0),
                    ComplexNumber(0.5427677471662933, 0.0), ComplexNumber(0.3477587854455544, 0.0), ComplexNumber(0.19268984507607056, 0.0), ComplexNumber(0.7192678380872337, 0.0), ComplexNumber(0.9559541349243819, 0.0),
                    ComplexNumber(0.321505626806055, 0.0), ComplexNumber(0.006884575599147569, 0.0), ComplexNumber(0.6476750621834297, 0.0),ComplexNumber(0.5173372215716103, 0.0), ComplexNumber(0.9725368892817383, 0.0),
                    ComplexNumber(0.4608372955168507, 0.0), ComplexNumber(0.5288264683170789, 0.0), ComplexNumber(0.1623419993788311, 0.0), ComplexNumber(0.25222970651843246, 0.0), ComplexNumber(0.4140459121616542, 0.0),
                    ComplexNumber(0.6946824973673238, 0.0), ComplexNumber(0.1703905471138405, 0.0), ComplexNumber(0.7068176866026521, 0.0), ComplexNumber(0.9822287486511017, 0.0), ComplexNumber(0.548470697381017, 0.0),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 5u,
                    columnNumber = 5u,
                    ComplexNumber(2.7156466438852016, 0.0), ComplexNumber(0.8065274950880383, 0.0), ComplexNumber(0.9661358079007849, 0.0), ComplexNumber(1.0227461315446915, 0.0), ComplexNumber(0.969673493339811, 0.0),
                    ComplexNumber(2.5438562609738042, 0.0), ComplexNumber(2.3623923290432027, 0.0), ComplexNumber(1.6214972160281191, 0.0), ComplexNumber(2.5815003928503417, 0.0), ComplexNumber(2.765402868784812, 0.0),
                    ComplexNumber(2.094142253379643, 0.0), ComplexNumber(0.8208942560159271, 0.0), ComplexNumber(3.1200440381187393, 0.0), ComplexNumber(2.274633926757379, 0.0), ComplexNumber(2.7352638435316035, 0.0),
                    ComplexNumber(1.7845507173203659, 0.0), ComplexNumber(1.190871082005485, 0.0), ComplexNumber(1.0697444539722698, 0.0), ComplexNumber(2.4824541604078223, 0.0), ComplexNumber(1.6614162125677499, 0.0),
                    ComplexNumber(2.7582113971525875, 0.0), ComplexNumber(1.225238817530138, 0.0), ComplexNumber(2.243929671708349, 0.0), ComplexNumber(2.8350341541274813, 0.0), ComplexNumber(3.5349801763546136, 0.0),
                ),
            ),
            TestData(
                input = MDList2.of(
                    rowNumber = 5u,
                    columnNumber = 5u,
                    ComplexNumber(0.2041552220558671, 0.5866685501098761), ComplexNumber(0.5112599358487231, 0.17420380112148948), ComplexNumber(0.9217445556752717, 0.3828888568484645), ComplexNumber(0.9100286997584972, 0.5986569759414775), ComplexNumber(0.38583916709276145, 0.7205812481215996),
                    ComplexNumber(0.778156376383673, 0.7872600616143126), ComplexNumber(0.6388734756189403, 0.4887610935815594), ComplexNumber(0.9275738887535621, 0.1711332584747871), ComplexNumber(0.9166751854702677, 0.10453510303783875), ComplexNumber(0.001916933706705759, 0.1933682241838408),
                    ComplexNumber(0.4701727299654157, 0.4175481011267064), ComplexNumber(0.657689489698482, 0.3960525078378554), ComplexNumber(0.1558731079735025, 0.9783326856697072), ComplexNumber(0.5728392765445585, 0.3626675884207633), ComplexNumber(0.738292274518362, 0.9725014481296095),
                    ComplexNumber(0.17402152415540617, 0.9742618377751635), ComplexNumber(0.19898430468251882, 0.3566217040409283), ComplexNumber(0.9117809493528302, 0.8011895992796609), ComplexNumber(0.07843404522427821, 0.6161701852535584), ComplexNumber(0.5043601050412589, 0.6255483001700457),
                    ComplexNumber(0.7141786526450769, 0.8554001835194573), ComplexNumber(0.12351553620989808, 0.572562258823015), ComplexNumber(0.8605571475505056, 0.15854740833089864), ComplexNumber(0.47975242918425565, 0.9148477332331788), ComplexNumber(0.2461780967688325, 0.4466905460173003),
                ),
                exponentOutput = MDList2.of(
                    rowNumber = 5u,
                    columnNumber = 5u,
                    ComplexNumber(-2.2964110965439386, 0.6419082724417239), ComplexNumber(-1.8026560916367829, 0.8814135487263368), ComplexNumber(-2.8356255969358015, 1.932918188667081), ComplexNumber(-2.421408447912703, 1.2931126361367757), ComplexNumber(-2.751111556455108, 0.8938662651680829),
                    ComplexNumber(-2.7353376354136207, 1.7271202468337141), ComplexNumber(-0.34205768349624643, 1.869370154212128), ComplexNumber(-2.341944263181504, 2.7317166605451844), ComplexNumber(-1.9184470084679546, 1.7338078619517083), ComplexNumber(-3.0385160470915773, 1.1308805163809386),
                    ComplexNumber(-3.1306443460728515, 0.1058979989150095), ComplexNumber(-1.9766618163562946, 0.8407104958256274), ComplexNumber(-2.762769011818295, 1.7053176215840775), ComplexNumber(-2.8516228262340686, 0.7715569166647622), ComplexNumber(-2.7083593639944707, 0.534952841780898),
                    ComplexNumber(-3.142608377861831, -0.25956907445535005), ComplexNumber(-2.2471898249069344, 0.3308911528169346), ComplexNumber(-3.3054197921658237, 0.939394700411359), ComplexNumber(-2.23235962343116, 0.3543538812395831), ComplexNumber(-2.619410533860688, -0.22418741585010493),
                    ComplexNumber(-2.9668688213006558, 0.23984801637015618), ComplexNumber(-2.285814630334815, 0.8865703848651736), ComplexNumber(-3.1513332894239587, 1.2483727179591306), ComplexNumber(-2.8300787834780463, 1.2115418776068283), ComplexNumber(-1.8267207486469719, 0.1531538545827389),
                ),
            ),
        )
        
        data class Algorithm(
            val name: String,
            val koneContextRegistry: KoneContextRegistry,
        )
        
        val algorithms = KoneList.of<Algorithm>(
            Algorithm(
                name = "via Schur and Parlett using scaling and squaring",
                koneContextRegistry = KoneContextRegistry.buildWithProvider {
                    val koneContextRegistry by lazy { contextOf<KoneContextRegistry.Provider>().get() }
                    Number.setSafeField()
                    Number.setSafeOrder()
                    // TODO: Workaround for KT-87097
//                    ComplexNumber.setFieldExtensionOver<Number>()
                    FieldExtension.Key<Number, ComplexNumber<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
                        ComplexNumber.fieldExtensionOver(koneContextRegistry[Field.Key<Number>()])
                    }
                    PositiveSquareRootComputer.setViaDefaultForDouble()
                    // TODO: Workaround for KT-87097
//                    SquareRootsComputer.setViaDefaultForComplexNumbers<Number>()
                    SquareRootsComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
                        SquareRootsComputer.viaDefaultForComplexNumbers<Number>(
                            field = koneContextRegistry[Field.Key<Number>()],
                            order = koneContextRegistry[Order.Key<Number>()],
                            positiveSquareRootComputer = koneContextRegistry[PositiveSquareRootComputer.Key<Number>()],
                        )
                    }
                    ExponentComputer.setViaDefaultForDouble()
                    CosineComputer.setViaDefaultForDouble()
                    SineComputer.setViaDefaultForDouble()
                    HyperbolicCosineComputer.setViaDefaultForDouble()
                    HyperbolicSineComputer.setViaDefaultForDouble()
                    // TODO: Workaround for KT-87097
//                    ExponentComputer.setViaDefaultForComplexNumbers<Number>()
                    ExponentComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
                        ExponentComputer.viaDefaultForComplexNumbers<Number>(
                            numberRing = koneContextRegistry[CommutativeRing.Key<Number>()],
                            numberExponentComputer = koneContextRegistry[ExponentComputer.Key<Number>()],
                            numberCosineComputer = koneContextRegistry[CosineComputer.Key<Number>()],
                            numberSineComputer = koneContextRegistry[SineComputer.Key<Number>()],
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    HyperbolicSineOverInputComputer.setViaDefaultForComplexNumbers<Number>()
                    HyperbolicSineOverInputComputer.Key<ComplexNumber<Number>>() correspondsTo RegisteredValueProvider.cached {
                        HyperbolicSineOverInputComputer.viaDefaultForComplexNumbers<Number>(
                            field = koneContextRegistry[Field.Key<Number>()],
                            complexNumbersFieldExtension = koneContextRegistry[FieldExtension.Key<Number, ComplexNumber<Number>>()],
                            cosineComputer = koneContextRegistry[CosineComputer.Key<Number>()],
                            sineComputer = koneContextRegistry[SineComputer.Key<Number>()],
                            hyperbolicCosineComputer = koneContextRegistry[HyperbolicCosineComputer.Key<Number>()],
                            hyperbolicSineComputer = koneContextRegistry[HyperbolicSineComputer.Key<Number>()],
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    MatrixFactory.setViaDefault<ComplexNumber<Number>>()
                    MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        MatrixFactory.viaDefault(
                            ring = koneContextRegistry.requestFor(CommutativeRing.Key<ComplexNumber<Number>>()) { "MatrixFactory.default<${suppliedTypeOf<ComplexNumber<Number>>()}>" }
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
//                    IsDiagonalMatrixChecker.setViaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()
                    IsDiagonalMatrixChecker.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        IsDiagonalMatrixChecker.viaDefault<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>(
                            numberRing = koneContextRegistry.requestFor(CommutativeRing.Key<ComplexNumber<Number>>()) {
                                "IsDiagonalMatrixChecker.viaDefault<${suppliedTypeOf<ComplexNumber<Number>>()}, ?>"
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
//                    HessenbergDecompositionComputer.setViaHouseholderForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>()
                    HessenbergDecompositionComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            numberOrder = koneContextRegistry.requestFor(Order.Key<Number>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, MDList2<ComplexNumber<Number>>>()) {
                                "HessenbergDecompositionComputer.viaHouseholderForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    SchurDecompositionComputer.setViaGolubVanLoanForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>(tolerance = 1E-17)
                    SchurDecompositionComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<Number, MDList2<ComplexNumber<Number>>>(
                            tolerance = 1E-17,
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            numberField = koneContextRegistry.requestFor(Field.Key<Number>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberFieldExtension = koneContextRegistry.requestFor(FieldExtension.Key<Number, ComplexNumber<Number>>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            numberOrder = koneContextRegistry.requestFor(Order.Key<Number>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Number>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberSquareRootComputer = koneContextRegistry.requestFor(SquareRootsComputer.Key<ComplexNumber<Number>>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            conjugateTransposeMatrixComputer = koneContextRegistry.requestFor(ConjugateTransposeMatrixComputer.Key<Number, MDList2<ComplexNumber<Number>>>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            hessenbergDecompositionComputer = koneContextRegistry.requestFor(HessenbergDecompositionComputer.Key<ComplexNumber<Number>, MDList2<ComplexNumber<Number>>>()) {
                                "SchurDecompositionComputer.viaGolubVanLoanForComplexNumbers<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                        )
                    }
                    // TODO: Workaround for KT-87097
//                    ExponentComputer.setViaSchurParlettUsingScalingAndSquaring<MDList2<ComplexNumber<Number>>>(blockingParameter = 0.1)
                    ExponentComputer.Key<MDList2<ComplexNumber<Number>>>() correspondsTo RegisteredValueProvider.cached {
                        ExponentComputer.viaSchurParlettUsingScalingAndSquaring(
                            matrixFactory = koneContextRegistry.requestFor(MatrixFactory.Key<ComplexNumber<Double>, MDList2<ComplexNumber<Number>>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<Double>()}>"
                            },
                            field = koneContextRegistry.requestFor(Field.Key<Double>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            order = koneContextRegistry.requestFor(Order.Key<Double>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberField = koneContextRegistry.requestFor(Field.Key<ComplexNumber<Double>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            positiveSquareRootComputer = koneContextRegistry.requestFor(PositiveSquareRootComputer.Key<Double>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberExponentComputer = koneContextRegistry.requestFor(ExponentComputer.Key<ComplexNumber<Double>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            complexNumberHyperbolicSineOverInputComputer = koneContextRegistry.requestFor(HyperbolicSineOverInputComputer.Key<ComplexNumber<Double>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixCategoryOverField = koneContextRegistry.requestFor(MatrixCategoryOverField.Key<ComplexNumber<Double>, MDList2<ComplexNumber<Number>>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            matrixProductComputer = koneContextRegistry.requestFor(MatrixProductComputer.Key<ComplexNumber<Double>, MDList2<ComplexNumber<Number>>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            inverseMatrixComputer = koneContextRegistry.requestFor(InverseMatrixComputer.Key<ComplexNumber<Double>, MDList2<ComplexNumber<Number>>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            schurDecompositionComputer = koneContextRegistry.requestFor(SchurDecompositionComputer.Key<ComplexNumber<Double>, MDList2<ComplexNumber<Number>>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            isDiagonalMatrixChecker = koneContextRegistry.requestFor(IsDiagonalMatrixChecker.Key<ComplexNumber<Double>, MDList2<ComplexNumber<Number>>>()) {
                                "ExponentComputer.viaSchurParlettUsingScalingAndSquaring<Double, ${suppliedTypeOf<MDList2<ComplexNumber<Number>>>()}>"
                            },
                            blockingParameter = 0.1,
                        )
                    }
                }
            )
        )
        
        for (algorithm in algorithms) testSuite(algorithm.name) {
            algorithm.koneContextRegistry.koneContext(
                Field.Key<Number>(),
                Order.Key<Number>(),
                PositiveSquareRootComputer.Key<Number>(),
                FieldExtension.Key<Number, ComplexNumber<Number>>(),
                ExponentComputer.Key<MDList2<ComplexNumber<Number>>>(),
            ) {
                for ((val index, val input = value) in inputs.withIndex()) test("input #$index") {
                    (val input, val exponentOutput) = input
                    AssertionScope.withClue(
                        {
                            buildString {
                                appendLine("Input:")
                                appendLine(input.toMatrixString())
                            }
                        }
                    ) {
                        Expect.notToThrow({ input.exponent() }) {
                            val result = exposeValue()
                            withClue(
                                {
                                    buildString {
                                        appendLine("Expected exponent output:")
                                        appendLine(exponentOutput.toMatrixString())
                                        appendLine()
                                        appendLine("Actual exponent output:")
                                        appendLine(result.toMatrixString())
                                    }
                                }
                            ) {
                                Expect.of(result).toBeEqualToWithLinearTolerance(exponentOutput, 1E-10, 1E-15)
                            }
                        }
                    }
                }
            }
        }
    }
}