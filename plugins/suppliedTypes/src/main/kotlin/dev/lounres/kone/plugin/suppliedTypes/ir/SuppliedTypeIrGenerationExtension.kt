/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


class SuppliedTypeIrGenerationExtension(
    private val messageCollector: MessageCollector,
    private val lastPhase: UInt = UInt.MAX_VALUE,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.transform(
            FunctionSuppliancesGenerationTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences
            ),
            null,
        )
        moduleFragment.transform(
            ConstructorSuppliancesGenerationTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences
            ),
            null,
        )
        
        if (currentPhase++ == lastPhase) return
        
        val suppliabilityMapper = SuppliabilityMapper(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            moduleFragment = moduleFragment,
        )
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.transform(
            SuppliedTypesStorageAccessorsTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                suppliabilityMapper = suppliabilityMapper,
            ),
            null
        )
        
        if (currentPhase++ == lastPhase) return
        
        supplyFunctionsParameters(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            suppliabilityMapper = suppliabilityMapper,
        )
        
        supplyConstructorsParameters(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            suppliabilityMapper = suppliabilityMapper,
        )
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.transform(
            SuppliableCallSubstitutionTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                suppliabilityMapper = suppliabilityMapper,
            ),
            null,
        )
        
        if (currentPhase++ == lastPhase) return
        
        supplyFunctionsBodies(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            suppliabilityMapper = suppliabilityMapper,
        )
        
        supplyConstructorsBodies(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            suppliabilityMapper = suppliabilityMapper,
        )
        
        moduleFragment.accept(
            SuppliableSingletonsSuppliedTypesStorageInitializerTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                suppliabilityMapper = suppliabilityMapper,
            ),
            null
        )
        
        if (currentPhase++ == lastPhase) return
        
        moduleFragment.transform(
            SuppliedTypeOfSubstitutionTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                suppliabilityMapper = suppliabilityMapper,
            ),
            SuppliedTypeOfSubstitutionTransformer.TransformationContext.INIT,
        )
        
        if (currentPhase++ == lastPhase) return
    }
}