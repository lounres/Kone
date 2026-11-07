/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.kotlinCompilerTestUtils.runners

import org.jetbrains.kotlin.js.test.runners.commonConfigurationForJsTest
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor.SuppressionChecker
import org.jetbrains.kotlin.test.backend.handlers.NoFirCompilationErrorsHandler
import org.jetbrains.kotlin.test.backend.handlers.NoIrCompilationErrorsHandler
import org.jetbrains.kotlin.test.backend.ir.IrDiagnosticsHandler
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureFirHandlersStep
import org.jetbrains.kotlin.test.builders.configureIrHandlersStep
import org.jetbrains.kotlin.test.builders.configureJvmArtifactsHandlersStep
import org.jetbrains.kotlin.test.builders.configureLoweredIrHandlersStep
import org.jetbrains.kotlin.test.configuration.commonBackendHandlersForCodegenTest
import org.jetbrains.kotlin.test.configuration.setupHandlersForDiagnosticTest
import org.jetbrains.kotlin.test.configuration.setupJvmPipelineSteps
import org.jetbrains.kotlin.test.directives.*
import org.jetbrains.kotlin.test.frontend.fir.FirFailingTestSuppressor
import org.jetbrains.kotlin.test.frontend.fir.handlers.NonSourceErrorMessagesHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.PsiLightTreeMetaInfoProcessor
import org.jetbrains.kotlin.test.initIdeaConfiguration
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.PhasedPipelineChecker
import org.jetbrains.kotlin.test.services.TestPhase
import org.jetbrains.kotlin.utils.bind
import org.junit.jupiter.api.BeforeAll


public abstract class KoneTestRunner : AbstractKotlinCompilerTest() {
    public companion object {
        @BeforeAll
        @JvmStatic
        public fun setUp() {
            initIdeaConfiguration()
        }
    }

    final override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider =
        EnvironmentBasedStandardLibrariesPathProvider
}

public fun TestConfigurationBuilder.commonJvmTestRunnerConfiguration(
    runPipelineTillPhase: TestPhase = TestPhase.CODEGEN,
) {
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
//            "+return-value-checker=full",
            "+LocalTypeAliases",
            "+NameBasedDestructuring",
            "+CollectionLiterals",
            "+AllowReturnsResultOfContract",
//            "+CompanionBlocksAndExtensions",
        )
        +CodegenTestDirectives.IGNORE_DEXING
    }
    
    setupJvmPipelineSteps(FirParser.Psi)
//    configureCommonDiagnosticTestPaths()
    
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

public fun TestConfigurationBuilder.commonJsTestRunnerConfiguration(
    runPipelineTillPhase: TestPhase = TestPhase.CODEGEN,
) {
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