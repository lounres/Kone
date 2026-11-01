/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks

import kotlinx.serialization.Serializable
import kotlin.time.Duration


// TODO: Think about profilers

@Serializable
public actual sealed interface BenchmarkModeConfiguration {
    @Serializable
    public actual data class Throughput(
        public actual val iterationTime: Duration,
    ): BenchmarkModeConfiguration
    @Serializable
    public actual data class AverageTime(
        public actual val iterationTime: Duration,
    ): BenchmarkModeConfiguration
    @Serializable
    public data class SampleTime(
        public val iterationTime: Duration,
    ): BenchmarkModeConfiguration
//    @Serializable
//    public data object SingleShotTime: BenchmarkModeConfiguration
}

@Serializable
public actual data class BenchmarkConfiguration(
    public actual val numberOfWarmups: UInt,
    public actual val numberOfMeasurements: UInt,
    public actual val mode: BenchmarkModeConfiguration,
    public val numberOfForks: UInt = DEFAULT_NUMBER_OF_FORKS,
    public val forceGarbageCollectionBetweenIterations: Boolean = DEFAULT_FORCE_GARBAGE_COLLECTION_BETWEEN_ITERATIONS,
) {
    public actual companion object {
        public const val DEFAULT_NUMBER_OF_FORKS: UInt = 0u
        public const val DEFAULT_FORCE_GARBAGE_COLLECTION_BETWEEN_ITERATIONS: Boolean = false
    }
}

public actual fun BenchmarkConfiguration(
    numberOfWarmups: UInt,
    numberOfMeasurements: UInt,
    mode: BenchmarkModeConfiguration,
) : BenchmarkConfiguration = BenchmarkConfiguration(
    numberOfWarmups = numberOfWarmups,
    numberOfMeasurements = numberOfMeasurements,
    mode = mode,
    numberOfForks = BenchmarkConfiguration.DEFAULT_NUMBER_OF_FORKS,
    forceGarbageCollectionBetweenIterations = BenchmarkConfiguration.DEFAULT_FORCE_GARBAGE_COLLECTION_BETWEEN_ITERATIONS
)

public data class BenchmarkJvmEnvironment(
    public val jvm: String,
    public val jvmArgs: List<String>,
    public val jdkVersion: String,
    public val jmhVersion: String,
    public val vmName: String,
    public val vmVersion: String,
)

@Serializable
public actual sealed interface BenchmarkModeResult<Configuration: BenchmarkModeConfiguration> {
    public actual val configuration: Configuration
    
    @Serializable
    public actual data class Throughput(
        actual override val configuration: BenchmarkModeConfiguration.Throughput,
        public actual val operationsPerIteration: Double,
    ): BenchmarkModeResult<BenchmarkModeConfiguration.Throughput>
    
    @Serializable
    public actual data class AverageTime(
        actual override val configuration: BenchmarkModeConfiguration.AverageTime,
        public actual val operationDuration: Duration,
    ): BenchmarkModeResult<BenchmarkModeConfiguration.AverageTime>
}

@Serializable
public actual data class BenchmarkResult(
    public val jvmEnvironment: BenchmarkJvmEnvironment,
    public actual val configuration: BenchmarkConfiguration,
)