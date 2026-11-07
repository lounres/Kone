/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.services.CompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.TestExtensionRegistrarConfigurator
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
import org.jetbrains.kotlin.test.services.TestPhase


abstract class AbstractBackendJsTest(
    private val pluginConfigurator: Constructor<EnvironmentConfigurator>,
    private val runJsBoxTest: Boolean = false,
) : KoneSuppliedTypesJsTestRunner() {
    final override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        defaultDirectives {
            +CodegenTestDirectives.DUMP_IR
        }
        
        commonConfigurationForJsBackendSecondStageTest(
            pathToTestDir = "${System.getProperty("test.data.dir")}/js/ir",
            testGroupOutputDirPrefix = "box/",
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

open class AbstractBackendJsTestForPhase(
    lastPhase: UInt = MAX_VALUE,
) : AbstractBackendJsTest(IrPartialExtensionRegistrarConfigurator.Constructor(lastPhase)) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.FIR2IR
}

open class AbstractBackendJsTestForPhase0 : AbstractBackendJsTestForPhase(0u)
open class AbstractBackendJsTestForPhase1 : AbstractBackendJsTestForPhase(1u)
open class AbstractBackendJsTestForPhase2 : AbstractBackendJsTestForPhase(2u)
open class AbstractBackendJsTestForPhase3 : AbstractBackendJsTestForPhase(3u)
open class AbstractBackendJsTestForPhase4 : AbstractBackendJsTestForPhase(4u)
open class AbstractBackendJsTestForPhase5 : AbstractBackendJsTestForPhase(5u)
open class AbstractBackendJsTestForPhase6 : AbstractBackendJsTestForPhase(6u)
open class AbstractBackendJsTestForPhase7 : AbstractBackendJsTestForPhase(7u)

open class AbstractBackendJsTestComplete : AbstractBackendJsTest(::CompleteExtensionRegistrarConfigurator) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractBoxJsTest : AbstractBackendJsTest(::CompleteExtensionRegistrarConfigurator, runJsBoxTest = true) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractJsTestWithoutPlugin: AbstractBackendJsTest(::TestExtensionRegistrarConfigurator, runJsBoxTest = true) {
    override val runPipelineTillPhase: TestPhase = TestPhase.CODEGEN
}