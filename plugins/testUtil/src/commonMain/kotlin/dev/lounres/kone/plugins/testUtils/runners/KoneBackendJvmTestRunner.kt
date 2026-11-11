/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.testUtils.runners

import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureJvmArtifactsHandlersStep
import org.jetbrains.kotlin.test.builders.irHandlersStep
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator


public abstract class KoneBackendJvmTestRunner(
    private val pluginConfigurator: Constructor<EnvironmentConfigurator>,
    private val runJvmBoxTest: Boolean = false,
) : KoneJvmTestRunner() {
    final override fun configure(builder: TestConfigurationBuilder): Unit = with(builder) {
        defaultDirectives {
            +CodegenTestDirectives.DUMP_IR
        }
        
        irHandlersStep {
            useHandlers(
                ::IrTextDumpHandler,
                ::IrTreeVerifierHandler,
            )
        }
        
        useConfigurators(pluginConfigurator)
        
        useFailureSuppressors(::BlackBoxCodegenSuppressor)
        
        if (runJvmBoxTest)
            configureJvmArtifactsHandlersStep {
                useHandlers(::JvmBoxRunner)
            }
    }
}