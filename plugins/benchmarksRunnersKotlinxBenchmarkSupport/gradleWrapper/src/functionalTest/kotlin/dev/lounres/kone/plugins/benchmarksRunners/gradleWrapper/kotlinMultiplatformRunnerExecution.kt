/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.gradleWrapper.KoneConstants
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Path
import kotlin.io.path.*


@OptIn(ExperimentalPathApi::class)
val kotlinMultiplatformRunnerExecutionTests by testSuite {
    val kotlinMultiplatformProjects = getResource("kotlinMultiplatform")
    
    kotlinMultiplatformProjects.forEachDirectoryEntry { resourceProject ->
        testSuite(resourceProject.name) {
            fun newProjectDirectory(): Path {
                val projectDirectory = createTempDirectory()
                resourceProject.copyToRecursively(projectDirectory, followLinks = false, overwrite = true)
                val libsVersionsToml = projectDirectory.resolve("gradle/libs.versions.toml")
                libsVersionsToml.writeText(
                    libsVersionsToml.readText()
                        .replace("<kone>", KoneConstants.KONE_VERSION)
                        .replace("<kotlin>", KoneConstants.KOTLIN_VERSION)
                )
                return projectDirectory
            }
            
            test("project sync") {
                val result = GradleRunner.create()
                    .withProjectDir(newProjectDirectory().toFile())
                    .build()
            }
            
            @Serializable
            data class RunnerDescription(
                val taskName: String,
                val output: String,
            )

            val runnerDescriptions = Json.decodeFromStream<Map<String, RunnerDescription>>(resourceProject.resolve("runners.json").inputStream())

            for ([runnerName, runnerDescription] in runnerDescriptions) {
                test(runnerName) {
                    val projectDirectory = newProjectDirectory()

                    val result = GradleRunner.create()
                        .withProjectDir(projectDirectory.toFile())
                        .withArguments(runnerDescription.taskName)
                        .build()

                    assert(runnerDescription.output in result.output) { "Did not found runner's output in the Gradle output!" }
                    assert(result.task(runnerDescription.taskName).let { it != null && it.outcome == SUCCESS }) { "The main task is not found or didn't succeed!" }

                    projectDirectory.deleteRecursively()
                }
            }
        }
    }
}