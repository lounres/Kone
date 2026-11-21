/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package org.gradle.kotlin.dsl

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json


fun KoneBenchmarkRunnerConfiguration.rawArgument(key: String, value: String) {
    rawArguments.put(key, value)
}
fun <V> KoneBenchmarkRunnerConfiguration.argument(name: String, valueSerializer: SerializationStrategy<V>, value: V) {
    rawArgument(name, Json.encodeToString(valueSerializer, value))
}
inline fun <reified V> KoneBenchmarkRunnerConfiguration.argument(name: String, value: V) {
    rawArgument(name, Json.encodeToString(value))
}