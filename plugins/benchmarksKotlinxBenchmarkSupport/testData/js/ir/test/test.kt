// FILE: benchmark1.kt
package foo.bar

import kotlinx.benchmark.*
import kotlin.math.tanh


var value = 57.179

@State(Scope.Benchmark)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
class MyBenchmark {
    @Param("5", "7")
    var times = 0u
    
    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        for (_ in 0u ..< times) blackhole.consume(tanh(value))
    }
}

// FILE: run.kt
package dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.generated

import kotlinx.benchmark.js.*
import kotlinx.benchmark.*
import foo.bar.MyBenchmark


@OptIn(kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi::class)
fun main(args: Array<String>) {
    println("Hello, IR-generated benchmarks!")
    runBenchmarks("js", args) { executor ->
        val suite = SuiteDescriptor<MyBenchmark>(
            name = "foo.bar.MyBenchmark",
            factory = { MyBenchmark() },
            parametrize = { benchmark, parameters ->
                benchmark.times = parameters["times"]!!.toUInt()
            },
            setup = {},
            teardown = {},
            parameters = listOf(
                "times",
            ),
            defaultParameters = mapOf(
                Pair(
                    "times",
                    listOf(
                        "5",
                        "7",
                    ),
                ),
            ),
            iterations = 3,
            warmups = 5,
            iterationTime = IterationTime(
                value = 1,
                timeUnit = BenchmarkTimeUnit.SECONDS,
            ),
            outputTimeUnit = BenchmarkTimeUnit.MILLISECONDS,
        )
        suite.add(
            benchmark = JsBenchmarkDescriptorWithBlackholeParameter(
                name = "foo.bar.MyBenchmark.benchmark",
                suite = suite,
                blackhole = Blackhole(),
                function = MyBenchmark::benchmark,
            )
        )
        executor.suite(suite)
        executor.run()
    }
}