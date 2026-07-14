/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts.ir

import dev.lounres.kone.plugin.contexts.*
import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


class ContextsIrPluginException(message: String? = null, reason: Throwable? = null) : RuntimeException(message, reason)
fun contextsIrPluginException(message: String? = null, reason: Throwable? = null): Nothing = throw ContextsIrPluginException(message, reason)

fun couldNotFindClass(classId: ClassId): Nothing = error("Could not find class '${classId.asFqNameString()}'")
fun couldNotFindCorrespondingCallable(callableId: CallableId): Nothing = error("Could not find corresponding callable '${callableId.asSingleFqName()}'.")
fun couldNotFindCorrespondingFunctionInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding function '$functionName' in class '${classId.asFqNameString()}'.")
fun couldNotFindCorrespondingPropertyInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding property '$functionName' in class '${classId.asFqNameString()}'.")

fun DeclarationFinder.referenceClassOrFail(classId: ClassId): IrClassSymbol =
    findClass(classId) ?: couldNotFindClass(classId)
inline fun DeclarationFinder.referenceFunctionThatOrFail(callableId: CallableId, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    findFunctions(callableId)
        .also {
            val filtered = it.filter(predicate)
            if (filtered.size != 1) error("${filtered.size}!!!\n${filtered.joinToString(separator = "") { "  $it\n" }}") }
        .singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)
inline fun IrClassSymbol.referenceFunctionThatOrFail(name: Name, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    owner.declarations.asSequence().filterIsInstance<IrSimpleFunction>().map { it.symbol }.singleOrNull { it.owner.name == name && predicate(it) } ?: couldNotFindCorrespondingFunctionInClass(owner.classIdOrFail, name)
inline fun IrClassSymbol.referenceFunctionThatOrFail(name: String, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    referenceFunctionThatOrFail(Name.identifier(name), predicate)
inline fun IrClassSymbol.referencePropertyThatOrFail(name: Name, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    owner.declarations.asSequence().filterIsInstance<IrProperty>().map { it.symbol }.singleOrNull { it.owner.name == name && predicate(it) } ?: couldNotFindCorrespondingPropertyInClass(owner.classIdOrFail, name)
inline fun IrClassSymbol.referencePropertyThatOrFail(name: String, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    referencePropertyThatOrFail(Name.identifier(name), predicate)

val fakeValueParametersErrorCallsDescriptions = fakeValueParametersNameStrings.map { "Unresolved reference: this@R|<local>/$it|" }

fun IrProperty.isInclude() = hasAnnotation(koneContextHolderIncludeAnnotationClassId)
fun IrProperty.isExclude() = hasAnnotation(koneContextHolderExcludeAnnotationClassId)
fun IrPropertySymbol.isInclude() = owner.isInclude()
fun IrPropertySymbol.isExclude() = owner.isExclude()

class IrRuntimeReferences(private val pluginContext: IrPluginContext) {
    private val finder by lazy { pluginContext.finderForBuiltins() }
    
    // Kotlin
//    val errorIrSimpleFunctionSymbol: IrSimpleFunctionSymbol = finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin"), callableName = Name.identifier("error"))) {
//        it.owner.parameters[0].type == pluginContext.irBuiltIns.anyType
//    }
    val runIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin"), callableName = Name.identifier("run"))) {
            it.owner.parameters.size == 1
        }
    }
    val readWritePropertyIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.properties"), topLevelName = Name.identifier("ReadWriteProperty"))) }
    val readWritePropertyGetValueIrSimpleFunctionSymbol by lazy { readWritePropertyIrClassSymbol.referenceFunctionThatOrFail("getValue") }
    val readWritePropertySetValueIrSimpleFunctionSymbol by lazy { readWritePropertyIrClassSymbol.referenceFunctionThatOrFail("setValue") }
    val listIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("List"))) }
    val listGetIrSimpleFunctionSymbol by lazy { listIrClassSymbol.referenceFunctionThatOrFail("get") }
    val listOfIrSimpleFunction by lazy {
        finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
            val parameters = symbol.owner.parameters
            parameters.size == 1 && parameters[0].isVararg
        }
    }
    val mapIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("Map"))) }
    val mapGetIrSimpleFunctionSymbol by lazy { mapIrClassSymbol.referenceFunctionThatOrFail("get") }
    val mapOfIrSimpleFunction by lazy {
        finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("mapOf"))) { symbol ->
            val parameters = symbol.owner.parameters
            parameters.size == 1 && parameters[0].isVararg
        }
    }
    val pairIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Pair"))) }
    val pairIrConstructorSymbol by lazy { pairIrClassSymbol.constructors.single() }
    
    //  Library
    val koneContextIrClassSymbol: IrClassSymbol by lazy { finder.referenceClassOrFail(koneContextClassId) }
    val koneContextHolderIrClassSymbol: IrClassSymbol by lazy { finder.referenceClassOrFail(koneContextHolderClassId) }
    
    // Public runtime
    val koneContextHolderIncludeAnnotationIrClassSymbol: IrClassSymbol by lazy { finder.referenceClassOrFail(koneContextHolderIncludeAnnotationClassId) }
    val koneContextHolderExcludeAnnotationIrClassSymbol: IrClassSymbol by lazy { finder.referenceClassOrFail(koneContextHolderExcludeAnnotationClassId) }
    val useLocallyAsExtensionReceiversIrSimpleFunctionSymbol: IrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(useLocallyAsExtensionReceiversFunctionCallableId) }
    val useLocallyAsContextsIrSimpleFunctionSymbol: IrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(useLocallyAsContextsFunctionCallableId) }
    val unwrapLocallyAsExtensionReceiversIrSimpleFunctionSymbol: IrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(unwrapLocallyAsExtensionReceiversFunctionCallableId) }
    
    // Private runtime
}