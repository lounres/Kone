/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.codegen.AnnotationCodegen.Companion.annotationClass
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrPackageFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.copyAnnotations
import org.jetbrains.kotlin.ir.util.copyFunctionSignatureFrom
import org.jetbrains.kotlin.ir.visitors.IrTransformer


class FunctionSuppliancesGenerationTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<FunctionSuppliancesGenerationTransformer.TransformationContext>() {
    data class TransformationContext(
        val suppliableToSupplianceMapping: MutableMap<IrSimpleFunction, IrSimpleFunction>,
        val supplianceToSuppliableMapping: MutableMap<IrSimpleFunction, IrSimpleFunction>,
    ) {
        companion object {
            val INIT: TransformationContext = TransformationContext(
                suppliableToSupplianceMapping = mutableMapOf(),
                supplianceToSuppliableMapping = mutableMapOf(),
            )
        }
    }
    
    private fun createSupplianceFor(function: IrSimpleFunction): IrSimpleFunction =
        pluginContext.irFactory.buildFun {
            name = function.name
            updateFrom(function)
        }.apply {
            annotations += function.copyAnnotations { it.annotationClass.symbol != irRuntimeReferences.suppliableAnnotationIrClassSymbol }
            annotations += irRuntimeReferences.newSupplianceProvidedAnnotation()
            copyFunctionSignatureFrom(function)
            for (typeParameter in function.typeParameters) {
                if (!typeParameter.isSupply) continue
                addValueParameter(internalSupplierParameterName(typeParameter.name), irRuntimeReferences.suppliedTypeIrType).also { newValueParameter ->
                    newValueParameter.annotations += irRuntimeReferences.newSupplianceProvidedAnnotation()
//                    newValueParameter.defaultValue = DeclarationIrBuilder(pluginContext, this.symbol).run {
//                        // TODO: KT-53992
//                        irExprBody(
//                            irCall(irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol).also { call ->
//                                call.typeArguments.add(typeParameter.defaultType)
//                            }
//                        )
//                    }
                }
            }
        }
    
//    override fun visitBody(body: IrBody, data: TransformationContext): IrBody {
//        var createdNewDeclaration = false
//        val newDeclarations = buildList {
//            for (statement in body.statements) {
//                add(statement)
//                if (statement is IrSimpleFunction && statement.isSuppliable && statement) {
//                    createdNewDeclaration = true
//                    add(
//                        pluginContext.irFactory.buildFun {
//                            updateFrom(statement)
//                        }.apply {
//                            typeParameters = statement.typeParameters.map { parameter ->
//                                parameter.deepCopyWithoutPatchingParents().apply {
//                                    parent = statement
//                                }
//                            }
//                            parameters = statement.parameters.map { parameter ->
//                                parameter.deepCopyWithoutPatchingParents().apply {
//                                    parent = statement
//                                }
//                            }
//                            val
//                            returnType = statement.returnType
//                        }
//                    )
//                }
//            }
//        }
//        return super.visitBody(body, data)
//    }
    
//    override fun visitContainerExpression(
//        expression: IrContainerExpression,
//        data: TransformationContext
//    ): IrExpression {
//        return super.visitContainerExpression(expression, data)
//    }
    
    override fun visitClass(declaration: IrClass, data: TransformationContext): IrStatement {
        for (subdeclaration in declaration.declarations.toList()) {
            if (subdeclaration is IrSimpleFunction && subdeclaration.isSuppliable && subdeclaration.typeParameters.any { it.isSupply }) {
                val newSubdeclaration = createSupplianceFor(subdeclaration)
                declaration.addChild(newSubdeclaration)
                pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(newSubdeclaration)
                data.suppliableToSupplianceMapping[subdeclaration] = newSubdeclaration
                data.supplianceToSuppliableMapping[newSubdeclaration] = subdeclaration
            }
        }
        return super.visitClass(declaration, data)
    }
    
    override fun visitPackageFragment(declaration: IrPackageFragment, data: TransformationContext): IrElement {
        for (subdeclaration in declaration.declarations.toList()) {
            if (subdeclaration is IrSimpleFunction && subdeclaration.isSuppliable && subdeclaration.typeParameters.any { it.isSupply }) {
                val newSubdeclaration = createSupplianceFor(subdeclaration)
                declaration.addChild(newSubdeclaration)
                pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(newSubdeclaration)
                data.suppliableToSupplianceMapping[subdeclaration] = newSubdeclaration
                data.supplianceToSuppliableMapping[newSubdeclaration] = subdeclaration
            }
        }
        return super.visitPackageFragment(declaration, data)
    }
    
    override fun visitFile(declaration: IrFile, data: TransformationContext): IrFile {
        for (subdeclaration in declaration.declarations.toList()) {
            if (subdeclaration is IrSimpleFunction && subdeclaration.isSuppliable && subdeclaration.typeParameters.any { it.isSupply }) {
                val newSubdeclaration = createSupplianceFor(subdeclaration)
                declaration.addChild(newSubdeclaration)
                pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(newSubdeclaration)
                data.suppliableToSupplianceMapping[subdeclaration] = newSubdeclaration
                data.supplianceToSuppliableMapping[newSubdeclaration] = subdeclaration
            }
        }
        return super.visitFile(declaration, data)
    }
}