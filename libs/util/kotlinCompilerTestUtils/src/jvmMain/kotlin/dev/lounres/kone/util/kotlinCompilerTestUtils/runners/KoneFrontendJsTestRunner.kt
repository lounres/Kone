/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.kotlinCompilerTestUtils.runners

import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestPhase


public open class KoneFrontendJsTestRunner(private val pluginConfigurator: Constructor<EnvironmentConfigurator>) : KoneJsTestRunner() {
    final override val runPipelineTillPhase: TestPhase = TestPhase.FRONTEND
    final override fun configure(builder: TestConfigurationBuilder) {
        builder.useConfigurators(pluginConfigurator)
    }
}