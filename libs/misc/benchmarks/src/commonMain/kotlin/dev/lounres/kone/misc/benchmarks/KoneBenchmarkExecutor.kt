/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks


public fun interface KoneBenchmarkExecutor<in Configuration, in Benchmark, out Result> {
    public fun execute(configuration: Configuration, benchmark: Benchmark): Result
    
    public companion object;
}