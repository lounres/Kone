package common

import dev.lounres.kone.misc.benchmarks.runners.*


@KoneBenchmarkRunner
fun doIt(input: String) {
    println("Hello from common runner! You said:\n${input}")
}