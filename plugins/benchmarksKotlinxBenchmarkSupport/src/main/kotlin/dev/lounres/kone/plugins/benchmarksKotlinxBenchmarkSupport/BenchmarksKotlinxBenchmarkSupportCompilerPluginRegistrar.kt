/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport

import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.fir.FirBenchmarksKotlinxBenchmarkSupportExtensionRegistrar
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.ir.BenchmarksKotlinxBenchmarkSupportFirBasedIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter


@OptIn(ExperimentalCompilerApi::class)
class BenchmarksKotlinxBenchmarkSupportCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String get() = "dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport"
    
    override val supportsK2: Boolean get() = true
    
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
//        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirBenchmarksKotlinxBenchmarkSupportExtensionRegistrar())
//        IrGenerationExtension.registerExtension(
//            BenchmarksKotlinxBenchmarkSupportIrGenerationExtension(
//                kotlinxBenchmarkJsExecutor = configuration[KotlinxBenchmarkJsExecutor.compilerConfigurationKey] ?: KotlinxBenchmarkJsExecutor.default,
//            )
//        )
        IrGenerationExtension.registerExtension(
            BenchmarksKotlinxBenchmarkSupportFirBasedIrGenerationExtension(
                kotlinxBenchmarkJsExecutor = configuration[KotlinxBenchmarkJsExecutor.compilerConfigurationKey] ?: KotlinxBenchmarkJsExecutor.default,
            )
        )
    }
}