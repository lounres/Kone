/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor


private object ValidOptions {
    val format = setOf("json", "csv", "scsv", "text")
    val timeUnits = setOf(
        "NANOSECONDS", "ns", "nanos",
        "MICROSECONDS", "us", "micros",
        "MILLISECONDS", "ms", "millis",
        "SECONDS", "s", "sec",
        "MINUTES", "m", "min"
    )
    val modes = setOf("thrpt", "avgt", "Throughput", "AverageTime")
    val nativeForks = setOf("perBenchmark", "perIteration")
    val wasmForks = setOf("perBenchmark")
}

internal fun validateConfig(config: KotlinxBenchmarkConfiguration) {
    config.reportFormat?.let {
        require(it.lowercase() in ValidOptions.format) {
            "Invalid report format: '$it'. Accepted formats: ${ValidOptions.format.joinToString(", ")} (e.g., reportFormat = \"json\")."
        }
    }
    
    config.iterations?.let {
        require(it > 0) {
            "Invalid iterations: '$it'. Expected a positive integer (e.g., iterations = 5)."
        }
    }
    
    config.warmups?.let {
        require(it >= 0) {
            "Invalid warmups: '$it'. Expected a non-negative integer (e.g., warmups = 3)."
        }
    }
    
    config.iterationTime?.let {
        require(it > 0) {
            "Invalid iterationTime: '$it'. Expected a positive number (e.g., iterationTime = 300)."
        }
        require(config.iterationTimeUnit != null) {
            "Missing iterationTimeUnit. Please provide iterationTimeUnit when specifying iterationTime."
        }
    }
    
    config.iterationTimeUnit?.let {
        require(config.iterationTime != null) {
            "Missing iterationTime. Please provide iterationTime when specifying iterationTimeUnit."
        }
    }
    
    config.threads?.let {
        require(it > 0 || it == -1 /*THREADS_CPU_COUNT*/) {
            "Invalid threads: '$it'. Expected a positive integer or a special THREADS_CPU_COUNT value (e.g., threads = 4)."
        }
    }
    
    config.params.forEach { [param, values] ->
        require(param.isNotBlank()) {
            "Invalid param name: '$param'. It must not be blank."
        }
        require(values.isNotEmpty()) {
            "Param '$param' has no values. At least one value is required."
        }
    }
    
    config.advanced.forEach { [param, value] ->
        require(param.isNotBlank()) {
            "Invalid advanced option name: '$param'. It must not be blank."
        }
        require(value.isNotBlank()) {
            "Invalid value for advanced option '$param': '$value'. Value should not be blank."
        }
        
        when (param) {
            "nativeFork" -> {
                require(value in ValidOptions.nativeForks) {
                    "Invalid value for 'nativeFork': '$value'. " +
                            "Accepted values: ${ValidOptions.nativeForks.joinToString(", ")}."
                }
            }
            
            "nativeGCAfterIteration" -> require(value.toBooleanStrictOrNull() != null) {
                "Invalid value for 'nativeGCAfterIteration': '$value'. Expected a Boolean value."
            }
            
            "jvmForks" -> {
                val intValue = value.toIntOrNull()
                require(intValue != null && intValue >= 0 || value == "definedByJmh") {
                    "Invalid value for 'jvmForks': '$value'. Expected a non-negative integer or \"definedByJmh\"."
                }
            }
            
            "jmhIgnoreLock" -> require(value.toBooleanStrictOrNull() != null) {
                "Invalid value for 'jmhIgnoreLock': '$value'. Expected a Boolean value."
            }
            
            "jsUseBridge" -> require(value.toBooleanStrictOrNull() != null) {
                "Invalid value for 'jsUseBridge': '$value'. Expected a Boolean value."
            }
            
            "wasmFork" -> {
                require(value in ValidOptions.wasmForks) {
                    "Invalid value for 'nativeFork': '$value'. " +
                            "Accepted values: ${ValidOptions.wasmForks.joinToString(", ")}."
                }
            }
            
            "engineBinaryPath" -> {}
            
            "engineArguments" -> {}
            
            else -> throw IllegalArgumentException("Invalid advanced option name: '$param'. Accepted options: \"nativeFork\", \"nativeGCAfterIteration\", \"jvmForks\", \"jsUseBridge\", \"wasmFork\".")
        }
    }
}

public expect val KoneBenchmarkExecutor.Companion.kotlinxBenchmark: KoneBenchmarkExecutor<KotlinxBenchmarkConfiguration, KotlinxBenchmarkBenchmark, KotlinxBenchmarkResult>