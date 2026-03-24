/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.visitors.IrTransformer


class SuppliableCallSubstitutionTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
    val suppliabilityMapper: SuppliabilityMapper,
) : IrTransformer<IrSymbol?>() {
    override fun visitDeclaration(declaration: IrDeclarationBase, data: IrSymbol?): IrStatement =
        super.visitDeclaration(declaration, declaration.symbol)
    
    override fun visitConstructorCall(expression: IrConstructorCall, data: IrSymbol?): IrElement {
        val suppliable = expression.symbol.owner
        val suppliance = suppliabilityMapper.mapSuppliableToSupplianceOrNull(suppliable) ?: return super.visitConstructorCall(expression, data)
        val suppliedTypeParametersIndices = suppliable.parentAsClass.typeParameters.withIndex().filter { it.value.isSupply }.map { it.index }
        
        return super.visitConstructorCall(
            DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = data!!,
                startOffset = expression.startOffset,
                endOffset = expression.endOffset,
            ).run {
                irCallConstructor(
                    callee = suppliance.symbol,
                    typeArguments = expression.typeArguments.requireNoNulls(),
                ).apply {
                    var initialParameterIndex = 0
                    for ((parameterIndex, parameter) in suppliance.parameters.withIndex()) {
                        if (parameter.isSupplianceProvided) {
                            arguments[parameterIndex] = arguments[parameterIndex] ?: irCall(
                                callee = irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
                            ).apply {
                                typeArguments[0] = expression.typeArguments[suppliedTypeParametersIndices[parameterIndex - initialParameterIndex]]
                            }
                        } else {
                            arguments[parameterIndex] = expression.arguments[parameterIndex] // ?: suppliable.parameters[initialParameterIndex].defaultValue?.expression?.deepCopyWithSymbols()
                            initialParameterIndex++
                        }
                    }
                }
            },
            data,
        )
    }
    
    override fun visitCall(expression: IrCall, data: IrSymbol?): IrElement {
        val suppliable = expression.symbol.owner
        val suppliance = suppliabilityMapper.mapSuppliableToSupplianceOrNull(suppliable) ?: return super.visitCall(expression, data)
        val suppliedTypeParametersIndices = suppliable.typeParameters.withIndex().filter { it.value.isSupply }.map { it.index }
        
        return super.visitCall(
            DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = data!!,
                startOffset = expression.startOffset,
                endOffset = expression.endOffset,
            ).run {
                irCall(
                    callee = suppliance.symbol,
                ).apply {
                    var initialParameterIndex = 0
                    for ((parameterIndex, parameter) in suppliance.parameters.withIndex()) {
                        if (parameter.isSupplianceProvided) {
                            arguments[parameterIndex] = arguments[parameterIndex] ?: irCall(
                                callee = irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
                            ).apply {
                                typeArguments[0] = expression.typeArguments[suppliedTypeParametersIndices[parameterIndex - initialParameterIndex]]
                            }
                        } else {
                            arguments[parameterIndex] = expression.arguments[parameterIndex] // ?: suppliable.parameters[initialParameterIndex].defaultValue?.expression?.deepCopyWithSymbols()
                            initialParameterIndex++
                        }
                    }
                    typeArguments.clear()
                    typeArguments += expression.typeArguments
                }
            },
            data,
        )
    }
}