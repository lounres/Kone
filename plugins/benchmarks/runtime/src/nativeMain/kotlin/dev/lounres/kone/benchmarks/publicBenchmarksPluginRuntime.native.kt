/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks

import kotlinx.serialization.Serializable
import kotlin.time.Duration


@Serializable
public actual sealed interface BenchmarkModeConfiguration {
    @Serializable
    public actual class Throughput : BenchmarkModeConfiguration {
        init {
            TODO("Not yet implemented")
        }
        public actual val iterationTime: Duration get() = TODO("Not yet implemented")
    }
    
    @Serializable
    public actual class AverageTime : BenchmarkModeConfiguration {
        init {
            TODO("Not yet implemented")
        }
        public actual val iterationTime: kotlin.time.Duration get() = TODO("Not yet implemented")
    }
}

@Serializable
public actual class BenchmarkConfiguration {
    init {
        TODO("Not yet implemented")
    }
    public actual val numberOfWarmups: UInt get() = TODO("Not yet implemented")
    public actual val numberOfMeasurements: UInt get() = TODO("Not yet implemented")
    public actual val mode: BenchmarkModeConfiguration get() = TODO("Not yet implemented")
    
    public actual companion object
}

public actual fun BenchmarkConfiguration(
    numberOfWarmups: UInt,
    numberOfMeasurements: UInt,
    mode: BenchmarkModeConfiguration,
): BenchmarkConfiguration = TODO("Not yet implemented")

@Serializable
public actual sealed interface BenchmarkModeResult<Configuration : BenchmarkModeConfiguration> {
    public actual val configuration: Configuration
    
    @Serializable
    public actual class Throughput : BenchmarkModeResult<BenchmarkModeConfiguration.Throughput> {
        init {
            TODO("Not yet implemented")
        }
        actual override val configuration: BenchmarkModeConfiguration.Throughput get() = TODO("Not yet implemented")
        public actual val operationsPerIteration: Double get() = TODO("Not yet implemented")
    }
    
    @Serializable
    public actual class AverageTime : BenchmarkModeResult<BenchmarkModeConfiguration.AverageTime> {
        init {
            TODO("Not yet implemented")
        }
        actual override val configuration: BenchmarkModeConfiguration.AverageTime get() = TODO("Not yet implemented")
        public actual val operationDuration: Duration get() = TODO("Not yet implemented")
    }
}

@Serializable
public actual class BenchmarkResult {
    init {
        TODO("Not yet implemented")
    }
    public actual val configuration: BenchmarkConfiguration get() = TODO("Not yet implemented")
}