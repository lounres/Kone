/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import dev.lounres.kone.plugin.contexts.localContextsActualValueParameterName
import dev.lounres.kone.plugin.contexts.localContextsFakeValueParameterName
import dev.lounres.kone.plugin.contexts.localContextsFunctionShortName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
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
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.util.PrivateForInline
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


class FirLocalContextsExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromLocalContextsFunctionKey : GeneratedDeclarationKey()
    
    private val localContextsFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, localContextsFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
    @OptIn(PrivateForInline::class)
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> {
        if (sessionHolder !is FirAbstractBodyResolveTransformer.BodyResolveTransformerComponents) return emptyList()
        if (functionCall.calleeReference.resolved?.resolvedSymbol != localContextsFirFunctionSymbol) return emptyList()
        if (functionCall.arguments.size != 1) return emptyList()
        val varargArgument = functionCall.arguments[0] as FirVarargArgumentsExpression
        val fakeValueProperty = buildProperty {
            val theSymbol = FirRegularPropertySymbol(CallableId(koneContextsPackageFQName, localContextsFakeValueParameterName))
            
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromLocalContextsFunctionKey.origin
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
                origin = GeneratedReceiverFromLocalContextsFunctionKey.origin
                symbol = FirReceiverParameterSymbol()
                typeRef = session.builtinTypes.nullableAnyType
                containingDeclarationSymbol = theSymbol
            }
            name = localContextsFakeValueParameterName
            isVar = false
            symbol = theSymbol
        }
        val newImplicitContextParameters = varargArgument.arguments.map { holder ->
            val type = holder.resolvedType
            val receiverParameter = buildReceiverParameter {
                source = holder.source
                resolvePhase = FirResolvePhase.BODY_RESOLVE
                moduleData = session.moduleData
                origin = GeneratedReceiverFromLocalContextsFunctionKey.origin
                symbol = FirReceiverParameterSymbol()
                containingDeclarationSymbol = fakeValueProperty.symbol
                typeRef = buildResolvedTypeRef {
                    coneType = type
                }
            }
            val valueParameterSymbol = buildValueParameter {
                source = holder.source
                resolvePhase = FirResolvePhase.BODY_RESOLVE
                moduleData = session.moduleData
                origin = GeneratedReceiverFromLocalContextsFunctionKey.origin
                returnTypeRef = buildResolvedTypeRef {
                    coneType = type
                }
                name = localContextsActualValueParameterName
                symbol = FirValueParameterSymbol()
                containingDeclarationSymbol = fakeValueProperty.symbol
                valueParameterKind = ContextParameter
            }.apply { fakeReceiver = receiverParameter }
            ImplicitContextParameterValue(
                boundSymbol = valueParameterSymbol.symbol,
                type = type,
            )
        }
        val bodyResolveContext = sessionHolder.context
        bodyResolveContext.replaceTowerDataContext(bodyResolveContext.towerDataContext.addContextGroups(newImplicitContextParameters))
        return emptyList()
    }
}