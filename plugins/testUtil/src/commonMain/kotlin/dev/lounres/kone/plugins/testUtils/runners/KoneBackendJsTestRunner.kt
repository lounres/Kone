/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.testUtils.runners

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.js.test.handlers.JsIrRecompiledArtifactsIdentityHandler
import org.jetbrains.kotlin.js.test.handlers.JsSizeHandler
import org.jetbrains.kotlin.js.test.runners.AbstractJsBlackBoxCodegenTestBase
import org.jetbrains.kotlin.js.test.runners.commonConfigurationForJsBackendSecondStageTest
import org.jetbrains.kotlin.js.test.runners.configureJsBoxHandlers
import org.jetbrains.kotlin.js.test.runners.setUpDefaultDirectivesForJsBoxTest
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureFirHandlersStep
import org.jetbrains.kotlin.test.builders.configureJsArtifactsHandlersStep
import org.jetbrains.kotlin.test.builders.irHandlersStep
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.LanguageSettingsDirectives.LANGUAGE
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirCfgConsistencyHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirCfgDumpHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirDumpHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirResolvedTypesVerifier
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator


public abstract class KoneBackendJsTestRunner(
    private val pluginConfigurator: Constructor<EnvironmentConfigurator>,
    private val runJsBoxTest: Boolean = false,
) : KoneJsTestRunner() {
    final override fun configure(builder: TestConfigurationBuilder): Unit = with(builder) {
        defaultDirectives {
            +CodegenTestDirectives.DUMP_IR
        }
        
        commonConfigurationForJsBackendSecondStageTest(
            pathToTestDir = "",
            testGroupOutputDirPrefix = "box/js",
            backendFacades = AbstractJsBlackBoxCodegenTestBase.JsBackendFacades.WithRecompilation
        )
        
        irHandlersStep {
            useHandlers(
                ::IrTextDumpHandler,
                ::IrTreeVerifierHandler,
            )
        }
        
        setUpDefaultDirectivesForJsBoxTest(FirParser.Psi)
        configureFirHandlersStep {
            useHandlers(
                ::FirDumpHandler,
                ::FirCfgDumpHandler,
                ::FirCfgConsistencyHandler,
                ::FirResolvedTypesVerifier,
            )
        }
        defaultDirectives {
            LANGUAGE with listOf(
                "-${LanguageFeature.IrIntraModuleInlinerBeforeKlibSerialization.name}",
                "-${LanguageFeature.IrCrossModuleInlinerBeforeKlibSerialization.name}"
            )
        }
        configureJsArtifactsHandlersStep {
            useHandlers(
                ::JsIrRecompiledArtifactsIdentityHandler,
                ::JsSizeHandler,
            )
        }
        
        if (runJsBoxTest)
            configureJsBoxHandlers()
        
        useConfigurators(pluginConfigurator)
    }
}