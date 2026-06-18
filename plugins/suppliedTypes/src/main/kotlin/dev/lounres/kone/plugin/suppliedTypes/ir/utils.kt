/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.plugin.suppliedTypes.internalSupplierPropertyName
import dev.lounres.kone.plugin.suppliedTypes.noSuppliedTypeParameterInClassStubSingletonClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableClassClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.supplianceProvidedAnnotationClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionRegularClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionStarSingletonClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeDynamicSingletonClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeOfFunctionCallableId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeRegularClassClassId
import dev.lounres.kone.plugin.suppliedTypes.supplyAnnotationClassId
import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.getPropertySetter
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames


fun couldNotFindClass(classId: ClassId): Nothing =
    error("Could not find class '${classId.asFqNameString()}'")
fun couldNotFindCorrespondingCallable(callableId: CallableId): Nothing =
    error("Could not find corresponding callable '${callableId.asSingleFqName()}'.")

fun DeclarationFinder.referenceClassOrFail(classId: ClassId): IrClassSymbol =
    findClass(classId) ?: couldNotFindClass(classId)
inline fun DeclarationFinder.referenceFunctionThatOrFail(callableId: CallableId, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    findFunctions(callableId).singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)

val IrDeclarationWithName.fqName: FqName get() = fqNameWhenAvailable ?: error("Expected declaration with available FQ name")
val IrClass.fqNameStringForSuppliedTypes: String get() = fqNameWhenAvailable?.toString() ?: "${SpecialNames.LOCAL}.$name"
val IrTypeParameter.isSupply: Boolean get() = hasAnnotation(supplyAnnotationClassId)
val IrClass.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId)
val IrSimpleFunction.isSuppliable: Boolean get() = hasAnnotation(suppliableAnnotationClassId)
val IrValueParameter.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedAnnotationClassId)
val IrConstructor.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedAnnotationClassId)
val IrSimpleFunction.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedAnnotationClassId)
val IrTypeParameter.providedSupplierParameterName: Name?
    get() {
        val supplyingAnnotationConstructorCallOrNull = annotations.first { it.symbol.owner.parentAsClass.classId == supplyAnnotationClassId }
        val theOnlyArgumentOrNull = supplyingAnnotationConstructorCallOrNull.arguments[0] as IrConst?
        val suppliedParameterName = (theOnlyArgumentOrNull?.value as? String?)?.takeIf { it.isNotEmpty() }
        return suppliedParameterName?.let { Name.identifier(it) }
    }
val IrTypeParameter.internalSupplierPropertyName: Name get() = internalSupplierPropertyName(parent.kotlinFqName, name)
val IrTypeParameter.internalSupplierParameterName: Name get() = internalSupplierParameterName(name)
val IrTypeParameter.supplierParameterName: Name get() = /*providedSupplierParameterName ?:*/ internalSupplierParameterName

class IrRuntimeReferences(pluginContext: IrPluginContext) {
    private val finder = pluginContext.finderForBuiltins()
    val suppliedTypeIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeClassClassId)
    val suppliedTypeIrType: IrType = suppliedTypeIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedTypeRegularIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeRegularClassClassId)
    val suppliedTypeRegularIrType: IrType = suppliedTypeRegularIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedTypeDynamicIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeDynamicSingletonClassId)
    val suppliedTypeDynamicIrType: IrType = suppliedTypeDynamicIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedProjectionIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionClassClassId)
    val suppliedProjectionRegularIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionRegularClassClassId)
    val suppliedProjectionStarIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionStarSingletonClassId)
    val suppliedProjectionIrType: IrSimpleType = suppliedProjectionIrClassSymbol.createType(false, emptyList())
    
    val noSuppliedTypeParameterInClassStubIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(noSuppliedTypeParameterInClassStubSingletonClassId)
    val suppliableClassIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliableClassClassClassId)
    val suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol = suppliableClassIrClassSymbol.getPropertyGetter("suppliedTypesStorage")!!
    val suppliableClassSuppliedTypesStorageSetterIrSimpleFunctionSymbol = suppliableClassIrClassSymbol.getPropertySetter("suppliedTypesStorage")!!
    val suppliableClassAfterSupplianceIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = suppliableClassIrClassSymbol.functionByName("afterSuppliance")
    val suppliedTypeOfIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = finder.referenceFunctionThatOrFail(suppliedTypeOfFunctionCallableId)
}