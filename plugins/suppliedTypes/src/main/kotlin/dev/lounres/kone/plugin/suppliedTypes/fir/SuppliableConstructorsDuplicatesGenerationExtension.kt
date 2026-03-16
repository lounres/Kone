/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.util.mapOperations.copyTo
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.builder.buildPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirEmptyAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.scopes.getDeclaredConstructors
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


class SuppliableConstructorsDuplicatesGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliableConstructorsDuplicatesGenerationExtension.Key"
    }
    
    private val utils = SuppliedTypeGenerationExtensionUtils(session)
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SuppliedTypeGenerationExtensionUtils.PREDICATES)
    }
    
//    private data class TypeParametersInfo(
//        val origin: FirClassSymbol<*>,
//        val appearances: Set<FirClassSymbol<*>>,
//    )
//    private val allTypeParametersInfoRegistry: MutableMap<FirClassSymbol<*>, Map<FirTypeParameterSymbol, TypeParametersInfo>> = mutableMapOf()
//    private val allSuperClassesTypeParametersInfoRegistry: MutableMap<FirClassSymbol<*>, Map<FirTypeParameterSymbol, TypeParametersInfo>> = mutableMapOf()
//    private val FirClassSymbol<*>.ownTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() =
//            typeParameterSymbols
//                .associateWith {
//                    TypeParametersInfo(
//                        origin = this,
//                        appearances = setOf(this),
//                    )
//                }
//    private val FirClassSymbol<*>.allTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() = allTypeParametersInfoRegistry.getOrPut(this) {
//            buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
//                ownTypeParametersInfo
//                    .copyToBy(
//                        destination = this,
//                        resolve = { _, currentInfo, newInfo ->
//                            check(currentInfo.origin == newInfo.origin)
//                            TypeParametersInfo(
//                                origin = currentInfo.origin,
//                                appearances = currentInfo.appearances + newInfo.appearances,
//                            )
//                        },
//                    )
//                allSuperClassesTypeParametersInfo
//                    .copyMapToBy(
//                        destination = this,
//                        transform = {
//                            TypeParametersInfo(
//                                origin = it.value.origin,
//                                appearances = it.value.appearances + this@allTypeParametersInfo
//                            )
//                        },
//                        resolve = { _, currentInfo, newInfo ->
//                            check(currentInfo.origin == newInfo.origin)
//                            TypeParametersInfo(
//                                origin = currentInfo.origin,
//                                appearances = currentInfo.appearances + newInfo.appearances + this@allTypeParametersInfo
//                            )
//                        }
//                    )
//            }
//        }
//    private val FirClassSymbol<*>.allSuperClassesTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() = allSuperClassesTypeParametersInfoRegistry.getOrPut(this) {
//            buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
//                for (superType in resolvedSuperTypes) {
//                    val superClassSymbol = superType.toClassSymbol(session)!!
//                    if (!superClassSymbol.isSuppliable) continue
//                    superClassSymbol.allTypeParametersInfo
//                        .copyToBy(
//                            destination = this,
//                            resolve = { _, currentInfo, newInfo ->
//                                check(currentInfo.origin == newInfo.origin)
//                                TypeParametersInfo(
//                                    origin = currentInfo.origin,
//                                    appearances = currentInfo.appearances + newInfo.appearances,
//                                )
//                            }
//                        )
//                }
//            }
//        }
//    private val FirClassSymbol<*>.ownSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() = ownTypeParametersInfo.filterKeys { it.isSupply }
//    private val FirClassSymbol<*>.allSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() = allTypeParametersInfo.filterKeys { it.isSupply }
//    private val FirClassSymbol<*>.allSuperClassesSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() = allSuperClassesTypeParametersInfo.filterKeys { it.isSupply }
    
//    private val FirClassSymbol<*>.ownNecessaryTypeParameterProperties: Set<Name>
//        get() {
//            return this
//                .ownTypeParametersInfo
//                .mapTo(mutableSetOf()) { internalSupplierPropertyName(it.value.origin.classId, it.key.name) }
//        }
//    private val FirClassSymbol<*>.allNecessaryTypeParameterProperties: Set<Name>
//        get() {
//            val superClassFinalTypes: Map<FirTypeParameterSymbol, TypeParametersInfo> =
//                this
//                    .resolvedSuperTypes
//                    .map { it.toClassSymbol(session)!! }
//                    .singleOrNull { it.isClass }
//                    ?.takeIf { it.isSuppliable }
//                    ?.allTypeParametersInfo
//                    ?: emptyMap()
//            return this
//                .allTypeParametersInfo
//                .filterKeys { it !in superClassFinalTypes }
//                .mapTo(mutableSetOf()) { internalSupplierPropertyName(it.value.origin.classId, it.key.name) }
//        }
//    private val FirClassSymbol<*>.allNecessarySuppliedTypeParameterProperties: Set<Name>
//        get() {
//            val superClassFinalSuppliedTypes =
//                this
//                    .resolvedSuperTypes
//                    .map { it.toClassSymbol(session)!! }
//                    .singleOrNull { it.isClass }
//                    ?.takeIf { it.isSuppliable }
//                    ?.allSuppliedTypeParametersInfo
//                    ?: emptyMap()
//            return this
//                .allSuppliedTypeParametersInfo
//                .filter { it.key !in superClassFinalSuppliedTypes }
//                .mapTo(mutableSetOf()) { internalSupplierPropertyName(it.value.origin.classId, it.key.name) }
//        }
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> = with(utils) {
        if (classSymbol.isSuppliable) setOf(SpecialNames.INIT)
        else emptySet()
    }
    
    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> = with(utils) {
        val classSymbol = context.owner
        val suppliedTypeArgumentsNumber = classSymbol.ownTypeParameterSymbols.count { it.isSupply }
        if (suppliedTypeArgumentsNumber == 0) return emptyList()
        context.declaredScope!!.getDeclaredConstructors().map { constructorToSupply ->
            createConstructor(
                owner = classSymbol,
                key = Key,
            ) {
                source = constructorToSupply.source
                visibility = constructorToSupply.rawStatus.visibility.takeIf { it != Visibilities.Unknown } ?: Visibilities.DEFAULT_VISIBILITY
                constructorToSupply.rawStatus.modality?.let { modality = it }
                status {
                    isExpect = constructorToSupply.rawStatus.isExpect
                    isActual = constructorToSupply.rawStatus.isActual
                    isOverride = constructorToSupply.rawStatus.isOverride
                    isInline = constructorToSupply.rawStatus.isInline
                    isTailRec = constructorToSupply.rawStatus.isTailRec
                    isExternal = constructorToSupply.rawStatus.isExternal
                    isConst = constructorToSupply.rawStatus.isConst
                    isLateInit = constructorToSupply.rawStatus.isLateInit
                    isInner = constructorToSupply.rawStatus.isInner
                    isCompanion = constructorToSupply.rawStatus.isCompanion
                    isSuspend = constructorToSupply.rawStatus.isSuspend
                    isStatic = constructorToSupply.rawStatus.isStatic
                    isFromSealedClass = constructorToSupply.rawStatus.isFromSealedClass
                    isFromEnumClass = constructorToSupply.rawStatus.isFromEnumClass
                    isFun = constructorToSupply.rawStatus.isFun
                }
                for (typeParameterSymbol in classSymbol.ownTypeParameterSymbols) {
                    if (typeParameterSymbol.isSupply) {
                        valueParameter(
                            name = internalSupplierParameterName(typeParameterSymbol.name),
                            type = suppliedTypeConeClassLikeType,
                            hasDefaultValue = true,
                            key = Key,
                        )
                    }
                }
                for (valueParameterSymbol in constructorToSupply.valueParameterSymbols) {
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
            }.apply {
                val suppliedConstructor = this
                var suppliedValueParametersCounter = 0
                for (typeParameterSymbol in constructorToSupply.typeParameterSymbols) {
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
                        this += constructorToSupply.resolvedAnnotationsWithArguments
                    }
                )
                replaceDelegatedConstructor(
                    buildDelegatedConstructorCall {
                        constructedTypeRef = classSymbol.defaultType().toFirResolvedTypeRef()
                        calleeReference = buildResolvedNamedReference {
                            name = SpecialNames.INIT
                            resolvedSymbol = constructorToSupply
                        }
                        isThis = true
                        val suppliedConstructorValueParametersToUse = suppliedConstructor.valueParameters.drop(suppliedTypeArgumentsNumber)
                        val suppliedConstructorValueParametersAccessExpression =
                            suppliedConstructorValueParametersToUse.map {
                                buildPropertyAccessExpression {
                                    calleeReference = buildResolvedNamedReference {
                                        name = it.name
                                        resolvedSymbol = it.symbol
                                    }
                                    coneTypeOrNull = it.symbol.resolvedReturnType
                                }
                            }
                        
                        @OptIn(SymbolInternals::class)
                        argumentList = buildResolvedArgumentList(
                            buildArgumentList {
                                arguments += suppliedConstructorValueParametersAccessExpression
                            },
                            mapping = suppliedConstructorValueParametersAccessExpression.zip(constructorToSupply.valueParameterSymbols.map { it.fir }).toMap().copyTo(LinkedHashMap())
                        )
                    }
                )
            }.symbol
        }
    }
    
//    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
//    override fun generateFunctions(
//        callableId: CallableId,
//        context: MemberGenerationContext?
//    ): List<FirNamedFunctionSymbol> = with(utils) {
//        val scope = context?.declaredScope
//
//        if (scope == null) buildList {
//            suppliableTopLevelFunctions.filter { it.callableId == callableId }.mapTo(this) { oldCallable ->
//                createTopLevelFunction(
//                    key = Key,
//                    callableId = callableId,
//                    returnTypeProvider = { newParameters ->
//                        substitutorByMap(
//                            substitution = oldCallable.ownTypeParameterSymbols.withIndex().associate {
//                                it.value to newParameters[it.index].toConeType()
//                            },
//                            useSiteSession = session,
//                        ).substituteOrSelf(oldCallable.resolvedReturnType)
//                    },
//                ) {
//                    describe(oldCallable)
//                }.apply {
//                    replace(oldCallable)
//                }.symbol
//            }
//            suppliableTopLevelClasses.filter {
//                it.name == callableId.callableName && it.packageFqName() == callableId.packageName && it.ownTypeParameterSymbols.any { it.isSupply }
//            }.forEach { suppliableClass ->
//                suppliableClass.constructors(session).mapTo(this) {
//                    createTopLevelFunction(
//                        key = Key,
//                        callableId = callableId,
//                        returnTypeProvider = { newParameters ->
//                            suppliableClass.constructType(
//                                typeArguments = newParameters.map { it.toConeType() }.toTypedArray(),
//                            )
//                        },
//                    ) {
//                        describe(it)
//                    }.apply {
//                        replace(it)
//                    }.symbol
//                }
//            }
//        } else buildList {
//            scope.processFunctionsByName(callableId.callableName) {
//                this += createMemberFunction(
//                    owner = context.owner,
//                    key = Key,
//                    name = callableId.callableName,
//                    returnType = it.resolvedReturnType,
//                ) {
//                    describe(it)
//                }.apply {
//                    replace(it)
//                }.symbol
//            }
//            val owner = context.owner
//            if (owner.isCompanion) {
//                val ownerContainer = owner.getContainingClassSymbol()!!
//                suppliableClasses.filter {
//                    it.name == callableId.callableName && it.getContainingClassSymbol() == ownerContainer && it.ownTypeParameterSymbols.any { it.isSupply }
//                }.forEach { suppliableClass ->
//                    suppliableClass.constructors(session).mapTo(this) {
//                        createMemberFunction(
//                            owner = context.owner,
//                            key = Key,
//                            name = callableId.callableName,
//                            returnTypeProvider = { newParameters ->
//                                suppliableClass.constructType(
//                                    typeArguments = newParameters.map { it.toConeType() }.toTypedArray(),
//                                )
//                            },
//                        ) {
//                            describe(it)
//                        }.apply {
//                            replace(it)
//                        }.symbol
//                    }
//                }
//            }
//        }
//    }
}