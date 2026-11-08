/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.fir

import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.platform.jvm.isJvm


class DeclarationExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +FirDeclarationGenerationExtension.Factory {
            if (!it.moduleData.isCommon && !it.moduleData.platform.isJvm()) BenchmarksKotlinxBenchmarkSupportEntryPointGenerationExtension(it)
            else object : FirDeclarationGenerationExtension(it) {}
        }
    }
}

class DiagnosticExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::BenchmarksKotlinxBenchmarkSupportCheckersExtension
        
        registerDiagnosticContainers(BenchmarksKotlinxBenchmarkSupportCheckersExtension.Errors)
    }
}