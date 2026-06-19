/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.fir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.plugin.suppliedTypes.noSuppliedTypeParameterInClassStubParameterName
import dev.lounres.kone.util.mapOperations.copyTo
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.collectEnumEntries
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.*
import org.jetbrains.kotlin.fir.expressions.impl.FirEmptyAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.scopes.getDeclaredConstructors
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.types.ConstantValueKind


class SuppliableConstructorsDuplicatesGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "SuppliableConstructorsDuplicatesGenerationExtension.Key"
    }
    
    private val utils = SuppliedTypeGenerationExtensionUtils(session)
    
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(SuppliedTypeGenerationExtensionUtils.PREDICATES)
    }
    
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> = with(utils) {
        if (classSymbol.isSuppliable) setOf(SpecialNames.INIT)
        else emptySet()
    }
    
    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> = with(utils) {
        val classSymbol = context.owner
        if (classSymbol.classKind !in listOf<ClassKind>(CLASS)) return emptyList()
        val suppliedTypeArguments = classSymbol.ownTypeParameterSymbols.filter { it.isSupply }
        if (suppliedTypeArguments.isNotEmpty()) {
            context.declaredScope!!.getDeclaredConstructors().map { suppliedConstructor ->
                createConstructor(
                    owner = classSymbol,
                    key = Key,
                ) {
                    source = suppliedConstructor.source
                    visibility = suppliedConstructor.rawStatus.visibility.takeIf { it != Visibilities.Unknown } ?: Visibilities.DEFAULT_VISIBILITY
                    suppliedConstructor.rawStatus.modality?.let { modality = it }
                    status {
                        isExpect = suppliedConstructor.rawStatus.isExpect
                        isActual = suppliedConstructor.rawStatus.isActual
                        isOverride = suppliedConstructor.rawStatus.isOverride
                        isInline = suppliedConstructor.rawStatus.isInline
                        isTailRec = suppliedConstructor.rawStatus.isTailRec
                        isExternal = suppliedConstructor.rawStatus.isExternal
                        isConst = suppliedConstructor.rawStatus.isConst
                        isLateInit = suppliedConstructor.rawStatus.isLateInit
                        isInner = suppliedConstructor.rawStatus.isInner
                        isCompanion = suppliedConstructor.rawStatus.isCompanion
                        isSuspend = suppliedConstructor.rawStatus.isSuspend
                        isStatic = suppliedConstructor.rawStatus.isStatic
                        isFromSealedClass = suppliedConstructor.rawStatus.isFromSealedClass
                        isFromEnumClass = suppliedConstructor.rawStatus.isFromEnumClass
                        isFun = suppliedConstructor.rawStatus.isFun
                    }
                    for (typeParameterSymbol in suppliedTypeArguments) {
                        valueParameter(
                            name = internalSupplierParameterName(typeParameterSymbol.name),
                            type = suppliedTypeConeClassLikeType,
                            hasDefaultValue = true,
                            key = Key,
                        )
                    }
                    for (valueParameterSymbol in suppliedConstructor.valueParameterSymbols) {
                        valueParameter(
                            name = valueParameterSymbol.name,
                            type = valueParameterSymbol.resolvedReturnType,
                            isCrossinline = valueParameterSymbol.isCrossinline,
                            isNoinline = valueParameterSymbol.isNoinline,
                            isVararg = valueParameterSymbol.isVararg,
                            hasDefaultValue = valueParameterSymbol.hasDefaultValue,
                            key = Key,
                        )
                    }
                }.apply {
                    val supplianceConstructor = this
                    repeat(suppliedTypeArguments.size) {
                        valueParameters[it].apply {
                            replaceAnnotations(
                                listOf(
                                    buildAnnotation {
                                        annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                                        argumentMapping = FirEmptyAnnotationArgumentMapping
                                    }
                                )
                            )
                        }
                    }
                    replaceAnnotations(
                        buildList {
                            this += buildAnnotation {
                                annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                                argumentMapping = FirEmptyAnnotationArgumentMapping
                            }
                            this += suppliedConstructor.resolvedAnnotationsWithArguments
                        }
                    )
                    replaceDelegatedConstructor(
                        buildDelegatedConstructorCall {
                            constructedTypeRef = classSymbol.defaultType().toFirResolvedTypeRef()
                            calleeReference = buildResolvedNamedReference {
                                name = SpecialNames.INIT
                                resolvedSymbol = suppliedConstructor
                            }
                            isThis = true
                            val suppliedConstructorValueParametersToUse = supplianceConstructor.valueParameters.drop(suppliedTypeArguments.size)
                            val suppliedConstructorValueParametersAccessExpression =
                                suppliedConstructorValueParametersToUse.map {
                                    buildPropertyAccessExpression {
                                        calleeReference = buildResolvedNamedReference {
                                            name = it.name
                                            resolvedSymbol = it.symbol
                                        }
                                        coneTypeOrNull = it.symbol.resolvedReturnType
                                    }
                                }
                            
                            @OptIn(SymbolInternals::class)
                            argumentList = buildResolvedArgumentList(
                                buildArgumentList {
                                    arguments += suppliedConstructorValueParametersAccessExpression
                                },
                                mapping = suppliedConstructorValueParametersAccessExpression.zip(suppliedConstructor.valueParameterSymbols.map { it.fir }).toMap().copyTo(LinkedHashMap())
                            )
                        }
                    )
                }.symbol
            }
        } else {
            context.declaredScope!!.getDeclaredConstructors().map { suppliedConstructor ->
                createConstructor(
                    owner = classSymbol,
                    key = Key,
                ) {
                    source = suppliedConstructor.source
                    visibility = suppliedConstructor.rawStatus.visibility.takeIf { it != Visibilities.Unknown } ?: Visibilities.DEFAULT_VISIBILITY
                    suppliedConstructor.rawStatus.modality?.let { modality = it }
                    status {
                        isExpect = suppliedConstructor.rawStatus.isExpect
                        isActual = suppliedConstructor.rawStatus.isActual
                        isOverride = suppliedConstructor.rawStatus.isOverride
                        isInline = suppliedConstructor.rawStatus.isInline
                        isTailRec = suppliedConstructor.rawStatus.isTailRec
                        isExternal = suppliedConstructor.rawStatus.isExternal
                        isConst = suppliedConstructor.rawStatus.isConst
                        isLateInit = suppliedConstructor.rawStatus.isLateInit
                        isInner = suppliedConstructor.rawStatus.isInner
                        isCompanion = suppliedConstructor.rawStatus.isCompanion
                        isSuspend = suppliedConstructor.rawStatus.isSuspend
                        isStatic = suppliedConstructor.rawStatus.isStatic
                        isFromSealedClass = suppliedConstructor.rawStatus.isFromSealedClass
                        isFromEnumClass = suppliedConstructor.rawStatus.isFromEnumClass
                        isFun = suppliedConstructor.rawStatus.isFun
                    }
                    valueParameter(
                        name = noSuppliedTypeParameterInClassStubParameterName,
                        type = noSuppliedTypeParameterInClassStubConeClassLikeType,
                        hasDefaultValue = true,
                        key = Key,
                    )
                    for (valueParameterSymbol in suppliedConstructor.valueParameterSymbols) {
                        valueParameter(
                            name = valueParameterSymbol.name,
                            type = valueParameterSymbol.resolvedReturnType,
                            isCrossinline = valueParameterSymbol.isCrossinline,
                            isNoinline = valueParameterSymbol.isNoinline,
                            isVararg = valueParameterSymbol.isVararg,
                            hasDefaultValue = valueParameterSymbol.hasDefaultValue,
                            key = Key,
                        )
                    }
                }.apply {
                    val supplianceConstructor = this
                    valueParameters[0].apply {
                        replaceAnnotations(
                            listOf(
                                buildAnnotation {
                                    annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                                    argumentMapping = FirEmptyAnnotationArgumentMapping
                                }
                            )
                        )
                    }
                    replaceAnnotations(
                        buildList {
                            this += buildAnnotation {
                                annotationTypeRef = supplianceProvidedFirResolvedTypeRef
                                argumentMapping = FirEmptyAnnotationArgumentMapping
                            }
                            this += buildAnnotation {
                                annotationTypeRef = deprecatedFirResolvedTypeRef
                                argumentMapping = buildAnnotationArgumentMapping {
                                    mapping[Name.identifier("message")] = buildLiteralExpression(
                                        source = null,
                                        kind = ConstantValueKind.String,
                                        value = "Pseudo-supplied types's constructor. Should be used by compiler plugin only.",
                                        setType = true,
                                    )
                                    mapping[Name.identifier("level")] = buildPropertyAccessExpression {
                                        coneTypeOrNull = deprecationLevelConeClassLikeType
                                        val receiver = buildResolvedQualifier {
                                            coneTypeOrNull = session.builtinTypes.unitType.coneType
                                            packageFqName = deprecationLevelClassLikeSymbol.classId.packageFqName
                                            relativeClassFqName = deprecationLevelClassLikeSymbol.classId.relativeClassName
                                            symbol = deprecationLevelClassLikeSymbol
                                            resolvedToCompanionObject = false
                                        }
                                        explicitReceiver = receiver
                                        dispatchReceiver = receiver
                                        calleeReference = buildResolvedNamedReference {
                                            name = Name.identifier("HIDDEN")
                                            resolvedSymbol = deprecationLevelClassLikeSymbol.collectEnumEntries(session).single { it.name == Name.identifier("HIDDEN") }
                                        }
                                    }
                                }
                            }
                            this += suppliedConstructor.resolvedAnnotationsWithArguments
                        }
                    )
                    replaceDelegatedConstructor(
                        buildDelegatedConstructorCall {
                            constructedTypeRef = classSymbol.defaultType().toFirResolvedTypeRef()
                            calleeReference = buildResolvedNamedReference {
                                name = SpecialNames.INIT
                                resolvedSymbol = suppliedConstructor
                            }
                            isThis = true
                            val suppliedConstructorValueParametersToUse = supplianceConstructor.valueParameters.drop(1)
                            val suppliedConstructorValueParametersAccessExpression =
                                suppliedConstructorValueParametersToUse.map {
                                    buildPropertyAccessExpression {
                                        calleeReference = buildResolvedNamedReference {
                                            name = it.name
                                            resolvedSymbol = it.symbol
                                        }
                                        coneTypeOrNull = it.symbol.resolvedReturnType
                                    }
                                }
                            
                            @OptIn(SymbolInternals::class)
                            argumentList = buildResolvedArgumentList(
                                buildArgumentList {
                                    arguments += suppliedConstructorValueParametersAccessExpression
                                },
                                mapping = suppliedConstructorValueParametersAccessExpression.zip(suppliedConstructor.valueParameterSymbols.map { it.fir }).toMap().copyTo(LinkedHashMap())
                            )
                        }
                    )
                }.symbol
            }
        }
    }
}