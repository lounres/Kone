/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.koneContextsPackageFQName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.SessionAndScopeSessionHolder
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataRegistry
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirCall
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.UnresolvedExpressionTypeAccess
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildBlock
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.extensions.FirExpressionResolutionExtension
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitExtensionReceiverValue
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularPropertySymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.visitors.FirTransformer
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance


//data object OuterFunctionCallsKey : FirDeclarationDataKey()
//private var FirDeclaration.outerFunctionCalls: MutableMap<FirFunctionCall, FirFunctionCall>? by FirDeclarationDataRegistry.data(OuterFunctionCallsKey)
//private val FirBasedSymbol<*>.outerFunctionCalls: Map<FirFunctionCall, FirFunctionCall>? by FirDeclarationDataRegistry.symbolAccessor(OuterFunctionCallsKey)
//
//class FirLocalContextsExpressionInsertionExtension(session: FirSession) : FirStatusTransformerExtension(session) {
////    data object InsertedLambdaLeadingCallKey : GeneratedDeclarationKey()
//
//    private data object VisitMark
//    private data object VisitedKey : FirDeclarationDataKey()
//
//    companion object {
//        private var FirDeclaration.visitMark: VisitMark? by FirDeclarationDataRegistry.data(VisitedKey)
//    }
//
//    private val contextsPluginLambdaLeadingCallFirFunctionSymbol by lazy {
//        session.symbolProvider
//            .getTopLevelFunctionSymbols(koneContextsPackageFQName, Name.identifier("contextsPluginLambdaLeadingCall"))
//            .firstIsInstance<FirFunctionSymbol<*>>()
//    }
//    private val suppressAnnotation by lazy {
//        session.symbolProvider.getClassLikeSymbolByClassId(ClassId(FqName("kotlin"), Name.identifier("Suppress")))!!
//    }
//
//    private class InsertingTransformer(
//        private val session: FirSession,
//        private val contextsPluginLambdaLeadingCallFirFunctionSymbol: FirFunctionSymbol<*>,
//        private val suppressAnnotation: FirClassLikeSymbol<*>,
//    ) : FirTransformer<Nothing?>() {
//        override fun <E : FirElement> transformElement(element: E, data: Nothing?): E {
//            if (element is FirDeclaration) {
//                declarationStack.addLast(element)
//                element.transformChildren(this, data)
//                element.visitMark = VisitMark
//                declarationStack.removeLast()
//            } else {
//                element.transformChildren(this, data)
//            }
//            return element
//        }
//
//        private val declarationStack = ArrayDeque<FirDeclaration>()
//
//        @OptIn(UnresolvedExpressionTypeAccess::class)
//        override fun transformFunctionCall(functionCall: FirFunctionCall, data: Nothing?): FirStatement {
//            val lastArgument = functionCall.argumentList.arguments.lastOrNull() as? FirAnonymousFunctionExpression
//            if (lastArgument != null) {
//                val anonymousFunction = lastArgument.anonymousFunction
//                val oldBody = anonymousFunction.body
//                if (oldBody != null) {
//                    anonymousFunction.replaceBody(
//                        buildBlock {
//                            source = oldBody.source
//                            coneTypeOrNull = oldBody.coneTypeOrNull
//                            annotations += oldBody.annotations
//                            statements += buildFunctionCall {
//                                coneTypeOrNull = session.builtinTypes.unitType.coneType
//                                annotations += buildAnnotation {
//                                    source = oldBody.source
//                                    annotationTypeRef = buildResolvedTypeRef {
//                                        source = oldBody.source
//                                        coneType = suppressAnnotation.defaultType()
//                                    }
//                                    argumentMapping = buildAnnotationArgumentMapping {
//                                        source = oldBody.source
//                                        mapping[Name.identifier("names")] = buildLiteralExpression(
//                                            source = oldBody.source,
//                                            kind = ConstantValueKind.String,
//                                            value = "DEPRECATION_ERROR",
//                                            setType = true,
//                                        )
//                                    }
//                                }
//                                source = oldBody.source
//                                calleeReference = buildResolvedNamedReference {
//                                    source = oldBody.source
//                                    name = Name.identifier("contextsPluginLambdaLeadingCall")
//                                    resolvedSymbol = contextsPluginLambdaLeadingCallFirFunctionSymbol
//                                }
//                            }.also { insertedCall ->
//                                val containingDeclaration = declarationStack.last()
//                                if (containingDeclaration.outerFunctionCalls == null) containingDeclaration.outerFunctionCalls = mutableMapOf()
//                                containingDeclaration.outerFunctionCalls!![insertedCall] = functionCall
//                            }
//                            statements += oldBody.statements
//                        }
//                    )
//                }
//            }
//            return super.transformFunctionCall(functionCall, data)
//        }
//
////        @OptIn(UnresolvedExpressionTypeAccess::class)
////        override fun transformAnonymousFunction(anonymousFunction: FirAnonymousFunction, data: Nothing?): FirStatement {
////            val oldBody = anonymousFunction.body
////            if (oldBody != null) {
////                anonymousFunction.replaceBody(
////                    buildBlock {
////                        source = oldBody.source
////                        coneTypeOrNull = oldBody.coneTypeOrNull
////                        annotations += oldBody.annotations
////                        statements += buildFunctionCall {
////                            coneTypeOrNull = session.builtinTypes.unitType.coneType
////                            annotations += buildAnnotation {
////                                source = oldBody.source
////                                annotationTypeRef = buildResolvedTypeRef {
////                                    source = oldBody.source
////                                    coneType = suppressAnnotation.defaultType()
////                                }
////                                argumentMapping = buildAnnotationArgumentMapping {
////                                    source = oldBody.source
////                                    mapping[Name.identifier("names")] = buildLiteralExpression(
////                                        source = oldBody.source,
////                                        kind = ConstantValueKind.String,
////                                        value = "DEPRECATION_ERROR",
////                                        setType = true,
////                                    )
////                                }
////                            }
////                            source = oldBody.source
////                            calleeReference = buildResolvedNamedReference {
////                                source = oldBody.source
////                                name = Name.identifier("contextsPluginLambdaLeadingCall")
////                                resolvedSymbol = contextsPluginLambdaLeadingCallFirFunctionSymbol
////                            }
////                        }
////                        statements += oldBody.statements
////                    }
////                )
////            }
////            return super.transformAnonymousFunction(anonymousFunction, data)
////        }
//    }
//
//    override fun needTransformStatus(declaration: FirDeclaration): Boolean {
//        if (declaration.visitMark == null)
//            declaration.transform<FirDeclaration, _>(
//                InsertingTransformer(
//                    session = session,
//                    contextsPluginLambdaLeadingCallFirFunctionSymbol = contextsPluginLambdaLeadingCallFirFunctionSymbol,
//                    suppressAnnotation = suppressAnnotation,
//                ),
//                null,
//            )
//        return false
//    }
//}
//
//class FirReceiversExpressionResolutionExtension(session: FirSession) : FirExpressionResolutionExtension(session) {
//    data object GeneratedReceiverFromReceiversFunctionKey : GeneratedDeclarationKey()
//
//    private val receiversFirFunctionSymbol by lazy {
//        session.symbolProvider
//            .getTopLevelFunctionSymbols(koneContextsPackageFQName, Name.identifier("receivers"))
//            .firstIsInstance<FirFunctionSymbol<*>>()
//    }
//    private val contextsPluginLambdaLeadingCallFirFunctionSymbol by lazy {
//        session.symbolProvider
//            .getTopLevelFunctionSymbols(koneContextsPackageFQName, Name.identifier("contextsPluginLambdaLeadingCall"))
//            .firstIsInstance<FirFunctionSymbol<*>>()
//    }
//
//    override fun addNewImplicitReceivers(
//        functionCall: FirFunctionCall,
//        sessionHolder: SessionAndScopeSessionHolder,
//        containingCallableSymbol: FirBasedSymbol<*>,
//    ): List<ImplicitExtensionReceiverValue> {
//        if (functionCall.calleeReference.resolved?.resolvedSymbol != contextsPluginLambdaLeadingCallFirFunctionSymbol) return emptyList()
//        val outerFunctionCall = containingCallableSymbol.outerFunctionCalls?.get(functionCall) ?: return emptyList()
//        if (outerFunctionCall.arguments.size != 2) return emptyList()
//        val varargArgument = outerFunctionCall.arguments[0] as FirVarargArgumentsExpression
//        val fakeValueProperty = buildProperty {
//            val theSymbol = FirRegularPropertySymbol(CallableId(koneContextsPackageFQName, Name.special("<Kone contexts receivers receiver holder>")))
//            resolvePhase = FirResolvePhase.BODY_RESOLVE
//            moduleData = session.moduleData
//            origin = GeneratedReceiverFromReceiversFunctionKey.origin
//            status = FirResolvedDeclarationStatusImpl(
//                Visibilities.DEFAULT_VISIBILITY,
//                Modality.FINAL,
//                EffectiveVisibility.Public,
//            )
//            isLocal = true
//            returnTypeRef = session.builtinTypes.nullableAnyType
//            receiverParameter = buildReceiverParameter {
//                resolvePhase = FirResolvePhase.BODY_RESOLVE
//                moduleData = session.moduleData
//                origin = GeneratedReceiverFromReceiversFunctionKey.origin
//                symbol = FirReceiverParameterSymbol()
//                typeRef = session.builtinTypes.nullableAnyType
//                containingDeclarationSymbol = theSymbol
//            }
//            name = Name.special("<Kone contexts receivers receiver holder>")
//            isVar = false
//            symbol = theSymbol
//        }
//        return varargArgument.arguments.map { holder ->
//            val type = holder.resolvedType
//            val receiverParameter = buildReceiverParameter {
//                source = holder.source
//                resolvePhase = FirResolvePhase.BODY_RESOLVE
//                moduleData = session.moduleData
//                origin = GeneratedReceiverFromReceiversFunctionKey.origin
//                symbol = FirReceiverParameterSymbol()
//                containingDeclarationSymbol = fakeValueProperty.symbol
//                typeRef = buildResolvedTypeRef {
//                    coneType = type
//                }
//            }
//            ImplicitExtensionReceiverValue(
//                boundSymbol = receiverParameter.symbol,
//                type = type,
//                useSiteSession = sessionHolder.session,
//                scopeSession = sessionHolder.scopeSession
//            )
//        }
//    }
//}

//@OptIn(FirExtensionApiInternals::class)
//class FirLocalContextsExpressionInsertionExtension(session: FirSession) : FirFunctionCallRefinementExtension(session) {
//    private val contextsFirFunctionSymbol by lazy {
//        session.symbolProvider
//            .getTopLevelFunctionSymbols(koneContextsPackageFQName, contextsFunctionShortName)
//            .firstIsInstance<FirFunctionSymbol<*>>()
//    }
//    private val localContextsFirFunctionSymbol by lazy {
//        session.symbolProvider
//            .getTopLevelFunctionSymbols(koneContextsPackageFQName, localContextsFunctionShortName)
//            .firstIsInstance<FirFunctionSymbol<*>>()
//    }
//    private val runFirFunctionSymbol by lazy {
//        session.symbolProvider
//            .getTopLevelFunctionSymbols(FqName("kotlin"), Name.identifier("run"))
//            .firstIsInstance<FirFunctionSymbol<*>>()
//    }
//
//    @OptIn(UnresolvedExpressionTypeAccess::class)
//    override fun intercept(
//        callInfo: CallInfo,
//        symbol: FirNamedFunctionSymbol,
//    ): CallReturnType? {
//        if (symbol != contextsFirFunctionSymbol) return null
//        val contextsFunctionCall = callInfo.callSite as? FirFunctionCall ?: return null
//        val initialArgumentList = contextsFunctionCall.argumentList
//        val initialArguments = initialArgumentList.arguments
//        if (initialArguments.isEmpty()) return null
//        val contextsArguments = initialArguments.dropLast(1)
//        val trailingLambda = initialArguments.last() as? FirAnonymousFunctionExpression ?: return null
//        if (!trailingLambda.anonymousFunction.isLambda) return null
//        val oldTrailingLambdaBody = trailingLambda.anonymousFunction.body ?: return null
//        contextsFunctionCall.replaceCalleeReference(
//            buildResolvedNamedReference {
//                name = Name.identifier("run")
//                resolvedSymbol = runFirFunctionSymbol
//            }
//        )
//        contextsFunctionCall.replaceArgumentList(
//            buildArgumentList {
//                arguments.add(trailingLambda)
//            }
//        )
//        trailingLambda.anonymousFunction.replaceBody(
//            buildBlock {
//                source = oldTrailingLambdaBody.source
//                coneTypeOrNull = oldTrailingLambdaBody.coneTypeOrNull
//                annotations += oldTrailingLambdaBody.annotations
//                statements += buildFunctionCall {
//                    source = initialArgumentList.source
//                    coneTypeOrNull = session.builtinTypes.unitType.coneType
//                    argumentList = buildArgumentList {
//                        arguments.addAll(contextsArguments)
//                    }
//                    calleeReference = buildResolvedNamedReference {
//                        name = localContextsFunctionShortName
//                        resolvedSymbol = localContextsFirFunctionSymbol
//                    }
//                }
//                statements += oldTrailingLambdaBody.statements
//            }
//        )
//        return null
//    }
//
//    override fun transform(
//        call: FirFunctionCall,
//        originalSymbol: FirNamedFunctionSymbol,
//    ): FirFunctionCall {
//        TODO("Not yet implemented")
//    }
//
//    override fun ownsSymbol(symbol: FirRegularClassSymbol): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun anchorElement(symbol: FirRegularClassSymbol): KtSourceElement {
//        TODO("Not yet implemented")
//    }
//
//    override fun restoreSymbol(
//        call: FirFunctionCall,
//        name: Name,
//    ): FirRegularClassSymbol? {
//        TODO("Not yet implemented")
//    }
//
//}