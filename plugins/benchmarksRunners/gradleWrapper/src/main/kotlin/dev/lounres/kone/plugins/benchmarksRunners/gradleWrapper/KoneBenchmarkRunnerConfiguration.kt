/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import org.gradle.api.Named
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property


interface KoneBenchmarkRunnerConfiguration : Named {
    val runnerName: Property<String>
    
    val rawArguments: MapProperty<String, String>
    
    fun finalize()
}