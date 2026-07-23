/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.scope
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirBasicExpressionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.expressions.FirContextArgumentListOwner
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.builder.buildThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableReference
import org.jetbrains.kotlin.fir.references.builder.buildImplicitThisReference
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.visitors.FirDefaultTransformer


class FirContextArgumentCleaner(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val expressionCheckers: ExpressionCheckers get() = CleaningExpressionCheckers
    
    object CleaningExpressionCheckers : ExpressionCheckers() {
        override val basicExpressionCheckers: Set<FirBasicExpressionChecker> = setOf(CleaningExpressionChecker)
    }
    
    object CleaningExpressionChecker : FirBasicExpressionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(expression: FirStatement) {
            if (expression !is FirContextArgumentListOwner) return
            expression.transformContextArguments(CleaningTransformer, null)
        }
    }
    
    object CleaningTransformer : FirDefaultTransformer<Nothing?>() {
        override fun <E : FirElement> transformElement(element: E, data: Nothing?): E {
            element.transformChildren(this, data)
            return element
        }
        
        private val contextProvidingGeneratedDeclarationKeys = listOf(
            FirLocalContextsExpressionResolutionExtension.GeneratedReceiverFromLocalContextsFunctionKey,
            FirLocalUnwrapExpressionResolutionExtension.GeneratedReceiverFromLocalUnwrapFunctionKey,
            FirKoneLocalUnwrapExpressionResolutionExtension.GeneratedReceiverFromKoneLocalUnwrapFunctionKey,
        )
        
        override fun transformPropertyAccessExpression(
            propertyAccessExpression: FirPropertyAccessExpression,
            data: Nothing?,
        ): FirStatement {
            scope {
                val reference = propertyAccessExpression.toResolvedCallableReference() ?: return@scope
                val symbol = reference.resolvedSymbol as? FirValueParameterSymbol ?: return@scope
                if ((symbol.origin as? FirDeclarationOrigin.Plugin)?.key !in contextProvidingGeneratedDeclarationKeys) return@scope
                val fakeReceiver = symbol.fakeReceiver ?: return@scope
                return buildThisReceiverExpression {
                    coneTypeOrNull = fakeReceiver.typeRef.coneTypeOrNull
                    isImplicit = true
                    source = propertyAccessExpression.source
                    nonFatalDiagnostics.addAll(propertyAccessExpression.nonFatalDiagnostics)
                    calleeReference = buildImplicitThisReference {
                        boundSymbol = fakeReceiver.symbol
                    }
                }
            }
            return super.transformPropertyAccessExpression(
                propertyAccessExpression,
                data,
            )
        }
    }
}