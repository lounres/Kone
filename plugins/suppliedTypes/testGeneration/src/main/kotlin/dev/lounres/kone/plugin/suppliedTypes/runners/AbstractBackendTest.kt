/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypeIrGenerationExtension
import dev.lounres.kone.plugin.suppliedTypes.services.ExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.irHandlersStep
import org.jetbrains.kotlin.test.builders.jvmArtifactsHandlersStep
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
        
        useAfterAnalysisCheckers(::BlackBoxCodegenSuppressor)
        
        if (runJvmBoxTest)
            jvmArtifactsHandlersStep {
                useHandlers(::JvmBoxRunner)
            }
    }
}

open class AbstractBackendTestForPhase(
    phases: List<SuppliedTypeIrGenerationExtension.Phase>
) : AbstractBackendTest(IrPartialExtensionRegistrarConfigurator.Constructor(phases)) {
    constructor(phases: Int) : this(SuppliedTypeIrGenerationExtension.phases.take(phases))
    override val runPipelineTillPhase: TestPhase get() = TestPhase.FIR2IR
}

open class AbstractBackendTestForPhase0 : AbstractBackendTestForPhase(0)
open class AbstractBackendTestForPhase1 : AbstractBackendTestForPhase(1)
open class AbstractBackendTestForPhase2 : AbstractBackendTestForPhase(2)
open class AbstractBackendTestForPhase3 : AbstractBackendTestForPhase(3)
open class AbstractBackendTestForPhase4 : AbstractBackendTestForPhase(4)

open class AbstractBackendTestComplete : AbstractBackendTest(::ExtensionRegistrarConfigurator) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.BACKEND
}

open class AbstractBoxTest : AbstractBackendTest(::ExtensionRegistrarConfigurator, runJvmBoxTest = true) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.BACKEND
}