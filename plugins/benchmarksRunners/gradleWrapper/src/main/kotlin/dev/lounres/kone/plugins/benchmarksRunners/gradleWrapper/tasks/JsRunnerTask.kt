/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarksRunnersGenerator
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec


object KoneBenchmarksJsRunnersGenerator : KoneBenchmarksRunnersGenerator<KotlinJvmCompilation, NodeJsExec> {
    override fun generateRunnerTask(
        project: Project,
        compilation: KotlinJvmCompilation,
        taskName: String,
        configuration: KoneBenchmarkRunnerConfiguration<KotlinJvmCompilation, NodeJsExec>
    ): TaskProvider<NodeJsExec> {
        TODO("Not yet implemented")
    }
}