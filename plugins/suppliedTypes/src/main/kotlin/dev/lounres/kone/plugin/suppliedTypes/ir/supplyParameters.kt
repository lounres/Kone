/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.parentAsClass


fun supplyFunctionsParameters(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    for ((suppliance, suppliable) in suppliabilityMapper.moduleFunctionsSupplianceToSuppliableMapping) {
        var parameterCounter = 0
        var supplyTypeArgumentCounter = 0
        for (parameter in suppliance.parameters)
            if (parameter.isSupplianceProvided) {
                parameter.defaultValue = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = parameter.symbol,
                ).run {
                    irExprBody(
                        value = irCall(
                            callee = irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
                        ).apply {
                            typeArguments[0] = suppliance.typeParameters[supplyTypeArgumentCounter].defaultType
                        }
                    )
                }
                supplyTypeArgumentCounter++
            } else {
                parameter.defaultValue = suppliable.parameters[parameterCounter].defaultValue?.deepCopyWithSymbols(initialParent = suppliance)
                parameterCounter++
            }
    }
}

fun supplyConstructorsParameters(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    for ((suppliance, suppliable) in suppliabilityMapper.moduleConstructorsSupplianceToSuppliableMapping) {
        var parameterCounter = 0
        var supplyTypeArgumentCounter = 0
        for (parameter in suppliance.parameters)
            if (parameter.isSupplianceProvided) {
                parameter.defaultValue = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = parameter.symbol,
                ).run {
                    irExprBody(
                        value = irCall(
                            callee = irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
                        ).apply {
                            typeArguments[0] = suppliance.parentAsClass.typeParameters[supplyTypeArgumentCounter].defaultType
                        }
                    )
                }
                supplyTypeArgumentCounter++
            } else {
                parameter.defaultValue = suppliable.parameters[parameterCounter].defaultValue?.deepCopyWithSymbols(initialParent = suppliance)
                parameterCounter++
            }
    }
}