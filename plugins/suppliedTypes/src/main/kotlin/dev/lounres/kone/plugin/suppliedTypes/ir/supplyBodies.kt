/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


fun supplyFunctionsBodies(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    val errorIrSimpleFunctionSymbol = pluginContext
        .finderForBuiltins()
        .findFunctions(CallableId(packageName = FqName("kotlin"), callableName = Name.identifier("error")))
        .single()
    
    for ((suppliance, suppliable) in suppliabilityMapper.moduleFunctionsSupplianceToSuppliableMapping) {
        suppliance.body = suppliable.body!!.deepCopyWithSymbols(initialParent = suppliance)
        suppliable.body = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = suppliable.symbol,
        ).run {
            irExprBody(
                value = irCall(
                    callee = errorIrSimpleFunctionSymbol,
                ).apply {
                    arguments[0] = irString("Intrinsic function call was not substituted. Ensure you have applied supplied types compiler plugin.")
                }
            )
        }
    }
}

fun supplyConstructorsBodies(
    pluginContext: IrPluginContext,
    irRuntimeReferences: IrRuntimeReferences,
    suppliabilityMapper: SuppliabilityMapper,
) {
    val declarationFinder = pluginContext.finderForBuiltins()
    
    val listOfIrSimpleFunction: IrSimpleFunctionSymbol =
        declarationFinder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
            val parameters = symbol.owner.parameters
            parameters.size == 1 && parameters[0].isVararg
        }
    val mapOfIrSimpleFunction: IrSimpleFunctionSymbol =
        declarationFinder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("mapOf"))) { symbol ->
            val parameters = symbol.owner.parameters
            parameters.size == 1 && parameters[0].isVararg
        }
    val listIrClassSymbol = declarationFinder.findClass(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("List")))!!
    val listOfSuppliedTypeIrType = listIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(irRuntimeReferences.suppliedTypeIrType))
    val pairIrClassSymbol = declarationFinder.findClass(ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Pair")))!!
    val pairIrConstructorSymbol = pairIrClassSymbol.constructors.single()
    val pairOfStringAndListOfSuppliedTypeIrType = pairIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(pluginContext.irBuiltIns.stringType, listOfSuppliedTypeIrType))
    
    for (suppliance in suppliabilityMapper.moduleConstructorsSupplianceToSuppliableMapping.keys) {
        val oldBody = suppliance.body!!
        suppliance.body = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = suppliance.symbol,
        ).run {
            irBlockBody {
                for (statement in oldBody.statements) +statement
                
                val irClass = suppliance.constructedClass
                val suppliableTypeParameters = irClass.typeParameters.filter { it.isSupply }
                val suppliedTypes = suppliance.parameters.filter { it.isSupplianceProvided }
                
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
                    arguments[1] = irCall(mapOfIrSimpleFunction).apply {
                        typeArguments[0] = pluginContext.irBuiltIns.stringType
                        typeArguments[1] = listOfSuppliedTypeIrType
                        arguments[0] = irVararg(
                            elementType = pairOfStringAndListOfSuppliedTypeIrType,
                            values = variablesForSuppliedTypesStorage.entries.map { (fqName, suppliedTypes) ->
                                irCallConstructor(
                                    callee = pairIrConstructorSymbol,
                                    typeArguments = listOf(pluginContext.irBuiltIns.stringType, listOfSuppliedTypeIrType),
                                ).apply {
                                    arguments[0] = irString(fqName)
                                    arguments[1] = irCall(
                                        callee = listOfIrSimpleFunction,
                                        type = listOfSuppliedTypeIrType,
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