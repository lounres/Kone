package foo.bar

import dev.lounres.kone.benchmarks.*
import kotlinx.benchmark.Blackhole
import kotlinx.serialization.*
import kotlin.math.tanh


@Serializable
enum class Action {
    Tanh57179 {
        override fun run(blackhole: Blackhole) {
            blackhole.consume(tanh(57.179))
        }
    },
    ;
    abstract fun run(blackhole: Blackhole)
}

@Benchmark
fun myLovelyBenchmark(
    action: Action,
    times: UInt,
    benchmarkConfiguration: BenchmarkConfiguration,
): BenchmarkResult {
    for (_ in 0u ..< inject { times * 2u }) action.run(blackhole)
    return benchmarkResult
}

fun box(): String {
    val _ = myLovelyBenchmark(
        action = Action.Tanh57179,
        times = 57179u,
        benchmarkConfiguration = BenchmarkConfiguration(
        
        )
    )
    return "OK"
}