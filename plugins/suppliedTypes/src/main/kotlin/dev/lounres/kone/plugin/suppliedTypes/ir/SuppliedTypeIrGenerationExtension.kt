/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeOfCallableId
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance


class SuppliedTypeIrGenerationExtension(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    typealias Phase = (
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
        irRuntimeReferences: IrRuntimeReferences,
        suppliabilityCollectionVisitor: SuppliabilityCollectionVisitor,
    ) -> Unit
    
    companion object {
        val phases: List<Phase> = listOf(
            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityCollectionVisitor ->
                supplyFunctionsParameters(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    suppliabilityCollectionVisitor = suppliabilityCollectionVisitor,
                )
                
                supplyConstructorsParameters(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    suppliabilityCollectionVisitor = suppliabilityCollectionVisitor,
                )
            },
            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityCollectionVisitor ->
                moduleFragment.transform(
                    SuppliableCallSubstitutionTransformer(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        suppliabilityCollectionVisitor = suppliabilityCollectionVisitor,
                    ),
                    null,
                )
            },
            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityCollectionVisitor ->
                supplyFunctionsBodies(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    suppliabilityCollectionVisitor = suppliabilityCollectionVisitor,
                )
                
                supplyConstructorsBodies(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    suppliabilityCollectionVisitor = suppliabilityCollectionVisitor,
                )
            },
//            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityCollectionVisitor ->
//
//            },
//            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityCollectionVisitor ->
//                moduleFragment.transform(
//                    SuppliedTypeOfSubstitutionTransformer(
//                        pluginContext = pluginContext,
//                        irRuntimeReferences = irRuntimeReferences,
//                    ),
//                    SuppliedTypeOfSubstitutionTransformer.TransformationContext(emptyMap(), emptyMap(), null)
//                )
//            },
        )
    }
    
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences: IrRuntimeReferences = IrRuntimeReferences(pluginContext)
        val suppliabilityCollectionVisitor: SuppliabilityCollectionVisitor = SuppliabilityCollectionVisitor(
            pluginContext = pluginContext,
            irRuntimeReferences = irRuntimeReferences,
        )
        
        moduleFragment.acceptVoid(suppliabilityCollectionVisitor)
        
        for (phase in phases) phase(moduleFragment, pluginContext, irRuntimeReferences, suppliabilityCollectionVisitor)
    }
}

// region Phase 5
fun IrBuilderWithScope.irGetEnumEntry(enumEntry: IrEnumEntry) =
    IrGetEnumValueImpl(startOffset, endOffset, enumEntry.parentAsClass.defaultType, enumEntry.symbol)

class SuppliedTypeOfSubstitutionTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<SuppliedTypeOfSubstitutionTransformer.TransformationContext>() {
    private val SUPPLIED_TYPE_OF_SUBSTITUTION_ORIGIN by IrDeclarationOriginImpl.Regular
    private val SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN by IrStatementOriginImpl
    
    data class TransformationContext(
        val typeParametersMapping: Map<IrTypeParameter, DeclarationIrBuilder.(dispatchReceiversMapping: Map<IrClassSymbol, IrValueParameter>) -> IrExpression>,
        val dispatchReceiversMapping: Map<IrClassSymbol, IrValueParameter>,
        val localSymbol: IrSymbol?,
    )
    
    private val listOfIrSimpleFunction: IrSimpleFunctionSymbol =
        pluginContext.finderForBuiltins().referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
            val parameters = symbol.owner.parameters
            parameters.size == 1 && parameters[0].isVararg
        }
    private val kVarianceIrClass = pluginContext.finderForBuiltins().referenceClassOrFail(ClassId(FqName("kotlin.reflect"), FqName("KVariance"), false)).owner
    private val kVarianceIrEnumEntries = kVarianceIrClass.declarations.filterIsInstance<IrEnumEntry>()
    private val kVarianceINVARIANTIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("INVARIANT") }
    private val kVarianceINIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("IN") }
    private val kVarianceOUTIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("OUT") }
    
    private fun DeclarationIrBuilder.suppliedTypeExpressionFor(
        type: IrType,
        transformationContext: TransformationContext,
    ): IrExpression =
        when(type) {
            is IrDynamicType -> irGetObject(irRuntimeReferences.suppliedTypeDynamicIrClassSymbol)
            is IrErrorType -> TODO()
            is IrSimpleType -> when (val classifier = type.classifier) {
                is IrClassSymbol -> {
                    val irClass = classifier.owner
                    val fullyQualifiedName: String = irClass.fqName.toString()
                    val typeArguments: List<IrExpression> = type.arguments.map {
                        when (it) {
                            is IrStarProjection -> irGetObject(irRuntimeReferences.suppliedProjectionStarIrClassSymbol)
                            is IrTypeProjection -> {
                                val variance = when (it.variance) {
                                    Variance.INVARIANT -> irGetEnumEntry(kVarianceINVARIANTIrEnumEntry)
                                    Variance.IN_VARIANCE -> irGetEnumEntry(kVarianceINIrEnumEntry)
                                    Variance.OUT_VARIANCE -> irGetEnumEntry(kVarianceOUTIrEnumEntry)
                                }
                                val type = suppliedTypeExpressionFor(it.type, transformationContext)
                                irCallConstructor(
                                    irRuntimeReferences.suppliedProjectionRegularIrClassSymbol.constructors.single { it.owner.parameters.size == 2 },
                                    emptyList()
                                ).apply {
                                    arguments.clear()
                                    arguments.add(variance)
                                    arguments.add(type)
                                    origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                                }
                            }
                        }
                    }
                    val isNullable: Boolean = type.nullability == SimpleTypeNullability.MARKED_NULLABLE
                    irCallConstructor(
                        irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single { it.owner.parameters.size == 3 },
                        emptyList(),
                    ).apply {
                        arguments.clear()
                        arguments.add(irString(fullyQualifiedName))
                        arguments.add(
                            irCall(listOfIrSimpleFunction).also {
                                it.typeArguments.clear()
                                it.typeArguments.add(irRuntimeReferences.suppliedProjectionIrType)
                                it.arguments.clear()
                                it.arguments.add(irVararg(irRuntimeReferences.suppliedProjectionIrType, typeArguments))
                                it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                            }
                        )
                        arguments.add(irBoolean(isNullable))
                        origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                    }
                }
                is IrScriptSymbol -> {
                    val irClass = classifier.owner.targetClass!!.owner
                    val fullyQualifiedName: String = irClass.fqName.toString()
                    val typeArguments: List<IrExpression> = type.arguments.map {
                        when (it) {
                            is IrStarProjection -> irGetObject(irRuntimeReferences.suppliedProjectionStarIrClassSymbol)
                            is IrTypeProjection -> {
                                val variance = when (it.variance) {
                                    Variance.INVARIANT -> irGetEnumEntry(kVarianceINVARIANTIrEnumEntry)
                                    Variance.IN_VARIANCE -> irGetEnumEntry(kVarianceINIrEnumEntry)
                                    Variance.OUT_VARIANCE -> irGetEnumEntry(kVarianceOUTIrEnumEntry)
                                }
                                val type = suppliedTypeExpressionFor(it.type, transformationContext)
                                irCallConstructor(
                                    irRuntimeReferences.suppliedProjectionRegularIrClassSymbol.constructors.single(),
                                    emptyList()
                                ).apply {
                                    arguments.clear()
                                    arguments.add(variance)
                                    arguments.add(type)
                                    origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                                }
                            }
                        }
                    }
                    val isNullable: Boolean = type.nullability == SimpleTypeNullability.MARKED_NULLABLE
                    irCallConstructor(
                        irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single { it.owner.parameters.size == 3 },
                        emptyList(),
                    ).apply {
                        arguments.clear()
                        arguments.add(irString(fullyQualifiedName))
                        arguments.add(
                            irCall(listOfIrSimpleFunction).also {
                                it.typeArguments.clear()
                                it.typeArguments.add(irRuntimeReferences.suppliedProjectionIrType)
                                it.arguments.clear()
                                it.arguments.add(irVararg(irRuntimeReferences.suppliedProjectionIrType, typeArguments))
                                it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                            }
                        )
                        arguments.add(irBoolean(isNullable))
                        origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                    }
                }
                is IrTypeParameterSymbol -> {
                    val typeParameter = classifier.owner
                    val nullability = type.nullability
                    val irGetSuppliedTypeExpression = transformationContext.typeParametersMapping[typeParameter]!!(transformationContext.dispatchReceiversMapping)
                    
                    irBlock(
                        origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                    ) {
                        val savedSuppliedTypeExpression = irTemporary(irGetSuppliedTypeExpression, origin = SUPPLIED_TYPE_OF_SUBSTITUTION_ORIGIN)
                        
                        +irWhen(
                            type = irRuntimeReferences.suppliedTypeIrType,
                            branches = listOf(
                                irBranch(
                                    condition = typeOperator(
                                        resultType = pluginContext.irBuiltIns.booleanType,
                                        argument = irGet(savedSuppliedTypeExpression),
                                        typeOperator = IrTypeOperator.INSTANCEOF,
                                        typeOperand = irRuntimeReferences.suppliedTypeRegularIrType,
                                    ),
                                    result = irCallConstructor(
                                        irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single { it.owner.parameters.size == 3 },
                                        emptyList(),
                                    ).apply {
                                        fun castedSuppliedTypeExpression() =
                                            typeOperator(
                                                resultType = irRuntimeReferences.suppliedTypeRegularIrType,
                                                argument = irGet(savedSuppliedTypeExpression),
                                                typeOperator = IrTypeOperator.IMPLICIT_CAST,
                                                typeOperand = irRuntimeReferences.suppliedTypeRegularIrType,
                                            )
                                        
                                        arguments.clear()
                                        arguments.add(
                                            irCall(
                                                irRuntimeReferences.suppliedTypeRegularIrClassSymbol.owner.properties.single { it.name.identifier == "fullyQualifiedName" }.getter!!
                                            ).also {
                                                it.arguments.clear()
                                                it.arguments.add(castedSuppliedTypeExpression())
                                                it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                                            }
                                        )
                                        arguments.add(
                                            irCall(
                                                irRuntimeReferences.suppliedTypeRegularIrClassSymbol.owner.properties.single { it.name.identifier == "typeArguments" }.getter!!
                                            ).also {
                                                it.arguments.clear()
                                                it.arguments.add(castedSuppliedTypeExpression())
                                                it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                                            }
                                        )
                                        arguments.add(
                                            when (nullability) {
                                                SimpleTypeNullability.MARKED_NULLABLE -> irBoolean(true)
                                                SimpleTypeNullability.NOT_SPECIFIED ->
                                                    irCall(
                                                        irRuntimeReferences.suppliedTypeRegularIrClassSymbol.owner.properties.single { it.name.identifier == "isNullable" }.getter!!
                                                    ).also {
                                                        it.arguments.clear()
                                                        it.arguments.add(castedSuppliedTypeExpression())
                                                        it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                                                    }
                                                SimpleTypeNullability.DEFINITELY_NOT_NULL -> irBoolean(false)
                                            }
                                        )
                                        origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                                    },
                                ),
                                irBranch(
                                    condition = typeOperator(
                                        resultType = pluginContext.irBuiltIns.booleanType,
                                        argument = irGet(savedSuppliedTypeExpression),
                                        typeOperator = IrTypeOperator.INSTANCEOF,
                                        typeOperand = irRuntimeReferences.suppliedTypeDynamicIrType,
                                    ),
                                    result = irGet(savedSuppliedTypeExpression),
                                ),
                                irBranch(
                                    condition = irBoolean(true),
                                    result = irCall(pluginContext.irBuiltIns.noWhenBranchMatchedExceptionSymbol),
                                ),
                            )
                        ).also {
                            it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                        }
                    }
                }
            }
        }
    override fun visitCall(
        expression: IrCall,
        data: TransformationContext,
    ): IrElement {
        val declaration = expression.symbol.owner
        if (declaration.callableId != suppliedTypeOfCallableId) return super.visitCall(expression, data)
        val typeToSupply = expression.typeArguments.single()!!
        return DeclarationIrBuilder(pluginContext, data.localSymbol!!).suppliedTypeExpressionFor(typeToSupply, data)
    }
    override fun visitSimpleFunction(
        declaration: IrSimpleFunction,
        data: TransformationContext,
    ): IrStatement {
        val newTypeParametersMapping = buildMap {
            putAll(data.typeParametersMapping)
            for (typeParameter in declaration.typeParameters) if (typeParameter.isSupply) {
                put(typeParameter) { _: Map<IrClassSymbol, IrValueParameter> ->
                    val valueParameter = declaration.parameters.first { it.name == typeParameter.supplierParameterName }
                    irGet(valueParameter).also { it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN }
                }
            }
        }
        val declarationDispatchReceivers = declaration.parameters.filter { it.kind == IrParameterKind.DispatchReceiver }
        val newDispatchReceiversMapping = buildMap {
            putAll(data.dispatchReceiversMapping)
            for (receiver in declarationDispatchReceivers)
                put(receiver.type.classOrFail, receiver)
        }
        return super.visitSimpleFunction(declaration, TransformationContext(newTypeParametersMapping, newDispatchReceiversMapping, declaration.symbol))
    }
    override fun visitConstructor(
        declaration: IrConstructor,
        data: TransformationContext,
    ): IrStatement {
        val irClass = declaration.parentAsClass
        val newTypeParametersMapping = buildMap {
            putAll(data.typeParametersMapping)
            for (typeParameter in irClass.typeParameters) if (typeParameter.isSupply) {
                put(typeParameter) { _: Map<IrClassSymbol, IrValueParameter> ->
                    val valueParameter = declaration.parameters.first { it.name == typeParameter.supplierParameterName }
                    irGet(valueParameter).also { it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN }
                }
            }
        }
        val declarationDispatchReceivers = declaration.parameters.filter { it.kind == IrParameterKind.DispatchReceiver }
        val newDispatchReceiversMapping = buildMap {
            putAll(data.dispatchReceiversMapping)
            for (receiver in declarationDispatchReceivers)
                put(receiver.type.classOrFail, receiver)
        }
        return super.visitConstructor(declaration, TransformationContext(newTypeParametersMapping, newDispatchReceiversMapping, declaration.symbol))
    }
    override fun visitAnonymousInitializer(
        declaration: IrAnonymousInitializer,
        data: TransformationContext,
    ): IrStatement {
        TODO()
        return super.visitAnonymousInitializer(declaration, data)
    }
    override fun visitField(declaration: IrField, data: TransformationContext): IrStatement {
        val irClass = declaration.parentAsClass
        val primaryConstructor = irClass.primaryConstructor ?: return super.visitField(declaration, data)
        val newTypeParametersMapping = buildMap {
            putAll(data.typeParametersMapping)
            for (typeParameter in irClass.typeParameters) if (typeParameter.isSupply) {
                put(typeParameter) { _: Map<IrClassSymbol, IrValueParameter> ->
                    val valueParameter = primaryConstructor.parameters.first { it.name == typeParameter.supplierParameterName }
                    irGet(valueParameter).also { it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN }
                }
            }
        }
        val declarationDispatchReceivers = primaryConstructor.parameters.filter { it.kind == IrParameterKind.DispatchReceiver }
        val newDispatchReceiversMapping = buildMap {
            putAll(data.dispatchReceiversMapping)
            for (receiver in declarationDispatchReceivers)
                put(receiver.type.classOrFail, receiver)
        }
        return super.visitField(declaration, TransformationContext(newTypeParametersMapping, newDispatchReceiversMapping, declaration.symbol))
    }
    override fun visitClass(
        declaration: IrClass,
        data: TransformationContext,
    ): IrStatement {
        val newTypeParametersMapping = buildMap {
            putAll(data.typeParametersMapping)
            for (typeParameter in declaration.typeParameters) if (typeParameter.isSupply) {
                put(typeParameter) { receivers: Map<IrClassSymbol, IrValueParameter> ->
                    val property = declaration.properties.first { it.name == typeParameter.internalSupplierPropertyName }
                    irCall(property.getter!!).also {
                        it.arguments.clear()
                        it.arguments.add(irGet(receivers[declaration.symbol]!!))
                        it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                    }
                }
            }
        }
        return super.visitClass(declaration, TransformationContext(newTypeParametersMapping, data.dispatchReceiversMapping, null))
    }
}
// endregion