/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.suppliableClassClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.supplyAnnotationClassId
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef


class SuppliedClassSupertypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session) {
    private val utils = SuppliedTypeGenerationExtensionUtils(session)
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SuppliedTypeGenerationExtensionUtils.PREDICATES)
    }
    
    private val symbolProvider by lazy { session.symbolProvider }
    
    private val suppliableClassFirClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(suppliableClassClassClassId)!!
    }
    
    private val suppliableClassConeClassLikeType by lazy {
        suppliableClassFirClassLikeSymbol.defaultType()
    }
    
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean = with(utils) { declaration.isSuppliable }
    
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService
    ): List<ConeKotlinType> =
        if (with(utils) { classLikeDeclaration.isSuppliable }) listOf(suppliableClassConeClassLikeType)
        else emptyList()
}