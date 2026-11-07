/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.kotlinCompilerTestUtils.runners

import dev.lounres.kone.util.kotlinCompilerTestUtils.services.KonePluginRuntimeClasspathProvider
import dev.lounres.kone.util.kotlinCompilerTestUtils.services.KonePluginRuntimeProvider
import org.jetbrains.kotlin.js.test.runners.commonConfigurationForJsTest
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.TestInfrastructureInternals
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.NoFirCompilationErrorsHandler
import org.jetbrains.kotlin.test.backend.ir.IrDiagnosticsHandler
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureFirHandlersStep
import org.jetbrains.kotlin.test.builders.configureIrHandlersStep
import org.jetbrains.kotlin.test.builders.configureLoweredIrHandlersStep
import org.jetbrains.kotlin.test.configuration.setupHandlersForDiagnosticTest
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives
import org.jetbrains.kotlin.test.directives.LanguageSettingsDirectives
import org.jetbrains.kotlin.test.directives.TestPhaseDirectives
import org.jetbrains.kotlin.test.directives.configureFirParser
import org.jetbrains.kotlin.test.frontend.fir.FirFailingTestSuppressor
import org.jetbrains.kotlin.test.services.PhasedPipelineChecker
import org.jetbrains.kotlin.test.services.TestPhase

public abstract class KoneJsTestRunner : KoneTestRunner() {
    protected abstract val runPipelineTillPhase: TestPhase
    @TestInfrastructureInternals
    override fun configureInternal(builder: TestConfigurationBuilder) {
        with(builder) {
            globalDefaults {
                targetBackend = TargetBackend.JS_IR
            }
            defaultDirectives {
                +FirDiagnosticsDirectives.ENABLE_PLUGIN_PHASES
                +FirDiagnosticsDirectives.FIR_DUMP
                TestPhaseDirectives.LATEST_PHASE_IN_PIPELINE with TestPhase.CODEGEN
                TestPhaseDirectives.RUN_PIPELINE_TILL with runPipelineTillPhase
                LanguageSettingsDirectives.LANGUAGE with listOf(
                    "+EnableDfaWarningsInK2",
                    
                    "+DataClassCopyRespectsConstructorVisibility",
                    "+ContextSensitiveResolutionUsingExpectedType",
//                    "+return-value-checker=full",
                    "+LocalTypeAliases",
                    "+NameBasedDestructuring",
                    "+CollectionLiterals",
                    "+AllowReturnsResultOfContract",
//                    "+CompanionBlocksAndExtensions",
                )
            }
            
            commonConfigurationForJsTest()
            configureFirParser(FirParser.Psi)
            
            configureFirHandlersStep {
                setupHandlersForDiagnosticTest()
                useHandlers(::NoFirCompilationErrorsHandler)
            }
            configureIrHandlersStep {
                useHandlers(::IrDiagnosticsHandler)
            }
            configureLoweredIrHandlersStep {
                useHandlers(::IrDiagnosticsHandler)
            }
            
            useFailureSuppressors(
                ::PhasedPipelineChecker,
                ::BlackBoxCodegenSuppressor,
                ::FirFailingTestSuppressor,
            )
            enableMetaInfoHandler()
        }
        
        configure(builder)
        
        builder.useConfigurators(::KonePluginRuntimeProvider)
        builder.useCustomRuntimeClasspathProviders(::KonePluginRuntimeClasspathProvider)
    }
}