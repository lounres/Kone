package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.services.ExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirSuppliedTypeCompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator1
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator2
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator3
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator4
import dev.lounres.kone.plugin.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator5
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.BytecodeListingHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.irHandlersStep
import org.jetbrains.kotlin.test.builders.jvmArtifactsHandlersStep
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.directives.configureFirParser
import org.jetbrains.kotlin.test.frontend.fir.Fir2IrResultsConverter
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.runners.RunnerWithTargetBackendForTestGeneratorMarker


/*
 * Containers of different directives, which can be used in tests:
 * - ModuleStructureDirectives
 * - LanguageSettingsDirectives
 * - DiagnosticsDirectives
 * - CodegenTestDirectives
 *
 * All of them are located in `org.jetbrains.kotlin.test.directives` package
 */
open class AbstractBoxTest : BaseTestRunner(), RunnerWithTargetBackendForTestGeneratorMarker {
    override val targetBackend: TargetBackend
        get() = TargetBackend.JVM_IR
    
    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        globalDefaults {
            targetBackend = TargetBackend.JVM_IR
            targetPlatform = JvmPlatforms.defaultJvmPlatform
            dependencyKind = DependencyKind.Binary
        }
        
        configureFirParser(FirParser.Psi)
        
        defaultDirectives {
            +DUMP_IR
        }
        
        commonFirWithPluginFrontendConfiguration()
        facadeStep(::Fir2IrResultsConverter)
        irHandlersStep {
            useHandlers(
                ::IrTextDumpHandler,
                ::IrTreeVerifierHandler,
            )
        }
        facadeStep(::JvmIrBackendFacade)
        
        useAfterAnalysisCheckers(::BlackBoxCodegenSuppressor)
    }
}

open class AbstractBoxTestWithoutPlugin : AbstractBoxTest()

open class AbstractBoxTestForPhase0 : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useConfigurators(::FirSuppliedTypeCompleteExtensionRegistrarConfigurator)
    }
}

open class AbstractBoxTestForPhase1 : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useConfigurators(::IrPartialExtensionRegistrarConfigurator1)
    }
}

open class AbstractBoxTestForPhase2 : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useConfigurators(::IrPartialExtensionRegistrarConfigurator2)
    }
}

open class AbstractBoxTestForPhase3 : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useConfigurators(::IrPartialExtensionRegistrarConfigurator3)
    }
}

open class AbstractBoxTestForPhase4 : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useConfigurators(::IrPartialExtensionRegistrarConfigurator4)
    }
}

open class AbstractBoxTestForPhase5 : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useConfigurators(::IrPartialExtensionRegistrarConfigurator5)
    }
}

open class AbstractBoxTestComplete : AbstractBoxTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.jvmArtifactsHandlersStep {
            useHandlers(::JvmBoxRunner)
        }
        builder.useConfigurators(::ExtensionRegistrarConfigurator)
    }
}