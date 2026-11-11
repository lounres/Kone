/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark.declarative

import dev.lounres.kone.misc.benchmarks.declarative.KoneBenchmarkDeclarationContext
import dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark.KotlinxBenchmarkBenchmark
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi


@OptIn(KotlinxBenchmarkRuntimeInternalApi::class)
public actual val KoneBenchmarkDeclarationContext.Companion.kotlinxBenchmark: KoneBenchmarkDeclarationContext<KotlinxBenchmarkInjection, KotlinxBenchmarkBenchmark>
    get() = TODO("Not yet implemented")