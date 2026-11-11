/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarks.ir

import dev.lounres.kone.plugins.benchmarks.benchmarkAnnotationClassId
import dev.lounres.kone.plugins.benchmarks.benchmarkConfigurationClassClassId
import dev.lounres.kone.plugins.benchmarks.benchmarkResultClassClassId
import dev.lounres.kone.plugins.benchmarks.benchmarkResultPropertyCallableId
import dev.lounres.kone.plugins.benchmarks.blackholePropertyCallableId
import dev.lounres.kone.plugins.benchmarks.disposableInjectionClassClassId
import dev.lounres.kone.plugins.benchmarks.injectDisposableFunctionCallableId
import dev.lounres.kone.plugins.benchmarks.injectFunctionCallableId
import dev.lounres.kone.plugins.benchmarks.injectedValueClassClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxBenchmarkAnnotationClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxBlackholeClassClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxJsonClassClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxJsonDecodeFromStringFunctionShortName
import dev.lounres.kone.plugins.benchmarks.kotlinxJsonEncodeToStringFunctionShortName
import dev.lounres.kone.plugins.benchmarks.kotlinxParamAnnotationClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxScopeBenchmarkEnumEntryShortName
import dev.lounres.kone.plugins.benchmarks.kotlinxScopeEnumClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxSetupAnnotationClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxStateAnnotationClassId
import dev.lounres.kone.plugins.benchmarks.kotlinxTearDownAnnotationClassId
import dev.lounres.kone.plugins.benchmarks.runBenchmarkFunctionCallableId
import org.jetbrains.kotlin.backend.common.extensions.DeclarationFinder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.*


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

val IrSimpleFunction.isBenchmark: Boolean get() = hasAnnotation(benchmarkAnnotationClassId)

class IrRuntimeReferences(private val pluginContext: IrPluginContext) {
    private val finder by lazy { pluginContext.finderForBuiltins() }
    
    // Kotlin
    val runIrSimpleFunctionSymbol = finder.referenceFunctionThatOrFail(CallableId(packageName = FqName("kotlin"), callableName = Name.identifier("run"))) {
        it.owner.parameters.size == 1
    }
    val listIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("List")))
    val listOfIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("listOf"))) { symbol ->
        val parameters = symbol.owner.parameters
        parameters.size == 1 && parameters[0].isVararg
    }
    val emptyListIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("emptyList"))) { symbol ->
        val parameters = symbol.owner.parameters
        parameters.size == 0
    }
    val mapIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin.collections"), topLevelName = Name.identifier("Map")))
    val mapGetIrSimpleFunction = mapIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("get"))
    val mapOfIrSimpleFunction = finder.referenceFunctionThatOrFail(CallableId(FqName("kotlin.collections"), Name.identifier("mapOf"))) { symbol ->
        val parameters = symbol.owner.parameters
        parameters.size == 1 && parameters[0].isVararg
    }
    val pairIrClassSymbol = finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Pair")))
    val pairIrConstructorSymbol = pairIrClassSymbol.constructors.single()
    
    // Library
    val kotlinxStateAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxStateAnnotationClassId) }
    val kotlinxScopeEnumIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxScopeEnumClassId) }
    val kotlinxScopeBenchmarkEnumEntryIrEnumEntrySymbol by lazy {
        val entry = kotlinxScopeEnumIrClassSymbol.owner.declarations.single {
            it is IrEnumEntry && it.name == kotlinxScopeBenchmarkEnumEntryShortName
        } as IrEnumEntry
        entry.symbol
    }
    val kotlinxParamAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxParamAnnotationClassId) }
    val kotlinxSetupAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxSetupAnnotationClassId) }
    val kotlinxTearDownAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxTearDownAnnotationClassId) }
    val kotlinxBenchmarkAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBenchmarkAnnotationClassId) }
    val kotlinxBlackholeClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxBlackholeClassClassId) }
    val kotlinxBlackholeConstructorIrConstructorSymbol by lazy { kotlinxBlackholeClassIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxJsonClassIrClassSymbol by lazy { finder.referenceClassOrFail(kotlinxJsonClassClassId) }
    val kotlinxJsonCompanionIrClassSymbol by lazy { kotlinxJsonClassIrClassSymbol.owner.companionObject()!!.symbol }
    val kotlinxJsonEncodeToStringFunctionIrSimpleFunctionSymbol by lazy {
        kotlinxJsonClassIrClassSymbol.referenceFunctionThatOrFail(kotlinxJsonEncodeToStringFunctionShortName) { functionSymbol ->
            functionSymbol.owner.isInline && functionSymbol.owner.typeParameters.let { it.size == 1 && it[0].isReified }
        }
    }
    val kotlinxJsonDecodeFromStringFunctionIrSimpleFunctionSymbol by lazy {
        kotlinxJsonClassIrClassSymbol.referenceFunctionThatOrFail(kotlinxJsonDecodeFromStringFunctionShortName) { functionSymbol ->
            functionSymbol.owner.isInline && functionSymbol.owner.typeParameters.let { it.size == 1 && it[0].isReified }
        }
    }
    // TODO: Move the following
    val kotlinxSuiteExecutorBaseIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlinx.benchmark"), relativeClassName = FqName("SuiteExecutorBase"), isLocal = false)) }
    val kotlinxSuiteExecutorBaseSuiteIrSimpleFunctionSymbol by lazy { kotlinxSuiteExecutorBaseIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("suite")) }
    val kotlinxSuiteDescriptorIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlinx.benchmark"), relativeClassName = FqName("SuiteDescriptor"), isLocal = false)) }
    val kotlinxSuiteDescriptorConstructorIrConstructorSymbol by lazy { kotlinxSuiteDescriptorIrClassSymbol.owner.primaryConstructor!!.symbol }
    val kotlinxSuiteDescriptorAddIrSimpleFunctionSymbol by lazy { kotlinxSuiteDescriptorIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("add")) }
    val kotlinxBenchmarkDescriptorWithBlackholeParameterIrClassSymbol by lazy { finder.referenceClassOrFail(ClassId(packageFqName = FqName("kotlinx.benchmark"), relativeClassName = FqName("BenchmarkDescriptorWithBlackholeParameter"), isLocal = false)) }
    val kotlinxBenchmarkDescriptorWithBlackholeParameterConstructorIrConstructorSymbol by lazy { kotlinxBenchmarkDescriptorWithBlackholeParameterIrClassSymbol.owner.primaryConstructor!!.symbol }
    
    // Public runtime
    val benchmarkAnnotationIrClassSymbol by lazy { finder.referenceClassOrFail(benchmarkAnnotationClassId) }
    val blackholePropertyIrPropertySymbol by lazy { finder.referencePropertyThatOrFail(blackholePropertyCallableId) }
    val blackholePropertyGetterIrSimpleFunctionSymbol by lazy { blackholePropertyIrPropertySymbol.owner.getter!!.symbol }
    val injectFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(injectFunctionCallableId) }
    val disposableInjectionClassIrClassSymbol by lazy { finder.referenceClassOrFail(disposableInjectionClassClassId) }
    val disposableInjectionValuePropertyIrPropertySymbol by lazy { disposableInjectionClassIrClassSymbol.referencePropertyThatOrFail(Name.identifier("value")) }
    val disposableInjectionValuePropertyGetterIrSimpleFunctionSymbol by lazy { disposableInjectionValuePropertyIrPropertySymbol.owner.getter!!.symbol }
    val disposableInjectionDisposeFunctionIrSimpleFunctionSymbol by lazy { disposableInjectionClassIrClassSymbol.referenceFunctionThatOrFail(Name.identifier("dispose")) }
    val injectDisposableFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(injectDisposableFunctionCallableId) }
    val benchmarkConfigurationClassIrClassSymbol by lazy { finder.referenceClassOrFail(benchmarkConfigurationClassClassId) }
    val benchmarkResultClassIrClassSymbol by lazy { finder.referenceClassOrFail(benchmarkResultClassClassId) }
    val benchmarkResultPropertyIrPropertySymbol by lazy { finder.referencePropertyThatOrFail(benchmarkResultPropertyCallableId) }
    val benchmarkResultPropertyGetterIrSimpleFunctionSymbol by lazy { benchmarkResultPropertyIrPropertySymbol.owner.getter!!.symbol }
    
    // Private runtime
    val injectedValueClassIrClassSymbol by lazy { finder.referenceClassOrFail(injectedValueClassClassId) }
    val injectedValueConstructorIrConstructorSymbol by lazy { injectedValueClassIrClassSymbol.constructors.single() }
    val injectedValueValuePropertyIrPropertySymbol by lazy { injectedValueClassIrClassSymbol.referencePropertyThatOrFail(Name.identifier("value")) }
    val injectedValueValuePropertyGetterIrSimpleFunctionSymbol by lazy { injectedValueValuePropertyIrPropertySymbol.owner.getter!!.symbol }
    val injectedValueDisposableInjectionPropertyIrPropertySymbol by lazy { injectedValueClassIrClassSymbol.referencePropertyThatOrFail(Name.identifier("disposableInjection")) }
    val injectedValueDisposableInjectionPropertyGetterIrSimpleFunctionSymbol by lazy { injectedValueDisposableInjectionPropertyIrPropertySymbol.owner.getter!!.symbol }
    val runBenchmarkFunctionIrSimpleFunctionSymbol by lazy { finder.referenceFunctionThatOrFail(runBenchmarkFunctionCallableId) }
}