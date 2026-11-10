/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.jmh.declarative

import dev.lounres.kone.misc.benchmarks.declarative.KoneBenchmarkDeclarationContext
import dev.lounres.kone.misc.benchmarks.declarative.KoneGeneratedBenchmarkDescription
import dev.lounres.kone.misc.benchmarks.support.jmh.JmhBenchmark
import dev.lounres.kone.misc.benchmarks.support.jmh.declarative.internal.JmhDeclarativeBenchmarksGenerationInternal
import dev.lounres.kone.misc.benchmarks.support.jmh.declarative.internal.JmhGeneratedBenchmarkDescription


@OptIn(JmhDeclarativeBenchmarksGenerationInternal::class)
private data object JmhBenchmarkDeclarationContext : KoneBenchmarkDeclarationContext<JmhInjection, JmhBenchmark> {
    override fun assembleBenchmark(description: KoneGeneratedBenchmarkDescription<JmhInjection>): JmhBenchmark {
        if (description !is JmhGeneratedBenchmarkDescription) error("... jmh plugin have not done its work ...")
        return description.jmhBenchmark
    }
}

public val KoneBenchmarkDeclarationContext.Companion.jmh: KoneBenchmarkDeclarationContext<JmhInjection, JmhBenchmark>
    get() = JmhBenchmarkDeclarationContext