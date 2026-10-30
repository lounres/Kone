/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val koneBenchmarksPackageFQNameString = "dev.lounres.kone.benchmarks"
const val kotlinxBenchmarkPackageFQNameString = "kotlinx.benchmark"
// Library
const val kotlinxStateAnnotationShortNameString = "State"
const val kotlinxScopeEnumShortNameString = "Scope"
const val kotlinxParamAnnotationShortNameString = "Param"
const val kotlinxSetupAnnotationShortNameString = "Setup"
const val kotlinxBenchmarkAnnotationShortNameString = "Benchmark"
const val kotlinxBlackholeClassShortNameString = "Blackhole"
// Public runtime
const val benchmarkAnnotationShortNameString = "Benchmark"
const val blackholePropertyShortNameString = "blackhole"
const val injectFunctionShortNameString = "inject"
const val disposableInjectionClassShortNameString = "Benchmark"
const val injectDisposableFunctionShortNameString = "injectDisposable"
const val benchmarkConfigurationClassShortNameString = "BenchmarkConfiguration"
const val benchmarkResultClassShortNameString = "BenchmarkResult"
const val benchmarkResultPropertyShortNameString = "benchmarkResult"

val koneBenchmarksPackageFQName = FqName(koneBenchmarksPackageFQNameString)
val kotlinxBenchmarkPackageFQName = FqName(kotlinxBenchmarkPackageFQNameString)
// Library
val kotlinxStateAnnotationShortName = FqName(kotlinxStateAnnotationShortNameString)
val kotlinxScopeEnumShortName = FqName(kotlinxScopeEnumShortNameString)
val kotlinxParamAnnotationShortName = FqName(kotlinxParamAnnotationShortNameString)
val kotlinxSetupAnnotationShortName = FqName(kotlinxSetupAnnotationShortNameString)
val kotlinxBenchmarkAnnotationShortName = FqName(kotlinxBenchmarkAnnotationShortNameString)
val kotlinxBlackholeClassShortName = FqName(kotlinxBlackholeClassShortNameString)
// Public runtime
val benchmarkAnnotationShortName = FqName(benchmarkAnnotationShortNameString)
val blackholePropertyShortName = Name.identifier(blackholePropertyShortNameString)
val injectFunctionShortName = Name.identifier(injectFunctionShortNameString)
val disposableInjectionClassShortName = FqName(disposableInjectionClassShortNameString)
val injectDisposableFunctionShortName = Name.identifier(injectDisposableFunctionShortNameString)
val benchmarkConfigurationClassShortName = FqName(benchmarkConfigurationClassShortNameString)
val benchmarkResultClassShortName = FqName(benchmarkResultClassShortNameString)
val benchmarkResultPropertyShortName = Name.identifier(benchmarkResultPropertyShortNameString)

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
// Public runtime
val benchmarkAnnotationClassId = ClassId(
    packageFqName = koneBenchmarksPackageFQName,
    relativeClassName = benchmarkAnnotationShortName,
    isLocal = false,
)
val blackholePropertyCallableId = CallableId(
    packageName = koneBenchmarksPackageFQName,
    className = null,
    callableName = blackholePropertyShortName,
)
val injectFunctionCallableId = CallableId(
    packageName = koneBenchmarksPackageFQName,
    className = null,
    callableName = injectFunctionShortName,
)
val disposableInjectionClassClassId = ClassId(
    packageFqName = koneBenchmarksPackageFQName,
    relativeClassName = disposableInjectionClassShortName,
    isLocal = false,
)
val injectDisposableFunctionCallableId = CallableId(
    packageName = koneBenchmarksPackageFQName,
    className = null,
    callableName = injectDisposableFunctionShortName,
)
val benchmarkConfigurationClassClassId = ClassId(
    packageFqName = koneBenchmarksPackageFQName,
    relativeClassName = benchmarkConfigurationClassShortName,
    isLocal = false,
)
val benchmarkResultClassClassId = ClassId(
    packageFqName = koneBenchmarksPackageFQName,
    relativeClassName = benchmarkResultClassShortName,
    isLocal = false,
)
val benchmarkResultPropertyCallableId = CallableId(
    packageName = koneBenchmarksPackageFQName,
    className = null,
    callableName = benchmarkResultPropertyShortName,
)