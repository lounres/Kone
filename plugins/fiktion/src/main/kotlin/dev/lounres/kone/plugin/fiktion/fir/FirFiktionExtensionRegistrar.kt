/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar


class FirFiktionExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::FiktionCheckersExtension
        
        registerDiagnosticContainers(FiktionCheckersExtension.Errors)
    }
}