package foo.bar

import dev.lounres.kone.benchmarks.*
import kotlinx.benchmark.Blackhole


fun myLovelyBenchmark(
    benchmarkConfiguration: BenchmarkConfiguration,
) {
    <!INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS!>inject<!> { }
    <!INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS!>blackhole<!>
    <!INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS!>benchmarkResult<!>
}