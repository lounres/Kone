/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


class ContextsIrGenerationExtension(
//    private val messageCollector: MessageCollector,
    private val lastPhase: UInt = UInt.MAX_VALUE,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.accept(
            ContextsFakeValueParametersReplacementTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )
        
        if (currentPhase++ == lastPhase) return
    }
}