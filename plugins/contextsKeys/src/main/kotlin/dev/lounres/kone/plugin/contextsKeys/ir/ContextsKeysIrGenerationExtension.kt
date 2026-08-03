/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.ir

import dev.lounres.kone.plugin.suppliedTypes.ir.IrRuntimeReferences as SuppliedTypesIrRuntimeReferences
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


class ContextsKeysIrGenerationExtension(
//    private val messageCollector: MessageCollector,
    private val lastPhase: UInt = MAX_VALUE,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val suppliedTypesIrRuntimeReferences = SuppliedTypesIrRuntimeReferences(pluginContext)
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.transformChildren(
            ContextsKeysConstructorsFillingIrTransformer(
                pluginContext = pluginContext,
                suppliedTypesIrRuntimeReferences = suppliedTypesIrRuntimeReferences,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.transformChildren(
            ContextsKeysToStringFillingIrTransformer(
                pluginContext = pluginContext,
                suppliedTypesIrRuntimeReferences = suppliedTypesIrRuntimeReferences,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.transformChildren(
            ContextsKeysImpliedKeysFillingIrTransformer(
                pluginContext = pluginContext,
                suppliedTypesIrRuntimeReferences = suppliedTypesIrRuntimeReferences,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )
        
        if (currentPhase++ == lastPhase) return
    }
}