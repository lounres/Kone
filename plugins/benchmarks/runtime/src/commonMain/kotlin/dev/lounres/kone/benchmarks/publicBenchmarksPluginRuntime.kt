/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks

import kotlinx.benchmark.Blackhole

@Target(FUNCTION)
public annotation class Benchmark

public val blackhole: Blackhole get() = error("... intrinsic ...")

public inline fun <T> inject(crossinline block: () -> T): T {
    error("... intrinsic ...")
}

public interface DisposableInjection<out T> {
    public val value: T
    public fun dispose()
}

public fun <T> DisposableInjection(value: T, dispose: () -> Unit): DisposableInjection<T> =
    object : DisposableInjection<T> {
        override val value: T = value
        override fun dispose() { dispose() }
    }

public inline fun <T> injectDisposable(crossinline block: () -> DisposableInjection<T>): T {
    error("... intrinsic ...")
}

// TODO: Add ability to inject functions, properties, and classes. Not only benchmark parameters.
//@Target(FUNCTION, PROPERTY, CLASS)
//public annotation class Inject

// TODO: Add configuration classes

public class BenchmarkConfiguration

// TODO: Add results classes

public class BenchmarkResult

public val benchmarkResult: BenchmarkResult get() = error("... intrinsic ...")