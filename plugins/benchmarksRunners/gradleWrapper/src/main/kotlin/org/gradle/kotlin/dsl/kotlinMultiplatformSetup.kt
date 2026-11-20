/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package org.gradle.kotlin.dsl

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarksRunnersGradleExtension
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks.KoneBenchmarksJsRunnerConfiguration
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks.KoneBenchmarksJvmRunnerConfiguration
import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget


@JvmName("koneBenchmarksRunnersJvm")
fun KotlinJvmCompilation.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarksJvmRunnerConfiguration>.() -> Unit) {
    project.the<KoneBenchmarksRunnersGradleExtension>()[this, KoneBenchmarksJvmRunnerConfiguration.namedDomainObjectFactory(this)].block()
}

@JvmName("koneBenchmarksRunnersJvm")
fun KotlinJvmTarget.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarksJvmRunnerConfiguration>.() -> Unit) {
    this.compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME) { koneBenchmarksRunners(block) }
}

@JvmName("koneBenchmarksRunnersJs")
fun KotlinJsIrCompilation.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarksJsRunnerConfiguration>.() -> Unit) {
    project.the<KoneBenchmarksRunnersGradleExtension>()[this, KoneBenchmarksJsRunnerConfiguration.namedDomainObjectFactory(this)].block()
}

@JvmName("koneBenchmarksRunnersJs")
fun KotlinJsTargetDsl.koneBenchmarksRunners(block: NamedDomainObjectContainer<KoneBenchmarksJsRunnerConfiguration>.() -> Unit) {
    this.compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME) { this.koneBenchmarksRunners(block) }
}