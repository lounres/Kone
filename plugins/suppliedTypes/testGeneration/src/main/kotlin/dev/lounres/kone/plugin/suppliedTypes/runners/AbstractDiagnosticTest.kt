package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.services.FirClassSuppliedTypeParametersPropertiesGenerationExtensionConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirSuppliedTypeCheckersExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirSuppliedTypeCompleteExtensionRegistrarConfigurator
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.configureFirParser
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider


open class AbstractDeclarationsTest : BaseTestRunner() {
    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        commonFirWithPluginFrontendConfiguration()
        configureFirParser(FirParser.Psi)
        
        useConfigurators(::FirClassSuppliedTypeParametersPropertiesGenerationExtensionConfigurator)
    }

    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }
}

open class AbstractDiagnosticTest : BaseTestRunner() {
    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        commonFirWithPluginFrontendConfiguration()
        configureFirParser(FirParser.Psi)
        
        useConfigurators(::FirSuppliedTypeCheckersExtensionRegistrarConfigurator)
    }
    
    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }
}

open class AbstractFirCompleteTest : BaseTestRunner() {
    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        commonFirWithPluginFrontendConfiguration()
        configureFirParser(FirParser.Psi)
        
        useConfigurators(::FirSuppliedTypeCompleteExtensionRegistrarConfigurator)
    }
    
    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }
}
