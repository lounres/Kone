/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


val kotlinxBenchmarkSupportEntryPointCallableId = CallableId(
    packageName = FqName("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated"),
    className = null,
    callableName = Name.identifier("runKotlinxBenchmarkSupport"),
)