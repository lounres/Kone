/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.ir

import dev.lounres.kone.plugin.contexts.registryKeyClassId
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrScriptSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.classIdWhenAvailable
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.types.Variance


fun IrType.registryKeyTypeArgument(): IrType? {
    val typesToCheck = ArrayDeque<IrType>()
    typesToCheck.addLast(this)
    
    while (typesToCheck.isNotEmpty()) {
        val type = typesToCheck.removeLast()
        when (type) {
            is IrDynamicType -> {}
            is IrErrorType -> {}
            is IrSimpleType -> when (val classSymbol = type.classifier) {
                is IrScriptSymbol -> {}
                is IrTypeParameterSymbol -> typesToCheck += classSymbol.owner.superTypes
                is IrClassSymbol -> {
                    if (classSymbol.classIdWhenAvailable == registryKeyClassId)
                        return when (val typeArgument = type.arguments.singleOrNull()) {
                            is IrStarProjection -> null
                            is IrTypeProjection -> when (typeArgument.variance) {
                                Variance.INVARIANT -> typeArgument.type
                                Variance.IN_VARIANCE -> null
                                Variance.OUT_VARIANCE -> typeArgument.type
                            }
                            null -> null
                        }
                    val substitutionMap = classSymbol.owner.typeParameters.map { it.symbol }.zip(type.arguments.map { it.typeOrNull ?: contextsIrPluginException() }).toMap()
                    classSymbol.owner.superTypes.mapTo(typesToCheck) { it.substitute(substitutionMap) }
                }
            }
        }
    }
    
    return null
}