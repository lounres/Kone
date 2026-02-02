/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.*
import dev.lounres.kone.plugin.suppliedTypes.ir.copyMapToBy
import dev.lounres.kone.plugin.suppliedTypes.ir.copyToBy
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.FirValueParameterKind
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.declarations.utils.isClass
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.expressions.builder.*
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.SimpleFunctionBuildingContext
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.getClassDeclaredPropertySymbols
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.scopes.getFunctions
import org.jetbrains.kotlin.fir.scopes.processAllFunctions
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.ConstantValueKind


class SuppliedTypeParametersFunctionsAndPropertiesGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliedTypeMemberGeneratorKey"
    }
    
    companion object {
        private val SUPPLIABLE_PREDICATE = LookupPredicate.create {
            annotated(suppliableClassId.asSingleFqName())
        }
    }
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SUPPLIABLE_PREDICATE)
    }
    
    private val predicateBasedProvider by lazy { session.predicateBasedProvider }
    private val symbolProvider by lazy { session.symbolProvider }
    
    private val deprecatedConeClassLikeType by lazy {
        symbolProvider.getClassLikeSymbolByClassId(
            ClassId(
                packageFqName = FqName("kotlin"),
                relativeClassName = FqName("Deprecated"),
                isLocal = false
            )
        )!!.defaultType()
    }
    private val deprecationLevelClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(
            ClassId(
                packageFqName = FqName("kotlin"),
                relativeClassName = FqName("DeprecationLevel"),
                isLocal = false
            )
        )!!
    }
    private val deprecationLevelConeClassLikeType by lazy { deprecationLevelClassLikeSymbol.defaultType() }
    private val deprecatedFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = deprecatedConeClassLikeType
        }
    }
    private val deprecationLevelFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = deprecationLevelConeClassLikeType
        }
    }
    
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
    
    private val suppliables get() =
        predicateBasedProvider.getSymbolsByPredicate(SUPPLIABLE_PREDICATE)
    private val suppliableFunctions get() =
        suppliables.filterIsInstance<FirNamedFunctionSymbol>()
    private val suppliableTopLevelFunctions get() =
        suppliableFunctions.filter { it.callableId.className == null }
    
    private val FirClassSymbol<*>.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId, session)
    private val FirNamedFunctionSymbol.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId, session)
    private val FirTypeParameterSymbol.isSupply: Boolean get() = hasAnnotation(supplyClassId, session)
    
    private data class TypeParametersInfo(
        val origin: FirClassSymbol<*>,
        val appearances: Set<FirClassSymbol<*>>,
    )
    private val allSuppliedTypeParametersInfoRegistry: MutableMap<FirClassSymbol<*>, Map<FirTypeParameterSymbol, TypeParametersInfo>> = mutableMapOf()
    private val allSuperClassesSuppliedTypeParametersInfoRegistry: MutableMap<FirClassSymbol<*>, Map<FirTypeParameterSymbol, TypeParametersInfo>> = mutableMapOf()
    private val FirClassSymbol<*>.allSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
        get() = allSuppliedTypeParametersInfoRegistry.getOrPut(this) {
            buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
                val typeParameters = typeParameterSymbols
                typeParameters
                    .filter { it.isSupply }
                    .associateWith {
                        TypeParametersInfo(
                            origin = this@allSuppliedTypeParametersInfo,
                            appearances = setOf(this@allSuppliedTypeParametersInfo),
                        )
                    }.copyToBy(
                        destination = this,
                        resolve = { _, currentInfo, newInfo ->
                            check(currentInfo.origin == newInfo.origin)
                            TypeParametersInfo(
                                origin = currentInfo.origin,
                                appearances = currentInfo.appearances + newInfo.appearances,
                            )
                        },
                    )
                allSuperClassesSuppliedTypeParametersInfo
                    .copyMapToBy(
                        destination = this,
                        transform = {
                            TypeParametersInfo(
                                origin = it.value.origin,
                                appearances = it.value.appearances + this@allSuppliedTypeParametersInfo
                            )
                        },
                        resolve = { _, currentInfo, newInfo ->
                            check(currentInfo.origin == newInfo.origin)
                            TypeParametersInfo(
                                origin = currentInfo.origin,
                                appearances = currentInfo.appearances + newInfo.appearances + this@allSuppliedTypeParametersInfo
                            )
                        }
                    )
            }
        }
    private val FirClassSymbol<*>.allSuperClassesSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
        get() = allSuperClassesSuppliedTypeParametersInfoRegistry.getOrPut(this) {
            buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
                for (superType in resolvedSuperTypes) {
                    val superClassSymbol = superType.toClassSymbol(session)!!
                    if (!superClassSymbol.isSuppliable) continue
                    superClassSymbol.allSuppliedTypeParametersInfo
                        .copyToBy(
                            destination = this,
                            resolve = { _, currentInfo, newInfo ->
                                check(currentInfo.origin == newInfo.origin)
                                TypeParametersInfo(
                                    origin = currentInfo.origin,
                                    appearances = currentInfo.appearances + newInfo.appearances,
                                )
                            }
                        )
                }
            }
        }
    
    private val FirClassSymbol<*>.allNecessarySuppliedTypeParameterProperties: Set<Name>
        get() {
            val superClassFinalSuppliedTypes =
                this
                    .resolvedSuperTypes
                    .map { it.toClassSymbol(session)!! }
                    .singleOrNull { it.isClass }
                    ?.takeIf { it.isSuppliable }
                    ?.allSuppliedTypeParametersInfo
                    ?: emptyMap()
            return this
                .allSuppliedTypeParametersInfo
                .filter { it.key !in superClassFinalSuppliedTypes }
                .mapTo(mutableSetOf()) { internalSupplierPropertyName(it.value.origin.classId, it.key.name) }
        }
    
    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun getTopLevelCallableIds(): Set<CallableId> =
        suppliableTopLevelFunctions.map { it.callableId }.toSet()
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        buildSet {
            if (classSymbol.isSuppliable) {
                addAll(classSymbol.allNecessarySuppliedTypeParameterProperties)
            }
            context.declaredScope?.processAllFunctions { if (it.isSuppliable) add(it.name) }
//            if (classSymbol.isSuppliable) add(SpecialNames.INIT) // TODO: Replace with companion methods
        }
    
    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        fun SimpleFunctionBuildingContext.describe(functionToSupply: FirNamedFunctionSymbol) {
            source = functionToSupply.source
            visibility = functionToSupply.rawStatus.visibility.takeIf { it != Visibilities.Unknown } ?: Visibilities.DEFAULT_VISIBILITY
            functionToSupply.rawStatus.modality?.let { modality = it }
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
        fun FirSimpleFunction.replace(functionToSupply: FirNamedFunctionSymbol) {
//            replaceAnnotations(
//                buildList {
//                    this += buildAnnotation {
//                        annotationTypeRef = supplianceProvidedFirResolvedTypeRef
//                        argumentMapping = FirEmptyAnnotationArgumentMapping
//                    }
//                    // TODO: Add old function annotations. For some reason the solution below does not work.
////                    this += functionToSupply.resolvedAnnotationsWithArguments.also { println(it.map { it.argumentMapping::class }) }
//                }
//            )
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
        val contextFunctionsToSupply = context?.declaredScope?.getFunctions(callableId.callableName)
        return if (contextFunctionsToSupply != null) {
            contextFunctionsToSupply.map {
                createMemberFunction(
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
        } else {
            suppliableFunctions.filter { it.callableId == callableId }.map {
                createTopLevelFunction(
                    key = Key,
                    callableId = callableId,
                    returnType = it.resolvedReturnType,
                ) {
                    describe(it)
                }.apply {
                    replace(it)
                }.symbol
            }
        }
    }
    
    private val suppliedTypePropertyDeprecationAnnotation by lazy {
        buildAnnotation {
            annotationTypeRef = deprecatedFirResolvedTypeRef
            argumentMapping = buildAnnotationArgumentMapping {
                with(mapping) {
                    put(
                        Name.identifier("message"),
                        buildLiteralExpression(
                            source = null,
                            kind = ConstantValueKind.String,
                            value = "Supplied type internal property",
                            setType = true,
                        ),
                    )
                    put(
                        Name.identifier("level"),
                        buildPropertyAccessExpression {
                            coneTypeOrNull = deprecationLevelConeClassLikeType
                            val receiver = buildResolvedQualifier {
                                coneTypeOrNull = deprecationLevelConeClassLikeType
                                packageFqName = FqName("kotlin")
                                relativeClassFqName = FqName("DeprecationLevel")
                                symbol = deprecationLevelClassLikeSymbol
                                resolvedToCompanionObject = false
                            }
                            explicitReceiver = receiver
                            dispatchReceiver = receiver
                            calleeReference = buildResolvedNamedReference {
                                name = Name.identifier("HIDDEN")
                                resolvedSymbol = session.getClassDeclaredPropertySymbols(
                                    classId =
                                        ClassId(
                                            packageFqName = FqName("kotlin"),
                                            relativeClassName = FqName("DeprecationLevel"),
                                            isLocal = false
                                        ),
                                    name = Name.identifier("HIDDEN")
                                ).single()
                            }
                        },
                    )
                }
            }
        }
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> {
        if (context == null) return emptyList()
        val classSymbol = context.owner
        if (!classSymbol.isSuppliable) return emptyList()
        if (callableId.callableName !in classSymbol.allNecessarySuppliedTypeParameterProperties) return emptyList()

        val property = createMemberProperty(
            owner = classSymbol,
            key = Key,
            name = callableId.callableName,
            returnType = suppliedTypeConeClassLikeType,
            hasBackingField = false,
        ) {
            modality = if (classSymbol.isInterface) Modality.ABSTRACT else Modality.FINAL
        }.apply {
            replaceAnnotations(
                buildList {
                    this += suppliedTypePropertyDeprecationAnnotation
                }
            )
        }
        return listOf(property.symbol)
    }
}