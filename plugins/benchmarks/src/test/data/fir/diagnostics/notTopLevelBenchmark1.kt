package foo.bar

import dev.lounres.kone.benchmarks.*
import kotlinx.benchmark.Blackhole


object Foo {
    <!NON_TOP_LEVEL_BENCHMARK!>@Benchmark<!>
    fun myLovelyBenchmark(): BenchmarkResult = <!INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS!>benchmarkResult<!>
}