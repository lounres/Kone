/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package org.gradle.kotlin.dsl

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarksRunnersGradleExtension
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks.KoneBenchmarksJvmRunnersGenerator
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.JavaExec
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinOnlyTarget


fun KotlinJvmCompilation.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinJvmCompilation, JavaExec>>.() -> Unit) {
    project.extensions.configure<KoneBenchmarksRunnersGradleExtension> {
        this@koneBenchmarksRunners(KoneBenchmarksJvmRunnersGenerator, block)
    }
}

fun KotlinOnlyTarget<KotlinJvmCompilation>.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinJvmCompilation, JavaExec>>.() -> Unit) {
    this.compilations.named("main") { koneBenchmarksRunners(block) }
}