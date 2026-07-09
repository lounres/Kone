/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.common

import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.empty
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.computationalGeometry.angles.Angle
import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.computationalGeometry.default2.Vector2
import dev.lounres.kone.misc.canvas.KoneCanvasContextRegistry
import dev.lounres.kone.misc.canvas.KoneCanvasController
import dev.lounres.kone.misc.canvas.KoneColor
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


@Suppliable
public class KoneCanvasTargetKey<@Supply Target> : SuppliedTypeRegistryKey<KoneCanvasPrimitivesRegistry<Target>>() {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasTargetKey<${suppliedTypeOf<Target>()}>"
    
    public data object SetKey : RegistryKey<KoneSet<KoneCanvasTargetKey<*>>>
}

@JvmInline
public value class KoneCanvasPrimitivesRegistry<Target>(override val registry: OwnedProviderRegistry<KoneCanvasPrimitivesRegistry<Target>>) : ProviderRegistryWrapper<KoneCanvasPrimitivesRegistry<Target>> {
    public companion object;
    
    public fun interface Provider<Target> {
        public fun get(): KoneCanvasPrimitivesRegistry<Target>
    }
}

public fun <Target> KoneCanvasPrimitivesRegistry.Companion.empty(): KoneCanvasPrimitivesRegistry<Target> = KoneCanvasPrimitivesRegistry(OwnedProviderRegistry.empty())

public fun <Target> KoneCanvasPrimitivesRegistry.Companion.wrap(providerRegistry: ProviderRegistry): KoneCanvasPrimitivesRegistry<Target> = KoneCanvasPrimitivesRegistry(OwnedProviderRegistry.wrapFor(providerRegistry))

public inline fun <Target> KoneCanvasPrimitivesRegistry.Companion.build(block: MutableOwnedProviderRegistry<KoneCanvasPrimitivesRegistry<Target>>.() -> Unit): KoneCanvasPrimitivesRegistry<Target> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneCanvasPrimitivesRegistry(OwnedProviderRegistry.build { this.block() })
}

public inline fun <Target> KoneCanvasPrimitivesRegistry.Companion.buildWithProvider(
    block: context(KoneCanvasPrimitivesRegistry.Provider<Target>) MutableOwnedProviderRegistry<KoneCanvasPrimitivesRegistry<Target>>.() -> Unit
): KoneCanvasPrimitivesRegistry<Target> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneCanvasPrimitivesRegistry.Provider<Target> {
        var result: KoneCanvasPrimitivesRegistry<Target>? = null
        override fun get(): KoneCanvasPrimitivesRegistry<Target> =
            result ?: error("KoneCanvasContext is not yet initialized but was requested by its properties.")
    }
    val result = KoneCanvasPrimitivesRegistry(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}

@JvmInline
public value class KoneCanvasTargetsRegistry(override val registry: OwnedProviderRegistry<KoneCanvasTargetsRegistry>) : ProviderRegistryWrapper<KoneCanvasTargetsRegistry> {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneCanvasTargetsRegistry
    }
    
    public data object Key : RegistryKey<KoneCanvasTargetsRegistry> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasTargetsRegistry.Key"
    }
}

public fun KoneCanvasTargetsRegistry.Companion.empty(): KoneCanvasTargetsRegistry = KoneCanvasTargetsRegistry(OwnedProviderRegistry.empty())

public fun KoneCanvasTargetsRegistry.Companion.wrap(providerRegistry: ProviderRegistry): KoneCanvasTargetsRegistry = KoneCanvasTargetsRegistry(OwnedProviderRegistry.wrapFor(providerRegistry))

public inline fun KoneCanvasTargetsRegistry.Companion.build(block: MutableOwnedProviderRegistry<KoneCanvasTargetsRegistry>.() -> Unit): KoneCanvasTargetsRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneCanvasTargetsRegistry(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneCanvasTargetsRegistry.Companion.buildWithProvider(
    block: context(KoneCanvasTargetsRegistry.Provider) MutableOwnedProviderRegistry<KoneCanvasTargetsRegistry>.() -> Unit
): KoneCanvasTargetsRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneCanvasTargetsRegistry.Provider {
        var result: KoneCanvasTargetsRegistry? = null
        override fun get(): KoneCanvasTargetsRegistry =
            result ?: error("KoneCanvasContext is not yet initialized but was requested by its properties.")
    }
    val result = KoneCanvasTargetsRegistry(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasTargetsRegistry.Companion.setBuilt(block: MutableOwnedProviderRegistry<KoneCanvasTargetsRegistry>.() -> Unit) {
    KoneCanvasTargetsRegistry.Key correspondsTo RegisteredValueProvider.cached {
        KoneCanvasTargetsRegistry.build(block)
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasController>)
public fun KoneCanvasTargetsRegistry.Companion.setBuiltWithProvider(
    block: context(KoneCanvasTargetsRegistry.Provider) MutableOwnedProviderRegistry<KoneCanvasTargetsRegistry>.() -> Unit
) {
    KoneCanvasTargetsRegistry.Key correspondsTo RegisteredValueProvider.cached {
        KoneCanvasTargetsRegistry.buildWithProvider(block)
    }
}

@JvmInline
public value class KoneCanvasCommonContext(public val contextRegistry: KoneCanvasContextRegistry) {
    public companion object;
}

public val KoneCanvasContextRegistry.common: KoneCanvasCommonContext
    get() = KoneCanvasCommonContext(this)

public interface KoneCanvasGroup {
    public val order: Order get() = TransformationFirst
    context(controller: KoneCanvasController)
    public fun groupContext(context: KoneCanvasContextRegistry): Group
    context(controller: KoneCanvasController)
    public fun transformationContext(context: KoneCanvasContextRegistry): Transformation
    
    public companion object;
    
    public interface Transformation : KoneCanvasTransformationContext {
        public fun commit()
        
        public object IDLE : Transformation {
            override fun affine(values: MDList2<Double>) {}
            override fun translate(vector: Vector2<Double>) {}
            override fun rotate(pivot: Point2<Double>, angle: Angle) {}
            override fun scale(pivot: Point2<Double>, scaleX: Double, scaleY: Double) {}
            override fun commit() {}
        }
    }
    
    public fun interface Group {
        public fun commit()
        
        public object IDLE : Group {
            override fun commit() {}
        }
    }
    
    public enum class Order {
        TransformationFirst, GroupFirst, ;
    }
    
    public data object Key : RegistryKey<KoneCanvasGroup> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasGroup.Key"
    }
}

context(controller: KoneCanvasController)
public inline fun KoneCanvasCommonContext.group(
    transformationBlock: KoneCanvasTransformationContext.() -> Unit = { },
    groupBlock: KoneCanvasCommonContext.() -> Unit,
) {
    val primitivesRegistries = controller.getOrNull(KoneCanvasTargetsRegistry.Key) ?: return
    val keys = contextRegistry.getOrNull(KoneCanvasTargetKey.SetKey) ?: KoneSet.empty()
    @Suppress("UNCHECKED_CAST")
    val groups = keys.map { primitivesRegistries.getOrNull(it)?.getOrNull(KoneCanvasGroup.Key) }.filter { it != null } as KoneList<KoneCanvasGroup>
    val groupContexts = groups.map { group ->
        when (group.order) {
            TransformationFirst -> {
                val groupContext = group.groupContext(contextRegistry)
                group.transformationContext(contextRegistry).apply(transformationBlock).commit()
                groupContext
            }
            GroupFirst -> {
                group.groupContext(contextRegistry)
            }
        }
    }
    this.groupBlock()
    groupContexts.forEachIndexed { index, groupContext ->
        val group = groups[index]
        when (group.order) {
            TransformationFirst -> {
                groupContext.commit()
            }
            GroupFirst -> {
                group.transformationContext(contextRegistry).apply(transformationBlock).commit()
                groupContext.commit()
            }
        }
    }
}

public fun interface KoneCanvasLine {
    context(controller: KoneCanvasController)
    public fun KoneCanvasContextRegistry.line(
        start: Point2<Double>,
        end: Point2<Double>,
        strokeColor: KoneColor?,
        strokeWidth: Double,
    )
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasLine> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasLine.Key"
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasCommonContext.line(
    start: Point2<Double>,
    end: Point2<Double>,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    val primitivesRegistries = controller.getOrNull(KoneCanvasTargetsRegistry.Key) ?: return
    for (key in contextRegistry.getOrNull(KoneCanvasTargetKey.SetKey) ?: KoneSet.empty()) {
        primitivesRegistries.getOrNull(key)?.getOrNull(KoneCanvasLine.Key)?.run {
            contextRegistry.line(
                start = start,
                end = end,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
            )
        }
    }
}

public fun interface KoneCanvasRectangle {
    context(controller: KoneCanvasController)
    public fun KoneCanvasContextRegistry.rectangle(
        center: Point2<Double>,
        size: Vector2<Double>,
        rotation: Angle,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double,
    )
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasRectangle> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasRectangle.Key"
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasCommonContext.rectangle(
    center: Point2<Double>,
    size: Vector2<Double>,
    rotation: Angle = Angle.zero,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    val primitivesRegistries = controller.getOrNull(KoneCanvasTargetsRegistry.Key) ?: return
    for (key in contextRegistry.getOrNull(KoneCanvasTargetKey.SetKey) ?: KoneSet.empty()) {
        primitivesRegistries.getOrNull(key)?.getOrNull(KoneCanvasRectangle.Key)?.run {
            contextRegistry.rectangle(
                center = center,
                size = size,
                rotation = rotation,
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
            )
        }
    }
}

public fun interface KoneCanvasCircle {
    context(controller: KoneCanvasController)
    public fun KoneCanvasContextRegistry.circle(
        center: Point2<Double>,
        radius: Double,
        strokeColor: KoneColor?,
        strokeWidth: Double,
        fillColor: KoneColor?,
    )
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasCircle> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasCircle.Key"
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasCommonContext.circle(
    center: Point2<Double>,
    radius: Double,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
    fillColor: KoneColor? = null,
) {
    val primitivesRegistries = controller.getOrNull(KoneCanvasTargetsRegistry.Key) ?: return
    for (key in contextRegistry.getOrNull(KoneCanvasTargetKey.SetKey) ?: KoneSet.empty()) {
        primitivesRegistries.getOrNull(key)?.getOrNull(KoneCanvasCircle.Key)?.run {
            contextRegistry.circle(
                center = center,
                radius = radius,
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
            )
        }
    }
}

public fun interface KoneCanvasEllipse {
    context(controller: KoneCanvasController)
    public fun KoneCanvasContextRegistry.ellipse(
        center: Point2<Double>,
        size: Vector2<Double>,
        rotation: Angle,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double,
    )
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasEllipse> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasEllipse.Key"
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasCommonContext.ellipse(
    center: Point2<Double>,
    size: Vector2<Double>,
    rotation: Angle = Angle.zero,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    val primitivesRegistries = controller.getOrNull(KoneCanvasTargetsRegistry.Key) ?: return
    for (key in contextRegistry.getOrNull(KoneCanvasTargetKey.SetKey) ?: KoneSet.empty()) {
        primitivesRegistries.getOrNull(key)?.getOrNull(KoneCanvasEllipse.Key)?.run {
            contextRegistry.ellipse(
                center = center,
                size = size,
                rotation = rotation,
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
            )
        }
    }
}

public fun interface KoneCanvasPolygon {
    context(controller: KoneCanvasController)
    public fun KoneCanvasContextRegistry.polygon(
        vertices: KoneList<Point2<Double>>,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double,
    )
    
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasPolygon> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasPolygon.Key"
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasCommonContext.polygon(
    vertices: KoneList<Point2<Double>>,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    val primitivesRegistries = controller.getOrNull(KoneCanvasTargetsRegistry.Key) ?: return
    for (key in contextRegistry.getOrNull(KoneCanvasTargetKey.SetKey) ?: KoneSet.empty()) {
        primitivesRegistries.getOrNull(key)?.getOrNull(KoneCanvasPolygon.Key)?.run {
            contextRegistry.polygon(
                vertices = vertices,
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
            )
        }
    }
}

public fun interface KoneCanvasPath {
    context(controller: KoneCanvasController)
    public fun pathContext(context: KoneCanvasContextRegistry): Context
    
    public companion object;
    
    public interface Context : KoneCanvasPathContext {
        public fun commit(
            fillColor: KoneColor?,
            strokeColor: KoneColor?,
            strokeWidth: Double,
        )
        
        public object IDLE : Context {
            override fun moveTo(point: Point2<Double>) {}
            override fun moveToRelative(vector: Vector2<Double>) {}
            override fun lineTo(point: Point2<Double>) {}
            override fun lineToRelative(vector: Vector2<Double>) {}
            override fun quadraticBezierTo(point1: Point2<Double>, point2: Point2<Double>) {}
            override fun quadraticBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>) {}
            override fun cubicBezierTo(point1: Point2<Double>, point2: Point2<Double>, point3: Point2<Double>) {}
            override fun cubicBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>, vector3: Vector2<Double>) {}
            override fun arcRelative(size: Vector2<Double>, rotation: Angle, startAngle: Angle, sweepAngle: Angle) {}
            override fun close() {}
            override fun commit(fillColor: KoneColor?, strokeColor: KoneColor?, strokeWidth: Double) {}
        }
    }
    
    public data object Key : RegistryKey<KoneCanvasPath> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.common.KoneCanvasCommonPathContext.Key"
    }
}

context(controller: KoneCanvasController)
public inline fun KoneCanvasCommonContext.path(
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
    pathBuilderBlock: KoneCanvasPathContext.() -> Unit,
) {
    val primitivesRegistries = controller.getOrNull(KoneCanvasTargetsRegistry.Key) ?: return
    val keys = contextRegistry.getOrNull(KoneCanvasTargetKey.SetKey) ?: KoneSet.empty()
    @Suppress("UNCHECKED_CAST")
    val paths = keys.map { primitivesRegistries.getOrNull(it)?.getOrNull(KoneCanvasPath.Key) }.filter { it != null } as KoneList<KoneCanvasPath>
    val pathContexts = paths.map { path -> path.pathContext(contextRegistry) }.filter { it !== KoneCanvasPath.Context.IDLE }
    object : KoneCanvasPathContext {
        override fun moveTo(point: Point2<Double>) {
            pathContexts.withEach { moveTo(point) }
        }
        override fun moveToRelative(vector: Vector2<Double>) {
            pathContexts.withEach { moveToRelative(vector) }
        }
        override fun lineTo(point: Point2<Double>) {
            pathContexts.withEach { lineTo(point) }
        }
        override fun lineToRelative(vector: Vector2<Double>) {
            pathContexts.withEach { lineToRelative(vector) }
        }
        override fun quadraticBezierTo(point1: Point2<Double>, point2: Point2<Double>) {
            pathContexts.withEach { quadraticBezierTo(point1, point2) }
        }
        override fun quadraticBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>) {
            pathContexts.withEach { quadraticBezierToRelative(vector1, vector2) }
        }
        override fun cubicBezierTo(point1: Point2<Double>, point2: Point2<Double>, point3: Point2<Double>) {
            pathContexts.withEach { cubicBezierTo(point1, point2, point3) }
        }
        override fun cubicBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>, vector3: Vector2<Double>) {
            pathContexts.withEach { cubicBezierToRelative(vector1, vector2, vector3) }
        }
        override fun arcRelative(size: Vector2<Double>, rotation: Angle, startAngle: Angle, sweepAngle: Angle) {
            pathContexts.withEach { arcRelative(size, rotation, startAngle, sweepAngle) }
        }
        override fun close() {
            pathContexts.withEach { close() }
        }
    }.pathBuilderBlock()
    pathContexts.forEach {
        it.commit(
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}