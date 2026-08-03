/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.ir

import dev.lounres.kone.plugin.contextsKeys.generatedContextKeyName
import dev.lounres.kone.plugin.suppliedTypes.ir.IrRuntimeReferences as SuppliedTypesIrRuntimeReferences
import dev.lounres.kone.scope
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.visitors.IrTransformer


class ContextsKeysConstructorsFillingIrTransformer(
    private val pluginContext: IrPluginContext,
    private val suppliedTypesIrRuntimeReferences: SuppliedTypesIrRuntimeReferences,
    private val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<Nothing?>() {
    override fun visitConstructor(declaration: IrConstructor, data: Nothing?): IrStatement {
        scope {
            if (!declaration.isPrimary) return@scope
            val constructedClass = declaration.constructedClass
            if (constructedClass.name != generatedContextKeyName) return@scope
            val containingClass = constructedClass.parent as? IrClass ?: return@scope
            if (!containingClass.isGenerateKey) return@scope
            check(declaration.body == null) { TODO() }
            declaration.typeParameters = emptyList()
            val containingClassTypeParameters = containingClass.typeParameters
            declaration.body = DeclarationIrBuilder(pluginContext, declaration.symbol).run {
                irBlockBody {
                    +irDelegatingConstructorCall(irRuntimeReferences.suppliedTypesRegistryKeyPrimaryConstructorIrConstructor).apply {
                        typeArguments[0] = containingClass.typeWith(containingClassTypeParameters.map { it.defaultType })
                    }
                    +IrInstanceInitializerCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        classSymbol = constructedClass.symbol,
                        type = pluginContext.irBuiltIns.unitType,
                    )
                }
            }
        }
        return super.visitConstructor(declaration, data)
    }
}