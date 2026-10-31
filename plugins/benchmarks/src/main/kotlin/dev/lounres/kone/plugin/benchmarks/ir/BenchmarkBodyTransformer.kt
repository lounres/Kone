/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.ir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addField
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.Name


class BenchmarkBodyTransformer(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
    private val irBuilder: IrBuilder,
    private val benchmarkClass: IrClass,
    private val benchmarkFunctionDispatchReceiver: IrValueParameter,
    private val benchmarkFunctionBlackholeParameter: IrValueParameter,
//    private val subdeclarationIrBlockBodyBuilder: IrBlockBodyBuilder,
    private val setupFunctionDispatchReceiver: IrValueParameter,
    private val setupFunctionIrBlockBodyBuilder: IrBlockBodyBuilder,
    private val tearDownFunctionDispatchReceiver: IrValueParameter,
    private val tearDownFunctionIrBlockBodyBuilder: IrBlockBodyBuilder,
    private val benchmarkBodyReplacements: Map<IrSymbol, (IrMemberAccessExpression<*>) -> IrExpression>,
    injectionBlockReplacements: Map<IrSymbol, (IrMemberAccessExpression<*>) -> IrExpression>,
) : IrTransformer<Nothing?>() {
    private object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "BenchmarkBodyTransformer.Key"
    }
    private val pluginDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(Key)
    
    private val injectionBlockReplacements = injectionBlockReplacements.toMutableMap()
    private val benchmarkInjectionBlockTransformer = BenchmarkInjectionBlockTransformer(
        pluginContext = pluginContext,
        irRuntimeReferences = irRuntimeReferences,
        irBuilder = irBuilder,
        replacements = this.injectionBlockReplacements,
    )
    
    var injectionsCounter = 0u
    
    override fun visitMemberAccess(expression: IrMemberAccessExpression<*>, data: Nothing?): IrElement =
        when (expression.symbol) {
            irRuntimeReferences.blackholePropertyIrPropertySymbol ->
                irBuilder.irGet(benchmarkFunctionBlackholeParameter)
            irRuntimeReferences.benchmarkResultPropertyIrPropertySymbol ->
                benchmarksIrPluginException("Expected to already replace 'return benchmarkResult', but visited 'benchmarkResult'")
            irRuntimeReferences.injectFunctionIrSimpleFunctionSymbol -> {
                val lambda = expression.arguments.singleOrNull() as? IrFunctionExpression ?: benchmarksIrPluginException("Unexpected arguments of 'inject' function call")
                val fixedLambda = lambda.transform(
                    transformer = benchmarkInjectionBlockTransformer,
                    data = null,
                ) as IrFunctionExpression
                val injectedValueOfTheType = irRuntimeReferences.injectedValueClassIrClassSymbol.createType(
                    hasQuestionMark = false,
                    arguments = listOf(expression.type),
                )
                val injectedValueNullableOfTheType = irRuntimeReferences.injectedValueClassIrClassSymbol.createType(
                    hasQuestionMark = true,
                    arguments = listOf(expression.type),
                )
                val injectedField = benchmarkClass.addField {
                    origin = pluginDeclarationOrigin
                    name = Name.identifier("\$injection${injectionsCounter++}")
                    type = injectedValueNullableOfTheType
                    visibility = DescriptorVisibilities.PRIVATE_TO_THIS
                }.apply {
                    initializer = irBuilder.run {
                        irExprBody(
                            value = irNull(
                                irType = injectedValueNullableOfTheType,
                            )
                        )
                    }
                }
                setupFunctionIrBlockBodyBuilder.run {
                    +irSetField(
                        receiver = irGet(setupFunctionDispatchReceiver),
                        field = injectedField,
                        value = irCall(
                            callee = irRuntimeReferences.injectedValueConstructorIrConstructorSymbol,
                        ).apply {
                            typeArguments[0] = expression.type
                            arguments[0] = irCall(irRuntimeReferences.runIrSimpleFunctionSymbol).apply {
                                type = expression.type
                                typeArguments[0] = expression.type
                                arguments[0] = fixedLambda
                            }
                            arguments[1] = irNull(
                                irType = irRuntimeReferences.disposableInjectionClassIrClassSymbol.createType(
                                    hasQuestionMark = true,
                                    arguments = listOf(expression.type),
                                ),
                            )
                        },
                    )
                }
                tearDownFunctionIrBlockBodyBuilder.run {
                    +irSetField(
                        receiver = irGet(tearDownFunctionDispatchReceiver),
                        field = injectedField,
                        value = irNull(
                            irType = injectedValueNullableOfTheType,
                        ),
                    )
                }
                irBuilder.run {
                    irCall(irRuntimeReferences.injectedValueValuePropertyGetterIrSimpleFunctionSymbol).apply {
                        type = expression.type
                        arguments[0] = irAs(
                            argument = irGetField(
                                receiver = irGet(benchmarkFunctionDispatchReceiver),
                                field = injectedField,
                            ),
                            type = injectedValueOfTheType,
                        )
                    }
                }
            }
            irRuntimeReferences.injectDisposableFunctionIrSimpleFunctionSymbol -> {
                val lambda = expression.arguments.singleOrNull() as? IrFunctionExpression ?: benchmarksIrPluginException("Unexpected arguments of 'injectDisposable' function call")
                val fixedLambda = lambda.transform(
                    transformer = benchmarkInjectionBlockTransformer,
                    data = null,
                ) as IrFunctionExpression
                val injectedValueOfTheType = irRuntimeReferences.injectedValueClassIrClassSymbol.createType(
                    hasQuestionMark = false,
                    arguments = listOf(expression.type),
                )
                val injectedValueNullableOfTheType = irRuntimeReferences.injectedValueClassIrClassSymbol.createType(
                    hasQuestionMark = true,
                    arguments = listOf(expression.type),
                )
                val injectedField = benchmarkClass.addField {
                    origin = pluginDeclarationOrigin
                    name = Name.identifier("\$injection${injectionsCounter++}")
                    type = injectedValueNullableOfTheType
                    visibility = DescriptorVisibilities.PRIVATE_TO_THIS
                }.apply {
                    initializer = irBuilder.run {
                        irExprBody(
                            value = irNull(
                                irType = injectedValueNullableOfTheType,
                            )
                        )
                    }
                }
                setupFunctionIrBlockBodyBuilder.run {
                    val injection = irTemporary(
                        value = irCall(irRuntimeReferences.runIrSimpleFunctionSymbol).apply {
                            type = fixedLambda.function.returnType
                            typeArguments[0] = fixedLambda.function.returnType
                            arguments[0] = fixedLambda
                        },
                        origin = pluginDeclarationOrigin,
                    )
                    +irSetField(
                        receiver = irGet(setupFunctionDispatchReceiver),
                        field = injectedField,
                        value = irCall(
                            callee = irRuntimeReferences.injectedValueConstructorIrConstructorSymbol,
                        ).apply {
                            typeArguments[0] = expression.type
                            arguments[0] = irCall(irRuntimeReferences.disposableInjectionValuePropertyGetterIrSimpleFunctionSymbol).apply {
                                type = expression.type
                                arguments[0] = irGet(injection)
                            }
                            arguments[1] = irGet(injection)
                        },
                    )
                }
                tearDownFunctionIrBlockBodyBuilder.run {
                    +irSetField(
                        receiver = irGet(tearDownFunctionDispatchReceiver),
                        field = injectedField,
                        value = irNull(
                            irType = injectedValueNullableOfTheType,
                        ),
                    )
                    +irCall(irRuntimeReferences.disposableInjectionDisposeFunctionIrSimpleFunctionSymbol).apply {
                        arguments[0] = irAs(
                            argument = irCall(irRuntimeReferences.injectedValueDisposableInjectionPropertyGetterIrSimpleFunctionSymbol).apply {
                                type = irRuntimeReferences.disposableInjectionClassIrClassSymbol.createType(
                                    hasQuestionMark = true,
                                    arguments = listOf(expression.type),
                                )
                                arguments[0] = irAs(
                                    argument = irGetField(
                                        receiver = irGet(tearDownFunctionDispatchReceiver),
                                        field = injectedField,
                                    ),
                                    type = injectedValueOfTheType,
                                )
                            },
                            type = irRuntimeReferences.disposableInjectionClassIrClassSymbol.createType(
                                hasQuestionMark = false,
                                arguments = listOf(expression.type),
                            )
                        )
                    }
                }
                irBuilder.run {
                    irCall(irRuntimeReferences.injectedValueValuePropertyGetterIrSimpleFunctionSymbol).apply {
                        type = expression.type
                        arguments[0] = irAs(
                            argument = irGetField(
                                receiver = irGet(benchmarkFunctionDispatchReceiver),
                                field = injectedField,
                            ),
                            type = injectedValueOfTheType,
                        )
                    }
                }
            }
            in benchmarkBodyReplacements -> benchmarkBodyReplacements[expression.symbol]!!(expression)
            else -> super.visitMemberAccess(expression, data)
        }
    
    override fun visitReturn(expression: IrReturn, data: Nothing?): IrExpression {
        val value = expression.value
        if (value is IrMemberAccessExpression<*> && value.symbol == irRuntimeReferences.benchmarkResultPropertyIrPropertySymbol) {
            expression.value = irBuilder.irUnit()
        }
        return super.visitReturn(expression, data)
    }
}