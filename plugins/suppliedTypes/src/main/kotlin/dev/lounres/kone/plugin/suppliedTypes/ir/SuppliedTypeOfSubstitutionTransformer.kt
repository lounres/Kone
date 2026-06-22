/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.visitors.IrTransformer


class SuppliedTypeOfSubstitutionTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
    val suppliabilityMapper: SuppliabilityMapper,
) : IrTransformer<SuppliedTypeOfSubstitutionTransformer.TransformationContext>() {
    data class TransformationContext(
        val suppliedTypes: Map<IrType, IrStatementsBuilder<*>.() -> IrVariable>,
        val localSymbol: IrSymbol?,
    ) {
        companion object {
            val INIT: TransformationContext get() = TransformationContext(
                suppliedTypes = emptyMap(),
                localSymbol = null,
            )
        }
    }
    
    override fun visitDeclaration(declaration: IrDeclarationBase, data: TransformationContext): IrStatement =
        super.visitDeclaration(
            declaration = declaration,
            TransformationContext(
                suppliedTypes = data.suppliedTypes,
                localSymbol = declaration.symbol
            )
        )
    
    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: TransformationContext): IrStatement =
        super.visitSimpleFunction(
            declaration = declaration,
            data = TransformationContext(
                suppliedTypes = buildMap {
                    putAll(data.suppliedTypes)
                    
                    val correspondingSuppliable = suppliabilityMapper.mapSupplianceToSuppliableOrNull(declaration)
                    if (correspondingSuppliable != null) {
                        val supplyTypeParameters = correspondingSuppliable.typeParameters.withIndex().filter { it.value.isSupply }.map { it.index }
                        val suppliedTypes = declaration.parameters.filter { it.isSupplianceProvided }
                        
                        check(supplyTypeParameters.size == suppliedTypes.size) { TODO() }
                        
                        for (i in supplyTypeParameters.indices)
                            put(declaration.typeParameters[supplyTypeParameters[i]].defaultType) {
                                irTemporary(irGet(suppliedTypes[i]))
                            }
                    }
                    
                    val parent = declaration.parent
                    if (parent is IrClass && parent.isSuppliable && parent.typeParameters.any { it.isSupply }) {
                        val supplyTypeParameters = parent.typeParameters.filter { it.isSupply }
                        val fqNameString = parent.fqNameStringForSuppliedTypes
                        val dispatchReceiver = declaration.parameters.single { it.kind == IrParameterKind.DispatchReceiver }
                        
                        for (i in supplyTypeParameters.indices)
                            put(supplyTypeParameters[i].defaultType) {
                                irTemporary(
                                    irCall(irRuntimeReferences.listGetIrSimpleFunctionSymbol).apply {
                                        arguments[0] = irImplicitCast(
                                            argument = irCall(irRuntimeReferences.mapGetIrSimpleFunctionSymbol).apply {
                                                arguments[0] = irCall(irRuntimeReferences.suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol).apply {
                                                    arguments[0] = irGet(dispatchReceiver)
                                                }
                                                arguments[1] = irString(fqNameString)
                                            },
                                            type = irRuntimeReferences.listOfSuppliedTypeIrType
                                        )
                                        arguments[1] = irInt(i)
                                    }
                                )
                            }
                        
                        // TODO: Handle outer classes of the class as well
                    }
                },
                localSymbol = data.localSymbol
            )
        )
    
    override fun visitField(declaration: IrField, data: TransformationContext): IrStatement =
        super.visitField(
            declaration = declaration,
            data = TransformationContext(
                suppliedTypes = buildMap {
                    putAll(data.suppliedTypes)
                    
                    val parent = declaration.parent
                    if (parent is IrClass && parent.isSuppliable && parent.typeParameters.any { it.isSupply }) {
                        val supplyTypeParameters = parent.typeParameters.filter { it.isSupply }
                        val fqNameString = parent.fqNameStringForSuppliedTypes
                        val dispatchReceiver = parent.thisReceiver!!

                        for (i in supplyTypeParameters.indices)
                            put(supplyTypeParameters[i].defaultType) {
                                irTemporary(
                                    irCall(irRuntimeReferences.listGetIrSimpleFunctionSymbol).apply {
                                        arguments[0] = irImplicitCast(
                                            argument = irCall(irRuntimeReferences.mapGetIrSimpleFunctionSymbol).apply {
                                                arguments[0] = irCall(irRuntimeReferences.suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol).apply {
                                                    arguments[0] = irGet(dispatchReceiver)
                                                }
                                                arguments[1] = irString(fqNameString)
                                            },
                                            type = irRuntimeReferences.listOfSuppliedTypeIrType
                                        )
                                        arguments[1] = irInt(i)
                                    }
                                )
                            }

                        // TODO: Handle outer classes of the class as well
                    }
                },
                localSymbol = data.localSymbol
            )
        )
    
//    override fun visitClass(declaration: IrClass, data: TransformationContext): IrStatement =
//        if (declaration.isSuppliable && declaration.typeParameters.any { it.isSupply })
//            super.visitClass(
//                declaration = declaration,
//                data = TransformationContext(
//                    suppliedTypes = buildMap {
//                        putAll(data.suppliedTypes)
//
//                        val supplyTypeParameters = declaration.typeParameters.filter { it.isSupply }
//                        val fqNameString = declaration.fqNameStringForSuppliedTypes
//
//                        for (i in supplyTypeParameters.indices)
//                            put(supplyTypeParameters[i].defaultType) {
//                                irTemporary(
//                                    irCall(listGetIrSimpleFunctionSymbol).apply {
//                                        arguments[0] = irImplicitCast(
//                                            argument = irCall(mapGetIrSimpleFunctionSymbol).apply {
//                                                arguments[0] = irCall(irRuntimeReferences.suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol).apply {
//                                                    arguments[0] = irGet(declaration.thisReceiver!!)
//                                                }
//                                                arguments[1] = irString(fqNameString)
//                                            },
//                                            type = listOfSuppliedTypeIrType
//                                        )
//                                        arguments[1] = irInt(i)
//                                    }
//                                )
//                            }
//                    },
//                    localSymbol = data.localSymbol
//                )
//            )
//        else
//            super.visitClass(declaration = declaration, data = data)
    
    override fun visitValueParameter(declaration: IrValueParameter, data: TransformationContext): IrStatement = declaration // TODO
    
    override fun visitCall(expression: IrCall, data: TransformationContext): IrElement {
        val declaration = expression.symbol.owner
        return when {
            declaration.symbol == irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol -> {
                val typeToSupply = expression.typeArguments.single()!!
                val declarationIrBuilder = DeclarationIrBuilder(pluginContext, data.localSymbol!!)
                declarationIrBuilder.irBlock {
                    +irGet(
                        SuppliedTypesBuilder(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            irStatementsBuilder = this,
                            initialSuppliedTypes = data.suppliedTypes.mapValues { [_, builder] -> lazy { builder() } }
                        ).resolve(typeToSupply)
                    )
                }
            }
            declaration.symbol in irRuntimeReferences.allWithSuppliedIrSimpleFunctionSymbols -> @Suppress("UNCHECKED_CAST") {
                val typesToSupply = expression.typeArguments.dropLast(1)
                check(typesToSupply.all { it is IrSimpleType && it.nullability == NOT_SPECIFIED && it.classifier is IrTypeParameterSymbol }) { TODO() }
                typesToSupply as List<IrSimpleType>
                
                val suppliances = expression.arguments.dropLast(1)
                check(suppliances.all { it != null })
                suppliances as List<IrExpression>
                
                check(typesToSupply.size == suppliances.size) { TODO() }
                
                val suppliedScope = expression.arguments.last()!!
                check(suppliedScope is IrFunctionExpression) { TODO() }
                val suppliedBody = suppliedScope.function.body ?: TODO()
                check(suppliedBody is IrBlockBody) { TODO() }
                
                DeclarationIrBuilder(pluginContext, data.localSymbol!!).irBlock {
                    val suppliedTypes = suppliances.map { irTemporary(it.transform(this@SuppliedTypeOfSubstitutionTransformer, data)) }
                    
                    val data = TransformationContext(
                        suppliedTypes = buildMap {
                            putAll(data.suppliedTypes)
                            
                            putAll(typesToSupply.zip(suppliedTypes.map { { it } }))
                        },
                        localSymbol = data.localSymbol,
                    )
                    
                    +irCall(irRuntimeReferences.runIrSimpleFunctionSymbol).apply {
                        arguments[0] = suppliedScope.transform(this@SuppliedTypeOfSubstitutionTransformer, data)
                    }
                }
            }
            else -> super.visitCall(expression, data)
        }
    }
}