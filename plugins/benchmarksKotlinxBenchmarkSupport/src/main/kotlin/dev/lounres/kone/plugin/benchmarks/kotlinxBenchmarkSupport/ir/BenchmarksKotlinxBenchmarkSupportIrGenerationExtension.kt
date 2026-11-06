/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.KotlinxBenchmarkJsExecutor
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.addFile
import org.jetbrains.kotlin.konan.target.presetName
import org.jetbrains.kotlin.platform.JsPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformWithTarget
import org.jetbrains.kotlin.platform.wasm.WasmPlatformWithTarget


class BenchmarksKotlinxBenchmarkSupportIrGenerationExtension(
    private val kotlinxBenchmarkJsExecutor: KotlinxBenchmarkJsExecutor,
    private val lastPhase: UInt = MAX_VALUE,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        val platform = moduleFragment.descriptor.platform
        val simplePlatform = platform?.singleOrNull()
        when {
            simplePlatform is JvmPlatform -> {}
            simplePlatform is JsPlatform -> moduleFragment.addFile(
                jsMainFile(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    moduleFragment = moduleFragment,
                    kotlinxBenchmarkJsExecutor = kotlinxBenchmarkJsExecutor,
                    benchmarkClasses = buildList {
                        moduleFragment.accept(
                            KotlinxBenchmarkClassCollectorVisitor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                benchmarkClasses = this
                            ),
                            null
                        )
                    },
                )
            )
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == JS -> moduleFragment.addFile(
                wasmJsMainFile(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    moduleFragment = moduleFragment,
                    benchmarkClasses = buildList {
                        moduleFragment.accept(
                            KotlinxBenchmarkClassCollectorVisitor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                benchmarkClasses = this
                            ),
                            null
                        )
                    },
                )
            )
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == WASI -> moduleFragment.addFile(
                wasmWasiMainFile(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    moduleFragment = moduleFragment,
                    benchmarkClasses = buildList {
                        moduleFragment.accept(
                            KotlinxBenchmarkClassCollectorVisitor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                benchmarkClasses = this
                            ),
                            null
                        )
                    },
                )
            )
            simplePlatform is NativePlatformWithTarget -> moduleFragment.addFile(
                nativeMainFile(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    moduleFragment = moduleFragment,
                    nativeTargetPresetName = simplePlatform.target.presetName,
                    benchmarkClasses = buildList {
                        moduleFragment.accept(
                            KotlinxBenchmarkClassCollectorVisitor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                benchmarkClasses = this
                            ),
                            null
                        )
                    },
                )
            )
        }
        
        if (currentPhase++ == lastPhase) return
    }
}