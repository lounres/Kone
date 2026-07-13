/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.fir.FirUnwrapLocallyAsExtensionReceiversExpressionResolutionExtension.GeneratedReceiverFromUnwrapLocallyAsExtensionReceiversFunctionKey
import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import dev.lounres.kone.plugin.contexts.useLocallyAsContextsActualValueParameterName
import dev.lounres.kone.plugin.contexts.useLocallyAsContextsFakeValueParameterName
import dev.lounres.kone.plugin.contexts.useLocallyAsContextsFunctionShortName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.extensions.FirExpressionResolutionExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitContextParameterValue
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitExtensionReceiverValue
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.FirAbstractBodyResolveTransformer
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.util.PrivateForInline
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


class FirUseLocallyAsContextsExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromUseLocallyAsContextsFunctionKey : GeneratedDeclarationKey()
    
    private val useLocallyAsContextsFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, useLocallyAsContextsFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
    @OptIn(PrivateForInline::class)
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> {
        if (functionCall.calleeReference.resolved?.resolvedSymbol != useLocallyAsContextsFirFunctionSymbol) return emptyList()
        val contextsToUse = (functionCall.arguments.single() as FirVarargArgumentsExpression).arguments.map { it.resolvedType }
        val fakeValueParameter = buildValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromUseLocallyAsContextsFunctionKey.origin
            symbol = FirValueParameterSymbol()
            containingDeclarationSymbol = useLocallyAsContextsFirFunctionSymbol
            returnTypeRef = session.builtinTypes.anyType
            name = useLocallyAsContextsFakeValueParameterName
        }
        val newImplicitContextParameters = contextsToUse.map {
            val valueParameterSymbol = buildValueParameter {
                resolvePhase = FirResolvePhase.BODY_RESOLVE
                moduleData = session.moduleData
                origin = GeneratedReceiverFromUnwrapLocallyAsExtensionReceiversFunctionKey.origin
                returnTypeRef = buildResolvedTypeRef {
                    coneType = it
                }
                name = useLocallyAsContextsActualValueParameterName
                symbol = FirValueParameterSymbol()
                containingDeclarationSymbol = fakeValueParameter.symbol
                valueParameterKind = ContextParameter
            }
            ImplicitContextParameterValue(
                boundSymbol = valueParameterSymbol.symbol,
                type = it,
            )
        }
        sessionHolder as FirAbstractBodyResolveTransformer.BodyResolveTransformerComponents
        val bodyResolveContext = sessionHolder.context
        bodyResolveContext.replaceTowerDataContext(bodyResolveContext.towerDataContext.addContextGroups(newImplicitContextParameters))
        return emptyList()
    }
}