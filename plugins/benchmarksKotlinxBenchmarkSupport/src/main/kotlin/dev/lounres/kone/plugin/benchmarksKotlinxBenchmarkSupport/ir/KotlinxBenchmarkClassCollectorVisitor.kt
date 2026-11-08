/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.ir

import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxBenchmarkAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxBenchmarkModeAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxMeasurementAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxOutputTimeUnitAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxParamAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxSetupAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxStateAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxTearDownAnnotationClassId
import dev.lounres.kone.plugin.benchmarksKotlinxBenchmarkSupport.kotlinxWarmupAnnotationClassId
import dev.lounres.kone.scope
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isLocal
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid


class KotlinxBenchmarkClassCollectorVisitor(
    private val pluginContext: IrPluginContext,
    private val irRuntimeReferences: IrRuntimeReferences,
    private val benchmarkClasses: MutableList<KotlinxBenchmarkClass>,
) : IrVisitorVoid() {
    override fun visitElement(element: IrElement) { element.acceptChildrenVoid(this) }
    
    override fun visitClass(declaration: IrClass) {
        if (
            !declaration.isInner
            && !declaration.isLocal
            && declaration.typeParameters.isEmpty()
            && declaration.hasAnnotation(kotlinxStateAnnotationClassId)
        ) scope {
            val constructor = declaration.constructors.firstOrNull { it.parameters.isEmpty() } ?: return@scope
            
            @Suppress("UNCHECKED_CAST")
            benchmarkClasses += KotlinxBenchmarkClass(
                theClass = declaration,
                theConstructor = constructor,
                setupMethods = declaration.functions.filter {
                    if (!it.hasAnnotation(kotlinxSetupAnnotationClassId)) return@filter false
                    val parameter = it.parameters.singleOrNull() ?: return@filter false
                    parameter.kind == DispatchReceiver && parameter.type == declaration.defaultType
                }.toList(),
                tearDownMethods = declaration.functions.filter {
                    if (!it.hasAnnotation(kotlinxTearDownAnnotationClassId)) return@filter false
                    val parameter = it.parameters.singleOrNull() ?: return@filter false
                    parameter.kind == DispatchReceiver && parameter.type == declaration.defaultType
                }.toList(),
                benchmarkMethods = declaration.functions
                    .mapNotNull { method ->
                        if (!method.hasAnnotation(kotlinxBenchmarkAnnotationClassId)) return@mapNotNull null
                        if (method.parameters.size > 2) return@mapNotNull null
                        KotlinxBenchmarkMethod(
                            theMethod = method,
                            dispatchReceiverValueParameter = method.parameters.firstOrNull()?.takeIf { it.kind == DispatchReceiver } ?: return@mapNotNull null,
                            blackholeValueParameter = method.parameters.getOrNull(1)?.also {
                                if (it.type != irRuntimeReferences.kotlinxBlackholeClassIrClassSymbol.defaultType) return@mapNotNull null
                            }
                        )
                    }.toList(),
                benchmarkModes = declaration.annotations
                    .firstOrNull { it.classId == kotlinxBenchmarkModeAnnotationClassId }
                    ?.let { annotation ->
                        val argument = annotation.arguments[0] as? IrVararg ?: return@let null
                        argument.elements.mapNotNull { varargElement ->
                            val name = (varargElement as? IrGetEnumValue)?.symbol?.owner?.name?.asString() ?: return@mapNotNull null
                            KotlinxBenchmarkMode.entries.firstOrNull { it.name == name }
                        }
                    } ?: emptyList(),
                outputTimeUnit = declaration.annotations
                    .firstOrNull { it.classId == kotlinxOutputTimeUnitAnnotationClassId }
                    ?.let { annotation ->
                        val argument = annotation.arguments[0] as? IrGetEnumValue ?: return@let null
                        val name = argument.symbol.owner.name.asString()
                        KotlinxBenchmarkTimeUnit.entries.firstOrNull { it.name == name }
                    } ?: KotlinxBenchmarkTimeUnit.SECONDS,
                warmup = declaration.annotations
                    .firstOrNull { it.classId == kotlinxWarmupAnnotationClassId }
                    ?.let { annotation ->
                        val arguments = annotation.arguments
                        KotlinxBenchmarkWarmupOrMeasurement(
                            iterations = (arguments[0] as? IrConst)?.let { (it.value as Int).toUInt() } ?: KotlinxBenchmarkWarmupOrMeasurement.default.iterations,
                            time = (arguments[1] as? IrConst)?.let { (it.value as Int).toULong() } ?: KotlinxBenchmarkWarmupOrMeasurement.default.time,
                            timeUnit = (arguments[2] as? IrGetEnumValue)?.let {
                                val name = it.symbol.owner.name.asString()
                                KotlinxBenchmarkTimeUnit.entries.firstOrNull { it.name == name }
                            } ?: KotlinxBenchmarkWarmupOrMeasurement.default.timeUnit,
                            batchSize = (arguments[3] as? IrConst)?.let { (it.value as Int).toUInt() } ?: KotlinxBenchmarkWarmupOrMeasurement.default.batchSize,
                        )
                    } ?: KotlinxBenchmarkWarmupOrMeasurement.default,
                measurement = declaration.annotations
                    .firstOrNull { it.classId == kotlinxMeasurementAnnotationClassId }
                    ?.let { annotation ->
                        val arguments = annotation.arguments
                        KotlinxBenchmarkWarmupOrMeasurement(
                            iterations = (arguments[0] as? IrConst)?.let { (it.value as Int).toUInt() } ?: KotlinxBenchmarkWarmupOrMeasurement.default.iterations,
                            time = (arguments[1] as? IrConst)?.let { (it.value as Int).toULong() } ?: KotlinxBenchmarkWarmupOrMeasurement.default.time,
                            timeUnit = (arguments[2] as? IrGetEnumValue)?.let {
                                val name = it.symbol.owner.name.asString()
                                KotlinxBenchmarkTimeUnit.entries.firstOrNull { it.name == name }
                            } ?: KotlinxBenchmarkWarmupOrMeasurement.default.timeUnit,
                            batchSize = (arguments[3] as? IrConst)?.let { (it.value as Int).toUInt() } ?: KotlinxBenchmarkWarmupOrMeasurement.default.batchSize,
                        )
                    } ?: KotlinxBenchmarkWarmupOrMeasurement.default,
                params = declaration.properties.associateWith { property ->
                    val setter = property.setter ?: return@associateWith null
                    if (setter.parameters.size != 2 || setter.parameters[0].kind != DispatchReceiver) return@associateWith null
                    val annotation = property.annotations.firstOrNull { it.classId == kotlinxParamAnnotationClassId } ?: return@associateWith null
                    KotlinxBenchmarkParamDescription(
                        setter = setter,
                        defaultValues = (annotation.arguments[0] as? IrVararg)?.elements?.mapNotNull { (it as? IrConst)?.value as? String } ?: return@associateWith null,
                        kind = when (setter.parameters[1].type) {
                            pluginContext.irBuiltIns.byteType -> KotlinxBenchmarkParamDescription.Kind.Byte
                            pluginContext.irBuiltIns.shortType -> KotlinxBenchmarkParamDescription.Kind.Short
                            pluginContext.irBuiltIns.intType -> KotlinxBenchmarkParamDescription.Kind.Int
                            pluginContext.irBuiltIns.longType -> KotlinxBenchmarkParamDescription.Kind.Long
                            pluginContext.irBuiltIns.ubyteType -> KotlinxBenchmarkParamDescription.Kind.UByte
                            pluginContext.irBuiltIns.ushortType -> KotlinxBenchmarkParamDescription.Kind.UShort
                            pluginContext.irBuiltIns.uintType -> KotlinxBenchmarkParamDescription.Kind.UInt
                            pluginContext.irBuiltIns.ulongType -> KotlinxBenchmarkParamDescription.Kind.ULong
                            pluginContext.irBuiltIns.floatType -> KotlinxBenchmarkParamDescription.Kind.Float
                            pluginContext.irBuiltIns.doubleType -> KotlinxBenchmarkParamDescription.Kind.Double
                            pluginContext.irBuiltIns.booleanType -> KotlinxBenchmarkParamDescription.Kind.Boolean
                            pluginContext.irBuiltIns.charType -> KotlinxBenchmarkParamDescription.Kind.Char
                            pluginContext.irBuiltIns.stringType -> KotlinxBenchmarkParamDescription.Kind.String
                            else -> return@associateWith null
                        }
                    )
                }.filterValues { it is KotlinxBenchmarkParamDescription } as Map<IrProperty, KotlinxBenchmarkParamDescription>
            )
        }
        
        super.visitClass(declaration)
    }
}