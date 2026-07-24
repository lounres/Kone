/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionApiInternals
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar


class FirContextsExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::FirLocalReceiversExpressionResolutionExtension
        +::FirLocalContextsExpressionResolutionExtension
        +::FirLocalUnwrapExpressionResolutionExtension
        +::FirKoneLocalUnwrapExpressionResolutionExtension
        +::FirContextArgumentCleaner
        
//        registerDiagnosticContainers(SuppliedTypeCheckersExtension.Errors)
    }
}