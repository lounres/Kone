/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.fir

import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.koneBenchmarkRunnerAnnotationClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkSupportEntryPointCallableId
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.builder.buildNamedFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.providers.dependenciesSymbolProvider
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.equalTypes
import org.jetbrains.kotlin.fir.types.typeContext
import org.jetbrains.kotlin.fir.types.withNullability
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


class BenchmarksRunnersKotlinxBenchmarkSupportEntryPointGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "BenchmarksRunnersKotlinxBenchmarkSupportEntryPointGenerationExtension.Key"
    }
    
    override fun hasPackage(packageFqName: FqName): Boolean = packageFqName == kotlinxBenchmarkSupportEntryPointCallableId.packageName
    
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelCallableIds(): Set<CallableId> =
        setOfNotNull(
            kotlinxBenchmarkSupportEntryPointCallableId.takeIf {
                session.dependenciesSymbolProvider
                    .getTopLevelFunctionSymbols(kotlinxBenchmarkSupportEntryPointCallableId.packageName, kotlinxBenchmarkSupportEntryPointCallableId.callableName)
                    .all {
                        // TODO
                        if (it.typeParameterSymbols.isNotEmpty()) return@all true
                        if (it.contextParameterSymbols.isNotEmpty()) return@all true
                        if (it.receiverParameterSymbol != null) return@all true
                        if (it.valueParameterSymbols.size != 17) return@all true
                        if (
                            !it.valueParameterSymbols[0].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.stringType.coneType,
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[1].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.stringType.coneType,
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[2].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.stringType.coneType,
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[3].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.stringType.coneType.withNullability(
                                    nullable = true,
                                    typeContext = session.typeContext,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[4].resolvedReturnType.equalTypes(
                                otherType = session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("kotlin.collections"),
                                        relativeClassName = FqName("Map"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(
                                        session.builtinTypes.stringType.coneType,
                                        session.symbolProvider.getClassLikeSymbolByClassId(
                                            ClassId(
                                                packageFqName = FqName("kotlin.collections"),
                                                relativeClassName = FqName("List"),
                                                isLocal = false,
                                            ),
                                        )!!.constructType(
                                            typeArguments = arrayOf(
                                                session.builtinTypes.stringType.coneType,
                                            ),
                                            isMarkedNullable = false,
                                        ),
                                    ),
                                    isMarkedNullable = false,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[5].resolvedReturnType.equalTypes(
                                otherType = session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("kotlin.collections"),
                                        relativeClassName = FqName("List"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(
                                        session.builtinTypes.stringType.coneType,
                                    ),
                                    isMarkedNullable = false,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[6].resolvedReturnType.equalTypes(
                                otherType = session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("kotlin.collections"),
                                        relativeClassName = FqName("List"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(
                                        session.builtinTypes.stringType.coneType,
                                    ),
                                    isMarkedNullable = false,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[7].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.intType.coneType.withNullability(
                                    nullable = true,
                                    typeContext = session.typeContext,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[8].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.intType.coneType.withNullability(
                                    nullable = true,
                                    typeContext = session.typeContext,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[9].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.longType.coneType.withNullability(
                                    nullable = true,
                                    typeContext = session.typeContext,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[10].resolvedReturnType.equalTypes(
                                otherType = session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal"),
                                        relativeClassName = FqName("BenchmarkTimeUnitWrapper"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(),
                                    isMarkedNullable = true,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[11].resolvedReturnType.equalTypes(
                                otherType = session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("kotlin.collections"),
                                        relativeClassName = FqName("Map"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(
                                        session.builtinTypes.stringType.coneType,
                                        session.builtinTypes.stringType.coneType,
                                    ),
                                    isMarkedNullable = false,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[12].resolvedReturnType.equalTypes(
                                otherType = session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal"),
                                        relativeClassName = FqName("BenchmarkTimeUnitWrapper"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(),
                                    isMarkedNullable = true,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[13].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.intType.coneType.withNullability(
                                    nullable = true,
                                    typeContext = session.typeContext,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[14].resolvedReturnType.equalTypes(
                                otherType = session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal"),
                                        relativeClassName = FqName("ModeWrapper"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(),
                                    isMarkedNullable = true,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[15].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.stringType.coneType.withNullability(
                                    nullable = true,
                                    typeContext = session.typeContext,
                                ),
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        if (
                            !it.valueParameterSymbols[16].resolvedReturnType.equalTypes(
                                otherType = session.builtinTypes.stringType.coneType,
                                session = session,
                                errorTypesEqualToAnything = false,
                            )
                        ) return@all true
                        false
                    }
            }
        )
    
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        if (callableId != kotlinxBenchmarkSupportEntryPointCallableId || context != null) return emptyList()
        return listOf(
            buildNamedFunction {
                moduleData = session.moduleData
                origin = Key.origin
                status = FirResolvedDeclarationStatusImpl(
                    visibility = Visibilities.Public,
                    modality = Modality.FINAL,
                    effectiveVisibility = Public,
                )
                isLocal = false
                returnTypeRef = session.builtinTypes.unitType
                name = callableId.callableName
                symbol = FirNamedFunctionSymbol(callableId)
                
                annotations += buildAnnotation {
                    annotationTypeRef = session.symbolProvider.getClassLikeSymbolByClassId(koneBenchmarkRunnerAnnotationClassId)!!.constructType().toFirResolvedTypeRef()
                    argumentMapping = buildAnnotationArgumentMapping {}
                }
                
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.stringType.coneType
                    }
                    name = Name.identifier("name")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.stringType.coneType
                    }
                    name = Name.identifier("reportFile")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.stringType.coneType
                    }
                    name = Name.identifier("traceFormat")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.stringType.coneType.withNullability(
                            nullable = true,
                            typeContext = session.typeContext,
                        )
                    }
                    name = Name.identifier("reportFormat")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider.getClassLikeSymbolByClassId(
                            ClassId(
                                packageFqName = FqName("kotlin.collections"),
                                relativeClassName = FqName("Map"),
                                isLocal = false,
                            ),
                        )!!.constructType(
                            typeArguments = arrayOf(
                                session.builtinTypes.stringType.coneType,
                                session.symbolProvider.getClassLikeSymbolByClassId(
                                    ClassId(
                                        packageFqName = FqName("kotlin.collections"),
                                        relativeClassName = FqName("List"),
                                        isLocal = false,
                                    ),
                                )!!.constructType(
                                    typeArguments = arrayOf(
                                        session.builtinTypes.stringType.coneType,
                                    ),
                                    isMarkedNullable = false,
                                ),
                            ),
                            isMarkedNullable = false,
                        )
                    }
                    name = Name.identifier("params")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider.getClassLikeSymbolByClassId(
                            ClassId(
                                packageFqName = FqName("kotlin.collections"),
                                relativeClassName = FqName("List"),
                                isLocal = false,
                            ),
                        )!!.constructType(
                            typeArguments = arrayOf(
                                session.builtinTypes.stringType.coneType,
                            ),
                            isMarkedNullable = false,
                        )
                    }
                    name = Name.identifier("include")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider.getClassLikeSymbolByClassId(
                            ClassId(
                                packageFqName = FqName("kotlin.collections"),
                                relativeClassName = FqName("List"),
                                isLocal = false,
                            ),
                        )!!.constructType(
                            typeArguments = arrayOf(
                                session.builtinTypes.stringType.coneType,
                            ),
                            isMarkedNullable = false,
                        )
                    }
                    name = Name.identifier("exclude")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.intType.coneType.withNullability(
                            nullable = true,
                            typeContext = session.typeContext,
                        )
                    }
                    name = Name.identifier("iterations")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.intType.coneType.withNullability(
                            nullable = true,
                            typeContext = session.typeContext,
                        )
                    }
                    name = Name.identifier("warmups")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.longType.coneType.withNullability(
                            nullable = true,
                            typeContext = session.typeContext,
                        )
                    }
                    name = Name.identifier("iterationTime")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider.getClassLikeSymbolByClassId(
                            ClassId(
                                packageFqName = FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal"),
                                relativeClassName = FqName("BenchmarkTimeUnitWrapper"),
                                isLocal = false,
                            ),
                        )!!.constructType(
                            typeArguments = arrayOf(),
                            isMarkedNullable = true,
                        )
                    }
                    name = Name.identifier("iterationTimeUnit")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider.getClassLikeSymbolByClassId(
                            ClassId(
                                packageFqName = FqName("kotlin.collections"),
                                relativeClassName = FqName("Map"),
                                isLocal = false,
                            ),
                        )!!.constructType(
                            typeArguments = arrayOf(
                                session.builtinTypes.stringType.coneType,
                                session.builtinTypes.stringType.coneType,
                            ),
                            isMarkedNullable = false,
                        )
                    }
                    name = Name.identifier("advanced")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider.getClassLikeSymbolByClassId(
                            ClassId(
                                packageFqName = FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal"),
                                relativeClassName = FqName("BenchmarkTimeUnitWrapper"),
                                isLocal = false,
                            ),
                        )!!.constructType(
                            typeArguments = arrayOf(),
                            isMarkedNullable = true,
                        )
                    }
                    name = Name.identifier("outputTimeUnit")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.intType.coneType.withNullability(
                            nullable = true,
                            typeContext = session.typeContext,
                        )
                    }
                    name = Name.identifier("threads")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.symbolProvider.getClassLikeSymbolByClassId(
                            ClassId(
                                packageFqName = FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal"),
                                relativeClassName = FqName("ModeWrapper"),
                                isLocal = false,
                            ),
                        )!!.constructType(
                            typeArguments = arrayOf(),
                            isMarkedNullable = true,
                        )
                    }
                    name = Name.identifier("mode")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.stringType.coneType.withNullability(
                            nullable = true,
                            typeContext = session.typeContext,
                        )
                    }
                    name = Name.identifier("compilationMode")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
                valueParameters += buildValueParameter {
                    moduleData = session.moduleData
                    origin = Key.origin
                    returnTypeRef = buildResolvedTypeRef {
                        coneType = session.builtinTypes.stringType.coneType
                    }
                    name = Name.identifier("configurationName")
                    symbol = FirValueParameterSymbol()
                    containingDeclarationSymbol = this@buildNamedFunction.symbol
                }
            }.symbol
        )
    }
}