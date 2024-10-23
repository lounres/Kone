/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import com.charleskorn.kaml.Yaml
import dev.lounres.kone.algebraic.BigIntegerRing
import dev.lounres.kone.algebraic.ring
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.emptyKoneIterableList
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.computationalGeometry.algorithms.constructConvexHullByGiftWrapping
import dev.lounres.kone.computationalGeometry.util.MockAbstractPolytopicConstruction
import dev.lounres.kone.computationalGeometry.util.PointSetDescription
import dev.lounres.kone.computationalGeometry.utils.map
import dev.lounres.kone.context.invoke
import kotlinx.benchmark.*
import kotlinx.serialization.decodeFromString
import org.openjdk.jmh.annotations.Level
import java.math.BigInteger


class LongRingBenchmarks {
    companion object {
        val numberContext = Long.ring
        val euclideanSpaceScope = numberContext.euclideanKategoryScope
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    class MultidimensionalConvexHullBenchmarksMock {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<Long>> = emptyKoneIterableList()

        val mockPolytopicConstruction by lazy {
            MockAbstractPolytopicConstruction(
                spaceDimension = spaceDimension,
                numberContext = numberContext,
            )
        }
        var listOfMockVertices: KoneIterableList<AbstractVertex> = emptyKoneIterableList()

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points
            mockPolytopicConstruction {
                listOfMockVertices = input.map { addVertex(it) }
            }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                val view = mockPolytopicConstruction.view()
                view {
                    blackhole.consume(listOfMockVertices)
                }
                blackhole.consume(view)
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                val view = mockPolytopicConstruction.view()
                view {
                    blackhole.consume(listOfMockVertices.constructConvexHullByGiftWrapping())
                }
                blackhole.consume(view)
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    @OptIn(DelicatePolytopicConstructionAPI::class)
    class MultidimensionalConvexHullBenchmarksUnsafe {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<Long>> = emptyKoneIterableList()

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildUnsafeAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) })
                }
                blackhole.consume(polytopicConstruction)
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildUnsafeAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) }.constructConvexHullByGiftWrapping())
                }
                blackhole.consume(polytopicConstruction)
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    class MultidimensionalConvexHullBenchmarksFair {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<Long>> = emptyKoneIterableList()

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) })
                }
                blackhole.consume(polytopicConstruction)
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) }.constructConvexHullByGiftWrapping())
                }
                blackhole.consume(polytopicConstruction)
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    @OptIn(DelicatePolytopicConstructionAPI::class)
    class MultidimensionalConvexHullBenchmarksUnsafeDangerous {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<Long>> = emptyKoneIterableList()

        var polytopicConstruction: MutableAbstractPolytopicConstruction<Long>? = null

        var inputVertices: KoneIterableList<AbstractVertex>? = null

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points
        }

        @Setup(Level.Invocation)
        fun init() {
            polytopicConstruction = UnsafeMutableAbstractPolytopicConstruction(
                spaceDimension = spaceDimension,
                numberContext = numberContext,
            ).apply {
                inputVertices = input.map { addVertex(it) }
            }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!)
                }
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!.constructConvexHullByGiftWrapping())
                }
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    class MultidimensionalConvexHullBenchmarksFairDangerous {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<Long>> = emptyKoneIterableList()

        var polytopicConstruction: MutableAbstractPolytopicConstruction<Long>? = null

        var inputVertices: KoneIterableList<AbstractVertex>? = null

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points
        }

        @Setup(Level.Invocation)
        fun init() {
            polytopicConstruction = MutableAbstractPolytopicConstruction(
                spaceDimension = spaceDimension,
                numberContext = numberContext,
            ).apply {
                inputVertices = input.map { addVertex(it) }
            }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!)
                }
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!.constructConvexHullByGiftWrapping())
                }
            }
        }
    }
}

class BigIntegerRingBenchmarks {
    companion object {
        val numberContext = BigIntegerRing
        val euclideanSpaceScope = numberContext.euclideanKategoryScope
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    class MultidimensionalConvexHullBenchmarksMock {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<BigInteger>> = emptyKoneIterableList()

        val mockPolytopicConstruction by lazy {
            MockAbstractPolytopicConstruction(
                spaceDimension = spaceDimension,
                numberContext = numberContext,
            )
        }
        var listOfMockVertices: KoneIterableList<AbstractVertex> = emptyKoneIterableList()

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points.map { it.map { it.toBigInteger() } }
            mockPolytopicConstruction {
                listOfMockVertices = input.map { addVertex(it) }
            }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                val view = mockPolytopicConstruction.view()
                view {
                    blackhole.consume(listOfMockVertices)
                }
                blackhole.consume(view)
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                val view = mockPolytopicConstruction.view()
                view {
                    blackhole.consume(listOfMockVertices.constructConvexHullByGiftWrapping())
                }
                blackhole.consume(view)
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    @OptIn(DelicatePolytopicConstructionAPI::class)
    class MultidimensionalConvexHullBenchmarksUnsafe {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<BigInteger>> = emptyKoneIterableList()

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points.map { it.map { it.toBigInteger() } }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildUnsafeAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) })
                }
                blackhole.consume(polytopicConstruction)
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildUnsafeAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) }.constructConvexHullByGiftWrapping())
                }
                blackhole.consume(polytopicConstruction)
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    class MultidimensionalConvexHullBenchmarksFair {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<BigInteger>> = emptyKoneIterableList()

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points.map { it.map { it.toBigInteger() } }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) })
                }
                blackhole.consume(polytopicConstruction)
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                val polytopicConstruction = buildAbstractPolytopicConstruction(
                    spaceDimension = spaceDimension,
                    numberContext = numberContext,
                ) {
                    blackhole.consume(input.map { addVertex(it) }.constructConvexHullByGiftWrapping())
                }
                blackhole.consume(polytopicConstruction)
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    @OptIn(DelicatePolytopicConstructionAPI::class)
    class MultidimensionalConvexHullBenchmarksUnsafeDangerous {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<BigInteger>> = emptyKoneIterableList()

        var polytopicConstruction: MutableAbstractPolytopicConstruction<BigInteger>? = null

        var inputVertices: KoneIterableList<AbstractVertex>? = null

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points.map { it.map { it.toBigInteger() } }
        }

        @Setup(Level.Invocation)
        fun init() {
            polytopicConstruction = UnsafeMutableAbstractPolytopicConstruction(
                spaceDimension = spaceDimension,
                numberContext = numberContext,
            ).apply {
                inputVertices = input.map { addVertex(it) }
            }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!)
                }
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!.constructConvexHullByGiftWrapping())
                }
            }
        }
    }

    @OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
    @State(Scope.Benchmark)
    class MultidimensionalConvexHullBenchmarksFairDangerous {
        @Param(
            "test"
        )
        var testId: String = ""

        var spaceDimension: UInt = 0u

        var input: KoneIterableList<Point<BigInteger>> = emptyKoneIterableList()

        var polytopicConstruction: MutableAbstractPolytopicConstruction<BigInteger>? = null

        var inputVertices: KoneIterableList<AbstractVertex>? = null

        @Setup
        fun setup() {
            val resource = this.javaClass.getResource("/IntegerValuedMultidimensionalConvexHullBenchmarks/$testId.yaml")!!
            val parsedInput: PointSetDescription<Long> = Yaml.default.decodeFromString(resource.readText())
            spaceDimension = parsedInput.dim
            input = parsedInput.points.map { it.map { it.toBigInteger() } }
        }

        @Setup(Level.Invocation)
        fun init() {
            polytopicConstruction = MutableAbstractPolytopicConstruction(
                spaceDimension = spaceDimension,
                numberContext = numberContext,
            ).apply {
                inputVertices = input.map { addVertex(it) }
            }
        }

        @Benchmark
        fun empty(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!)
                }
            }
        }

        @Benchmark
        fun giftWrapping(blackhole: Blackhole) {
            euclideanSpaceScope {
                polytopicConstruction!! {
                    blackhole.consume(inputVertices!!.constructConvexHullByGiftWrapping())
                }
            }
        }
    }
}