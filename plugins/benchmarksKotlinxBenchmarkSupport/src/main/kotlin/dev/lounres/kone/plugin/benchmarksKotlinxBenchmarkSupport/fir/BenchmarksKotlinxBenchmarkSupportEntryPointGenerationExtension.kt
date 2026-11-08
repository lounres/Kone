/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.builder.buildNamedFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.visitors.FirTransformer
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.ReturnValueStatus


class BenchmarksKotlinxBenchmarkSupportEntryPointGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "BenchmarksKotlinxBenchmarkSupportEntryPointGenerationExtension.Key"
    }
    
    companion object {
        val entryPointCallableId = CallableId(
            packageName = FqName("dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.generated"),
            className = null,
            callableName = Name.identifier("main"),
        )
    }
    
    override fun hasPackage(packageFqName: FqName): Boolean = packageFqName == entryPointCallableId.packageName
    
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelCallableIds(): Set<CallableId> = setOf(entryPointCallableId)
    
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
            }.symbol
        )
    }
}