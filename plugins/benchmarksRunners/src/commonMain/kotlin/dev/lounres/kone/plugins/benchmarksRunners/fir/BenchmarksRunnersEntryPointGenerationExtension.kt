/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.fir

import dev.lounres.kone.plugins.benchmarksRunners.entryPointCallableId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.builder.buildNamedFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


class BenchmarksRunnersEntryPointGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "BenchmarksRunnersEntryPointGenerationExtension.Key"
    }
    
    override fun hasPackage(packageFqName: FqName): Boolean = packageFqName == entryPointCallableId.packageName
    
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelCallableIds(): Set<CallableId> = setOfNotNull(entryPointCallableId.takeIf { !session.moduleData.isCommon })
    
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        if (callableId != entryPointCallableId || context != null) return emptyList()
        return listOf(
            buildNamedFunction {
                moduleData = session.moduleData
                origin = Key.origin
                status = FirResolvedDeclarationStatusImpl(
                    visibility = Visibilities.Public,
                    modality = Modality.FINAL,
                    effectiveVisibility = Public,
                )
                isLocal = false
                returnTypeRef = session.builtinTypes.unitType
                name = callableId.callableName
                symbol = FirNamedFunctionSymbol(callableId)
                
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider
                            .getClassLikeSymbolByClassId(ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Array")))!!
                            .constructType(
                                typeArguments = arrayOf(
                                    session.builtinTypes.stringType.coneType,
                                ),
                                isMarkedNullable = false,
                            )
                    }
                    name = Name.identifier("args")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
            }.symbol
        )
    }
}