/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.declarative


@Target(FUNCTION)
public annotation class KoneBenchmarkDeclaration

public fun interface KoneBenchmarkDeclarationContext<out Injection, out Benchmark> : KoneBenchmarkInjectionContext<Injection> {
    public fun assembleBenchmark(description: KoneGeneratedBenchmarkDescription<Injection>): Benchmark
    
    public companion object;
}

public val <Benchmark> KoneBenchmarkDeclarationContext<*, Benchmark>.newBenchmark: Benchmark get() = error("... intrinsic ...")

context(_: KoneBenchmarkDeclarationContext<*, Benchmark>)
public val <Benchmark> newBenchmark: Benchmark get() = error("... intrinsic ...")