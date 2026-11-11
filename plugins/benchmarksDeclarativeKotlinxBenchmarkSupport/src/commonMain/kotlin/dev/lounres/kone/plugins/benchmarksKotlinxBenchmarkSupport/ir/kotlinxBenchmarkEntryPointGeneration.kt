/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.KotlinxBenchmarkJsExecutor
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.KotlinxBenchmarkJsExecutor.BenchmarkJs
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.KotlinxBenchmarkJsExecutor.BuiltIn
import dev.lounres.kone.scope
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.impl.MutablePackageFragmentDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildRichReferenceWrapper
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrFileImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


fun IrBlockBuilder.suiteDescriptorVariable(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClass: KotlinxBenchmarkClass,
): IrVariable = irTemporary(
    value = irCall(irRuntimeReferences.kotlinxSuiteDescriptorConstructorIrConstructorSymbol).apply {
        type = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
            hasQuestionMark = false,
            arguments = listOf(benchmarkClass.theClass.defaultType),
        )
        typeArguments[0] = benchmarkClass.theClass.defaultType
        // name: String
        arguments[0] = irString(benchmarkClass.theClass.fqName.asString())
        // factory: () -> T
        arguments[1] = irRichFunctionReference(
            invokeFunction = pluginContext.irFactory.buildFun {
                returnType = benchmarkClass.theClass.defaultType
                name = SpecialNames.ANONYMOUS
            }.apply {
                parent = scope.getLocalDeclarationParent()
                body = DeclarationIrBuilder(pluginContext, symbol).run {
                    irBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET) {
                        +irReturn(
                            irCall(benchmarkClass.theConstructor)
                        )
                    }
                }
            },
            superType = pluginContext.irBuiltIns.functionN(0).symbol.createType(
                hasQuestionMark = false,
                arguments = listOf(benchmarkClass.theClass.defaultType),
            ),
            reflectionTargetSymbol = null,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(0).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
        // parametrize: (T, Map<String, String>) -> Unit
        arguments[2] = irRichFunctionReference(
            invokeFunction = pluginContext.irFactory.buildFun {
                returnType = benchmarkClass.theClass.defaultType
                name = SpecialNames.ANONYMOUS
            }.apply {
                parent = scope.getLocalDeclarationParent()
                val benchmarkValueParameter = addValueParameter {
                    type = benchmarkClass.theClass.defaultType
                    name = Name.identifier("benchmark")
                }
                val parametersValueParameter = addValueParameter {
                    type = irRuntimeReferences.mapIrClassSymbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            pluginContext.irBuiltIns.stringType,
                            pluginContext.irBuiltIns.stringType,
                        )
                    )
                    name = Name.identifier("parameters")
                }
                body = DeclarationIrBuilder(pluginContext, symbol).run {
                    irBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET) {
                        for ((property = key, description = value) in benchmarkClass.params) {
                            +irCall(description.setter).apply {
                                arguments[0] = irGet(benchmarkValueParameter)
                                val stringParameter = irAs(
                                    argument = irCall(irRuntimeReferences.mapGetIrSimpleFunction).apply {
                                        arguments[0] = irGet(parametersValueParameter)
                                        arguments[1] = irString(property.name.identifier)
                                    },
                                    type = pluginContext.irBuiltIns.stringType
                                )
                                arguments[1] = when (description.kind) {
                                    KotlinxBenchmarkParamDescription.Kind.Byte -> irCall(irRuntimeReferences.stringToByteFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.Short -> irCall(irRuntimeReferences.stringToShortFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.Int -> irCall(irRuntimeReferences.stringToIntFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.Long -> irCall(irRuntimeReferences.stringToLongFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.UByte -> irCall(irRuntimeReferences.stringToUByteFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.UShort -> irCall(irRuntimeReferences.stringToUShortFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.UInt -> irCall(irRuntimeReferences.stringToUIntFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.ULong -> irCall(irRuntimeReferences.stringToULongFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.Float -> irCall(irRuntimeReferences.stringToFloatFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.Double -> irCall(irRuntimeReferences.stringToDoubleFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.Boolean -> irCall(irRuntimeReferences.stringToBooleanStrictFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.Char -> irCall(irRuntimeReferences.charSequenceSingleFunctionIrSimpleFunctionSymbol).apply {
                                        arguments[0] = stringParameter
                                    }
                                    KotlinxBenchmarkParamDescription.Kind.String -> stringParameter
                                }
                            }
                        }
                    }
                }
            },
            superType = pluginContext.irBuiltIns.functionN(2).symbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    benchmarkClass.theClass.defaultType,
                    irRuntimeReferences.mapIrClassSymbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            pluginContext.irBuiltIns.stringType,
                            pluginContext.irBuiltIns.stringType,
                        )
                    ),
                    pluginContext.irBuiltIns.unitType,
                ),
            ),
            reflectionTargetSymbol = null,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(2).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
        // setup: (T) -> Unit
        arguments[3] = irRichFunctionReference(
            invokeFunction = pluginContext.irFactory.buildFun {
                returnType = pluginContext.irBuiltIns.unitType
                name = SpecialNames.ANONYMOUS
            }.apply {
                parent = scope.getLocalDeclarationParent()
                val benchmarkValueParameter = addValueParameter {
                    type = benchmarkClass.theClass.defaultType
                    name = Name.identifier("benchmark")
                }
                body = DeclarationIrBuilder(pluginContext, symbol).run {
                    irBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET) {
                        benchmarkClass.setupMethods.forEach {
                            irCall(it).apply {
                                arguments[0] = irGet(benchmarkValueParameter)
                            }
                        }
                    }
                }
            },
            superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    benchmarkClass.theClass.defaultType,
                    pluginContext.irBuiltIns.unitType,
                ),
            ),
            reflectionTargetSymbol = null,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
        // teardown: (T) -> Unit
        arguments[4] = irRichFunctionReference(
            invokeFunction = pluginContext.irFactory.buildFun {
                returnType = pluginContext.irBuiltIns.unitType
                name = SpecialNames.ANONYMOUS
            }.apply {
                parent = scope.getLocalDeclarationParent()
                val benchmarkValueParameter = addValueParameter {
                    type = benchmarkClass.theClass.defaultType
                    name = Name.identifier("benchmark")
                }
                body = DeclarationIrBuilder(pluginContext, symbol).run {
                    irBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET) {
                        benchmarkClass.tearDownMethods.forEach {
                            irCall(it).apply {
                                arguments[0] = irGet(benchmarkValueParameter)
                            }
                        }
                    }
                }
            },
            superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    benchmarkClass.theClass.defaultType,
                    pluginContext.irBuiltIns.unitType,
                ),
            ),
            reflectionTargetSymbol = null,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
        // parameters: List<String>
        arguments[5] = irCall(irRuntimeReferences.listOfIrSimpleFunction).apply {
            type = irRuntimeReferences.listIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    pluginContext.irBuiltIns.stringType,
                ),
            )
            typeArguments[0] = pluginContext.irBuiltIns.stringType
            arguments[0] = irVararg(
                elementType = pluginContext.irBuiltIns.stringType,
                values = benchmarkClass.params.keys.map { irString(it.name.identifier) },
            )
        }
        // defaultParameters: Map<String, List<String>>
        arguments[6] = irCall(irRuntimeReferences.mapOfIrSimpleFunction).apply {
            type = irRuntimeReferences.mapIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    pluginContext.irBuiltIns.stringType,
                    irRuntimeReferences.listIrClassSymbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            pluginContext.irBuiltIns.stringType,
                        ),
                    )
                ),
            )
            typeArguments[0] = pluginContext.irBuiltIns.stringType
            typeArguments[1] = irRuntimeReferences.listIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    pluginContext.irBuiltIns.stringType,
                ),
            )
            arguments[0] = irVararg(
                elementType = pluginContext.irBuiltIns.stringType,
                values = benchmarkClass.params.map { [property, description] ->
                    irCall(irRuntimeReferences.pairIrConstructorSymbol).apply {
                        type = irRuntimeReferences.pairIrClassSymbol.createType(
                            hasQuestionMark = false,
                            arguments = listOf(
                                pluginContext.irBuiltIns.stringType,
                                irRuntimeReferences.listIrClassSymbol.createType(
                                    hasQuestionMark = false,
                                    arguments = listOf(
                                        pluginContext.irBuiltIns.stringType,
                                    )
                                )
                            )
                        )
                        typeArguments[0] = pluginContext.irBuiltIns.stringType
                        typeArguments[1] = irRuntimeReferences.listIrClassSymbol.createType(
                            hasQuestionMark = false,
                            arguments = listOf(
                                pluginContext.irBuiltIns.stringType,
                            )
                        )
                        arguments[0] = irString(property.name.identifier)
                        arguments[1] = irCall(irRuntimeReferences.listOfIrSimpleFunction).apply {
                            type = irRuntimeReferences.listIrClassSymbol.createType(
                                hasQuestionMark = false,
                                arguments = listOf(
                                    pluginContext.irBuiltIns.stringType,
                                ),
                            )
                            typeArguments[0] = pluginContext.irBuiltIns.stringType
                            arguments[0] = irVararg(
                                elementType = pluginContext.irBuiltIns.stringType,
                                values = description.defaultValues.map { irString(it) },
                            )
                        }
                    }
                },
            )
        }
        // iterations: Int
        arguments[7] = irInt(benchmarkClass.measurement.iterations.toInt())
        // warmups: Int
        arguments[8] = irInt(benchmarkClass.warmup.iterations.toInt())
        // iterationTime: IterationTime
        arguments[9] = irCall(irRuntimeReferences.kotlinxIterationTimeConstructorIrConstructorSymbol).apply {
            arguments[0] = irLong(benchmarkClass.measurement.time.toLong())
            arguments[1] = IrGetEnumValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.defaultType,
                symbol = scope {
                    val entry =
                        irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.owner.declarations.single {
                            it is IrEnumEntry && it.name.identifier == benchmarkClass.measurement.timeUnit.name
                        } as IrEnumEntry
                    entry.symbol
                }
            )
        }
        // outputTimeUnit: BenchmarkTimeUnit
        arguments[10] = IrGetEnumValueImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.defaultType,
            symbol = scope {
                val entry = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.owner.declarations.single {
                    it is IrEnumEntry && it.name.identifier == benchmarkClass.outputTimeUnit.name
                } as IrEnumEntry
                entry.symbol
            }
        )
//        // mode: Mode
//        arguments[11] = IrGetEnumValueImpl(
//            startOffset = UNDEFINED_OFFSET,
//            endOffset = UNDEFINED_OFFSET,
//            type = irRuntimeReferences.kotlinxBenchmarkModeAnnotationIrClassSymbol.defaultType,
//            symbol = scope {
//                val entry = irRuntimeReferences.kotlinxBenchmarkModeAnnotationIrClassSymbol.owner.declarations.single {
//                    it is IrEnumEntry && it.name.identifier == benchmarkClass.benchmarkModes.first().name
//                } as IrEnumEntry
//                entry.symbol
//            }
//        )
    }
)

fun IrBlockBuilder.jsAddBenchmarkDescriptor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suiteDescriptorVariable: IrVariable,
    benchmarkClass: KotlinxBenchmarkClass,
    benchmarkMethod: KotlinxBenchmarkMethod,
): IrCall = irCall(irRuntimeReferences.kotlinxSuiteDescriptorAddFunctionIrSimpleFunctionSymbol).apply {
    arguments[0] = irGet(suiteDescriptorVariable)
    arguments[1] = irCall(
        if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxJsBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol
        else irRuntimeReferences.kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterConstructorIrConstructorSymbol
    ).apply {
        val benchmarkDescriptorIrClassSymbol =
            if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol
            else irRuntimeReferences.kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol
        type = benchmarkDescriptorIrClassSymbol.createType(
            hasQuestionMark = false,
            arguments = listOf(
                benchmarkClass.theClass.defaultType,
            )
        )
        typeArguments[0] = benchmarkClass.theClass.defaultType
        arguments[0] = irString(benchmarkMethod.theMethod.fqName.asString())
        arguments[1] = irGet(suiteDescriptorVariable)
        arguments[2] = irCall(irRuntimeReferences.kotlinxBlackholeConstructorIrConstructorSymbol)
        val theMethodArity = 1 + if (benchmarkMethod.blackholeValueParameter != null) 1 else 0
        arguments[3] = irRichFunctionReference(
            invokeFunction = pluginContext.irBuiltIns.buildRichReferenceWrapper(
                target = benchmarkMethod.theMethod,
                parent = scope.getLocalDeclarationParent(),
                boundReceiverType = null,
                parameterTypes = benchmarkMethod.theMethod.parameters.map { it.type },
                returnType = benchmarkMethod.theMethod.returnType,
            ),
            superType = pluginContext.irBuiltIns.kFunctionN(theMethodArity).symbol.createType(
                hasQuestionMark = false,
                arguments = listOfNotNull(
                    benchmarkClass.theClass.defaultType,
                    if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBlackholeClassIrClassSymbol.defaultType
                    else null,
                    pluginContext.irBuiltIns.unitType,
                ),
            ),
            reflectionTargetSymbol = benchmarkMethod.theMethod.symbol,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(theMethodArity).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
    }
}

fun IrBlockBuilder.wasmJsAddBenchmarkDescriptor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suiteDescriptorVariable: IrVariable,
    benchmarkClass: KotlinxBenchmarkClass,
    benchmarkMethod: KotlinxBenchmarkMethod,
): IrCall = irCall(irRuntimeReferences.kotlinxSuiteDescriptorAddFunctionIrSimpleFunctionSymbol).apply {
    arguments[0] = irGet(suiteDescriptorVariable)
    arguments[1] = irCall(
        if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol
        else irRuntimeReferences.kotlinxBenchmarkDescriptorWithNoBlackholeParameterConstructorIrConstructorSymbol
    ).apply {
        val benchmarkDescriptorIrClassSymbol =
            if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol
            else irRuntimeReferences.kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol
        type = benchmarkDescriptorIrClassSymbol.createType(
            hasQuestionMark = false,
            arguments = listOf(
                benchmarkClass.theClass.defaultType,
            )
        )
        typeArguments[0] = benchmarkClass.theClass.defaultType
        arguments[0] = irString(benchmarkMethod.theMethod.fqName.asString())
        arguments[1] = irGet(suiteDescriptorVariable)
        arguments[2] = irCall(irRuntimeReferences.kotlinxBlackholeConstructorIrConstructorSymbol)
        val theMethodArity = 1 + if (benchmarkMethod.blackholeValueParameter != null) 1 else 0
        arguments[3] = irRichFunctionReference(
            invokeFunction = pluginContext.irBuiltIns.buildRichReferenceWrapper(
                target = benchmarkMethod.theMethod,
                parent = scope.getLocalDeclarationParent(),
                boundReceiverType = null,
                parameterTypes = benchmarkMethod.theMethod.parameters.map { it.type },
                returnType = benchmarkMethod.theMethod.returnType,
            ),
            superType = pluginContext.irBuiltIns.kFunctionN(theMethodArity).symbol.createType(
                hasQuestionMark = false,
                arguments = listOfNotNull(
                    benchmarkClass.theClass.defaultType,
                    if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBlackholeClassIrClassSymbol.defaultType
                    else null,
                    pluginContext.irBuiltIns.unitType,
                ),
            ),
            reflectionTargetSymbol = benchmarkMethod.theMethod.symbol,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(theMethodArity).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
    }
}

fun IrBlockBuilder.wasmWasiAddBenchmarkDescriptor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suiteDescriptorVariable: IrVariable,
    benchmarkClass: KotlinxBenchmarkClass,
    benchmarkMethod: KotlinxBenchmarkMethod,
): IrCall = irCall(irRuntimeReferences.kotlinxSuiteDescriptorAddFunctionIrSimpleFunctionSymbol).apply {
    arguments[0] = irGet(suiteDescriptorVariable)
    arguments[1] = irCall(
        if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol
        else irRuntimeReferences.kotlinxBenchmarkDescriptorWithNoBlackholeParameterConstructorIrConstructorSymbol
    ).apply {
        val benchmarkDescriptorIrClassSymbol =
            if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol
            else irRuntimeReferences.kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol
        type = benchmarkDescriptorIrClassSymbol.createType(
            hasQuestionMark = false,
            arguments = listOf(
                benchmarkClass.theClass.defaultType,
            )
        )
        typeArguments[0] = benchmarkClass.theClass.defaultType
        arguments[0] = irString(benchmarkMethod.theMethod.fqName.asString())
        arguments[1] = irGet(suiteDescriptorVariable)
        arguments[2] = irCall(irRuntimeReferences.kotlinxBlackholeConstructorIrConstructorSymbol)
        val theMethodArity = 1 + if (benchmarkMethod.blackholeValueParameter != null) 1 else 0
        arguments[3] = irRichFunctionReference(
            invokeFunction = pluginContext.irBuiltIns.buildRichReferenceWrapper(
                target = benchmarkMethod.theMethod,
                parent = scope.getLocalDeclarationParent(),
                boundReceiverType = null,
                parameterTypes = benchmarkMethod.theMethod.parameters.map { it.type },
                returnType = benchmarkMethod.theMethod.returnType,
            ),
            superType = pluginContext.irBuiltIns.kFunctionN(theMethodArity).symbol.createType(
                hasQuestionMark = false,
                arguments = listOfNotNull(
                    benchmarkClass.theClass.defaultType,
                    if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBlackholeClassIrClassSymbol.defaultType
                    else null,
                    pluginContext.irBuiltIns.unitType,
                ),
            ),
            reflectionTargetSymbol = benchmarkMethod.theMethod.symbol,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(theMethodArity).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
    }
}

fun IrBlockBuilder.nativeAddBenchmarkDescriptor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suiteDescriptorVariable: IrVariable,
    benchmarkClass: KotlinxBenchmarkClass,
    benchmarkMethod: KotlinxBenchmarkMethod,
): IrCall = irCall(irRuntimeReferences.kotlinxSuiteDescriptorAddFunctionIrSimpleFunctionSymbol).apply {
    arguments[0] = irGet(suiteDescriptorVariable)
    arguments[1] = irCall(
        if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol
        else irRuntimeReferences.kotlinxBenchmarkDescriptorWithNoBlackholeParameterConstructorIrConstructorSymbol
    ).apply {
        val benchmarkDescriptorIrClassSymbol =
            if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol
            else irRuntimeReferences.kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol
        type = benchmarkDescriptorIrClassSymbol.createType(
            hasQuestionMark = false,
            arguments = listOf(
                benchmarkClass.theClass.defaultType,
            )
        )
        typeArguments[0] = benchmarkClass.theClass.defaultType
        arguments[0] = irString(benchmarkMethod.theMethod.fqName.asString())
        arguments[1] = irGet(suiteDescriptorVariable)
        arguments[2] = irCall(irRuntimeReferences.kotlinxBlackholeConstructorIrConstructorSymbol)
        val theMethodArity = 1 + if (benchmarkMethod.blackholeValueParameter != null) 1 else 0
        arguments[3] = irRichFunctionReference(
            invokeFunction = pluginContext.irBuiltIns.buildRichReferenceWrapper(
                target = benchmarkMethod.theMethod,
                parent = scope.getLocalDeclarationParent(),
                boundReceiverType = null,
                parameterTypes = benchmarkMethod.theMethod.parameters.map { it.type },
                returnType = benchmarkMethod.theMethod.returnType,
            ),
            superType = pluginContext.irBuiltIns.kFunctionN(theMethodArity).symbol.createType(
                hasQuestionMark = false,
                arguments = listOfNotNull(
                    benchmarkClass.theClass.defaultType,
                    if (benchmarkMethod.blackholeValueParameter != null) irRuntimeReferences.kotlinxBlackholeClassIrClassSymbol.defaultType
                    else null,
                    pluginContext.irBuiltIns.unitType,
                ),
            ),
            reflectionTargetSymbol = benchmarkMethod.theMethod.symbol,
            overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(theMethodArity).invokeFun!!.symbol,
            captures = listOf(),
            origin = null,
        )
    }
}

fun IrBlockBodyBuilder.jsDeclareAndExecuteSuitesArgument(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrExpression = irRichFunctionReference(
    invokeFunction = pluginContext.irFactory.buildFun {
        returnType = pluginContext.irBuiltIns.unitType
        name = SpecialNames.ANONYMOUS
    }.apply {
        parent = scope.getLocalDeclarationParent()
        val executorValueParameter = addValueParameter {
            type = irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType
            name = Name.identifier("executor")
        }
        body = DeclarationIrBuilder(pluginContext, symbol).run {
            irBlockBody {
                benchmarkClasses.forEach { benchmarkClass ->
                    +irBlock {
                        val suiteDescriptorVariable = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +jsAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suiteDescriptorVariable,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseSuiteFunctionIrSimpleFunctionSymbol).apply {
                            typeArguments[0] = benchmarkClass.theClass.defaultType
                            arguments[0] = irGet(executorValueParameter)
                            arguments[1] = irGet(suiteDescriptorVariable)
                        }
                    }
                }
                +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseRunFunctionIrSimpleFunctionSymbol).apply {
                    arguments[0] = irGet(executorValueParameter)
                }
            }
        }
    },
    superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
        hasQuestionMark = false,
        arguments = listOf(
            irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType,
            pluginContext.irBuiltIns.unitType,
        ),
    ),
    reflectionTargetSymbol = null,
    overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
    captures = listOf(),
    origin = null,
)

fun IrBlockBodyBuilder.wasmJsDeclareAndExecuteSuitesArgument(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrExpression = irRichFunctionReference(
    invokeFunction = pluginContext.irFactory.buildFun {
        returnType = pluginContext.irBuiltIns.unitType
        name = SpecialNames.ANONYMOUS
    }.apply {
        parent = scope.getLocalDeclarationParent()
        val executorValueParameter = addValueParameter {
            type = irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType
            name = Name.identifier("executor")
        }
        body = DeclarationIrBuilder(pluginContext, symbol).run {
            irBlockBody {
                benchmarkClasses.forEach { benchmarkClass ->
                    +irBlock {
                        val suiteDescriptorVariable = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +wasmJsAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suiteDescriptorVariable,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseSuiteFunctionIrSimpleFunctionSymbol).apply {
                            typeArguments[0] = benchmarkClass.theClass.defaultType
                            arguments[0] = irGet(executorValueParameter)
                            arguments[1] = irGet(suiteDescriptorVariable)
                        }
                    }
                }
                +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseRunFunctionIrSimpleFunctionSymbol).apply {
                    arguments[0] = irGet(executorValueParameter)
                }
            }
        }
    },
    superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
        hasQuestionMark = false,
        arguments = listOf(
            irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType,
            pluginContext.irBuiltIns.unitType,
        ),
    ),
    reflectionTargetSymbol = null,
    overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
    captures = listOf(),
    origin = null,
)

fun IrBlockBodyBuilder.wasmWasiDeclareAndExecuteSuitesArgument(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrExpression = irRichFunctionReference(
    invokeFunction = pluginContext.irFactory.buildFun {
        returnType = pluginContext.irBuiltIns.unitType
        name = SpecialNames.ANONYMOUS
    }.apply {
        parent = scope.getLocalDeclarationParent()
        val executorValueParameter = addValueParameter {
            type = irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType
            name = Name.identifier("executor")
        }
        body = DeclarationIrBuilder(pluginContext, symbol).run {
            irBlockBody {
                benchmarkClasses.forEach { benchmarkClass ->
                    +irBlock {
                        val suiteDescriptorVariable = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +wasmWasiAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suiteDescriptorVariable,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseSuiteFunctionIrSimpleFunctionSymbol).apply {
                            typeArguments[0] = benchmarkClass.theClass.defaultType
                            arguments[0] = irGet(executorValueParameter)
                            arguments[1] = irGet(suiteDescriptorVariable)
                        }
                    }
                }
                +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseRunFunctionIrSimpleFunctionSymbol).apply {
                    arguments[0] = irGet(executorValueParameter)
                }
            }
        }
    },
    superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
        hasQuestionMark = false,
        arguments = listOf(
            irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType,
            pluginContext.irBuiltIns.unitType,
        ),
    ),
    reflectionTargetSymbol = null,
    overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
    captures = listOf(),
    origin = null,
)

fun IrBlockBodyBuilder.nativeDeclareAndExecuteSuitesArgument(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrExpression = irRichFunctionReference(
    invokeFunction = pluginContext.irFactory.buildFun {
        returnType = pluginContext.irBuiltIns.unitType
        name = SpecialNames.ANONYMOUS
    }.apply {
        parent = scope.getLocalDeclarationParent()
        val executorValueParameter = addValueParameter {
            type = irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType
            name = Name.identifier("executor")
        }
        body = DeclarationIrBuilder(pluginContext, symbol).run {
            irBlockBody {
                benchmarkClasses.forEach { benchmarkClass ->
                    +irBlock {
                        val suiteDescriptorVariable = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +nativeAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suiteDescriptorVariable,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseSuiteFunctionIrSimpleFunctionSymbol).apply {
                            typeArguments[0] = benchmarkClass.theClass.defaultType
                            arguments[0] = irGet(executorValueParameter)
                            arguments[1] = irGet(suiteDescriptorVariable)
                        }
                    }
                }
                +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseRunFunctionIrSimpleFunctionSymbol).apply {
                    arguments[0] = irGet(executorValueParameter)
                }
            }
        }
    },
    superType = pluginContext.irBuiltIns.functionN(1).symbol.createType(
        hasQuestionMark = false,
        arguments = listOf(
            irRuntimeReferences.kotlinxSuiteExecutorBaseClassIrClassSymbol.defaultType,
            pluginContext.irBuiltIns.unitType,
        ),
    ),
    reflectionTargetSymbol = null,
    overriddenFunctionSymbol = pluginContext.irBuiltIns.functionN(1).invokeFun!!.symbol,
    captures = listOf(),
    origin = null,
)

fun IrBlockBodyBuilder.jsRunBenchmarksViaBenchmarkJs(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    argsValueParameter: IrValueParameter,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall =
    irCall(irRuntimeReferences.kotlinxJsRunBenchmarksFunctionIrSimpleFunctionSymbol).apply {
        arguments[0] = irString("js")
        arguments[1] = irGet(argsValueParameter)
        arguments[2] = jsDeclareAndExecuteSuitesArgument(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    }

fun IrBlockBodyBuilder.jsRunBenchmarksViaBuiltIn(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    argsValueParameter: IrValueParameter,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall =
    irCall(irRuntimeReferences.kotlinxJsRunBenchmarksBuiltInFunctionIrSimpleFunctionSymbol).apply {
        arguments[0] = irString("js")
        arguments[1] = irGet(argsValueParameter)
        arguments[2] = jsDeclareAndExecuteSuitesArgument(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    }

fun IrBlockBodyBuilder.wasmJsRunBenchmarks(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    argsValueParameter: IrValueParameter,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall =
    irCall(irRuntimeReferences.kotlinxWasmJsRunBenchmarksFunctionIrSimpleFunctionSymbol).apply {
        arguments[0] = irString("wasmJs")
        arguments[1] = irGet(argsValueParameter)
        arguments[2] = wasmJsDeclareAndExecuteSuitesArgument(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    }

fun IrBlockBodyBuilder.wasmWasiRunBenchmarks(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    argsValueParameter: IrValueParameter,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall =
    irCall(irRuntimeReferences.kotlinxWasmWasiRunBenchmarksFunctionIrSimpleFunctionSymbol).apply {
        arguments[0] = irString("wasmWasi")
        arguments[1] = irGet(argsValueParameter)
        arguments[2] = wasmWasiDeclareAndExecuteSuitesArgument(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    }

fun IrBlockBodyBuilder.nativeRunBenchmarks(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    nativeTargetPresetName: String,
    argsValueParameter: IrValueParameter,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall =
    irCall(irRuntimeReferences.kotlinxNativeRunBenchmarksFunctionIrSimpleFunctionSymbol).apply {
        arguments[0] = irString(nativeTargetPresetName)
        arguments[1] = irGet(argsValueParameter)
        arguments[2] = nativeDeclareAndExecuteSuitesArgument(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    }

fun jsMain(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    kotlinxBenchmarkJsExecutor: KotlinxBenchmarkJsExecutor,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val argsValueParameter = addValueParameter {
        name = Name.identifier("args")
        type = pluginContext.irBuiltIns.arrayClass.createType(
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
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        when (kotlinxBenchmarkJsExecutor) {
            BenchmarkJs -> +jsRunBenchmarksViaBenchmarkJs(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                argsValueParameter = argsValueParameter,
                benchmarkClasses = benchmarkClasses,
            )
            BuiltIn -> +jsRunBenchmarksViaBuiltIn(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                argsValueParameter = argsValueParameter,
                benchmarkClasses = benchmarkClasses,
            )
        }
    }
}

fun wasmJsMain(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val argsValueParameter = addValueParameter {
        name = Name.identifier("args")
        type = pluginContext.irBuiltIns.arrayClass.createType(
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
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +wasmJsRunBenchmarks(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            argsValueParameter = argsValueParameter,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun wasmWasiMain(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val argsValueParameter = addValueParameter {
        name = Name.identifier("args")
        type = pluginContext.irBuiltIns.arrayClass.createType(
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
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +wasmWasiRunBenchmarks(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            argsValueParameter = argsValueParameter,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun nativeMain(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    nativeTargetPresetName: String,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val argsValueParameter = addValueParameter {
        name = Name.identifier("args")
        type = pluginContext.irBuiltIns.arrayClass.createType(
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
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +nativeRunBenchmarks(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            nativeTargetPresetName = nativeTargetPresetName,
            argsValueParameter = argsValueParameter,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun jsMainFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    kotlinxBenchmarkJsExecutor: KotlinxBenchmarkJsExecutor,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/run.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        jsMain(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            kotlinxBenchmarkJsExecutor = kotlinxBenchmarkJsExecutor,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

fun wasmJsMainFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/run.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        wasmJsMain(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

fun wasmWasiMainFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/run.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        wasmWasiMain(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

fun nativeMainFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    nativeTargetPresetName: String,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/run.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        nativeMain(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            nativeTargetPresetName = nativeTargetPresetName,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

class JsMainFunctionBodyGenerationVisitor(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
    private val kotlinxBenchmarkJsExecutor: KotlinxBenchmarkJsExecutor,
    private val benchmarkClasses: List<KotlinxBenchmarkClass>,
) : IrVisitorVoid() {
    companion object {
        val entryPointCallableId = CallableId(
            packageName = FqName("dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.generated"),
            className = null,
            callableName = Name.identifier("main"),
        )
    }
    
    override fun visitElement(element: IrElement) { element.acceptChildrenVoid(this) }

    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        if (declaration.isTopLevel && declaration.callableId == entryPointCallableId && declaration.parameters.size == 1) {
            declaration.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = declaration.symbol,
            ).irBlockBody {
                +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
                    arguments[0] = irString("Hello, IR-generated benchmarks!")
                }
                val argsValueParameter = declaration.parameters.single()
                when (kotlinxBenchmarkJsExecutor) {
                    BenchmarkJs -> +jsRunBenchmarksViaBenchmarkJs(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        argsValueParameter = argsValueParameter,
                        benchmarkClasses = benchmarkClasses,
                    )
                    BuiltIn -> +jsRunBenchmarksViaBuiltIn(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        argsValueParameter = argsValueParameter,
                        benchmarkClasses = benchmarkClasses,
                    )
                }
            }
        }
        super.visitSimpleFunction(declaration)
    }
}