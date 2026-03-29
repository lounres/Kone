/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.ir

import dev.lounres.kone.plugin.suppliedTypes.internalSupplierParameterName
import dev.lounres.kone.plugin.suppliedTypes.internalSupplierPropertyName
import dev.lounres.kone.plugin.suppliedTypes.noSuppliedTypeParameterInClassStubClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableClassClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliableClassId
import dev.lounres.kone.plugin.suppliedTypes.supplianceProvidedClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionRegularClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedProjectionStarClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeDynamicClassId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeOfCallableId
import dev.lounres.kone.plugin.suppliedTypes.suppliedTypeRegularClassId
import dev.lounres.kone.plugin.suppliedTypes.supplyClassId
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
val IrTypeParameter.isSupply: Boolean get() = hasAnnotation(supplyClassId)
val IrClass.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId)
val IrSimpleFunction.isSuppliable: Boolean get() = hasAnnotation(suppliableClassId)
val IrValueParameter.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedClassId)
val IrConstructor.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedClassId)
val IrSimpleFunction.isSupplianceProvided: Boolean get() = hasAnnotation(supplianceProvidedClassId)
val IrTypeParameter.providedSupplierParameterName: Name?
    get() {
        val supplyingAnnotationConstructorCallOrNull = annotations.first { it.symbol.owner.parentAsClass.classId == supplyClassId }
        val theOnlyArgumentOrNull = supplyingAnnotationConstructorCallOrNull.arguments[0] as IrConst?
        val suppliedParameterName = (theOnlyArgumentOrNull?.value as? String?)?.takeIf { it.isNotEmpty() }
        return suppliedParameterName?.let { Name.identifier(it) }
    }
val IrTypeParameter.internalSupplierPropertyName: Name get() = internalSupplierPropertyName(parent.kotlinFqName, name)
val IrTypeParameter.internalSupplierParameterName: Name get() = internalSupplierParameterName(name)
val IrTypeParameter.supplierParameterName: Name get() = /*providedSupplierParameterName ?:*/ internalSupplierParameterName

class IrRuntimeReferences(pluginContext: IrPluginContext) {
    private val finder = pluginContext.finderForBuiltins()
    val suppliedTypeIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeClassId)
    val suppliedTypeIrType: IrType = suppliedTypeIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedTypeRegularIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeRegularClassId)
    val suppliedTypeRegularIrType: IrType = suppliedTypeRegularIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedTypeDynamicIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedTypeDynamicClassId)
    val suppliedTypeDynamicIrType: IrType = suppliedTypeDynamicIrClassSymbol.createType(hasQuestionMark = false, arguments = emptyList())
    val suppliedProjectionIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionClassId)
    val suppliedProjectionRegularIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionRegularClassId)
    val suppliedProjectionStarIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliedProjectionStarClassId)
    val suppliedProjectionIrType: IrSimpleType = suppliedProjectionIrClassSymbol.createType(false, emptyList())
    
    val noSuppliedTypeParameterInClassStubIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(noSuppliedTypeParameterInClassStubClassId)
    val suppliableClassIrClassSymbol: IrClassSymbol = finder.referenceClassOrFail(suppliableClassClassId)
    val suppliableClassSuppliedTypesStorageGetterIrSimpleFunctionSymbol = suppliableClassIrClassSymbol.getPropertyGetter("suppliedTypesStorage")!!
    val suppliableClassSuppliedTypesStorageSetterIrSimpleFunctionSymbol = suppliableClassIrClassSymbol.getPropertySetter("suppliedTypesStorage")!!
    val suppliableClassAfterSupplianceIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = suppliableClassIrClassSymbol.functionByName("afterSuppliance")
    val suppliedTypeOfIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = finder.referenceFunctionThatOrFail(suppliedTypeOfCallableId)
}