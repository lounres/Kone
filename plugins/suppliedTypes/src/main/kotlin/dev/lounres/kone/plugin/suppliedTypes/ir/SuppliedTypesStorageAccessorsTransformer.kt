/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irBlockBody
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.impl.IrPropertyReferenceImpl
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.filterIsInstanceAnd

class SuppliedTypesStorageAccessorsTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
    val suppliabilityMapper: SuppliabilityMapper,
) : IrElementTransformerVoid() {
    private val readWritePropertyIrClassSymbol = pluginContext.finderForBuiltins().findClass(ClassId(packageFqName = FqName("kotlin.properties"), topLevelName = Name.identifier("ReadWriteProperty")))!!
    private val readWritePropertyGetValueIrSimpleFunctionSymbol = readWritePropertyIrClassSymbol.getSimpleFunction("getValue")!!
    private val readWritePropertySetValueIrSimpleFunctionSymbol = readWritePropertyIrClassSymbol.getSimpleFunction("setValue")!!
    private val listIrClassSymbol = pluginContext.finderForBuiltins().findClass(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("List")))!!
    private val listOfSuppliedTypeIrType = listIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(irRuntimeReferences.suppliedTypeIrType))
    private val mapIrClassSymbol = pluginContext.finderForBuiltins().findClass(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("Map")))!!
    private val mapOfStringAndListOfSuppliedTypeIrType = mapIrClassSymbol.createType(hasQuestionMark = false, arguments = listOf(pluginContext.irBuiltIns.stringType, listOfSuppliedTypeIrType))
    override fun visitClass(declaration: IrClass): IrStatement {
        if (declaration.isSuppliable && declaration.kind in listOf<ClassKind>(CLASS, OBJECT)) {
            val suppliedTypesStorageProperty = declaration.declarations.filterIsInstanceAnd<IrProperty> { it.name.asString() == "suppliedTypesStorage" }.single()
            val delegate = suppliedTypesStorageProperty.backingField!!
            val getter = suppliedTypesStorageProperty.getter!!
            val setter = suppliedTypesStorageProperty.setter!!
            getter.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = getter.symbol,
            ).run {
                irExprBody(
                    value = irCall(readWritePropertyGetValueIrSimpleFunctionSymbol).apply {
                        arguments[0] = irGetField(
                            receiver = irGet(getter.parameters[0]),
                            field = delegate
                        )
                        arguments[1] = irGet(getter.parameters[0])
                        arguments[2] = IrPropertyReferenceImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = readWritePropertyIrClassSymbol.createType(
                                hasQuestionMark = false,
                                arguments = listOf(
                                    pluginContext.irBuiltIns.anyNType,
                                    mapOfStringAndListOfSuppliedTypeIrType,
                                )
                            ),
                            symbol = suppliedTypesStorageProperty.symbol,
                            typeArgumentsCount = 0,
                            field = null,
                            getter = getter.symbol,
                            setter = setter.symbol,
                            origin = null,
                        )/*.apply {
                            arguments[0] = irGet(getter.parameters[0])
                        }*/
                    }
                )
            }
            setter.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = getter.symbol,
            ).run {
                irBlockBody(setter) {
                    +irCall(readWritePropertySetValueIrSimpleFunctionSymbol).apply {
                        arguments[0] = irGetField(
                            receiver = irGet(setter.parameters[0]),
                            field = delegate
                        )
                        arguments[1] = irGet(setter.parameters[0])
                        arguments[2] = IrPropertyReferenceImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = readWritePropertyIrClassSymbol.createType(
                                hasQuestionMark = false,
                                arguments = listOf(
                                    pluginContext.irBuiltIns.anyNType,
                                    mapOfStringAndListOfSuppliedTypeIrType,
                                )
                            ),
                            symbol = suppliedTypesStorageProperty.symbol,
                            typeArgumentsCount = 0,
                            field = null,
                            getter = getter.symbol,
                            setter = setter.symbol,
                            origin = null,
                        )/*.apply {
                            arguments[0] = irGet(setter.parameters[0])
                        }*/
                        arguments[3] = irGet(setter.parameters[1])
                    }
                }
            }
        }
        
        return super.visitClass(declaration)
    }
}