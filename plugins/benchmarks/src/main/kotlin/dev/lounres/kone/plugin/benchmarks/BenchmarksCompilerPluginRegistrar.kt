/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks

import dev.lounres.kone.plugin.benchmarks.fir.FirBenchmarksExtensionRegistrar
import dev.lounres.kone.plugin.benchmarks.ir.BenchmarksIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter


@OptIn(ExperimentalCompilerApi::class)
class BenchmarksCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String get() = "dev.lounres.kone.plugin.benchmarks"
    
    override val supportsK2: Boolean get() = true
    
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
//        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirBenchmarksExtensionRegistrar())
        IrGenerationExtension.registerExtension(BenchmarksIrGenerationExtension())
    }
}