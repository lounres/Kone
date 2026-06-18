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
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef


class SuppliedClassSupertypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session) {
    private val symbolProvider by lazy { session.symbolProvider }
    
    private val suppliableClassFirClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(suppliableClassClassClassId)!!
    }
    
    private val suppliableClassConeClassLikeType by lazy {
        suppliableClassFirClassLikeSymbol.defaultType()
    }
    
    private val FirClassLikeDeclaration.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId, session)
    private val FirNamedFunctionSymbol.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId, session)
    private val FirTypeParameterSymbol.isSupply: Boolean get() = hasAnnotation(supplyAnnotationClassId, session)
    
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean =
        declaration.isSuppliable
    
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService
    ): List<ConeKotlinType> =
        if (classLikeDeclaration.isSuppliable) listOf(suppliableClassConeClassLikeType)
        else emptyList()
}