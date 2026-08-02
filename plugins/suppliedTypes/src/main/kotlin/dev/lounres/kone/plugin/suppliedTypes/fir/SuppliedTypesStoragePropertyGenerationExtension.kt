/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSuppliedTypesStoragePropertyName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularPropertySymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name


class SuppliedTypesStoragePropertyGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliedTypesStoragePropertyGenerationExtension.Key"
    }
    
    private val utils = SuppliedTypeGenerationExtensionUtils(session)
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SuppliedTypeGenerationExtensionUtils.PREDICATES)
    }
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> = with(utils) {
        if (classSymbol.isSuppliable && classSymbol.classKind in listOf<ClassKind>(CLASS, /*ENUM_CLASS,*/ OBJECT)) setOf(internalSuppliedTypesStoragePropertyName) else emptySet()
    }
    
    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> = with(utils) {
        if (callableId.callableName != internalSuppliedTypesStoragePropertyName) return emptyList()
        if (context == null) return emptyList()
        val classSymbol = context.owner
        if (classSymbol.classKind !in listOf<ClassKind>(CLASS, /*ENUM_CLASS,*/ OBJECT)) return emptyList()
        if (!classSymbol.isSuppliable) return emptyList()
        
        listOf(
            buildProperty {
                resolvePhase = BODY_RESOLVE
                moduleData = session.moduleData
                origin = Key.origin
                status = FirResolvedDeclarationStatusImpl(
                    visibility = Visibilities.Public,
                    modality = Modality.OPEN,
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
    }
}