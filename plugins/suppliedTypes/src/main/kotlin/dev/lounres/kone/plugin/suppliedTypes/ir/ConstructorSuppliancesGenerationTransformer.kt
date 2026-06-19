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
import org.jetbrains.kotlin.ir.expressions.impl.IrAnnotationImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrTransformer


class ConstructorSuppliancesGenerationTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<Nothing?>() {
    private fun createSupplianceFor(constructor: IrConstructor): IrConstructor =
        pluginContext.irFactory.buildConstructor {
            updateFrom(constructor)
            isPrimary = false
        }.apply {
            annotations += IrAnnotationImpl.fromSymbolOwner(
                irRuntimeReferences.supplianceProvidedAnnotationIrClassSymbol.defaultType,
                irRuntimeReferences.supplianceProvidedAnnotationIrClassSymbol.constructors.single(),
            )
            copyFunctionSignatureFrom(constructor)
            var addFalseSuppliedArgument = true
            for (typeParameter in constructor.constructedClass.typeParameters) {
                if (!typeParameter.isSupply) continue
                addFalseSuppliedArgument = false
                addValueParameter(internalSupplierParameterName(typeParameter.name), irRuntimeReferences.suppliedTypeIrType).also { newValueParameter ->
                    newValueParameter.annotations += IrAnnotationImpl.fromSymbolOwner(
                        irRuntimeReferences.supplianceProvidedAnnotationIrClassSymbol.defaultType,
                        irRuntimeReferences.supplianceProvidedAnnotationIrClassSymbol.constructors.single(),
                    )
//                    newValueParameter.defaultValue = DeclarationIrBuilder(pluginContext, this.symbol).run {
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
                    newValueParameter.annotations += IrAnnotationImpl.fromSymbolOwner(
                        irRuntimeReferences.supplianceProvidedAnnotationIrClassSymbol.defaultType,
                        irRuntimeReferences.supplianceProvidedAnnotationIrClassSymbol.constructors.single(),
                    )
//                    newValueParameter.defaultValue = DeclarationIrBuilder(pluginContext, this.symbol).run {
//                        irExprBody(
//                            irGetObject(irRuntimeReferences.noSuppliedTypeParameterInClassStubIrClassSymbol)
//                        )
//                    }
                }
            }
        }
    
    override fun visitClass(declaration: IrClass, data: Nothing?): IrStatement {
        if (declaration.kind in listOf<ClassKind>(CLASS/*, ENUM_CLASS*/) && declaration.isSuppliable)
            for (constructor in declaration.constructors.toList()) {
                val newConstructor = createSupplianceFor(constructor)
                declaration.addChild(newConstructor)
                pluginContext.metadataDeclarationRegistrar.registerConstructorAsMetadataVisible(newConstructor)
            }
        return super.visitClass(declaration, data)
    }
}