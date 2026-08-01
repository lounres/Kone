/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.internalSuppliedTypesStoragePropertyName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irBlockBody
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.impl.IrPropertyReferenceImpl
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class SuppliedTypesStorageAccessorsTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
    val suppliabilityMapper: SuppliabilityMapper,
) : IrElementTransformerVoid() {
    override fun visitClass(declaration: IrClass): IrStatement {
        if (declaration.isSuppliable && declaration.kind in listOf<ClassKind>(CLASS, OBJECT)) {
            val suppliedTypesStorageProperty = declaration.symbol.referencePropertyThatOrFail(internalSuppliedTypesStoragePropertyName).owner
            val delegate = suppliedTypesStorageProperty.backingField!!
            val getter = suppliedTypesStorageProperty.getter!!
            val setter = suppliedTypesStorageProperty.setter!!
            getter.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = getter.symbol,
            ).run {
                irBlockBody {
                    +irReturn(
                        irCall(irRuntimeReferences.readWritePropertyGetValueIrSimpleFunctionSymbol).apply {
                            arguments[0] = irGetField(
                                receiver = irGet(getter.parameters[0]),
                                field = delegate
                            )
                            arguments[1] = irGet(getter.parameters[0])
                            arguments[2] = IrPropertyReferenceImpl(
                                startOffset = UNDEFINED_OFFSET,
                                endOffset = UNDEFINED_OFFSET,
                                type = irRuntimeReferences.suppliedTypesStoragePropertyReturnIrType,
                                symbol = suppliedTypesStorageProperty.symbol,
                                typeArgumentsCount = 0,
                                field = null,
                                getter = getter.symbol,
                                setter = setter.symbol,
                                origin = null,
                            )/*.apply {
                                arguments[0] = irGet(getter.parameters[0])
                            }*/
                            type = irRuntimeReferences.mapOfStringAndListOfSuppliedTypeIrType
                        }
                    )
                }
            }
            setter.body = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = getter.symbol,
            ).run {
                irBlockBody(setter) {
                    +irCall(irRuntimeReferences.readWritePropertySetValueIrSimpleFunctionSymbol).apply {
                        arguments[0] = irGetField(
                            receiver = irGet(setter.parameters[0]),
                            field = delegate
                        )
                        arguments[1] = irGet(setter.parameters[0])
                        arguments[2] = IrPropertyReferenceImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = irRuntimeReferences.suppliedTypesStoragePropertyReturnIrType,
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