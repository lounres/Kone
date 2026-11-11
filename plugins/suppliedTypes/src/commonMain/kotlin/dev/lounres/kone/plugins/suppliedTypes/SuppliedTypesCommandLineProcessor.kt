/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.suppliedTypes

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitCLP
import org.jetbrains.kotlin.config.CompilerConfiguration


class SuppliedTypesCommandLineProcessor : DevKitCLP {
    override val pluginOptions: Collection<CliOption> = listOf()
    
    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) = when (option.optionName) {
        else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
    }
}