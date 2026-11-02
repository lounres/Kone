/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.getKFunctionType
import org.jetbrains.kotlin.ir.util.invokeFun
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


class BenchmarksDeclareAndExecuteSuitesTransformer(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
    private val benchmarkToGeneratedClass: Map<IrSimpleFunction, GeneratedBenchmarkClassDescription>,
) : IrTransformer<Nothing?>() {
    override fun visitFile(declaration: IrFile, data: Nothing?): IrFile {
        if (declaration.name != "BenchmarkSuite" || declaration.packageFqName != FqName("kotlinx.benchmark.generated")) return declaration
        
        val declareAndExecuteSuitesIrSimpleFunction = declaration.declarations
            .filterIsInstance<IrSimpleFunction>()
            .firstOrNull { it.name.identifierOrNullIfSpecial == "declareAndExecuteSuites" } ?: return declaration
        
        val declareAndExecuteSuitesFunctionExecutor = declareAndExecuteSuitesIrSimpleFunction.parameters.single()
        val declareAndExecuteSuitesFunctionBody = declareAndExecuteSuitesIrSimpleFunction.body as? IrBlockBody ?: return declaration
        
        declareAndExecuteSuitesFunctionBody.statements
            .addAll(
                index = 0,
                elements = benchmarkToGeneratedClass.map { [benchmark, benchmarkDescription] ->
                    DeclarationIrBuilder(pluginContext, declareAndExecuteSuitesIrSimpleFunction.symbol).run {
                        irBlock {
                            val descriptor = irTemporary(
                                value = irCall(irRuntimeReferences.kotlinxSuiteDescriptorConstructorIrConstructorSymbol).apply {
                                    type = irRuntimeReferences.kotlinxSuiteDescriptorIrClassSymbol.createType(
                                        hasQuestionMark = false,
                                        arguments = listOf(benchmarkDescription.benchmarkClass.defaultType),
                                    )
                                    typeArguments[0] = benchmarkDescription.benchmarkClass.defaultType
                                    // name: String
                                    arguments[0] = irString(benchmark.fqName.asString())
                                    // factory: () -> T
                                    arguments[1] = irRichFunctionReference(
                                        invokeFunction = pluginContext.irFactory.buildFun {
                                            returnType = benchmarkDescription.benchmarkClass.defaultType
                                            name = SpecialNames.ANONYMOUS
                                        }.apply {
                                            body = DeclarationIrBuilder(pluginContext, symbol).run {
                                                irBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET) {
                                                    +irReturn(
                                                        irCall(benchmarkDescription.benchmarkClass.primaryConstructor!!)
                                                    )
                                                }
                                            }
                                        },
                                        superType = pluginContext.irBuiltIns.getKFunctionType(
                                            returnType = benchmarkDescription.benchmarkClass.defaultType,
                                            parameterTypes = listOf(),
                                        ),
                                        reflectionTargetSymbol = null,
                                        overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(0).invokeFun!!.symbol,
                                        captures = listOf(),
                                        origin = null,
                                    )
                                    // parametrize: (T, Map<String, String>) -> Unit
                                    arguments[2] = irRichFunctionReference(
                                        invokeFunction = pluginContext.irFactory.buildFun {
                                            returnType = benchmarkDescription.benchmarkClass.defaultType
                                            name = SpecialNames.ANONYMOUS
                                        }.apply {
                                            val benchmarkValueParameter = addValueParameter {
                                                type = benchmarkDescription.benchmarkClass.defaultType
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
                                                    for ((parameter = key, field = value) in benchmarkDescription.parameters) {
                                                        +irSetField(
                                                            receiver = irGet(benchmarkValueParameter),
                                                            field = field,
                                                            value = irAs(
                                                                argument = irCall(irRuntimeReferences.mapGetIrSimpleFunction).apply {
                                                                    arguments[0] = irGet(parametersValueParameter)
                                                                    arguments[1] = irString(parameter.name.identifier)
                                                                },
                                                                type = pluginContext.irBuiltIns.stringType
                                                            ),
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        superType = pluginContext.irBuiltIns.getKFunctionType(
                                            returnType = pluginContext.irBuiltIns.unitType,
                                            parameterTypes = listOf(
                                                benchmarkDescription.benchmarkClass.defaultType,
                                                irRuntimeReferences.mapIrClassSymbol.createType(
                                                    hasQuestionMark = false,
                                                    arguments = listOf(
                                                        pluginContext.irBuiltIns.stringType,
                                                        pluginContext.irBuiltIns.stringType,
                                                    )
                                                ),
                                            ),
                                        ),
                                        reflectionTargetSymbol = null,
                                        overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(2).invokeFun!!.symbol,
                                        captures = listOf(),
                                        origin = null,
                                    )
                                    // setup: (T) -> Unit
                                    arguments[3] = irRichFunctionReference(
                                        invokeFunction = benchmarkDescription.setupFunction,
                                        superType = pluginContext.irBuiltIns.getKFunctionType(
                                            returnType = pluginContext.irBuiltIns.unitType,
                                            parameterTypes = listOf(
                                                benchmarkDescription.benchmarkClass.defaultType,
                                            ),
                                        ),
                                        reflectionTargetSymbol = benchmarkDescription.setupFunction.symbol,
                                        overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(1).invokeFun!!.symbol,
                                        captures = listOf(),
                                        origin = null,
                                    )
                                    // teardown: (T) -> Unit
                                    arguments[4] = irRichFunctionReference(
                                        invokeFunction = benchmarkDescription.tearDownFunction,
                                        superType = pluginContext.irBuiltIns.getKFunctionType(
                                            returnType = pluginContext.irBuiltIns.unitType,
                                            parameterTypes = listOf(
                                                benchmarkDescription.benchmarkClass.defaultType,
                                            ),
                                        ),
                                        reflectionTargetSymbol = benchmarkDescription.setupFunction.symbol,
                                        overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(1).invokeFun!!.symbol,
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
                                            values = benchmarkDescription.parameters.keys.map { irString(it.name.identifier) },
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
                                            values = benchmarkDescription.parameters.keys.map {
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
                                                    arguments[0] = irString(it.name.identifier)
                                                    arguments[1] = irCall(irRuntimeReferences.emptyListIrSimpleFunction).apply {
                                                        type = pluginContext.irBuiltIns.stringType
                                                        typeArguments[0] = pluginContext.irBuiltIns.stringType
                                                    }
                                                }
                                            },
                                        )
                                    }
                                }
                            )
                            +irCall(irRuntimeReferences.kotlinxSuiteDescriptorAddIrSimpleFunctionSymbol).apply {
                                arguments[0] = irGet(descriptor)
                                arguments[1] = irCall(irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol).apply {
                                    type = irRuntimeReferences.kotlinxBenchmarkDescriptorWithBlackholeParameterIrClassSymbol.createType(
                                        hasQuestionMark = false,
                                        arguments = listOf(
                                            benchmarkDescription.benchmarkClass.defaultType,
                                        )
                                    )
                                    typeArguments[0] = benchmarkDescription.benchmarkClass.defaultType
                                    arguments[0] = irString(benchmark.fqName.asString())
                                    arguments[1] = irGet(descriptor)
                                    arguments[2] = irCall(irRuntimeReferences.kotlinxBlackholeConstructorIrConstructorSymbol)
                                    arguments[3] = irRichFunctionReference(
                                        invokeFunction = benchmarkDescription.benchmarkFunction,
                                        superType = pluginContext.irBuiltIns.getKFunctionType(
                                            returnType = pluginContext.irBuiltIns.unitType,
                                            parameterTypes = listOf(
                                                benchmarkDescription.benchmarkClass.defaultType,
                                                irRuntimeReferences.kotlinxBlackholeClassIrClassSymbol.defaultType,
                                            ),
                                        ),
                                        reflectionTargetSymbol = benchmarkDescription.benchmarkFunction.symbol,
                                        overriddenFunctionSymbol = pluginContext.irBuiltIns.kFunctionN(2).invokeFun!!.symbol,
                                        captures = listOf(),
                                        origin = null,
                                    )
                                }
                            }
                            +irCall(irRuntimeReferences.kotlinxSuiteExecutorBaseSuiteIrSimpleFunctionSymbol).apply {
                                typeArguments[0] = benchmarkDescription.benchmarkClass.defaultType
                                arguments[0] = irGet(declareAndExecuteSuitesFunctionExecutor)
                                arguments[1] = irGet(descriptor)
                            }
                        }
                    }
                }
            )
        
        return declaration
    }
}