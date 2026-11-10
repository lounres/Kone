/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.declarative


@Target(FUNCTION)
public annotation class KoneBenchmarkExecutionDeclaration

public fun interface KoneBenchmarkExecutionDeclarationContext<Configuration, Injection, Result> : KoneBenchmarkInjectionContext<Injection> {
    public fun runBenchmark(configuration: Configuration, description: KoneGeneratedBenchmarkDescription<Injection>): Result
    
    public companion object;
}

public val <Result> KoneBenchmarkExecutionDeclarationContext<*, *, Result>.benchmarkResult: Result get() = error("... intrinsic ...")

context(_: KoneBenchmarkExecutionDeclarationContext<*, *, Result>)
public val <Result> benchmarkResult: Result get() = error("... intrinsic ...")