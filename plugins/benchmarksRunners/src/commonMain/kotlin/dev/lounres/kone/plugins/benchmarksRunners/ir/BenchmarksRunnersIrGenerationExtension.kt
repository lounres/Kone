/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.ir

import dev.lounres.kone.plugins.benchmarksRunners.entryPointCallableId
import dev.lounres.kone.plugins.benchmarksRunners.entryPointFileName
import dev.lounres.kone.scope
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.impl.EmptyPackageFragmentDescriptor
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
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.impl.IrFileImpl
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.util.NaiveSourceBasedFileEntryImpl
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.addFile
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


class BenchmarksRunnersIrGenerationExtension(
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
        
        val runnerDescriptions = buildList {
            moduleFragment.acceptVoid(
                KotlinxRunnerFunctionsCollectorVisitor(
                    runnerDescriptions = this
                )
            )
        }
        
        val newFile = IrFileImpl(
            fileEntry = NaiveSourceBasedFileEntryImpl(
                name = entryPointFileName,
            ),
            packageFragmentDescriptor = EmptyPackageFragmentDescriptor(
                module = moduleFragment.descriptor,
                fqName = entryPointCallableId.packageName,
            ),
            module = moduleFragment,
        )
        moduleFragment.addFile(newFile)
        scope {
            val registrar = pluginContext.metadataDeclarationRegistrar as Fir2IrIrGeneratedDeclarationsRegistrar
            val sessionGetter = Fir2IrIrGeneratedDeclarationsRegistrar::class.java.getMethod("access\$getSession", Fir2IrIrGeneratedDeclarationsRegistrar::class.java)
            sessionGetter.isAccessible = true
            val session = sessionGetter.invoke(registrar, registrar) as FirSession
            sessionGetter.isAccessible = false
            newFile.metadata = FirMetadataSource.File(
                fir = buildFile {
                    moduleData = session.moduleData
                    origin = Key.origin
                    packageDirective = object : FirPackageDirective() {
                        override val source: KtSourceElement? get() = null
                        override val packageFqName: FqName get() = newFile.packageFqName
                        override fun <R, D> acceptChildren(visitor: FirVisitor<R, D>, data: D) {}
                        override fun <D> transformChildren(transformer: FirTransformer<D>, data: D): FirElement = this
                    }
                    name = newFile.name
                }
            )
        }
        
        val entryPointFunction: IrSimpleFunction = pluginContext.irFactory.buildFun {
            name = entryPointCallableId.callableName
            returnType = pluginContext.irBuiltIns.unitType
        }.apply {
            val argsValueParameter = addValueParameter {
                name = Name.identifier("args")
                type = pluginContext.irBuiltIns.arrayClass.createType(
                    hasQuestionMark = false,
                    arguments = listOf(
                        pluginContext.irBuiltIns.stringType,
                    ),
                )
            }
            body = entryPointBody(
                pluginContext = pluginContext,
                irRuntimeReferences = irRuntimeReferences,
                entryPointFunction = this,
                runnerDescriptions = runnerDescriptions,
            )
        }
        newFile.addChild(entryPointFunction)
        pluginContext.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(entryPointFunction)
        
        if (currentPhase++ == lastPhase) return
    }
}