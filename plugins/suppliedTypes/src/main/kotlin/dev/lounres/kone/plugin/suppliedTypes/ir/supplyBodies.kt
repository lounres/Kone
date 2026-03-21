/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.util.statements


fun supplyFunctionsBodies(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    for ((suppliance, suppliable) in suppliabilityMapper.moduleFunctionsSupplianceToSuppliableMapping) {
        suppliance.body = suppliable.body/*!!.deepCopyWithSymbols(initialParent = suppliance)*/
    }
}

fun supplyConstructorsBodies(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    for (suppliance in suppliabilityMapper.moduleConstructorsSupplianceToSuppliableMapping.keys) {
        val oldBody = suppliance.body!!
        suppliance.body = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = suppliance.symbol,
        ).run {
            irBlockBody {
                for (statement in oldBody.statements) +statement
                TODO()
            }
        }
    }
}