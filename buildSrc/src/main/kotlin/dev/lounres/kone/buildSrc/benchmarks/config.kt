/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.buildSrc.benchmarks

import kotlinx.benchmark.gradle.BenchmarkConfiguration


// TODO: Create my own configurations

public fun BenchmarkConfiguration.mainConfiguration() {
    mode = "AverageTime"
    warmups = 20
    iterations = 10
    iterationTime = 3
    iterationTimeUnit = "s"
    outputTimeUnit = "ns"
}

public fun BenchmarkConfiguration.smokeConfiguration() {
    mode = "AverageTime"
    warmups = 5
    iterations = 3
    iterationTime = 500
    iterationTimeUnit = "ms"
    outputTimeUnit = "ns"
}