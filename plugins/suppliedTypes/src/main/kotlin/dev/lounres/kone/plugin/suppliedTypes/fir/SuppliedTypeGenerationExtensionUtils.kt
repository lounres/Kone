/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.*
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.predicate.AbstractPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class SuppliedTypeGenerationExtensionUtils(private val session: FirSession) {
    private val predicateBasedProvider by lazy { session.predicateBasedProvider }
    private val symbolProvider by lazy { session.symbolProvider }
    
    companion object {
        val SUPPLIABLE_PREDICATE = LookupPredicate.create {
            annotated(suppliableAnnotationClassId.asSingleFqName())
        }
        val PREDICATES = listOf<AbstractPredicate<*>>(
            SUPPLIABLE_PREDICATE,
        )
    }
    
    val deprecatedConeClassLikeType by lazy {
        symbolProvider.getClassLikeSymbolByClassId(
            ClassId(
                packageFqName = FqName("kotlin"),
                relativeClassName = FqName("Deprecated"),
                isLocal = false
            )
        )!!.defaultType()
    }
    val deprecationLevelClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(
            ClassId(
                packageFqName = FqName("kotlin"),
                relativeClassName = FqName("DeprecationLevel"),
                isLocal = false
            )
        )!! as FirRegularClassSymbol
    }
    val deprecationLevelConeClassLikeType by lazy { deprecationLevelClassLikeSymbol.defaultType() }
    val deprecatedFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = deprecatedConeClassLikeType
        }
    }
    val deprecationLevelFirResolvedTypeRef by lazy {
        buildResolvedTypeRef {
            coneType = deprecationLevelConeClassLikeType
        }
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
    
    val suppliables get() = predicateBasedProvider.getSymbolsByPredicate(SUPPLIABLE_PREDICATE)
    val suppliableFunctions get() = suppliables.filterIsInstance<FirNamedFunctionSymbol>()
    val suppliableTopLevelFunctions get() = suppliableFunctions.filter { it.callableId.className == null }
    val suppliableClasses get() = suppliables.filterIsInstance<FirClassSymbol<*>>()
    val suppliableTopLevelClasses get() = suppliableClasses.filter { !it.classId.isNestedClass }
    
    val FirClassSymbol<*>.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId, session)
    val FirNamedFunctionSymbol.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId, session)
    val FirTypeParameterSymbol.isSupply: Boolean get() = hasAnnotation(supplyAnnotationClassId, session)
}