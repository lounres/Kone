/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.jmh.declarative

import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor
import dev.lounres.kone.misc.benchmarks.declarative.KoneBenchmarkExecutionDeclarationContext
import dev.lounres.kone.misc.benchmarks.declarative.KoneGeneratedBenchmarkDescription
import dev.lounres.kone.misc.benchmarks.support.jmh.JmhConfiguration
import dev.lounres.kone.misc.benchmarks.support.jmh.JmhResult
import dev.lounres.kone.misc.benchmarks.support.jmh.declarative.internal.JmhDeclarativeBenchmarksGenerationInternal
import dev.lounres.kone.misc.benchmarks.support.jmh.declarative.internal.JmhGeneratedBenchmarkDescription
import dev.lounres.kone.misc.benchmarks.support.jmh.jmh


@OptIn(JmhDeclarativeBenchmarksGenerationInternal::class)
private data object JmhBenchmarkExecutionDeclarationContext : KoneBenchmarkExecutionDeclarationContext<JmhConfiguration, JmhInjection, JmhResult> {
    override fun runBenchmark(configuration: JmhConfiguration, description: KoneGeneratedBenchmarkDescription<JmhInjection>): JmhResult {
        if (description !is JmhGeneratedBenchmarkDescription) error("... jmh plugin have not done its work ...")
        return KoneBenchmarkExecutor.jmh.execute(configuration, description.jmhBenchmark)
    }
}

public val KoneBenchmarkExecutionDeclarationContext.Companion.jmh: KoneBenchmarkExecutionDeclarationContext<JmhConfiguration, JmhInjection, JmhResult>
    get() = JmhBenchmarkExecutionDeclarationContext