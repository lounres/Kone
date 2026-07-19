/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.*
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
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitExtensionReceiverValue
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


class FirUnwrapExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
    data object GeneratedReceiverFromUnwrapFunctionKey : GeneratedDeclarationKey()
    
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
        private data class PropertyOverriddenClassSuperClassesAndType(
            val propertySymbol: FirPropertySymbol,
            val overridden: Set<FirPropertySymbol>,
            val classSuperClassesAndTypeRealisation: ClassSuperClassesAndTypeRealisation
        )
        context(session: FirSession)
        private fun ConeKotlinType.allProperties(): Map<FirClassSymbol<*>, Map<Name, PropertyOverriddenClassSuperClassesAndType>> {
            val classes = superClassTypes()
            
            data class PropertyMutableOverriddenClassSuperClassesAndType(
                val propertySymbol: FirPropertySymbol,
                val overridden: MutableSet<FirPropertySymbol>,
                val classSuperClassesAndTypeRealisation: ClassSuperClassesAndTypeRealisation
            )
            
            val classesProperties = classes.mapValues { (classSuperClassesAndTypeRealisation = value) ->
                classSuperClassesAndTypeRealisation.classSymbol
                    .declaredProperties(session)
                    .filter { it.receiverParameterSymbol == null && it.contextParameterSymbols.isEmpty() }
                    .associate {
                        it.name to PropertyMutableOverriddenClassSuperClassesAndType(
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
            return classesProperties.mapValues {
                it.value.mapValues { (combination = value) ->
                    PropertyOverriddenClassSuperClassesAndType(
                        propertySymbol = combination.propertySymbol,
                        overridden = combination.overridden,
                        classSuperClassesAndTypeRealisation = combination.classSuperClassesAndTypeRealisation,
                    )
                }
            }
        }
        context(session: FirSession)
        private fun FirPropertySymbol.isInclude() = hasAnnotation(koneContextHolderIncludeAnnotationClassId, session)
        context(session: FirSession)
        private fun FirPropertySymbol.isExclude() = hasAnnotation(koneContextHolderExcludeAnnotationClassId, session)
    }
    
    private val unwrapFirFunctionSymbol by lazy {
        session.symbolProvider
            .getTopLevelFunctionSymbols(koneContextsPackageFQName, unwrapFunctionShortName)
            .firstIsInstance<FirFunctionSymbol<*>>()
    }
    
    override fun addNewImplicitReceivers(
        functionCall: FirFunctionCall,
        sessionHolder: SessionAndScopeSessionHolder,
        containingCallableSymbol: FirBasedSymbol<*>,
    ): List<ImplicitExtensionReceiverValue> = context(session) {
        if (functionCall.calleeReference.resolved?.resolvedSymbol != unwrapFirFunctionSymbol) return emptyList()
        val holdersToUnwrap = (functionCall.arguments.single() as FirVarargArgumentsExpression).arguments.map { it.resolvedType.unwrapToSimpleTypeUsingLowerBound() }
        val fakeValueParameter = buildValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            origin = GeneratedReceiverFromUnwrapFunctionKey.origin
            symbol = FirValueParameterSymbol()
            containingDeclarationSymbol = unwrapFirFunctionSymbol
            returnTypeRef = session.builtinTypes.nullableAnyType
            name = unwrapFakeValueParameterName
        }
        holdersToUnwrap.flatMap { holder ->
            val classesProperties = holder.allProperties()
            val possiblePropertiesToUnwrap = classesProperties.values.flatMap { it.values }
            val decidingProperties = buildSet<FirPropertySymbol> {
                val markedProperties = possiblePropertiesToUnwrap.filter { it.propertySymbol.isInclude() || it.propertySymbol.isExclude() }
                markedProperties.forEach { add(it.propertySymbol) }
                markedProperties.forEach { it.overridden.forEach { override -> remove(override) } }
            }
            val topPossiblePropertiesToUnwrap = buildMap<FirPropertySymbol, PropertyOverriddenClassSuperClassesAndType> {
                possiblePropertiesToUnwrap.forEach { put(it.propertySymbol, it) }
                possiblePropertiesToUnwrap.forEach { it.overridden.forEach { override -> remove(override) } }
            }
            val propertiesToUnwrap = topPossiblePropertiesToUnwrap.values
                .groupBy { it.propertySymbol.name }
                .filter { it.value.size == 1 }
                .values
                .map { it.single() }
                .filter { (it.overridden + it.propertySymbol).any { it in decidingProperties && it.isInclude() } }
            val typesToUnwrap = propertiesToUnwrap.map { (propertySymbol, classSuperClassesAndTypeRealisation) ->
                val (classSymbol, type) = classSuperClassesAndTypeRealisation
                val substitutor = substitutorByMap(
                    substitution = classSymbol.typeParameterSymbols.zip(type.typeArguments.map { it.type!! }).toMap(),
                    useSiteSession = session,
                )
                substitutor.substituteOrSelf(propertySymbol.resolvedReturnType)
            }
            typesToUnwrap.map {
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