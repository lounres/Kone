/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrValueAccessExpression
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid


class ParametersSubstitutionTransformer(
    private val typeParametersSubstitution: Map<IrTypeParameter, IrTypeParameter> = emptyMap(),
    private val valueParametersSubstitution: Map<IrValueParameter, IrValueParameter> = emptyMap(),
) : IrElementTransformerVoid() {
    private val typeParametersToNewTypesSubstitution =
        typeParametersSubstitution.mapValues { it.value.defaultType }.mapKeys { it.key.symbol }
    private val valueParameterSymbolsSubstitution =
        valueParametersSubstitution.mapValues { it.value.symbol }.mapKeys { it.key.symbol }
    
//    override fun visitTypeParameter(declaration: IrTypeParameter): IrStatement =
//        typeParametersSubstitution[declaration] ?: declaration
    override fun visitValueParameter(declaration: IrValueParameter): IrValueParameter =
        valueParametersSubstitution[declaration] ?: declaration
    
    override fun visitValueAccess(expression: IrValueAccessExpression): IrExpression {
        expression.symbol = valueParameterSymbolsSubstitution[expression.symbol] ?: expression.symbol
        
        return super.visitValueAccess(expression)
    }
    
    override fun visitMemberAccess(expression: IrMemberAccessExpression<*>): IrExpression {
        for (i in expression.typeArguments.indices)
            expression.typeArguments[i] = expression.typeArguments[i]?.substitute(typeParametersToNewTypesSubstitution)
        
        return super.visitMemberAccess(expression)
    }
}