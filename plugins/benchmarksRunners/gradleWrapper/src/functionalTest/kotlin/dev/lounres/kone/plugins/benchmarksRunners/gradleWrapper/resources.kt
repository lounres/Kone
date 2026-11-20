/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import java.nio.file.Path
import kotlin.io.path.Path


private val resourcesDirectory = Path(System.getProperty("kone.plugin.gradleWrapper.functionalTest.resources.path")!!)

fun getResource(path: String): Path = resourcesDirectory.resolve(path)