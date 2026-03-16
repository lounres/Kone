/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar


class DeclarationExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::SuppliedClassSupertypeGenerationExtension
        +::SuppliedTypesStoragePropertyGenerationExtension
        +::SuppliableFunctionsDuplicatesGenerationExtension
        +::SuppliableConstructorsDuplicatesGenerationExtension
    }
}

class DiagnosticExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::SuppliedTypeCheckersExtension
    }
}