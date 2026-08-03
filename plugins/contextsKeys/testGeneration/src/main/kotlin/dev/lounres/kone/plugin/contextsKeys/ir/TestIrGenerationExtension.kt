/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.ir

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.util.copyFunctionSignatureFrom
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name


@OptIn(UnsafeDuringIrConstructionAPI::class)
class TestIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val additionalValueParameterName = Name.identifier("_ignore_me_")
        
        // Duplicates any top-level function adding another value parameter
        moduleFragment.transform(
            transformer = object : IrElementTransformerVoid() {
                override fun visitFile(declaration: IrFile): IrFile {
                    for (subdeclaration in declaration.declarations.toList()) {
                        if (subdeclaration !is IrSimpleFunction) continue
                        val newSubdeclaration = pluginContext.irFactory.buildFun {
                            name = subdeclaration.name
                            updateFrom(subdeclaration)
                        }.apply {
                            copyFunctionSignatureFrom(subdeclaration)
                            val newValueParameter = addValueParameter(additionalValueParameterName, pluginContext.irBuiltIns.nothingType)
                            body = DeclarationIrBuilder(pluginContext, this.symbol).run {
                                irBlockBody {
                                    +irReturn(irGet(newValueParameter))
                                }
                            }
                        }
                        declaration.addChild(newSubdeclaration)
                        pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(newSubdeclaration)
                    }
                    return super.visitFile(declaration)
                }
            },
            data = null
        )
        
        // Substitutes any function call to duplicate function call if there is an exactly one duplicate
        moduleFragment.transform(
            transformer = object : IrElementTransformerVoidWithContext() {
                override fun visitCall(expression: IrCall): IrExpression {
                    val callee = expression.symbol.owner
                    val parameters = callee.parameters
                    val typeParameters = callee.typeParameters
                    val substituteCallee = pluginContext.finderForBuiltins().findFunctions(callee.callableId).filter {
                        val function = it.owner
                        if (function.parameters.lastOrNull()?.name != additionalValueParameterName) return@filter false
                        val functionParameters = function.parameters.dropLast(1)
                        val functionTypeParameters = function.typeParameters
                        if (parameters.size != functionParameters.size || typeParameters.size != functionTypeParameters.size) return@filter false
                        val typeParametersSubstitution = typeParameters.map { it.symbol }.zip(functionTypeParameters.map { it.defaultType }).toMap()
                        for (index in parameters.indices) {
                            val parameter = parameters[index]
                            val otherParameter = functionParameters[index]
                            if (parameter.kind != otherParameter.kind || parameter.type.substitute(typeParametersSubstitution) != otherParameter.type) return@filter false
                        }
                        true
                    }
                    when {
                        substituteCallee.size == 0 -> {}
                        substituteCallee.size == 1 -> {
                            expression.symbol = substituteCallee.single()
                            // The following will be a plugin bug, not a compiler one. Just Ignore it.
                            expression.arguments.add(57.toIrConst(pluginContext.irBuiltIns.intType))
                        }
                        else -> {
                            error("Received several suppliances of function.\n  CallableId: ${callee.callableId}\n  Substitutee: ${callee.symbol}\n  Substitute candidates:${substituteCallee.joinToString(separator = "") { "\n    $it" }}")
                        }
                    }
                    return super.visitCall(expression)
                }
            },
            data = null
        )
    }
}