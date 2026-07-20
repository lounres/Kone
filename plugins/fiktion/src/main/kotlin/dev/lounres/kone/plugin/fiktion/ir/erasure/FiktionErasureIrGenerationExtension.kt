/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion.ir.erasure

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


class FiktionErasureIrGenerationExtension(
//    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
//    typealias Phase = (
//        moduleFragment: IrModuleFragment,
//        pluginContext: IrPluginContext,
//        irRuntimeReferences: IrRuntimeReferences,
//        suppliabilityMapper: SuppliabilityMapper,
//    ) -> Unit
//
//    companion object {
//        val phases: List<Phase> = listOf(
//            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityMapper ->
//                moduleFragment.transform(
//                    SuppliedTypesStorageAccessorsTransformer(
//                        pluginContext = pluginContext,
//                        irRuntimeReferences = irRuntimeReferences,
//                        suppliabilityMapper = suppliabilityMapper,
//                    ),
//                    null
//                )
//            },
//            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityMapper ->
//                supplyFunctionsParameters(
//                    pluginContext = pluginContext,
//                    irRuntimeReferences = irRuntimeReferences,
//                    suppliabilityMapper = suppliabilityMapper,
//                )
//
//                supplyConstructorsParameters(
//                    pluginContext = pluginContext,
//                    irRuntimeReferences = irRuntimeReferences,
//                    suppliabilityMapper = suppliabilityMapper,
//                )
//            },
//            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityMapper ->
//                moduleFragment.transform(
//                    SuppliableCallSubstitutionTransformer(
//                        pluginContext = pluginContext,
//                        irRuntimeReferences = irRuntimeReferences,
//                        suppliabilityMapper = suppliabilityMapper,
//                    ),
//                    null,
//                )
//            },
//            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityMapper ->
//                supplyFunctionsBodies(
//                    pluginContext = pluginContext,
//                    irRuntimeReferences = irRuntimeReferences,
//                    suppliabilityMapper = suppliabilityMapper,
//                )
//
//                supplyConstructorsBodies(
//                    pluginContext = pluginContext,
//                    irRuntimeReferences = irRuntimeReferences,
//                    suppliabilityMapper = suppliabilityMapper,
//                )
//
//                moduleFragment.accept(
//                    SuppliableSingletonsSuppliedTypesStorageInitializerTransformer(
//                        pluginContext = pluginContext,
//                        irRuntimeReferences = irRuntimeReferences,
//                        suppliabilityMapper = suppliabilityMapper,
//                    ),
//                    null
//                )
//            },
//            { moduleFragment, pluginContext, irRuntimeReferences, suppliabilityMapper ->
//                moduleFragment.transform(
//                    SuppliedTypeOfSubstitutionTransformer(
//                        pluginContext = pluginContext,
//                        irRuntimeReferences = irRuntimeReferences,
//                        suppliabilityMapper = suppliabilityMapper,
//                    ),
//                    SuppliedTypeOfSubstitutionTransformer.TransformationContext.INIT,
//                )
//            },
//        )
//    }
    
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
//        val irRuntimeReferences: IrRuntimeReferences = IrRuntimeReferences(pluginContext)
//        val suppliabilityMapper: SuppliabilityMapper = SuppliabilityMapper(
//            pluginContext = pluginContext,
//            irRuntimeReferences = irRuntimeReferences,
//            moduleFragment = moduleFragment,
//        )
//
//        for (phase in phases) phase(moduleFragment, pluginContext, irRuntimeReferences, suppliabilityMapper)
    }
}