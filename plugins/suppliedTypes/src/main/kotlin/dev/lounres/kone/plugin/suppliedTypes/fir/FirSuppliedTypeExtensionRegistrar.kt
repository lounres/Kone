package dev.lounres.kone.plugin.suppliedTypes.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar


class FirSuppliedTypeExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::ClassSuppliedTypeParametersPropertiesGenerationExtension
        +::SuppliedTypeCheckersExtension
    }
}