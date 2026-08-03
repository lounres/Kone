/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.fir

import dev.lounres.kone.plugin.contextsKeys.generatedContextKeyName
import dev.lounres.kone.plugin.contextsKeys.registryKeyImpliedKeysPropertyShortName
import dev.lounres.kone.plugin.suppliedTypes.internalSuppliedTypesStoragePropertyName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.containingClassForStaticMemberAttr
import org.jetbrains.kotlin.fir.declarations.builder.buildConstructedClassTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.builder.buildNamedFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildPrimaryConstructor
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClass
import org.jetbrains.kotlin.fir.declarations.builder.buildTypeParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.getContainingClassSymbol
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.scopes.kotlinScopeProvider
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


class ContextKeyGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "ContextKeyGenerationExtension.Key"
    }
    
    private val utils = ContextsKeysGenerationExtensionUtils(session)
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(ContextsKeysGenerationExtensionUtils.PREDICATES)
    }
    
    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext
    ): Set<Name> = with(utils) {
        if (classSymbol.isGenerateKey) setOf(generatedContextKeyName) else emptySet()
    }
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> = with(utils) {
        val parent = classSymbol.getContainingClassSymbol()
        when {
            !(parent is FirRegularClassSymbol && parent.isGenerateKey) -> emptySet()
            classSymbol.name == generatedContextKeyName -> setOf(
                SpecialNames.INIT,
                Name.identifier("toString"),
                registryKeyImpliedKeysPropertyShortName,
                internalSuppliedTypesStoragePropertyName
            )
            else -> emptySet()
        }
    }
    
    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext
    ): FirClassLikeSymbol<*>? = with(utils) {
        if (!(owner is FirRegularClassSymbol && owner.isGenerateKey && name == generatedContextKeyName)) return null
        buildRegularClass {
            resolvePhase = BODY_RESOLVE
            moduleData = session.moduleData
            origin = Key.origin
            val classSymbol = FirRegularClassSymbol(owner.classId.createNestedClassId(name))
            val newTypeParameters = owner.typeParameterSymbols.map {
                buildTypeParameter {
                    resolvePhase = BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = Key.origin
                    this.name = it.name
                    symbol = FirTypeParameterSymbol()
                    containingDeclarationSymbol = classSymbol
                    variance = it.variance
                    isReified = it.isReified
                    annotations += supplyAnnotation()
                }
            }
            val substitutor = substitutorByMap(
                substitution = owner.typeParameterSymbols.zip(newTypeParameters.map { it.symbol.defaultType }).toMap(),
                useSiteSession = session
            )
            newTypeParameters.forEachIndexed { index, newTypeParameter ->
                newTypeParameter.replaceBounds(
                    owner.typeParameterSymbols[index].resolvedBounds.map {
                        buildResolvedTypeRef {
                            coneType = substitutor.substituteOrSelf(it.coneType)
                        }
                    }
                )
            }
            typeParameters += newTypeParameters
            status = FirResolvedDeclarationStatusImpl(
                visibility = Visibilities.Public,
                modality = Modality.FINAL,
                effectiveVisibility = EffectiveVisibility.Public,
            )
            scopeProvider = session.kotlinScopeProvider
            classKind = if (owner.typeParameterSymbols.isEmpty()) OBJECT else CLASS
            annotations += suppliableAnnotation()
            this.name = name
            symbol = classSymbol
            superTypeRefs += buildResolvedTypeRef {
                coneType = suppliedTypeRegistryKeyClassLikeSymbol.constructType(
                    typeArguments = arrayOf(
                        owner.constructType(
                            typeArguments = newTypeParameters.map { it.symbol.defaultType }.toTypedArray()
                        )
                    )
                )
            }
        }.symbol
    }
    
    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> = with(utils) {
        val classSymbol = context.owner
        if (classSymbol.name != generatedContextKeyName) return emptyList()
        val parent = classSymbol.getContainingClassSymbol()
        if (!(parent is FirRegularClassSymbol && parent.isGenerateKey)) return emptyList()
        listOf(
            buildPrimaryConstructor {
                resolvePhase = BODY_RESOLVE
                moduleData = session.moduleData
                origin = Key.origin
                typeParameters += classSymbol.typeParameterSymbols.map {
                    buildConstructedClassTypeParameterRef {
                        symbol = it
                    }
                }
                status = FirResolvedDeclarationStatusImpl(
                    visibility = Visibilities.Public,
                    modality = Modality.FINAL,
                    effectiveVisibility = EffectiveVisibility.Public,
                )
                isLocal = false
                returnTypeRef = buildResolvedTypeRef {
                    coneType = classSymbol.defaultType()
                }
                symbol = FirConstructorSymbol(classSymbol.classId)
            }.apply {
                containingClassForStaticMemberAttr = classSymbol.toLookupTag()
            }.symbol
        )
    }
    
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> = with(utils) {
        if (context == null) return emptyList()
        val classSymbol = context.owner
        if (classSymbol.name != generatedContextKeyName) return emptyList()
        val parent = classSymbol.getContainingClassSymbol()
        if (!(parent is FirRegularClassSymbol && parent.isGenerateKey)) return emptyList()
        when (callableId.callableName) {
            Name.identifier("toString") -> listOf(
                buildNamedFunction {
                    resolvePhase = BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = Key.origin
                    status = FirResolvedDeclarationStatusImpl(
                        visibility = Visibilities.Public,
                        modality = Modality.FINAL,
                        effectiveVisibility = EffectiveVisibility.Public,
                    )
                    isLocal = false
                    returnTypeRef = session.builtinTypes.stringType
                    dispatchReceiverType = classSymbol.defaultType()
                    name = Name.identifier("toString")
                    symbol = FirNamedFunctionSymbol(callableId)
                }.symbol
            )
            else -> emptyList()
        }
    }
    
    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> = with(utils) {
        if (context == null) return emptyList()
        val classSymbol = context.owner
        if (classSymbol.name != generatedContextKeyName) return emptyList()
        val parent = classSymbol.getContainingClassSymbol()
        if (!(parent is FirRegularClassSymbol && parent.isGenerateKey)) return emptyList()
        when (callableId.callableName) {
            registryKeyImpliedKeysPropertyShortName -> listOf(
                buildProperty {
                    resolvePhase = BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = Key.origin
                    status = FirResolvedDeclarationStatusImpl(
                        visibility = Visibilities.Public,
                        modality = Modality.FINAL,
                        effectiveVisibility = EffectiveVisibility.Public,
                    )
                    isLocal = false
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = impliedKeysRegistryClassLikeSymbol.constructType(
                            typeArguments = arrayOf(
                                parent.constructType(
                                    typeArguments = classSymbol.typeParameterSymbols.map { it.defaultType }.toTypedArray()
                                )
                            )
                        )
                    }
                    dispatchReceiverType = classSymbol.defaultType()
                    name = registryKeyImpliedKeysPropertyShortName
                    isVar = false
                    symbol = FirRegularPropertySymbol(callableId)
                }.symbol,
            )
            internalSuppliedTypesStoragePropertyName -> listOf(
                buildProperty {
                    resolvePhase = BODY_RESOLVE
                    moduleData = session.moduleData
                    origin = Key.origin
                    status = FirResolvedDeclarationStatusImpl(
                        visibility = Visibilities.Public,
                        modality = Modality.FINAL,
                        effectiveVisibility = EffectiveVisibility.Public,
                    )
                    isLocal = false
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = suppliedTypesStorageConeClassLikeType
                    }
                    dispatchReceiverType = classSymbol.defaultType()
                    name = internalSuppliedTypesStoragePropertyName
                    isVar = true
                    val suppliedTypesStoragePropertySymbol = FirRegularPropertySymbol(callableId)
                    symbol = suppliedTypesStoragePropertySymbol
                }.symbol,
            )
            else -> emptyList()
        }
    }
}