/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration


@OptIn(ExperimentalCompilerApi::class)
class ContextsKeysCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "dev.lounres.kone.plugin.contextsKeys"
    
    override val pluginOptions: Collection<CliOption> = listOf()
    
    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) = when (option.optionName) {
        else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
    }
}