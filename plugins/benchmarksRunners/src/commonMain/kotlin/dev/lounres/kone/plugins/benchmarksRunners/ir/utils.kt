/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.ir

import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance


class BenchmarksIrPluginException(message: String? = null, reason: Throwable? = null) : RuntimeException(message, reason)
fun benchmarksIrPluginException(message: String? = null, reason: Throwable? = null): Nothing = throw BenchmarksIrPluginException(message, reason)

fun couldNotFindClass(classId: ClassId): Nothing = error("Could not find class '${classId.asFqNameString()}'")
fun couldNotFindCorrespondingCallable(callableId: CallableId): Nothing = error("Could not find corresponding callable '${callableId.asSingleFqName()}'.")
fun couldNotFindCorrespondingFunctionInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding function '$functionName' in class '${classId.asFqNameString()}'.")
fun couldNotFindCorrespondingPropertyInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding property '$functionName' in class '${classId.asFqNameString()}'.")

fun DeclarationFinder.referenceClassOrFail(classId: ClassId): IrClassSymbol =
    findClass(classId) ?: couldNotFindClass(classId)
inline fun DeclarationFinder.referenceFunctionThatOrFail(callableId: CallableId, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    findFunctions(callableId).singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)
inline fun DeclarationFinder.referencePropertyThatOrFail(callableId: CallableId, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    findProperties(callableId).singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)
inline fun IrClassSymbol.referenceFunctionThatOrFail(name: Name, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    owner.declarations.asSequence().filterIsInstance<IrSimpleFunction>().map { it.symbol }.singleOrNull { it.owner.name == name && predicate(it) } ?: couldNotFindCorrespondingFunctionInClass(owner.classIdOrFail, name)
inline fun IrClassSymbol.referenceFunctionThatOrFail(name: String, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    referenceFunctionThatOrFail(Name.identifier(name), predicate)
inline fun IrClassSymbol.referencePropertyThatOrFail(name: Name, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    owner.declarations.asSequence().filterIsInstance<IrProperty>().map { it.symbol }.singleOrNull { it.owner.name == name && predicate(it) } ?: couldNotFindCorrespondingPropertyInClass(owner.classIdOrFail, name)
inline fun IrClassSymbol.referencePropertyThatOrFail(name: String, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    referencePropertyThatOrFail(Name.identifier(name), predicate)

val IrDeclarationWithName.fqName: FqName get() = fqNameWhenAvailable ?: error("Expected declaration with available FQ name")

class IrRuntimeReferences(private val pluginContext: IrPluginContext) {
    private val finder by lazy { pluginContext.finderForBuiltins() }
    
    // Kotlin
    val intRemIntFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin"), className = FqName("Int"), callableName = Name.identifier("rem"))) {
            it.owner.parameters.getOrNull(1)?.type == pluginContext.irBuiltIns.intType
        }
    }
    val pairClassIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlinx"), isLocal = false, relativeClassName = FqName("Pair"))) }
    val pairConstructorIrConstructorSymbol by lazy { pairClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val arraySizePropertyIrPropertySymbol by lazy { finder.referencePropertyThatOrFail(CallableId(packageName = FqName("kotlin"), className = FqName("Array"), callableName = Name.identifier("size"))) }
    val arraySizeGetterIrSimpleFunctionSymbol by lazy { arraySizePropertyIrPropertySymbol.owner.getter!!.symbol }
    val arrayGetFunctionIrSimpleFunctionSymbol by lazy { pluginContext.irBuiltIns.arrayClass.referenceFunctionThatOrFail(Name.identifier("get")) }
    val listGetFunctionIrSimpleFunctionSymbol by lazy { pluginContext.irBuiltIns.listClass.referenceFunctionThatOrFail(Name.identifier("get")) }
    val mapGetFunctionIrSimpleFunctionSymbol by lazy { pluginContext.irBuiltIns.mapClass.referenceFunctionThatOrFail(Name.identifier("get")) }
    val arrayDropFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.collections"), className = null, callableName = Name.identifier("drop"))) {
            val owner = it.owner
            if (owner.typeParameters.size != 1 || owner.parameters.size != 2) return@referenceFunctionThatOrFail false
            if (
                owner.parameters[0].type != pluginContext.irBuiltIns.arrayClass.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        makeTypeProjection(pluginContext.irBuiltIns.stringType, OUT_VARIANCE),
                    )
                )
            ) return@referenceFunctionThatOrFail false
            true
        }
    }
    val listChunkedFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.collections"), className = null, callableName = Name.identifier("chunked"))) {
            val owner = it.owner
            if (owner.typeParameters.size != 1 || owner.parameters.size != 2) return@referenceFunctionThatOrFail false
            true
        }
    }
    val associateFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.collections"), className = null, callableName = Name.identifier("associate"))) {
            val owner = it.owner
            if (owner.typeParameters.size != 3 || owner.parameters.size != 2) return@referenceFunctionThatOrFail false
            true
        }
    }
    val illegalArgumentExceptionClassIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin"), isLocal = false, relativeClassName = FqName("IllegalArgumentException"))) }
    val illegalArgumentExceptionConstructorStringIrConstructorSymbol by lazy {
        illegalArgumentExceptionClassIrClassSymbol.constructors.single {
            it.owner.parameters.size == 1 && it.owner.parameters[0].type == pluginContext.irBuiltIns.stringType
        }
    }
    
    // kotlinx.seerialization
    val jsonClassIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlinx.serialization.json"), isLocal = false, relativeClassName = FqName("Json"))) }
    val jsonDefaultCompanionIrClassSymbol by lazy { jsonClassIrClassSymbol.owner.companionObject()!!.symbol }
    val jsonDecodeFromStringFunctionIrSimpleFunctionSymbol by lazy {
        jsonClassIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("decodeFromString")) {
            it.owner.parameters.size == 2
        }
    }
}