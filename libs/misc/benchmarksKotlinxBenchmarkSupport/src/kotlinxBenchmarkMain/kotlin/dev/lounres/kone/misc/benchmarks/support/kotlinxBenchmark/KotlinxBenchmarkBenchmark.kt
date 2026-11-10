/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import kotlinx.benchmark.SuiteDescriptor
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi


@OptIn(KotlinxBenchmarkRuntimeInternalApi::class)
public typealias KotlinxBenchmarkBenchmark = SuiteDescriptor<*>