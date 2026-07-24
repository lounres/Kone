/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.fir.FirLocalContextsExpressionResolutionExtension.GeneratedReceiverFromLocalContextsFunctionKey
import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import dev.lounres.kone.plugin.contexts.koneLocalUnwrapActualValueParameterName
import dev.lounres.kone.plugin.contexts.koneLocalUnwrapFakeValueParameterName
import dev.lounres.kone.plugin.contexts.koneLocalUnwrapFunctionShortName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
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
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.util.PrivateForInline


class FirKoneLocalUnwrapExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromKoneLocalUnwrapFunctionKey : GeneratedDeclarationKey()
    
    private val koneLocalUnwrapFirFunctionSymbols by lazy {
        session.symbolProvider.getTopLevelFunctionSymbols(koneContextsPackageFQName, koneLocalUnwrapFunctionShortName)
    }
    
    @OptIn(PrivateForInline::class)
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> = context(session) {
        if (sessionHolder !is FirAbstractBodyResolveTransformer.BodyResolveTransformerComponents) return emptyList()
        if (functionCall.calleeReference.resolved?.resolvedSymbol !in koneLocalUnwrapFirFunctionSymbols) return emptyList()
        if (functionCall.arguments.size != 1) return emptyList()
        val varargArgument = functionCall.arguments[0] as FirVarargArgumentsExpression
        val fakeValueProperty = buildProperty {
            val theSymbol = FirRegularPropertySymbol(CallableId(koneContextsPackageFQName, koneLocalUnwrapFakeValueParameterName))
            
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromKoneLocalUnwrapFunctionKey.origin
            status = FirResolvedDeclarationStatusImpl(
                Visibilities.DEFAULT_VISIBILITY,
                Modality.FINAL,
                EffectiveVisibility.Public,
            )
            isLocal = true
            returnTypeRef = session.builtinTypes.nullableAnyType
            receiverParameter = buildReceiverParameter {
                resolvePhase = FirResolvePhase.BODY_RESOLVE
                moduleData = session.moduleData
                origin = GeneratedReceiverFromKoneLocalUnwrapFunctionKey.origin
                symbol = FirReceiverParameterSymbol()
                typeRef = session.builtinTypes.nullableAnyType
                containingDeclarationSymbol = theSymbol
            }
            name = koneLocalUnwrapFakeValueParameterName
            isVar = false
            symbol = theSymbol
        }
        val firTypesToUnwrapProvider = FirTypesToUnwrapProvider(session)
        val newImplicitContextParameters = varargArgument.arguments.flatMap { holder ->
            val type = holder.resolvedType.registryKeyTypeArgument() ?: return@flatMap emptyList()
            firTypesToUnwrapProvider[type].map {
                val receiverParameter = buildReceiverParameter {
                    source = holder.source
                    resolvePhase = FirResolvePhase.BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = GeneratedReceiverFromKoneLocalUnwrapFunctionKey.origin
                    symbol = FirReceiverParameterSymbol()
                    typeRef = buildResolvedTypeRef {
                        coneType = it
                    }
                    containingDeclarationSymbol = fakeValueProperty.symbol
                }
                val valueParameterSymbol = buildValueParameter {
                    source = holder.source
                    resolvePhase = FirResolvePhase.BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = GeneratedReceiverFromLocalContextsFunctionKey.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = it
                    }
                    name = koneLocalUnwrapActualValueParameterName
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = fakeValueProperty.symbol
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