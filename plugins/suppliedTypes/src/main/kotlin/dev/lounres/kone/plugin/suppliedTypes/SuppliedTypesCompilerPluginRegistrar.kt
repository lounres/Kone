/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes

import dev.lounres.kone.plugin.suppliedTypes.fir.FirSuppliedTypeExtensionRegistrar
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypeIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter


@OptIn(ExperimentalCompilerApi::class)
class SuppliedTypesCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String
        get() = TODO("Not yet implemented")
    
    override val supportsK2: Boolean get() = true
    
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirSuppliedTypeExtensionRegistrar())
        IrGenerationExtension.registerExtension(SuppliedTypeIrGenerationExtension(messageCollector))
    }
}