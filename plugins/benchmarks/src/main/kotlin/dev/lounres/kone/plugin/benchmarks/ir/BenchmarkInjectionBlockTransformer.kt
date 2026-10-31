/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.visitors.IrTransformer


class BenchmarkInjectionBlockTransformer(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
    private val irBuilder: IrBuilder,
    private val subdeclarationFunction: IrFunction,
    private val setupFunction: IrFunction,
    private val replacements: Map<IrSymbol, (IrExpression) -> IrExpression>,
) : IrTransformer<Nothing?>() {
    override fun visitMemberAccess(expression: IrMemberAccessExpression<*>, data: Nothing?): IrElement =
        when (expression.symbol) {
            irRuntimeReferences.blackholePropertyGetterIrSimpleFunctionSymbol ->
                benchmarksIrPluginException("Unexpected appearance of 'blackhole' in injection block.")
            irRuntimeReferences.benchmarkResultPropertyGetterIrSimpleFunctionSymbol ->
                benchmarksIrPluginException("Unexpected appearance of 'benchmarkResult' in injection block.")
            irRuntimeReferences.injectFunctionIrSimpleFunctionSymbol -> {
                val lambda = expression.arguments.singleOrNull() as? IrFunctionExpression ?: benchmarksIrPluginException("Unexpected arguments of 'inject' function call")
                irBuilder.irCall(irRuntimeReferences.runIrSimpleFunctionSymbol).apply {
                    type = expression.type
                    typeArguments[0] = expression.type
                    arguments[0] = lambda
                }
            }
            irRuntimeReferences.injectDisposableFunctionIrSimpleFunctionSymbol -> {
                val lambda = expression.arguments.singleOrNull() as? IrFunctionExpression ?: benchmarksIrPluginException("Unexpected arguments of 'inject' function call")
                irBuilder.run {
                    irCall(irRuntimeReferences.disposableInjectionValuePropertyGetterIrSimpleFunctionSymbol).apply {
                        type = expression.type
                        arguments[0] = irCall(irRuntimeReferences.runIrSimpleFunctionSymbol).apply {
                            type = lambda.function.returnType
                            typeArguments[0] = lambda.function.returnType
                            arguments[0] = lambda
                        }
                    }
                    
                }
            }
            in replacements -> replacements[expression.symbol]!!(expression)
            else -> super.visitMemberAccess(expression, data)
        }
    
    override fun visitDeclarationReference(expression: IrDeclarationReference, data: Nothing?): IrExpression =
        when (expression.symbol) {
            in replacements -> replacements[expression.symbol]!!(expression)
            else -> super.visitDeclarationReference(expression, data)
        }
    
    override fun visitDeclaration(declaration: IrDeclarationBase, data: Nothing?): IrStatement {
        if (declaration.parent === subdeclarationFunction) declaration.parent = setupFunction
        return super.visitDeclaration(declaration, data)
    }
}