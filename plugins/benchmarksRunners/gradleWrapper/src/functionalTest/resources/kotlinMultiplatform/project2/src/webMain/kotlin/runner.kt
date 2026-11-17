package web

import dev.lounres.kone.misc.benchmarks.runners.*


@KoneBenchmarkRunner
fun doIt(input: String) {
    println("Hello from web runner! You said:\n${input}")
}