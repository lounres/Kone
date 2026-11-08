/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.KotlinxBenchmarkJsExecutor
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirPackageDirective
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.backend.Fir2IrIrGeneratedDeclarationsRegistrar
import org.jetbrains.kotlin.fir.backend.FirMetadataSource
import org.jetbrains.kotlin.fir.declarations.builder.buildFile
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.visitors.FirTransformer
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.util.addFile
import org.jetbrains.kotlin.konan.target.presetName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.platform.JsPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformUnspecifiedTarget
import org.jetbrains.kotlin.platform.konan.NativePlatformWithTarget
import org.jetbrains.kotlin.platform.wasm.WasmPlatformWithTarget


class BenchmarksKotlinxBenchmarkSupportIrGenerationExtension(
    private val kotlinxBenchmarkJsExecutor: KotlinxBenchmarkJsExecutor,
    private val lastPhase: UInt = MAX_VALUE,
) : IrGenerationExtension {
    object Key : GeneratedDeclarationKey() {
        override fun toString(): String = "BenchmarksKotlinxBenchmarkSupportIrGenerationExtension.Key"
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
                val file = jsMainFile(
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
                moduleFragment.addFile(file)
                val registrar = pluginContext.metadataDeclarationRegistrar as Fir2IrIrGeneratedDeclarationsRegistrar
                val sessionGetter = Fir2IrIrGeneratedDeclarationsRegistrar::class.java.getMethod("access\$getSession", Fir2IrIrGeneratedDeclarationsRegistrar::class.java)
                sessionGetter.isAccessible = true
                val session = sessionGetter.invoke(registrar, registrar) as FirSession
                sessionGetter.isAccessible = false
                file.metadata = FirMetadataSource.File(
                    fir = buildFile {
                        moduleData = session.moduleData
                        origin = Key.origin
                        packageDirective = object : FirPackageDirective() {
                            override val source: KtSourceElement? get() = null
                            override val packageFqName: FqName get() = file.packageFqName
                            override fun <R, D> acceptChildren(visitor: FirVisitor<R, D>, data: D) {}
                            override fun <D> transformChildren(transformer: FirTransformer<D>, data: D): FirElement = this
                        }
                        name = file.name
                    }
                )
                pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(file.declarations.single() as IrSimpleFunction)
            }
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == JS -> {
                val file = wasmJsMainFile(
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
                moduleFragment.addFile(file)
                val registrar = pluginContext.metadataDeclarationRegistrar as Fir2IrIrGeneratedDeclarationsRegistrar
                val sessionGetter = Fir2IrIrGeneratedDeclarationsRegistrar::class.java.getMethod("access\$getSession", Fir2IrIrGeneratedDeclarationsRegistrar::class.java)
                sessionGetter.isAccessible = true
                val session = sessionGetter.invoke(registrar, registrar) as FirSession
                sessionGetter.isAccessible = false
                file.metadata = FirMetadataSource.File(
                    fir = buildFile {
                        moduleData = session.moduleData
                        origin = Key.origin
                        packageDirective = object : FirPackageDirective() {
                            override val source: KtSourceElement? get() = null
                            override val packageFqName: FqName get() = file.packageFqName
                            override fun <R, D> acceptChildren(visitor: FirVisitor<R, D>, data: D) {}
                            override fun <D> transformChildren(transformer: FirTransformer<D>, data: D): FirElement = this
                        }
                        name = file.name
                    }
                )
                pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(file.declarations.single() as IrSimpleFunction)
            }
            simplePlatform is WasmPlatformWithTarget && simplePlatform.target == WASI -> {
                val file = wasmWasiMainFile(
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
                moduleFragment.addFile(file)
                val registrar = pluginContext.metadataDeclarationRegistrar as Fir2IrIrGeneratedDeclarationsRegistrar
                val sessionGetter = Fir2IrIrGeneratedDeclarationsRegistrar::class.java.getMethod("access\$getSession", Fir2IrIrGeneratedDeclarationsRegistrar::class.java)
                sessionGetter.isAccessible = true
                val session = sessionGetter.invoke(registrar, registrar) as FirSession
                sessionGetter.isAccessible = false
                file.metadata = FirMetadataSource.File(
                    fir = buildFile {
                        moduleData = session.moduleData
                        origin = Key.origin
                        packageDirective = object : FirPackageDirective() {
                            override val source: KtSourceElement? get() = null
                            override val packageFqName: FqName get() = file.packageFqName
                            override fun <R, D> acceptChildren(visitor: FirVisitor<R, D>, data: D) {}
                            override fun <D> transformChildren(transformer: FirTransformer<D>, data: D): FirElement = this
                        }
                        name = file.name
                    }
                )
                pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(file.declarations.single() as IrSimpleFunction)
            }
            simplePlatform is NativePlatform -> {
                val file = nativeMainFile(
                    pluginContext = pluginContext,
                    irRuntimeReferences = irRuntimeReferences,
                    moduleFragment = moduleFragment,
                    nativeTargetPresetName = when (simplePlatform) {
                        NativePlatformUnspecifiedTarget -> simplePlatform.targetName
                        is NativePlatformWithTarget -> simplePlatform.target.presetName
                    },
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
                moduleFragment.addFile(file)
                val registrar = pluginContext.metadataDeclarationRegistrar as Fir2IrIrGeneratedDeclarationsRegistrar
                val sessionGetter = Fir2IrIrGeneratedDeclarationsRegistrar::class.java.getMethod("access\$getSession", Fir2IrIrGeneratedDeclarationsRegistrar::class.java)
                sessionGetter.isAccessible = true
                val session = sessionGetter.invoke(registrar, registrar) as FirSession
                sessionGetter.isAccessible = false
                file.metadata = FirMetadataSource.File(
                    fir = buildFile {
                        moduleData = session.moduleData
                        origin = Key.origin
                        packageDirective = object : FirPackageDirective() {
                            override val source: KtSourceElement? get() = null
                            override val packageFqName: FqName get() = file.packageFqName
                            override fun <R, D> acceptChildren(visitor: FirVisitor<R, D>, data: D) {}
                            override fun <D> transformChildren(transformer: FirTransformer<D>, data: D): FirElement = this
                        }
                        name = file.name
                    }
                )
                pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(file.declarations.single() as IrSimpleFunction)
            }
        }
        
        if (currentPhase++ == lastPhase) return
    }
}