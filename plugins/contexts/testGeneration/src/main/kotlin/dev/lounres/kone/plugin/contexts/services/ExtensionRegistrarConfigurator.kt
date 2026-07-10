/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalCompilerApi::class)

package dev.lounres.kone.plugin.contexts.services

import dev.lounres.kone.plugin.contexts.fir.DeclarationExtensionRegistrar
import dev.lounres.kone.plugin.contexts.fir.DiagnosticExtensionRegistrar
import dev.lounres.kone.plugin.contexts.fir.FirContextsExtensionRegistrar
import dev.lounres.kone.plugin.contexts.ir.ContextsIrGenerationExtension
import dev.lounres.kone.plugin.contexts.ir.TestIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar.ExtensionStorage
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices


class FirDeclarationsConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(DeclarationExtensionRegistrar())
    }
}

class FirDiagnosticConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(DiagnosticExtensionRegistrar())
    }
}

class FirCompleteExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirContextsExtensionRegistrar())
    }
}

class IrPartialExtensionRegistrarConfigurator(
    testServices: TestServices,
    private val lastPhase: UInt = UInt.MAX_VALUE,
) : EnvironmentConfigurator(testServices) {
    companion object {
        fun Constructor(
            lastPhase: UInt = UInt.MAX_VALUE,
        ): Constructor<IrPartialExtensionRegistrarConfigurator> =
            { testServices -> IrPartialExtensionRegistrarConfigurator(testServices = testServices, lastPhase = lastPhase) }
    }
    
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        FirExtensionRegistrarAdapter.registerExtension(FirContextsExtensionRegistrar())
        IrGenerationExtension.registerExtension(
            ContextsIrGenerationExtension(
                messageCollector = messageCollector,
                lastPhase = lastPhase
            )
        )
    }
}

class CompleteExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirContextsExtensionRegistrar())
        IrGenerationExtension.registerExtension(ContextsIrGenerationExtension(messageCollector))
    }
}

class TestExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirContextsExtensionRegistrar())
        IrGenerationExtension.registerExtension(TestIrGenerationExtension())
    }
}