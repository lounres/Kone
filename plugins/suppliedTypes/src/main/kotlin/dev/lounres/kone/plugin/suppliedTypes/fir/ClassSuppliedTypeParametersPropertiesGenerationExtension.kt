/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.suppliedTargetClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName


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
    private val suppliedTypeFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = suppliedTypeConeClassLikeType
        }
    }
    
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
    private val suppliedTargetPackages: Set<FqName> by lazy { suppliedTargets.mapTo(mutableSetOf()) { it.packageFqName() } }
    
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelCallableIds(): Set<CallableId> =
        suppliedTargetTopLevelFunctions.map { it.callableId }.toSet().also {
            println(
                """
                suppliedTargetPackages = $suppliedTargetPackages
                suppliedTargetFunctions = $suppliedTargetFunctions
                suppliedTargetClasses = $suppliedTargetClasses
            """.trimIndent()
            )
        }
    
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