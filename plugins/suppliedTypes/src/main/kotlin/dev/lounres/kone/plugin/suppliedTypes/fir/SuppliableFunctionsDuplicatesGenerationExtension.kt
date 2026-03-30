/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.plugin.suppliedTypes.suppliableClassId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirValueParameterKind
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.impl.FirEmptyAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.SimpleFunctionBuildingContext
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.scopes.processAllFunctions
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name


class SuppliableFunctionsDuplicatesGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliableFunctionsDuplicatesGenerationExtension.Key"
    }
    
    private val utils = SuppliedTypeGenerationExtensionUtils(session)
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SuppliedTypeGenerationExtensionUtils.PREDICATES)
    }
    
    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun getTopLevelCallableIds(): Set<CallableId> = with(utils) {
        buildSet {
            suppliableTopLevelFunctions.mapTo(this) { it.callableId }
        }
    }
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> = with(utils) {
        buildSet {
            context.declaredScope?.processAllFunctions { if (it.isSuppliable) add(it.name) }
        }
    }
    
    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> = with(utils) {
        fun SimpleFunctionBuildingContext.describe(functionToSupply: FirFunctionSymbol<*>) {
            source = functionToSupply.source
            visibility = functionToSupply.rawStatus.visibility.takeIf { it != Visibilities.Unknown } ?: Visibilities.DEFAULT_VISIBILITY
            functionToSupply.rawStatus.modality?.let { modality = it }
            modality = functionToSupply.rawStatus.modality
                ?: when {
                    !functionToSupply.hasBody -> Modality.ABSTRACT
                    functionToSupply.rawStatus.isOverride -> Modality.OPEN
                    else -> Modality.FINAL
                }
            status {
                isExpect = functionToSupply.rawStatus.isExpect
                isActual = functionToSupply.rawStatus.isActual
                isOverride = functionToSupply.rawStatus.isOverride
                isInline = functionToSupply.rawStatus.isInline
                isTailRec = functionToSupply.rawStatus.isTailRec
                isExternal = functionToSupply.rawStatus.isExternal
                isConst = functionToSupply.rawStatus.isConst
                isLateInit = functionToSupply.rawStatus.isLateInit
                isInner = functionToSupply.rawStatus.isInner
                isCompanion = functionToSupply.rawStatus.isCompanion
                isSuspend = functionToSupply.rawStatus.isSuspend
                isStatic = functionToSupply.rawStatus.isStatic
                isFromSealedClass = functionToSupply.rawStatus.isFromSealedClass
                isFromEnumClass = functionToSupply.rawStatus.isFromEnumClass
                isFun = functionToSupply.rawStatus.isFun
            }
            for (typeParameterSymbol in functionToSupply.typeParameterSymbols) {
                typeParameter(
                    name = typeParameterSymbol.name,
                    variance = typeParameterSymbol.variance,
                    isReified = typeParameterSymbol.isReified,
                    key = Key,
                ) { // TODO: Copy type parameters' annotations
                    for (currentBound in typeParameterSymbol.resolvedBounds) {
                        bound {
                            currentBound.coneType
                        }
                    }
                }
                if (typeParameterSymbol.isSupply) {
                    valueParameter(
                        name = internalSupplierParameterName(typeParameterSymbol.name),
                        type = suppliedTypeConeClassLikeType,
                        hasDefaultValue = true,
                        key = Key,
                    )
                }
            }
            for (valueParameterSymbol in functionToSupply.valueParameterSymbols) {
                valueParameter(
                    name = valueParameterSymbol.name,
                    type = valueParameterSymbol.resolvedReturnType,
                    isCrossinline = valueParameterSymbol.isCrossinline,
                    isNoinline = valueParameterSymbol.isNoinline,
                    isVararg = valueParameterSymbol.isVararg,
                    hasDefaultValue = valueParameterSymbol.hasDefaultValue,
                    key = Key,
                )
            }
            functionToSupply.resolvedReceiverType?.let { extensionReceiverType(it) }
        }
        fun FirNamedFunction.replace(functionToSupply: FirFunctionSymbol<*>) {
            var suppliedValueParametersCounter = 0
            for (typeParameterSymbol in functionToSupply.typeParameterSymbols) {
                if (typeParameterSymbol.isSupply) {
                    valueParameters[suppliedValueParametersCounter].apply {
                        replaceAnnotations(
                            listOf(
                                buildAnnotation {
                                    annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                                    argumentMapping = FirEmptyAnnotationArgumentMapping
                                }
                            )
                        )
                    }
                    suppliedValueParametersCounter++
                }
            }
            replaceAnnotations(
                buildList {
                    this += buildAnnotation {
                        annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                        argumentMapping = FirEmptyAnnotationArgumentMapping
                    }
                    functionToSupply.resolvedAnnotationsWithArguments.filterTo(this) {
                        it.toAnnotationClassId(session) != suppliableClassId
                    }
                }
            )
            replaceContextParameters(
                functionToSupply.contextParameterSymbols.map {
                    buildValueParameter {
                        moduleData = session.moduleData
                        origin = Key.origin
                        name = it.name
                        symbol = FirValueParameterSymbol()
                        returnTypeRef = it.resolvedReturnTypeRef
                        containingDeclarationSymbol = this@replace.symbol
                        valueParameterKind = FirValueParameterKind.ContextParameter
                    }
                }
            )
        }
        val scope = context?.declaredScope
        
        if (scope == null) {
            suppliableTopLevelFunctions.filter { it.callableId == callableId && it.typeParameterSymbols.any { it.isSupply } }.map { oldCallable ->
                createTopLevelFunction(
                    key = Key,
                    callableId = callableId,
                    returnTypeProvider = { newParameters ->
                        substitutorByMap(
                            substitution = oldCallable.typeParameterSymbols.withIndex().associate {
                                it.value to newParameters[it.index].toConeType()
                            },
                            useSiteSession = session,
                        ).substituteOrSelf(oldCallable.resolvedReturnType)
                    },
                ) {
                    describe(oldCallable)
                }.apply {
                    replace(oldCallable)
                }.symbol
            }
        } else buildList {
            scope.processFunctionsByName(callableId.callableName) {
                if (it.typeParameterSymbols.none { it.isSupply } ) return@processFunctionsByName
                this += createMemberFunction(
                    owner = context.owner,
                    key = Key,
                    name = callableId.callableName,
                    returnType = it.resolvedReturnType,
                ) {
                    describe(it)
                }.apply {
                    replace(it)
                }.symbol
            }
        }
    }
}