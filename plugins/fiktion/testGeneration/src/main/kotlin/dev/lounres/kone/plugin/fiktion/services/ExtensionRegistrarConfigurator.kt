/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalCompilerApi::class)

package dev.lounres.kone.plugin.fiktion.services

import dev.lounres.kone.plugin.fiktion.fir.DeclarationExtensionRegistrar
import dev.lounres.kone.plugin.fiktion.fir.DiagnosticExtensionRegistrar
import dev.lounres.kone.plugin.fiktion.fir.FirFiktionExtensionRegistrar
import dev.lounres.kone.plugin.fiktion.ir.fiktionIrGenerationExtension
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
        
        FirExtensionRegistrarAdapter.registerExtension(FirFiktionExtensionRegistrar())
    }
}

//class IrPartialExtensionRegistrarConfigurator(
//    testServices: TestServices,
//    private val phases: List<FiktionIrGenerationExtension.Phase>,
//) : EnvironmentConfigurator(testServices) {
//    companion object {
//        fun Constructor(
//            phases: List<FiktionIrGenerationExtension.Phase>
//        ): Constructor<IrPartialExtensionRegistrarConfigurator> =
//            { testServices -> IrPartialExtensionRegistrarConfigurator(testServices = testServices, phases = phases) }
//    }
//
//    override fun ExtensionStorage.registerCompilerExtensions(
//        module: TestModule,
//        configuration: CompilerConfiguration
//    ) {
//        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
//
//        FirExtensionRegistrarAdapter.registerExtension(FirFiktionExtensionRegistrar())
//        IrGenerationExtension.registerExtension(
//            FiktionPartialIrGenerationExtension(
//                messageCollector = messageCollector,
//                phases = phases
//            )
//        )
//    }
//}

abstract class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    protected abstract val eraseFiktion: Boolean
    override fun ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirFiktionExtensionRegistrar())
        IrGenerationExtension.registerExtension(fiktionIrGenerationExtension(messageCollector, eraseFiktion))
    }
}

class ErasureExtensionRegistrarConfigurator(testServices: TestServices) : ExtensionRegistrarConfigurator(testServices) {
    override val eraseFiktion: Boolean get() = true
}

class InterceptionExtensionRegistrarConfigurator(testServices: TestServices) : ExtensionRegistrarConfigurator(testServices) {
    override val eraseFiktion: Boolean get() = false
}