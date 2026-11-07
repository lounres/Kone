/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.services.CompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.TestExtensionRegistrarConfigurator
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


abstract class AbstractBackendJvmTest(
    private val pluginConfigurator: Constructor<EnvironmentConfigurator>,
    private val runJvmBoxTest: Boolean = false,
) : KoneSuppliedTypesJvmTestRunner() {
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

open class AbstractBackendJvmTestForPhase(
    lastPhase: UInt = UInt.MAX_VALUE,
) : AbstractBackendJvmTest(IrPartialExtensionRegistrarConfigurator.Constructor(lastPhase)) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.FIR2IR
}

open class AbstractBackendJvmTestForPhase0 : AbstractBackendJvmTestForPhase(0u)
open class AbstractBackendJvmTestForPhase1 : AbstractBackendJvmTestForPhase(1u)
open class AbstractBackendJvmTestForPhase2 : AbstractBackendJvmTestForPhase(2u)
open class AbstractBackendJvmTestForPhase3 : AbstractBackendJvmTestForPhase(3u)
open class AbstractBackendJvmTestForPhase4 : AbstractBackendJvmTestForPhase(4u)
open class AbstractBackendJvmTestForPhase5 : AbstractBackendJvmTestForPhase(5u)
open class AbstractBackendJvmTestForPhase6 : AbstractBackendJvmTestForPhase(6u)
open class AbstractBackendJvmTestForPhase7 : AbstractBackendJvmTestForPhase(7u)

open class AbstractBackendJvmTestComplete : AbstractBackendJvmTest(::CompleteExtensionRegistrarConfigurator) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractBoxJvmTest : AbstractBackendJvmTest(::CompleteExtensionRegistrarConfigurator, runJvmBoxTest = true) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractJvmTestWithoutPlugin: AbstractBackendJvmTest(::TestExtensionRegistrarConfigurator, runJvmBoxTest = true) {
    override val runPipelineTillPhase: TestPhase = TestPhase.CODEGEN
}