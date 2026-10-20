/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.ir

import dev.lounres.kone.scope
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid


class ContextsScopeArgumentsMovingExtension(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
) : IrElementTransformerVoid() {
    override fun visitCall(expression: IrCall): IrExpression {
        when (expression.symbol) {
            irRuntimeReferences.receiversIrSimpleFunctionSymbol -> scope {
                if (expression.arguments.size != 2) return@scope
                val [vararg, lambda] = expression.arguments
                if (vararg !is IrVararg) return@scope
                if (lambda !is IrFunctionExpression) return@scope
                val localCallStatement = lambda.function.body?.statements?.first()
                if (localCallStatement !is IrCall || localCallStatement.symbol != irRuntimeReferences.localReceiversIrSimpleFunctionSymbol) return@scope
                val scopeVararg = localCallStatement.arguments[0] as IrVararg
                for ([index, argument] in vararg.elements.withIndex()) {
                    scopeVararg.elements[index] = argument
                }
                vararg.elements.clear()
            }
            irRuntimeReferences.contextsIrSimpleFunctionSymbol -> scope {
                if (expression.arguments.size != 2) return@scope
                val [vararg, lambda] = expression.arguments
                if (vararg !is IrVararg) return@scope
                if (lambda !is IrFunctionExpression) return@scope
                val localCallStatement = lambda.function.body?.statements?.first()
                if (localCallStatement !is IrCall || localCallStatement.symbol != irRuntimeReferences.localContextsIrSimpleFunctionSymbol) return@scope
                val scopeVararg = localCallStatement.arguments[0] as IrVararg
                for ([index, argument] in vararg.elements.withIndex()) {
                    scopeVararg.elements[index] = argument
                }
                vararg.elements.clear()
            }
            irRuntimeReferences.unwrapIrSimpleFunctionSymbol -> scope {
                if (expression.arguments.size != 2) return@scope
                val [_, vararg, lambda] = expression.arguments
                if (vararg !is IrVararg) return@scope
                if (lambda !is IrFunctionExpression) return@scope
                val localCallStatement = lambda.function.body?.statements?.first()
                if (localCallStatement !is IrCall || localCallStatement.symbol != irRuntimeReferences.localUnwrapIrSimpleFunctionSymbol) return@scope
                val scopeVararg = localCallStatement.arguments[0] as IrVararg
                for ([index, argument] in vararg.elements.withIndex()) {
                    scopeVararg.elements[index] = argument
                }
                vararg.elements.clear()
            }
        }
        return super.visitCall(expression)
    }
}