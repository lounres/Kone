/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.ir

import dev.lounres.kone.plugin.contextsKeys.generatedContextKeyName
import dev.lounres.kone.plugin.suppliedTypes.ir.SuppliedTypesBuilder
import dev.lounres.kone.plugin.suppliedTypes.ir.fqNameStringForSuppliedTypes
import dev.lounres.kone.plugin.suppliedTypes.ir.isSupply
import dev.lounres.kone.plugin.suppliedTypes.ir.IrRuntimeReferences as SuppliedTypesIrRuntimeReferences
import dev.lounres.kone.scope
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irImplicitCast
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.impl.IrStringConcatenationImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.types.withNullability
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.Name


class ContextsKeysToStringFillingIrTransformer(
    private val pluginContext: IrPluginContext,
    private val suppliedTypesIrRuntimeReferences: SuppliedTypesIrRuntimeReferences,
    private val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<Nothing?>() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Nothing?): IrStatement {
        scope {
            if (declaration.name != Name.identifier("toString")) return@scope
            val constructedClass = declaration.parent as? IrClass ?: return@scope
            if (constructedClass.name != generatedContextKeyName) return@scope
            val containingClass = constructedClass.parent as? IrClass ?: return@scope
            if (!containingClass.isGenerateKey) return@scope
            check(declaration.body == null) { TODO() }
            declaration.body = DeclarationIrBuilder(pluginContext, declaration.symbol).run {
                irBlockBody {
                    +irReturn(
                        IrStringConcatenationImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = pluginContext.irBuiltIns.stringType,
                            arguments = buildList {
                                add(irString(constructedClass.classId!!.asFqNameString()))
                                
                                val supplyTypeParameters = constructedClass.typeParameters
                                val fqNameString = constructedClass.fqNameStringForSuppliedTypes
                                val dispatchReceiver = declaration.parameters.single { it.kind == IrParameterKind.DispatchReceiver }
                                
                                add(irString("<"))
                                for (i in supplyTypeParameters.indices) {
                                    if (i != 0) add(irString(", "))
                                    
                                    add(
                                        irCall(irRuntimeReferences.anyToStringIrSimpleFunctionSymbol).apply {
                                            arguments[0] = irCall(suppliedTypesIrRuntimeReferences.listGetIrSimpleFunctionSymbol).apply {
                                                arguments[0] = irImplicitCast(
                                                    argument = irCall(suppliedTypesIrRuntimeReferences.mapGetIrSimpleFunctionSymbol).apply {
                                                        arguments[0] = irCall(suppliedTypesIrRuntimeReferences.suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol).apply {
                                                            arguments[0] = irGet(dispatchReceiver)
                                                        }
                                                        arguments[1] = irString(fqNameString)
                                                        type = suppliedTypesIrRuntimeReferences.listOfSuppliedTypeIrType.withNullability(true)
                                                    },
                                                    type = suppliedTypesIrRuntimeReferences.listOfSuppliedTypeIrType
                                                )
                                                arguments[1] = irInt(i)
                                                type = suppliedTypesIrRuntimeReferences.suppliedTypeIrType
                                            }
                                        }
                                    )
                                }
                                add(irString(">"))
                            }
                        )
                    )
                }
            }
        }
        return super.visitSimpleFunction(declaration, data)
    }
}