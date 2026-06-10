/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion.fir

import dev.lounres.kone.plugin.fiktion.fiktionImaginaryClassId
import dev.lounres.kone.plugin.fiktion.fiktionRealClassId
import dev.lounres.kone.plugin.fiktion.fir.FiktionCheckersExtension.Companion.Imaginarity.Imaginary
import dev.lounres.kone.util.kotlinCompilerUtils.KtDiagnosticFactory1Delegate
import org.jetbrains.kotlin.diagnostics.AbstractKtDiagnosticFactory
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirElementWithResolveState
import org.jetbrains.kotlin.fir.FirFunctionTypeParameter
import org.jetbrains.kotlin.fir.FirLabel
import org.jetbrains.kotlin.fir.FirPackageDirective
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.FirTargetElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.cfa.FirControlFlowChecker
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirQualifiedAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.contracts.FirContractDescription
import org.jetbrains.kotlin.fir.contracts.FirContractElementDeclaration
import org.jetbrains.kotlin.fir.contracts.FirEffectDeclaration
import org.jetbrains.kotlin.fir.contracts.FirErrorContractDescription
import org.jetbrains.kotlin.fir.contracts.FirLazyContractDescription
import org.jetbrains.kotlin.fir.contracts.FirLegacyRawContractDescription
import org.jetbrains.kotlin.fir.contracts.FirRawContractDescription
import org.jetbrains.kotlin.fir.contracts.FirResolvedContractDescription
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirAnonymousInitializer
import org.jetbrains.kotlin.fir.declarations.FirAnonymousObject
import org.jetbrains.kotlin.fir.declarations.FirBackingField
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirCodeFragment
import org.jetbrains.kotlin.fir.declarations.FirConstructedClassTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirContractDescriptionOwner
import org.jetbrains.kotlin.fir.declarations.FirControlFlowGraphOwner
import org.jetbrains.kotlin.fir.declarations.FirDanglingModifierList
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.declarations.FirErrorFunction
import org.jetbrains.kotlin.fir.declarations.FirErrorPrimaryConstructor
import org.jetbrains.kotlin.fir.declarations.FirErrorProperty
import org.jetbrains.kotlin.fir.declarations.FirField
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirImport
import org.jetbrains.kotlin.fir.declarations.FirMemberDeclaration
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirOuterClassTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirReplSnippet
import org.jetbrains.kotlin.fir.declarations.FirResolvedDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirResolvedImport
import org.jetbrains.kotlin.fir.declarations.FirScript
import org.jetbrains.kotlin.fir.declarations.FirScriptReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirTypeAlias
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRefsOwner
import org.jetbrains.kotlin.fir.declarations.FirTypeParametersOwner
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.diagnostics.FirDiagnosticHolder
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirAnonymousObjectExpression
import org.jetbrains.kotlin.fir.expressions.FirArgumentList
import org.jetbrains.kotlin.fir.expressions.FirAugmentedAssignment
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirBooleanOperatorExpression
import org.jetbrains.kotlin.fir.expressions.FirBreakExpression
import org.jetbrains.kotlin.fir.expressions.FirCall
import org.jetbrains.kotlin.fir.expressions.FirCallableReferenceAccess
import org.jetbrains.kotlin.fir.expressions.FirCatch
import org.jetbrains.kotlin.fir.expressions.FirCheckNotNullCall
import org.jetbrains.kotlin.fir.expressions.FirCheckedSafeCallSubject
import org.jetbrains.kotlin.fir.expressions.FirClassReferenceExpression
import org.jetbrains.kotlin.fir.expressions.FirCollectionLiteral
import org.jetbrains.kotlin.fir.expressions.FirComparisonExpression
import org.jetbrains.kotlin.fir.expressions.FirComponentCall
import org.jetbrains.kotlin.fir.expressions.FirContextArgumentListOwner
import org.jetbrains.kotlin.fir.expressions.FirContinueExpression
import org.jetbrains.kotlin.fir.expressions.FirDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.FirDesugaredAssignmentValueReferenceExpression
import org.jetbrains.kotlin.fir.expressions.FirDoWhileLoop
import org.jetbrains.kotlin.fir.expressions.FirElvisExpression
import org.jetbrains.kotlin.fir.expressions.FirEnumEntryDeserializedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirEqualityOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirErrorAnnotationCall
import org.jetbrains.kotlin.fir.expressions.FirErrorExpression
import org.jetbrains.kotlin.fir.expressions.FirErrorLoop
import org.jetbrains.kotlin.fir.expressions.FirErrorResolvedQualifier
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirFunctionTypeConversionExpression
import org.jetbrains.kotlin.fir.expressions.FirGetClassCall
import org.jetbrains.kotlin.fir.expressions.FirImplicitInvokeCall
import org.jetbrains.kotlin.fir.expressions.FirInaccessibleReceiverExpression
import org.jetbrains.kotlin.fir.expressions.FirIncrementDecrementExpression
import org.jetbrains.kotlin.fir.expressions.FirIndexedAccessAugmentedAssignment
import org.jetbrains.kotlin.fir.expressions.FirIntegerLiteralOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirJump
import org.jetbrains.kotlin.fir.expressions.FirLazyBlock
import org.jetbrains.kotlin.fir.expressions.FirLazyExpression
import org.jetbrains.kotlin.fir.expressions.FirLiteralExpression
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirLoopJump
import org.jetbrains.kotlin.fir.expressions.FirMultiDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.FirNamedArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirQualifiedErrorAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirQualifierWithContextSensitiveAlternative
import org.jetbrains.kotlin.fir.expressions.FirReplDeclarationReference
import org.jetbrains.kotlin.fir.expressions.FirReplExpressionReference
import org.jetbrains.kotlin.fir.expressions.FirReplPropertyDelegate
import org.jetbrains.kotlin.fir.expressions.FirReplPropertyInitializer
import org.jetbrains.kotlin.fir.expressions.FirResolvable
import org.jetbrains.kotlin.fir.expressions.FirResolvedQualifier
import org.jetbrains.kotlin.fir.expressions.FirResolvedReifiedParameterReference
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirSafeCallExpression
import org.jetbrains.kotlin.fir.expressions.FirSmartCastExpression
import org.jetbrains.kotlin.fir.expressions.FirSpreadArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirStringConcatenationCall
import org.jetbrains.kotlin.fir.expressions.FirSuperReceiverExpression
import org.jetbrains.kotlin.fir.expressions.FirThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.FirThrowExpression
import org.jetbrains.kotlin.fir.expressions.FirTryExpression
import org.jetbrains.kotlin.fir.expressions.FirTypeOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.FirVariableAssignment
import org.jetbrains.kotlin.fir.expressions.FirWhenBranch
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.expressions.FirWhenSubjectExpression
import org.jetbrains.kotlin.fir.expressions.FirWhileLoop
import org.jetbrains.kotlin.fir.expressions.FirWrappedArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirWrappedDelegateExpression
import org.jetbrains.kotlin.fir.expressions.FirWrappedExpression
import org.jetbrains.kotlin.fir.expressions.argument
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.FirBackingFieldReference
import org.jetbrains.kotlin.fir.references.FirControlFlowGraphReference
import org.jetbrains.kotlin.fir.references.FirDelegateFieldReference
import org.jetbrains.kotlin.fir.references.FirErrorNamedReference
import org.jetbrains.kotlin.fir.references.FirErrorSuperReference
import org.jetbrains.kotlin.fir.references.FirNamedReference
import org.jetbrains.kotlin.fir.references.FirNamedReferenceWithCandidateBase
import org.jetbrains.kotlin.fir.references.FirPropertyWithExplicitBackingFieldResolvedNamedReference
import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.references.FirResolvedCallableReference
import org.jetbrains.kotlin.fir.references.FirResolvedErrorReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.references.FirSuperReference
import org.jetbrains.kotlin.fir.references.FirThisReference
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ControlFlowGraph
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.FirDynamicTypeRef
import org.jetbrains.kotlin.fir.types.FirErrorTypeRef
import org.jetbrains.kotlin.fir.types.FirFunctionTypeRef
import org.jetbrains.kotlin.fir.types.FirImplicitTypeRef
import org.jetbrains.kotlin.fir.types.FirIntersectionTypeRef
import org.jetbrains.kotlin.fir.types.FirPlaceholderProjection
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirStarProjection
import org.jetbrains.kotlin.fir.types.FirTypeProjection
import org.jetbrains.kotlin.fir.types.FirTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.FirUnresolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirUserTypeRef
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.utils.addToStdlib.shouldNotBeCalled


class FiktionCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    companion object {
        context(context: CheckerContext)
        private fun AbstractKtDiagnosticFactory.isSuppressed(): Boolean {
            if (name == FirErrors.ERROR_SUPPRESSION.name) {
                // Can't suppress warning about suppressed error
                return false
            }
            
            val suppressedByAll = when (severity) {
                Severity.INFO -> context.allInfosSuppressed
                Severity.WARNING, Severity.STRONG_WARNING -> context.allWarningsSuppressed
                Severity.ERROR -> context.allErrorsSuppressed
                // diagnostic factory cannot have FIXED_WARNING severity
                Severity.FIXED_WARNING -> shouldNotBeCalled()
            }
            
            return suppressedByAll || name in context.suppressedDiagnostics
        }
        
        context(context: CheckerContext)
        private val FirBasedSymbol<*>.isImaginary: Boolean get() = hasAnnotation(fiktionImaginaryClassId, context.session)
        context(context: CheckerContext)
        private val FirAnnotationContainer.isImaginary: Boolean get() = hasAnnotation(fiktionImaginaryClassId, context.session)
        context(context: CheckerContext)
        private val FirBasedSymbol<*>.isReal: Boolean get() = hasAnnotation(fiktionRealClassId, context.session)
        context(context: CheckerContext)
        private val FirAnnotationContainer.isReal: Boolean get() = hasAnnotation(fiktionRealClassId, context.session)
//        context(context: CheckerContext)
//        private val FirTypeParameterSymbol.isSupply: Boolean get() = hasAnnotation(supplyClassId, context.session)
//        context(context: CheckerContext)
//        private val FirNamedFunctionSymbol.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId, context.session)
//        context(context: CheckerContext)
//        private val FirClassSymbol<*>.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId, context.session)
//        context(context: CheckerContext)
//        private val FirNamedFunctionSymbol.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedClassId, context.session)
//        context(context: CheckerContext)
//        private val FirConstructorSymbol.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedClassId, context.session)
        
        private enum class Imaginarity {
            Imaginary, Real;
        }
        
        private object ImaginarityVisitor : FirVisitor<Imaginarity?, CheckerContext>() {
            private fun decideByAnnotations(annotationContainer: FirAnnotationContainer, data: CheckerContext): Imaginarity? = when {
                context(data) { annotationContainer.isImaginary } -> Imaginary
                context(data) { annotationContainer.isReal } -> Real
                else -> null
            }
            
            override fun visitElement(
                element: FirElement,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitAnnotationContainer(
                annotationContainer: FirAnnotationContainer,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(annotationContainer, data)
            
            override fun visitTypeParameterRef(
                typeParameterRef: FirTypeParameterRef,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitTypeParametersOwner(
                typeParametersOwner: FirTypeParametersOwner,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitTypeParameterRefsOwner(
                typeParameterRefsOwner: FirTypeParameterRefsOwner,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitResolvable(
                resolvable: FirResolvable,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitDiagnosticHolder(
                diagnosticHolder: FirDiagnosticHolder,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitControlFlowGraphOwner(
                controlFlowGraphOwner: FirControlFlowGraphOwner,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitElementWithResolveState(
                elementWithResolveState: FirElementWithResolveState,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitDeclaration(
                declaration: FirDeclaration,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(declaration, data)
            
            override fun visitCallableDeclaration(
                callableDeclaration: FirCallableDeclaration,
                data: CheckerContext,
            ): Imaginarity? = visitMemberDeclaration(callableDeclaration, data)
            
            override fun visitFunction(
                function: FirFunction,
                data: CheckerContext,
            ): Imaginarity? = visitCallableDeclaration(function, data)
            
            override fun visitErrorExpression(
                errorExpression: FirErrorExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(errorExpression, data)
            
            override fun visitErrorFunction(
                errorFunction: FirErrorFunction,
                data: CheckerContext,
            ): Imaginarity? = visitFunction(errorFunction, data)
            
            override fun visitMemberDeclaration(
                memberDeclaration: FirMemberDeclaration,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(memberDeclaration, data)
            
            override fun visitStatement(
                statement: FirStatement,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(statement, data)
            
            override fun visitExpression(
                expression: FirExpression,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(expression, data)
            
            override fun visitLazyExpression(
                lazyExpression: FirLazyExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(lazyExpression, data)
            
            override fun visitArgumentList(
                argumentList: FirArgumentList,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitCall(
                call: FirCall,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitBlock(
                block: FirBlock,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(block, data) ?: if (block.statements.any { it.accept(this, data) == Imaginary }) Imaginary else null
            
            override fun visitLazyBlock(
                lazyBlock: FirLazyBlock,
                data: CheckerContext,
            ): Imaginarity? = visitBlock(lazyBlock, data)
            
            override fun visitBooleanOperatorExpression(
                booleanOperatorExpression: FirBooleanOperatorExpression,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(booleanOperatorExpression, data)
                ?: if (booleanOperatorExpression.leftOperand.accept(this, data) == Imaginary || booleanOperatorExpression.rightOperand.accept(this, data) == Imaginary) Imaginary else null
            
            override fun visitTargetElement(
                targetElement: FirTargetElement,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun <E : FirTargetElement> visitJump( // TODO: Think about marking jumps Imaginary as well
                jump: FirJump<E>,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(jump, data)
            
            override fun visitLoopJump(
                loopJump: FirLoopJump,
                data: CheckerContext,
            ): Imaginarity? = visitJump(loopJump, data)
            
            override fun visitBreakExpression(
                breakExpression: FirBreakExpression,
                data: CheckerContext,
            ): Imaginarity? = visitLoopJump(breakExpression, data)
            
            override fun visitContinueExpression(
                continueExpression: FirContinueExpression,
                data: CheckerContext,
            ): Imaginarity? = visitLoopJump(continueExpression, data)
            
            override fun visitReturnExpression(
                returnExpression: FirReturnExpression,
                data: CheckerContext,
            ): Imaginarity? = visitJump(returnExpression, data)
            
            override fun visitLabel(
                label: FirLabel,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitLoop(
                loop: FirLoop,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(loop, data) ?: loop.condition.accept(this, data)
            
            override fun visitWhileLoop(
                whileLoop: FirWhileLoop,
                data: CheckerContext,
            ): Imaginarity? = visitLoop(whileLoop, data)
            
            override fun visitDoWhileLoop(
                doWhileLoop: FirDoWhileLoop,
                data: CheckerContext,
            ): Imaginarity? = visitLoop(doWhileLoop, data)
            
            override fun visitErrorLoop(
                errorLoop: FirErrorLoop,
                data: CheckerContext,
            ): Imaginarity? = visitLoop(errorLoop, data)
            
            override fun visitCatch(
                catch: FirCatch,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitTryExpression(
                tryExpression: FirTryExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(tryExpression, data)
            
            override fun visitElvisExpression(
                elvisExpression: FirElvisExpression,
                data: CheckerContext,
            ): Imaginarity? = decideByAnnotations(elvisExpression, data)
                ?: if (elvisExpression.lhs.accept(this, data) == Imaginary || elvisExpression.rhs.accept(this, data) == Imaginary) Imaginary else null
            
            override fun visitContextArgumentListOwner(
                contextArgumentListOwner: FirContextArgumentListOwner,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitQualifiedAccessExpression(
                qualifiedAccessExpression: FirQualifiedAccessExpression,
                data: CheckerContext,
            ): Imaginarity? {
                val calleeReference = qualifiedAccessExpression.calleeReference as? FirResolvedNamedReference ?: return decideByAnnotations(qualifiedAccessExpression, data)
                @OptIn(SymbolInternals::class)
                val callee = calleeReference.resolvedSymbol.fir
                decideByAnnotations(callee, data)?.let { return it }
                decideByAnnotations(qualifiedAccessExpression, data)?.let { return it }
                return when (callee) {
                    is FirAnonymousInitializer -> null
                    is FirCodeFragment -> null
                    is FirDanglingModifierList -> null
                    is FirFile -> null
                    is FirAnonymousFunction -> {
                        qualifiedAccessExpression as FirFunctionCall
                        for ([index, parameter] in callee.contextParameters.withIndex())
                            if (context(data) { !parameter.isImaginary } && qualifiedAccessExpression.contextArguments[index].accept(this, data) == Imaginary) return Imaginary
                        
                        val receiverParameter = callee.receiverParameter
                        if (receiverParameter != null && context(data) { !receiverParameter.isImaginary } && qualifiedAccessExpression.extensionReceiver!!.accept(this, data) == Imaginary) return Imaginary
                        
                        for ([index, parameter] in callee.valueParameters.withIndex())
                            if (context(data) { !parameter.isImaginary } && qualifiedAccessExpression.arguments[index].accept(this, data) == Imaginary) return Imaginary
                        
                        null
                    }
                    is FirConstructor -> TODO()
                    is FirErrorFunction -> TODO()
                    is FirPropertyAccessor -> TODO()
                    is FirBackingField -> TODO()
                    is FirEnumEntry -> TODO()
                    is FirField -> TODO()
                    is FirProperty -> TODO()
                    is FirValueParameter -> TODO()
                    is FirAnonymousObject -> TODO()
                    is FirRegularClass -> TODO()
                    is FirTypeAlias -> TODO()
                    is FirReceiverParameter -> TODO()
                    is FirReplSnippet -> TODO()
                    is FirScript -> TODO()
                    is FirTypeParameter -> TODO()
                    is FirNamedFunction -> TODO()
                }
            }
            
            override fun visitQualifiedErrorAccessExpression(
                qualifiedErrorAccessExpression: FirQualifiedErrorAccessExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(qualifiedErrorAccessExpression, data)
            
            override fun visitLiteralExpression(
                literalExpression: FirLiteralExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(literalExpression, data)
            
            override fun visitFunctionCall(
                functionCall: FirFunctionCall,
                data: CheckerContext,
            ): Imaginarity? = visitQualifiedAccessExpression(functionCall, data)
            
            override fun visitIntegerLiteralOperatorCall(
                integerLiteralOperatorCall: FirIntegerLiteralOperatorCall,
                data: CheckerContext,
            ): Imaginarity? = visitFunctionCall(integerLiteralOperatorCall, data)
            
            override fun visitCollectionLiteral( // TODO
                collectionLiteral: FirCollectionLiteral,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(collectionLiteral, data)
            
            override fun visitCheckNotNullCall( // TODO
                checkNotNullCall: FirCheckNotNullCall,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(checkNotNullCall, data)
            
            override fun visitComparisonExpression(
                comparisonExpression: FirComparisonExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(comparisonExpression, data)
            
            override fun visitTypeOperatorCall(
                typeOperatorCall: FirTypeOperatorCall,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(typeOperatorCall, data)
            
            override fun visitAugmentedAssignment(
                augmentedAssignment: FirAugmentedAssignment,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitIncrementDecrementExpression(
                incrementDecrementExpression: FirIncrementDecrementExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(incrementDecrementExpression, data)
            
            override fun visitEqualityOperatorCall(
                equalityOperatorCall: FirEqualityOperatorCall,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(equalityOperatorCall, data)
            
            override fun visitWhenBranch(
                whenBranch: FirWhenBranch,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitClassLikeDeclaration(
                classLikeDeclaration: FirClassLikeDeclaration,
                data: CheckerContext,
            ): Imaginarity? = visitMemberDeclaration(classLikeDeclaration, data)
            
            override fun visitClass(
                klass: FirClass,
                data: CheckerContext,
            ): Imaginarity? = visitClassLikeDeclaration(klass, data)
            
            override fun visitRegularClass(
                regularClass: FirRegularClass,
                data: CheckerContext,
            ): Imaginarity? = visitClass(regularClass, data)
            
            override fun visitAnonymousObject(
                anonymousObject: FirAnonymousObject,
                data: CheckerContext,
            ): Imaginarity? = visitClass(anonymousObject, data)
            
            override fun visitAnonymousObjectExpression(
                anonymousObjectExpression: FirAnonymousObjectExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(anonymousObjectExpression, data)
            
            override fun visitTypeAlias(
                typeAlias: FirTypeAlias,
                data: CheckerContext,
            ): Imaginarity? = visitClassLikeDeclaration(typeAlias, data)
            
            override fun visitAnonymousFunction(
                anonymousFunction: FirAnonymousFunction,
                data: CheckerContext,
            ): Imaginarity? = visitFunction(anonymousFunction, data)
            
            override fun visitAnonymousFunctionExpression(
                anonymousFunctionExpression: FirAnonymousFunctionExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(anonymousFunctionExpression, data)
            
            override fun visitTypeParameter(
                typeParameter: FirTypeParameter,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(typeParameter, data)
            
            override fun visitConstructedClassTypeParameterRef(
                constructedClassTypeParameterRef: FirConstructedClassTypeParameterRef,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitOuterClassTypeParameterRef(
                outerClassTypeParameterRef: FirOuterClassTypeParameterRef,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitNamedFunction(
                namedFunction: FirNamedFunction,
                data: CheckerContext,
            ): Imaginarity? = visitFunction(namedFunction, data)
            
            override fun visitContractDescriptionOwner(
                contractDescriptionOwner: FirContractDescriptionOwner,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitProperty(
                property: FirProperty,
                data: CheckerContext,
            ): Imaginarity? = visitVariable(property, data)
            
            override fun visitPropertyAccessor(
                propertyAccessor: FirPropertyAccessor,
                data: CheckerContext,
            ): Imaginarity? = visitFunction(propertyAccessor, data)
            
            override fun visitBackingField(
                backingField: FirBackingField,
                data: CheckerContext,
            ): Imaginarity? = visitVariable(backingField, data)
            
            override fun visitDeclarationStatus(
                declarationStatus: FirDeclarationStatus,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitResolvedDeclarationStatus(
                resolvedDeclarationStatus: FirResolvedDeclarationStatus,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitImplicitInvokeCall(
                implicitInvokeCall: FirImplicitInvokeCall,
                data: CheckerContext,
            ): Imaginarity? = visitFunctionCall(implicitInvokeCall, data)
            
            override fun visitConstructor(
                constructor: FirConstructor,
                data: CheckerContext,
            ): Imaginarity? = visitFunction(constructor, data)
            
            override fun visitErrorPrimaryConstructor(
                errorPrimaryConstructor: FirErrorPrimaryConstructor,
                data: CheckerContext,
            ): Imaginarity? = visitConstructor(errorPrimaryConstructor, data)
            
            override fun visitDelegatedConstructorCall(
                delegatedConstructorCall: FirDelegatedConstructorCall,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(delegatedConstructorCall, data)
            
            override fun visitMultiDelegatedConstructorCall(
                multiDelegatedConstructorCall: FirMultiDelegatedConstructorCall,
                data: CheckerContext,
            ): Imaginarity? = visitDelegatedConstructorCall(multiDelegatedConstructorCall, data)
            
            override fun visitValueParameter(
                valueParameter: FirValueParameter,
                data: CheckerContext,
            ): Imaginarity? = visitVariable(valueParameter, data)
            
            override fun visitReceiverParameter(
                receiverParameter: FirReceiverParameter,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(receiverParameter, data)
            
            override fun visitScriptReceiverParameter(
                scriptReceiverParameter: FirScriptReceiverParameter,
                data: CheckerContext,
            ): Imaginarity? = visitReceiverParameter(scriptReceiverParameter, data)
            
            override fun visitVariable(
                variable: FirVariable,
                data: CheckerContext,
            ): Imaginarity? = visitCallableDeclaration(variable, data)
            
            override fun visitFunctionTypeParameter(
                functionTypeParameter: FirFunctionTypeParameter,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitErrorProperty(
                errorProperty: FirErrorProperty,
                data: CheckerContext,
            ): Imaginarity? = visitProperty(errorProperty, data)
            
            override fun visitEnumEntry(
                enumEntry: FirEnumEntry,
                data: CheckerContext,
            ): Imaginarity? = visitVariable(enumEntry, data)
            
            override fun visitField(
                field: FirField,
                data: CheckerContext,
            ): Imaginarity? = visitVariable(field, data)
            
            override fun visitAnonymousInitializer(
                anonymousInitializer: FirAnonymousInitializer,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(anonymousInitializer, data)
            
            override fun visitDanglingModifierList(
                danglingModifierList: FirDanglingModifierList,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(danglingModifierList, data)
            
            override fun visitFile(
                file: FirFile,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(file, data)
            
            override fun visitScript(
                script: FirScript,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(script, data)
            
            override fun visitCodeFragment(
                codeFragment: FirCodeFragment,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(codeFragment, data)
            
            override fun visitReplSnippet(
                replSnippet: FirReplSnippet,
                data: CheckerContext,
            ): Imaginarity? = visitDeclaration(replSnippet, data)
            
            override fun visitReplDeclarationReference(
                replDeclarationReference: FirReplDeclarationReference,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitReplExpressionReference(
                replExpressionReference: FirReplExpressionReference,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(replExpressionReference, data)
            
            override fun visitReplPropertyInitializer(
                replPropertyInitializer: FirReplPropertyInitializer,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitReplPropertyDelegate(
                replPropertyDelegate: FirReplPropertyDelegate,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitPackageDirective(
                packageDirective: FirPackageDirective,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitImport(
                import: FirImport,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitResolvedImport(
                resolvedImport: FirResolvedImport,
                data: CheckerContext,
            ): Imaginarity? = visitImport(resolvedImport, data)
            
            override fun visitAnnotation(
                annotation: FirAnnotation,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(annotation, data)
            
            override fun visitAnnotationCall(
                annotationCall: FirAnnotationCall,
                data: CheckerContext,
            ): Imaginarity? = visitAnnotation(annotationCall, data)
            
            override fun visitErrorAnnotationCall(
                errorAnnotationCall: FirErrorAnnotationCall,
                data: CheckerContext,
            ): Imaginarity? = visitAnnotationCall(errorAnnotationCall, data)
            
            override fun visitAnnotationArgumentMapping(
                annotationArgumentMapping: FirAnnotationArgumentMapping,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitIndexedAccessAugmentedAssignment(
                indexedAccessAugmentedAssignment: FirIndexedAccessAugmentedAssignment,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitClassReferenceExpression(
                classReferenceExpression: FirClassReferenceExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(classReferenceExpression, data)
            
            override fun visitComponentCall(
                componentCall: FirComponentCall,
                data: CheckerContext,
            ): Imaginarity? = visitFunctionCall(componentCall, data)
            
            override fun visitSmartCastExpression(
                smartCastExpression: FirSmartCastExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(smartCastExpression, data)
            
            override fun visitSafeCallExpression(
                safeCallExpression: FirSafeCallExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(safeCallExpression, data)
            
            override fun visitCheckedSafeCallSubject(
                checkedSafeCallSubject: FirCheckedSafeCallSubject,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(checkedSafeCallSubject, data)
            
            override fun visitCallableReferenceAccess(
                callableReferenceAccess: FirCallableReferenceAccess,
                data: CheckerContext,
            ): Imaginarity? = visitQualifiedAccessExpression(callableReferenceAccess, data)
            
            override fun visitQualifierWithContextSensitiveAlternative(
                qualifierWithContextSensitiveAlternative: FirQualifierWithContextSensitiveAlternative,
                data: CheckerContext,
            ): Imaginarity = error("Defined on interface")
            
            override fun visitPropertyAccessExpression(
                propertyAccessExpression: FirPropertyAccessExpression,
                data: CheckerContext,
            ): Imaginarity? = visitQualifiedAccessExpression(propertyAccessExpression, data)
            
            override fun visitGetClassCall(
                getClassCall: FirGetClassCall,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(getClassCall, data)
            
            override fun visitWrappedArgumentExpression(
                wrappedArgumentExpression: FirWrappedArgumentExpression,
                data: CheckerContext,
            ): Imaginarity? = visitWrappedExpression(wrappedArgumentExpression, data)
            
            override fun visitSpreadArgumentExpression(
                spreadArgumentExpression: FirSpreadArgumentExpression,
                data: CheckerContext,
            ): Imaginarity? = visitWrappedArgumentExpression(spreadArgumentExpression, data)
            
            override fun visitNamedArgumentExpression(
                namedArgumentExpression: FirNamedArgumentExpression,
                data: CheckerContext,
            ): Imaginarity? = visitWrappedArgumentExpression(namedArgumentExpression, data)
            
            override fun visitVarargArgumentsExpression(
                varargArgumentsExpression: FirVarargArgumentsExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(varargArgumentsExpression, data)
            
            override fun visitFunctionTypeConversionExpression(
                functionTypeConversionExpression: FirFunctionTypeConversionExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(functionTypeConversionExpression, data)
            
            override fun visitResolvedQualifier(
                resolvedQualifier: FirResolvedQualifier,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(resolvedQualifier, data)
            
            override fun visitErrorResolvedQualifier(
                errorResolvedQualifier: FirErrorResolvedQualifier,
                data: CheckerContext,
            ): Imaginarity? = visitResolvedQualifier(errorResolvedQualifier, data)
            
            override fun visitResolvedReifiedParameterReference(
                resolvedReifiedParameterReference: FirResolvedReifiedParameterReference,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(resolvedReifiedParameterReference, data)
            
            override fun visitStringConcatenationCall(
                stringConcatenationCall: FirStringConcatenationCall,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(stringConcatenationCall, data)
            
            override fun visitThrowExpression(
                throwExpression: FirThrowExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(throwExpression, data)
            
            override fun visitVariableAssignment(
                variableAssignment: FirVariableAssignment,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitWhenSubjectExpression(
                whenSubjectExpression: FirWhenSubjectExpression,
                data: CheckerContext,
            ): Imaginarity? = visitPropertyAccessExpression(whenSubjectExpression, data)
            
            override fun visitDesugaredAssignmentValueReferenceExpression(
                desugaredAssignmentValueReferenceExpression: FirDesugaredAssignmentValueReferenceExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(desugaredAssignmentValueReferenceExpression, data)
            
            override fun visitWrappedExpression(
                wrappedExpression: FirWrappedExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(wrappedExpression, data)
            
            override fun visitWrappedDelegateExpression(
                wrappedDelegateExpression: FirWrappedDelegateExpression,
                data: CheckerContext,
            ): Imaginarity? = visitWrappedExpression(wrappedDelegateExpression, data)
            
            override fun visitEnumEntryDeserializedAccessExpression(
                enumEntryDeserializedAccessExpression: FirEnumEntryDeserializedAccessExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(enumEntryDeserializedAccessExpression, data)
            
            override fun visitReference(
                reference: FirReference,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitNamedReference(
                namedReference: FirNamedReference,
                data: CheckerContext,
            ): Imaginarity? = visitReference(namedReference, data)
            
            override fun visitNamedReferenceWithCandidateBase(
                namedReferenceWithCandidateBase: FirNamedReferenceWithCandidateBase,
                data: CheckerContext,
            ): Imaginarity? = visitNamedReference(namedReferenceWithCandidateBase, data)
            
            override fun visitResolvedNamedReference(
                resolvedNamedReference: FirResolvedNamedReference,
                data: CheckerContext,
            ): Imaginarity? = visitNamedReference(resolvedNamedReference, data)
            
            override fun visitPropertyWithExplicitBackingFieldResolvedNamedReference(
                propertyWithExplicitBackingFieldResolvedNamedReference: FirPropertyWithExplicitBackingFieldResolvedNamedReference,
                data: CheckerContext,
            ): Imaginarity? = visitResolvedNamedReference(propertyWithExplicitBackingFieldResolvedNamedReference, data)
            
            override fun visitResolvedCallableReference(
                resolvedCallableReference: FirResolvedCallableReference,
                data: CheckerContext,
            ): Imaginarity? = visitResolvedNamedReference(resolvedCallableReference, data)
            
            override fun visitDelegateFieldReference(
                delegateFieldReference: FirDelegateFieldReference,
                data: CheckerContext,
            ): Imaginarity? = visitResolvedNamedReference(delegateFieldReference, data)
            
            override fun visitBackingFieldReference(
                backingFieldReference: FirBackingFieldReference,
                data: CheckerContext,
            ): Imaginarity? = visitResolvedNamedReference(backingFieldReference, data)
            
            override fun visitSuperReference(
                superReference: FirSuperReference,
                data: CheckerContext,
            ): Imaginarity? = visitReference(superReference, data)
            
            override fun visitThisReference(
                thisReference: FirThisReference,
                data: CheckerContext,
            ): Imaginarity? = visitReference(thisReference, data)
            
            override fun visitControlFlowGraphReference(
                controlFlowGraphReference: FirControlFlowGraphReference,
                data: CheckerContext,
            ): Imaginarity? = visitReference(controlFlowGraphReference, data)
            
            override fun visitTypeRef(
                typeRef: FirTypeRef,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitResolvedTypeRef(
                resolvedTypeRef: FirResolvedTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitTypeRef(resolvedTypeRef, data)
            
            override fun visitUnresolvedTypeRef(
                unresolvedTypeRef: FirUnresolvedTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitTypeRef(unresolvedTypeRef, data)
            
            override fun visitUserTypeRef(
                userTypeRef: FirUserTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitUnresolvedTypeRef(userTypeRef, data)
            
            override fun visitFunctionTypeRef(
                functionTypeRef: FirFunctionTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitUnresolvedTypeRef(functionTypeRef, data)
            
            override fun visitDynamicTypeRef(
                dynamicTypeRef: FirDynamicTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitUnresolvedTypeRef(dynamicTypeRef, data)
            
            override fun visitImplicitTypeRef(
                implicitTypeRef: FirImplicitTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitTypeRef(implicitTypeRef, data)
            
            override fun visitErrorTypeRef(
                errorTypeRef: FirErrorTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitResolvedTypeRef(errorTypeRef, data)
            
            override fun visitResolvedErrorReference(
                resolvedErrorReference: FirResolvedErrorReference,
                data: CheckerContext,
            ): Imaginarity? = visitResolvedNamedReference(resolvedErrorReference, data)
            
            override fun visitErrorNamedReference(
                errorNamedReference: FirErrorNamedReference,
                data: CheckerContext,
            ): Imaginarity? = visitNamedReference(errorNamedReference, data)
            
            override fun visitErrorSuperReference(
                errorSuperReference: FirErrorSuperReference,
                data: CheckerContext,
            ): Imaginarity? = visitSuperReference(errorSuperReference, data)
            
            override fun visitIntersectionTypeRef(
                intersectionTypeRef: FirIntersectionTypeRef,
                data: CheckerContext,
            ): Imaginarity? = visitUnresolvedTypeRef(intersectionTypeRef, data)
            
            override fun visitThisReceiverExpression(
                thisReceiverExpression: FirThisReceiverExpression,
                data: CheckerContext,
            ): Imaginarity? = visitQualifiedAccessExpression(thisReceiverExpression, data)
            
            override fun visitSuperReceiverExpression(
                superReceiverExpression: FirSuperReceiverExpression,
                data: CheckerContext,
            ): Imaginarity? = visitQualifiedAccessExpression(superReceiverExpression, data)
            
            override fun visitInaccessibleReceiverExpression(
                inaccessibleReceiverExpression: FirInaccessibleReceiverExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(inaccessibleReceiverExpression, data)
            
            override fun visitWhenExpression(
                whenExpression: FirWhenExpression,
                data: CheckerContext,
            ): Imaginarity? = visitExpression(whenExpression, data)
            
            override fun visitTypeProjection(
                typeProjection: FirTypeProjection,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitTypeProjectionWithVariance(
                typeProjectionWithVariance: FirTypeProjectionWithVariance,
                data: CheckerContext,
            ): Imaginarity? = visitTypeProjection(typeProjectionWithVariance, data)
            
            override fun visitStarProjection(
                starProjection: FirStarProjection,
                data: CheckerContext,
            ): Imaginarity? = visitTypeProjection(starProjection, data)
            
            override fun visitPlaceholderProjection(
                placeholderProjection: FirPlaceholderProjection,
                data: CheckerContext,
            ): Imaginarity? = visitTypeProjection(placeholderProjection, data)
            
            override fun visitContractElementDeclaration(
                contractElementDeclaration: FirContractElementDeclaration,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitEffectDeclaration(
                effectDeclaration: FirEffectDeclaration,
                data: CheckerContext,
            ): Imaginarity? = visitContractElementDeclaration(effectDeclaration, data)
            
            override fun visitContractDescription(
                contractDescription: FirContractDescription,
                data: CheckerContext,
            ): Imaginarity? = null
            
            override fun visitRawContractDescription(
                rawContractDescription: FirRawContractDescription,
                data: CheckerContext,
            ): Imaginarity? = visitContractDescription(rawContractDescription, data)
            
            override fun visitResolvedContractDescription(
                resolvedContractDescription: FirResolvedContractDescription,
                data: CheckerContext,
            ): Imaginarity? = visitContractDescription(resolvedContractDescription, data)
            
            override fun visitLegacyRawContractDescription(
                legacyRawContractDescription: FirLegacyRawContractDescription,
                data: CheckerContext,
            ): Imaginarity? = visitContractDescription(legacyRawContractDescription, data)
            
            override fun visitLazyContractDescription(
                lazyContractDescription: FirLazyContractDescription,
                data: CheckerContext,
            ): Imaginarity? = visitLegacyRawContractDescription(lazyContractDescription, data)
            
            override fun visitErrorContractDescription(
                errorContractDescription: FirErrorContractDescription,
                data: CheckerContext,
            ): Imaginarity? = visitContractDescription(errorContractDescription, data)
        }
        
        context(context: CheckerContext)
        private val FirElement.imaginarity: Imaginarity? get() = accept(ImaginarityVisitor, context)
        
        context(context: CheckerContext)
        private val contextImaginarity: Imaginarity?
            get() {
                val stack = context.containingElements.dropLast(1)
                for (i in stack.indices.reversed()) {
                    val imaginarity = stack[i].imaginarity
                    if (imaginarity != null) return imaginarity
                }
                return null
            }
    }
    
    override val declarationCheckers: DeclarationCheckers get() = FiktionDeclarationCheckers
    override val expressionCheckers: ExpressionCheckers get() = FiktionExpressionCheckers

    object FiktionDeclarationCheckers : DeclarationCheckers() {
        override val controlFlowAnalyserCheckers: Set<FirControlFlowChecker> = setOf(
//            AtomicCallableControlFlowChecker,
        )
    }

    object FiktionExpressionCheckers : ExpressionCheckers() {
        override val qualifiedAccessExpressionCheckers: Set<FirQualifiedAccessExpressionChecker> = setOf(
            ImaginaryCallableRealAccessChecker
        )
    }
    
    object ImaginaryCallableRealAccessChecker : FirQualifiedAccessExpressionChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(expression: FirQualifiedAccessExpression) {
            if (Errors.FIKTION_IMAGINARY_CALLABLE_REAL_CALL.isSuppressed()) return
            if (contextImaginarity == Imaginary) return
            when (val calleeReference = expression.calleeReference) {
                is FirResolvedNamedReference -> if (calleeReference.resolvedSymbol.isImaginary) {
                    reporter.reportOn(
                        source = calleeReference.source,
                        factory = Errors.FIKTION_IMAGINARY_CALLABLE_REAL_CALL,
                        a = calleeReference.resolvedSymbol,
                    )
                }
                else -> { /* TODO: Log me!!! */ }
            }
        }
    }
    
    object AtomicCallableControlFlowChecker : FirControlFlowChecker(MppCheckerKind.Common) {
        context(reporter: DiagnosticReporter, context: CheckerContext)
        override fun analyze(graph: ControlFlowGraph) {
            val declaration = graph.declaration
            TODO("Not yet implemented")
        }
    }
    
    object Errors : KtDiagnosticsContainer() {
        // Imaginary
        val FIKTION_IMAGINARY_CALLABLE_REAL_CALL by KtDiagnosticFactory1Delegate.ERROR<FirBasedSymbol<*>>()
        
        // Atomic
//        val FIKTION_ATOMIC_CALLABLE_CONTAINS_NON_ATOMIC_CALL by KtDiagnosticFactory2Delegate.ERROR<FirFunctionSymbol<*>, FirCall>()
//        val FIKTION_ATOMIC_CALLABLE_CONTAINS_SEVERAL_ATOMIC_CALLS by KtDiagnosticFactory2Delegate.ERROR<FirFunctionSymbol<*>, FirCall>()
//        val FIKTION_ATOMIC_CALLABLE_CONTAINS_NO_ATOMIC_CALL by KtDiagnosticFactory1Delegate.WARNING<FirFunctionSymbol<*>>()
        
        override fun getRendererFactory(): BaseDiagnosticRendererFactory = DefaultMessages
        
        object DefaultMessages : BaseDiagnosticRendererFactory() {
            override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("FIKTION") { map ->
                // Imaginary
                map.put(
                    FIKTION_IMAGINARY_CALLABLE_REAL_CALL,
                    "{0} is marked as imaginary (via '@Fiktion.Imaginary' annotation), but is called from real code.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
                
                // Atomic
//                map.put(
//                    FIKTION_ATOMIC_CALLABLE_CONTAINS_NON_ATOMIC_CALL,
//                    "{0} is marked as atomic (via '@FiktionScope.Atomic' annotation), but contains non-atomic call.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                    null,
//                )
//                map.put(
//                    FIKTION_ATOMIC_CALLABLE_CONTAINS_SEVERAL_ATOMIC_CALLS,
//                    "{0} is marked as atomic (via '@FiktionScope.Atomic' annotation), but contains several atomic calls.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                    null,
//                )
//                map.put(
//                    FIKTION_ATOMIC_CALLABLE_CONTAINS_NO_ATOMIC_CALL,
//                    "{0} is marked as atomic (via '@FiktionScope.Atomic' annotation), but contains no atomic call.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                )
            }
        }
    }
}