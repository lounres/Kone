/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir


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