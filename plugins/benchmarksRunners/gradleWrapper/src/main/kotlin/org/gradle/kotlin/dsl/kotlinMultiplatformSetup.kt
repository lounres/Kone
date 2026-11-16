/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package org.gradle.kotlin.dsl

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarksRunnersGradleExtension
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks.KoneBenchmarksJsRunnersGenerator
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks.KoneBenchmarksJvmRunnersGenerator
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.JavaExec
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget


@JvmName("koneBenchmarksRunnersJvm")
fun KotlinJvmCompilation.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinJvmCompilation, JavaExec>>.() -> Unit) {
    project.extensions.configure<KoneBenchmarksRunnersGradleExtension> {
        this@koneBenchmarksRunners(KoneBenchmarksJvmRunnersGenerator, block)
    }
}

@JvmName("koneBenchmarksRunnersJvm")
fun KotlinJvmTarget.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinJvmCompilation, JavaExec>>.() -> Unit) {
    this.compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME) { koneBenchmarksRunners(block) }
}

@JvmName("koneBenchmarksRunnersJs")
fun KotlinJsIrCompilation.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinJsIrCompilation, NodeJsExec>>.() -> Unit) {
    project.extensions.configure<KoneBenchmarksRunnersGradleExtension> {
        this@koneBenchmarksRunners(KoneBenchmarksJsRunnersGenerator, block)
    }
}

@JvmName("koneBenchmarksRunnersJs")
fun KotlinJsTargetDsl.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinJsIrCompilation, NodeJsExec>>.() -> Unit) {
    this.compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME) { koneBenchmarksRunners(block) }
}