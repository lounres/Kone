/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val kotlinxBenchmarkPackageFQNameString = "kotlinx.benchmark"
// Library
const val kotlinxStateAnnotationShortNameString = "State"
const val kotlinxScopeEnumShortNameString = "Scope"
const val kotlinxScopeBenchmarkEnumEntryShortNameString = "Benchmark"
const val kotlinxBenchmarkModeAnnotationShortNameString = "BenchmarkMode"
const val kotlinxBenchmarkTimeUnitEnumShortNameString = "BenchmarkTimeUnit"
const val kotlinxOutputTimeUnitAnnotationShortNameString = "OutputTimeUnit"
const val kotlinxWarmupAnnotationShortNameString = "Warmup"
const val kotlinxMeasurementAnnotationShortNameString = "Measurement"
const val kotlinxParamAnnotationShortNameString = "Param"
const val kotlinxSetupAnnotationShortNameString = "Setup"
const val kotlinxTearDownAnnotationShortNameString = "TearDown"
const val kotlinxBenchmarkAnnotationShortNameString = "Benchmark"
const val kotlinxBlackholeClassShortNameString = "Blackhole"
const val kotlinxSuiteExecutorBaseClassShortNameString = "SuiteExecutorBase"
const val kotlinxSuiteExecutorBaseSuiteFunctionShortNameString = "suite"
const val kotlinxSuiteExecutorBaseRunFunctionShortNameString = "run"
const val kotlinxSuiteDescriptorClassShortNameString = "SuiteDescriptor"
const val kotlinxSuiteDescriptorAddFunctionShortNameString = "add"
const val kotlinxIterationTimeClassShortNameString = "IterationTime"
const val kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassShortNameString = "BenchmarkDescriptorWithNoBlackholeParameter"
const val kotlinxBenchmarkDescriptorWithBlackholeParameterClassShortNameString = "BenchmarkDescriptorWithBlackholeParameter"
const val kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassShortNameString = "JsBenchmarkDescriptorWithNoBlackholeParameter"
const val kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassShortNameString = "JsBenchmarkDescriptorWithBlackholeParameter"

val kotlinxBenchmarkPackageFQName = FqName(kotlinxBenchmarkPackageFQNameString)
// Library
val kotlinxStateAnnotationShortName = FqName(kotlinxStateAnnotationShortNameString)
val kotlinxScopeEnumShortName = FqName(kotlinxScopeEnumShortNameString)
val kotlinxScopeBenchmarkEnumEntryShortName = Name.identifier(kotlinxScopeBenchmarkEnumEntryShortNameString)
val kotlinxBenchmarkModeAnnotationShortName = FqName(kotlinxBenchmarkModeAnnotationShortNameString)
val kotlinxBenchmarkTimeUnitEnumShortName = FqName(kotlinxBenchmarkTimeUnitEnumShortNameString)
val kotlinxOutputTimeUnitAnnotationShortName = FqName(kotlinxOutputTimeUnitAnnotationShortNameString)
val kotlinxWarmupAnnotationShortName = FqName(kotlinxWarmupAnnotationShortNameString)
val kotlinxMeasurementAnnotationShortName = FqName(kotlinxMeasurementAnnotationShortNameString)
val kotlinxParamAnnotationShortName = FqName(kotlinxParamAnnotationShortNameString)
val kotlinxSetupAnnotationShortName = FqName(kotlinxSetupAnnotationShortNameString)
val kotlinxTearDownAnnotationShortName = FqName(kotlinxTearDownAnnotationShortNameString)
val kotlinxBenchmarkAnnotationShortName = FqName(kotlinxBenchmarkAnnotationShortNameString)
val kotlinxBlackholeClassShortName = FqName(kotlinxBlackholeClassShortNameString)
val kotlinxSuiteExecutorBaseClassShortName = FqName(kotlinxSuiteExecutorBaseClassShortNameString)
val kotlinxSuiteExecutorBaseSuiteFunctionShortName = Name.identifier(kotlinxSuiteExecutorBaseSuiteFunctionShortNameString)
val kotlinxSuiteExecutorBaseRunFunctionShortName = Name.identifier(kotlinxSuiteExecutorBaseRunFunctionShortNameString)
val kotlinxSuiteDescriptorClassShortName = FqName(kotlinxSuiteDescriptorClassShortNameString)
val kotlinxSuiteDescriptorAddFunctionShortName = Name.identifier(kotlinxSuiteDescriptorAddFunctionShortNameString)
val kotlinxIterationTimeClassShortName = FqName(kotlinxIterationTimeClassShortNameString)
val kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassShortName = FqName(kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassShortNameString)
val kotlinxBenchmarkDescriptorWithBlackholeParameterClassShortName = FqName(kotlinxBenchmarkDescriptorWithBlackholeParameterClassShortNameString)
val kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassShortName = FqName(kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassShortNameString)
val kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassShortName = FqName(kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassShortNameString)

// Library
val kotlinxStateAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxStateAnnotationShortName,
    isLocal = false,
)
val kotlinxScopeEnumClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxScopeEnumShortName,
    isLocal = false,
)
val kotlinxBenchmarkModeAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxBenchmarkModeAnnotationShortName,
    isLocal = false,
)
val kotlinxBenchmarkTimeUnitEnumClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxBenchmarkTimeUnitEnumShortName,
    isLocal = false,
)
val kotlinxOutputTimeUnitAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxOutputTimeUnitAnnotationShortName,
    isLocal = false,
)
val kotlinxWarmupAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxWarmupAnnotationShortName,
    isLocal = false,
)
val kotlinxMeasurementAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxMeasurementAnnotationShortName,
    isLocal = false,
)
val kotlinxParamAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxParamAnnotationShortName,
    isLocal = false,
)
val kotlinxSetupAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxSetupAnnotationShortName,
    isLocal = false,
)
val kotlinxTearDownAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxTearDownAnnotationShortName,
    isLocal = false,
)
val kotlinxBenchmarkAnnotationClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxBenchmarkAnnotationShortName,
    isLocal = false,
)
val kotlinxBlackholeClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxBlackholeClassShortName,
    isLocal = false,
)
val kotlinxSuiteExecutorBaseClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxSuiteExecutorBaseClassShortName,
    isLocal = false,
)
val kotlinxSuiteDescriptorClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxSuiteDescriptorClassShortName,
    isLocal = false,
)
val kotlinxIterationTimeClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxIterationTimeClassShortName,
    isLocal = false,
)
val kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxBenchmarkDescriptorWithNoBlackholeParameterClassShortName,
    isLocal = false,
)
val kotlinxBenchmarkDescriptorWithBlackholeParameterClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxBenchmarkDescriptorWithBlackholeParameterClassShortName,
    isLocal = false,
)
val kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassShortName,
    isLocal = false,
)
val kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassShortName,
    isLocal = false,
)
val kotlinxJsRunBenchmarksFunctionCallableId = CallableId(
    packageName = FqName("kotlinx.benchmark.js"),
    className = null,
    callableName = Name.identifier("runBenchmarks"),
)
val kotlinxJsRunBenchmarksBuiltInFunctionCallableId = CallableId(
    packageName = FqName("kotlinx.benchmark.js"),
    className = null,
    callableName = Name.identifier("runBenchmarksBuiltIn"),
)
val kotlinxWasmJsRunBenchmarksFunctionCallableId = CallableId(
    packageName = FqName("kotlinx.benchmark.wasm"),
    className = null,
    callableName = Name.identifier("runBenchmarks"),
)
val kotlinxWasmWasiRunBenchmarksFunctionCallableId = CallableId(
    packageName = FqName("kotlinx.benchmark.wasm"),
    className = null,
    callableName = Name.identifier("runBenchmarks"),
)
val kotlinxNativeRunBenchmarksFunctionCallableId = CallableId(
    packageName = FqName("kotlinx.benchmark.native"),
    className = null,
    callableName = Name.identifier("runBenchmarks"),
)