package jvm

import dev.lounres.kone.misc.benchmarks.runners.*


@KoneBenchmarkRunner
fun doIt(input: String) {
    println("Hello from jvm runner! You said:\n${input}")
}