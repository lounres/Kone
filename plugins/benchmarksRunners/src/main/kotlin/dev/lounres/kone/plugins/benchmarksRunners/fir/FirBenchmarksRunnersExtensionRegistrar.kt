/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar


class FirBenchmarksRunnersExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::BenchmarksRunnersEntryPointGenerationExtension
        +::BenchmarksRunnersCheckersExtension
        
        registerDiagnosticContainers(BenchmarksRunnersCheckersExtension.Errors)
    }
}