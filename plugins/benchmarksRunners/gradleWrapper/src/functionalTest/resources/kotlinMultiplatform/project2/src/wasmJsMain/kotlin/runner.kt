package wasmJs

import dev.lounres.kone.misc.benchmarks.runners.*


@KoneBenchmarkRunner
fun doIt(input: String) {
    println("Hello from wasmJs runner! You said:\n${input}")
}