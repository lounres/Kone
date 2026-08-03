/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.fir

import dev.lounres.kone.plugin.contextsKeys.generatedContextKeyName
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.extensions.ExperimentalSupertypesGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.getContainingClassSymbol
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirDynamicTypeRef
import org.jetbrains.kotlin.fir.types.FirFunctionTypeRef
import org.jetbrains.kotlin.fir.types.FirIntersectionTypeRef
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirUnresolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirUserTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType


class ContextKeySuperTypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session) {
    private val utils = ContextsKeysGenerationExtensionUtils(session)
    
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean = with(utils) {
        val classSymbol = declaration.symbol
        if (classSymbol.name != generatedContextKeyName) return false
        val parent = classSymbol.getContainingClassSymbol()
        if (!(parent is FirRegularClassSymbol && parent.isGenerateKey)) return false
        return true
    }
    
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService
    ): List<ConeKotlinType> = emptyList()
    
    @OptIn(ExperimentalSupertypesGenerationApi::class, SymbolInternals::class)
    override fun computeAdditionalSupertypesForGeneratedNestedClass(
        klass: FirRegularClass,
        typeResolver: TypeResolveService
    ): List<ConeKotlinType> = with(utils) {
        val classSymbol = klass.symbol
        if (classSymbol.name != generatedContextKeyName) return emptyList()
        val parent = classSymbol.getContainingClassSymbol()
        if (!(parent is FirRegularClassSymbol && parent.isGenerateKey)) return emptyList()
        
        val substitutor = substitutorByMap(
            substitution = parent.typeParameterSymbols.zip(klass.typeParameters.map { it.symbol.defaultType }).toMap(),
            useSiteSession = session
        )
        klass.typeParameters.forEachIndexed { index, typeParameter ->
            typeParameter as FirTypeParameter
            typeParameter.replaceBounds(
                parent.typeParameterSymbols[index].fir.bounds.map {
                    buildResolvedTypeRef {
                        coneType = substitutor.substituteOrSelf(
                            when (it) {
                                is FirResolvedTypeRef -> it.coneType
                                is FirUnresolvedTypeRef -> when (it) {
                                    is FirUserTypeRef -> typeResolver.resolveUserType(it).coneType
                                    is FirDynamicTypeRef -> TODO()
                                    is FirFunctionTypeRef -> TODO()
                                    is FirIntersectionTypeRef -> TODO()
                                }
                                else -> TODO()
                            }
                        )
                    }
                }
            )
        }
        
        listOf(
            suppliedTypeRegistryKeyClassLikeSymbol.constructType(
                typeArguments = arrayOf(
                    parent.constructType(
                        typeArguments = klass.typeParameters.map { it.symbol.defaultType }.toTypedArray()
                    )
                )
            )
        )
    }
}