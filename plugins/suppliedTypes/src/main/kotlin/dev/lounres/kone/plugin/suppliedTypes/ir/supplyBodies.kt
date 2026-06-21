/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.noSuppliedTypeParameterInClassStubParameterName
import dev.lounres.kone.plugin.suppliedTypes.suppliableFunctionGeneratedBodyMessage
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrAnonymousInitializerSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid


fun supplyFunctionsBodies(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    for ([suppliance, suppliable] in suppliabilityMapper.moduleFunctionsSupplianceToSuppliableMapping) {
        check(suppliance.body == null) { TODO() }
        suppliance.body = suppliable.body!!
            .deepCopyWithSymbols(initialParent = suppliance)
            .transform(
                ParametersSubstitutionTransformer(
                    typeParametersSubstitution = suppliable.typeParameters.zip(suppliance.typeParameters).toMap(),
                    valueParametersSubstitution = suppliable.parameters.zip(suppliance.parameters.filter { !it.isSupplianceProvided }).toMap(),
                ),
                null
            )
            .transform(
                FunctionSubstitutionTransformer(
                    symbolSubstitution = mapOf(suppliable.symbol to suppliance.symbol),
                ),
                null
            )
        suppliable.body = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = suppliable.symbol,
        ).run {
            irBlockBody {
                +irReturn(
                    irCall(
                        callee = irRuntimeReferences.suppliedTypesPluginExceptionForPluginMachineryIrSimpleFunctionSymbol,
                    ).apply {
                        arguments[0] = irString(suppliableFunctionGeneratedBodyMessage)
                    }
                )
            }
        }
    }
}

class FunctionSubstitutionTransformer(
    private val symbolSubstitution: Map<IrSimpleFunctionSymbol, IrSimpleFunctionSymbol>,
) : IrElementTransformerVoid() {
    override fun visitReturn(expression: IrReturn): IrExpression {
        expression.returnTargetSymbol = symbolSubstitution[expression.returnTargetSymbol] ?: expression.returnTargetSymbol
        return super.visitReturn(expression)
    }
}

fun supplyConstructorsBodies(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    for ([suppliance, suppliable] in suppliabilityMapper.moduleConstructorsSupplianceToSuppliableMapping) {
        check(suppliance.body == null) { TODO() }
        suppliance.body = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = suppliance.symbol,
        ).run {
            irBlockBody {
                +irDelegatingConstructorCall(suppliable).apply {
                    typeArguments.clear()
                    typeArguments.addAll(suppliable.constructedClass.typeParameters.map { it.defaultType })
                    arguments.clear()
                    arguments.addAll(suppliance.parameters.filter { !it.isSupplianceProvided }.map { irGet(it) })
                }
                
                val irClass = suppliance.constructedClass
                val suppliableTypeParameters = irClass.typeParameters.filter { it.isSupply }
                val suppliedTypes = suppliance.parameters.filter { it.isSupplianceProvided && it.name != noSuppliedTypeParameterInClassStubParameterName }
                
                check(suppliableTypeParameters.size == suppliedTypes.size) { TODO() }
                
                val suppliedTypesBuilder = SuppliedTypesBuilder(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    irStatementsBuilder = this,
                    initialSuppliedTypes = buildMap {
                        for (i in suppliableTypeParameters.indices) {
                            put(
                                key = suppliableTypeParameters[i].defaultType,
                                value = lazy { irTemporary(value = irGet(suppliedTypes[i])) },
                            )
                        }
                    }
                )
                
                val variablesForSuppliedTypesStorage = buildMap {
                    if (suppliableTypeParameters.isNotEmpty())
                        put(
                            irClass.fqNameStringForSuppliedTypes,
                            suppliableTypeParameters.map { suppliedTypesBuilder.resolve(it.defaultType) },
                        )
                    
                    irClass.superTypes
                        .filter { (it as IrSimpleType).classOrFail.owner.let { it.isSuppliable && it.typeParameters.any { it.isSupply } } }
                        .associateTo(this) {
                            it as IrSimpleType
                            val irClass = (it.classifier as IrClassSymbol).owner
                            
                            irClass.fqNameStringForSuppliedTypes to
                                    irClass.typeParameters.zip(it.arguments)
                                        .filter { it.first.isSupply }
                                        .map { suppliedTypesBuilder.resolve((it.second as IrTypeProjection).type) }
                        }
                }
                
                +irCall(irRuntimeReferences.suppliableClassSuppliedTypesStorageSetterIrSimpleFunctionSymbol).apply {
                    arguments[0] = irGet(irClass.thisReceiver!!)
                    arguments[1] = irCall(irRuntimeReferences.mapOfIrSimpleFunction).apply {
                        typeArguments[0] = pluginContext.irBuiltIns.stringType
                        typeArguments[1] = irRuntimeReferences.listOfSuppliedTypeIrType
                        arguments[0] = irVararg(
                            elementType = irRuntimeReferences.pairOfStringAndListOfSuppliedTypeIrType,
                            values = variablesForSuppliedTypesStorage.entries.map { [fqName, suppliedTypes] ->
                                irCallConstructor(
                                    callee = irRuntimeReferences.pairIrConstructorSymbol,
                                    typeArguments = listOf(pluginContext.irBuiltIns.stringType, irRuntimeReferences.listOfSuppliedTypeIrType),
                                ).apply {
                                    arguments[0] = irString(fqName)
                                    arguments[1] = irCall(
                                        callee = irRuntimeReferences.listOfIrSimpleFunction,
                                        type = irRuntimeReferences.listOfSuppliedTypeIrType,
                                        typeArguments = listOf(irRuntimeReferences.suppliedTypeIrType),
                                    ).apply {
                                        arguments[0] = irVararg(
                                            elementType = irRuntimeReferences.suppliedTypeIrType,
                                            values = suppliedTypes.map { irGet(it) }
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
                
                +irCall(irRuntimeReferences.suppliableClassAfterSupplianceIrSimpleFunctionSymbol).apply {
                    arguments[0] = irGet(irClass.thisReceiver!!)
                }
            }
        }
    }
}

class SuppliableSingletonsSuppliedTypesStorageInitializerTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
    val suppliabilityMapper: SuppliabilityMapper,
) : IrElementTransformerVoid() {
    private object Key : GeneratedDeclarationKey()
    private val origin = IrDeclarationOrigin.GeneratedByPlugin(Key)
    
    override fun visitClass(declaration: IrClass): IrStatement {
        if (declaration.isSuppliable && declaration.kind in listOf<ClassKind>(OBJECT)) {
            declaration.declarations +=
                pluginContext.irFactory.createAnonymousInitializer(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    origin = origin,
                    symbol = IrAnonymousInitializerSymbolImpl(declaration.symbol),
                    isStatic = false,
                ).apply {
                    parent = declaration
                    body = DeclarationIrBuilder(
                        generatorContext = pluginContext,
                        symbol = this.symbol,
                    ).irBlockBody {
                        val suppliedTypesBuilder = SuppliedTypesBuilder(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            irStatementsBuilder = this,
                            initialSuppliedTypes = emptyMap()
                        )
                        
                        val variablesForSuppliedTypesStorage = buildMap {
                            declaration.superTypes
                                .filter { (it as IrSimpleType).classOrFail.owner.let { it.isSuppliable && it.typeParameters.any { it.isSupply } } }
                                .associateTo(this) {
                                    it as IrSimpleType
                                    val irClass = (it.classifier as IrClassSymbol).owner
                                    
                                    irClass.fqNameStringForSuppliedTypes to
                                            irClass.typeParameters.zip(it.arguments)
                                                .filter { it.first.isSupply }
                                                .map { suppliedTypesBuilder.resolve((it.second as IrTypeProjection).type) }
                                }
                        }
                        
                        +irCall(irRuntimeReferences.suppliableClassSuppliedTypesStorageSetterIrSimpleFunctionSymbol).apply {
                            arguments[0] = irGet(declaration.thisReceiver!!)
                            arguments[1] = irCall(irRuntimeReferences.mapOfIrSimpleFunction).apply {
                                typeArguments[0] = pluginContext.irBuiltIns.stringType
                                typeArguments[1] = irRuntimeReferences.listOfSuppliedTypeIrType
                                arguments[0] = irVararg(
                                    elementType = irRuntimeReferences.pairOfStringAndListOfSuppliedTypeIrType,
                                    values = variablesForSuppliedTypesStorage.entries.map { [fqName, suppliedTypes] ->
                                        irCallConstructor(
                                            callee = irRuntimeReferences.pairIrConstructorSymbol,
                                            typeArguments = listOf(pluginContext.irBuiltIns.stringType, irRuntimeReferences.listOfSuppliedTypeIrType),
                                        ).apply {
                                            arguments[0] = irString(fqName)
                                            arguments[1] = irCall(
                                                callee = irRuntimeReferences.listOfIrSimpleFunction,
                                                type = irRuntimeReferences.listOfSuppliedTypeIrType,
                                                typeArguments = listOf(irRuntimeReferences.suppliedTypeIrType),
                                            ).apply {
                                                arguments[0] = irVararg(
                                                    elementType = irRuntimeReferences.suppliedTypeIrType,
                                                    values = suppliedTypes.map { irGet(it) }
                                                )
                                            }
                                        }
                                    },
                                )
                            }
                        }
                        
                        +irCall(irRuntimeReferences.suppliableClassAfterSupplianceIrSimpleFunctionSymbol).apply {
                            arguments[0] = irGet(declaration.thisReceiver!!)
                        }
                    }
                }
        }
        
        return super.visitClass(declaration)
    }
}