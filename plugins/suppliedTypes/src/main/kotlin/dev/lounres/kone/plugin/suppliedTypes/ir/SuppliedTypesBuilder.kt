/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrScriptSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance


class SuppliedTypesBuilder(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
    private val irStatementsBuilder: IrStatementsBuilder<*>,
    initialSuppliedTypes: Map<IrType, Lazy<IrVariable>>,
) {
    private val declarationFinder = pluginContext.finderForBuiltins()
    
    private val listOfIrSimpleFunction: IrSimpleFunctionSymbol =
        declarationFinder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
            val parameters = symbol.owner.parameters
            parameters.size == 1 && parameters[0].isVararg
        }
    private val kVarianceIrClass = declarationFinder.referenceClassOrFail(ClassId(FqName("kotlin.reflect"), FqName("KVariance"), false)).owner
    private val kVarianceIrEnumEntries = kVarianceIrClass.declarations.filterIsInstance<IrEnumEntry>()
    private val kVarianceINVARIANTIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("INVARIANT") }
    private val kVarianceINIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("IN") }
    private val kVarianceOUTIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("OUT") }
    
    private val suppliedTypes = initialSuppliedTypes.toMutableMap()
    
    private fun IrBuilderWithScope.irGetEnumEntry(enumEntry: IrEnumEntry) =
        IrGetEnumValueImpl(startOffset, endOffset, enumEntry.parentAsClass.defaultType, enumEntry.symbol)
    
    fun resolve(type: IrType): IrVariable = suppliedTypes.getOrPut(type) {
        lazy {
            with (irStatementsBuilder) {
                irTemporary(
                    value = when (type) {
                        is IrDynamicType -> irGetObject(irRuntimeReferences.suppliedTypeDynamicIrClassSymbol)
                        is IrErrorType -> TODO()
                        is IrSimpleType -> when (val classifierSymbol = type.classifier) {
                            is IrClassSymbol -> {
                                val irClass = classifierSymbol.owner
                                val fullyQualifiedName: String = irClass.fqNameStringForSuppliedTypes
                                val typeArguments: List<IrExpression> = irClass.typeParameters.zip(type.arguments).map { [typeParameter, typeArgument] ->
                                    when (typeArgument) {
                                        is IrStarProjection -> irGetObject(irRuntimeReferences.suppliedProjectionStarIrClassSymbol)
                                        is IrTypeProjection -> {
                                            val variance = when (typeParameter.variance) {
                                                Variance.INVARIANT -> when (typeArgument.variance) {
                                                    Variance.INVARIANT -> irGetEnumEntry(kVarianceINVARIANTIrEnumEntry)
                                                    Variance.IN_VARIANCE -> irGetEnumEntry(kVarianceINIrEnumEntry)
                                                    Variance.OUT_VARIANCE -> irGetEnumEntry(kVarianceOUTIrEnumEntry)
                                                }
                                                Variance.IN_VARIANCE -> irGetEnumEntry(kVarianceINIrEnumEntry)
                                                Variance.OUT_VARIANCE -> irGetEnumEntry(kVarianceOUTIrEnumEntry)
                                            }
                                            val typeVariable = resolve(typeArgument.type)
                                            irCallConstructor(
                                                irRuntimeReferences.suppliedProjectionRegularIrClassSymbol.constructors.single {
                                                    it.owner.parameters.let { it.size == 2 && it[0].name.toString() == "variance" && it[1].name.toString() == "type" }
                                                },
                                                emptyList()
                                            ).apply {
                                                arguments[0] = variance
                                                arguments[1] = irGet(typeVariable)
                                            }
                                        }
                                    }
                                }
                                val isNullable: Boolean = type.nullability == SimpleTypeNullability.MARKED_NULLABLE
                                irCallConstructor(
                                    irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single {
                                        it.owner.parameters.let { it.size == 3 && it[0].name.toString() == "fullyQualifiedName" && it[1].name.toString() == "typeArguments" && it[2].name.toString() == "isNullable" }
                                    },
                                    emptyList(),
                                ).apply {
                                    arguments[0] = irString(fullyQualifiedName)
                                    arguments[1] = irCall(listOfIrSimpleFunction).also {
                                        it.typeArguments[0] = irRuntimeReferences.suppliedProjectionIrType
                                        it.arguments[0] = irVararg(irRuntimeReferences.suppliedProjectionIrType, typeArguments)
                                    }
                                    arguments[2] = irBoolean(isNullable)
                                }
                            }
                            is IrScriptSymbol -> {
                                val irClass = classifierSymbol.owner.targetClass!!.owner
                                val fullyQualifiedName: String = irClass.fqNameStringForSuppliedTypes
                                val typeArguments: List<IrExpression> = irClass.typeParameters.zip(type.arguments).map { [typeParameter, typeArgument] ->
                                    when (typeArgument) {
                                        is IrStarProjection -> irGetObject(irRuntimeReferences.suppliedProjectionStarIrClassSymbol)
                                        is IrTypeProjection -> {
                                            val variance = when (typeParameter.variance) {
                                                Variance.INVARIANT -> when (typeArgument.variance) {
                                                    Variance.INVARIANT -> irGetEnumEntry(kVarianceINVARIANTIrEnumEntry)
                                                    Variance.IN_VARIANCE -> irGetEnumEntry(kVarianceINIrEnumEntry)
                                                    Variance.OUT_VARIANCE -> irGetEnumEntry(kVarianceOUTIrEnumEntry)
                                                }
                                                Variance.IN_VARIANCE -> irGetEnumEntry(kVarianceINIrEnumEntry)
                                                Variance.OUT_VARIANCE -> irGetEnumEntry(kVarianceOUTIrEnumEntry)
                                            }
                                            val typeVariable = resolve(typeArgument.type)
                                            irCallConstructor(
                                                irRuntimeReferences.suppliedProjectionRegularIrClassSymbol.constructors.single {
                                                    it.owner.parameters.let { it.size == 2 && it[0].name.toString() == "variance" && it[1].name.toString() == "type" }
                                                },
                                                emptyList()
                                            ).apply {
                                                arguments[0] = variance
                                                arguments[1] = irGet(typeVariable)
                                            }
                                        }
                                    }
                                }
                                val isNullable: Boolean = type.nullability == SimpleTypeNullability.MARKED_NULLABLE
                                irCallConstructor(
                                    irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single {
                                        it.owner.parameters.let { it.size == 3 && it[0].name.toString() == "fullyQualifiedName" && it[1].name.toString() == "typeArguments" && it[2].name.toString() == "isNullable" }
                                    },
                                    emptyList(),
                                ).apply {
                                    arguments[0] = irString(fullyQualifiedName)
                                    arguments[1] = irCall(listOfIrSimpleFunction).also {
                                        it.typeArguments[0] = irRuntimeReferences.suppliedProjectionIrType
                                        it.arguments[0] = irVararg(irRuntimeReferences.suppliedProjectionIrType, typeArguments)
                                    }
                                    arguments[2] = irBoolean(isNullable)
                                }
                            }
                            is IrTypeParameterSymbol -> {
                                val typeParameter = classifierSymbol.owner
                                val typeParameterType = typeParameter.defaultType
                                if (typeParameterType == type)
                                    error("Supplied type is not provided but was requested for type parameter: $classifierSymbol.")
                                val suppliedTypeVariable = resolve(typeParameterType)
                                val nullability = type.nullability
                                
                                when (nullability) {
                                    SimpleTypeNullability.NOT_SPECIFIED -> error("For some reason supplied type for type parameter is provided but not for its 'NOT_SPECIFIED' nullability.")
                                    SimpleTypeNullability.MARKED_NULLABLE -> irWhen(
                                        type = irRuntimeReferences.suppliedTypeIrType,
                                        branches = listOf(
                                            irBranch(
                                                condition = irIs(
                                                    argument = irGet(suppliedTypeVariable),
                                                    type = irRuntimeReferences.suppliedTypeRegularIrType,
                                                ),
                                                result = irBlock {
                                                    val castedSuppliedTypeVariable = irTemporary(
                                                        value = irImplicitCast(
                                                            argument = irGet(suppliedTypeVariable),
                                                            type = irRuntimeReferences.suppliedTypeRegularIrType,
                                                        )
                                                    )
                                                    
                                                    irCallConstructor(
                                                        irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single {
                                                            it.owner.parameters.let { it.size == 3 && it[0].name.toString() == "fullyQualifiedName" && it[1].name.toString() == "typeArguments" && it[2].name.toString() == "isNullable" }
                                                        },
                                                        emptyList(),
                                                    ).apply {
                                                        arguments[0] = irCall(
                                                            irRuntimeReferences.suppliedTypeRegularIrClassSymbol.owner.properties.single { it.name.identifier == "fullyQualifiedName" }.getter!!
                                                        ).apply {
                                                            arguments[0] = irGet(castedSuppliedTypeVariable)
                                                        }
                                                        arguments[1] = irCall(
                                                            irRuntimeReferences.suppliedTypeRegularIrClassSymbol.owner.properties.single { it.name.identifier == "typeArguments" }.getter!!
                                                        ).also {
                                                            arguments[0] = irGet(castedSuppliedTypeVariable)
                                                        }
                                                        arguments[2] = irBoolean(true)
                                                    }
                                                },
                                            ),
                                            irBranch(
                                                condition = irIs(
                                                    argument = irGet(suppliedTypeVariable),
                                                    type = irRuntimeReferences.suppliedTypeDynamicIrType,
                                                ),
                                                result = irGet(suppliedTypeVariable),
                                            ),
                                            irElseBranch(
                                                irCall(pluginContext.irBuiltIns.noWhenBranchMatchedExceptionSymbol)
                                            ),
                                        )
                                    )
                                    SimpleTypeNullability.DEFINITELY_NOT_NULL -> irWhen(
                                        type = irRuntimeReferences.suppliedTypeIrType,
                                        branches = listOf(
                                            irBranch(
                                                condition = irIs(
                                                    argument = irGet(suppliedTypeVariable),
                                                    type = irRuntimeReferences.suppliedTypeRegularIrType,
                                                ),
                                                result = irBlock {
                                                    val castedSuppliedTypeVariable = irTemporary(
                                                        value = irImplicitCast(
                                                            argument = irGet(suppliedTypeVariable),
                                                            type = irRuntimeReferences.suppliedTypeRegularIrType,
                                                        )
                                                    )
                                                    
                                                    irCallConstructor(
                                                        irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single {
                                                            it.owner.parameters.let { it.size == 3 && it[0].name.toString() == "fullyQualifiedName" && it[1].name.toString() == "typeArguments" && it[2].name.toString() == "isNullable" }
                                                        },
                                                        emptyList(),
                                                    ).apply {
                                                        arguments[0] = irCall(
                                                            irRuntimeReferences.suppliedTypeRegularIrClassSymbol.owner.properties.single { it.name.identifier == "fullyQualifiedName" }.getter!!
                                                        ).apply {
                                                            arguments[0] = irGet(castedSuppliedTypeVariable)
                                                        }
                                                        arguments[1] = irCall(
                                                            irRuntimeReferences.suppliedTypeRegularIrClassSymbol.owner.properties.single { it.name.identifier == "typeArguments" }.getter!!
                                                        ).also {
                                                            arguments[0] = irGet(castedSuppliedTypeVariable)
                                                        }
                                                        arguments[2] = irBoolean(false)
                                                    }
                                                },
                                            ),
                                            irBranch(
                                                condition = irIs(
                                                    argument = irGet(suppliedTypeVariable),
                                                    type = irRuntimeReferences.suppliedTypeDynamicIrType,
                                                ),
                                                result = irGet(suppliedTypeVariable),
                                            ),
                                            irElseBranch(
                                                irCall(pluginContext.irBuiltIns.noWhenBranchMatchedExceptionSymbol)
                                            ),
                                        )
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }.value
}