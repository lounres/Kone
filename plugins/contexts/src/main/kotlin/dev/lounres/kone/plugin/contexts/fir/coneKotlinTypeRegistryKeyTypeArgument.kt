/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.registryKeyClassId
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeAliasSymbol
import org.jetbrains.kotlin.fir.types.*


context(session: FirSession)
fun ConeKotlinType.registryKeyTypeArgument(): ConeKotlinType? {
    val typesToCheck = ArrayDeque<ConeKotlinType>()
    typesToCheck.addLast(this)
    
    while (typesToCheck.isNotEmpty()) {
        val type = typesToCheck.removeLast()
        when (val unwrappedType = type.unwrapToSimpleTypeUsingLowerBound()) {
            is ConeCapturedType -> typesToCheck += (unwrappedType.constructor.supertypes ?: emptyList())
            is ConeIntegerConstantOperatorType -> {}
            is ConeIntegerLiteralConstantType -> {}
            is ConeIntersectionType -> typesToCheck += unwrappedType.intersectedTypes
            is ConeStubTypeForTypeVariableInSubtyping -> {}
            is ConeTypeVariableType -> {}
            is ConeLookupTagBasedType -> when (unwrappedType) {
                is ConeTypeParameterType -> unwrappedType.lookupTag.typeParameterSymbol.resolvedBounds.mapTo(typesToCheck) { it.coneType }
                is ConeClassLikeType -> {
                    val classId = unwrappedType.lookupTag.classId
                    if (classId == registryKeyClassId)
                        return when (val typeArgument = unwrappedType.typeArguments.singleOrNull()) {
                            is ConeKotlinType -> typeArgument
                            is ConeKotlinTypeProjectionOut -> typeArgument.type
                            is ConeKotlinTypeProjectionIn -> null
                            ConeStarProjection -> null
                            is ConeKotlinTypeConflictingProjection -> null
                            null -> null
                        }
                    val classSymbol = session.symbolProvider.getClassLikeSymbolByClassId(classId) ?: continue
                    val substitutor = substitutorByMap(
                        substitution = classSymbol.typeParameterSymbols.zip(unwrappedType.typeArguments.map { it.type!! }).toMap(),
                        useSiteSession = session,
                    )
                    when(classSymbol) {
                        is FirClassSymbol -> {
                            classSymbol.resolvedSuperTypes.mapTo(typesToCheck) { substitutor.substituteOrSelf(it) }
                        }
                        is FirTypeAliasSymbol -> {
                            typesToCheck.add(substitutor.substituteOrSelf(classSymbol.resolvedExpandedTypeRef.coneType))
                        }
                    }
                }
                else -> {}
            }
        }
    }
    
    return null
}