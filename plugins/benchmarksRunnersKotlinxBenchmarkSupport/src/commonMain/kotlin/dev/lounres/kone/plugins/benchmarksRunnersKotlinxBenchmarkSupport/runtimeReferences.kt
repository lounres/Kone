/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val kotlinxBenchmarkPackageFQNameString = "kotlinx.benchmark"
const val kotlinxBenchmarkJsPackageFQNameString = "kotlinx.benchmark.js"
const val kotlinxBenchmarkWasmJsPackageFQNameString = "kotlinx.benchmark.wasm"
const val kotlinxBenchmarkWasmWasiPackageFQNameString = "kotlinx.benchmark.wasm"
const val kotlinxBenchmarkNativePackageFQNameString = "kotlinx.benchmark.native"
const val koneMiscBenchmarksPackageFqNameString = "dev.lounres.kone.misc.benchmarks"
const val koneMiscBenchmarksSupportKotlinxBenchmarkPackageFqNameString = "dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark"
const val koneMiscBenchmarksRunnersPackageFqNameString = "dev.lounres.kone.misc.benchmarks.runners"
const val koneMiscBenchmarksRunnersSupportKotlinxBenchmarkPackageFqNameString = "dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark"
const val koneMiscBenchmarksRunnersSupportKotlinxBenchmarkInternalPackageFqNameString = "dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal"
// Library
const val kotlinxStateAnnotationShortNameString = "State"
const val kotlinxScopeEnumShortNameString = "Scope"
const val kotlinxScopeBenchmarkEnumEntryShortNameString = "Benchmark"
const val kotlinxBenchmarkModeAnnotationShortNameString = "BenchmarkMode"
const val kotlinxModeClassShortNameString = "BenchmarkMode"
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
const val kotlinxJsRunBenchmarksFunctionShortNameString = "runBenchmarks"
const val kotlinxJsRunBenchmarksBuiltInFunctionShortNameString = "runBenchmarksBuiltIn"
const val kotlinxWasmJsRunBenchmarksFunctionShortNameString = "runBenchmarks"
const val kotlinxWasmWasiRunBenchmarksFunctionShortNameString = "runBenchmarks"
const val kotlinxNativeRunBenchmarksFunctionShortNameString = "runBenchmarks"
// Public API
const val kotlinxBenchmarkBenchmarkClassShortNameString = "KotlinxBenchmarkBenchmark"
const val kotlinxBenchmarkConfigurationClassShortNameString = "KotlinxBenchmarkConfiguration"
const val koneBenchmarkExecutorClassShortNameString = "KoneBenchmarkExecutor"
const val kotlinxBenchmarkPropertyShortNameString = "kotlinxBenchmark"
const val koneBenchmarkRunnerAnnotationShortNameString = "KoneBenchmarkRunner"
const val benchmarkTimeUnitWrapperClassShortNameString = "BenchmarkTimeUnitWrapper"
const val modeWrapperClassShortNameString = "ModeWrapper"

val kotlinxBenchmarkPackageFQName = FqName(kotlinxBenchmarkPackageFQNameString)
val kotlinxBenchmarkJsPackageFQName = FqName(kotlinxBenchmarkJsPackageFQNameString)
val kotlinxBenchmarkWasmJsPackageFQName = FqName(kotlinxBenchmarkWasmJsPackageFQNameString)
val kotlinxBenchmarkWasmWasiPackageFQName = FqName(kotlinxBenchmarkWasmWasiPackageFQNameString)
val kotlinxBenchmarkNativePackageFQName = FqName(kotlinxBenchmarkNativePackageFQNameString)
val koneMiscBenchmarksPackageFqName = FqName(koneMiscBenchmarksPackageFqNameString)
val koneMiscBenchmarksSupportKotlinxBenchmarkPackageFqName = FqName(koneMiscBenchmarksSupportKotlinxBenchmarkPackageFqNameString)
val koneMiscBenchmarksRunnersPackageFqName = FqName(koneMiscBenchmarksRunnersPackageFqNameString)
val koneMiscBenchmarksRunnersSupportKotlinxBenchmarkPackageFqName = FqName(koneMiscBenchmarksRunnersSupportKotlinxBenchmarkPackageFqNameString)
val koneMiscBenchmarksRunnersSupportKotlinxBenchmarkInternalPackageFqName = FqName(koneMiscBenchmarksRunnersSupportKotlinxBenchmarkInternalPackageFqNameString)
// Library
val kotlinxStateAnnotationShortName = FqName(kotlinxStateAnnotationShortNameString)
val kotlinxScopeEnumShortName = FqName(kotlinxScopeEnumShortNameString)
val kotlinxScopeBenchmarkEnumEntryShortName = Name.identifier(kotlinxScopeBenchmarkEnumEntryShortNameString)
val kotlinxBenchmarkModeAnnotationShortName = FqName(kotlinxBenchmarkModeAnnotationShortNameString)
val kotlinxModeClassShortName = FqName(kotlinxModeClassShortNameString)
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
val kotlinxJsRunBenchmarksFunctionShortName = Name.identifier(kotlinxJsRunBenchmarksFunctionShortNameString)
val kotlinxJsRunBenchmarksBuiltInFunctionShortName = Name.identifier(kotlinxJsRunBenchmarksBuiltInFunctionShortNameString)
val kotlinxWasmJsRunBenchmarksFunctionShortName = Name.identifier(kotlinxWasmJsRunBenchmarksFunctionShortNameString)
val kotlinxWasmWasiRunBenchmarksFunctionShortName = Name.identifier(kotlinxWasmWasiRunBenchmarksFunctionShortNameString)
val kotlinxNativeRunBenchmarksFunctionShortName = Name.identifier(kotlinxNativeRunBenchmarksFunctionShortNameString)
// Public API
val kotlinxBenchmarkBenchmarkClassShortName = FqName(kotlinxBenchmarkBenchmarkClassShortNameString)
val kotlinxBenchmarkConfigurationClassShortName = FqName(kotlinxBenchmarkConfigurationClassShortNameString)
val koneBenchmarkExecutorClassShortName = FqName(koneBenchmarkExecutorClassShortNameString)
val kotlinxBenchmarkPropertyShortName = Name.identifier(kotlinxBenchmarkPropertyShortNameString)
val koneBenchmarkRunnerAnnotationShortName = FqName(koneBenchmarkRunnerAnnotationShortNameString)
val benchmarkTimeUnitWrapperClassShortName = FqName(benchmarkTimeUnitWrapperClassShortNameString)
val modeWrapperClassShortName = FqName(modeWrapperClassShortNameString)

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
val kotlinxModeClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkPackageFQName,
    relativeClassName = kotlinxModeClassShortName,
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
    packageFqName = kotlinxBenchmarkJsPackageFQName,
    relativeClassName = kotlinxJsBenchmarkDescriptorWithNoBlackholeParameterClassShortName,
    isLocal = false,
)
val kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassClassId = ClassId(
    packageFqName = kotlinxBenchmarkJsPackageFQName,
    relativeClassName = kotlinxJsBenchmarkDescriptorWithBlackholeParameterClassShortName,
    isLocal = false,
)
val kotlinxJsRunBenchmarksFunctionCallableId = CallableId(
    packageName = kotlinxBenchmarkJsPackageFQName,
    className = null,
    callableName = kotlinxJsRunBenchmarksFunctionShortName,
)
val kotlinxJsRunBenchmarksBuiltInFunctionCallableId = CallableId(
    packageName = kotlinxBenchmarkJsPackageFQName,
    className = null,
    callableName = kotlinxJsRunBenchmarksBuiltInFunctionShortName,
)
val kotlinxWasmJsRunBenchmarksFunctionCallableId = CallableId(
    packageName = kotlinxBenchmarkWasmJsPackageFQName,
    className = null,
    callableName = kotlinxWasmJsRunBenchmarksFunctionShortName,
)
val kotlinxWasmWasiRunBenchmarksFunctionCallableId = CallableId(
    packageName = kotlinxBenchmarkWasmWasiPackageFQName,
    className = null,
    callableName = kotlinxWasmWasiRunBenchmarksFunctionShortName,
)
val kotlinxNativeRunBenchmarksFunctionCallableId = CallableId(
    packageName = kotlinxBenchmarkNativePackageFQName,
    className = null,
    callableName = kotlinxNativeRunBenchmarksFunctionShortName,
)
// Public API
val kotlinxBenchmarkBenchmarkClassClassId = ClassId(
    packageFqName = koneMiscBenchmarksSupportKotlinxBenchmarkPackageFqName,
    relativeClassName = kotlinxBenchmarkBenchmarkClassShortName,
    isLocal = false,
)
val kotlinxBenchmarkConfigurationClassClassId = ClassId(
    packageFqName = koneMiscBenchmarksSupportKotlinxBenchmarkPackageFqName,
    relativeClassName = kotlinxBenchmarkConfigurationClassShortName,
    isLocal = false,
)
val koneBenchmarkExecutorClassClassId = ClassId(
    packageFqName = koneMiscBenchmarksPackageFqName,
    relativeClassName = koneBenchmarkExecutorClassShortName,
    isLocal = false,
)
val kotlinxBenchmarkPropertyCallableId = CallableId(
    packageName = koneMiscBenchmarksSupportKotlinxBenchmarkPackageFqName,
    className = null,
    callableName = kotlinxBenchmarkPropertyShortName,
)
val koneBenchmarkRunnerAnnotationClassId = ClassId(
    packageFqName = koneMiscBenchmarksRunnersPackageFqName,
    relativeClassName = koneBenchmarkRunnerAnnotationShortName,
    isLocal = false,
)
val benchmarkTimeUnitWrapperClassClassId = ClassId(
    packageFqName = koneMiscBenchmarksRunnersSupportKotlinxBenchmarkInternalPackageFqName,
    relativeClassName = benchmarkTimeUnitWrapperClassShortName,
    isLocal = false,
)
val modeWrapperClassClassId = ClassId(
    packageFqName = koneMiscBenchmarksRunnersSupportKotlinxBenchmarkInternalPackageFqName,
    relativeClassName = modeWrapperClassShortName,
    isLocal = false,
)