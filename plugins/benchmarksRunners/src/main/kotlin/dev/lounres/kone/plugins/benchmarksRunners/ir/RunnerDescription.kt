/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.ir

import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction


data class RunnerDescription(
    val name: String,
    val theRunnerFunction: IrSimpleFunction,
    val parent: IrDeclarationParent,
)