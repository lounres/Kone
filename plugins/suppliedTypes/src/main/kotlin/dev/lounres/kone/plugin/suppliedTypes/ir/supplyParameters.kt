/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGetObjectValue
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name


fun supplyFunctionsParameters(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    for ((suppliance, suppliable) in suppliabilityMapper.moduleFunctionsSupplianceToSuppliableMapping) {
        val supplianceSupplyTypeParameters = suppliable.typeParameters.withIndex().filter { it.value.isSupply }.map { suppliance.typeParameters[it.index] }
        val parametersSubstitutionTransformer = ParametersSubstitutionTransformer(
            typeParametersSubstitution = suppliable.typeParameters.zip(suppliance.typeParameters).toMap(),
            valueParametersSubstitution = suppliable.parameters.zip(suppliance.parameters.filter { !it.isSupplianceProvided }).toMap(),
        )
        
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
                            typeArguments[0] = supplianceSupplyTypeParameters[supplyTypeArgumentCounter].defaultType
                        }
                    )
                }
                supplyTypeArgumentCounter++
            } else {
                parameter.defaultValue = suppliable.parameters[parameterCounter].defaultValue
                    ?.deepCopyWithSymbols(initialParent = suppliance)
                    ?.transform(parametersSubstitutionTransformer, null)
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
        val classSupplyTypeParameters = suppliance.parentAsClass.typeParameters.filter { it.isSupply }
        val parametersSubstitutionTransformer = ParametersSubstitutionTransformer(
            valueParametersSubstitution = suppliable.parameters.zip(suppliance.parameters.filter { !it.isSupplianceProvided }).toMap(),
        )
        
        var parameterCounter = 0
        var supplyTypeArgumentCounter = 0
        for (parameter in suppliance.parameters)
            if (parameter.isSupplianceProvided) {
                parameter.defaultValue = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = parameter.symbol,
                ).run {
                    if (parameter.name == Name.special("<supplianceStub>"))
                        irExprBody(
                            value = irGetObjectValue(
                                type = irRuntimeReferences.noSuppliedTypeParameterInClassStubIrClassSymbol.defaultType,
                                classSymbol = irRuntimeReferences.noSuppliedTypeParameterInClassStubIrClassSymbol,
                            )
                        )
                    else
                        irExprBody(
                            value = irCall(
                                callee = irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
                            ).apply {
                                typeArguments[0] = classSupplyTypeParameters[supplyTypeArgumentCounter].defaultType
                            }
                        )
                }
                supplyTypeArgumentCounter++
            } else {
                parameter.defaultValue = suppliable.parameters[parameterCounter].defaultValue
                    ?.deepCopyWithSymbols(initialParent = suppliance)
                    ?.transform(parametersSubstitutionTransformer, null)
                parameterCounter++
            }
    }
}