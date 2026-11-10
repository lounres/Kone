/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.jmh.declarative.internal

import dev.lounres.kone.misc.benchmarks.declarative.KoneGeneratedBenchmarkDescription
import dev.lounres.kone.misc.benchmarks.support.jmh.JmhBenchmark
import dev.lounres.kone.misc.benchmarks.support.jmh.declarative.JmhInjection


@JmhDeclarativeBenchmarksGenerationInternal
public interface JmhGeneratedBenchmarkDescription : KoneGeneratedBenchmarkDescription<JmhInjection> {
    public val jmhBenchmark: JmhBenchmark
}