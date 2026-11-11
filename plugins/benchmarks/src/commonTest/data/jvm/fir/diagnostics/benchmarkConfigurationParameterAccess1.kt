package foo.bar

import dev.lounres.kone.benchmarks.*
import kotlinx.benchmark.Blackhole


@Benchmark
fun myLovelyBenchmark(
    benchmarkConfiguration: BenchmarkConfiguration,
): BenchmarkResult {
    <!BENCHMARK_CONFIGURATION_PARAMETER_ACCESS, UNUSED_EXPRESSION!>benchmarkConfiguration<!>
    return benchmarkResult
}