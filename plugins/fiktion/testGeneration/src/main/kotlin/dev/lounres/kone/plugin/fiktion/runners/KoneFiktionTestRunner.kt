/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.fiktion.runners

import dev.lounres.kone.plugins.fiktion.services.PluginRuntimeClasspathProvider
import dev.lounres.kone.plugins.fiktion.services.PluginRuntimeProvider
import dev.lounres.kone.plugins.testUtils.runners.KoneTestRunner
import dev.lounres.kone.plugins.testUtils.runners.commonTestRunnerConfiguration
import org.jetbrains.kotlin.test.TestInfrastructureInternals
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.services.TestPhase


abstract class KoneFiktionTestRunner : KoneTestRunner() {
    protected abstract val runPipelineTillPhase: TestPhase
    @TestInfrastructureInternals
    override fun configureInternal(builder: TestConfigurationBuilder) {
        builder.commonTestRunnerConfiguration(
            runPipelineTillPhase = runPipelineTillPhase,
        )
        configure(builder)
        builder.useConfigurators(::PluginRuntimeProvider)
        builder.useCustomRuntimeClasspathProviders(::PluginRuntimeClasspathProvider)
    }
}