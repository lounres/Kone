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
import org.jetbrains.kotlin.ir.builders.IrStatementsBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irAs
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irConcat
import org.jetbrains.kotlin.ir.builders.irElseBranch
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
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.invokeFun
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.jvm.isJvm
import org.jetbrains.kotlin.platform.wasm.isWasmJs


fun handleEntryPointFunction(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    entryPointFunction: IrSimpleFunction,
    runnerDescriptions: List<RunnerDescription>,
) {
    val platform = pluginContext.platform
    when {
        platform.isJvm() -> {
            val argsValueParameter = entryPointFunction.parameters[0]
            entryPointFunction.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = entryPointFunction.symbol,
            ).irBlockBody {
                addRunnerExecution(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    argsValueDeclaration = argsValueParameter,
                    runnerDescriptions = runnerDescriptions,
                )
            }
        }
        platform.isJs() -> {
            entryPointFunction.parameters = emptyList()
            entryPointFunction.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = entryPointFunction.symbol,
            ).irBlockBody {
                val argsVariable = irTemporary(
                    value = irWhen(
                        type = pluginContext.irBuiltIns.arrayClass.createType(
                            hasQuestionMark = false,
                            arguments = listOf(
                                pluginContext.irBuiltIns.stringType,
                            ),
                        ),
                        branches = listOf(
                            irBranch(
                                condition = irAs(
                                    argument = irCall(irRuntimeReferences.jsFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = irString("typeof process !== 'undefined'")
                                    },
                                    type = pluginContext.irBuiltIns.booleanType,
                                ),
                                result = irAs(
                                    argument = irCall(irRuntimeReferences.jsFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = irString("process.argv.slice(2)")
                                    },
                                    type = pluginContext.irBuiltIns.arrayClass.createType(
                                        hasQuestionMark = false,
                                        arguments = listOf(
                                            pluginContext.irBuiltIns.stringType,
                                        ),
                                    ),
                                ),
                            ),
                            irBranch(
                                condition = irAs(
                                    argument = irCall(irRuntimeReferences.jsFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = irString("typeof window !== 'undefined' && typeof window.document !== 'undefined'")
                                    },
                                    type = pluginContext.irBuiltIns.booleanType,
                                ),
                                result = irThrow(
                                    arg = irCall(irRuntimeReferences.illegalArgumentExceptionConstructorStringIrConstructorSymbol).apply {
                                        arguments[0] = irString("Unexpected JS runtime.")
                                    },
                                ),
                            ),
                            irElseBranch(
                                expression = irThrow(
                                    arg = irCall(irRuntimeReferences.illegalArgumentExceptionConstructorStringIrConstructorSymbol).apply {
                                        arguments[0] = irString("Unexpected JS runtime.")
                                    },
                                ),
                            )
                        )
                    ),
                )
                addRunnerExecution(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    argsValueDeclaration = argsVariable,
                    runnerDescriptions = runnerDescriptions,
                )
            }
        }
        else -> error("Platform '$platform' is not yet supported.")
    }
}

fun IrStatementsBuilder<*>.addRunnerExecution(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    argsValueDeclaration: IrValueDeclaration,
    runnerDescriptions: List<RunnerDescription>,
) {
    +irIfThen(
        // args.size % 2 != 1
        condition = irNotEquals(
            arg1 = irCall(irRuntimeReferences.intRemIntFunctionIrSimpleFunctionSymbol).apply {
                arguments[0] = irCall(irRuntimeReferences.arraySizeGetterIrSimpleFunctionSymbol).apply {
                    arguments[0] = irGet(argsValueDeclaration)
                }
                arguments[1] = irInt(2)
            },
            arg2 = irInt(1),
        ),
        //args.joinToString(prefix = "[", postfix = "]") {
        //    "".replace(Regex("[\t\b\n\r\'\"\\\\$]")) { matchResult ->
        //        when (val value = matchResult.value) {
        //            "\t" -> "\\t"
        //            "\b" -> "\\b"
        //            "\n" -> "\\n"
        //            "\r" -> "\\r"
        //            else -> "\\${value}"
        //        }
        //    }
        //}
        thenPart = irThrow(
            arg = irCall(irRuntimeReferences.illegalArgumentExceptionConstructorStringIrConstructorSymbol).apply {
                arguments[0] = irConcat().apply {
                    with (arguments) {
                        add(irString("Expected odd number of arguments. Actual arguments: "))
                        add(
                            irCall(irRuntimeReferences.arrayJoinToStringFunctionIrSimpleFunctionSymbol).apply {
                                typeArguments[0] = pluginContext.irBuiltIns.stringType
                                arguments[0] = irGet(argsValueDeclaration)
                                arguments[2] = irString("[")
                                arguments[3] = irString("]")
                                arguments[6] = irRichFunctionReference(
                                    invokeFunction = pluginContext.irFactory.buildFun {
                                        name = SpecialNames.ANONYMOUS
                                        origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                                        visibility = DescriptorVisibilities.LOCAL
                                        returnType = pluginContext.irBuiltIns.stringType
                                    }.apply {
                                        parent = scope.getLocalDeclarationParent()
                                        val joinToStringLambda = this
                                        val argParameter = addValueParameter {
                                            name = Name.identifier("it")
                                            type = pluginContext.irBuiltIns.stringType
                                        }
                                        body = DeclarationIrBuilder(
                                            generatorContext = pluginContext,
                                            symbol = symbol,
                                        ).irBlockBody {
                                            +irReturn(
                                                irCall(irRuntimeReferences.charSequenceReplaceFunctionIrSimpleFunctionSymbol).apply {
                                                    arguments[0] = irGet(argParameter)
                                                    arguments[1] = irCall(irRuntimeReferences.regexStringConstructorIrConstructorSymbol).apply {
                                                        arguments[0] = irString("[\t\b\n\r\'\"\\\\$]")
                                                    }
                                                    arguments[2] = irRichFunctionReference(
                                                        invokeFunction = pluginContext.irFactory.buildFun {
                                                            name = SpecialNames.ANONYMOUS
                                                            origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                                                            visibility = DescriptorVisibilities.LOCAL
                                                            returnType = pluginContext.irBuiltIns.charSequenceClass.defaultType
                                                        }.apply {
                                                            parent = joinToStringLambda
                                                            val matchResultParameter = addValueParameter {
                                                                name = Name.identifier("matchResult")
                                                                type = irRuntimeReferences.matchResultClassIrClassSymbol.defaultType
                                                            }
                                                            body = DeclarationIrBuilder(
                                                                generatorContext = pluginContext,
                                                                symbol = symbol,
                                                            ).irBlockBody {
                                                                val valueVariable = irTemporary(
                                                                    irCall(irRuntimeReferences.matchResultValueGetterIrSimpleFunctionSymbol).apply {
                                                                        arguments[0] = irGet(matchResultParameter)
                                                                    }
                                                                )
                                                                +irReturn(
                                                                    irWhen(
                                                                        type = pluginContext.irBuiltIns.stringType,
                                                                        branches = listOf(
                                                                            irBranch(
                                                                                condition = irEquals(
                                                                                    arg1 = irGet(valueVariable),
                                                                                    arg2 = irString("\t"),
                                                                                ),
                                                                                result = irString("\\t"),
                                                                            ),
                                                                            irBranch(
                                                                                condition = irEquals(
                                                                                    arg1 = irGet(valueVariable),
                                                                                    arg2 = irString("\b"),
                                                                                ),
                                                                                result = irString("\\b"),
                                                                            ),
                                                                            irBranch(
                                                                                condition = irEquals(
                                                                                    arg1 = irGet(valueVariable),
                                                                                    arg2 = irString("\n"),
                                                                                ),
                                                                                result = irString("\\n"),
                                                                            ),
                                                                            irBranch(
                                                                                condition = irEquals(
                                                                                    arg1 = irGet(valueVariable),
                                                                                    arg2 = irString("\r"),
                                                                                ),
                                                                                result = irString("\\r"),
                                                                            ),
                                                                            irElseBranch(
                                                                                expression = irConcat().apply {
                                                                                    with (arguments) {
                                                                                        add(irString("\\"))
                                                                                        add(irGet(valueVariable))
                                                                                    }
                                                                                }
                                                                            ),
                                                                        ),
                                                                    )
                                                                )
                                                            }
                                                        },
                                                        superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
                                                            hasQuestionMark = true,
                                                            arguments = listOf(
                                                                irRuntimeReferences.matchResultClassIrClassSymbol.defaultType,
                                                                pluginContext.irBuiltIns.charSequenceClass.defaultType,
                                                            ),
                                                        ),
                                                        reflectionTargetSymbol = null,
                                                        overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
                                                        captures = emptyList(),
                                                        origin = null,
                                                    )
                                                }
                                            )
                                        }
                                    },
                                    superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
                                        hasQuestionMark = true,
                                        arguments = listOf(
                                            pluginContext.irBuiltIns.stringType,
                                            pluginContext.irBuiltIns.charSequenceClass.defaultType,
                                        ),
                                    ),
                                    reflectionTargetSymbol = null,
                                    overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
                                    captures = emptyList(),
                                    origin = null,
                                )
                            }
                        )
                    }
                }
            },
        ),
    )
    val nameVariable = irTemporary(
        irCall(irRuntimeReferences.arrayGetFunctionIrSimpleFunctionSymbol).apply {
            type = pluginContext.irBuiltIns.stringType
            arguments[0] = irGet(argsValueDeclaration)
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
                    arguments[0] = irGet(argsValueDeclaration)
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
                    parent = scope.getLocalDeclarationParent()
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
        branches = buildList {
            runnerDescriptions.mapTo(this) { runnerDescription ->
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
            add(
                irElseBranch(
                    expression = irThrow(
                        arg = irCall(irRuntimeReferences.illegalArgumentExceptionConstructorStringIrConstructorSymbol).apply {
                            arguments[0] = irConcat().apply {
                                with(arguments) {
                                    add(irString("Did not found runner '"))
                                    add(irGet(nameVariable))
                                    add(irString("'."))
                                }
                            }
                        },
                    ),
                )
            )
        }
    )
}