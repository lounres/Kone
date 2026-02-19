/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry


//class DoubleBenchmarks {
//    companion object {
//        val numberContext = Double.context
//        val euclideanSpaceScope = numberContext.euclideanKategoryScope
//    }
//
//    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
//    @State(Scope.Benchmark)
//    class MultidimensionalConvexHullBenchmarksMock {
//        @Param(
//            "test"
//        )
//        var testId: String = ""
//
//        var spaceDimension: UInt = 0u
//
//        var input: KoneList<Point<Double>> = emptyKoneList()
//
//        val mockPolytopicConstruction by lazy {
//            MockAbstractPolytopicConstruction(
//                spaceDimension = spaceDimension,
//                numberContext = numberContext,
//            )
//        }
//        val mockPolytopicConstruction2 =
//            MockAbstractPolytopicConstruction(
//                spaceDimension = spaceDimension,
//                numberContext = numberContext,
//            )
//        var listOfMockVertices: KoneList<AbstractVertex> = emptyKoneList()
//
//        @Setup
//        fun setup() {
//            val resource = this.javaClass.getResource("/RealValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
//            val parsedInput: PointSetDescription<Double> = Yaml.default.decodeFromString(resource.readText())
//            spaceDimension = parsedInput.dim
//            input = parsedInput.points
//            mockPolytopicConstruction {
//                listOfMockVertices = input.map { addVertex(it) }
//            }
//        }
//
//        @Benchmark
//        fun empty(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                val view = mockPolytopicConstruction.view()
//                view {
//                    blackhole.consume(listOfMockVertices)
//                }
//                blackhole.consume(view)
//            }
//        }
//
//        @Benchmark
//        fun giftWrapping(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                val view = mockPolytopicConstruction.view()
//                view {
//                    blackhole.consume(listOfMockVertices.constructConvexHullByGiftWrapping())
//                }
//                blackhole.consume(view)
//            }
//        }
//    }
//
//    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
//    @State(Scope.Benchmark)
//    @OptIn(DelicatePolytopicConstructionAPI::class)
//    class MultidimensionalConvexHullBenchmarksUnsafe {
//        @Param(
//            "test"
//        )
//        var testId: String = ""
//
//        var spaceDimension: UInt = 0u
//
//        var input: KoneList<Point<Double>> = emptyKoneList()
//
//        @Setup
//        fun setup() {
//            val resource = this.javaClass.getResource("/RealValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
//            val parsedInput: PointSetDescription<Double> = Yaml.default.decodeFromString(resource.readText())
//            spaceDimension = parsedInput.dim
//            input = parsedInput.points
//        }
//
//        @Benchmark
//        fun empty(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                val polytopicConstruction = buildUnsafeAbstractPolytopicConstruction(
//                    spaceDimension = spaceDimension,
//                    numberContext = numberContext,
//                ) {
//                    blackhole.consume(input.map { addVertex(it) })
//                }
//                blackhole.consume(polytopicConstruction)
//            }
//        }
//
//        @Benchmark
//        fun giftWrapping(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                val polytopicConstruction = buildUnsafeAbstractPolytopicConstruction(
//                    spaceDimension = spaceDimension,
//                    numberContext = numberContext,
//                ) {
//                    blackhole.consume(input.map { addVertex(it) }.constructConvexHullByGiftWrapping())
//                }
//                blackhole.consume(polytopicConstruction)
//            }
//        }
//    }
//
//    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
//    @State(Scope.Benchmark)
//    class MultidimensionalConvexHullBenchmarksFair {
//        @Param(
//            "test"
//        )
//        var testId: String = ""
//
//        var spaceDimension: UInt = 0u
//
//        var input: KoneList<Point<Double>> = emptyKoneList()
//
//        @Setup
//        fun setup() {
//            val resource = this.javaClass.getResource("/RealValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
//            val parsedInput: PointSetDescription<Double> = Yaml.default.decodeFromString(resource.readText())
//            spaceDimension = parsedInput.dim
//            input = parsedInput.points
//        }
//
//        @Benchmark
//        fun empty(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                val polytopicConstruction = buildAbstractPolytopicConstruction(
//                    spaceDimension = spaceDimension,
//                    numberContext = numberContext,
//                ) {
//                    blackhole.consume(input.map { addVertex(it) })
//                }
//                blackhole.consume(polytopicConstruction)
//            }
//        }
//
//        @Benchmark
//        fun giftWrapping(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                val polytopicConstruction = buildAbstractPolytopicConstruction(
//                    spaceDimension = spaceDimension,
//                    numberContext = numberContext,
//                ) {
//                    blackhole.consume(input.map { addVertex(it) }.constructConvexHullByGiftWrapping())
//                }
//                blackhole.consume(polytopicConstruction)
//            }
//        }
//    }
//
//    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
//    @State(Scope.Benchmark)
//    @OptIn(DelicatePolytopicConstructionAPI::class)
//    class MultidimensionalConvexHullBenchmarksUnsafeDangerous {
//        @Param(
//            "test"
//        )
//        var testId: String = ""
//
//        var spaceDimension: UInt = 0u
//
//        var input: KoneList<Point<Double>> = emptyKoneList()
//
//        var polytopicConstruction: dev.lounres.kone.computationalGeometry.polytopes.MutableAbstractPolytopicConstruction<Double>? = null
//
//        var inputVertices: KoneList<AbstractVertex>? = null
//
//        @Setup
//        fun setup() {
//            val resource = this.javaClass.getResource("/RealValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
//            val parsedInput: PointSetDescription<Double> = Yaml.default.decodeFromString(resource.readText())
//            spaceDimension = parsedInput.dim
//            input = parsedInput.points
//        }
//
//        @Setup(Level.Invocation)
//        fun init() {
//            polytopicConstruction = UnsafeMutableAbstractPolytopicConstruction(
//                spaceDimension = spaceDimension,
//                numberContext = numberContext,
//            ).apply {
//                inputVertices = input.map { addVertex(it) }
//            }
//        }
//
//        @Benchmark
//        fun empty(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                polytopicConstruction!! {
//                    blackhole.consume(inputVertices!!)
//                }
//            }
//        }
//
//        @Benchmark
//        fun giftWrapping(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                polytopicConstruction!! {
//                    blackhole.consume(inputVertices!!.constructConvexHullByGiftWrapping())
//                }
//            }
//        }
//    }
//
//    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
//    @State(Scope.Benchmark)
//    class MultidimensionalConvexHullBenchmarksFairDangerous {
//        @Param(
//            "test"
//        )
//        var testId: String = ""
//
//        var spaceDimension: UInt = 0u
//
//        var input: KoneList<Point<Double>> = emptyKoneList()
//
//        var polytopicConstruction: dev.lounres.kone.computationalGeometry.polytopes.MutableAbstractPolytopicConstruction<Double>? = null
//
//        var inputVertices: KoneList<AbstractVertex>? = null
//
//        @Setup
//        fun setup() {
//            val resource = this.javaClass.getResource("/RealValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
//            val parsedInput: PointSetDescription<Double> = Yaml.default.decodeFromString(resource.readText())
//            spaceDimension = parsedInput.dim
//            input = parsedInput.points
//        }
//
//        @Setup(Level.Invocation)
//        fun init() {
//            polytopicConstruction = MutableAbstractPolytopicConstruction(
//                spaceDimension = spaceDimension,
//                numberContext = numberContext,
//            ).apply {
//                inputVertices = input.map { addVertex(it) }
//            }
//        }
//
//        @Benchmark
//        fun empty(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                polytopicConstruction!! {
//                    blackhole.consume(inputVertices!!)
//                }
//            }
//        }
//
//        @Benchmark
//        fun giftWrapping(blackhole: Blackhole) {
//            euclideanSpaceScope {
//                polytopicConstruction!! {
//                    blackhole.consume(inputVertices!!.constructConvexHullByGiftWrapping())
//                }
//            }
//        }
//    }
//}