/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfigurationKey


enum class KotlinxBenchmarkJsExecutor(
    val cliOptionValueName: String,
) {
    BenchmarkJs(
        cliOptionValueName = "BenchmarkJs"
    ),
    BuiltIn(
        cliOptionValueName = "BuiltIn"
    ),
    ;
    companion object {
        val default: KotlinxBenchmarkJsExecutor = BenchmarkJs
        val compilerConfigurationKey = CompilerConfigurationKey<KotlinxBenchmarkJsExecutor>("koneBenchmarksKotlinxBenchmarkSupportJsExecutor")
        val cliOption = CliOption(
            optionName = "kotlinxBenchmarkJsExecutor",
            valueDescription = "${entries.joinToString(separator = ",", prefix = "<", postfix = ">") { it.cliOptionValueName }} (default is ${default.cliOptionValueName})",
            description = "What kotlinx-benchmark executor to use.",
            required = false,
            allowMultipleOccurrences = false,
        )
    }
}