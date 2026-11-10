/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.ir

import dev.lounres.kone.plugins.benchmarksRunners.entryPointCallableId
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irIfThen
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.backend.js.utils.isDispatchReceiver
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irAs
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irEqualsNull
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irNotEquals
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irRichFunctionReference
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.builders.irWhen
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.invokeFun
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


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