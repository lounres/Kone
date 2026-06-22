/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.suppliableAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableClassClassClassId
import dev.lounres.kone.plugin.suppliedTypes.supplianceProvidedAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.supplyAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.withSuppliedFunctionCallableId
import dev.lounres.kone.util.kotlinCompilerUtils.KtDiagnosticFactory1Delegate
import dev.lounres.kone.util.kotlinCompilerUtils.KtDiagnosticFactory2Delegate
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.Renderer
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirTypeParameterChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers
import org.jetbrains.kotlin.fir.analysis.diagnostics.joinToString
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.declarations.getConstructedClass
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.utils.isInner
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.references.symbol
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeCapturedType
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeIntegerLiteralType
import org.jetbrains.kotlin.fir.types.ConeIntersectionType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeKotlinTypeProjection
import org.jetbrains.kotlin.fir.types.ConeLookupTagBasedType
import org.jetbrains.kotlin.fir.types.ConeStarProjection
import org.jetbrains.kotlin.fir.types.ConeStubType
import org.jetbrains.kotlin.fir.types.ConeTypeParameterType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.ConeTypeVariableType
import org.jetbrains.kotlin.fir.types.renderReadable
import org.jetbrains.kotlin.fir.types.toConeTypeProjection
import org.jetbrains.kotlin.fir.types.unwrapLowerBound


class SuppliedTypeCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    companion object {
        context(context: CheckerContext)
        private val FirTypeParameterSymbol.isSupply: Boolean get() = hasAnnotation(supplyAnnotationClassId, context.session)
        context(context: CheckerContext)
        private val FirNamedFunctionSymbol.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId, context.session)
        context(context: CheckerContext)
        private val FirClassSymbol<*>.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId, context.session)
        context(context: CheckerContext)
        private val FirNamedFunctionSymbol.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedAnnotationClassId, context.session)
        context(context: CheckerContext)
        private val FirConstructorSymbol.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedAnnotationClassId, context.session)
    }
    
    override val declarationCheckers: DeclarationCheckers get() = SuppliedTypeDeclarationCheckers
    override val expressionCheckers: ExpressionCheckers get() = SuppliedTypeExpressionCheckers
    
    object SuppliedTypeDeclarationCheckers : DeclarationCheckers() {
        override val classCheckers: Set<FirClassChecker> = setOf(
            SuppliabilityInheritanceChecker,
        )
        override val typeParameterCheckers: Set<FirTypeParameterChecker> = setOf(
            ClassSuppliedTypeParametersChecker,
            UselessSuppliedTypeParametersChecker,
        )
    }
    
    object SuppliedTypeExpressionCheckers : ExpressionCheckers() {
        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
            SuppliableFunctionCallChecker,
        )
    }
    
    object SuppliabilityInheritanceChecker : FirClassChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirClass) {
            val declarationSymbol = declaration.symbol
            val suppliableClassFirClassSymbol = context.session.symbolProvider.getClassLikeSymbolByClassId(suppliableClassClassClassId)!!
            val suppliableSuperTypesSymbols = declarationSymbol.resolvedSuperTypes.map { it.toClassSymbol()!! }.filter { it.isSuppliable && it != suppliableClassFirClassSymbol }
            if (!declarationSymbol.isSuppliable && suppliableSuperTypesSymbols.isNotEmpty())
                reporter.reportOn(
                    source = declaration.source,
                    factory = Errors.SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE,
                    a = declarationSymbol,
                    b = suppliableSuperTypesSymbols,
                    context = context,
                )
            if (declarationSymbol.isSuppliable && suppliableSuperTypesSymbols.isEmpty() && declarationSymbol.typeParameterSymbols.none { it.isSupply })
                reporter.reportOn(
                    source = declaration.source,
                    factory = Errors.USELESS_SUPPLIABILITY,
                    a = declarationSymbol,
                    context = context,
                )
        }
    }
    
    object ClassSuppliedTypeParametersChecker : FirTypeParameterChecker(MppCheckerKind.Common) {
        private fun ConeTypeProjection.usesTypeParameterSymbols(typeParameterSymbol: FirTypeParameterSymbol): Boolean {
            val typeProjectionsToCheck = ArrayDeque<ConeTypeProjection>()
            typeProjectionsToCheck.addLast(this)
            while (typeProjectionsToCheck.isNotEmpty()) {
                val typeProjection = typeProjectionsToCheck.removeFirst()
                when (typeProjection) {
                    ConeStarProjection -> {}
                    is ConeKotlinTypeProjection -> when (val type = typeProjection.type.unwrapLowerBound()) {
                        is ConeCapturedType -> typeProjectionsToCheck.addAll(type.typeArguments)
                        is ConeIntegerLiteralType -> {}
                        is ConeIntersectionType -> typeProjectionsToCheck.addAll(type.intersectedTypes)
                        is ConeLookupTagBasedType -> when (type) {
                            is ConeClassLikeType -> typeProjectionsToCheck.addAll(type.typeArguments)
                            is ConeTypeParameterType -> if (type.lookupTag.typeParameterSymbol == typeParameterSymbol) return true
                            else -> error("Unexpected 'ConeLookupTagBasedType' inheritor: ${type::class.qualifiedName}")
                        }
                        is ConeStubType -> {}
                        is ConeTypeVariableType -> {}
                    }
                }
            }
            return false
        }
        
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirTypeParameter) {
            if (declaration.symbol.isSupply) return
            
            val parent = declaration.containingDeclarationSymbol
            if (parent !is FirClassSymbol<*> || !parent.isSuppliable) return
            
            val containingSuperTypes = parent.resolvedSuperTypes.filter {
                val fullyExpandedType = it.fullyExpandedType() as ConeClassLikeType
                val itClassSymbol = fullyExpandedType.toSymbol() as FirClassSymbol<*>
                if (!itClassSymbol.isSuppliable) return@filter false
                itClassSymbol.typeParameterSymbols.withIndex().filter { it.value.isSupply }.map { it.index }.any { index ->
                    fullyExpandedType.typeArguments[index].usesTypeParameterSymbols(declaration.symbol)
                }
            }
            
            if (containingSuperTypes.isNotEmpty())
                reporter.reportOn(
                    source = declaration.source,
                    factory = Errors.SUPPLIANCE_IS_NEEDED,
                    a = declaration.symbol,
                    b = containingSuperTypes,
                    context = context,
                )
        }
    }
    
    object UselessSuppliedTypeParametersChecker : FirTypeParameterChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirTypeParameter) {
            if (!declaration.symbol.isSupply) return
            
            if (!declaration.containingDeclarationSymbol.hasAnnotation(suppliableAnnotationClassId, context.session))
                reporter.reportOn(
                    source = declaration.source,
                    factory = Errors.USELESS_SUPPLIANCE,
                    a = declaration.symbol,
                    context = context,
                )
        }
    }
    
    object SuppliableFunctionCallChecker : FirFunctionCallChecker(MppCheckerKind.Common) {
        private fun ConeTypeProjection.checkFullSuppliance(availableSupplyTypeParameters: Set<FirTypeParameterSymbol>): Boolean {
            val typeProjectionsToCheck = ArrayDeque<ConeTypeProjection>()
            typeProjectionsToCheck.addLast(this)
            while (typeProjectionsToCheck.isNotEmpty()) {
                val typeProjection = typeProjectionsToCheck.removeFirst()
                when (typeProjection) {
                    ConeStarProjection -> {}
                    is ConeKotlinTypeProjection -> when (val type = typeProjection.type.unwrapLowerBound()) {
                        is ConeCapturedType -> typeProjectionsToCheck.addAll(type.typeArguments)
                        is ConeIntegerLiteralType -> {}
                        is ConeIntersectionType -> typeProjectionsToCheck.addAll(type.intersectedTypes)
                        is ConeLookupTagBasedType -> when (type) {
                            is ConeClassLikeType -> typeProjectionsToCheck.addAll(type.typeArguments)
                            is ConeTypeParameterType -> {
                                if (type.lookupTag.typeParameterSymbol !in availableSupplyTypeParameters) return false
                            }
                            else -> error("Unexpected 'ConeLookupTagBasedType' inheritor: ${type::class.qualifiedName}")
                        }
                        is ConeStubType -> {}
                        is ConeTypeVariableType -> {}
                    }
                }
            }
            return true
        }
        
        context(context: CheckerContext, reporter: DiagnosticReporter)
        private fun FirFunctionCall.validateTypeArguments(
            typeParameterSymbols: List<FirTypeParameterSymbol>,
            availableSupplyTypeParameters: Set<FirTypeParameterSymbol>,
        ) {
            val supplyTypeParameterIndices = typeParameterSymbols.withIndex().filter { it.value.isSupply }.map { it.index }
            for (index in supplyTypeParameterIndices) {
                val typeArgumentConeTypeProjection = this.typeArguments[index].toConeTypeProjection() as ConeKotlinType
                if (!typeArgumentConeTypeProjection.checkFullSuppliance(availableSupplyTypeParameters))
                    reporter.reportOn(
                        source = this.typeArguments[index].source ?: this.source,
                        factory = Errors.NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT,
                        a = typeParameterSymbols[index],
                        b = typeArgumentConeTypeProjection,
                        context = context,
                    )
            }
        }
        
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(expression: FirFunctionCall) {
            val availableSupplyTypeParameters by lazy(NONE) {
                buildSet {
                    val startIndices = context.containingElements.withIndex().filter { it.value is FirFunction || it.value is FirProperty }.map { it.index }
                    for (startIndex in startIndices) {
                        var currentPossibleDeclarationIndex = startIndex
                        while (currentPossibleDeclarationIndex >= 0) {
                            val element = context.containingElements[currentPossibleDeclarationIndex]
                            when (element) {
                                is FirClass -> {
                                    if (element.symbol.isSuppliable) element.typeParameters.map { it.symbol }.filterTo(this) { it.isSupply }
                                    if (!element.isInner) break
                                }
                                is FirNamedFunction -> {
                                    if (element.symbol.isSuppliable) element.typeParameters.map { it.symbol }.filterTo(this) { it.isSupply }
                                }
                                is FirProperty -> {}
                                else -> break
                            }
                            currentPossibleDeclarationIndex -= 1
                        }
                    }
                    
                    val withSuppliedFirNamedFunctionSymbols = context.session.symbolProvider.getTopLevelFunctionSymbols(packageFqName = withSuppliedFunctionCallableId.packageName, name = withSuppliedFunctionCallableId.callableName)
                    
                    for (element in context.containingElements) {
                        if (element is FirFunctionCall && element.calleeReference.symbol in withSuppliedFirNamedFunctionSymbols) {
                            element.typeArguments
                                .dropLast(1)
                                .map { it.toConeTypeProjection() }
                                .filterIsInstance<ConeTypeParameterType>()
                                .mapTo(this) { it.lookupTag.typeParameterSymbol }
                        }
                    }
                }
            }
            
            val calleeSymbol = expression.calleeReference.resolved?.resolvedSymbol ?: return
            when (calleeSymbol) {
                is FirNamedFunctionSymbol -> if (calleeSymbol.isSuppliable) {
                    expression.validateTypeArguments(
                        typeParameterSymbols = calleeSymbol.typeParameterSymbols,
                        availableSupplyTypeParameters = availableSupplyTypeParameters,
                    )
                }
                is FirConstructorSymbol -> {
                    val classSymbol = calleeSymbol.getConstructedClass(context.session)!!
                    if (classSymbol.isSuppliable) {
                        expression.validateTypeArguments(
                            typeParameterSymbols = classSymbol.typeParameterSymbols,
                            availableSupplyTypeParameters = availableSupplyTypeParameters,
                        )
                    }
                }
            }
        }
    }
    
    object Errors : KtDiagnosticsContainer() {
        val SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE by KtDiagnosticFactory2Delegate<FirClassSymbol<*>, List<FirClassLikeSymbol<*>>>(
            severity = ERROR,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
        )
        val USELESS_SUPPLIABILITY by KtDiagnosticFactory1Delegate<FirClassSymbol<*>>(
            severity = WARNING,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
        )
        val SUPPLIANCE_IS_NEEDED by KtDiagnosticFactory2Delegate<FirTypeParameterSymbol, List<ConeKotlinType>>(
            severity = ERROR,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
        )
        val USELESS_SUPPLIANCE by KtDiagnosticFactory1Delegate<FirTypeParameterSymbol>(
            severity = WARNING,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DECLARATION_NAME_ONLY,
        )
        val NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT by KtDiagnosticFactory2Delegate<FirTypeParameterSymbol, ConeKotlinType>(
            severity = ERROR,
            defaultPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
        )
        
        override fun getRendererFactory(): BaseDiagnosticRendererFactory = DefaultMessages
        
        object DefaultMessages : BaseDiagnosticRendererFactory() {
            override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("SUPPLIED_TYPES") { map ->
                map.put(
                    SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE,
                    "{0} is not suppliable (is not marked with '@Suppliable' annotation), but inherits the following suppliable classes (which is prohibited): {1}.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                    FirDiagnosticRenderers.DECLARATION_FQ_NAME.joinToString(),
                )
                map.put(
                    USELESS_SUPPLIABILITY,
                    "{0} is suppliable, but does not have supplied type parameters and does not inherit any other suppliable class.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
                map.put(
                    SUPPLIANCE_IS_NEEDED,
                    "Non-supplied type parameter {0} is used in supplied type arguments of suppliable super types: {1}.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                    Renderer<ConeKotlinType> { it.renderReadable() }.joinToString()
                )
                map.put(
                    USELESS_SUPPLIANCE,
                    "Supplied type parameter {0} is useless in non-suppliable declaration.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                )
                map.put(
                    NON_SUPPLIABLE_TYPE_IN_SUPPLY_ARGUMENT,
                    "Non-suppliable type {1} is used in supplied type argument {0} of suppliable function call.",
                    FirDiagnosticRenderers.DECLARATION_NAME,
                    Renderer<ConeKotlinType> { it.renderReadable() }
                )
            }
        }
    }
}