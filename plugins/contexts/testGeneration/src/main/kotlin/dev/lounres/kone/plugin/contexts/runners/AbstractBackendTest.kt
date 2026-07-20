/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.runners

import dev.lounres.kone.plugin.contexts.services.CompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.contexts.services.IrPartialExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.contexts.services.TestExtensionRegistrarConfigurator
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
import org.jetbrains.kotlin.test.services.TestPhase


abstract class AbstractBackendTest(
    private val pluginConfigurator: Constructor<EnvironmentConfigurator>,
    private val runJvmBoxTest: Boolean = false,
) : KoneSuppliedTypesTestRunner() {
    final override fun configure(builder: TestConfigurationBuilder) = with(builder) {
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

open class AbstractBackendTestForPhase(
    lastPhase: UInt = UInt.MAX_VALUE,
) : AbstractBackendTest(IrPartialExtensionRegistrarConfigurator.Constructor(lastPhase)) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.FIR2IR
}

open class AbstractBackendTestForPhase0 : AbstractBackendTestForPhase(0u)
open class AbstractBackendTestForPhase1 : AbstractBackendTestForPhase(1u)
open class AbstractBackendTestForPhase2 : AbstractBackendTestForPhase(2u)
open class AbstractBackendTestForPhase3 : AbstractBackendTestForPhase(3u)
open class AbstractBackendTestForPhase4 : AbstractBackendTestForPhase(4u)
open class AbstractBackendTestForPhase5 : AbstractBackendTestForPhase(5u)
open class AbstractBackendTestForPhase6 : AbstractBackendTestForPhase(6u)
open class AbstractBackendTestForPhase7 : AbstractBackendTestForPhase(7u)

open class AbstractBackendTestComplete : AbstractBackendTest(::CompleteExtensionRegistrarConfigurator) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.BACKEND
}

open class AbstractBoxTest : AbstractBackendTest(::CompleteExtensionRegistrarConfigurator, runJvmBoxTest = true) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.BACKEND
}

open class AbstractTestWithoutPlugin: AbstractBackendTest(::TestExtensionRegistrarConfigurator, runJvmBoxTest = true) {
    override val runPipelineTillPhase: TestPhase = TestPhase.BACKEND
}