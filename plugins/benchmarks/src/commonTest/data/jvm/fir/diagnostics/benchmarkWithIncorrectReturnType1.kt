package foo.bar

import dev.lounres.kone.benchmarks.*
import kotlinx.benchmark.Blackhole


@Benchmark
fun <!BENCHMARK_INCORRECT_RETURN_TYPE!>myLovelyBenchmark<!>(
    benchmarkConfiguration: BenchmarkConfiguration,
) {}