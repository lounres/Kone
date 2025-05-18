/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.suppliedClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionRegularClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionStarClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeDynamicClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeOfCallableId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeRegularClassId
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addBackingField
import org.jetbrains.kotlin.ir.builders.declarations.addGetter
import org.jetbrains.kotlin.ir.builders.declarations.addProperty
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildValueParameter
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrEnumConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrInstanceInitializerCall
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrScriptSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance
import kotlin.collections.iterator
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract


fun couldNotFindClass(classId: ClassId): Nothing =
    error("Could not find class '${classId.asFqNameString()}'. Ensure you have added dependency on plugin runtime.")
fun couldNotFindCorrespondingCallable(callableId: CallableId): Nothing =
    error("Could not find corresponding callable '${callableId.asSingleFqName()}'. Ensure you have added dependency on plugin runtime.")

fun IrPluginContext.referenceClassOrFail(classId: ClassId): IrClassSymbol =
    referenceClass(classId) ?: couldNotFindClass(classId)
inline fun IrPluginContext.referenceFunctionThatOrFail(callableId: CallableId, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    referenceFunctions(callableId).singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)

val IrDeclarationWithName.fqName: FqName get() = fqNameWhenAvailable ?: error("Expected declaration with available FQ name")
val IrTypeParameter.isSupplied: Boolean get() = hasAnnotation(suppliedClassId)
val IrTypeParameter.providedSupplierParameterName: Name?
    get() {
        val supplyingAnnotationConstructorCallOrNull = annotations.first { it.symbol.owner.parentAsClass.classId == suppliedClassId }
        val theOnlyArgumentOrNull = supplyingAnnotationConstructorCallOrNull.arguments[0] as IrConst?
        val suppliedParameterName = (theOnlyArgumentOrNull?.value as? String?)?.takeIf { it.isNotEmpty() }
        return suppliedParameterName?.let { Name.identifier(it) }
    }
val IrTypeParameter.internalSupplierPropertyName: Name get() = Name.special("<supplied-type-variable-for-${parent.kotlinFqName}-${name}>")
val IrTypeParameter.internalSupplierParameterName: Name get() = Name.special("<supplied-type-argument-for-${name}>")
val IrTypeParameter.supplierParameterName: Name get() = /*providedSupplierParameterName ?:*/ internalSupplierParameterName

class IrRuntimeReferences(pluginContext: IrPluginContext) {
    val suppliedTypeIrClassSymbol: IrClassSymbol = pluginContext.referenceClassOrFail(suppliedTypeClassId)
    val suppliedTypeIrType: IrType = suppliedTypeIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedTypeRegularIrClassSymbol: IrClassSymbol = pluginContext.referenceClassOrFail(suppliedTypeRegularClassId)
    val suppliedTypeDynamicIrClassSymbol: IrClassSymbol = pluginContext.referenceClassOrFail(suppliedTypeDynamicClassId)
    val suppliedProjectionIrClassSymbol: IrClassSymbol = pluginContext.referenceClassOrFail(suppliedProjectionClassId)
    val suppliedProjectionRegularIrClassSymbol: IrClassSymbol = pluginContext.referenceClassOrFail(suppliedProjectionRegularClassId)
    val suppliedProjectionStarIrClassSymbol: IrClassSymbol = pluginContext.referenceClassOrFail(suppliedProjectionStarClassId)
    val suppliedTypeOfIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = pluginContext.referenceFunctionThatOrFail(suppliedTypeOfCallableId)
}

class SuppliedTypeIrGenerationExtension(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irRuntimeReferences: IrRuntimeReferences = IrRuntimeReferences(pluginContext)

        moduleFragment.transform(
            ClassSuppliedTypeParametersPropertiesGenerationTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )

        moduleFragment.transform(
            ClassSuppliedTypeParametersOverridesGenerationTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )
        
        moduleFragment.transform(
            FunctionsWithSuppliedTypeParametersModificationTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )
        
        moduleFragment.transform(
            FunctionsWithSuppliedTypeParametersUsageTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
            ),
            null
        )
        
        moduleFragment.transform(
            SuppliedTypeOfSubstitutionTransformer(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
            ),
            emptyMap()
        )
    }
}

// region Phase 1
class ClassSuppliedTypeParametersPropertiesGenerationTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrElementTransformerVoid() {
    private val CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN by IrDeclarationOriginImpl
    private val CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_STATEMENT_ORIGIN by IrStatementOriginImpl
    override fun visitClass(declaration: IrClass): IrStatement {
        when (declaration.kind) {
            ClassKind.INTERFACE -> {
                for (typeParameter in declaration.typeParameters) if (typeParameter.isSupplied) {
                    val property = declaration.addProperty {
                        name = typeParameter.internalSupplierPropertyName
                        modality = Modality.ABSTRACT
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN
                    }
                    val getter = property.addGetter {
                        modality = Modality.ABSTRACT
                        returnType = irRuntimeReferences.suppliedTypeIrType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN
                    }
                    val getterDispatchReceiver = getter.buildReceiverParameter {
                        type = declaration.defaultType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN
                    }
                    getter.parameters = listOf(getterDispatchReceiver)
                }
            }
            ClassKind.CLASS -> {
                check(!(declaration.isValue && declaration.typeParameters.any { it.isSupplied })) { "Found IrClass with `isValue = true` that unexpectedly has supplied type parameters:\n${declaration.symbol}" }
                for (typeParameter in declaration.typeParameters) if (typeParameter.isSupplied) {
                    val property = declaration.addProperty {
                        name = typeParameter.internalSupplierPropertyName
                        modality = Modality.FINAL
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN
                    }
                    val backingField = property.addBackingField {
                        name = typeParameter.internalSupplierPropertyName
                        type = irRuntimeReferences.suppliedTypeIrType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN
                    }
                    backingField.initializer = DeclarationIrBuilder(pluginContext, backingField.symbol).run {
                        irExprBody(
                            irCallWithSubstitutedType(
                                irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol,
                                listOf(typeParameter.defaultType)
                            ).also { it.origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_STATEMENT_ORIGIN }
                        )
                    }
                    val getter = property.addGetter {
                        modality = Modality.ABSTRACT
                        returnType = irRuntimeReferences.suppliedTypeIrType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN
                    }
                    val getterDispatchReceiver = getter.buildReceiverParameter {
                        type = declaration.defaultType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_ORIGIN
                    }
                    getter.parameters = listOf(getterDispatchReceiver)
                    getter.body = DeclarationIrBuilder(pluginContext, getter.symbol).run {
                        irExprBody(
                            irGetField(
                                irGet(getterDispatchReceiver),
                                backingField,
                            ).also { it.origin = CLASS_SUPPLIED_TYPE_PARAMETERS_PROPERTIES_GENERATION_STATEMENT_ORIGIN }
                        )
                    }
                }
            }
            ClassKind.ENUM_CLASS,
            ClassKind.ENUM_ENTRY,
            ClassKind.ANNOTATION_CLASS,
            ClassKind.OBJECT -> check(declaration.typeParameters.none()) { "Found IrClass that unexpectedly has type parameters:\n${declaration.symbol}" }
        }
        return super.visitClass(declaration)
    }
}
// endregion

// region Phase 2
val IrType.arguments
    get() =
        (this as? IrSimpleType ?: error("Super type\n${this.dumpKotlinLike()}\nis not IrSimpleType"))
            .arguments.map { it as? IrType ?: error("Type argument\n${it.dumpKotlinLike()}\nis not IrType") }

@OptIn(ExperimentalContracts::class)
public inline fun <K, V, R> Map<K, V>.computeOnOrElse(key: K, defaultResult: () -> R, compute: (value: V) -> R): R {
    contract {
        callsInPlace(defaultResult, AT_MOST_ONCE)
        callsInPlace(compute, AT_MOST_ONCE)
    }
    @Suppress("UNCHECKED_CAST")
    return (if (key !in this) defaultResult() else compute(get(key) as V))
}

@OptIn(ExperimentalContracts::class)
public inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): V {
    contract {
        callsInPlace(valueOnPut, AT_MOST_ONCE)
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return computeOnOrElse(key, valueOnPut, transformOnChange).also { this[key] = it }
}

public inline fun <K, V: W, W, D: MutableMap<in K, W>> Map<out K, V>.copyToBy(destination: D, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for ((key, value) in this) {
        destination.putOrChange(key, { value }, { resolve(key, it, value) })
    }
    return destination
}

public inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapToBy(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for (entry in this) {
        val (key, value) = entry
        destination.putOrChange(key, { transform(entry) }, { resolve(key, it, value) })
    }
    return destination
}

class ClassSuppliedTypeParametersOverridesGenerationTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrElementTransformerVoid() {
    private val CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN by IrDeclarationOriginImpl
    private val CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_STATEMENT_ORIGIN by IrStatementOriginImpl
    private data class TypeParametersInfo(
        val type: IrType,
        val appearances: Set<IrClass>,
    )
    private val allSuperClassesSuppliedTypeParametersInfoRegistry: MutableMap<IrClass, Map<IrTypeParameter, TypeParametersInfo>> = mutableMapOf()
    private val IrClass.allSuperClassesSuppliedTypeParametersInfo: Map<IrTypeParameter, TypeParametersInfo>
        get() = allSuperClassesSuppliedTypeParametersInfoRegistry.getOrPut(this) {
            buildMap<IrTypeParameter, TypeParametersInfo> {
                for (superType in superTypes) {
                    val superClass = superType.classifierOrFail.owner as IrClass
                    val typeArguments = superType.arguments
                    check(superClass.typeParameters.size == typeArguments.size) { TODO() }
                    val substitutions = superClass.typeParameters.map { it.symbol }.zip(typeArguments).toMap()
                    superClass.typeParameters.filter { it.isSupplied }.associateWith {
                        TypeParametersInfo(
                            type = it.defaultType.substitute(substitutions),
                            appearances = setOf(superClass)
                        )
                    }.copyToBy(
                        destination = this,
                        resolve = { typeParameter, currentInfo, newInfo ->
                            check(currentInfo.type == newInfo.type) {
                                "For some reason interface was inherited twice with different type arguments. " +
                                        "The resulting type of type parameter\n${typeParameter.symbol}\nis both\n${currentInfo.type.dumpKotlinLike()}\nand\n${newInfo.type.dumpKotlinLike()}"
                            }
                            TypeParametersInfo(
                                type = currentInfo.type,
                                appearances = currentInfo.appearances + newInfo.appearances,
                            )
                        }
                    )
                    superClass.allSuperClassesSuppliedTypeParametersInfo
                        .copyMapToBy(
                            destination = this,
                            transform = {
                                TypeParametersInfo(
                                    type = it.value.type.substitute(substitutions),
                                    appearances = it.value.appearances + superClass
                                )
                            },
                            resolve = { typeParameter, currentInfo, newInfo ->
                                check(currentInfo.type == newInfo.type.substitute(substitutions)) {
                                    "For some reason interface was inherited twice with different type arguments." +
                                            "The resulting type of type parameter\n${typeParameter.symbol}\nis both\n${currentInfo.type.dumpKotlinLike()}\nand\n${newInfo.type.dumpKotlinLike()}"
                                }
                                TypeParametersInfo(
                                    type = currentInfo.type,
                                    appearances = currentInfo.appearances + newInfo.appearances + superClass,
                                )
                            },
                        )
                }
            }
        }
    override fun visitClass(declaration: IrClass): IrStatement {
        when (declaration.kind) {
            ClassKind.INTERFACE -> {
                val suppliedTypeParametersToOverride = declaration.allSuperClassesSuppliedTypeParametersInfo
                for ((typeParameter, info) in suppliedTypeParametersToOverride) {
                    val overriddenProperties = info.appearances.map { it.properties.single { it.name == typeParameter.internalSupplierPropertyName } }
                    val property = declaration.addProperty {
                        name = typeParameter.internalSupplierPropertyName
                        modality = Modality.OPEN
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN
                    }
                    property.overriddenSymbols = overriddenProperties.map { it.symbol }
                    val getter = property.addGetter {
                        modality = Modality.OPEN
                        returnType = irRuntimeReferences.suppliedTypeIrType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN
                    }
                    val getterDispatchReceiver = getter.buildReceiverParameter {
                        type = declaration.defaultType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN
                    }
                    getter.parameters = listOf(getterDispatchReceiver)
                    getter.overriddenSymbols = overriddenProperties.map { it.getter!!.symbol }
                    getter.body = DeclarationIrBuilder(pluginContext, getter.symbol).run {
                        irExprBody(
                            irCallWithSubstitutedType(
                                irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol,
                                listOf(info.type)
                            ).also { it.origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_STATEMENT_ORIGIN }
                        )
                    }
                }
            }
            ClassKind.CLASS,
            ClassKind.ENUM_CLASS,
            ClassKind.ENUM_ENTRY,
            ClassKind.OBJECT, -> {
                // TODO: Add checks on value classes
                val superClass = declaration.superClass
                val suppliedTypeParametersToOverride =
                    if (superClass == null) declaration.allSuperClassesSuppliedTypeParametersInfo
                    else declaration.allSuperClassesSuppliedTypeParametersInfo.filterKeys { it !in superClass.allSuperClassesSuppliedTypeParametersInfo }
                for ((typeParameter, info) in suppliedTypeParametersToOverride) {
                    val overriddenProperties = info.appearances.map { it.properties.single { it.name == typeParameter.internalSupplierPropertyName } }
                    val property = declaration.addProperty {
                        name = typeParameter.internalSupplierPropertyName
                        modality = Modality.FINAL
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN
                    }
                    property.overriddenSymbols = overriddenProperties.map { it.symbol }
                    val backingField = property.addBackingField {
                        name = typeParameter.internalSupplierPropertyName
                        type = irRuntimeReferences.suppliedTypeIrType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN
                    }
                    backingField.initializer = DeclarationIrBuilder(pluginContext, backingField.symbol).run {
                        irExprBody(
                            irCallWithSubstitutedType(
                                irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol,
                                listOf(info.type)
                            ).also { it.origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_STATEMENT_ORIGIN }
                        )
                    }
                    val getter = property.addGetter {
                        modality = Modality.FINAL
                        returnType = irRuntimeReferences.suppliedTypeIrType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN
                    }
                    getter.overriddenSymbols = overriddenProperties.map { it.getter!!.symbol }
                    val getterDispatchReceiver = getter.buildReceiverParameter {
                        type = declaration.defaultType
                        origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_ORIGIN
                    }
                    getter.parameters = listOf(getterDispatchReceiver)
                    getter.body = DeclarationIrBuilder(pluginContext, getter.symbol).run {
                        irExprBody(
                            irGetField(
                                irGet(getterDispatchReceiver),
                                backingField,
                            ).also { it.origin = CLASS_SUPPLIED_TYPE_PARAMETERS_OVERRIDES_GENERATION_STATEMENT_ORIGIN }
                        )
                    }
                }
            }
            ClassKind.ANNOTATION_CLASS -> {}
        }
        return super.visitClass(declaration)
    }
}
// endregion

// region Phase 3
class FunctionsWithSuppliedTypeParametersModificationTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrElementTransformerVoid() {
    private val FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_MODIFICATION_ORIGIN by IrDeclarationOriginImpl
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val suppliedValueArguments =
            declaration.typeParameters
                .filter { it.isSupplied }
                .map {
                    buildValueParameter(declaration) {
                        kind = IrParameterKind.Regular
                        name = it.supplierParameterName
                        // TODO
//                        isHidden = true
                        type = irRuntimeReferences.suppliedTypeIrType
                        origin = FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_MODIFICATION_ORIGIN
                    }
                }
        declaration.parameters = suppliedValueArguments + declaration.parameters
        return super.visitSimpleFunction(declaration)
    }
    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val suppliedValueArguments =
            (declaration.parent as IrClass).typeParameters
                .filter { it.isSupplied }
                .map {
                    buildValueParameter(declaration) {
                        kind = IrParameterKind.Regular
                        name = it.supplierParameterName
//                        isHidden = true
                        type = irRuntimeReferences.suppliedTypeIrType
                        origin = FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_MODIFICATION_ORIGIN
                    }
                }
        declaration.parameters = suppliedValueArguments + declaration.parameters
        return super.visitConstructor(declaration)
    }
}
// endregion

// region Phase 4
class FunctionsWithSuppliedTypeParametersUsageTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrElementTransformerVoid() {
    private val FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_USAGE_ORIGIN by IrDeclarationOriginImpl
    private val FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_USAGE_STATEMENT_ORIGIN by IrStatementOriginImpl
    data class TypeArgumentInfo(
        val typeParameter: IrTypeParameter,
        val typeArgument: IrType,
    )
    override fun visitCall(expression: IrCall): IrExpression {
        val declaration = expression.symbol.owner
        if (declaration.callableId != suppliedTypeOfCallableId) {
            val suppliedValueArguments =
                declaration.typeParameters
                    .zip(expression.typeArguments) { typeParameter, typeArgument -> TypeArgumentInfo(typeParameter, typeArgument!!) }
                    .filter { (typeParameter, _) -> typeParameter.isSupplied }
                    .map { (_, typeArgument) ->
                        DeclarationIrBuilder(pluginContext, declaration.symbol)
                            .irCallWithSubstitutedType(
                                irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol,
                                listOf(typeArgument)
                            ).also { it.origin = FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_USAGE_STATEMENT_ORIGIN }
                    }
            expression.arguments.addAll(0, suppliedValueArguments)
        }
        return super.visitCall(expression)
    }
    override fun visitConstructorCall(expression: IrConstructorCall): IrExpression {
        val declaration = expression.symbol.owner.parent as IrClass
        val suppliedValueArguments =
            declaration.typeParameters
                .zip(expression.typeArguments) { typeParameter, typeArgument -> TypeArgumentInfo(typeParameter, typeArgument!!) }
                .filter { (typeParameter, _) -> typeParameter.isSupplied }
                .map { (_, typeArgument) ->
                    DeclarationIrBuilder(pluginContext, declaration.symbol)
                        .irCallWithSubstitutedType(
                            irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol,
                            listOf(typeArgument)
                        ).also { it.origin = FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_USAGE_STATEMENT_ORIGIN }
                }
        expression.arguments.addAll(0, suppliedValueArguments)
        return super.visitConstructorCall(expression)
    }
    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall): IrExpression {
        val declaration = expression.symbol.owner.parent as IrClass
        val suppliedValueArguments =
            declaration.typeParameters
                .zip(expression.typeArguments) { typeParameter, typeArgument -> TypeArgumentInfo(typeParameter, typeArgument!!) }
                .filter { (typeParameter, _) -> typeParameter.isSupplied }
                .map { (_, typeArgument) ->
                    DeclarationIrBuilder(pluginContext, declaration.symbol)
                        .irCallWithSubstitutedType(
                            irRuntimeReferences.suppliedTypeOfIrSimpleFunctionSymbol,
                            listOf(typeArgument)
                        ).also { it.origin = FUNCTIONS_WITH_SUPPLIED_TYPE_PARAMETERS_USAGE_STATEMENT_ORIGIN }
                }
        expression.arguments.addAll(0, suppliedValueArguments)
        return super.visitDelegatingConstructorCall(expression)
    }
    override fun visitEnumConstructorCall(expression: IrEnumConstructorCall): IrExpression {
        return super.visitEnumConstructorCall(expression)
    }
}
// endregion

// region Phase 5
fun IrBuilderWithScope.irGetEnumEntry(enumEntry: IrEnumEntry) =
    IrGetEnumValueImpl(startOffset, endOffset, enumEntry.parentAsClass.defaultType, enumEntry.symbol)

class SuppliedTypeOfSubstitutionTransformer(
    val pluginContext: IrPluginContext,
    val irRuntimeReferences: IrRuntimeReferences,
) : IrTransformer<Map<IrTypeParameter, DeclarationIrBuilder.(isNullable: Boolean) -> IrExpression>>() {
    private val SUPPLIED_TYPE_OF_SUBSTITUTION_ORIGIN by IrDeclarationOriginImpl
    private val SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN by IrStatementOriginImpl
    private val listOfIrSimpleFunction: IrSimpleFunctionSymbol =
        pluginContext.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
            val parameters = symbol.owner.parameters
            parameters.size == 1 && parameters[0].isVararg
        }
    private val kVarianceIrClass = pluginContext.referenceClassOrFail(ClassId(FqName("kotlin.reflect"), FqName("KVariance"), false)).owner
    private val kVarianceIrEnumEntries = kVarianceIrClass.declarations.filterIsInstance<IrEnumEntry>()
    private val kVarianceINVARIANTIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("INVARIANT") }
    private val kVarianceINIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("IN") }
    private val kVarianceOUTIrEnumEntry = kVarianceIrEnumEntries.single { it.name == Name.identifier("OUT") }
    private val suppliedProjectionIrType: IrSimpleType = irRuntimeReferences.suppliedProjectionIrClassSymbol.createType(false, emptyList())
    private fun DeclarationIrBuilder.suppliedTypeExpressionFor(
        type: IrType,
        typeParametersMapping: Map<IrTypeParameter, DeclarationIrBuilder.(isNullable: Boolean) -> IrExpression>
    ): IrExpression =
        when(type) {
            is IrDynamicType -> irGetObject(irRuntimeReferences.suppliedTypeDynamicIrClassSymbol)
            is IrErrorType -> TODO()
            is IrSimpleType -> {
                when (val classifier = type.classifier) {
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
                                    val type = suppliedTypeExpressionFor(it.type, typeParametersMapping)
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
                                    it.typeArguments.add(suppliedProjectionIrType)
                                    it.arguments.clear()
                                    it.arguments.add(irVararg(suppliedProjectionIrType, typeArguments))
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
                                    val type = suppliedTypeExpressionFor(it.type, typeParametersMapping)
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
                            irRuntimeReferences.suppliedTypeRegularIrClassSymbol.constructors.single(),
                            emptyList(),
                        ).apply {
                            arguments.clear()
                            arguments.add(irString(fullyQualifiedName))
                            arguments.add(
                                irCall(listOfIrSimpleFunction).also {
                                    it.typeArguments.clear()
                                    it.typeArguments.add(suppliedProjectionIrType)
                                    it.arguments.clear()
                                    it.arguments.add(irVararg(suppliedProjectionIrType, typeArguments))
                                    it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                                }
                            )
                            arguments.add(irBoolean(isNullable))
                            origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN
                        }
                    }
                    is IrTypeParameterSymbol -> {
                        val typeParameter = classifier.owner
                        val isNullable: Boolean = type.nullability == SimpleTypeNullability.MARKED_NULLABLE
                        typeParametersMapping[typeParameter]!!(isNullable)
                    }
                }
            }
        }
    override fun visitCall(
        expression: IrCall,
        data: Map<IrTypeParameter, DeclarationIrBuilder.(Boolean) -> IrExpression>
    ): IrElement {
        val declaration = expression.symbol.owner
        if (declaration.callableId != suppliedTypeOfCallableId) return super.visitCall(expression, data)
        val typeToSupply = expression.typeArguments.single()!!
        return DeclarationIrBuilder(pluginContext, declaration.symbol).suppliedTypeExpressionFor(typeToSupply, data)
    }
    override fun visitSimpleFunction(
        declaration: IrSimpleFunction,
        data: Map<IrTypeParameter, DeclarationIrBuilder.(Boolean) -> IrExpression>
    ): IrStatement {
        val newData = buildMap {
            putAll(data)
            for (typeParameter in declaration.typeParameters) if (typeParameter.isSupplied) {
                put(typeParameter) { isNullable: Boolean ->
                    val valueParameter = declaration.parameters.first { it.name == typeParameter.supplierParameterName }
                    irGet(valueParameter).also { it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN }
                }
            }
        }
        return super.visitSimpleFunction(declaration, newData)
    }
    override fun visitConstructor(
        declaration: IrConstructor,
        data: Map<IrTypeParameter, DeclarationIrBuilder.(Boolean) -> IrExpression>
    ): IrStatement {
        val newData = buildMap {
            putAll(data)
            for (typeParameter in (declaration.parent as IrClass).typeParameters) if (typeParameter.isSupplied) {
                put(typeParameter) { isNullable: Boolean ->
                    val valueParameter = declaration.parameters.first { it.name == typeParameter.supplierParameterName }
                    irGet(valueParameter).also { it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN }
                }
            }
        }
        return super.visitConstructor(declaration, newData)
    }
    override fun visitAnonymousInitializer(
        declaration: IrAnonymousInitializer,
        data: Map<IrTypeParameter, DeclarationIrBuilder.(Boolean) -> IrExpression>
    ): IrStatement {
        TODO()
        return super.visitAnonymousInitializer(declaration, data)
    }
    
    override fun visitInstanceInitializerCall(
        expression: IrInstanceInitializerCall,
        data: Map<IrTypeParameter, DeclarationIrBuilder.(Boolean) -> IrExpression>
    ): IrExpression {
        val irClass = expression.classSymbol.owner
        val newData = buildMap {
            putAll(data)
            for (typeParameter in irClass.typeParameters) if (typeParameter.isSupplied) {
                put(typeParameter) { isNullable: Boolean ->
                    val valueParameter = irClass.primaryConstructor!!.parameters.first { it.name == typeParameter.supplierParameterName }
                    irGet(valueParameter).also { it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN }
                }
            }
        }
        return super.visitInstanceInitializerCall(expression, newData)
    }
    override fun visitClass(
        declaration: IrClass,
        data: Map<IrTypeParameter, DeclarationIrBuilder.(Boolean) -> IrExpression>
    ): IrStatement {
        val newData = buildMap {
            putAll(data)
            for (typeParameter in declaration.typeParameters) if (typeParameter.isSupplied) {
                put(typeParameter) { isNullable: Boolean ->
                    val property = declaration.properties.first { it.name == typeParameter.internalSupplierPropertyName }
                    irCall(property.getter!!).also { it.origin = SUPPLIED_TYPE_OF_SUBSTITUTION_STATEMENT_ORIGIN }
                }
            }
        }
        return super.visitClass(declaration, newData)
    }
}
// endregion