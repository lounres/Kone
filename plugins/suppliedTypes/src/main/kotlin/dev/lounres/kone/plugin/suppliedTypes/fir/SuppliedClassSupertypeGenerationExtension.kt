/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.suppliableClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import dev.lounres.kone.plugin.suppliedTypes.supplyClassId
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef


class SuppliedClassSupertypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session) {
    private val symbolProvider by lazy { session.symbolProvider }
    
    private val suppliableClassFirClassLikeSymbol by lazy {
        symbolProvider.getClassLikeSymbolByClassId(suppliableClassClassId)!!
    }
    
    private val suppliableClassConeClassLikeType by lazy {
        suppliableClassFirClassLikeSymbol.defaultType()
    }
    
    private val FirClassLikeDeclaration.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId, session)
    private val FirNamedFunctionSymbol.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId, session)
    private val FirTypeParameterSymbol.isSupply: Boolean get() = hasAnnotation(supplyClassId, session)
    
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean = false
    
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService
    ): List<ConeKotlinType> =
        TODO()
//        if (classLikeDeclaration.isSuppliable) listOf() else emptyList()
}