/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.fir.FirLocalContextsExpressionResolutionExtension.GeneratedReceiverFromLocalContextsFunctionKey
import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import dev.lounres.kone.plugin.contexts.localUnwrapActualValueParameterName
import dev.lounres.kone.plugin.contexts.localUnwrapFakeValueParameterName
import dev.lounres.kone.plugin.contexts.localUnwrapFunctionShortName
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
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitContextParameterValue
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitExtensionReceiverValue
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.FirAbstractBodyResolveTransformer
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.util.PrivateForInline
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


class FirLocalUnwrapExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromUnwrapFunctionKey : GeneratedDeclarationKey()
    
    private val localUnwrapFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, localUnwrapFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
    @OptIn(PrivateForInline::class)
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> = context(session) {
        if (sessionHolder !is FirAbstractBodyResolveTransformer.BodyResolveTransformerComponents) return listOf()
        if (functionCall.calleeReference.resolved?.resolvedSymbol != localUnwrapFirFunctionSymbol) return emptyList()
        check(functionCall.arguments.size <= 1)
        val varargArgument = (functionCall.arguments.getOrNull(0) ?: return emptyList()) as FirVarargArgumentsExpression
        val holdersToUnwrap = varargArgument.arguments.map { it.resolvedType }
        val fakeValueParameter = buildValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromUnwrapFunctionKey.origin
            symbol = FirValueParameterSymbol()
            containingDeclarationSymbol = localUnwrapFirFunctionSymbol
            returnTypeRef = session.builtinTypes.nullableAnyType
            name = localUnwrapFakeValueParameterName
        }
        val firTypesToUnwrapProvider = FirTypesToUnwrapProvider(session)
        val newImplicitContextParameters = holdersToUnwrap.flatMap { holder ->
            firTypesToUnwrapProvider[holder].map {
                val receiverParameter = buildReceiverParameter {
                    resolvePhase = FirResolvePhase.BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = GeneratedReceiverFromUnwrapFunctionKey.origin
                    symbol = FirReceiverParameterSymbol()
                    typeRef = buildResolvedTypeRef {
                        coneType = it
                    }
                    containingDeclarationSymbol = fakeValueParameter.symbol
                }
                val valueParameterSymbol = buildValueParameter {
                    resolvePhase = FirResolvePhase.BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = GeneratedReceiverFromLocalContextsFunctionKey.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = it
                    }
                    name = localUnwrapActualValueParameterName
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = fakeValueParameter.symbol
                    valueParameterKind = ContextParameter
                }.apply { fakeReceiver = receiverParameter }
                ImplicitContextParameterValue(
                    boundSymbol = valueParameterSymbol.symbol,
                    type = it,
                )
            }
        }
        val bodyResolveContext = sessionHolder.context
        bodyResolveContext.replaceTowerDataContext(bodyResolveContext.towerDataContext.addContextGroups(newImplicitContextParameters))
        emptyList()
    }
}