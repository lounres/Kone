/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSuppliedTypesStoragePropertyName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
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
        
        val property = createMemberProperty(
            owner = classSymbol,
            key = Key,
            name = callableId.callableName,
            returnType = suppliedTypesStorageConeClassLikeType,
            isVal = false,
            hasBackingField = true,
        ) {
            modality = Modality.OPEN
            setter(Visibilities.Public)
        }.apply {
//            replaceAnnotations(
//                buildList {
//                    this += suppliedTypePropertyDeprecationAnnotation
//                }
//            )
            replaceDelegate(
                buildFunctionCall {
                    calleeReference = suppliedTypesStorageDelegateFirResolvedNamedReference
                    coneTypeOrNull = suppliedTypesStorageDelegateFirNamedFunctionSymbol.resolvedReturnType
                }
            )
            replaceGetter(null)
            replaceSetter(null)
        }
        
        listOf(property.symbol)
    }
}