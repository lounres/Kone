/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners

import dev.lounres.kone.plugins.benchmarksRunners.fir.FirBenchmarksRunnersExtensionRegistrar
import dev.lounres.kone.plugins.benchmarksRunners.ir.BenchmarksRunnersFirBasedIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter


class BenchmarksRunnersCompilerPluginRegistrar : DevKitComponentRegistrar {
    override fun CompilerPluginRegistrar.ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
//        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        
        FirExtensionRegistrarAdapter.registerExtension(FirBenchmarksRunnersExtensionRegistrar())
//        IrGenerationExtension.registerExtension(
//            BenchmarksKotlinxBenchmarkSupportIrGenerationExtension(
//                kotlinxBenchmarkJsExecutor = configuration[KotlinxBenchmarkJsExecutor.compilerConfigurationKey] ?: KotlinxBenchmarkJsExecutor.default,
//            )
//        )
        IrGenerationExtension.registerExtension(
            BenchmarksRunnersFirBasedIrGenerationExtension()
        )
    }
}