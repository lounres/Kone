/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import dev.lounres.kone.plugin.contexts.useLocallyAsExtensionReceiversFakeValueParameterName
import dev.lounres.kone.plugin.contexts.useLocallyAsExtensionReceiversFunctionShortName
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
import org.jetbrains.kotlin.fir.extensions.captureValueInAnalyze
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


class FirUseLocallyAsExtensionReceiversExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromUseLocallyAsExtensionReceiversFunctionKey : GeneratedDeclarationKey()
    
    private val useLocallyAsExtensionReceiversFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, useLocallyAsExtensionReceiversFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> {
        if (functionCall.calleeReference.resolved?.resolvedSymbol != useLocallyAsExtensionReceiversFirFunctionSymbol) return emptyList()
        val contextsToUse = (functionCall.arguments.single() as FirVarargArgumentsExpression).arguments.map { it.resolvedType }
        val fakeValueParam = buildValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromUseLocallyAsExtensionReceiversFunctionKey.origin
            symbol = FirValueParameterSymbol()
            containingDeclarationSymbol = useLocallyAsExtensionReceiversFirFunctionSymbol
            returnTypeRef = session.builtinTypes.anyType
            name = useLocallyAsExtensionReceiversFakeValueParameterName
        }
        return contextsToUse.map {
            val receiverParameter = buildReceiverParameter {
                resolvePhase = FirResolvePhase.BODY_RESOLVE
                moduleData = session.moduleData
                origin = GeneratedReceiverFromUseLocallyAsExtensionReceiversFunctionKey.origin
                symbol = FirReceiverParameterSymbol()
                containingDeclarationSymbol = fakeValueParam.symbol
                typeRef = buildResolvedTypeRef {
                    coneType = it
                }
            }
            receiverParameter.captureValueInAnalyze = true
            ImplicitExtensionReceiverValue(
                boundSymbol = receiverParameter.symbol,
                type = it,
                useSiteSession = sessionHolder.session,
                scopeSession = sessionHolder.scopeSession
            )
        }
    }
}