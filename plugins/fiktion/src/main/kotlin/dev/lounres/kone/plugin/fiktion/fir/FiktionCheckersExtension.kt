/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion.fir

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory2
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.cfa.FirControlFlowChecker
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.expressions.FirCall
import org.jetbrains.kotlin.fir.resolve.dfa.cfg.ControlFlowGraph
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.psi.KtElement


class FiktionCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    companion object {
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
    }
    
    override val declarationCheckers: DeclarationCheckers get() = FiktionDeclarationCheckers
//    override val expressionCheckers: ExpressionCheckers get() = FiktionExpressionCheckers

    object FiktionDeclarationCheckers : DeclarationCheckers() {
//        override val classCheckers: Set<FirClassChecker> = setOf(
//            SuppliabilityInheritanceChecker,
//        )
//        override val typeParameterCheckers: Set<FirTypeParameterChecker> = setOf(
//            ClassFiktionParametersChecker,
//            UselessFiktionParametersChecker,
//        )
        
        override val controlFlowAnalyserCheckers: Set<FirControlFlowChecker> = setOf(
        
        )
    }

//    object FiktionExpressionCheckers : ExpressionCheckers() {
//        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
//            SuppliableFunctionCallChecker,
//        )
//    }
    
    object AtomicCallableControlFlowChecker : FirControlFlowChecker(MppCheckerKind.Common) {
        context(reporter: DiagnosticReporter, context: CheckerContext)
        override fun analyze(graph: ControlFlowGraph) {
            val declaration = graph.declaration
            TODO("Not yet implemented")
        }
    }
    
//    object SuppliabilityInheritanceChecker : FirClassChecker(MppCheckerKind.Common) {
//        context(context: CheckerContext, reporter: DiagnosticReporter)
//        override fun check(declaration: FirClass) {
//            val declarationSymbol = declaration.symbol
//            val suppliableSuperTypesSymbols = declarationSymbol.resolvedSuperTypes.map { it.toClassSymbol()!! }.filter { it.isSuppliable }
//            if (!declarationSymbol.isSuppliable && suppliableSuperTypesSymbols.isNotEmpty())
//                reporter.reportOn(
//                    source = declaration.source,
//                    factory = Errors.SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE,
//                    a = declarationSymbol,
//                    b = suppliableSuperTypesSymbols,
//                    context = context,
//                )
//            if (declarationSymbol.isSuppliable && suppliableSuperTypesSymbols.isEmpty() && declarationSymbol.typeParameterSymbols.none { it.isSupply })
//                reporter.reportOn(
//                    source = declaration.source,
//                    factory = Errors.USELESS_SUPPLIABILITY,
//                    a = declarationSymbol,
//                    context = context,
//                )
//        }
//    }
//
//    object ClassFiktionParametersChecker : FirTypeParameterChecker(MppCheckerKind.Common) {
//        private fun ConeTypeProjection.usesTypeParameterSymbols(typeParameterSymbol: FirTypeParameterSymbol): Boolean {
//            val typeProjectionsToCheck = ArrayDeque<ConeTypeProjection>()
//            typeProjectionsToCheck.addLast(this)
//            while (typeProjectionsToCheck.isNotEmpty()) {
//                val typeProjection = typeProjectionsToCheck.removeFirst()
//                when (typeProjection) {
//                    ConeStarProjection -> {}
//                    is ConeKotlinTypeProjection -> when (val type = typeProjection.type.unwrapLowerBound()) {
//                        is ConeCapturedType -> typeProjectionsToCheck.addAll(type.typeArguments)
//                        is ConeIntegerLiteralType -> {}
//                        is ConeIntersectionType -> typeProjectionsToCheck.addAll(type.intersectedTypes)
//                        is ConeLookupTagBasedType -> when (type) {
//                            is ConeClassLikeType -> typeProjectionsToCheck.addAll(type.typeArguments)
//                            is ConeTypeParameterType -> if (type.lookupTag.typeParameterSymbol == typeParameterSymbol) return true
//                            else -> error("Unexpected 'ConeLookupTagBasedType' inheritor: ${type::class.qualifiedName}")
//                        }
//                        is ConeStubType -> {}
//                        is ConeTypeVariableType -> {}
//                    }
//                }
//            }
//            return false
//        }
//
//        context(context: CheckerContext, reporter: DiagnosticReporter)
//        override fun check(declaration: FirTypeParameter) {
//            if (declaration.symbol.isSupply) return
//
//            val parent = declaration.containingDeclarationSymbol
//            if (parent !is FirClassSymbol<*> || !parent.isSuppliable) return
//
//            val containingSuperTypes = parent.resolvedSuperTypes.filter {
//                val fullyExpandedType = it.fullyExpandedType() as ConeClassLikeType
//                val itClassSymbol = fullyExpandedType.toSymbol() as FirClassSymbol<*>
//                if (!itClassSymbol.isSuppliable) return@filter false
//                itClassSymbol.typeParameterSymbols.withIndex().filter { it.value.isSupply }.map { it.index }.any { index ->
//                    fullyExpandedType.typeArguments[index].usesTypeParameterSymbols(declaration.symbol)
//                }
//            }
//
//            if (containingSuperTypes.isNotEmpty())
//                reporter.reportOn(
//                    source = declaration.source,
//                    factory = Errors.SUPPLIANCE_IS_NEEDED,
//                    a = declaration.symbol,
//                    b = containingSuperTypes,
//                    context = context,
//                )
//        }
//    }
//
//    object UselessFiktionParametersChecker : FirTypeParameterChecker(MppCheckerKind.Common) {
//        context(context: CheckerContext, reporter: DiagnosticReporter)
//        override fun check(declaration: FirTypeParameter) {
//            if (!declaration.symbol.isSupply) return
//
//            if (!declaration.containingDeclarationSymbol.hasAnnotation(suppliableClassId, context.session))
//                reporter.reportOn(
//                    source = declaration.source,
//                    factory = Errors.USELESS_SUPPLIANCE,
//                    a = declaration.symbol,
//                    context = context,
//                )
//        }
//    }
//
//    object SuppliableFunctionCallChecker : FirFunctionCallChecker(MppCheckerKind.Common) {
//        context(context: CheckerContext)
//        private fun ConeTypeProjection.checkFullSuppliance(): Boolean {
//            val typeProjectionsToCheck = ArrayDeque<ConeTypeProjection>()
//            typeProjectionsToCheck.addLast(this)
//            while (typeProjectionsToCheck.isNotEmpty()) {
//                val typeProjection = typeProjectionsToCheck.removeFirst()
//                when (typeProjection) {
//                    ConeStarProjection -> {}
//                    is ConeKotlinTypeProjection -> when (val type = typeProjection.type.unwrapLowerBound()) {
//                        is ConeCapturedType -> typeProjectionsToCheck.addAll(type.typeArguments)
//                        is ConeIntegerLiteralType -> {}
//                        is ConeIntersectionType -> typeProjectionsToCheck.addAll(type.intersectedTypes)
//                        is ConeLookupTagBasedType -> when (type) {
//                            is ConeClassLikeType -> typeProjectionsToCheck.addAll(type.typeArguments)
//                            is ConeTypeParameterType -> {
//                                val typeParameterSymbol = type.lookupTag.typeParameterSymbol
//                                if (
//                                    !typeParameterSymbol.isSupply ||
//                                    !typeParameterSymbol.containingDeclarationSymbol.hasAnnotation(suppliableClassId, context.session)
//                                ) return false
//                            }
//                            else -> error("Unexpected 'ConeLookupTagBasedType' inheritor: ${type::class.qualifiedName}")
//                        }
//                        is ConeStubType -> {}
//                        is ConeTypeVariableType -> {}
//                    }
//                }
//            }
//            return true
//        }
//
//        context(context: CheckerContext, reporter: DiagnosticReporter)
//        override fun check(expression: FirFunctionCall) {
//            val calleeSymbol = expression.calleeReference.resolved?.resolvedSymbol ?: return
//            when (calleeSymbol) {
//                is FirNamedFunctionSymbol -> when {
//                    calleeSymbol.isSuppliable -> {
//                        val supplyTypeParameterIndices = calleeSymbol.typeParameterSymbols.withIndex().filter { it.value.isSupply }.map { it.index }
//                        for (index in supplyTypeParameterIndices) {
//                            val typeArgumentConeTypeProjection = expression.typeArguments[index].toConeTypeProjection() as ConeKotlinType
//                            if (!typeArgumentConeTypeProjection.checkFullSuppliance())
//                                reporter.reportOn(
//                                    source = expression.typeArguments[index].source ?: expression.source,
//                                    factory = Errors.NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT,
//                                    a = calleeSymbol.typeParameterSymbols[index],
//                                    b = typeArgumentConeTypeProjection,
//                                    context = context,
//                                )
//                        }
//                    }
////                    calleeSymbol.isSupplianceProvided -> TODO()
//                }
//                is FirConstructorSymbol -> {
//                    val classSymbol = calleeSymbol.getConstructedClass(context.session)!!
//                    if (!classSymbol.isSuppliable) return
//                    if (!calleeSymbol.isSupplianceProvided) {
//                        val supplyTypeParameterIndices = classSymbol.typeParameterSymbols.withIndex().filter { it.value.isSupply }.map { it.index }
//                        for (index in supplyTypeParameterIndices) {
//                            val typeArgumentConeTypeProjection = expression.typeArguments[index].toConeTypeProjection() as ConeKotlinType
//                            if (!typeArgumentConeTypeProjection.checkFullSuppliance())
//                                reporter.reportOn(
//                                    source = expression.typeArguments[index].source ?: expression.source,
//                                    factory = Errors.NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT,
//                                    a = classSymbol.typeParameterSymbols[index],
//                                    b = typeArgumentConeTypeProjection,
//                                    context = context,
//                                )
//                        }
//                    } else {
////                        TODO()
//                    }
//                }
//            }
//        }
//    }
    
    object Errors : KtDiagnosticsContainer() {
        val FIKTION_ATOMIC_CALLABLE_CONTAINS_NON_ATOMIC_CALL = KtDiagnosticFactory2<FirFunctionSymbol<*>, FirCall>(
            name = "FIKTION_ATOMIC_CALLABLE_CONTAINS_NON_ATOMIC_CALL",
            severity = ERROR,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
            psiType = KtElement::class,
            rendererFactory = getRendererFactory(),
        )
        val FIKTION_ATOMIC_CALLABLE_CONTAINS_SEVERAL_ATOMIC_CALLS = KtDiagnosticFactory2<FirFunctionSymbol<*>, FirCall>(
            name = "FIKTION_ATOMIC_CALLABLE_CONTAINS_SEVERAL_ATOMIC_CALLS",
            severity = ERROR,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
            psiType = KtElement::class,
            rendererFactory = getRendererFactory(),
        )
        val FIKTION_ATOMIC_CALLABLE_CONTAINS_NO_ATOMIC_CALL = KtDiagnosticFactory1<FirFunctionSymbol<*>>(
            name = "FIKTION_ATOMIC_CALLABLE_CONTAINS_NO_ATOMIC_CALL",
            severity = WARNING,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
            psiType = KtElement::class,
            rendererFactory = getRendererFactory(),
        )
//        val SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE = KtDiagnosticFactory2<FirClassSymbol<*>, List<FirClassLikeSymbol<*>>>(
//            name = "SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE",
//            severity = ERROR,
//            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
//            psiType = KtElement::class,
//            rendererFactory = getRendererFactory()
//        )
//        val USELESS_SUPPLIABILITY = KtDiagnosticFactory1<FirClassSymbol<*>>(
//            name = "USELESS_SUPPLIABILITY",
//            severity = WARNING,
//            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
//            psiType = KtElement::class,
//            rendererFactory = getRendererFactory()
//        )
//        val SUPPLIANCE_IS_NEEDED = KtDiagnosticFactory2<FirTypeParameterSymbol, List<ConeKotlinType>>(
//            name = "SUPPLIANCE_IS_NEEDED",
//            severity = ERROR,
//            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
//            psiType = KtElement::class,
//            rendererFactory = getRendererFactory()
//        )
//        val USELESS_SUPPLIANCE = KtDiagnosticFactory1<FirTypeParameterSymbol>(
//            name = "USELESS_SUPPLIANCE",
//            severity = WARNING,
//            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
//            psiType = KtElement::class,
//            rendererFactory = getRendererFactory()
//        )
//        val NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT = KtDiagnosticFactory2<FirTypeParameterSymbol, ConeKotlinType>(
//            name = "NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT",
//            severity = ERROR,
//            defaultPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
//            psiType = KtElement::class,
//            rendererFactory = getRendererFactory()
//        )
        
        override fun getRendererFactory(): BaseDiagnosticRendererFactory = DefaultMessages
        
        object DefaultMessages : BaseDiagnosticRendererFactory() {
            override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("FIKTION") { map ->
                map.put(
                    FIKTION_ATOMIC_CALLABLE_CONTAINS_NON_ATOMIC_CALL,
                    "{0} is marked as atomic (via '@FiktionScope.Atomic' annotation), but contains non-atomic call.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                    null,
                )
                map.put(
                    FIKTION_ATOMIC_CALLABLE_CONTAINS_SEVERAL_ATOMIC_CALLS,
                    "{0} is marked as atomic (via '@FiktionScope.Atomic' annotation), but contains several atomic calls.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                    null,
                )
                map.put(
                    FIKTION_ATOMIC_CALLABLE_CONTAINS_NO_ATOMIC_CALL,
                    "{0} is marked as atomic (via '@FiktionScope.Atomic' annotation), but contains no atomic call.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
//                map.put(
//                    SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE,
//                    "{0} is not suppliable (is not marked with '@Suppliable' annotation), but inherits the following suppliable classes (which is prohibited): {1}.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                    FirDiagnosticRenderers.DECLARATION_FQ_NAME.joinToString(),
//                )
//                map.put(
//                    USELESS_SUPPLIABILITY,
//                    "{0} is suppliable, but does not have supplied type parameters and does not inherit any other suppliable class.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                )
//                map.put(
//                    SUPPLIANCE_IS_NEEDED,
//                    "Non-supplied type parameter {0} is used in supplied type arguments of suppliable super types: {1}.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                    Renderer<ConeKotlinType> { it.renderReadable() }.joinToString()
//                )
//                map.put(
//                    USELESS_SUPPLIANCE,
//                    "Supplied type parameter {0} is useless in non-suppliable declaration.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                )
//                map.put(
//                    NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT,
//                    "Non-suppliable type {1} is used in supplied type argument {0} of suppliable function call.",
//                    FirDiagnosticRenderers.DECLARATION_NAME,
//                    Renderer<ConeKotlinType> { it.renderReadable() }
//                )
            }
        }
    }
}