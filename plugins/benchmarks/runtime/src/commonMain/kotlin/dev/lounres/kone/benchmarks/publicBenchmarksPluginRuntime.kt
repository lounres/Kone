/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks

import kotlinx.benchmark.Blackhole
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Target(FUNCTION)
public annotation class Benchmark

public val blackhole: Blackhole get() = benchmarksPluginExceptionForRuntimeDeclarations()

public inline fun <T> inject(crossinline block: () -> T): T {
    benchmarksPluginExceptionForRuntimeDeclarations()
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
    benchmarksPluginExceptionForRuntimeDeclarations()
}

// TODO: Add ability to inject functions, properties, and classes. Not only benchmark parameters.
//@Target(FUNCTION, PROPERTY, CLASS)
//public annotation class Inject

// TODO: Add configuration classes

@Serializable
public expect sealed interface BenchmarkModeConfiguration {
    @Serializable
    public class Throughput: BenchmarkModeConfiguration {
        public val iterationTime: Duration
    }
    @Serializable
    public class AverageTime: BenchmarkModeConfiguration {
        public val iterationTime: Duration
    }
}

@Serializable
public expect class BenchmarkConfiguration {
    public val numberOfWarmups: UInt
    public val numberOfMeasurements: UInt
    public val mode: BenchmarkModeConfiguration
    
    public companion object;
}

public expect fun BenchmarkConfiguration(
    numberOfWarmups: UInt,
    numberOfMeasurements: UInt,
    mode: BenchmarkModeConfiguration,
): BenchmarkConfiguration

// TODO: Add results classes

@Serializable
public expect sealed interface BenchmarkModeResult<Configuration: BenchmarkModeConfiguration> {
    public val configuration: Configuration
    
    @Serializable
    public class Throughput: BenchmarkModeResult<BenchmarkModeConfiguration.Throughput> {
        override val configuration: BenchmarkModeConfiguration.Throughput
        public val operationsPerIteration: Double
    }
    
    @Serializable
    public class AverageTime: BenchmarkModeResult<BenchmarkModeConfiguration.AverageTime> {
        override val configuration: BenchmarkModeConfiguration.AverageTime
        public val operationDuration: Duration
    }
}

@Serializable
public expect class BenchmarkResult {
    public val configuration: BenchmarkConfiguration
}

public val benchmarkResult: BenchmarkResult get() = benchmarksPluginExceptionForRuntimeDeclarations()