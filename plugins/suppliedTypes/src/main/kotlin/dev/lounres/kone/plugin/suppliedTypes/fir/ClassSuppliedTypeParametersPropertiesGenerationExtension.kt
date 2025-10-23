/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.suppliedClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTargetClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.builder.buildConstructor
import org.jetbrains.kotlin.fir.declarations.constructors
import org.jetbrains.kotlin.fir.declarations.getConstructedClass
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.utils.isExtension
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildBlock
import org.jetbrains.kotlin.fir.expressions.builder.buildDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.impl.FirEmptyAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.toReference
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.scopes.getDeclaredConstructors
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


class ClassSuppliedTypeParametersPropertiesGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliedTypeMemberGeneratorKey"
    }
    
    companion object {
        private val SUPPLIED_TARGET_PREDICATE = LookupPredicate.create {
            annotated(suppliedTargetClassId.asSingleFqName())
        }
    }
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SUPPLIED_TARGET_PREDICATE)
    }
    
    private val predicateBasedProvider by lazy { session.predicateBasedProvider }
    private val symbolProvider by lazy { session.symbolProvider }
    private val suppliedTypeConeClassLikeType by lazy { symbolProvider.getClassLikeSymbolByClassId(suppliedTypeClassId)!!.defaultType() }
//    private val supplianceProvidedConeClassLikeType by lazy { symbolProvider.getClassLikeSymbolByClassId(supplianceProvidedClassId)!!.defaultType() }
    private val suppliedTypeFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = suppliedTypeConeClassLikeType
        }
    }
//    private val supplianceProvidedFirResolvedTypeRef by lazy {
//        buildResolvedTypeRef {
//            coneType = supplianceProvidedConeClassLikeType
//        }
//    }
    
    private val suppliedTargets by lazy {
        predicateBasedProvider.getSymbolsByPredicate(SUPPLIED_TARGET_PREDICATE)
    }
    private val suppliedTargetFunctions by lazy {
        suppliedTargets.filterIsInstance<FirNamedFunctionSymbol>()
    }
    private val suppliedTargetTopLevelFunctions by lazy {
        suppliedTargetFunctions.filter { it.callableId.className == null }
    }
    private val suppliedTargetClasses by lazy {
        suppliedTargets.filterIsInstance<FirRegularClassSymbol>()
    }
    private val suppliedTargetConstructors by lazy {
        suppliedTargets
            .filterIsInstance<FirConstructorSymbol>()
            .groupBy { it.getConstructedClass(session)!! }
            .filterKeys { it in suppliedTargetClasses }
    }
    
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelCallableIds(): Set<CallableId> =
        suppliedTargetTopLevelFunctions.map { it.callableId }.toSet()
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        buildSet {
            suppliedTargetFunctions.filter { it.callableId.classId == classSymbol.classId }.mapTo(this) { it.name }
//            if (classSymbol in suppliedTargetClasses) add(SpecialNames.INIT)
        }
    
    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val functionToSupply = suppliedTargetFunctions.first { it.callableId == callableId }
        return listOf(
            if (context != null)
                createMemberFunction(
                    owner = context.owner,
                    key = Key,
                    name = callableId.callableName,
                    returnType = functionToSupply.resolvedReturnType,
                ) {
                
                }.symbol
            else {
//                val typeSubstitutor = ConeSubstitutor
                createTopLevelFunction(
                    key = Key,
                    callableId = callableId,
                    returnType = functionToSupply.resolvedReturnType,
                ) {
                
                }.symbol
            }
        )
    }
    
//    @OptIn(SymbolInternals::class)
//    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
//        val suppliedTypeParameters = context.owner.ownTypeParameterSymbols.filter { it.hasAnnotation(suppliedClassId, session) }
//        val contextClass = context.owner as? FirRegularClassSymbol ?: return emptyList()
//        return suppliedTargetConstructors.getOrElse(contextClass) { emptyList() }.map { oldConstructor ->
//            createConstructor(
//                owner = context.owner,
//                key = Key,
//            ) {
//                source = oldConstructor.source
//                for (typeParameter in suppliedTypeParameters) {
//                    valueParameter(
//                        name = typeParameter.name,
//                        type = suppliedTypeConeClassLikeType,
//                        isCrossinline = false,
//                        isNoinline = false,
//                        isVararg = false,
//                        hasDefaultValue = /*true*/ false,
//                        key = Key,
//                    )
//                }
//                for (valueParameterSymbol in oldConstructor.valueParameterSymbols) {
//                    valueParameter(
//                        name = valueParameterSymbol.name,
//                        type = valueParameterSymbol.resolvedReturnType,
//                        isCrossinline = valueParameterSymbol.isCrossinline,
//                        isNoinline = valueParameterSymbol.isNoinline,
//                        isVararg = valueParameterSymbol.isVararg,
//                        hasDefaultValue = valueParameterSymbol.hasDefaultValue,
//                        key = Key,
//                    )
//                }
//            }.apply {
//                replaceAnnotations(
//                    buildList {
////                        add(
////                            buildAnnotation {
////                                annotationTypeRef = supplianceProvidedFirResolvedTypeRef
////                                argumentMapping = FirEmptyAnnotationArgumentMapping
////                            }
////                        )
//                        addAll(oldConstructor.annotations)
//                    }
//                )
//            }.symbol
//        }
//    }
    
//    override fun generateProperties(
//        callableId: CallableId,
//        context: MemberGenerationContext?
//    ): List<FirPropertySymbol> {
//        return super.generateProperties(callableId, context)
//    }
    
//    private data class TypeParametersInfo(
//        val origin: FirClassSymbol<*>,
//        val appearances: Set<FirClassSymbol<*>>,
//    )
//    private val allSuperClassesSuppliedTypeParametersInfoRegistry: MutableMap<FirClassSymbol<*>, Map<FirTypeParameterSymbol, TypeParametersInfo>> = mutableMapOf()
//    private val FirClassSymbol<*>.allSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() = buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
//            val typeParameters = typeParameterSymbols
//            typeParameters
//                .filter { it.hasAnnotation(suppliedClassId, session) }
//                .associateWith {
//                    TypeParametersInfo(
//                        origin = this@allSuppliedTypeParametersInfo,
//                        appearances = setOf(this@allSuppliedTypeParametersInfo),
//                    )
//                }.copyToBy(
//                    destination = this,
//                    resolve = { _, currentInfo, newInfo ->
//                        check(currentInfo.origin == newInfo.origin)
//                        TypeParametersInfo(
//                            origin = currentInfo.origin,
//                            appearances = currentInfo.appearances + newInfo.appearances,
//                        )
//                    },
//                )
//            allSuperClassesSuppliedTypeParametersInfo
//                .copyMapToBy(
//                    destination = this,
//                    transform = {
//                        TypeParametersInfo(
//                            origin = it.value.origin,
//                            appearances = it.value.appearances + this@allSuppliedTypeParametersInfo
//                        )
//                    },
//                    resolve = { _, currentInfo, newInfo ->
//                        check(currentInfo.origin == newInfo.origin)
//                        TypeParametersInfo(
//                            origin = currentInfo.origin,
//                            appearances = currentInfo.appearances + newInfo.appearances + this@allSuppliedTypeParametersInfo
//                        )
//                    }
//                )
//        }
//    private val FirClassSymbol<*>.allSuperClassesSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
//        get() = allSuperClassesSuppliedTypeParametersInfoRegistry.getOrPut(this) {
//            buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
//                for (superType in resolvedSuperTypes) {
//                    val superClassSymbol = superType.toClassSymbol(session)!!
//                    superClassSymbol.allSuppliedTypeParametersInfo
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
    
//    @OptIn(SymbolInternals::class)
//    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
//        buildSet {
//            classSymbol.allSuppliedTypeParametersInfo.mapTo(this) { internalSupplierPropertyName(it.value.origin.classId, it.key.name) }
//            if (classSymbol.typeParameterSymbols.any { it.hasAnnotation(suppliedClassId, session) }) add(SpecialNames.INIT)
////            if (classSymbol in constructorsWithSuppliedTypeParametersByContainingClass) add(SpecialNames.INIT)
//        }
    
//    override fun generateProperties(
//        callableId: CallableId,
//        context: MemberGenerationContext?
//    ): List<FirPropertySymbol> {
//        if (context == null) return emptyList()
//        val classSymbol = context.owner
//
//        val property = createMemberProperty(
//            owner = classSymbol,
//            key = Key,
//            name = callableId.callableName,
//            returnType = suppliedTypeConeClassLikeType,
//            hasBackingField = !classSymbol.isInterface,
//        ) {
//            modality = if (classSymbol.isInterface) Modality.ABSTRACT else Modality.FINAL
//        }.apply {
////            replaceAnnotations(listOf(buildDeprecatedAnnotation(message = "Supplied type internal property", level = DeprecationLevel.HIDDEN)))
//        }
//
////        val property = buildProperty {
////            moduleData = session.moduleData
////            origin = Key.origin
////
////            symbol = FirPropertySymbol(callableId)
////            name = callableId.callableName
////
////            returnTypeRef = suppliedTypeFirResolvedTypeRef
////            annotations += buildDeprecatedAnnotation(message = "Supplied type internal property", level = DeprecationLevel.HIDDEN)
////        }
//        return listOf(property.symbol)
//    }
    
//    @OptIn(DirectDeclarationsAccess::class, SymbolInternals::class)
//    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
//        functionsWithSuppliedTypeParameters
//        val classSymbol = context.owner
//        val suppliedTypeParameterSymbols = classSymbol.typeParameterSymbols.filter { it.hasAnnotation(suppliedClassId, session) }
//        if (suppliedTypeParameterSymbols.isEmpty()) return emptyList()
//        val constructorsToCopy = constructorsWithSuppliedTypeParametersByContainingClass.getOrDefault(classSymbol, emptyList())
//
////        return super.generateConstructors(context)
//
//        println(classSymbol.fir.declarations.filterIsInstance<FirConstructor>().map { it.body })
//
//        return constructorsToCopy.map { oldConstructor ->
//            createConstructor(
//                owner = classSymbol,
//                key = Key,
//            ) {
//                for (typeParameter in suppliedTypeParameterSymbols)
//                    valueParameter(
//                        name = internalSupplierParameterName(typeParameter.name),
//                        type = suppliedTypeConeClassLikeType,
//                    )
//                for (valueParameterSymbol in oldConstructor.valueParameterSymbols)
//                    valueParameter(
//                        name = valueParameterSymbol.name,
//                        type = valueParameterSymbol.resolvedReturnType,
//                    )
//            }.apply {
////                replaceAnnotations(listOf(buildDeprecatedAnnotation(message = "Supplied type internal constructor", level = DeprecationLevel.HIDDEN)))
//            }.symbol
//        }
//    }
}