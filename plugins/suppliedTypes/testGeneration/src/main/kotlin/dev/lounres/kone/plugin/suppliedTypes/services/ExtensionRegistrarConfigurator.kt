@file:OptIn(ExperimentalCompilerApi::class)

package dev.lounres.kone.plugin.suppliedTypes.services

import dev.lounres.kone.plugin.suppliedTypes.fir.ClassSuppliedTypeParametersPropertiesGenerationExtensionRegistrar
import dev.lounres.kone.plugin.suppliedTypes.fir.FirSuppliedTypeExtensionRegistrar
import dev.lounres.kone.plugin.suppliedTypes.fir.SuppliedTypeCheckersExtensionRegistrar
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypeIrGenerationExtension
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypePartialIrGenerationExtension1
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypePartialIrGenerationExtension2
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypePartialIrGenerationExtension3
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypePartialIrGenerationExtension4
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypePartialIrGenerationExtension5
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar.ExtensionStorage
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter


class FirClassSuppliedTypeParametersPropertiesGenerationExtensionConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(ClassSuppliedTypeParametersPropertiesGenerationExtensionRegistrar())
    }
}

class FirSuppliedTypeCheckersExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(SuppliedTypeCheckersExtensionRegistrar())
    }
}

class FirSuppliedTypeCompleteExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
    }
}

class IrPartialExtensionRegistrarConfigurator1(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
        IrGenerationExtension.registerExtension(SuppliedTypePartialIrGenerationExtension1(messageCollector))
    }
}

class IrPartialExtensionRegistrarConfigurator2(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
        IrGenerationExtension.registerExtension(SuppliedTypePartialIrGenerationExtension2(messageCollector))
    }
}

class IrPartialExtensionRegistrarConfigurator3(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
        IrGenerationExtension.registerExtension(SuppliedTypePartialIrGenerationExtension3(messageCollector))
    }
}

class IrPartialExtensionRegistrarConfigurator4(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
        IrGenerationExtension.registerExtension(SuppliedTypePartialIrGenerationExtension4(messageCollector))
    }
}

class IrPartialExtensionRegistrarConfigurator5(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
        IrGenerationExtension.registerExtension(SuppliedTypePartialIrGenerationExtension5(messageCollector))
    }
}

class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
        IrGenerationExtension.registerExtension(SuppliedTypeIrGenerationExtension(messageCollector))
    }
}