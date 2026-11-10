/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.jmh

import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder


private data object JmhBenchmarkExecutor : KoneBenchmarkExecutor<JmhConfiguration, JmhBenchmark, JmhResult> {
    override fun execute(configuration: JmhConfiguration, benchmark: JmhBenchmark): JmhResult {
        val runResult = Runner(
            OptionsBuilder()
                .parent(configuration)
                .include(benchmark.name)
                .let {
                    var builder = it
                    for ((parameterName = key, parameterValue = value) in benchmark.parameters) {
                        builder = builder.param(parameterName, parameterValue)
                    }
                    builder
                }
                .build()
        ).runSingle()
        return runResult
    }
}

public val KoneBenchmarkExecutor.Companion.jmh: KoneBenchmarkExecutor<JmhConfiguration, JmhBenchmark, JmhResult>
    get() = JmhBenchmarkExecutor