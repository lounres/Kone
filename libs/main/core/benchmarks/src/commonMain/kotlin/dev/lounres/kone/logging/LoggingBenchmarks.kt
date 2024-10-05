/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.logging

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State


@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class LoggingBenchmarks {
    @Benchmark
    fun simple() {
        // TODO
//        koneLogger.debug("dev.lounres.kone.logging.LoggingBenchmarks.simple") { "Simple message" }
    }
}