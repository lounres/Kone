/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.fir

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFileChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirPropertyAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension


class BenchmarksKotlinxBenchmarkSupportCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    companion object {
    
    }
    
    override val declarationCheckers: DeclarationCheckers get() = BenchmarksDeclarationCheckers
    override val expressionCheckers: ExpressionCheckers get() = BenchmarksExpressionCheckers
    
    object BenchmarksDeclarationCheckers : DeclarationCheckers() {
        override val namedFunctionCheckers: Set<FirNamedFunctionChecker> = setOf(
        )
        
        override val fileCheckers: Set<FirFileChecker> = setOf(
        )
    }
    
    object BenchmarksExpressionCheckers : ExpressionCheckers() {
        override val propertyAccessExpressionCheckers: Set<FirPropertyAccessExpressionChecker> = setOf(
        )
        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
        )
    }
    
    object Errors : KtDiagnosticsContainer() {
//        val NON_TOP_LEVEL_BENCHMARK by KtDiagnosticFactory1Delegate<FirAnnotation>(
//            severity = ERROR,
//        )
//        val BENCHMARK_TYPE_PARAMETER by KtDiagnosticFactory1Delegate<FirTypeParameterSymbol>(
//            severity = ERROR,
//        )
//        val BENCHMARK_NOT_ONLY_CONFIGURATION_PARAMETER by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//        )
//        val BENCHMARK_WITHOUT_CONFIGURATION_PARAMETER by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME,
//        )
//        val BENCHMARK_INCORRECT_RETURN_TYPE by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE,
//        )
//        val BENCHMARK_CONFIGURATION_PARAMETER_ACCESS by KtDiagnosticFactory1Delegate<FirPropertyAccessExpression>(
//            severity = ERROR,
//        )
//        val INJECT_FUNCTION_CALL_WITH_NON_LAMBDA_ARGUMENT by KtDiagnosticFactory1Delegate<FirFunctionCall>(
//            severity = ERROR,
//        )
//        val NON_INJECTABLE_ACCESS_INSIDE_INJECTION by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//        )
//        val BENCHMARK_CALL_INSIDE_BENCHMARK by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//        )
//        val BLACKHOLE_CALL_INSIDE_INJECTION by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//        )
//        val BENCHMARK_RESULT_IS_NOT_RETURNED_BY_BENCHMARK by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//        )
//        val BENCHMARK_RETURNS_NOT_BENCHMARK_RESULT_PROPERTY by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//        )
//        val INJECT_CALL_INSIDE_INJECTION by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = WARNING,
//        )
//        val INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
//            severity = ERROR,
//        )
        
        override fun getRendererFactory(): BaseDiagnosticRendererFactory = DefaultMessages
        
        object DefaultMessages : BaseDiagnosticRendererFactory() {
            override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("KONE_BENCHMARKS_KOTLINX_BENCHMARKS_SUPPORT") { map ->
//                map.put(
//                    NON_TOP_LEVEL_BENCHMARK,
//                    "Only top-level functions are allowed to be benchmarks (that is, to be marked with '@Benchmark' annotation).",
//                    FirDiagnosticRenderers.CALLEE_NAME,
//                )
//                map.put(
//                    BENCHMARK_TYPE_PARAMETER,
//                    "Benchmark ('@Benchmark'-annotated function) type parameters are prohibited.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                )
//                map.put(
//                    BENCHMARK_NOT_ONLY_CONFIGURATION_PARAMETER,
//                    "Benchmark ('@Benchmark'-annotated function) must have exactly one configuration parameter (parameter of type 'BenchmarkConfiguration'), but found several of them.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                )
//                map.put(
//                    BENCHMARK_WITHOUT_CONFIGURATION_PARAMETER,
//                    "Benchmark ('@Benchmark'-annotated function) must have exactly one configuration parameter (parameter of type 'BenchmarkConfiguration'), but found none.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                )
//                map.put(
//                    BENCHMARK_INCORRECT_RETURN_TYPE,
//                    "Benchmark ('@Benchmark'-annotated function) must have 'BenchmarkResult' as its return type.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                )
//                map.put(
//                    BENCHMARK_CONFIGURATION_PARAMETER_ACCESS,
//                    "Configuration parameter (parameter of type 'BenchmarkConfiguration') of benchmark ('@Benchmark'-annotated function) must not be accessed inside the benchmark.",
//                    FirDiagnosticRenderers.CALLEE_NAME,
//                )
//                map.put(
//                    INJECT_FUNCTION_CALL_WITH_NON_LAMBDA_ARGUMENT,
//                    "'${injectFunctionCallableId.callableName.identifier}' and '${injectDisposableFunctionCallableId.callableName.identifier}' functions must be called only with one lambda argument.",
//                    FirDiagnosticRenderers.CALLEE_NAME,
//                )
//                map.put(
//                    NON_INJECTABLE_ACCESS_INSIDE_INJECTION,
//                    "Non-injectable entities (functions, properties, variables, classes, etc.) must not be accessed inside injection block. {0} is not injectable.",
//                    FirDiagnosticRenderers.SYMBOL,
//                )
//                map.put(
//                    BENCHMARK_CALL_INSIDE_BENCHMARK,
//                    "{0} must not be called inside benchmark ('@Benchmark'-annotated function) because it is benchmark itself.",
//                    FirDiagnosticRenderers.SYMBOL,
//                )
//                map.put(
//                    BLACKHOLE_CALL_INSIDE_INJECTION,
//                    "{0} must not be called inside injection block.",
//                    FirDiagnosticRenderers.SYMBOL,
//                )
//                map.put(
//                    BENCHMARK_RESULT_IS_NOT_RETURNED_BY_BENCHMARK,
//                    "{0} must be immediately returned by benchmark ('@Benchmark'-annotated function).",
//                    FirDiagnosticRenderers.SYMBOL,
//                )
//                map.put(
//                    BENCHMARK_RETURNS_NOT_BENCHMARK_RESULT_PROPERTY,
//                    "Benchmark ('@Benchmark'-annotated function) must return only {0}",
//                    FirDiagnosticRenderers.SYMBOL,
//                )
//                map.put(
//                    INJECT_CALL_INSIDE_INJECTION,
//                    "{0} call is useless inside injection block.",
//                    FirDiagnosticRenderers.SYMBOL,
//                )
//                map.put(
//                    INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS,
//                    "{0} must not be called outside benchmarks.",
//                    FirDiagnosticRenderers.SYMBOL,
//                )
            }
        }
    }
}