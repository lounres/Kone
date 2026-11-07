package foo.bar

import dev.lounres.kone.benchmarks.*
import kotlinx.benchmark.Blackhole
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
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
    val actionSerialized = Json.encodeToString(action)
    val timesSerialized = Json.encodeToString(times)
    return runBenchmark(
        benchmarkClass = KoneBenchmarkForMyLovelyBenchmark::class,
        parameters = mapOf(
            Pair("action", actionSerialized),
            Pair("times", timesSerialized),
        ),
        configuration = benchmarkConfiguration,
    )
}

@kotlinx.benchmark.State(kotlinx.benchmark.Scope.Benchmark)
class KoneBenchmarkForMyLovelyBenchmark {
    @kotlinx.benchmark.Param("")
    var action: String = ""
    var actionDeserialized: Action? = null
    
    @kotlinx.benchmark.Param("")
    var times: String = ""
    var timesDeserialized: UInt? = null
    
    var injection1: InjectedValue<UInt>? = null
    
    @kotlinx.benchmark.Setup
    fun setup() {
        val actionDeserialized: Action = Json.decodeFromString(action)
        this.actionDeserialized = actionDeserialized
        val timesDeserialized: UInt = Json.decodeFromString(times)
        this.timesDeserialized = timesDeserialized
        injection1 = InjectedValue(
            value = run { timesDeserialized * 2u },
            disposableInjection = null,
        )
    }
    
    @kotlinx.benchmark.Benchmark
    fun benchmark(blackhole: Blackhole) {
        val action = actionDeserialized!!
        val times = timesDeserialized!!
        val injection1 = injection1!!.value
        for (_ in 0u ..< injection1) action.run(blackhole)
    }
    
    @kotlinx.benchmark.TearDown
    fun tearDown() {
        injection1 = null
        timesDeserialized = null
        times = ""
        actionDeserialized = null
        action = ""
    }
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