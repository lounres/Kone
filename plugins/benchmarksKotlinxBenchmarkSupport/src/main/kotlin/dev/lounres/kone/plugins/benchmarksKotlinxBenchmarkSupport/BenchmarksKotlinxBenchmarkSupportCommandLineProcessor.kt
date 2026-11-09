/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration


@OptIn(ExperimentalCompilerApi::class)
class BenchmarksKotlinxBenchmarkSupportCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport"
    
    override val pluginOptions: Collection<CliOption> = listOf(
        KotlinxBenchmarkJsExecutor.cliOption
    )
    
    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) = when (option.optionName) {
        KotlinxBenchmarkJsExecutor.cliOption.optionName -> {
            val kotlinxBenchmarkJsExecutor = KotlinxBenchmarkJsExecutor.entries.firstOrNull { it.cliOptionValueName == value } ?: throw CliOptionProcessingException("kotlinx-benchmark JS executor '$value' not found")
            configuration.put(KotlinxBenchmarkJsExecutor.compilerConfigurationKey, kotlinxBenchmarkJsExecutor)
        }
        else -> throw CliOptionProcessingException("Unexpected config option ${option.optionName}")
    }
}