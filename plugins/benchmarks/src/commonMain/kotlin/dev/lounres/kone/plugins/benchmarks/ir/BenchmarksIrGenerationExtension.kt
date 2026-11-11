/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarks.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction


class BenchmarksIrGenerationExtension(
    private val lastPhase: UInt = UInt.MAX_VALUE,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        val benchmarkToGeneratedClass = mutableMapOf<IrSimpleFunction, GeneratedBenchmarkClassDescription>()
        
        moduleFragment.accept(
            BenchmarkClassGenerationTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                benchmarkToGeneratedClass = benchmarkToGeneratedClass,
            ),
            null
        )
        
        if (currentPhase++ == lastPhase) return
    }
}