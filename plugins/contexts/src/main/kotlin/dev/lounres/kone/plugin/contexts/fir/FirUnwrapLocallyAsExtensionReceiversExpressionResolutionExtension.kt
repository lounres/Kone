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
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


class FirUnwrapLocallyAsExtensionReceiversExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromUnwrapLocallyAsExtensionReceiversFunctionKey : GeneratedDeclarationKey()
    
    companion object {
        private data class ClassSuperClassesAndTypeRealisation(
            val classSymbol: FirClassSymbol<*>,
            val superClassSymbols: MutableSet<FirClassSymbol<*>>,
            val type: ConeClassLikeType
        )
        context(session: FirSession)
        private fun ConeKotlinType.superClassTypes(): Map<FirClassSymbol<*>, ClassSuperClassesAndTypeRealisation> = buildMap {
            data class TypeToCheck(
                val type: ConeKotlinType,
                val subClassSymbols: Set<FirClassSymbol<*>>,
            )
            val toCheck = ArrayDeque<TypeToCheck>()
            toCheck.addLast(TypeToCheck(this@superClassTypes, emptySet()))
            
            data class ClassMutableSuperClassesAndTypeRealisation(
                val classSymbol: FirClassSymbol<*>,
                val superClassSymbols: MutableSet<FirClassSymbol<*>>,
                val type: ConeClassLikeType
            )
            
            while (toCheck.isNotEmpty()) {
                val (type, subClassSymbols) = toCheck.removeLast()
                when (val unwrappedType = type.unwrapToSimpleTypeUsingLowerBound()) {
                    is ConeCapturedType -> (unwrappedType.constructor.supertypes ?: emptyList()).mapTo(toCheck) { TypeToCheck(it, subClassSymbols) }
                    is ConeIntegerConstantOperatorType -> {}
                    is ConeIntegerLiteralConstantType -> {}
                    is ConeIntersectionType -> unwrappedType.intersectedTypes.mapTo(toCheck) { TypeToCheck(it, subClassSymbols) }
                    is ConeLookupTagBasedType -> when (unwrappedType) {
                        is ConeTypeParameterType -> unwrappedType.lookupTag.typeParameterSymbol.resolvedBounds.mapTo(toCheck) { TypeToCheck(it.coneType, subClassSymbols) }
                        is ConeClassLikeType -> {
                            val classId = unwrappedType.lookupTag.classId
                            val classSymbol = session.symbolProvider.getClassLikeSymbolByClassId(classId) ?: continue
                            val substitutor = substitutorByMap(
                                substitution = classSymbol.typeParameterSymbols.zip(unwrappedType.typeArguments.map { it.type!! }).toMap(),
                                useSiteSession = session,
                            )
                            when(classSymbol) {
                                is FirClassSymbol -> {
                                    put(classSymbol, ClassMutableSuperClassesAndTypeRealisation(classSymbol, mutableSetOf(), unwrappedType))
                                    subClassSymbols.forEach { get(it)!!.superClassSymbols.add(classSymbol) }
                                    val subClassSymbols = subClassSymbols + classSymbol
                                    classSymbol.resolvedSuperTypes.mapTo(toCheck) { TypeToCheck(substitutor.substituteOrSelf(it), subClassSymbols) }
                                }
                                is FirTypeAliasSymbol -> toCheck.add(TypeToCheck(substitutor.substituteOrSelf(classSymbol.resolvedExpandedTypeRef.coneType), subClassSymbols))
                            }
                        }
                        else -> {}
                    }
                    is ConeStubTypeForTypeVariableInSubtyping -> {}
                    is ConeTypeVariableType -> {}
                }
            }
        }.mapValues {
            ClassSuperClassesAndTypeRealisation(
                classSymbol = it.value.classSymbol,
                superClassSymbols = it.value.superClassSymbols,
                type = it.value.type,
            )
        }
    }
    
    private val unwrapLocallyAsExtensionReceiversFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, unwrapLocallyAsExtensionReceiversFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
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
            returnTypeRef = session.builtinTypes.nullableAnyType
            name = unwrapLocallyAsExtensionReceiversFakeValueParameterName
        }
        holdersToUnwrap.flatMap { holder ->
            data class PropertyOverriddenClassSuperClassesAndType(
                val propertySymbol: FirPropertySymbol,
                val overridden: MutableSet<FirPropertySymbol>,
                val classSuperClassesAndTypeRealisation: ClassSuperClassesAndTypeRealisation
            )
            val classes = holder.superClassTypes()
            val classesProperties = classes.mapValues { (classSuperClassesAndTypeRealisation = value) ->
                classSuperClassesAndTypeRealisation.classSymbol
                    .declaredProperties(session)
                    .filter { it.receiverParameterSymbol == null && it.contextParameterSymbols.isEmpty() }
                    .associate {
                        it.name to PropertyOverriddenClassSuperClassesAndType(
                            propertySymbol = it,
                            overridden = mutableSetOf(),
                            classSuperClassesAndTypeRealisation = classSuperClassesAndTypeRealisation,
                        )
                    }
            }
            for ([classSymbol, classSuperClassesAndTypeRealisation] in classes) {
                val classPropertyOverriddenClassSuperClassesAndType = classesProperties[classSymbol]!!
                for (superClassSymbol in classSuperClassesAndTypeRealisation.superClassSymbols) {
                    for ([superClassPropertyName, superClassPropertyOverriddenClassSuperClassesAndType] in classesProperties[superClassSymbol]!!) {
                        classPropertyOverriddenClassSuperClassesAndType[superClassPropertyName]?.overridden?.add(
                            superClassPropertyOverriddenClassSuperClassesAndType.propertySymbol,
                        )
                    }
                }
            }
            val possiblePropertiesToUnwrap = classesProperties.values.flatMap { it.values }
            val topPossiblePropertiesToUnwrap = buildMap<FirPropertySymbol, PropertyOverriddenClassSuperClassesAndType> {
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
            val typesToUnwrap = propertiesToUnwrap.map { (propertySymbol, classSuperClassesAndTypeRealisation) ->
                val (classSymbol, type) = classSuperClassesAndTypeRealisation
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