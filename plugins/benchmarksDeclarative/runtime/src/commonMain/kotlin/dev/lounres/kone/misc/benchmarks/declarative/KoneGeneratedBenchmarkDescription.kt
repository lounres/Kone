/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.declarative


public interface KoneGeneratedBenchmarkDescription<in Injection> {
    public val name: String
    public fun beforeBenchmark() {}
    public fun runBenchmark(injection: Injection)
    public fun afterBenchmark() {}
}