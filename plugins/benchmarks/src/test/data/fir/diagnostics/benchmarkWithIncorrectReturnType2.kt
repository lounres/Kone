package foo.bar

import dev.lounres.kone.benchmarks.*
import kotlinx.benchmark.Blackhole


@Benchmark
fun myLovelyBenchmark(
    benchmarkConfiguration: BenchmarkConfiguration,
): <!BENCHMARK_INCORRECT_RETURN_TYPE!>String<!> = <!BENCHMARK_RESULT_IS_NOT_RETURNED_BY_BENCHMARK!>"Hello, Kone!"<!>
