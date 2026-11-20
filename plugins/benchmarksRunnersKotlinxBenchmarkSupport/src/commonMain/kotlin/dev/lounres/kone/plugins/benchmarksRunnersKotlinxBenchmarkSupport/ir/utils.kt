/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.benchmarkTimeUnitWrapperClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.koneBenchmarkExecutorClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.koneBenchmarkRunnerAnnotationClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkBenchmarkClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkConfigurationClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkDescriptorWithBlackholeParameterClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkModeAnnotationClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkPropertyCallableId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkTimeUnitEnumClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBlackholeClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxIterationTimeClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxJsRunBenchmarksBuiltInFunctionCallableId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxJsRunBenchmarksFunctionCallableId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxModeClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxNativeRunBenchmarksFunctionCallableId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxSuiteDescriptorAddFunctionShortName
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxSuiteDescriptorClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxSuiteExecutorBaseClassClassId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxSuiteExecutorBaseRunFunctionShortName
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxSuiteExecutorBaseSuiteFunctionShortName
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxWasmJsRunBenchmarksFunctionCallableId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxWasmWasiRunBenchmarksFunctionCallableId
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.modeWrapperClassClassId
import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
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
import kotlin.getValue


class BenchmarksIrPluginException(message: String? = null, reason: Throwable? = null) : RuntimeException(message, reason)
fun benchmarksIrPluginException(message: String? = null, reason: Throwable? = null): Nothing = throw BenchmarksIrPluginException(message, reason)

fun couldNotFindClass(classId: ClassId): Nothing = error("Could not find class '${classId.asFqNameString()}'")
fun couldNotFindCorrespondingCallable(callableId: CallableId): Nothing = error("Could not find corresponding callable '${callableId.asSingleFqName()}'.")
fun couldNotFindCorrespondingConstructorInClass(classId: ClassId): Nothing = error("Could not find corresponding constructor in class '${classId.asFqNameString()}'.")
fun couldNotFindCorrespondingFunctionInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding function '$functionName' in class '${classId.asFqNameString()}'.")
fun couldNotFindCorrespondingPropertyInClass(classId: ClassId, functionName: Name): Nothing = error("Could not find corresponding property '$functionName' in class '${classId.asFqNameString()}'.")

fun DeclarationFinder.referenceClassOrFail(classId: ClassId): IrClassSymbol =
    findClass(classId) ?: couldNotFindClass(classId)
inline fun DeclarationFinder.referenceFunctionThatOrFail(callableId: CallableId, predicate: (IrSimpleFunctionSymbol) -> Boolean = { true }): IrSimpleFunctionSymbol =
    findFunctions(callableId).singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)
inline fun DeclarationFinder.referencePropertyThatOrFail(callableId: CallableId, predicate: (IrPropertySymbol) -> Boolean = { true }): IrPropertySymbol =
    findProperties(callableId).singleOrNull(predicate) ?: couldNotFindCorrespondingCallable(callableId)
inline fun IrClassSymbol.referenceConstructorThatOrFail(predicate: (IrConstructorSymbol) -> Boolean = { true }): IrConstructorSymbol =
    owner.declarations.asSequence().filterIsInstance<IrConstructor>().map { it.symbol }.singleOrNull(predicate) ?: couldNotFindCorrespondingConstructorInClass(owner.classIdOrFail)
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
    val emptyArrayFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin"), Name.identifier("emptyArray"))) }
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
    val printlnFunctionIrSimpleFunctionSymbol by lazy {
        finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.io"), Name.identifier("println"))) {
            it.owner.parameters.let { it.size == 1 && it[0].type == pluginContext.irBuiltIns.anyNType }
        }
    }
    
    // Library
    val kotlinxBenchmarkModeAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkModeAnnotationClassId) }
    val kotlinxModeClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxModeClassClassId) }
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
    val kotlinxBenchmarkBenchmarkClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkBenchmarkClassClassId) }
    val kotlinxBenchmarkBenchmarkConstructorIrConstructorSymbol by lazy { kotlinxBenchmarkBenchmarkClassIrClassSymbol.constructors.single() }
    val kotlinxBenchmarkConfigurationClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkConfigurationClassClassId) }
    val kotlinxBenchmarkConfigurationConstructorIrConstructorSymbol by lazy { kotlinxBenchmarkConfigurationClassIrClassSymbol.constructors.single() }
    val koneBenchmarkExecutorClassIrClassSymbol by lazy { finder.referenceClassOrFail(koneBenchmarkExecutorClassClassId) }
    val koneBenchmarkExecutorExecuteFunctionIrSimpleFunctionSymbol by lazy { koneBenchmarkExecutorClassIrClassSymbol.referenceFunctionThatOrFail("execute") }
    val kotlinxBenchmarkPropertyIrPropertySymbol by lazy { finder.referencePropertyThatOrFail(kotlinxBenchmarkPropertyCallableId) }
    val kotlinxBenchmarkPropertyGetterIrSimpleFunctionSymbol by lazy { kotlinxBenchmarkPropertyIrPropertySymbol.owner.getter!!.symbol }
    val koneBenchmarkRunnerAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(koneBenchmarkRunnerAnnotationClassId) }
    val benchmarkTimeUnitWrapperClassIrClassSymbol by lazy { finder.referenceClassOrFail(benchmarkTimeUnitWrapperClassClassId) }
    val benchmarkTimeUnitWrapperValuePropertyIrPropertySymbol by lazy { benchmarkTimeUnitWrapperClassIrClassSymbol.referencePropertyThatOrFail("value") }
    val benchmarkTimeUnitWrapperValuePropertyGetterIrSimpleFunctionSymbol by lazy { benchmarkTimeUnitWrapperValuePropertyIrPropertySymbol.owner.getter!!.symbol }
    val modeWrapperClassIrClassSymbol by lazy { finder.referenceClassOrFail(modeWrapperClassClassId) }
    val modeWrapperValuePropertyIrPropertySymbol by lazy { modeWrapperClassIrClassSymbol.referencePropertyThatOrFail("value") }
    val modeWrapperValuePropertyGetterIrSimpleFunctionSymbol by lazy { modeWrapperValuePropertyIrPropertySymbol.owner.getter!!.symbol }
}