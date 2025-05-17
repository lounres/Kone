/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension


class SuppliedTypeFirAdditionalCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
//    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
//        override val basicDeclarationCheckers: Set<FirBasicDeclarationChecker> = setOf()
//        override val typeParameterCheckers: Set<FirTypeParameterChecker> = setOf()
//    }
}

//object : FirBasicDeclarationChecker(MppCheckerKind.Common) {
//    override fun check(declaration: FirDeclaration, context: CheckerContext, reporter: DiagnosticReporter) {
//        println(declaration)
//    }
//}

//object  : FirTypeParameterChecker(MppCheckerKind.Common) {
//    override fun check(
//        declaration: FirTypeParameter,
//        context: CheckerContext,
//        reporter: DiagnosticReporter
//    ) {
//        val isSupplied = declaration.hasAnnotationSafe(suppliedClassId, context.session)
//        val containingDeclarationSymbol = declaration.containingDeclarationSymbol
//
//        when (containingDeclarationSymbol) {
//            is FirCallableSymbol<*> -> {}
//            is FirClassifierSymbol<*> -> {
////                            reporter.reportOn()
//            }
//        }
//
//        println(
//            """
//                            ===========================
//                            $declaration
//                            ${declaration.symbol}
//                            ${declaration.symbol.containingDeclarationSymbol}
//                            ${declaration.containingDeclarationSymbol}
//                            ${declaration.origin}
//                            ${declaration.source}
//                            ${declaration.getContainingClassSymbol()}
//                            $isSupplied
//                            ===========================
//                        """.trimIndent()
//        )
//    }
//}