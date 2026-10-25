/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.internalSuppliedTypesStoragePropertyName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class SuppliedTypesStorageAccessorsTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
    val suppliabilityMapper: SuppliabilityMapper,
) : IrElementTransformerVoid() {
    override fun visitClass(declaration: IrClass): IrStatement {
        if (declaration.isSuppliable && declaration.kind in listOf<ClassKind>(CLASS, OBJECT)) {
            val suppliedTypesStorageProperty = declaration.symbol.referencePropertyThatOrFail(internalSuppliedTypesStoragePropertyName).owner
            val backingField = suppliedTypesStorageProperty.backingField!!
            val getter = suppliedTypesStorageProperty.getter!!
            val setter = suppliedTypesStorageProperty.setter!!
            backingField.initializer = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = backingField.symbol,
            ).run {
                irExprBody(
                    irCallConstructor(
                        callee = irRuntimeReferences.atomicReferenceConstructorIrConstructorSymbol,
                        typeArguments = listOf(irRuntimeReferences.nullableMapOfStringAndListOfSuppliedTypeIrType)
                    ).apply {
                        arguments[0] = irNull(
                            irType = irRuntimeReferences.nullableMapOfStringAndListOfSuppliedTypeIrType,
                        )
                        type = irRuntimeReferences.atomicReferenceOfNullableMapOfStringAndListOfSuppliedTypeIrType
                    }
                )
            }
            backingField.type = irRuntimeReferences.atomicReferenceOfNullableMapOfStringAndListOfSuppliedTypeIrType
            getter.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = getter.symbol,
            ).run {
                irBlockBody {
                    val loadedValue = irTemporary(
                        irCall(irRuntimeReferences.atomicReferenceLoadIrSimpleFunctionSymbol).apply {
                            arguments[0] = irGetField(
                                receiver = irGet(getter.parameters[0]),
                                field = backingField
                            )
                            type = irRuntimeReferences.nullableMapOfStringAndListOfSuppliedTypeIrType
                        }
                    )
                    +irReturn(
                        irIfThenElse(
                            type = irRuntimeReferences.mapOfStringAndListOfSuppliedTypeIrType,
                            condition = irEqeqeq(
                                irGet(loadedValue),
                                irNull(
                                    irType = irRuntimeReferences.nullableMapOfStringAndListOfSuppliedTypeIrType,
                                )
                            ),
                            thenPart = irThrow(
                                arg = irCallConstructor(
                                    callee = irRuntimeReferences.suppliedTypesStorageInitializationExceptionConstructorIrConstructorSymbol,
                                    typeArguments = emptyList()
                                ).apply {
                                    arguments[0] = irCall(irRuntimeReferences.suppliedTypesStorageDelegateInitializationExceptionForGetterGetterIrPropertySymbol).apply {
                                        type = pluginContext.irBuiltIns.stringType
                                    }
                                    arguments[1] = irNull(pluginContext.irBuiltIns.throwableType.makeNullable())
                                    type = irRuntimeReferences.suppliedTypesStorageInitializationExceptionIrType
                                }
                            ),
                            elsePart = irImplicitCast(
                                argument = irGet(loadedValue),
                                type = irRuntimeReferences.mapOfStringAndListOfSuppliedTypeIrType,
                            ),
                            origin = null
                        )
                    )
                }
            }
            setter.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = setter.symbol,
            ).run {
                irBlockBody(setter) {
                    +irIfThen(
                        condition = irNot(
                            arg = irCall(irRuntimeReferences.atomicReferenceCompareAndSetIrSimpleFunctionSymbol).apply {
                                arguments[0] = irGetField(
                                    receiver = irGet(setter.parameters[0]),
                                    field = backingField
                                )
                                arguments[1] = irNull(irRuntimeReferences.nullableMapOfStringAndListOfSuppliedTypeIrType)
                                arguments[2] = irGet(setter.parameters[1])
                                type = pluginContext.irBuiltIns.booleanType
                            }
                        ),
                        thenPart = irThrow(
                            arg = irCallConstructor(
                                callee = irRuntimeReferences.suppliedTypesStorageInitializationExceptionConstructorIrConstructorSymbol,
                                typeArguments = emptyList()
                            ).apply {
                                arguments[0] = irCall(irRuntimeReferences.suppliedTypesStorageDelegateInitializationExceptionForSetterGetterIrPropertySymbol).apply {
                                    type = pluginContext.irBuiltIns.stringType
                                }
                                arguments[1] = irNull(pluginContext.irBuiltIns.throwableType.makeNullable())
                                type = irRuntimeReferences.suppliedTypesStorageInitializationExceptionIrType
                            }
                        )
                    )
                }
            }
        }
        
        return super.visitClass(declaration)
    }
}