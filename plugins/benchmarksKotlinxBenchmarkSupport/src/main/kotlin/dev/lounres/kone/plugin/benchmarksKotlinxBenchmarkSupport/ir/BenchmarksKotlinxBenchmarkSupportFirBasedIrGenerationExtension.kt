/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.KotlinxBenchmarkJsExecutor
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.konan.target.presetName
import org.jetbrains.kotlin.platform.JsPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformWithTarget
import org.jetbrains.kotlin.platform.wasm.WasmPlatformWithTarget
import org.jetbrains.kotlin.platform.wasm.WasmTarget.JS
import org.jetbrains.kotlin.platform.wasm.WasmTarget.WASI


class BenchmarksKotlinxBenchmarkSupportFirBasedIrGenerationExtension(
    private val kotlinxBenchmarkJsExecutor: KotlinxBenchmarkJsExecutor,
    private val lastPhase: UInt = MAX_VALUE,
) : IrGenerationExtension {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "BenchmarksKotlinxBenchmarkSupportFirBasedIrGenerationExtension.Key"
    }
    
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        var currentPhase = 0u
        
        val irRuntimeReferences = IrRuntimeReferences(pluginContext)
        
        if (currentPhase++ == lastPhase) return
        
        val platform = moduleFragment.descriptor.platform
        val simplePlatform = platform?.singleOrNull()
        when {
            simplePlatform is JvmPlatform -> {}
            simplePlatform is JsPlatform -> {
                moduleFragment.acceptVoid(
                    JsMainFunctionBodyGenerationVisitor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
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
            }
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == JS -> {
                moduleFragment.acceptVoid(
                    WasmJsMainFunctionBodyGenerationVisitor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
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
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == WASI -> {
                moduleFragment.acceptVoid(
                    WasmWasiMainFunctionBodyGenerationVisitor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
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
            simplePlatform is NativePlatformWithTarget -> {
                moduleFragment.acceptVoid(
                    NativeMainFunctionBodyGenerationVisitor(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
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
        }
        
        if (currentPhase++ == lastPhase) return
    }
}