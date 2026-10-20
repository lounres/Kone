/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.fir

import dev.lounres.kone.plugin.contexts.*
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirNamedArgumentExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildBlock
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.unwrapArgument
import org.jetbrains.kotlin.fir.extensions.FirExtensionApiInternals
import org.jetbrains.kotlin.fir.extensions.FirFunctionCallRefinementExtension
import org.jetbrains.kotlin.fir.references.builder.buildSimpleNamedReference
import org.jetbrains.kotlin.fir.resolve.calls.candidate.CallInfo
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance


@OptIn(FirExtensionApiInternals::class)
class FirScopeCallsInjectorExtension(session: FirSession) : FirFunctionCallRefinementExtension(session) {
    private val receiversFirFunctionSymbol by lazy {
        session.symbolProvider.getTopLevelFunctionSymbols(receiversFunctionCallableId.packageName, receiversFunctionCallableId.callableName).single()
    }
    private val contextsFirFunctionSymbol by lazy {
        session.symbolProvider.getTopLevelFunctionSymbols(contextsFunctionCallableId.packageName, contextsFunctionCallableId.callableName).single()
    }
    private val unwrapFirFunctionSymbol by lazy {
        session.symbolProvider.getTopLevelFunctionSymbols(unwrapFunctionCallableId.packageName, unwrapFunctionCallableId.callableName).single()
    }
    
    override fun intercept(callInfo: CallInfo, symbol: FirNamedFunctionSymbol): CallReturnType? {
        when (symbol) {
            receiversFirFunctionSymbol -> {
                val arguments = callInfo.argumentList.arguments
                if (arguments.firstOrNull() is FirNamedArgumentExpression?) return null
                val lambda = arguments.last().unwrapArgument()
                if (lambda !is FirAnonymousFunctionExpression) return null
                val vararg = arguments.dropLast(1)
                val body = lambda.anonymousFunction.body ?: return null
                
                lambda.anonymousFunction.replaceBody(
                    buildBlock {
                        source = body.source
                        statements += buildFunctionCall {
                            source = callInfo.callSite.source
                            coneTypeOrNull = session.builtinTypes.unitType.coneType
                            calleeReference = buildSimpleNamedReference {
                                source = callInfo.callSite.source
                                name = localReceiversFunctionCallableId.callableName
                            }
                            argumentList = buildArgumentList {
                                source = callInfo.callSite.source
                                for (argument in vararg) {
                                    this.arguments += buildFunctionCall {
                                        source = callInfo.callSite.source
                                        calleeReference = buildSimpleNamedReference {
                                            source = callInfo.callSite.source
                                            name = koneContextsPluginScopeArgumentStubFunctionCallableId.callableName
                                        }
                                        typeArguments += buildTypeProjectionWithVariance {
                                            source = callInfo.callSite.source
                                            typeRef = buildResolvedTypeRef {
                                                source = callInfo.callSite.source
                                                coneType = argument.resolvedType
                                            }
                                            variance = Variance.INVARIANT
                                        }
                                    }
                                }
                            }
                        }
                        statements += body.statements
                    }
                )
            }
            contextsFirFunctionSymbol -> {
                val arguments = callInfo.argumentList.arguments
                if (arguments.firstOrNull() is FirNamedArgumentExpression?) return null
                val lambda = arguments.last().unwrapArgument()
                if (lambda !is FirAnonymousFunctionExpression) return null
                val vararg = arguments.dropLast(1)
                val body = lambda.anonymousFunction.body ?: return null
                
                lambda.anonymousFunction.replaceBody(
                    buildBlock {
                        source = body.source
                        statements += buildFunctionCall {
                            source = callInfo.callSite.source
                            coneTypeOrNull = session.builtinTypes.unitType.coneType
                            calleeReference = buildSimpleNamedReference {
                                source = callInfo.callSite.source
                                name = localContextsFunctionCallableId.callableName
                            }
                            argumentList = buildArgumentList {
                                source = callInfo.callSite.source
                                for (argument in vararg) {
                                    this.arguments += buildFunctionCall {
                                        source = callInfo.callSite.source
                                        calleeReference = buildSimpleNamedReference {
                                            source = callInfo.callSite.source
                                            name = koneContextsPluginScopeArgumentStubFunctionCallableId.callableName
                                        }
                                        typeArguments += buildTypeProjectionWithVariance {
                                            source = callInfo.callSite.source
                                            typeRef = buildResolvedTypeRef {
                                                source = callInfo.callSite.source
                                                coneType = argument.resolvedType
                                            }
                                            variance = Variance.INVARIANT
                                        }
                                    }
                                }
                            }
                        }
                        statements += body.statements
                    }
                )
            }
            unwrapFirFunctionSymbol -> {
                val arguments = callInfo.argumentList.arguments
                if (arguments.firstOrNull() is FirNamedArgumentExpression?) return null
                val lambda = arguments.last().unwrapArgument()
                if (lambda !is FirAnonymousFunctionExpression) return null
                val vararg = arguments.dropLast(1)
                val body = lambda.anonymousFunction.body ?: return null
                
                lambda.anonymousFunction.replaceBody(
                    buildBlock {
                        source = body.source
                        statements += buildFunctionCall {
                            source = callInfo.callSite.source
                            coneTypeOrNull = session.builtinTypes.unitType.coneType
                            calleeReference = buildSimpleNamedReference {
                                source = callInfo.callSite.source
                                name = localUnwrapFunctionCallableId.callableName
                            }
                            explicitReceiver = callInfo.explicitReceiver
                            argumentList = buildArgumentList {
                                source = callInfo.callSite.source
                                for (argument in vararg) {
                                    this.arguments += buildFunctionCall {
                                        source = callInfo.callSite.source
                                        calleeReference = buildSimpleNamedReference {
                                            source = callInfo.callSite.source
                                            name = koneContextsPluginScopeArgumentStubFunctionCallableId.callableName
                                        }
                                        typeArguments += buildTypeProjectionWithVariance {
                                            source = callInfo.callSite.source
                                            typeRef = buildResolvedTypeRef {
                                                source = callInfo.callSite.source
                                                coneType = argument.resolvedType
                                            }
                                            variance = Variance.INVARIANT
                                        }
                                    }
                                }
                            }
                        }
                        statements += body.statements
                    }
                )
            }
        }
        
        return null
    }
    
    override fun transform(call: FirFunctionCall, originalSymbol: FirNamedFunctionSymbol): FirFunctionCall = call
    override fun ownsSymbol(symbol: FirRegularClassSymbol): Boolean = false
    override fun anchorElement(symbol: FirRegularClassSymbol): KtSourceElement = symbol.source!!
    override fun restoreSymbol(call: FirFunctionCall, name: Name): FirRegularClassSymbol? = null
}