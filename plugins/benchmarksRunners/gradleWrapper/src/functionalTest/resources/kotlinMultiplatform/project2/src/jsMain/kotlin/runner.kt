package js

import dev.lounres.kone.misc.benchmarks.runners.*


@KoneBenchmarkRunner
fun doIt(input: String) {
    println("Hello from js runner! You said:\n${input}")
}