/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.plugin.suppliedTypes.internalSupplierPropertyName
import dev.lounres.kone.plugin.suppliedTypes.ir.copyMapToBy
import dev.lounres.kone.plugin.suppliedTypes.ir.copyToBy
import dev.lounres.kone.plugin.suppliedTypes.supplianceProvidedClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTargetClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildCallableReferenceAccess
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildResolvedQualifier
import org.jetbrains.kotlin.fir.expressions.impl.FirEmptyAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.plugin.SimpleFunctionBuildingContext
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.scopes.getDeclaredConstructors
import org.jetbrains.kotlin.fir.scopes.getFunctions
import org.jetbrains.kotlin.fir.scopes.processAllFunctions
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.types.ConstantValueKind


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
    private val supplianceProvidedConeClassLikeType by lazy { symbolProvider.getClassLikeSymbolByClassId(supplianceProvidedClassId)!!.defaultType() }
    private val suppliedTypeFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = suppliedTypeConeClassLikeType
        }
    }
    private val supplianceProvidedFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = supplianceProvidedConeClassLikeType
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
    
    private data class TypeParametersInfo(
        val origin: FirClassSymbol<*>,
        val appearances: Set<FirClassSymbol<*>>,
    )
    private val allSuperClassesSuppliedTypeParametersInfoRegistry: MutableMap<FirClassSymbol<*>, Map<FirTypeParameterSymbol, TypeParametersInfo>> = mutableMapOf()
    private val FirClassSymbol<*>.allSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
        get() = buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
            val typeParameters = typeParameterSymbols
            typeParameters
                .filter { it.hasAnnotation(suppliedClassId, session) }
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
    private val FirClassSymbol<*>.allSuperClassesSuppliedTypeParametersInfo: Map<FirTypeParameterSymbol, TypeParametersInfo>
        get() = allSuperClassesSuppliedTypeParametersInfoRegistry.getOrPut(this) {
            buildMap<FirTypeParameterSymbol, TypeParametersInfo> {
                for (superType in resolvedSuperTypes) {
                    val superClassSymbol = superType.toClassSymbol(session)!!
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
    
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelCallableIds(): Set<CallableId> =
        suppliedTargetTopLevelFunctions.map { it.callableId }.toSet()
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        buildSet {
            classSymbol.allSuppliedTypeParametersInfo.mapTo(this) { internalSupplierPropertyName(it.value.origin.classId, it.key.name) }
            context.declaredScope?.processAllFunctions { if (it.hasAnnotation(suppliedTargetClassId, session)) add(it.name) }
            if (classSymbol.hasAnnotation(suppliedTargetClassId, session)) add(SpecialNames.INIT)
        }
    
    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        fun SimpleFunctionBuildingContext.describe(functionToSupply: FirNamedFunctionSymbol) {
            visibility = functionToSupply.rawStatus.visibility.takeIf { it != Visibilities.Unknown } ?: Visibilities.DEFAULT_VISIBILITY
            modality = functionToSupply.rawStatus.modality ?: Modality.FINAL
            status {
                // TODO: Correct status
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
                if (typeParameterSymbol.hasAnnotation(suppliedClassId, session)) {
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
            for (contextParameterSymbol in functionToSupply.contextParameterSymbols) {
                TODO("Context parameters are not yet supported")
            }
//            extensionReceiverType {
//                functionToSupply.receiverParameterSymbol?.resolvedType?.replaceArgumentsDeeply {  }
//            }
        }
        fun FirSimpleFunction.replace(functionToSupply: FirNamedFunctionSymbol) {
            replaceAnnotations(
                buildList {
                    this += buildAnnotation {
                        annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                        argumentMapping = FirEmptyAnnotationArgumentMapping
                    }
                    // TODO: Add old function annotations. For some reason the solution below does not work.
//                    this += functionToSupply.resolvedAnnotationsWithArguments.also { println(it.map { it.argumentMapping::class }) }
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
            suppliedTargetFunctions.filter { it.callableId == callableId }.map {
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
    
    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val suppliedTypeParameters = context.owner.ownTypeParameterSymbols.filter { it.hasAnnotation(suppliedClassId, session) }
        val constructors = context.declaredScope?.getDeclaredConstructors() ?: return emptyList()
        val constructorsMapping = constructors.associateWith { oldConstructor ->
            createConstructor(
                owner = context.owner,
                key = Key,
            ) {
                source = oldConstructor.source
                for (typeParameter in suppliedTypeParameters) {
                    valueParameter(
                        name = typeParameter.name,
                        type = suppliedTypeConeClassLikeType,
                        hasDefaultValue = true,
                        key = Key,
                    )
                }
                for (valueParameterSymbol in oldConstructor.valueParameterSymbols) {
                    valueParameter(
                        name = internalSupplierParameterName(valueParameterSymbol.name),
                        type = valueParameterSymbol.resolvedReturnType,
                        isCrossinline = valueParameterSymbol.isCrossinline,
                        isNoinline = valueParameterSymbol.isNoinline,
                        isVararg = valueParameterSymbol.isVararg,
                        hasDefaultValue = valueParameterSymbol.hasDefaultValue,
                        key = Key,
                    )
                }
            }.apply {
                replaceAnnotations(
                    buildList {
                        this += buildAnnotation {
                            annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                            argumentMapping = FirEmptyAnnotationArgumentMapping
                        }
                        // TODO: Add old function annotations. For some reason the solution below does not work.
//                        this += oldConstructor.resolvedAnnotationsWithArguments.also { println(it.map { it.argumentMapping::class }) }
                    }
                )
            }.symbol
        }
        for ((oldConstructor, newConstructor) in constructorsMapping) {
        
        }
        return constructorsMapping.values.toList()
    }
    
    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> {
        if (context == null) return emptyList()
        val classSymbol = context.owner
        
        val typeParameter = classSymbol.typeParameterSymbols
            .firstOrNull { internalSupplierPropertyName(classSymbol.classId, it.name) == callableId.callableName }
            ?: return emptyList()

        val property = createMemberProperty(
            owner = classSymbol,
            key = Key,
            name = callableId.callableName,
            returnType = suppliedTypeConeClassLikeType,
            hasBackingField = !classSymbol.isInterface,
        ) {
            modality = if (classSymbol.isInterface) Modality.ABSTRACT else Modality.FINAL
        }.apply {
            replaceAnnotations(
                buildList {
                    this += buildAnnotation {
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
                                    buildResolvedQualifier {
                                        coneTypeOrNull = deprecationLevelConeClassLikeType
                                        packageFqName = FqName("kotlin")
                                        relativeClassFqName = FqName("DeprecationLevel")
                                        symbol = deprecationLevelClassLikeSymbol
                                        resolvedToCompanionObject = false
                                    },
                                )
                            }
                        }
                    }
                }
            )
        }
        return listOf(property.symbol)
    }
}