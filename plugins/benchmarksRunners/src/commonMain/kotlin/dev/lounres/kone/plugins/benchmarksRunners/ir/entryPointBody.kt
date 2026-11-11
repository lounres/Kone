/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.ir

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
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.invokeFun
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


fun entryPointBody(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    entryPointFunction: IrSimpleFunction,
    runnerDescriptions: List<RunnerDescription>,
): IrBody {
    val argsValueParameter = entryPointFunction.parameters[0]
    return DeclarationIrBuilder(
        generatorContext = pluginContext,
        symbol = entryPointFunction.symbol,
    ).irBlockBody {
        +irIfThen(
            condition = irNotEquals(
                arg1 = irCall(irRuntimeReferences.intRemIntFunctionIrSimpleFunctionSymbol).apply {
                    arguments[0] = irCall(irRuntimeReferences.arraySizeGetterIrSimpleFunctionSymbol).apply {
                        arguments[0] = irGet(argsValueParameter)
                    }
                    arguments[1] = irInt(2)
                },
                arg2 = irInt(1),
            ),
            thenPart = irThrow(
                arg = irCall(irRuntimeReferences.illegalArgumentExceptionConstructorStringIrConstructorSymbol).apply {
                    arguments[0] = irString("Expected odd number of arguments.")
                },
            ),
        )
        val nameVariable = irTemporary(
            irCall(irRuntimeReferences.arrayGetFunctionIrSimpleFunctionSymbol).apply {
                type = pluginContext.irBuiltIns.stringType
                arguments[0] = irGet(argsValueParameter)
                arguments[1] = irInt(0)
            }
        )
        // args.drop(1).chunked(2).associate { it[0] to it[1] }
        val argumentsMapVariable = irTemporary(
            irCall(irRuntimeReferences.associateFunctionIrSimpleFunctionSymbol).apply {
                type = pluginContext.irBuiltIns.mapClass.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        pluginContext.irBuiltIns.stringType,
                        pluginContext.irBuiltIns.stringType,
                    ),
                )
                typeArguments[0] = pluginContext.irBuiltIns.listClass.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        pluginContext.irBuiltIns.stringType,
                    ),
                )
                typeArguments[1] = pluginContext.irBuiltIns.stringType
                typeArguments[2] = pluginContext.irBuiltIns.stringType
                arguments[0] = irCall(irRuntimeReferences.listChunkedFunctionIrSimpleFunctionSymbol).apply {
                    type = pluginContext.irBuiltIns.listClass.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            pluginContext.irBuiltIns.listClass.createType(
                                hasQuestionMark = false,
                                arguments = listOf(
                                    pluginContext.irBuiltIns.stringType,
                                ),
                            )
                        ),
                    )
                    typeArguments[0] = pluginContext.irBuiltIns.stringType
                    arguments[0] = irCall(irRuntimeReferences.arrayDropFunctionIrSimpleFunctionSymbol).apply {
                        type = pluginContext.irBuiltIns.listClass.createType(
                            hasQuestionMark = false,
                            arguments = listOf(
                                pluginContext.irBuiltIns.stringType,
                            ),
                        )
                        typeArguments[0] = pluginContext.irBuiltIns.stringType
                        arguments[0] = irGet(argsValueParameter)
                        arguments[1] = irInt(1)
                    }
                    arguments[1] = irInt(2)
                }
                arguments[1] = irRichFunctionReference(
                    invokeFunction = pluginContext.irFactory.buildFun {
                        name = SpecialNames.ANONYMOUS
                        origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                        visibility = DescriptorVisibilities.LOCAL
                        returnType = irRuntimeReferences.pairClassIrClassSymbol.createType(
                            hasQuestionMark = false,
                            arguments = listOf(
                                pluginContext.irBuiltIns.stringType,
                                pluginContext.irBuiltIns.stringType,
                            ),
                        )
                    }.apply {
                        parent = entryPointFunction
                        val chunkValueParameter = addValueParameter {
                            name = Name.identifier("it")
                            type = pluginContext.irBuiltIns.listClass.createType(
                                hasQuestionMark = false,
                                arguments = listOf(
                                    pluginContext.irBuiltIns.stringType,
                                ),
                            )
                        }
                        body = DeclarationIrBuilder(
                            generatorContext = pluginContext,
                            symbol = symbol,
                        ).irBlockBody {
                            +irReturn(
                                irCall(irRuntimeReferences.pairConstructorIrConstructorSymbol).apply {
                                    type = irRuntimeReferences.pairClassIrClassSymbol.createType(
                                        hasQuestionMark = false,
                                        arguments = listOf(
                                            pluginContext.irBuiltIns.stringType,
                                            pluginContext.irBuiltIns.stringType,
                                        ),
                                    )
                                    typeArguments[0] = pluginContext.irBuiltIns.stringType
                                    typeArguments[1] = pluginContext.irBuiltIns.stringType
                                    arguments[0] = irCall(irRuntimeReferences.listGetFunctionIrSimpleFunctionSymbol).apply {
                                        type = pluginContext.irBuiltIns.stringType
                                        arguments[0] = irGet(chunkValueParameter)
                                        arguments[1] = irInt(0)
                                    }
                                    arguments[1] = irCall(irRuntimeReferences.listGetFunctionIrSimpleFunctionSymbol).apply {
                                        type = pluginContext.irBuiltIns.stringType
                                        arguments[0] = irGet(chunkValueParameter)
                                        arguments[1] = irInt(1)
                                    }
                                }
                            )
                        }
                    },
                    superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            pluginContext.irBuiltIns.listClass.createType(
                                hasQuestionMark = false,
                                arguments = listOf(
                                    pluginContext.irBuiltIns.stringType,
                                ),
                            ),
                            irRuntimeReferences.pairClassIrClassSymbol.createType(
                                hasQuestionMark = false,
                                arguments = listOf(
                                    pluginContext.irBuiltIns.stringType,
                                    pluginContext.irBuiltIns.stringType,
                                ),
                            ),
                        ),
                    ),
                    reflectionTargetSymbol = null,
                    overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
                    captures = emptyList(),
                    origin = null,
                )
            }
        )
        +irWhen(
            type = pluginContext.irBuiltIns.unitType,
            branches = runnerDescriptions.map { runnerDescription ->
                irBranch(
                    condition = irEquals(
                        arg1 = irGet(nameVariable),
                        arg2 = irString(runnerDescription.name),
                    ),
                    result = irBlock {
                        +irCall(runnerDescription.theRunnerFunction).apply {
                            for ([parameterIndex, parameter] in runnerDescription.theRunnerFunction.parameters.withIndex()) {
                                arguments[parameterIndex] = when {
                                    parameter.isDispatchReceiver -> irGetObject((runnerDescription.parent as IrClass).symbol)
                                    else -> {
                                        val argumentString = irTemporary(
                                            irCall(irRuntimeReferences.mapGetFunctionIrSimpleFunctionSymbol).apply {
                                                type = pluginContext.irBuiltIns.stringType.makeNullable()
                                                arguments[0] = irGet(argumentsMapVariable)
                                                arguments[1] = irString(parameter.name.asString())
                                            }
                                        )
                                        +irIfThen(
                                            condition = irEqualsNull(irGet(argumentString)),
                                            thenPart = irThrow(
                                                arg = irCall(irRuntimeReferences.illegalArgumentExceptionConstructorStringIrConstructorSymbol).apply {
                                                    arguments[0] = irString("Did not found argument for parameter '${parameter.name}'.")
                                                },
                                            ),
                                        )
                                        irCall(irRuntimeReferences.jsonDecodeFromStringFunctionIrSimpleFunctionSymbol).apply {
                                            type = parameter.type
                                            typeArguments[0] = parameter.type
                                            arguments[0] = irGetObject(irRuntimeReferences.jsonDefaultCompanionIrClassSymbol)
                                            arguments[1] = irAs(
                                                argument = irGet(argumentString),
                                                type = pluginContext.irBuiltIns.stringType,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        )
    }
}