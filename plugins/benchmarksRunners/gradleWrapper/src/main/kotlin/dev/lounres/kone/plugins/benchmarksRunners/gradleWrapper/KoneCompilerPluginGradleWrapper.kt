/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import org.gradle.api.Project
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitSupportPlugin


class KoneCompilerPluginGradleWrapper : DevKitSupportPlugin(KonePluginInfo.PLUGIN_INFO) {
    override fun apply(target: Project) {
        val extension = KoneBenchmarksRunnersGradleExtension(target)
        target.extensions.add("koneBenchmarksRunners", extension)
        
        target.afterEvaluate {
            extension.finalizeCurrentConfigurations()
        }
    }
}