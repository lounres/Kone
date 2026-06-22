/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.plugin.suppliedTypes.noSuppliedTypeParameterInClassStubParameterName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildConstructor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.copyFunctionSignatureFrom
import org.jetbrains.kotlin.ir.visitors.IrTransformer


class ConstructorSuppliancesGenerationTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<ConstructorSuppliancesGenerationTransformer.TransformationContext>() {
    data class TransformationContext(
        val suppliableToSupplianceMapping: MutableMap<IrConstructor, IrConstructor>,
        val supplianceToSuppliableMapping: MutableMap<IrConstructor, IrConstructor>,
    ) {
        companion object {
            val INIT: TransformationContext get() = TransformationContext(
                suppliableToSupplianceMapping = mutableMapOf(),
                supplianceToSuppliableMapping = mutableMapOf(),
            )
        }
    }
    
    private fun createSupplianceFor(constructor: IrConstructor): IrConstructor =
        pluginContext.irFactory.buildConstructor {
            updateFrom(constructor)
            isPrimary = false
        }.apply {
            annotations += irRuntimeReferences.newSupplianceProvidedAnnotation()
            copyFunctionSignatureFrom(constructor)
            var addFalseSuppliedArgument = true
            for (typeParameter in constructor.constructedClass.typeParameters) {
                if (!typeParameter.isSupply) continue
                addFalseSuppliedArgument = false
                addValueParameter(internalSupplierParameterName(typeParameter.name), irRuntimeReferences.suppliedTypeIrType).also { newValueParameter ->
                    newValueParameter.annotations += irRuntimeReferences.newSupplianceProvidedAnnotation()
//                    newValueParameter.defaultValue = DeclarationIrBuilder(pluginContext, this.symbol).run {
//                        // TODO: KT-53992
//                        irExprBody(
//                            irCall(irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol).also { call ->
//                                call.typeArguments[0] = typeParameter.defaultType
//                            }
//                        )
//                    }
                }
            }
            if (addFalseSuppliedArgument) {
                addValueParameter(noSuppliedTypeParameterInClassStubParameterName, irRuntimeReferences.noSuppliedTypeParameterInClassStubIrClassSymbol.defaultType).also { newValueParameter ->
                    newValueParameter.annotations += irRuntimeReferences.newSupplianceProvidedAnnotation()
//                    newValueParameter.defaultValue = DeclarationIrBuilder(pluginContext, this.symbol).run {
//                        // TODO: KT-53992
//                        irExprBody(
//                            irGetObject(irRuntimeReferences.noSuppliedTypeParameterInClassStubIrClassSymbol)
//                        )
//                    }
                }
            }
        }
    
    override fun visitClass(declaration: IrClass, data: TransformationContext): IrStatement {
        if (declaration.kind in listOf<ClassKind>(CLASS/*, ENUM_CLASS*/) && declaration.isSuppliable)
            for (constructor in declaration.constructors.toList()) {
                val newConstructor = createSupplianceFor(constructor)
                declaration.addChild(newConstructor)
                pluginContext.metadataDeclarationRegistrar.registerConstructorAsMetadataVisible(newConstructor)
                data.suppliableToSupplianceMapping[constructor] = newConstructor
                data.supplianceToSuppliableMapping[newConstructor] = constructor
            }
        return super.visitClass(declaration, data)
    }
}