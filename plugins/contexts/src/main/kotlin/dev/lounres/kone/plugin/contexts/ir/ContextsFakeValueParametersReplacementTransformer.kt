/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid


class ContextsFakeValueParametersReplacementTransformer(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
) : IrElementTransformerVoid() {
    private val declarationSymbolsStack = mutableListOf<IrSymbol>()
    data class UsedOrUnwrappedExpression(
        val type: IrType,
        val producer: () -> IrExpression,
    )
    private val usedAndUnwrappedExpressionsStack = mutableListOf<MutableList<UsedOrUnwrappedExpression>>()
    
    private inline fun <T> withinScope(declaration: IrSymbolOwner, block: () -> T): T {
        declarationSymbolsStack.push(declaration.symbol)
        val result = block()
        declarationSymbolsStack.pop()
        return result
    }
    
    override fun visitFile(declaration: IrFile): IrFile =
        withinScope(declaration) { super.visitFile(declaration) }
    
    override fun visitClass(declaration: IrClass): IrStatement =
        withinScope(declaration) { super.visitClass(declaration) }
    
    override fun visitProperty(declaration: IrProperty): IrStatement =
        withinScope(declaration) { super.visitProperty(declaration) }
    
    override fun visitField(declaration: IrField): IrStatement =
        withinScope(declaration) { super.visitField(declaration) }
    
    override fun visitFunction(declaration: IrFunction): IrStatement =
        withinScope(declaration) { super.visitFunction(declaration) }
    
    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer): IrStatement =
        withinScope(declaration) { super.visitAnonymousInitializer(declaration) }
    
    override fun visitValueParameter(declaration: IrValueParameter): IrStatement =
        withinScope(declaration) { super.visitValueParameter(declaration) }
    
    override fun visitScript(declaration: IrScript): IrStatement =
        withinScope(declaration) { super.visitScript(declaration) }
    
    private inline fun <T> withinBlock(block: (MutableList<UsedOrUnwrappedExpression>) -> T): T {
        val expressions = mutableListOf<UsedOrUnwrappedExpression>()
        usedAndUnwrappedExpressionsStack.push(expressions)
        val result = block(expressions)
        usedAndUnwrappedExpressionsStack.pop()
        return result
    }
    
    private fun <T : IrStatementContainer> visitStatementContainer(container: T) = withinBlock { expressions ->
        val iterator = container.statements.listIterator()
        while (iterator.hasNext()) {
            val statement = iterator.next()
            val newStatement = statement.transform(this, null)
            if (newStatement is IrCall) when(newStatement.symbol) {
                irRuntimeReferences.useLocallyAsExtensionReceiversIrSimpleFunctionSymbol -> {
                    iterator.remove()
                    check(newStatement.arguments.size == 2)
                    val vararg = newStatement.arguments.last() as IrVararg
                    for (context in vararg.elements) {
                        if (context !is IrExpression) continue
                        val contextVariable = Scope(declarationSymbolsStack.last()).createTemporaryVariable(context)
                        iterator.add(contextVariable)
                        expressions.add(
                            UsedOrUnwrappedExpression(contextVariable.type) {
                                IrGetValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, contextVariable.symbol)
                            }
                        )
                    }
                }
                irRuntimeReferences.unwrapLocallyAsExtensionReceiversIrSimpleFunctionSymbol -> {
                    iterator.remove()
                    check(newStatement.arguments.size == 2)
                    val vararg = newStatement.arguments.last() as IrVararg
                    for (holder in vararg.elements) {
                        if (holder !is IrExpression) continue
                        val holderVariable = Scope(declarationSymbolsStack.last()).createTemporaryVariable(holder)
                        iterator.add(holderVariable)
                        
                        when(val type = holder.type) {
                            is IrDynamicType -> {}
                            is IrErrorType -> {}
                            is IrSimpleType -> run {
                                val classSymbol = type.classifier as? IrClassSymbol ?: return@run
                                for (property in classSymbol.owner.properties) {
                                    if (
                                        !property.hasAnnotation(irRuntimeReferences.koneContextHolderContextAnnotationIrClassSymbol)
                                        && property.overriddenSymbols.all { !it.owner.hasAnnotation(irRuntimeReferences.koneContextHolderContextAnnotationIrClassSymbol) }
                                    ) continue
                                    val getter = property.getter ?: continue
                                    if (getter.parameters.size != 1 || getter.parameters[0].kind != IrParameterKind.DispatchReceiver) continue
                                    val type = getter.returnType.substitute(classSymbol.owner.typeParameters, type.arguments.map { it.typeOrNull ?: contextsIrPluginException() })
                                    expressions.add(
                                        UsedOrUnwrappedExpression(type) {
                                            IrCallImpl.fromSymbolOwner(
                                                startOffset = UNDEFINED_OFFSET,
                                                endOffset = UNDEFINED_OFFSET,
                                                type = type,
                                                symbol = getter.symbol,
                                            ).apply {
                                                arguments[0] = IrGetValueImpl(
                                                    UNDEFINED_OFFSET,
                                                    UNDEFINED_OFFSET,
                                                    holderVariable.symbol
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        container
    }
    
    override fun visitContainerExpression(expression: IrContainerExpression) = visitStatementContainer(expression)
    override fun visitBlockBody(body: IrBlockBody) = visitStatementContainer(body)
    
    override fun visitErrorCallExpression(expression: IrErrorCallExpression): IrExpression {
        if (expression.description !in fakeValueParametersErrorCallsDescriptions)
            return super.visitErrorCallExpression(expression)
        
        return usedAndUnwrappedExpressionsStack
            .asReversed()
            .firstNotNullOfOrNull { variables -> variables.lastOrNull { it.type == expression.type } }
            ?.producer?.invoke()
            ?: super.visitErrorCallExpression(expression)
    }
}