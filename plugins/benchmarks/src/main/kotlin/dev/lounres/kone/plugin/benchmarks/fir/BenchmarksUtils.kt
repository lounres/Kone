/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.fir

import dev.lounres.kone.plugin.benchmarks.benchmarkAnnotationClassId
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.predicate.AbstractPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol


class BenchmarksUtils(private val session: FirSession) {
    private val predicateBasedProvider by lazy { session.predicateBasedProvider }
    private val symbolProvider by lazy { session.symbolProvider }
    
    companion object {
        val BENCHMARK_PREDICATE = LookupPredicate.create {
            annotated(benchmarkAnnotationClassId.asSingleFqName())
        }
        val PREDICATES = listOf<AbstractPredicate<*>>(
            BENCHMARK_PREDICATE,
        )
    }
    
    val benchmarks by lazy {
        predicateBasedProvider.getSymbolsByPredicate(BENCHMARK_PREDICATE)
            .filterIsInstance<FirNamedFunctionSymbol>()
            .filter { !it.isLocal && it.callableId.classId === null }
    }
}