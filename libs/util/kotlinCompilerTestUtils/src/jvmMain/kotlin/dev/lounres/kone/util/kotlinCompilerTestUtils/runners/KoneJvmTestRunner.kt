/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.kotlinCompilerTestUtils.runners

import dev.lounres.kone.util.kotlinCompilerTestUtils.services.KonePluginRuntimeClasspathProvider
import dev.lounres.kone.util.kotlinCompilerTestUtils.services.KonePluginRuntimeProvider
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.TestInfrastructureInternals
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor.SuppressionChecker
import org.jetbrains.kotlin.test.backend.handlers.NoFirCompilationErrorsHandler
import org.jetbrains.kotlin.test.backend.handlers.NoIrCompilationErrorsHandler
import org.jetbrains.kotlin.test.backend.ir.IrDiagnosticsHandler
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureFirHandlersStep
import org.jetbrains.kotlin.test.builders.configureIrHandlersStep
import org.jetbrains.kotlin.test.builders.configureJvmArtifactsHandlersStep
import org.jetbrains.kotlin.test.configuration.commonBackendHandlersForCodegenTest
import org.jetbrains.kotlin.test.configuration.setupHandlersForDiagnosticTest
import org.jetbrains.kotlin.test.configuration.setupJvmPipelineSteps
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives
import org.jetbrains.kotlin.test.directives.LanguageSettingsDirectives
import org.jetbrains.kotlin.test.directives.TestPhaseDirectives
import org.jetbrains.kotlin.test.frontend.fir.handlers.NonSourceErrorMessagesHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.PsiLightTreeMetaInfoProcessor
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.services.PhasedPipelineChecker
import org.jetbrains.kotlin.test.services.TestPhase
import org.jetbrains.kotlin.utils.bind


public abstract class KoneJvmTestRunner : KoneTestRunner() {
    protected abstract val runPipelineTillPhase: TestPhase
    @TestInfrastructureInternals
    override fun configureInternal(builder: TestConfigurationBuilder) {
        with(builder) {
            globalDefaults {
                targetBackend = TargetBackend.JVM
                targetPlatform = JvmPlatforms.defaultJvmPlatform
                dependencyKind = DependencyKind.Binary
            }
            
            defaultDirectives {
                +FirDiagnosticsDirectives.ENABLE_PLUGIN_PHASES
                +FirDiagnosticsDirectives.FIR_DUMP
                +JvmEnvironmentConfigurationDirectives.FULL_JDK
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
                +CodegenTestDirectives.IGNORE_DEXING
            }
            
            setupJvmPipelineSteps(FirParser.Psi)
//            configureCommonDiagnosticTestPaths()
            
            configureFirHandlersStep {
                setupHandlersForDiagnosticTest()
                useHandlers(::NoFirCompilationErrorsHandler)
            }
            
            configureIrHandlersStep {
                useHandlers(::IrDiagnosticsHandler, ::NoIrCompilationErrorsHandler)
            }
            
            configureJvmArtifactsHandlersStep {
                commonBackendHandlersForCodegenTest(includeNoCompilationErrorsHandler = false)
            }
            
            useMetaInfoProcessors(::PsiLightTreeMetaInfoProcessor)
            useAfterAnalysisCheckers(::NonSourceErrorMessagesHandler)
            useFailureSuppressors(::PhasedPipelineChecker)
            enableMetaInfoHandler()
            useAdditionalService<SuppressionChecker>(::SuppressionChecker.bind(null, null))
        }
        
        configure(builder)
        
        builder.useConfigurators(::KonePluginRuntimeProvider)
        builder.useCustomRuntimeClasspathProviders(::KonePluginRuntimeClasspathProvider)
    }
}