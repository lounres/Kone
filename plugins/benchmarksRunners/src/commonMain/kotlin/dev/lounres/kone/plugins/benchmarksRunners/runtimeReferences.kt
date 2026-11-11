/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName


const val koneMiscBenchmarksRunnersPackageFqNameString = "dev.lounres.kone.misc.benchmarks.runners"
// Public API
const val koneBenchmarkRunnerAnnotationShortNameString = "KoneBenchmarkRunner"

val koneMiscBenchmarksRunnersPackageFqName = FqName(koneMiscBenchmarksRunnersPackageFqNameString)
// Public API
val koneBenchmarkRunnerAnnotationShortName = FqName(koneBenchmarkRunnerAnnotationShortNameString)

// Public API
val koneBenchmarkRunnerAnnotationClassId = ClassId(
    packageFqName = koneMiscBenchmarksRunnersPackageFqName,
    relativeClassName = koneBenchmarkRunnerAnnotationShortName,
    isLocal = false,
)