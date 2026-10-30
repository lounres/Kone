/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.fir

import dev.lounres.kone.plugin.benchmarks.benchmarkAnnotationClassId
import dev.lounres.kone.plugin.benchmarks.benchmarkConfigurationClassClassId
import dev.lounres.kone.plugin.benchmarks.benchmarkResultPropertyCallableId
import dev.lounres.kone.plugin.benchmarks.blackholePropertyCallableId
import dev.lounres.kone.plugin.benchmarks.injectDisposableFunctionCallableId
import dev.lounres.kone.plugin.benchmarks.injectFunctionCallableId
import dev.lounres.kone.scope
import dev.lounres.kone.util.kotlinCompilerUtils.KtDiagnosticFactory1Delegate
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.diagnostics.Severity.ERROR
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFileChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirPropertyAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.symbol
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.getContainingClassSymbol
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid


class BenchmarksCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    companion object {
        context(context: CheckerContext)
        val FirNamedFunction.isBenchmark: Boolean get() = hasAnnotation(benchmarkAnnotationClassId, context.session)
        context(context: CheckerContext)
        val FirNamedFunctionSymbol.isBenchmark: Boolean get() = hasAnnotation(benchmarkAnnotationClassId, context.session)
    }
    
    override val declarationCheckers: DeclarationCheckers get() = BenchmarksDeclarationCheckers
    override val expressionCheckers: ExpressionCheckers get() = BenchmarksExpressionCheckers
    
    object BenchmarksDeclarationCheckers : DeclarationCheckers() {
        override val namedFunctionCheckers: Set<FirNamedFunctionChecker> = setOf(
            BenchmarkMarkedNamedFunctionChecker,
            BenchmarkTypeParametersChecker,
            BenchmarkConfigurationParametersChecker,
            BenchmarkInjectableCallsCheckers,
            BenchmarkInsideBenchmarkChecker,
            BlackholeInsideInjectionChecker,
            BenchmarkResultAndReturnChecker,
            InjectionInsideInjectionChecker,
        )
        
        override val fileCheckers: Set<FirFileChecker> = setOf(
            InjectionBlackholeAndBenchmarkResultCallsOutsideBenchmarkChecker,
        )
    }
    
    object BenchmarksExpressionCheckers : ExpressionCheckers() {
        override val propertyAccessExpressionCheckers: Set<FirPropertyAccessExpressionChecker> = setOf(
            BenchmarkConfigurationParameterAccessChecker,
        )
        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
            InjectFunctionCallChecker,
        )
    }
    
    object BenchmarkMarkedNamedFunctionChecker : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            val annotation = declaration.getAnnotationByClassId(benchmarkAnnotationClassId, context.session) ?: return
            if (declaration.isLocal || declaration.getContainingClassSymbol() != null)
                reporter.reportOn(
                    source = annotation.source,
                    factory = Errors.NON_TOP_LEVEL_BENCHMARK,
                    a = annotation,
                    context = context,
                )
        }
    }
    
    object BenchmarkTypeParametersChecker : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            if (!declaration.isBenchmark || declaration.isLocal || declaration.getContainingClassSymbol() != null) return
            for (typeParameter in declaration.typeParameters)
                reporter.reportOn(
                    source = typeParameter.source,
                    factory = Errors.BENCHMARK_TYPE_PARAMETER,
                    a = typeParameter.symbol,
                    context = context,
                )
        }
    }
    
    object BenchmarkConfigurationParametersChecker : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            if (!declaration.isBenchmark || declaration.isLocal || declaration.getContainingClassSymbol() != null) return
            
            val benchmarkConfigurationConeClassLikeType = context.session.symbolProvider
                .getClassLikeSymbolByClassId(benchmarkConfigurationClassClassId)!!
                .defaultType()
            
            val receiverParameter = declaration.receiverParameter
            val valueParameters = declaration.valueParameters
            val contextParameters = declaration.contextParameters
            
            val configurationDeclarations = buildList {
                if (
                    receiverParameter != null
                    && receiverParameter.typeRef.coneTypeOrNull == benchmarkConfigurationConeClassLikeType
                ) add(receiverParameter)
                (valueParameters + contextParameters).filterTo(this) {
                    it.returnTypeRef.coneTypeOrNull == benchmarkConfigurationConeClassLikeType
                }
            }
            
            if (configurationDeclarations.size > 1) {
                configurationDeclarations.forEach {
                    reporter.reportOn(
                        source = it.source,
                        factory = Errors.BENCHMARK_NOT_ONLY_CONFIGURATION_PARAMETER,
                        a = it.symbol,
                        context = context,
                    )
                }
                return
            }
            
            if (configurationDeclarations.size == 0) {
                reporter.reportOn(
                    source = declaration.source,
                    factory = Errors.BENCHMARK_WITHOUT_CONFIGURATION_PARAMETER,
                    a = declaration.symbol,
                    context = context,
                )
                return
            }
        }
    }
    
    object BenchmarkConfigurationParameterAccessChecker : FirPropertyAccessExpressionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(expression: FirPropertyAccessExpression) {
            val calleeSymbol = expression.calleeReference.symbol ?: return
            val containingDeclarationSymbol: FirBasedSymbol<FirDeclaration>
            val calleeType: ConeKotlinType
            when (calleeSymbol) {
                is FirReceiverParameterSymbol -> {
                    containingDeclarationSymbol = calleeSymbol.containingDeclarationSymbol
                    calleeType = calleeSymbol.resolvedType
                }
                is FirValueParameterSymbol -> {
                    containingDeclarationSymbol = calleeSymbol.containingDeclarationSymbol
                    calleeType = calleeSymbol.resolvedReturnType
                }
                else -> return
            }
            val benchmarkConfigurationConeClassLikeType = context.session.symbolProvider
                .getClassLikeSymbolByClassId(benchmarkConfigurationClassClassId)!!
                .defaultType()
            if (calleeType != benchmarkConfigurationConeClassLikeType) return
            if (containingDeclarationSymbol !is FirNamedFunctionSymbol) return
            if (
                !containingDeclarationSymbol.isBenchmark
                || containingDeclarationSymbol.isLocal
                || containingDeclarationSymbol.getContainingClassSymbol() != null
            ) return
            reporter.reportOn(
                source = expression.source,
                factory = Errors.BENCHMARK_CONFIGURATION_PARAMETER_ACCESS,
                a = expression,
                context = context,
            )
        }
    }
    
    object InjectFunctionCallChecker : FirFunctionCallChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(expression: FirFunctionCall) {
            val injectFunctions = listOf(
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectFunctionCallableId.packageName,
                    injectFunctionCallableId.callableName,
                ).single(),
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectDisposableFunctionCallableId.packageName,
                    injectDisposableFunctionCallableId.callableName,
                ).single(),
            )
            
            if (expression.calleeReference.symbol !in injectFunctions) return
            if (expression.arguments.singleOrNull() is FirAnonymousFunctionExpression) return
            
            reporter.reportOn(
                source = expression.source,
                factory = Errors.INJECT_FUNCTION_CALL_WITH_NON_LAMBDA_ARGUMENT,
                a = expression,
                context = context,
            )
        }
    }
    
    class BenchmarkInjectionBlockFinder(
        context: CheckerContext,
        private val subvisitor: FirVisitorVoid,
    ) : DefaultFirVisitorVoid() {
        private val injectFunctions = listOf(
            context.session.symbolProvider.getTopLevelFunctionSymbols(
                injectFunctionCallableId.packageName,
                injectFunctionCallableId.callableName,
            ).single(),
            context.session.symbolProvider.getTopLevelFunctionSymbols(
                injectDisposableFunctionCallableId.packageName,
                injectDisposableFunctionCallableId.callableName,
            ).single(),
        )
        
        override fun visitFunctionCall(functionCall: FirFunctionCall) {
            scope {
                if (functionCall.calleeReference.symbol !in injectFunctions) return@scope
                val argument = functionCall.arguments.singleOrNull()
                if (argument !is FirAnonymousFunctionExpression) return@scope
                
                argument.anonymousFunction.body?.accept(subvisitor)
                
                return
            }
            
            visitQualifiedAccessExpression(functionCall)
        }
    }
    
    object BenchmarkInjectableCallsCheckers : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            if (!declaration.isBenchmark || declaration.isLocal || declaration.getContainingClassSymbol() != null) return
            
            val nonInjectableDeclarationSymbols = mutableSetOf<FirBasedSymbol<*>>()
            
            declaration.body?.accept(
                BenchmarkNonInjectableDeclarationsCollector(
                    context = context,
                    nonInjectableDeclarationSymbols = nonInjectableDeclarationSymbols,
                )
            )
            
            declaration.accept(
                BenchmarkInjectionBlockFinder(
                    context = context,
                    subvisitor = BenchmarkNonInjectableCallsReporter(
                        context = context,
                        reporter = reporter,
                        nonInjectableDeclarationSymbols = nonInjectableDeclarationSymbols,
                    )
                )
            )
        }
        
        private class BenchmarkNonInjectableDeclarationsCollector(
            private val context: CheckerContext,
            private val nonInjectableDeclarationSymbols: MutableSet<FirBasedSymbol<*>>,
        ) : DefaultFirVisitorVoid() {
            private val injectFunctions = listOf(
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectFunctionCallableId.packageName,
                    injectFunctionCallableId.callableName,
                ).single(),
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectDisposableFunctionCallableId.packageName,
                    injectDisposableFunctionCallableId.callableName,
                ).single(),
            )
            
            override fun visitFunctionCall(functionCall: FirFunctionCall) {
                if (
                    functionCall.calleeReference.symbol in injectFunctions
                    && functionCall.arguments.singleOrNull() is FirAnonymousFunctionExpression
                ) return
                
                super.visitFunctionCall(functionCall)
            }
            
            override fun visitDeclaration(declaration: FirDeclaration) {
                nonInjectableDeclarationSymbols += declaration.symbol
                declaration.acceptChildren(this)
            }
        }
        
        private class BenchmarkNonInjectableCallsReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
            private val nonInjectableDeclarationSymbols: Set<FirBasedSymbol<*>>,
        ) : DefaultFirVisitorVoid() {
            override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
                val calleeSymbol = qualifiedAccessExpression.calleeReference.symbol
                
                if (calleeSymbol != null && calleeSymbol in nonInjectableDeclarationSymbols) {
                    reporter.reportOn(
                        source = qualifiedAccessExpression.source,
                        factory = Errors.NON_INJECTABLE_ACCESS_INSIDE_INJECTION,
                        a = calleeSymbol,
                        context = context,
                    )
                }
                
                super.visitQualifiedAccessExpression(qualifiedAccessExpression)
            }
        }
    }
    
    object BenchmarkInsideBenchmarkChecker : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            if (!declaration.isBenchmark || declaration.isLocal || declaration.getContainingClassSymbol() != null) return
            
            declaration.accept(
                BenchmarkCallsReporter(
                    context = context,
                    reporter = reporter,
                )
            )
        }
        
        private class BenchmarkCallsReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
        ) : DefaultFirVisitorVoid() {
            override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
                val calleeSymbol = qualifiedAccessExpression.calleeReference.symbol
                
                if (
                    calleeSymbol != null
                    && calleeSymbol is FirNamedFunctionSymbol
                    && context(context) { calleeSymbol.isBenchmark }
                    && !calleeSymbol.isLocal
                    && calleeSymbol.getContainingClassSymbol() == null
                ) {
                    reporter.reportOn(
                        source = qualifiedAccessExpression.source,
                        factory = Errors.BENCHMARK_CALL_INSIDE_BENCHMARK,
                        a = calleeSymbol,
                        context = context,
                    )
                }
                
                super.visitQualifiedAccessExpression(qualifiedAccessExpression)
            }
        }
    }
    
    object BlackholeInsideInjectionChecker : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            if (!declaration.isBenchmark || declaration.isLocal || declaration.getContainingClassSymbol() != null) return
            
            declaration.accept(
                BenchmarkInjectionBlockFinder(
                    context = context,
                    subvisitor = BlackholeInsideInjectionReporter(
                        context = context,
                        reporter = reporter,
                    )
                )
            )
        }
        
        private class BlackholeInsideInjectionReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
        ) : DefaultFirVisitorVoid() {
            private val blackhole = context.session.symbolProvider
                .getTopLevelPropertySymbols(
                    blackholePropertyCallableId.packageName,
                    blackholePropertyCallableId.callableName,
                ).single()
            
            override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
                val calleeSymbol = qualifiedAccessExpression.calleeReference.symbol
                
                if (calleeSymbol == blackhole) {
                    reporter.reportOn(
                        source = qualifiedAccessExpression.source,
                        factory = Errors.BLACKHOLE_CALL_INSIDE_INJECTION,
                        a = calleeSymbol,
                        context = context,
                    )
                }
                
                super.visitQualifiedAccessExpression(qualifiedAccessExpression)
            }
        }
    }
    
    object BenchmarkResultAndReturnChecker : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            if (!declaration.isBenchmark || declaration.isLocal || declaration.getContainingClassSymbol() != null) return
            
            declaration.accept(
                IncorrectBenchmarkResultAndReturnReporter(
                    context = context,
                    reporter = reporter,
                    benchmark = declaration,
                )
            )
        }
        
        private class IncorrectBenchmarkResultAndReturnReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
            private val benchmark: FirNamedFunction,
        ) : DefaultFirVisitorVoid() {
            private val benchmarkResult = context.session.symbolProvider
                .getTopLevelPropertySymbols(
                    benchmarkResultPropertyCallableId.packageName,
                    benchmarkResultPropertyCallableId.callableName,
                ).single()
            
            override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
                val calleeSymbol = qualifiedAccessExpression.calleeReference.symbol
                
                if (calleeSymbol == benchmarkResult) {
                    reporter.reportOn(
                        source = qualifiedAccessExpression.source,
                        factory = Errors.BENCHMARK_RESULT_IS_NOT_RETURNED_BY_BENCHMARK,
                        a = calleeSymbol,
                        context = context,
                    )
                }
                
                super.visitQualifiedAccessExpression(qualifiedAccessExpression)
            }
            
            override fun visitReturnExpression(returnExpression: FirReturnExpression) {
                if (returnExpression.target.labeledElement.symbol == benchmark.symbol) {
                    val returnResult = returnExpression.result
                    if (returnResult !is FirQualifiedAccessExpression || returnResult.calleeReference.symbol != benchmarkResult) {
                        reporter.reportOn(
                            source = returnExpression.source,
                            factory = Errors.BENCHMARK_RESULT_IS_NOT_RETURNED_BY_BENCHMARK,
                            a = benchmarkResult,
                            context = context,
                        )
                    }
                } else super.visitReturnExpression(returnExpression)
            }
        }
    }
    
    object InjectionInsideInjectionChecker : FirNamedFunctionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirNamedFunction) {
            if (!declaration.isBenchmark || declaration.isLocal || declaration.getContainingClassSymbol() != null) return
            
            declaration.accept(
                BenchmarkInjectionBlockFinder(
                    context = context,
                    subvisitor = BlackholeInsideInjectionReporter(
                        context = context,
                        reporter = reporter,
                    )
                )
            )
        }
        
        private class BlackholeInsideInjectionReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
        ) : DefaultFirVisitorVoid() {
            private val injectFunctions = listOf(
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectFunctionCallableId.packageName,
                    injectFunctionCallableId.callableName,
                ).single(),
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectDisposableFunctionCallableId.packageName,
                    injectDisposableFunctionCallableId.callableName,
                ).single(),
            )
            
            override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
                val calleeSymbol = qualifiedAccessExpression.calleeReference.symbol
                
                if (calleeSymbol != null && calleeSymbol in injectFunctions) {
                    reporter.reportOn(
                        source = qualifiedAccessExpression.calleeReference.source,
                        factory = Errors.INJECT_CALL_INSIDE_INJECTION,
                        a = calleeSymbol,
                        context = context,
                    )
                }
                
                super.visitQualifiedAccessExpression(qualifiedAccessExpression)
            }
        }
    }
    
    object InjectionBlackholeAndBenchmarkResultCallsOutsideBenchmarkChecker : FirFileChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirFile) {
            declaration.accept(
                InjectionBlackholeAndBenchmarkResultCallsOutsideBenchmarkReporter(
                    context = context,
                    reporter = reporter,
                )
            )
        }
        
        private class InjectionBlackholeAndBenchmarkResultCallsOutsideBenchmarkReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
        ) : DefaultFirVisitorVoid() {
            private val forbiddenCallables = listOf(
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectFunctionCallableId.packageName,
                    injectFunctionCallableId.callableName,
                ).single(),
                context.session.symbolProvider.getTopLevelFunctionSymbols(
                    injectDisposableFunctionCallableId.packageName,
                    injectDisposableFunctionCallableId.callableName,
                ).single(),
                context.session.symbolProvider.getTopLevelPropertySymbols(
                    blackholePropertyCallableId.packageName,
                    blackholePropertyCallableId.callableName,
                ).single(),
                context.session.symbolProvider.getTopLevelPropertySymbols(
                    benchmarkResultPropertyCallableId.packageName,
                    benchmarkResultPropertyCallableId.callableName,
                ).single(),
            )
            
            override fun visitNamedFunction(namedFunction: FirNamedFunction) {
                if (context(context) { namedFunction.isBenchmark } && !namedFunction.isLocal && namedFunction.getContainingClassSymbol() == null) return
                super.visitNamedFunction(namedFunction)
            }
            
            override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
                val calleeSymbol = qualifiedAccessExpression.calleeReference.symbol
                
                if (calleeSymbol != null && calleeSymbol in forbiddenCallables) {
                    reporter.reportOn(
                        source = qualifiedAccessExpression.calleeReference.source,
                        factory = Errors.INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS,
                        a = calleeSymbol,
                        context = context,
                    )
                }
                
                super.visitQualifiedAccessExpression(qualifiedAccessExpression)
            }
        }
    }
    
    object Errors : KtDiagnosticsContainer() {
        val NON_TOP_LEVEL_BENCHMARK by KtDiagnosticFactory1Delegate<FirAnnotation>(
            severity = ERROR,
        )
        val BENCHMARK_TYPE_PARAMETER by KtDiagnosticFactory1Delegate<FirTypeParameterSymbol>(
            severity = ERROR,
        )
        val BENCHMARK_NOT_ONLY_CONFIGURATION_PARAMETER by KtDiagnosticFactory1Delegate<FirBasedSymbol<FirDeclaration>>(
            severity = ERROR,
        )
        val BENCHMARK_WITHOUT_CONFIGURATION_PARAMETER by KtDiagnosticFactory1Delegate<FirBasedSymbol<FirDeclaration>>(
            severity = ERROR,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME,
        )
        val BENCHMARK_CONFIGURATION_PARAMETER_ACCESS by KtDiagnosticFactory1Delegate<FirPropertyAccessExpression>(
            severity = ERROR,
        )
        val INJECT_FUNCTION_CALL_WITH_NON_LAMBDA_ARGUMENT by KtDiagnosticFactory1Delegate<FirFunctionCall>(
            severity = ERROR,
        )
        val NON_INJECTABLE_ACCESS_INSIDE_INJECTION by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
            severity = ERROR,
        )
        val BENCHMARK_CALL_INSIDE_BENCHMARK by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
            severity = ERROR,
        )
        val BLACKHOLE_CALL_INSIDE_INJECTION by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
            severity = ERROR,
        )
        val BENCHMARK_RESULT_IS_NOT_RETURNED_BY_BENCHMARK by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
            severity = ERROR,
        )
        val BENCHMARK_RETURNS_NOT_BENCHMARK_RESULT_PROPERTY by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
            severity = ERROR,
        )
        val INJECT_CALL_INSIDE_INJECTION by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
            severity = WARNING,
        )
        val INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS by KtDiagnosticFactory1Delegate<FirBasedSymbol<*>>(
            severity = ERROR,
        )
        // TODO:
        //  wrong benchmark modifiers: suspend, inline, ???
        
        override fun getRendererFactory(): BaseDiagnosticRendererFactory = DefaultMessages
        
        object DefaultMessages : BaseDiagnosticRendererFactory() {
            override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("KONE_BENCHMARKS") { map ->
                map.put(
                    NON_TOP_LEVEL_BENCHMARK,
                    "Only top-level functions are allowed to be benchmarks (that is, to be marked with '@Benchmark' annotation).",
                    FirDiagnosticRenderers.CALLEE_NAME,
                )
                map.put(
                    BENCHMARK_TYPE_PARAMETER,
                    "Benchmark ('@Benchmark'-annotated function) type parameters are prohibited.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
                map.put(
                    BENCHMARK_NOT_ONLY_CONFIGURATION_PARAMETER,
                    "Benchmark ('@Benchmark'-annotated function) must have exactly one configuration parameter (parameter of type 'BenchmarkConfiguration'), but found several of them.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
                map.put(
                    BENCHMARK_WITHOUT_CONFIGURATION_PARAMETER,
                    "Benchmark ('@Benchmark'-annotated function) must have exactly one configuration parameter (parameter of type 'BenchmarkConfiguration'), but found none.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
                map.put(
                    BENCHMARK_CONFIGURATION_PARAMETER_ACCESS,
                    "Configuration parameter (parameter of type 'BenchmarkConfiguration') of benchmark ('@Benchmark'-annotated function) must not be accessed inside the benchmark.",
                    FirDiagnosticRenderers.CALLEE_NAME,
                )
                map.put(
                    INJECT_FUNCTION_CALL_WITH_NON_LAMBDA_ARGUMENT,
                    "'${injectFunctionCallableId.callableName.identifier}' and '${injectDisposableFunctionCallableId.callableName.identifier}' functions must be called only with one lambda argument.",
                    FirDiagnosticRenderers.CALLEE_NAME,
                )
                map.put(
                    NON_INJECTABLE_ACCESS_INSIDE_INJECTION,
                    "Non-injectable entities (functions, properties, variables, classes, etc.) must not be accessed inside injection block. {0} is not injectable.",
                    FirDiagnosticRenderers.SYMBOL,
                )
                map.put(
                    BENCHMARK_CALL_INSIDE_BENCHMARK,
                    "{0} must not be called inside benchmark ('@Benchmark'-annotated function) because it is benchmark itself.",
                    FirDiagnosticRenderers.SYMBOL,
                )
                map.put(
                    BLACKHOLE_CALL_INSIDE_INJECTION,
                    "{0} must not be called inside injection block.",
                    FirDiagnosticRenderers.SYMBOL,
                )
                map.put(
                    BENCHMARK_RESULT_IS_NOT_RETURNED_BY_BENCHMARK,
                    "{0} must be immediately returned by benchmark ('@Benchmark'-annotated function).",
                    FirDiagnosticRenderers.SYMBOL,
                )
                map.put(
                    BENCHMARK_RETURNS_NOT_BENCHMARK_RESULT_PROPERTY,
                    "Benchmark ('@Benchmark'-annotated function) must return only {0}",
                    FirDiagnosticRenderers.SYMBOL,
                )
                map.put(
                    INJECT_CALL_INSIDE_INJECTION,
                    "{0} call is useless inside injection block.",
                    FirDiagnosticRenderers.SYMBOL,
                )
                map.put(
                    INJECT_BLACKHOLE_OR_BENCHMARK_RESULT_OUTSIDE_BENCHMARKS,
                    "{0} must not be called outside benchmarks.",
                    FirDiagnosticRenderers.SYMBOL,
                )
            }
        }
    }
}