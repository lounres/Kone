/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


class SuppliedTypePartialIrGenerationExtension(
    private val messageCollector: MessageCollector,
    private val phases: List<SuppliedTypeIrGenerationExtension.Phase>,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences: IrRuntimeReferences = IrRuntimeReferences(pluginContext)
        val suppliabilityMapper: SuppliabilityMapper = SuppliabilityMapper(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            moduleFragment = moduleFragment
        )
        
        for (phase in phases) phase(moduleFragment, pluginContext, irRuntimeReferences, suppliabilityMapper)
    }
}