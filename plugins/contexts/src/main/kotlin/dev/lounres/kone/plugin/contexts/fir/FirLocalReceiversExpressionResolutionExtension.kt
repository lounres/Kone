/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import dev.lounres.kone.plugin.contexts.localReceiversFakeValueParameterName
import dev.lounres.kone.plugin.contexts.localReceiversFunctionShortName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.extensions.FirExpressionResolutionExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitExtensionReceiverValue
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


class FirLocalReceiversExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromLocalReceiversFunctionKey : GeneratedDeclarationKey()
    
    private val localReceiversFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, localReceiversFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> {
        if (functionCall.calleeReference.resolved?.resolvedSymbol != localReceiversFirFunctionSymbol) return emptyList()
        if (functionCall.arguments.size != 1) return emptyList()
        val varargArgument = functionCall.arguments[0] as FirVarargArgumentsExpression
        val contextsToUse = varargArgument.arguments.map { it.resolvedType }
        val fakeValueParameter = buildValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromLocalReceiversFunctionKey.origin
            symbol = FirValueParameterSymbol()
            containingDeclarationSymbol = localReceiversFirFunctionSymbol
            returnTypeRef = session.builtinTypes.anyType
            name = localReceiversFakeValueParameterName
        }
        return contextsToUse.map {
            val receiverParameter = buildReceiverParameter {
                resolvePhase = FirResolvePhase.BODY_RESOLVE
                moduleData = session.moduleData
                origin = GeneratedReceiverFromLocalReceiversFunctionKey.origin
                symbol = FirReceiverParameterSymbol()
                containingDeclarationSymbol = fakeValueParameter.symbol
                typeRef = buildResolvedTypeRef {
                    coneType = it
                }
            }
            ImplicitExtensionReceiverValue(
                boundSymbol = receiverParameter.symbol,
                type = it,
                useSiteSession = sessionHolder.session,
                scopeSession = sessionHolder.scopeSession
            )
        }
    }
}