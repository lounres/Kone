/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.ir

import dev.lounres.kone.plugins.benchmarksRunners.entryPointCallableId
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.visitors.acceptVoid


class BenchmarksRunnersFirBasedIrGenerationExtension(
    private val lastPhase: UInt = MAX_VALUE,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        val platform = moduleFragment.descriptor.platform
        val simplePlatform = platform?.singleOrNull()
        
        val runnerDescriptions = buildList {
            moduleFragment.acceptVoid(
                KotlinxRunnerFunctionsCollectorVisitor(
                    runnerDescriptions = this
                )
            )
        }
        
        val entryPointFunction = moduleFragment
            .files
            .filter { it.packageFqName == entryPointCallableId.packageName }
            .flatMap { it.declarations }
            .filterIsInstance<IrSimpleFunction>()
            .filter {
                it.name == entryPointCallableId.callableName && it.parameters.size == 1
            }
            .single()
        
        entryPointFunction.body = entryPointBody(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            entryPointFunction = entryPointFunction,
            runnerDescriptions = runnerDescriptions,
        )
        
        if (currentPhase++ == lastPhase) return
    }
}