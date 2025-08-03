/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.buildSrc.benchmarks

import kotlinx.serialization.Serializable


@Serializable
public data class JmhReport(
    val jmhVersion: String,
    val benchmark: String,
    val mode: String,
    val threads: UInt,
    val forks: UInt,
    val jvm: String,
    val jvmArgs: List<String>,
    val jdkVersion: String,
    val vmName: String,
    val vmVersion: String,
    val warmupIterations: UInt,
    val warmupTime: String,
    val warmupBatchSize: UInt,
    val measurementIterations: UInt,
    val measurementTime: String,
    val measurementBatchSize: UInt,
    val params: Map<String, String> = emptyMap(),
    val primaryMetric: PrimaryMetric,
    val secondaryMetrics: Map<String, SecondaryMetric>,
) {
    public interface Metric {
        public val score: Double
        public val scoreError: Double
        public val scoreConfidence: List<Double>
        public val scorePercentiles: Map<Double, Double>
        public val scoreUnit: String
    }
    
    @Serializable
    public data class PrimaryMetric(
        override val score: Double,
        override val scoreError: Double,
        override val scoreConfidence: List<Double>,
        override val scorePercentiles: Map<Double, Double>,
        override val scoreUnit: String,
        val rawDataHistogram: List<List<List<List<Double>>>>? = null,
        val rawData: List<List<Double>>? = null,
    ) : Metric
    
    @Serializable
    public data class SecondaryMetric(
        override val score: Double,
        override val scoreError: Double,
        override val scoreConfidence: List<Double>,
        override val scorePercentiles: Map<Double, Double>,
        override val scoreUnit: String,
        val rawData: List<List<Double>>,
    ) : Metric
}