/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptVoid


class SuppliedTypePartialIrGenerationExtension(
    private val messageCollector: MessageCollector,
    private val phases: List<SuppliedTypeIrGenerationExtension.Phase>,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences: IrRuntimeReferences = IrRuntimeReferences(pluginContext)
        val suppliabilityCollectionVisitor: SuppliabilityCollectionVisitor = SuppliabilityCollectionVisitor(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
        )
        
        moduleFragment.acceptVoid(suppliabilityCollectionVisitor)
        
        for (phase in phases) phase(moduleFragment, pluginContext, irRuntimeReferences, suppliabilityCollectionVisitor)
    }
}