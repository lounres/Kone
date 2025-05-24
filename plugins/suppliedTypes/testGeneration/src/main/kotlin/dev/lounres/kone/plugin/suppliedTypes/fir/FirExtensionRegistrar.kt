/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar


class ClassSuppliedTypeParametersPropertiesGenerationExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::ClassSuppliedTypeParametersPropertiesGenerationExtension
    }
}

class SuppliedTypeCheckersExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::SuppliedTypeCheckersExtension
    }
}