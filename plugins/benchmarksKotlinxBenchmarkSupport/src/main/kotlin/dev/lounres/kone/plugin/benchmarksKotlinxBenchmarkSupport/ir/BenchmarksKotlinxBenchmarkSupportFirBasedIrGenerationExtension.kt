/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.KotlinxBenchmarkJsExecutor
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.konan.target.presetName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.JsPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformUnspecifiedTarget
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
                val mainFunction = moduleFragment
                    .files
                    .filter { it.packageFqName == FqName("dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.generated") }
                    .flatMap { it.declarations }
                    .filterIsInstance<IrSimpleFunction>()
                    .filter {
                        it.name == Name.identifier("main") && it.parameters.size == 1
                    }
                    .single()
                mainFunction.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = mainFunction.symbol,
                ).irBlockBody {
                    +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
                        arguments[0] = irString("Hello, IR-generated benchmarks!")
                    }
                    val argsValueParameter = mainFunction.parameters.single()
                    val benchmarkClasses = buildList {
                        moduleFragment.accept(
                            KotlinxBenchmarkClassCollectorVisitor(
                                pluginContext = pluginContext,
                                irRuntimeReferences = irRuntimeReferences,
                                benchmarkClasses = this
                            ),
                            null
                        )
                    }
                    when (kotlinxBenchmarkJsExecutor) {
                        BenchmarkJs -> +jsRunBenchmarksViaBenchmarkJs(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            argsValueParameter = argsValueParameter,
                            benchmarkClasses = benchmarkClasses,
                        )
                        BuiltIn -> +jsRunBenchmarksViaBuiltIn(
                            pluginContext = pluginContext,
                            irRuntimeReferences = irRuntimeReferences,
                            argsValueParameter = argsValueParameter,
                            benchmarkClasses = benchmarkClasses,
                        )
                    }
                }
            }
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == JS -> {
                val mainFunction = moduleFragment
                    .files
                    .filter { it.packageFqName == FqName("dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.generated") }
                    .flatMap { it.declarations }
                    .filterIsInstance<IrSimpleFunction>()
                    .filter {
                        it.name == Name.identifier("main") && it.parameters.size == 1
                    }
                    .single()
                mainFunction.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = mainFunction.symbol,
                ).irBlockBody {
                    +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
                        arguments[0] = irString("Hello, IR-generated benchmarks!")
                    }
                    val argsValueParameter = mainFunction.parameters.single()
                    +wasmJsRunBenchmarks(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        argsValueParameter = argsValueParameter,
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
                }
            }
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == WASI -> {
                val mainFunction = moduleFragment
                    .files
                    .filter { it.packageFqName == FqName("dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.generated") }
                    .flatMap { it.declarations }
                    .filterIsInstance<IrSimpleFunction>()
                    .filter {
                        it.name == Name.identifier("main") && it.parameters.size == 1
                    }
                    .single()
                mainFunction.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = mainFunction.symbol,
                ).irBlockBody {
                    +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
                        arguments[0] = irString("Hello, IR-generated benchmarks!")
                    }
                    val argsValueParameter = mainFunction.parameters.single()
                    +wasmWasiRunBenchmarks(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        argsValueParameter = argsValueParameter,
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
                }
            }
            simplePlatform is NativePlatform -> {
                val mainFunction = moduleFragment
                    .files
                    .filter { it.packageFqName == FqName("dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.generated") }
                    .flatMap { it.declarations }
                    .filterIsInstance<IrSimpleFunction>()
                    .filter {
                        it.name == Name.identifier("main") && it.parameters.size == 1
                    }
                    .single()
                mainFunction.body = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = mainFunction.symbol,
                ).irBlockBody {
                    +irCall(irRuntimeReferences.printlnFunctionIrSimpleFunctionSymbol).apply {
                        arguments[0] = irString("Hello, IR-generated benchmarks!")
                    }
                    val argsValueParameter = mainFunction.parameters.single()
                    +nativeRunBenchmarks(
                        pluginContext = pluginContext,
                        irRuntimeReferences = irRuntimeReferences,
                        nativeTargetPresetName = when (simplePlatform) {
                            NativePlatformUnspecifiedTarget -> simplePlatform.targetName
                            is NativePlatformWithTarget -> simplePlatform.target.presetName
                        },
                        argsValueParameter = argsValueParameter,
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
                }
            }
        }
        
        if (currentPhase++ == lastPhase) return
    }
}