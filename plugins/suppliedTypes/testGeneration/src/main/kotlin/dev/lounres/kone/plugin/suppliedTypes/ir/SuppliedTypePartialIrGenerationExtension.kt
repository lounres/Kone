/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


fun phase1(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext, irRuntimeReferences: IrRuntimeReferences) {
    moduleFragment.transform(
        ClassSuppliedTypeParametersPropertiesGenerationTransformer(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
        ),
        null
    )
}

fun phase2(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext, irRuntimeReferences: IrRuntimeReferences) {
    moduleFragment.transform(
        ClassSuppliedTypeParametersOverridesGenerationTransformer(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
        ),
        null
    )
}

fun phase3(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext, irRuntimeReferences: IrRuntimeReferences) {
    moduleFragment.transform(
        FunctionsWithSuppliedTypeParametersModificationTransformer(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
        ),
        null
    )
}

fun phase4(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext, irRuntimeReferences: IrRuntimeReferences) {
    moduleFragment.transform(
        FunctionsWithSuppliedTypeParametersUsageTransformer(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
        ),
        null
    )
}

fun phase5(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext, irRuntimeReferences: IrRuntimeReferences) {
    moduleFragment.transform(
        SuppliedTypeOfSubstitutionTransformer(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
        ),
        SuppliedTypeOfSubstitutionTransformer.TransformationContext(emptyMap(), emptyMap(), null)
    )
}

class SuppliedTypePartialIrGenerationExtension1(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        phase1(moduleFragment, pluginContext, irRuntimeReferences)
    }
}

class SuppliedTypePartialIrGenerationExtension2(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        phase1(moduleFragment, pluginContext, irRuntimeReferences)
        phase2(moduleFragment, pluginContext, irRuntimeReferences)
    }
}

class SuppliedTypePartialIrGenerationExtension3(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        phase1(moduleFragment, pluginContext, irRuntimeReferences)
        phase2(moduleFragment, pluginContext, irRuntimeReferences)
        phase3(moduleFragment, pluginContext, irRuntimeReferences)
    }
}

class SuppliedTypePartialIrGenerationExtension4(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        phase1(moduleFragment, pluginContext, irRuntimeReferences)
        phase2(moduleFragment, pluginContext, irRuntimeReferences)
        phase3(moduleFragment, pluginContext, irRuntimeReferences)
        phase4(moduleFragment, pluginContext, irRuntimeReferences)
    }
}

class SuppliedTypePartialIrGenerationExtension5(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        phase1(moduleFragment, pluginContext, irRuntimeReferences)
        phase2(moduleFragment, pluginContext, irRuntimeReferences)
        phase3(moduleFragment, pluginContext, irRuntimeReferences)
        phase4(moduleFragment, pluginContext, irRuntimeReferences)
        phase5(moduleFragment, pluginContext, irRuntimeReferences)
    }
}