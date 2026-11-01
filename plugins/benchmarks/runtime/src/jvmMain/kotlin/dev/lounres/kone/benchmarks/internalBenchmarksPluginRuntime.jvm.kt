/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks

import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.runner.options.TimeValue
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds


internal val Duration.jmhTimeValue: TimeValue get() = TimeValue.nanoseconds(inWholeNanoseconds)
internal val TimeValue.kotlinDuration: Duration get() = this.convertTo(TimeUnit.NANOSECONDS).nanoseconds

@Deprecated(message = internalsDeprecationAnnotationMessage, level = DeprecationLevel.HIDDEN)
public actual fun runBenchmark(
    benchmarkClass: KClass<*>,
    parameters: Map<String, String>,
    configuration: BenchmarkConfiguration,
): BenchmarkResult {
    val jmhOptions = OptionsBuilder()
        .include(benchmarkClass.qualifiedName)
        .warmupIterations(configuration.numberOfWarmups.toInt())
        .measurementIterations(configuration.numberOfMeasurements.toInt())
        .let {
            when (val mode = configuration.mode) {
                is AverageTime -> it
                    .warmupTime(mode.iterationTime.jmhTimeValue)
                    .measurementTime(mode.iterationTime.jmhTimeValue)
                    .mode(AverageTime)
                is Throughput -> it
                    .warmupTime(mode.iterationTime.jmhTimeValue)
                    .measurementTime(mode.iterationTime.jmhTimeValue)
                    .mode(Throughput)
                is SampleTime -> it
                    .warmupTime(mode.iterationTime.jmhTimeValue)
                    .measurementTime(mode.iterationTime.jmhTimeValue)
                    .mode(SampleTime)
            }
        }
        .warmupForks(configuration.numberOfForks.toInt())
        .forks(configuration.numberOfForks.toInt())
        .let {
            var builder = it
            for ((parameterName = key, parameterValue = value) in parameters) {
                builder = builder.param(parameterName, parameterValue)
            }
            builder
        }
        .shouldDoGC(configuration.forceGarbageCollectionBetweenIterations)
        .verbosity(SILENT)
        .build()
    
    val jmhResult = Runner(jmhOptions).runSingle()
    
    return BenchmarkResult(
        jvmEnvironment = BenchmarkJvmEnvironment(
            jvm = jmhResult.params.jvm,
            jvmArgs = jmhResult.params.jvmArgs.toList(),
            jdkVersion = jmhResult.params.jdkVersion,
            jmhVersion = jmhResult.params.jmhVersion,
            vmName = jmhResult.params.vmName,
            vmVersion = jmhResult.params.vmVersion,
        ),
        configuration = configuration,
        
    )
}