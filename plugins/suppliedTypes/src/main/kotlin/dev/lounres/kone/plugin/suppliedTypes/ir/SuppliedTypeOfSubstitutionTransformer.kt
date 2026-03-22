/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeOfCallableId
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


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
            val INIT: TransformationContext = TransformationContext(
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
        if (declaration.isSupplianceProvided && suppliabilityMapper.mapSupplianceToSuppliable(declaration).typeParameters.any { it.isSupply })
            super.visitSimpleFunction(
                declaration = declaration,
                data = TransformationContext(
                    suppliedTypes = buildMap {
                        putAll(data.suppliedTypes)
                        
                        val supplyTypeParameters = suppliabilityMapper.mapSupplianceToSuppliable(declaration).typeParameters.filter { it.isSupply }
                        val suppliedTypes = declaration.parameters.filter { it.isSupplianceProvided }
                        
                        check(supplyTypeParameters.size == suppliedTypes.size) { TODO() }
                        
                        for (i in supplyTypeParameters.indices)
                            put(supplyTypeParameters[i].defaultType) {
                                irTemporary(irGet(suppliedTypes[i]))
                            }
                    },
                    localSymbol = data.localSymbol
                )
            )
        else
            super.visitSimpleFunction(declaration = declaration, data = data)
    
    val declarationFinder = pluginContext.finderForBuiltins()
    val listIrClassSymbol = declarationFinder.findClass(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("List")))!!
    val listOfSuppliedTypeIrType = listIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(irRuntimeReferences.suppliedTypeIrType))
    val listOfSuppliedTypeNullableIrType = listIrClassSymbol.createType(hasQuestionMark = true, arguments = listOf(irRuntimeReferences.suppliedTypeIrType))
    val listGetIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = listIrClassSymbol.getSimpleFunction("get")!!
    val mapIrClassSymbol = declarationFinder.findClass(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("Map")))!!
    val mapGetIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = mapIrClassSymbol.getSimpleFunction("get")!!
    
    override fun visitClass(declaration: IrClass, data: TransformationContext): IrStatement =
        if (declaration.isSuppliable && declaration.typeParameters.any { it.isSupply })
            super.visitClass(
                declaration = declaration,
                data = TransformationContext(
                    suppliedTypes = buildMap {
                        putAll(data.suppliedTypes)
                        
                        val supplyTypeParameters = declaration.typeParameters.filter { it.isSupply }
                        val fqNameString = declaration.fqNameStringForSuppliedTypes
                        
                        for (i in supplyTypeParameters.indices)
                            put(supplyTypeParameters[i].defaultType) {
                                irTemporary(
                                    irCall(listGetIrSimpleFunctionSymbol).apply {
                                        arguments[0] = irImplicitCast(
                                            argument = irCall(mapGetIrSimpleFunctionSymbol).apply {
                                                arguments[0] = irCall(irRuntimeReferences.suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol).apply {
                                                    arguments[0] = irGet(declaration.thisReceiver!!)
                                                }
                                                arguments[1] = irString(fqNameString)
                                            },
                                            type = listOfSuppliedTypeIrType
                                        )
                                        arguments[1] = irInt(i)
                                    }
                                )
                            }
                    },
                    localSymbol = data.localSymbol
                )
            )
        else
            super.visitClass(declaration = declaration, data = data)
    
    override fun visitValueParameter(declaration: IrValueParameter, data: TransformationContext): IrStatement = declaration
    
    override fun visitCall(expression: IrCall, data: TransformationContext): IrElement {
        val declaration = expression.symbol.owner
        if (declaration.callableId != suppliedTypeOfCallableId) return super.visitCall(expression, data)
        val typeToSupply = expression.typeArguments.single()!!
        val declarationIrBuilder = DeclarationIrBuilder(pluginContext, data.localSymbol!!)
        return declarationIrBuilder.irBlock {
            +irGet(
                SuppliedTypesBuilder(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    irStatementsBuilder = this,
                    initialSuppliedTypes = data.suppliedTypes.mapValues { (_, builder) -> lazy { builder() } }
                ).resolve(typeToSupply)
            )
        }
    }
}