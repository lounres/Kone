/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Mode


public data class KotlinxBenchmarkConfiguration(
    val name: String,
    val reportFile: String,
    val traceFormat: String,
    val reportFormat: String?,
    val params: Map<String, List<String>>,
    val include: List<String>,
    val exclude: List<String>,
    val iterations: Int?,
    val warmups: Int?,
    val iterationTime: Long?,
    val iterationTimeUnit: BenchmarkTimeUnit?,
    val advanced: Map<String, String>,
    val outputTimeUnit: BenchmarkTimeUnit?,
    val threads: Int?,
    val mode: Mode?,
    val compilationMode: String?,
    val configurationName: String,
)