/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.ir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueParameterSymbol
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.createThisReceiverParameter
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


class BenchmarkClassGenerationTransformer(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
    private val benchmarkToGeneratedClass: MutableMap<IrSimpleFunction, GeneratedBenchmarkClassDescription>,
) : IrTransformer<Nothing?>() {
    private object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "BenchmarkGenerationTransformer.Key"
    }
    private val pluginDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(Key)
    
    private val irFactory = pluginContext.irFactory
    private val irBuilder: IrBuilder = object : IrBuilder(pluginContext, UNDEFINED_OFFSET, UNDEFINED_OFFSET) {}
    
    override fun visitFile(declaration: IrFile, data: Nothing?): IrFile {
        for (subdeclaration in declaration.declarations.toList()) {
            if (subdeclaration is IrSimpleFunction && subdeclaration.isBenchmark) {
                val benchmarkClass = irFactory.buildClass {
                    origin = pluginDeclarationOrigin
                    name = Name.identifier("KoneBenchmarkClassFor${subdeclaration.name.identifier.replaceFirstChar { it.uppercaseChar() }}")
                    kind = CLASS
                    modality = OPEN
                    visibility = DescriptorVisibilities.PUBLIC
                }
                benchmarkClass.superTypes = listOf(pluginContext.irBuiltIns.anyType)
                benchmarkClass.createThisReceiverParameter()
                benchmarkClass.annotations = listOf(
                    irBuilder.irAnnotation(irRuntimeReferences.kotlinxStateAnnotationIrClassSymbol.constructors.single()).apply {
                        arguments[0] = IrGetEnumValueImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = irRuntimeReferences.kotlinxScopeEnumIrClassSymbol.defaultType,
                            symbol = irRuntimeReferences.kotlinxScopeBenchmarkEnumEntryIrEnumEntrySymbol,
                        )
                    },
                )
                benchmarkClass.addConstructor {
                    name = SpecialNames.INIT
                    origin = pluginDeclarationOrigin
                    visibility = DescriptorVisibilities.PUBLIC
                    isPrimary = true
                }.apply {
                    body = DeclarationIrBuilder(
                        generatorContext = pluginContext,
                        symbol = symbol,
                    ).run {
                        irBlockBody {
                            +irDelegatingConstructorCall(pluginContext.irBuiltIns.anyClass.owner.constructors.single())
                        }
                    }
                }
                val setupFunction = benchmarkClass.addFunction {
                    origin = pluginDeclarationOrigin
                    name = Name.identifier("setup")
                    visibility = DescriptorVisibilities.PUBLIC
                    returnType = pluginContext.irBuiltIns.unitType
                }.apply {
                    addValueParameter {
                        origin = pluginDeclarationOrigin
                        name = SpecialNames.THIS
                        visibility = DescriptorVisibilities.PUBLIC
                        kind = DispatchReceiver
                        type = benchmarkClass.symbol.defaultType
                    }.apply {
                        kind = DispatchReceiver
                    }
                }
                setupFunction.annotations = listOf(
                    irBuilder.irAnnotation(irRuntimeReferences.kotlinxSetupAnnotationIrClassSymbol.constructors.single()),
                )
                val tearDownFunction = benchmarkClass.addFunction {
                    origin = pluginDeclarationOrigin
                    name = Name.identifier("tearDown")
                    visibility = DescriptorVisibilities.PUBLIC
                    returnType = pluginContext.irBuiltIns.unitType
                }.apply {
                    addValueParameter {
                        origin = pluginDeclarationOrigin
                        name = SpecialNames.THIS
                        visibility = DescriptorVisibilities.PUBLIC
                        kind = DispatchReceiver
                        type = benchmarkClass.symbol.defaultType
                    }.apply {
                        kind = DispatchReceiver
                    }
                }
                tearDownFunction.annotations = listOf(
                    irBuilder.irAnnotation(irRuntimeReferences.kotlinxTearDownAnnotationIrClassSymbol.constructors.single()),
                )
                val benchmarkFunction = benchmarkClass.addFunction {
                    origin = pluginDeclarationOrigin
                    name = Name.identifier("benchmark")
                    visibility = DescriptorVisibilities.PUBLIC
                    returnType = pluginContext.irBuiltIns.unitType
                }.apply {
                    addValueParameter {
                        origin = pluginDeclarationOrigin
                        name = SpecialNames.THIS
                        visibility = DescriptorVisibilities.PUBLIC
                        kind = DispatchReceiver
                        type = benchmarkClass.symbol.defaultType
                    }.apply {
                        kind = DispatchReceiver
                    }
                    addValueParameter {
                        origin = pluginDeclarationOrigin
                        name = Name.identifier("blackhole")
                        visibility = DescriptorVisibilities.PUBLIC
                        kind = Regular
                        type = irRuntimeReferences.kotlinxBlackholeClassIrClassSymbol.defaultType
                    }
                }
                benchmarkFunction.annotations = listOf(
                    irBuilder.irAnnotation(irRuntimeReferences.kotlinxBenchmarkAnnotationIrClassSymbol.constructors.single()),
                )
                val subdeclarationIrBlockBodyBuilder = IrBlockBodyBuilder(
                    context = pluginContext,
                    scope = Scope(subdeclaration.symbol),
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                )
                val benchmarkFunctionIrBlockBodyBuilder = IrBlockBodyBuilder(
                    context = pluginContext,
                    scope = Scope(benchmarkFunction.symbol),
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                )
                val setupFunctionIrBlockBodyBuilder = IrBlockBodyBuilder(
                    context = pluginContext,
                    scope = Scope(setupFunction.symbol),
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                )
                val tearDownFunctionIrBlockBodyBuilder = IrBlockBodyBuilder(
                    context = pluginContext,
                    scope = Scope(tearDownFunction.symbol),
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                )
                var benchmarkConfigurationParameter: IrValueParameter? = null
                val benchmarkBodyReplacements = mutableMapOf<IrSymbol, (IrExpression) -> IrExpression>()
                val injectionBlockReplacements = mutableMapOf<IrSymbol, (IrExpression) -> IrExpression>()
                val subdeclarationBodyReplacements = mutableMapOf<IrValueParameterSymbol, IrVariable>()
                val parametersDescription = mutableMapOf<IrValueParameter, IrField>()
                for (parameter in subdeclaration.parameters) {
                    if (parameter.type == irRuntimeReferences.benchmarkConfigurationClassIrClassSymbol.defaultType) {
                        benchmarkConfigurationParameter = parameter
                    } else with(irBuilder) {
                        val serializedField = benchmarkClass.addField {
                            origin = pluginDeclarationOrigin
                            name = parameter.name
                            type = pluginContext.irBuiltIns.stringType
                            visibility = DescriptorVisibilities.PUBLIC
                        }.apply {
                            initializer = irExprBody(
                                value = irString(value = "")
                            )
                            annotations = listOf(
                                irAnnotation(irRuntimeReferences.kotlinxParamAnnotationIrClassSymbol.constructors.single()).apply {
                                    arguments[0] = irVararg(
                                        elementType = pluginContext.irBuiltIns.stringType,
                                        values = listOf(irString("")),
                                    )
                                }
                            )
                        }
                        val deserializedField = benchmarkClass.addField {
                            origin = pluginDeclarationOrigin
                            name = Name.identifier("${parameter.name.identifier}\$deserialized")
                            type = parameter.type.makeNullable()
                            visibility = DescriptorVisibilities.PRIVATE_TO_THIS
                        }.apply {
                            initializer = irExprBody(
                                value = irNull(
                                    irType = parameter.type.makeNullable(),
                                )
                            )
                        }
                        val localSubdeclarationVariable = subdeclarationIrBlockBodyBuilder.run {
                            irTemporary(
                                value = irCall(irRuntimeReferences.kotlinxJsonEncodeToStringFunctionIrSimpleFunctionSymbol).apply {
                                    typeArguments[0] = parameter.type
                                    arguments[0] = irGetObject(irRuntimeReferences.kotlinxJsonCompanionIrClassSymbol)
                                    arguments[1] = irGet(parameter)
                                },
                                origin = pluginDeclarationOrigin,
                            )
                        }
                        val localSetupVariable = setupFunctionIrBlockBodyBuilder.run {
                            val variable = irTemporary(
                                value = irCall(irRuntimeReferences.kotlinxJsonDecodeFromStringFunctionIrSimpleFunctionSymbol).apply {
                                    type = parameter.type
                                    typeArguments[0] = parameter.type
                                    arguments[0] = irGetObject(irRuntimeReferences.kotlinxJsonCompanionIrClassSymbol)
                                    arguments[1] = irGetField(
                                        receiver = irGet(setupFunction.parameters[0]),
                                        field = serializedField,
                                    )
                                },
                                origin = pluginDeclarationOrigin,
                            )
                            +irSetField(
                                receiver = irGet(setupFunction.parameters[0]),
                                field = deserializedField,
                                value = irGet(variable),
                            )
                            variable
                        }
                        val localBenchmarkVariable = benchmarkFunctionIrBlockBodyBuilder.run {
                            irTemporary(
                                value = irAs(
                                    argument = irGetField(
                                        receiver = irGet(benchmarkFunction.parameters[0]),
                                        field = deserializedField,
                                    ),
                                    type = parameter.type,
                                ),
                                origin = pluginDeclarationOrigin,
                            )
                        }
                        tearDownFunctionIrBlockBodyBuilder.run {
                            +irSetField(
                                receiver = irGet(tearDownFunction.parameters[0]),
                                field = serializedField,
                                value = irString("")
                            )
                            +irSetField(
                                receiver = irGet(tearDownFunction.parameters[0]),
                                field = deserializedField,
                                value = irNull(
                                    irType = parameter.type.makeNullable(),
                                )
                            )
                        }
                        
                        benchmarkBodyReplacements[parameter.symbol] = { irBuilder.irGet(localBenchmarkVariable) }
                        injectionBlockReplacements[parameter.symbol] = { irBuilder.irGet(localSetupVariable) }
                        subdeclarationBodyReplacements[parameter.symbol] = localSubdeclarationVariable
                        parametersDescription[parameter] = serializedField
                    }
                }
                val transformedBody = subdeclaration.body!!.transform(
                    transformer = BenchmarkBodyTransformer(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        irBuilder = irBuilder,
                        subdeclarationFunction = subdeclaration,
                        benchmarkClass = benchmarkClass,
                        benchmarkFunction = benchmarkFunction,
                        setupFunction = setupFunction,
                        setupFunctionIrBlockBodyBuilder = setupFunctionIrBlockBodyBuilder,
                        tearDownFunctionDispatchReceiver = tearDownFunction.parameters[0],
                        tearDownFunctionIrBlockBodyBuilder = tearDownFunctionIrBlockBodyBuilder,
                        benchmarkBodyReplacements = benchmarkBodyReplacements,
                        injectionBlockReplacements = injectionBlockReplacements,
                    ),
                    data = null,
                )
                benchmarkFunctionIrBlockBodyBuilder.run {
                    when (transformedBody) {
                        is IrBlockBody -> +transformedBody.statements
                        is IrExpressionBody -> +transformedBody.expression
                        is IrSyntheticBody -> benchmarksIrPluginException("Cannot transform benchmark with synthetic body.")
                    }
                }
                subdeclarationIrBlockBodyBuilder.run {
                    +irReturn(
                        value = irCall(
                            callee = irRuntimeReferences.runBenchmarkFunctionIrSimpleFunctionSymbol,
                        ).apply {
                            arguments[0] = IrClassReferenceImpl(
                                startOffset = UNDEFINED_OFFSET,
                                endOffset = UNDEFINED_OFFSET,
                                type = pluginContext.irBuiltIns.kClassClass.createType(
                                    hasQuestionMark = false,
                                    arguments = listOf(benchmarkClass.symbol.defaultType),
                                ),
                                symbol = benchmarkClass.symbol,
                                classType = benchmarkClass.symbol.defaultType,
                            )
                            arguments[1] = irCall(irRuntimeReferences.mapOfIrSimpleFunction).apply {
                                type = irRuntimeReferences.mapIrClassSymbol.createType(
                                    hasQuestionMark = false,
                                    arguments = listOf(pluginContext.irBuiltIns.stringType, pluginContext.irBuiltIns.stringType),
                                )
                                typeArguments[0] = pluginContext.irBuiltIns.stringType
                                typeArguments[1] = pluginContext.irBuiltIns.stringType
                                arguments[0] = irVararg(
                                    elementType = irRuntimeReferences.pairIrClassSymbol.createType(
                                        hasQuestionMark = false,
                                        arguments = listOf(pluginContext.irBuiltIns.stringType, pluginContext.irBuiltIns.stringType),
                                    ),
                                    values = subdeclarationBodyReplacements.entries.map { (parameterSymbol = key, replacements = value) ->
                                        irCall(irRuntimeReferences.pairIrConstructorSymbol).apply {
                                            type = irRuntimeReferences.pairIrClassSymbol.createType(
                                                hasQuestionMark = false,
                                                arguments = listOf(pluginContext.irBuiltIns.stringType, pluginContext.irBuiltIns.stringType),
                                            )
                                            typeArguments[0] = pluginContext.irBuiltIns.stringType
                                            typeArguments[1] = pluginContext.irBuiltIns.stringType
                                            arguments[0] = irString(parameterSymbol.owner.name.identifier)
                                            arguments[1] = irGet(replacements)
                                        }
                                    }
                                )
                            }
                            arguments[2] = irGet(benchmarkConfigurationParameter!!)
                        }
                    )
                }
                benchmarkFunction.body = benchmarkFunctionIrBlockBodyBuilder.doBuild()
                subdeclaration.body = subdeclarationIrBlockBodyBuilder.doBuild()
                setupFunction.body = setupFunctionIrBlockBodyBuilder.doBuild()
                tearDownFunction.body = tearDownFunctionIrBlockBodyBuilder.doBuild().also { it.statements.reverse() }
                declaration.addChild(benchmarkClass)
                benchmarkToGeneratedClass[subdeclaration] = GeneratedBenchmarkClassDescription(
                    benchmarkClass = benchmarkClass,
                    parameters = parametersDescription,
                    setupFunction = setupFunction,
                    benchmarkFunction = benchmarkFunction,
                    tearDownFunction = tearDownFunction,
                )
            }
        }
        return declaration
    }
}