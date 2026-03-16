/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.visitors.IrTransformer


@OptIn(UnsafeDuringIrConstructionAPI::class)
class SuppliableCallSubstitutionTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
    val suppliabilityCollectionVisitor: SuppliabilityCollectionVisitor,
) : IrTransformer<IrSymbol?>() {
    override fun visitDeclarationReference(expression: IrDeclarationReference, data: IrSymbol?): IrExpression {
        return super.visitDeclarationReference(expression, expression.symbol)
    }
    
    override fun visitConstructorCall(expression: IrConstructorCall, data: IrSymbol?): IrElement {
        val constructor = expression.symbol.owner
        require(suppliabilityCollectionVisitor.checkSuppliable(constructor))
        val supplianceConstructor = suppliabilityCollectionVisitor.mapSuppliableToSuppliance(constructor)
        
        return DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = data!!,
            startOffset = expression.startOffset,
            endOffset = expression.endOffset,
        ).run {
            irCallConstructor(
                callee = supplianceConstructor.symbol,
                typeArguments = expression.typeArguments.requireNoNulls(),
            ).apply {
                var initialParameterIndex = 0
                for ((parameterIndex, parameter) in supplianceConstructor.parameters.withIndex()) {
                    arguments[parameterIndex] = arguments[parameterIndex] ?: if (parameter.isSupplianceProvided) {
                        irCall(
                            callee = irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
                        )
                    } else {
                        constructor.parameters[initialParameterIndex++].defaultValue?.expression
                    }
                }
            }
        }
    }
    
    override fun visitCall(expression: IrCall, data: IrSymbol?): IrElement {
        val function = expression.symbol.owner
        require(suppliabilityCollectionVisitor.checkSuppliable(function))
        val supplianceFunction = suppliabilityCollectionVisitor.mapSuppliableToSuppliance(function)
        
        return DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = data!!,
            startOffset = expression.startOffset,
            endOffset = expression.endOffset,
        ).run {
            irCall(
                callee = supplianceFunction.symbol,
            ).apply {
                var initialParameterIndex = 0
                for ((parameterIndex, parameter) in supplianceFunction.parameters.withIndex()) {
                    arguments[parameterIndex] = arguments[parameterIndex] ?: if (parameter.isSupplianceProvided) {
                        irCall(
                            callee = irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol
                        )
                    } else {
                        function.parameters[initialParameterIndex++].defaultValue?.expression
                    }
                }
                typeArguments.clear()
                typeArguments += expression.typeArguments
            }
        }
    }
}