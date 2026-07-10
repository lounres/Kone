/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.koneContextHolderContextAnnotationClassId
import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import dev.lounres.kone.plugin.contexts.unwrapLocallyAsExtensionReceiversFakeValueParameterName
import dev.lounres.kone.plugin.contexts.unwrapLocallyAsExtensionReceiversFunctionShortName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
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
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.scopes.ScopeFunctionRequiresPrewarm
import org.jetbrains.kotlin.fir.scopes.getDirectOverriddenProperties
import org.jetbrains.kotlin.fir.scopes.unsubstitutedScope
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


class FirUnwrapLocallyAsExtensionReceiversExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromUnwrapLocallyAsExtensionReceiversFunctionKey : GeneratedDeclarationKey()
    
    companion object {
        private data class ClassWithTypeRealisation(
            val classSymbol: FirRegularClassSymbol,
            val type: ConeClassLikeType
        )
        context(session: FirSession)
        private fun ConeKotlinType.superClassTypes(): Map<ClassId, ClassWithTypeRealisation> = buildMap {
            val toCheck = ArrayDeque<ConeKotlinType>()
            toCheck.addLast(this@superClassTypes)
            
            while (toCheck.isNotEmpty()) {
                val type = toCheck.removeFirst().unwrapToSimpleTypeUsingLowerBound()
                when (type) {
                    is ConeCapturedType -> toCheck.addAll(type.constructor.supertypes ?: emptyList())
                    is ConeIntegerConstantOperatorType -> {}
                    is ConeIntegerLiteralConstantType -> {}
                    is ConeIntersectionType -> toCheck.addAll(type.intersectedTypes)
                    is ConeLookupTagBasedType -> when (type) {
                        is ConeTypeParameterType -> type.lookupTag.typeParameterSymbol.resolvedBounds.mapTo(toCheck) { it.coneType }
                        is ConeClassLikeType -> {
                            val classId = type.lookupTag.classId
                            val classSymbol = session.symbolProvider.getClassLikeSymbolByClassId(classId) ?: continue
                            if (classSymbol is FirRegularClassSymbol) put(classId, ClassWithTypeRealisation(classSymbol, type))
                            val substitutor = substitutorByMap(
                                substitution = classSymbol.typeParameterSymbols.zip(type.typeArguments.map { it.type!! }).toMap(),
                                useSiteSession = session,
                            )
                            when(classSymbol) {
                                is FirClassSymbol -> classSymbol.resolvedSuperTypes.mapTo(toCheck) { substitutor.substituteOrSelf(it) }
                                is FirTypeAliasSymbol -> toCheck.add(substitutor.substituteOrSelf(classSymbol.resolvedExpandedTypeRef.coneType))
                            }
                        }
                        else -> {}
                    }
                    is ConeStubTypeForTypeVariableInSubtyping -> {}
                    is ConeTypeVariableType -> {}
                }
            }
        }
    }
    
    private val unwrapLocallyAsExtensionReceiversFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, unwrapLocallyAsExtensionReceiversFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
    @OptIn(ScopeFunctionRequiresPrewarm::class)
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> = context(session) {
        if (functionCall.calleeReference.resolved?.resolvedSymbol != unwrapLocallyAsExtensionReceiversFirFunctionSymbol) return emptyList()
        val holdersToUnwrap = (functionCall.arguments.single() as FirVarargArgumentsExpression).arguments.map { it.resolvedType.unwrapToSimpleTypeUsingLowerBound() }
        val fakeValueParameter = buildValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromUnwrapLocallyAsExtensionReceiversFunctionKey.origin
            symbol = FirValueParameterSymbol()
            containingDeclarationSymbol = unwrapLocallyAsExtensionReceiversFirFunctionSymbol
            returnTypeRef = session.builtinTypes.anyType
            name = unwrapLocallyAsExtensionReceiversFakeValueParameterName
        }
        holdersToUnwrap.flatMap { holder ->
            data class PropertyOverriddenClassAndType(
                val propertySymbol: FirPropertySymbol,
                val overridden: List<FirPropertySymbol>,
                val classWithTypeRealisation: ClassWithTypeRealisation
            )
            val possiblePropertiesToUnwrap = buildList {
                holder.superClassTypes().values.forEach { classWithTypeRealisation ->
                    val classSymbol = classWithTypeRealisation.classSymbol
                    classSymbol.declaredProperties(session).forEach {
                        if (it.receiverParameterSymbol != null || it.contextParameterSymbols.isNotEmpty()) return@forEach
                        val overridden = classSymbol.unsubstitutedScope(
                            sessionHolder.session,
                            sessionHolder.scopeSession,
                            withForcedTypeCalculator = true,
                            memberRequiredPhase = FirResolvePhase.STATUS,
                        ).getDirectOverriddenProperties(it)
                        add(PropertyOverriddenClassAndType(it, overridden, classWithTypeRealisation))
                    }
                }
            }
            val topPossiblePropertiesToUnwrap = buildMap<FirPropertySymbol, PropertyOverriddenClassAndType> {
                possiblePropertiesToUnwrap.forEach {
                    put(it.propertySymbol, it)
                }
                possiblePropertiesToUnwrap.forEach {
                    it.overridden.forEach { override ->
                        remove(override)
                    }
                }
            }
            val propertiesToUnwrap = topPossiblePropertiesToUnwrap.values
                .groupBy { it.propertySymbol.name }
                .filter { it.value.size == 1 }
                .values
                .map { it.single() }
                .filter {
                    it.propertySymbol.hasAnnotation(koneContextHolderContextAnnotationClassId, session)
                            || it.overridden.any { override -> override.hasAnnotation(koneContextHolderContextAnnotationClassId, session) }
                }
            val typesToUnwrap = propertiesToUnwrap.map { (propertySymbol, classWithTypeRealisation) ->
                val (classSymbol, type) = classWithTypeRealisation
                val substitutor by lazy(LazyThreadSafetyMode.NONE) {
                    substitutorByMap(
                        substitution = classSymbol.typeParameterSymbols.zip(type.typeArguments.map { it.type!! }).toMap(),
                        useSiteSession = session,
                    )
                }
                substitutor.substituteOrSelf(propertySymbol.resolvedReturnType)
            }
            typesToUnwrap.map {
                val receiverParameter = buildReceiverParameter {
                    resolvePhase = FirResolvePhase.BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = GeneratedReceiverFromUnwrapLocallyAsExtensionReceiversFunctionKey.origin
                    symbol = FirReceiverParameterSymbol()
                    containingDeclarationSymbol = fakeValueParameter.symbol
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
}