/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi
import kotlinx.benchmark.native.runBenchmarks


@OptIn(KotlinxBenchmarkRuntimeInternalApi::class)
private data object KotlinxBenchmarkBenchmarkExecutor : KoneBenchmarkExecutor<KotlinxBenchmarkConfiguration, KotlinxBenchmarkBenchmark, KotlinxBenchmarkResult> {
    override fun execute(configuration: KotlinxBenchmarkConfiguration, benchmark: KotlinxBenchmarkBenchmark) {
        runBenchmarks(
            name = "mingwX64",
            args = TODO(),
        ) {
            it.suite(benchmark)
            it.run()
        }
    }
}

@OptIn(KotlinxBenchmarkRuntimeInternalApi::class)
public actual val KoneBenchmarkExecutor.Companion.kotlinxBenchmark: KoneBenchmarkExecutor<KotlinxBenchmarkConfiguration, KotlinxBenchmarkBenchmark, KotlinxBenchmarkResult>
    get() = TODO("Not yet implemented")