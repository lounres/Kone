/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.kotlinxBenchmarkSupportEntryPointCallableId
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.platform.JsPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatform
import org.jetbrains.kotlin.platform.wasm.WasmPlatformWithTarget
import org.jetbrains.kotlin.platform.wasm.WasmTarget.JS


class BenchmarksRunnersKotlinxBenchmarkSupportFirBasedIrGenerationExtension(
    private val lastPhase: UInt = MAX_VALUE,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        val platform = moduleFragment.descriptor.platform
        val simplePlatform = platform?.singleOrNull()
        
        val kotlinxBenchmarkSupportEntryPoint by lazy {
            moduleFragment
                .files
                .filter { it.packageFqName == kotlinxBenchmarkSupportEntryPointCallableId.packageName }
                .flatMap { it.declarations }
                .filterIsInstance<IrSimpleFunction>()
                .filter {
                    if (it.name != kotlinxBenchmarkSupportEntryPointCallableId.callableName) return@filter false
                    if (it.parameters.size != 17) return@filter false
                    true
                }
                .single()
        }
        
        val benchmarkClasses by lazy {
            buildList {
                moduleFragment.accept(
                    KotlinxBenchmarkClassCollectorVisitor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        benchmarkClasses = this
                    ),
                    null
                )
            }
        }
        
        when {
            simplePlatform is JvmPlatform -> {
                kotlinxBenchmarkSupportEntryPoint.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = kotlinxBenchmarkSupportEntryPoint.symbol,
                ).irBlockBody {
                    +jvmRunBenchmarkExecutor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
                        benchmarkClasses = benchmarkClasses,
                    )
                }
            }
            simplePlatform is JsPlatform -> {
                kotlinxBenchmarkSupportEntryPoint.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = kotlinxBenchmarkSupportEntryPoint.symbol,
                ).irBlockBody {
                    +jsRunBenchmarkExecutor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
                        benchmarkClasses = benchmarkClasses,
                    )
                }
            }
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == JS -> {
                kotlinxBenchmarkSupportEntryPoint.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = kotlinxBenchmarkSupportEntryPoint.symbol,
                ).irBlockBody {
                    +wasmJsRunBenchmarkExecutor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
                        benchmarkClasses = benchmarkClasses,
                    )
                }
            }
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == WASI ->  {
                kotlinxBenchmarkSupportEntryPoint.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = kotlinxBenchmarkSupportEntryPoint.symbol,
                ).irBlockBody {
                    +wasmWasiRunBenchmarkExecutor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
                        benchmarkClasses = benchmarkClasses,
                    )
                }
            }
            simplePlatform is NativePlatform ->  {
                kotlinxBenchmarkSupportEntryPoint.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = kotlinxBenchmarkSupportEntryPoint.symbol,
                ).irBlockBody {
                    +nativeRunBenchmarkExecutor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        valueParameters = kotlinxBenchmarkSupportEntryPoint.parameters,
                        benchmarkClasses = benchmarkClasses,
                    )
                }
            }
        }
        
        if (currentPhase++ == lastPhase) return
    }
}