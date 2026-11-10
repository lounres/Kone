/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import kotlinx.benchmark.Mode


public data class KotlinxBenchmarkConfiguration(
    public val numberOfWarmups: UInt,
    public val numberOfMeasurements: UInt,
    public val mode: Mode,
    // TODO: More... With expect-actuals as well...
)