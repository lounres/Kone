/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package lounres.kone.plugins.benchmarksRunners.gradleWrapper

import de.infix.testBalloon.framework.core.testSuite
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
                return projectDirectory
            }
            
            test("project sync") {
                val result = GradleRunner.create()
                    .withProjectDir(newProjectDirectory().toFile())
                    .withPluginClasspath()
                    .forwardOutput()
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
                    val result = GradleRunner.create()
                        .withProjectDir(newProjectDirectory().toFile())
                        .withPluginClasspath()
                        .withArguments(runnerDescription.taskName)
                        .forwardOutput()
                        .build()
                    
                    assert(runnerDescription.output in result.output) { "Did not found runner's output in the Gradle output!" }
                    assert(result.task(runnerDescription.taskName).let { it != null && it.outcome == SUCCESS }) { "The main task is not found or didn't succeed!" }
                }
            }
        }
    }
}