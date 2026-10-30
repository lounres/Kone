/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.fir

import dev.lounres.kone.plugin.benchmarks.benchmarkAnnotationClassId
import dev.lounres.kone.plugin.benchmarks.benchmarkConfigurationClassClassId
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
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirPropertyAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirAnonymousInitializer
import org.jetbrains.kotlin.fir.declarations.FirAnonymousObject
import org.jetbrains.kotlin.fir.declarations.FirBackingField
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirCodeFragment
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirDanglingModifierList
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.declarations.FirErrorFunction
import org.jetbrains.kotlin.fir.declarations.FirErrorPrimaryConstructor
import org.jetbrains.kotlin.fir.declarations.FirErrorProperty
import org.jetbrains.kotlin.fir.declarations.FirField
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirMemberDeclaration
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirReplSnippet
import org.jetbrains.kotlin.fir.declarations.FirScript
import org.jetbrains.kotlin.fir.declarations.FirScriptReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirTypeAlias
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirCallableReferenceAccess
import org.jetbrains.kotlin.fir.expressions.FirComponentCall
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirImplicitInvokeCall
import org.jetbrains.kotlin.fir.expressions.FirIntegerLiteralOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirSuperReceiverExpression
import org.jetbrains.kotlin.fir.expressions.FirThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.FirWhenSubjectExpression
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
                BenchmarkInjectionBlockAccessor(
                    context = context,
                    reporter = reporter,
                    nonInjectableDeclarationSymbols = nonInjectableDeclarationSymbols,
                )
            )
        }
        
        class BenchmarkNonInjectableDeclarationsCollector(
            private val context: CheckerContext,
            private val nonInjectableDeclarationSymbols: MutableSet<FirBasedSymbol<*>>,
        ) : FirVisitorVoid() {
            override fun visitElement(element: FirElement) { element.acceptChildren(this) }
            
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
                
                visitQualifiedAccessExpression(functionCall)
            }
            
            override fun visitComponentCall(componentCall: FirComponentCall) {
                visitFunctionCall(componentCall)
            }
            override fun visitImplicitInvokeCall(implicitInvokeCall: FirImplicitInvokeCall) {
                visitFunctionCall(implicitInvokeCall)
            }
            override fun visitIntegerLiteralOperatorCall(integerLiteralOperatorCall: FirIntegerLiteralOperatorCall) {
                visitFunctionCall(integerLiteralOperatorCall)
            }
            
            override fun visitDeclaration(declaration: FirDeclaration) {
                nonInjectableDeclarationSymbols += declaration.symbol
                declaration.acceptChildren(this)
            }
            
            override fun visitReplSnippet(replSnippet: FirReplSnippet) {
                visitDeclaration(replSnippet)
            }
            override fun visitDanglingModifierList(danglingModifierList: FirDanglingModifierList) {
                visitDeclaration(danglingModifierList)
            }
            override fun visitMemberDeclaration(memberDeclaration: FirMemberDeclaration) {
                visitDeclaration(memberDeclaration)
            }
            override fun visitCallableDeclaration(callableDeclaration: FirCallableDeclaration) {
                visitMemberDeclaration(callableDeclaration)
            }
            override fun visitVariable(variable: FirVariable) {
                visitCallableDeclaration(variable)
            }
            override fun visitValueParameter(valueParameter: FirValueParameter) {
                visitVariable(valueParameter)
            }
            override fun visitField(field: FirField) {
                visitVariable(field)
            }
            override fun visitBackingField(backingField: FirBackingField) {
                visitVariable(backingField)
            }
            override fun visitProperty(property: FirProperty) {
                visitVariable(property)
            }
            override fun visitErrorProperty(errorProperty: FirErrorProperty) {
                visitProperty(errorProperty)
            }
            override fun visitEnumEntry(enumEntry: FirEnumEntry) {
                visitVariable(enumEntry)
            }
            override fun visitFunction(function: FirFunction) {
                visitCallableDeclaration(function)
            }
            override fun visitAnonymousFunction(anonymousFunction: FirAnonymousFunction) {
                visitFunction(anonymousFunction)
            }
            override fun visitConstructor(constructor: FirConstructor) {
                visitFunction(constructor)
            }
            override fun visitErrorPrimaryConstructor(errorPrimaryConstructor: FirErrorPrimaryConstructor) {
                visitConstructor(errorPrimaryConstructor)
            }
            override fun visitNamedFunction(namedFunction: FirNamedFunction) {
                visitFunction(namedFunction)
            }
            override fun visitPropertyAccessor(propertyAccessor: FirPropertyAccessor) {
                visitFunction(propertyAccessor)
            }
            override fun visitErrorFunction(errorFunction: FirErrorFunction) {
                visitFunction(errorFunction)
            }
            override fun visitClassLikeDeclaration(classLikeDeclaration: FirClassLikeDeclaration) {
                visitMemberDeclaration(classLikeDeclaration)
            }
            override fun visitClass(klass: FirClass) {
                visitClassLikeDeclaration(klass)
            }
            override fun visitRegularClass(regularClass: FirRegularClass) {
                visitClass(regularClass)
            }
            override fun visitAnonymousObject(anonymousObject: FirAnonymousObject) {
                visitClass(anonymousObject)
            }
            override fun visitTypeAlias(typeAlias: FirTypeAlias) {
                visitClassLikeDeclaration(typeAlias)
            }
            override fun visitAnonymousInitializer(anonymousInitializer: FirAnonymousInitializer) {
                visitDeclaration(anonymousInitializer)
            }
            override fun visitCodeFragment(codeFragment: FirCodeFragment) {
                visitDeclaration(codeFragment)
            }
            override fun visitReceiverParameter(receiverParameter: FirReceiverParameter) {
                visitDeclaration(receiverParameter)
            }
            override fun visitScriptReceiverParameter(scriptReceiverParameter: FirScriptReceiverParameter) {
                visitReceiverParameter(scriptReceiverParameter)
            }
            override fun visitFile(file: FirFile) {
                visitDeclaration(file)
            }
            override fun visitScript(script: FirScript) {
                visitDeclaration(script)
            }
            override fun visitTypeParameter(typeParameter: FirTypeParameter) {
                visitDeclaration(typeParameter)
            }
        }
        
        class BenchmarkInjectionBlockAccessor(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
            private val nonInjectableDeclarationSymbols: Set<FirBasedSymbol<*>>,
        ) : FirVisitorVoid() {
            override fun visitElement(element: FirElement) { element.acceptChildren(this) }
            
            private val callsReporter = BenchmarkNonInjectableCallsReporter(
                context = context,
                reporter = reporter,
                nonInjectableDeclarationSymbols = nonInjectableDeclarationSymbols,
            )
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
                qualifiedAccessExpression.acceptChildren(this)
            }
            
            override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression) {
                visitQualifiedAccessExpression(propertyAccessExpression)
            }
            override fun visitWhenSubjectExpression(whenSubjectExpression: FirWhenSubjectExpression) {
                visitPropertyAccessExpression(whenSubjectExpression)
            }
            override fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess) {
                visitQualifiedAccessExpression(callableReferenceAccess)
            }
            override fun visitFunctionCall(functionCall: FirFunctionCall) {
                scope {
                    if (functionCall.calleeReference.symbol !in injectFunctions) return@scope
                    val argument = functionCall.arguments.singleOrNull()
                    if (argument !is FirAnonymousFunctionExpression) return@scope
                    
                    argument.anonymousFunction.body?.accept(callsReporter)
                    
                    return
                }
                
                visitQualifiedAccessExpression(functionCall)
            }
            override fun visitComponentCall(componentCall: FirComponentCall) {
                visitFunctionCall(componentCall)
            }
            override fun visitImplicitInvokeCall(implicitInvokeCall: FirImplicitInvokeCall) {
                visitFunctionCall(implicitInvokeCall)
            }
            override fun visitIntegerLiteralOperatorCall(integerLiteralOperatorCall: FirIntegerLiteralOperatorCall) {
                visitFunctionCall(integerLiteralOperatorCall)
            }
            override fun visitThisReceiverExpression(thisReceiverExpression: FirThisReceiverExpression) {
                visitQualifiedAccessExpression(thisReceiverExpression)
            }
            override fun visitSuperReceiverExpression(superReceiverExpression: FirSuperReceiverExpression) {
                visitQualifiedAccessExpression(superReceiverExpression)
            }
        }
        
        class BenchmarkNonInjectableCallsReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
            private val nonInjectableDeclarationSymbols: Set<FirBasedSymbol<*>>,
        ) : FirVisitorVoid() {
            override fun visitElement(element: FirElement) { element.acceptChildren(this) }
            
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
                
                qualifiedAccessExpression.acceptChildren(this)
            }
            
            override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression) {
                visitQualifiedAccessExpression(propertyAccessExpression)
            }
            override fun visitWhenSubjectExpression(whenSubjectExpression: FirWhenSubjectExpression) {
                visitPropertyAccessExpression(whenSubjectExpression)
            }
            override fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess) {
                visitQualifiedAccessExpression(callableReferenceAccess)
            }
            override fun visitFunctionCall(functionCall: FirFunctionCall) {
                visitQualifiedAccessExpression(functionCall)
            }
            override fun visitComponentCall(componentCall: FirComponentCall) {
                visitFunctionCall(componentCall)
            }
            override fun visitImplicitInvokeCall(implicitInvokeCall: FirImplicitInvokeCall) {
                visitFunctionCall(implicitInvokeCall)
            }
            override fun visitIntegerLiteralOperatorCall(integerLiteralOperatorCall: FirIntegerLiteralOperatorCall) {
                visitFunctionCall(integerLiteralOperatorCall)
            }
            override fun visitThisReceiverExpression(thisReceiverExpression: FirThisReceiverExpression) {
                visitQualifiedAccessExpression(thisReceiverExpression)
            }
            override fun visitSuperReceiverExpression(superReceiverExpression: FirSuperReceiverExpression) {
                visitQualifiedAccessExpression(superReceiverExpression)
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
        
        class BenchmarkCallsReporter(
            private val context: CheckerContext,
            private val reporter: DiagnosticReporter,
        ) : FirVisitorVoid() {
            override fun visitElement(element: FirElement) { element.acceptChildren(this) }
            
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
                
                qualifiedAccessExpression.acceptChildren(this)
            }
            
            override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression) {
                visitQualifiedAccessExpression(propertyAccessExpression)
            }
            override fun visitWhenSubjectExpression(whenSubjectExpression: FirWhenSubjectExpression) {
                visitPropertyAccessExpression(whenSubjectExpression)
            }
            override fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess) {
                visitQualifiedAccessExpression(callableReferenceAccess)
            }
            override fun visitFunctionCall(functionCall: FirFunctionCall) {
                visitQualifiedAccessExpression(functionCall)
            }
            override fun visitComponentCall(componentCall: FirComponentCall) {
                visitFunctionCall(componentCall)
            }
            override fun visitImplicitInvokeCall(implicitInvokeCall: FirImplicitInvokeCall) {
                visitFunctionCall(implicitInvokeCall)
            }
            override fun visitIntegerLiteralOperatorCall(integerLiteralOperatorCall: FirIntegerLiteralOperatorCall) {
                visitFunctionCall(integerLiteralOperatorCall)
            }
            override fun visitThisReceiverExpression(thisReceiverExpression: FirThisReceiverExpression) {
                visitQualifiedAccessExpression(thisReceiverExpression)
            }
            override fun visitSuperReceiverExpression(superReceiverExpression: FirSuperReceiverExpression) {
                visitQualifiedAccessExpression(superReceiverExpression)
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
        // TODO:
        //  blackhole in injection block <- ERROR
        //  benchmarkResult and returns in benchmarks <- ERROR
        //  INJECT_IN_INJECT <- WARNING (useless)
        //  inject/blackhole/benchmarkResult not in @Benchmark <- ERROR
        
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
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
                map.put(
                    BENCHMARK_CALL_INSIDE_BENCHMARK,
                    "{0} cannot be called inside benchmark ('@Benchmark'-annotated function) because it is benchmark itself.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
            }
        }
    }
}