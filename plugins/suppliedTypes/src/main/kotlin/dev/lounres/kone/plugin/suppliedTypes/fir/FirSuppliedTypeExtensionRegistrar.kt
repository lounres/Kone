/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar


class FirSuppliedTypeExtensionRegistrar(val forbidTopLevel: Boolean = true) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::SuppliedClassSupertypeGenerationExtension
        +::SuppliedTypesStoragePropertyGenerationExtension
//        +::SuppliableFunctionsDuplicatesGenerationExtension
//        +::SuppliableConstructorsDuplicatesGenerationExtension
        +{ session: FirSession -> SuppliedTypeCheckersExtension(session, forbidTopLevel) }
        
        registerDiagnosticContainers(SuppliedTypeCheckersExtension.Errors)
    }
}