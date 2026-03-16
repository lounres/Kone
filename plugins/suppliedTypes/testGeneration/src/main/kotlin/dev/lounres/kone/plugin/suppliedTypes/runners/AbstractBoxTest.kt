/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.ir.IrRuntimeReferences
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliabilityCollectionVisitor
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypeIrGenerationExtension
import dev.lounres.kone.plugin.suppliedTypes.services.ExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirCompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.backend.ir.BackendCliJvmFacade
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.irHandlersStep
import org.jetbrains.kotlin.test.builders.jvmArtifactsHandlersStep
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.configureFirParser
import org.jetbrains.kotlin.test.frontend.fir.Fir2IrCliJvmFacade
import org.jetbrains.kotlin.test.frontend.fir.Fir2IrResultsConverter
import org.jetbrains.kotlin.test.model.DependencyKind


/*
 * Containers of different directives, which can be used in tests:
 * - ModuleStructureDirectives
 * - LanguageSettingsDirectives
 * - DiagnosticsDirectives
 * - CodegenTestDirectives
 *
 * All of them are located in `org.jetbrains.kotlin.test.directives` package
 */
open class AbstractBoxTest : BaseTestRunner() {
    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        globalDefaults {
            targetBackend = TargetBackend.JVM_IR
            targetPlatform = JvmPlatforms.defaultJvmPlatform
            dependencyKind = DependencyKind.Binary
        }
        
        configureFirParser(FirParser.Psi)
        
        defaultDirectives {
            +CodegenTestDirectives.DUMP_IR
        }
        
        commonFirWithPluginFrontendConfiguration()
        facadeStep(::Fir2IrCliJvmFacade)
        irHandlersStep {
            useHandlers(
                ::IrTextDumpHandler,
                ::IrTreeVerifierHandler,
            )
        }
        facadeStep(::BackendCliJvmFacade)
        
        useAfterAnalysisCheckers(::BlackBoxCodegenSuppressor)
    }
}

open class AbstractBoxTestForPhase(
    private val phases:  List<(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext, irRuntimeReferences: IrRuntimeReferences, suppliabilityCollectionVisitor: SuppliabilityCollectionVisitor) -> Unit>
) : AbstractBoxTest() {
    constructor(phases: Int) : this(SuppliedTypeIrGenerationExtension.phases.take(phases))
    
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useConfigurators(
            { testServices ->
                IrPartialExtensionRegistrarConfigurator(
                    testServices = testServices,
                    phases = phases,
                )
            }
        )
    }
}

open class AbstractBoxTestForPhase0 : AbstractBoxTestForPhase(0)
open class AbstractBoxTestForPhase1 : AbstractBoxTestForPhase(1)
open class AbstractBoxTestForPhase2 : AbstractBoxTestForPhase(2)
open class AbstractBoxTestForPhase3 : AbstractBoxTestForPhase(3)
open class AbstractBoxTestForPhase4 : AbstractBoxTestForPhase(4)
open class AbstractBoxTestForPhase5 : AbstractBoxTestForPhase(5)

open class AbstractBoxTestComplete : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.jvmArtifactsHandlersStep {
            useHandlers(::JvmBoxRunner)
        }
        builder.useConfigurators(::ExtensionRegistrarConfigurator)
    }
}

open class AbstractBoxTestWithoutPlugin : AbstractBoxTest()