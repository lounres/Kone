/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks

import kotlin.reflect.KClass


private class BenchmarksPluginException(message: String? = null, reason: Throwable? = null) : IllegalStateException(message, reason)

@PublishedApi
internal fun benchmarksPluginExceptionForRuntimeDeclarations(): Nothing = throw BenchmarksPluginException(benchmarksPluginExceptionForRuntimeDeclarationsMessage)

@Deprecated(
    message = internalsDeprecationAnnotationMessage,
    level = HIDDEN,
)
public data class InjectedValue<out T>(
    val value: T,
    val disposableInjection: DisposableInjection<T>? = null,
)

@Deprecated(
    message = internalsDeprecationAnnotationMessage,
    level = HIDDEN,
)
public fun runBenchmark(
    benchmarkClass: KClass<*>,
    parameters: Map<String, String>,
    configuration: BenchmarkConfiguration,
): BenchmarkResult = TODO()