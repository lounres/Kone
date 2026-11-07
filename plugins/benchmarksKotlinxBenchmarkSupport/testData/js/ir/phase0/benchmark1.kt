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