/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.fir

import dev.lounres.kone.plugin.contextsKeys.generateKoneContextKeyAnnotationClassId
import dev.lounres.kone.plugin.contextsKeys.impliedKeysRegistryClassId
import dev.lounres.kone.plugin.contextsKeys.suppliedTypeRegistryKeyClassId
import dev.lounres.kone.plugin.suppliedTypes.noSuppliedTypeParameterInClassStubSingletonClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.supplianceProvidedAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypesStorageDelegateFunctionCallableId
import dev.lounres.kone.plugin.suppliedTypes.supplyAnnotationClassId
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.predicate.AbstractPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


class ContextsKeysGenerationExtensionUtils(private val session: FirSession) {
    private val predicateBasedProvider by lazy { session.predicateBasedProvider }
    private val symbolProvider by lazy { session.symbolProvider }
    
    companion object {
        val GENERATE_KEY_PREDICATE = LookupPredicate.create {
            annotated(generateKoneContextKeyAnnotationClassId.asSingleFqName())
        }
        val PREDICATES = listOf<AbstractPredicate<*>>(
            GENERATE_KEY_PREDICATE,
        )
    }
    
    val supplyAnnotationClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(supplyAnnotationClassId)!! as FirRegularClassSymbol
    }
    val suppliableAnnotationClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(suppliableAnnotationClassId)!! as FirRegularClassSymbol
    }
    fun supplyAnnotation() = buildAnnotation {
        annotationTypeRef = buildResolvedTypeRef {
            coneType = supplyAnnotationClassLikeSymbol.defaultType()
        }
        argumentMapping = buildAnnotationArgumentMapping {}
    }
    fun suppliableAnnotation() = buildAnnotation {
        annotationTypeRef = buildResolvedTypeRef {
            coneType = suppliableAnnotationClassLikeSymbol.defaultType()
        }
        argumentMapping = buildAnnotationArgumentMapping {}
    }
    
    val suppliedTypeFirClassLikeSymbol by lazy { symbolProvider.getClassLikeSymbolByClassId(suppliedTypeClassClassId)!! }
    val supplianceProvidedFirClassLikeSymbol by lazy { symbolProvider.getClassLikeSymbolByClassId(supplianceProvidedAnnotationClassId)!! }
    val noSuppliedTypeParameterInClassStubFirClassLikeSymbol by lazy { symbolProvider.getClassLikeSymbolByClassId(noSuppliedTypeParameterInClassStubSingletonClassId)!! }
    val suppliedTypesStorageDelegateFirNamedFunctionSymbol by lazy {
        symbolProvider.getTopLevelFunctionSymbols(
            packageFqName = suppliedTypesStorageDelegateFunctionCallableId.packageName,
            name = suppliedTypesStorageDelegateFunctionCallableId.callableName,
        ).single()
    }
    
    val suppliedTypeConeClassLikeType by lazy { suppliedTypeFirClassLikeSymbol.defaultType() }
    val supplianceProvidedConeClassLikeType by lazy { supplianceProvidedFirClassLikeSymbol.defaultType() }
    val noSuppliedTypeParameterInClassStubConeClassLikeType by lazy { noSuppliedTypeParameterInClassStubFirClassLikeSymbol.defaultType() }
    val suppliedTypesStorageConeClassLikeType by lazy {
        symbolProvider.getClassLikeSymbolByClassId(
            ClassId(
                packageFqName = FqName("kotlin.collections"),
                relativeClassName = FqName("Map"),
                isLocal = false,
            )
        )!!.constructType(
            typeArguments = arrayOf(
                session.builtinTypes.stringType.coneType,
                symbolProvider.getClassLikeSymbolByClassId(
                    ClassId(
                        packageFqName = FqName("kotlin.collections"),
                        relativeClassName = FqName("List"),
                        isLocal = false,
                    )
                )!!.constructType(
                    typeArguments = arrayOf(
                        suppliedTypeConeClassLikeType,
                    )
                )
            )
        )
    }
    
    val suppliedTypeFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = suppliedTypeConeClassLikeType
        }
    }
    val supplianceProvidedFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = supplianceProvidedConeClassLikeType
        }
    }
    val suppliedTypesStorageDelegateFirResolvedNamedReference by lazy {
        buildResolvedNamedReference {
            name = Name.identifier("suppliedTypesStorageDelegate")
            resolvedSymbol = suppliedTypesStorageDelegateFirNamedFunctionSymbol
        }
    }
    
    val impliedKeysRegistryClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(impliedKeysRegistryClassId)!!
    }
    
    val suppliedTypeRegistryKeyClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(suppliedTypeRegistryKeyClassId)!! as FirRegularClassSymbol
    }
    
    val FirClassLikeSymbol<*>.isGenerateKey: Boolean get() = predicateBasedProvider.matches(GENERATE_KEY_PREDICATE, this)
    val FirClassLikeDeclaration.isGenerateKey: Boolean get() = predicateBasedProvider.matches(GENERATE_KEY_PREDICATE, this)
}