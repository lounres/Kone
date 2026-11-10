/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark.declarative

import dev.lounres.kone.misc.benchmarks.declarative.KoneBenchmarkExecutionDeclarationContext
import dev.lounres.kone.misc.benchmarks.declarative.KoneGeneratedBenchmarkDescription
import dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark.KotlinxBenchmarkConfiguration
import dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark.KotlinxBenchmarkResult
import kotlinx.benchmark.BenchmarkDescriptorWithBlackholeParameter
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.IterationTime
import kotlinx.benchmark.SuiteDescriptor
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi
import kotlinx.benchmark.native.runBenchmarks


@OptIn(KotlinxBenchmarkRuntimeInternalApi::class)
private data object KotlinxBenchmarkBenchmarkExecutionDeclarationContext : KoneBenchmarkExecutionDeclarationContext<KotlinxBenchmarkConfiguration, KotlinxBenchmarkInjection, KotlinxBenchmarkResult> {
    override fun runBenchmark(configuration: KotlinxBenchmarkConfiguration, description: KoneGeneratedBenchmarkDescription<KotlinxBenchmarkInjection>) {
        runBenchmarks(
            name = "mingwX64",
            args = TODO(),
        ) {
            val suiteDescriptor = SuiteDescriptor(
                name = description.name,
                factory = { description },
                parametrize = { _, _ -> },
                setup = { it.beforeBenchmark() },
                teardown = { it.afterBenchmark() },
                parameters = emptyList(),
                defaultParameters = emptyMap(),
                iterations = 0,
                warmups = 0,
                iterationTime = IterationTime(0, SECONDS),
                outputTimeUnit = SECONDS,
                mode = AverageTime
            )
            suiteDescriptor.add(
                BenchmarkDescriptorWithBlackholeParameter(
                    name = description.name,
                    suite = suiteDescriptor,
                    blackhole = Blackhole(),
                    function = { runBenchmark(it) }
                )
            )
            it.suite(suiteDescriptor)
            it.run()
        }
    }
}

public actual val KoneBenchmarkExecutionDeclarationContext.Companion.kotlinxBenchmark: KoneBenchmarkExecutionDeclarationContext<KotlinxBenchmarkConfiguration, KotlinxBenchmarkInjection, KotlinxBenchmarkResult>
    get() = KotlinxBenchmarkBenchmarkExecutionDeclarationContext