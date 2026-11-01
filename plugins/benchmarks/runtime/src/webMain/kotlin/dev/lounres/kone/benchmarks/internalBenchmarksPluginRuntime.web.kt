/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks

import kotlin.reflect.KClass


@Deprecated(message = "Internal benchmarks API.", level = DeprecationLevel.HIDDEN)
public actual fun runBenchmark(
    benchmarkClass: KClass<*>,
    parameters: Map<String, String>,
    configuration: BenchmarkConfiguration,
): BenchmarkResult = TODO("Not yet implemented")