/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey


@OptIn(ExperimentalCompilerApi::class)
class FiktionCommandLineProcessor : CommandLineProcessor {
    companion object {
        private const val CLI_OPTION_ERASE_FIKTION_NAME = "erase-fiktion"
        val ERASE_FIKTION = CompilerConfigurationKey<Boolean>(CLI_OPTION_ERASE_FIKTION_NAME)
    }
    
    override val pluginId: String = "dev.lounres.kone.plugin.fiktion"
    
    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = CLI_OPTION_ERASE_FIKTION_NAME,
            valueDescription = "To enable erase of '@Fiktion' entities",
            description = "Enables or disables erase of '@Fiktion' entities. Enable it for production use or disable it for test environments",
            required = false,
            allowMultipleOccurrences = false
        )
    )
    
    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        when (option.optionName) {
            CLI_OPTION_ERASE_FIKTION_NAME -> configuration.put(ERASE_FIKTION, value.toBoolean())
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}