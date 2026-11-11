/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarks.ir

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter


data class GeneratedBenchmarkClassDescription(
    val benchmarkClass: IrClass,
    val parameters: Map<IrValueParameter, IrField>,
    val setupFunction: IrSimpleFunction,
    val benchmarkFunction: IrSimpleFunction,
    val tearDownFunction: IrSimpleFunction,
)
