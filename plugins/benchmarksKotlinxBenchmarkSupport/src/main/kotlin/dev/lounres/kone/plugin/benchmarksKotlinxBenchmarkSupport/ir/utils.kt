/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxBenchmarkDescriptorWithBlackholeParameterClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxBenchmarkModeAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxBenchmarkTimeUnitEnumClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxBlackholeClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxIterationTimeClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxJsRunBenchmarksBuiltInFunctionCallableId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxJsRunBenchmarksFunctionCallableId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxNativeRunBenchmarksFunctionCallableId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxSuiteDescriptorAddFunctionShortName
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxSuiteDescriptorClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxSuiteExecutorBaseClassClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxSuiteExecutorBaseRunFunctionShortName
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxSuiteExecutorBaseSuiteFunctionShortName
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxWasmJsRunBenchmarksFunctionCallableId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxWasmWasiRunBenchmarksFunctionCallableId
import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


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
    val stringToByteFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toByte"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToShortFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toShort"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToIntFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toInt"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToLongFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toLong"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToUByteFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toUByte"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToUShortFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toUShort"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToUIntFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toUInt"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToULongFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toULong"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToFloatFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toFloat"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToDoubleFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toDouble"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val stringToBooleanStrictFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("toBooleanStrict"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.stringType}
        }
    }
    val charSequenceSingleFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin.text"), className = null, callableName = Name.identifier("single"))) {
            it.owner.parameters.let { it.size == 1 && it[0].kind == ExtensionReceiver && it[0].type == pluginContext.irBuiltIns.charSequenceClass.defaultType}
        }
    }
    val listIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("List")))
    val listOfIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
        val parameters = symbol.owner.parameters
        parameters.size == 1 && parameters[0].isVararg
    }
    val mapIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("Map"))) }
    val mapGetIrSimpleFunction by lazy { mapIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("get")) }
    val mapOfIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("mapOf"))) { symbol ->
        val parameters = symbol.owner.parameters
        parameters.size == 1 && parameters[0].isVararg
    }
    val pairIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Pair")))
    val pairIrConstructorSymbol = pairIrClassSymbol.constructors.single()
    
    // Library
    val kotlinxBenchmarkModeAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkModeAnnotationClassId) }
    val kotlinxBenchmarkTimeUnitEnumIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkTimeUnitEnumClassId) }
    val kotlinxBlackholeClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBlackholeClassClassId) }
    val kotlinxBlackholeConstructorIrConstructorSymbol by lazy { kotlinxBlackholeClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxSuiteExecutorBaseClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxSuiteExecutorBaseClassClassId) }
    val kotlinxSuiteExecutorBaseSuiteFunctionIrSimpleFunctionSymbol by lazy { kotlinxSuiteExecutorBaseClassIrClassSymbol.referenceFunctionThatOrFail(kotlinxSuiteExecutorBaseSuiteFunctionShortName) }
    val kotlinxSuiteExecutorBaseRunFunctionIrSimpleFunctionSymbol by lazy { kotlinxSuiteExecutorBaseClassIrClassSymbol.referenceFunctionThatOrFail(kotlinxSuiteExecutorBaseRunFunctionShortName) }
    val kotlinxSuiteDescriptorClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxSuiteDescriptorClassClassId) }
    val kotlinxSuiteDescriptorConstructorIrConstructorSymbol by lazy { kotlinxSuiteDescriptorClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxSuiteDescriptorAddFunctionIrSimpleFunctionSymbol by lazy { kotlinxSuiteDescriptorClassIrClassSymbol.referenceFunctionThatOrFail(kotlinxSuiteDescriptorAddFunctionShortName) }
    val kotlinxIterationTimeClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxIterationTimeClassClassId) }
    val kotlinxIterationTimeConstructorIrConstructorSymbol by lazy { kotlinxIterationTimeClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassClassId) }
    val kotlinxBenchmarkDescriptorWithNoBlackholeParameterConstructorIrConstructorSymbol by lazy { kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkDescriptorWithBlackholeParameterClassClassId) }
    val kotlinxBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol by lazy { kotlinxBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassClassId) }
    val kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterConstructorIrConstructorSymbol by lazy { kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassClassId) }
    val kotlinxJsBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol by lazy { kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxJsRunBenchmarksFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(kotlinxJsRunBenchmarksFunctionCallableId) }
    val kotlinxJsRunBenchmarksBuiltInFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(kotlinxJsRunBenchmarksBuiltInFunctionCallableId) }
    val kotlinxWasmJsRunBenchmarksFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(kotlinxWasmJsRunBenchmarksFunctionCallableId) }
    val kotlinxWasmWasiRunBenchmarksFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(kotlinxWasmWasiRunBenchmarksFunctionCallableId) }
    val kotlinxNativeRunBenchmarksFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(kotlinxNativeRunBenchmarksFunctionCallableId) }
}