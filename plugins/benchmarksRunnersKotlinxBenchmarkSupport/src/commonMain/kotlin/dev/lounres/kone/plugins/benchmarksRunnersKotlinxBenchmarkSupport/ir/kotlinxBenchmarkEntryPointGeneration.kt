/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.ir

import dev.lounres.kone.scope
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.impl.MutablePackageFragmentDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildRichReferenceWrapper
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrFileImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.impl.IrStarProjectionImpl
import org.jetbrains.kotlin.ir.util.*
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

fun IrBlockBodyBuilder.jvmRunBenchmarkExecutor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    valueParameters: List<IrValueParameter>,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall = irCall(irRuntimeReferences.koneBenchmarkExecutorExecuteFunctionIrSimpleFunctionSymbol).apply {
    type = pluginContext.irBuiltIns.unitType
    arguments[0] = irCall(irRuntimeReferences.kotlinxBenchmarkPropertyGetterIrSimpleFunctionSymbol).apply {
        arguments[0] = irGetObject(irRuntimeReferences.koneBenchmarkExecutorClassIrClassSymbol.owner.companionObject()!!.symbol)
    }
    arguments[1] = irCall(irRuntimeReferences.kotlinxBenchmarkConfigurationConstructorIrConstructorSymbol).apply {
        arguments[0] = irGet(valueParameters[0])
        arguments[1] = irGet(valueParameters[1])
        arguments[2] = irGet(valueParameters[2])
        arguments[3] = irGet(valueParameters[3])
        arguments[4] = irGet(valueParameters[4])
        arguments[5] = irGet(valueParameters[5])
        arguments[6] = irGet(valueParameters[6])
        arguments[7] = irGet(valueParameters[7])
        arguments[8] = irGet(valueParameters[8])
        arguments[9] = irGet(valueParameters[9])
        arguments[10] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[10]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[10]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[11] = irGet(valueParameters[11])
        arguments[12] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[12]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[12]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[13] = irGet(valueParameters[13])
        arguments[14] = irIfNull(
            type = irRuntimeReferences.kotlinxModeClassIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[14]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.modeWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[14]),
                    type = irRuntimeReferences.modeWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[15] = irGet(valueParameters[15])
        arguments[16] = irGet(valueParameters[16])
    }
    arguments[2] = irGetObject(irRuntimeReferences.kotlinxBenchmarkBenchmarkClassIrClassSymbol)
}

fun IrBlockBodyBuilder.jsRunBenchmarkExecutor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    valueParameters: List<IrValueParameter>,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall = irCall(irRuntimeReferences.koneBenchmarkExecutorExecuteFunctionIrSimpleFunctionSymbol).apply {
    type = pluginContext.irBuiltIns.unitType
    arguments[0] = irCall(irRuntimeReferences.kotlinxBenchmarkPropertyGetterIrSimpleFunctionSymbol).apply {
        arguments[0] = irGetObject(irRuntimeReferences.koneBenchmarkExecutorClassIrClassSymbol.owner.companionObject()!!.symbol)
    }
    arguments[1] = irCall(irRuntimeReferences.kotlinxBenchmarkConfigurationConstructorIrConstructorSymbol).apply {
        arguments[0] = irGet(valueParameters[0])
        arguments[1] = irGet(valueParameters[1])
        arguments[2] = irGet(valueParameters[2])
        arguments[3] = irGet(valueParameters[3])
        arguments[4] = irGet(valueParameters[4])
        arguments[5] = irGet(valueParameters[5])
        arguments[6] = irGet(valueParameters[6])
        arguments[7] = irGet(valueParameters[7])
        arguments[8] = irGet(valueParameters[8])
        arguments[9] = irGet(valueParameters[9])
        arguments[10] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[10]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[10]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[11] = irGet(valueParameters[11])
        arguments[12] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[12]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[12]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[13] = irGet(valueParameters[13])
        arguments[14] = irIfNull(
            type = irRuntimeReferences.kotlinxModeClassIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[14]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.modeWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[14]),
                    type = irRuntimeReferences.modeWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[15] = irGet(valueParameters[15])
        arguments[16] = irGet(valueParameters[16])
    }
    arguments[2] = irCall(irRuntimeReferences.kotlinxBenchmarkBenchmarkConstructorIrConstructorSymbol).apply {
        arguments[0] = irCall(irRuntimeReferences.listOfIrSimpleFunction).apply {
            type = irRuntimeReferences.listIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            IrStarProjectionImpl
                        )
                    ),
                ),
            )
            typeArguments[0] = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    IrStarProjectionImpl
                )
            )
            arguments[0] = irVararg(
                elementType = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        IrStarProjectionImpl
                    )
                ),
                values = benchmarkClasses.map { benchmarkClass ->
                    irBlock {
                        val suite = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +jsAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suite,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irGet(suite)
                    }
                }
            )
        }
    }
}

fun IrBlockBodyBuilder.wasmJsRunBenchmarkExecutor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    valueParameters: List<IrValueParameter>,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall = irCall(irRuntimeReferences.koneBenchmarkExecutorExecuteFunctionIrSimpleFunctionSymbol).apply {
    type = pluginContext.irBuiltIns.unitType
    arguments[0] = irCall(irRuntimeReferences.kotlinxBenchmarkPropertyGetterIrSimpleFunctionSymbol).apply {
        arguments[0] = irGetObject(irRuntimeReferences.koneBenchmarkExecutorClassIrClassSymbol.owner.companionObject()!!.symbol)
    }
    arguments[1] = irCall(irRuntimeReferences.kotlinxBenchmarkConfigurationConstructorIrConstructorSymbol).apply {
        arguments[0] = irGet(valueParameters[0])
        arguments[1] = irGet(valueParameters[1])
        arguments[2] = irGet(valueParameters[2])
        arguments[3] = irGet(valueParameters[3])
        arguments[4] = irGet(valueParameters[4])
        arguments[5] = irGet(valueParameters[5])
        arguments[6] = irGet(valueParameters[6])
        arguments[7] = irGet(valueParameters[7])
        arguments[8] = irGet(valueParameters[8])
        arguments[9] = irGet(valueParameters[9])
        arguments[10] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[10]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[10]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[11] = irGet(valueParameters[11])
        arguments[12] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[12]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[12]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[13] = irGet(valueParameters[13])
        arguments[14] = irIfNull(
            type = irRuntimeReferences.kotlinxModeClassIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[14]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.modeWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[14]),
                    type = irRuntimeReferences.modeWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[15] = irGet(valueParameters[15])
        arguments[16] = irGet(valueParameters[16])
    }
    arguments[2] = irCall(irRuntimeReferences.kotlinxBenchmarkBenchmarkConstructorIrConstructorSymbol).apply {
        arguments[0] = irCall(irRuntimeReferences.listOfIrSimpleFunction).apply {
            type = irRuntimeReferences.listIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            IrStarProjectionImpl
                        )
                    ),
                ),
            )
            typeArguments[0] = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    IrStarProjectionImpl
                )
            )
            arguments[0] = irVararg(
                elementType = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        IrStarProjectionImpl
                    )
                ),
                values = benchmarkClasses.map { benchmarkClass ->
                    irBlock {
                        val suite = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +wasmJsAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suite,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irGet(suite)
                    }
                }
            )
        }
    }
}

fun IrBlockBodyBuilder.wasmWasiRunBenchmarkExecutor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    valueParameters: List<IrValueParameter>,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall = irCall(irRuntimeReferences.koneBenchmarkExecutorExecuteFunctionIrSimpleFunctionSymbol).apply {
    type = pluginContext.irBuiltIns.unitType
    arguments[0] = irCall(irRuntimeReferences.kotlinxBenchmarkPropertyGetterIrSimpleFunctionSymbol).apply {
        arguments[0] = irGetObject(irRuntimeReferences.koneBenchmarkExecutorClassIrClassSymbol.owner.companionObject()!!.symbol)
    }
    arguments[1] = irCall(irRuntimeReferences.kotlinxBenchmarkConfigurationConstructorIrConstructorSymbol).apply {
        arguments[0] = irGet(valueParameters[0])
        arguments[1] = irGet(valueParameters[1])
        arguments[2] = irGet(valueParameters[2])
        arguments[3] = irGet(valueParameters[3])
        arguments[4] = irGet(valueParameters[4])
        arguments[5] = irGet(valueParameters[5])
        arguments[6] = irGet(valueParameters[6])
        arguments[7] = irGet(valueParameters[7])
        arguments[8] = irGet(valueParameters[8])
        arguments[9] = irGet(valueParameters[9])
        arguments[10] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[10]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[10]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[11] = irGet(valueParameters[11])
        arguments[12] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[12]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[12]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[13] = irGet(valueParameters[13])
        arguments[14] = irIfNull(
            type = irRuntimeReferences.kotlinxModeClassIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[14]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.modeWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[14]),
                    type = irRuntimeReferences.modeWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[15] = irGet(valueParameters[15])
        arguments[16] = irGet(valueParameters[16])
    }
    arguments[2] = irCall(irRuntimeReferences.kotlinxBenchmarkBenchmarkConstructorIrConstructorSymbol).apply {
        arguments[0] = irCall(irRuntimeReferences.listOfIrSimpleFunction).apply {
            type = irRuntimeReferences.listIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            IrStarProjectionImpl
                        )
                    ),
                ),
            )
            typeArguments[0] = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    IrStarProjectionImpl
                )
            )
            arguments[0] = irVararg(
                elementType = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        IrStarProjectionImpl
                    )
                ),
                values = benchmarkClasses.map { benchmarkClass ->
                    irBlock {
                        val suite = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +wasmWasiAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suite,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irGet(suite)
                    }
                }
            )
        }
    }
}

fun IrBlockBodyBuilder.nativeRunBenchmarkExecutor(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    valueParameters: List<IrValueParameter>,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrCall = irCall(irRuntimeReferences.koneBenchmarkExecutorExecuteFunctionIrSimpleFunctionSymbol).apply {
    type = pluginContext.irBuiltIns.unitType
    arguments[0] = irCall(irRuntimeReferences.kotlinxBenchmarkPropertyGetterIrSimpleFunctionSymbol).apply {
        arguments[0] = irGetObject(irRuntimeReferences.koneBenchmarkExecutorClassIrClassSymbol.owner.companionObject()!!.symbol)
    }
    arguments[1] = irCall(irRuntimeReferences.kotlinxBenchmarkConfigurationConstructorIrConstructorSymbol).apply {
        arguments[0] = irGet(valueParameters[0])
        arguments[1] = irGet(valueParameters[1])
        arguments[2] = irGet(valueParameters[2])
        arguments[3] = irGet(valueParameters[3])
        arguments[4] = irGet(valueParameters[4])
        arguments[5] = irGet(valueParameters[5])
        arguments[6] = irGet(valueParameters[6])
        arguments[7] = irGet(valueParameters[7])
        arguments[8] = irGet(valueParameters[8])
        arguments[9] = irGet(valueParameters[9])
        arguments[10] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[10]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[10]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[11] = irGet(valueParameters[11])
        arguments[12] = irIfNull(
            type = irRuntimeReferences.kotlinxBenchmarkTimeUnitEnumIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[12]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[12]),
                    type = irRuntimeReferences.benchmarkTimeUnitWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[13] = irGet(valueParameters[13])
        arguments[14] = irIfNull(
            type = irRuntimeReferences.kotlinxModeClassIrClassSymbol.createType(
                hasQuestionMark = true,
                arguments = listOf(),
            ),
            subject = irGet(valueParameters[14]),
            thenPart = irNull(),
            elsePart = irCall(irRuntimeReferences.modeWrapperValuePropertyGetterIrSimpleFunctionSymbol).apply {
                arguments[0] = irAs(
                    argument = irGet(valueParameters[14]),
                    type = irRuntimeReferences.modeWrapperClassIrClassSymbol.defaultType,
                )
            },
        )
        arguments[15] = irGet(valueParameters[15])
        arguments[16] = irGet(valueParameters[16])
    }
    arguments[2] = irCall(irRuntimeReferences.kotlinxBenchmarkBenchmarkConstructorIrConstructorSymbol).apply {
        arguments[0] = irCall(irRuntimeReferences.listOfIrSimpleFunction).apply {
            type = irRuntimeReferences.listIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                        hasQuestionMark = false,
                        arguments = listOf(
                            IrStarProjectionImpl
                        )
                    ),
                ),
            )
            typeArguments[0] = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                hasQuestionMark = false,
                arguments = listOf(
                    IrStarProjectionImpl
                )
            )
            arguments[0] = irVararg(
                elementType = irRuntimeReferences.kotlinxSuiteDescriptorClassIrClassSymbol.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        IrStarProjectionImpl
                    )
                ),
                values = benchmarkClasses.map { benchmarkClass ->
                    irBlock {
                        val suite = suiteDescriptorVariable(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            benchmarkClass = benchmarkClass,
                        )
                        benchmarkClass.benchmarkMethods.forEach { benchmarkMethod ->
                            +nativeAddBenchmarkDescriptor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                suiteDescriptorVariable = suite,
                                benchmarkClass = benchmarkClass,
                                benchmarkMethod = benchmarkMethod,
                            )
                        }
                        +irGet(suite)
                    }
                }
            )
        }
    }
}

fun jvmKotlinxBenchmarkSupportEntryPoint(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val kotlinxBenchmarkSupportEntryPoint = this
    TODO("Add runner annotation")
    TODO("Add value parameters")
    body = DeclarationIrBuilder(
        generatorContext = pluginContext,
        symbol = symbol,
    ).irBlockBody {
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +jvmRunBenchmarkExecutor(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun jsKotlinxBenchmarkSupportEntryPoint(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val kotlinxBenchmarkSupportEntryPoint = this
    TODO("Add runner annotation")
    TODO("Add value parameters")
    body = DeclarationIrBuilder(
        generatorContext = pluginContext,
        symbol = symbol,
    ).irBlockBody {
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +jsRunBenchmarkExecutor(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun wasmJsKotlinxBenchmarkSupportEntryPoint(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val kotlinxBenchmarkSupportEntryPoint = this
    TODO("Add runner annotation")
    TODO("Add value parameters")
    body = DeclarationIrBuilder(
        generatorContext = pluginContext,
        symbol = symbol,
    ).irBlockBody {
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +wasmJsRunBenchmarkExecutor(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun wasmWasiKotlinxBenchmarkSupportEntryPoint(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val kotlinxBenchmarkSupportEntryPoint = this
    TODO("Add runner annotation")
    TODO("Add value parameters")
    body = DeclarationIrBuilder(
        generatorContext = pluginContext,
        symbol = symbol,
    ).irBlockBody {
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +wasmWasiRunBenchmarkExecutor(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun nativeKotlinxBenchmarkSupportEntryPoint(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.identifier("main")
    returnType = pluginContext.irBuiltIns.unitType
}.apply {
    val kotlinxBenchmarkSupportEntryPoint = this
    TODO("Add runner annotation")
    TODO("Add value parameters")
    body = DeclarationIrBuilder(
        generatorContext = pluginContext,
        symbol = symbol,
    ).irBlockBody {
        +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
            arguments[0] = irString("Hello, IR-generated benchmarks!")
        }
        +nativeRunBenchmarkExecutor(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
            benchmarkClasses = benchmarkClasses,
        )
    }
}

fun jvmKotlinxBenchmarkSupportFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/kotlinxBenchmarkSupport.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        jvmKotlinxBenchmarkSupportEntryPoint(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

fun jsKotlinxBenchmarkSupportFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/kotlinxBenchmarkSupport.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        jsKotlinxBenchmarkSupportEntryPoint(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

fun wasmJsKotlinxBenchmarkSupportFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/kotlinxBenchmarkSupport.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        wasmJsKotlinxBenchmarkSupportEntryPoint(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

fun wasmWasiKotlinxBenchmarkSupportFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/kotlinxBenchmarkSupport.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        wasmWasiKotlinxBenchmarkSupportEntryPoint(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    )
}

fun nativeKotlinxBenchmarkSupportFile(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    moduleFragment: IrModuleFragment,
    benchmarkClasses: List<KotlinxBenchmarkClass>,
): IrFile = IrFileImpl(
    fileEntry = NaiveSourceBasedFileEntryImpl(
        name = "/kotlinxBenchmarkSupport.kt",
    ),
    packageFragmentDescriptor = MutablePackageFragmentDescriptor(
        moduleFragment.descriptor,
        FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated"),
    ),
    module = moduleFragment,
).apply {
    addChild(
        nativeKotlinxBenchmarkSupportEntryPoint(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
            benchmarkClasses = benchmarkClasses,
        )
    )
}