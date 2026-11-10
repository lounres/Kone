/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


val entryPointCallableId = CallableId(
    packageName = FqName("dev.lounres.kone.plugins.benchmarksRunners.generated"),
    className = null,
    callableName = Name.identifier("main"),
)

val entryPointFileName = "/run.kt"