/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package lounres.kone.plugins.benchmarksRunners.gradleWrapper

import de.infix.testBalloon.framework.core.testSuite
import org.gradle.testkit.runner.GradleRunner
import java.net.URL
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createTempDirectory
import kotlin.io.path.toPath


data object Resources

fun getResource(path: String): URL? = Resources::class.java.getResource(path)

@OptIn(ExperimentalPathApi::class)
val jvmRunnerExecutionTests by testSuite {
    test("project sync") {
        val projectDirectory = createTempDirectory()
        val resource = getResource("/project1")!!.toURI().toPath()
        resource.copyToRecursively(projectDirectory, followLinks = false, overwrite = true)
        
        val result = GradleRunner.create()
            .withProjectDir(projectDirectory.toFile())
            .withPluginClasspath()
            .forwardOutput()
            .build()
    }
    
    test("jvm runner execution") {
        val projectDirectory = createTempDirectory()
        val resource = getResource("/project1")!!.toURI().toPath()
        resource.copyToRecursively(projectDirectory, followLinks = false, overwrite = true)
        
        val result = GradleRunner.create()
            .withProjectDir(projectDirectory.toFile())
            .withPluginClasspath()
            .withArguments("runJvmMainBenchmarkTestRunner")
            .forwardOutput()
            .build()
    }
}