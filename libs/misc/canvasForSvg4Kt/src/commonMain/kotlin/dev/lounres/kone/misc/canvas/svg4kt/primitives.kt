/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.svg4kt

import dev.jamesyox.svg4kt.TagConsumer
import dev.jamesyox.svg4kt.attr.AttributeConsumer
import dev.jamesyox.svg4kt.attr.attrs.cx
import dev.jamesyox.svg4kt.attr.attrs.cy
import dev.jamesyox.svg4kt.attr.attrs.r
import dev.jamesyox.svg4kt.attr.attrs.stroke
import dev.jamesyox.svg4kt.attr.set
import dev.jamesyox.svg4kt.attr.types.obj.Length
import dev.jamesyox.svg4kt.attr.types.obj.SvgColor
import dev.jamesyox.svg4kt.tags.*
import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.joinToString
import dev.lounres.kone.computationalGeometry.angles.*
import dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverField
import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.computationalGeometry.default2.Vector2
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.misc.canvas.*
import dev.lounres.kone.misc.canvas.common.*
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.columnsView
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrNull
import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.abs


public val KoneColor.svg4kt: SvgColor get() = SvgColor.Hex.RGBA(toRgbaUInt().toLong())

@JvmInline
public value class KoneCanvasSvg4ktContext(public val contextRegistry: KoneCanvasContextRegistry) {
    public companion object;
}

public val KoneCanvasCommonContext.svg4kt: KoneCanvasSvg4ktContext
    get() = KoneCanvasSvg4ktContext(this.contextRegistry)

// TODO: A bug in supplied types compiler plugin
//public typealias KoneCanvasSvg4ktTargetKey = KoneCanvasTargetKey<KoneCanvasSvg4ktContext>

public data object KoneCanvasSvg4ktTagConsumerKey : RegistryKey<TagConsumer<*>> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.svg4kt.KoneCanvasSvg4ktTagConsumerKey"
}

@PublishedApi
internal class KoneCanvasSvg4ktTransformationContext(private val context: AttributeConsumer) : KoneCanvasTransformationContext, KoneCanvasGroup.Transformation {
    private val result = KoneMutableList.of<String>()
    
    override fun affine(values: MDList2<Double>) {
        require(values.rowNumber == 2u && values.columnNumber == 3u)
        result.add("matrix(${values.columnsView.joinToString(separator = " ") { it.joinToString(separator = " ") }})")
    }
    
    override fun translate(vector: Vector2<Double>) {
        result.add("translate(${vector.x} ${vector.y})")
    }
    
    override fun rotate(pivot: Point2<Double>, angle: Angle) {
        result.add("rotate(${angle.inDegrees()} ${pivot.x} ${pivot.y})")
    }
    
    override fun scale(pivot: Point2<Double>, scaleX: Double, scaleY: Double) {
        TODO()
    }
    
    override fun commit() {
        context["transform"] = result.joinToString(" ")
    }
}

public inline fun KoneCanvasSvg4ktContext.group(
    transformation: KoneCanvasTransformationContext.() -> Unit,
    groupBody: KoneCanvasSvg4ktContext.() -> Unit,
) {
    contextRegistry.getOrNull(KoneCanvasSvg4ktTagConsumerKey)?.let { tagConsumer ->
        tagConsumer.onTagStart(G)
        KoneCanvasSvg4ktTransformationContext(tagConsumer.attributeConsumer).apply(transformation).commit()
        groupBody()
        tagConsumer.onTagEnd(G)
    }
}

public object KoneCanvasSvg4ktGroup : KoneCanvasGroup {
    context(controller: KoneCanvasController)
    override fun transformationContext(context: KoneCanvasContextRegistry): KoneCanvasGroup.Transformation {
        val tagConsumer = context.getOrNull(KoneCanvasSvg4ktTagConsumerKey) ?: return KoneCanvasGroup.Transformation.IDLE
        return KoneCanvasSvg4ktTransformationContext(tagConsumer.attributeConsumer)
    }
    
    context(controller: KoneCanvasController)
    override fun groupContext(context: KoneCanvasContextRegistry): KoneCanvasGroup.Group {
        val tagConsumer = context.getOrNull(KoneCanvasSvg4ktTagConsumerKey) ?: return KoneCanvasGroup.Group.IDLE
        tagConsumer.onTagStart(G)
        return KoneCanvasGroup.Group { tagConsumer.onTagEnd(G) }
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasSvg4ktContext.line(
    start: Point2<Double>,
    end: Point2<Double>,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    contextRegistry.getOrNull(KoneCanvasSvg4ktTagConsumerKey)?.apply {
        val euclideanSpace = controller.getOrNull(KoneContextRegistry.Key)?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        euclideanSpace {
            val data = contextRegistry.getOrNull(KoneCanvasData.Key)
            val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
            val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 0.0
            val start = (start - shift) * zoom
            val end = (end - shift) * zoom
            if (strokeColor != null) context(G) {
                line {
                    contextOf<AttributeConsumer>()["x1"] = Length.None(start.x)
                    contextOf<AttributeConsumer>()["y1"] = Length.None(start.y)
                    contextOf<AttributeConsumer>()["x2"] = Length.None(end.x)
                    contextOf<AttributeConsumer>()["y2"] = Length.None(end.y)
                    stroke(strokeColor.svg4kt)
                    contextOf<AttributeConsumer>()["stroke-width"] = Length.None(strokeWidth)
                }
            }
        }
    }
}

public object KoneCanvasSvg4ktLine : KoneCanvasLine {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.line(
        start: Point2<Double>,
        end: Point2<Double>,
        strokeColor: KoneColor?,
        strokeWidth: Double
    ) {
        KoneCanvasSvg4ktContext(this).line(
            start = start,
            end = end,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasSvg4ktContext.rectangle(
    center: Point2<Double>,
    size: Vector2<Double>,
    rotation: Angle = Angle.zero,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    contextRegistry.getOrNull(KoneCanvasSvg4ktTagConsumerKey)?.apply {
        val providedKoneContextRegistry = controller.getOrNull(KoneContextRegistry.Key)
        val field = providedKoneContextRegistry?.getOrNull(Field.Key<Double>())
            ?: defaultKoneContextRegistry[Field.Key<Double>()]
        val euclideanSpace = providedKoneContextRegistry?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        context(field, euclideanSpace, G) {
            val data = contextRegistry.getOrNull(KoneCanvasData.Key)
            val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
            val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 0.0
            val center = (center - shift) * zoom
            val size = size * zoom
            val topLeftVector = center - size / 2
            val _ = rect {
//                contextOf<AttributeConsumer>()["transform"] = "matrix()" // TODO: Add rotation
                contextOf<AttributeConsumer>()["x"] = Length.None(topLeftVector.x)
                contextOf<AttributeConsumer>()["y"] = Length.None(topLeftVector.y)
                contextOf<AttributeConsumer>()["width"] = Length.None(size.x)
                contextOf<AttributeConsumer>()["height"] = Length.None(size.y)
                
                if (fillColor != null)
                    contextOf<AttributeConsumer>()["fill"] = fillColor.svg4kt
                if (strokeColor != null) {
                    stroke(strokeColor.svg4kt)
                    contextOf<AttributeConsumer>()["stroke-width"] = Length.None(strokeWidth)
                }
            }
        }
    }
}

public object KoneCanvasSvg4ktRectangle : KoneCanvasRectangle {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.rectangle(
        center: Point2<Double>,
        size: Vector2<Double>,
        rotation: Angle,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double
    ) {
        KoneCanvasSvg4ktContext(this).rectangle(
            center = center,
            size = size,
            rotation = rotation,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasSvg4ktContext.circle(
    center: Point2<Double>,
    radius: Double,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
    fillColor: KoneColor? = null,
) {
    contextRegistry.getOrNull(KoneCanvasSvg4ktTagConsumerKey)?.apply {
        val providedKoneContextRegistry = controller.getOrNull(KoneContextRegistry.Key)
        val field = providedKoneContextRegistry?.getOrNull(Field.Key<Double>())
            ?: defaultKoneContextRegistry[Field.Key<Double>()]
        val euclideanSpace = providedKoneContextRegistry?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        val data = contextRegistry.getOrNull(KoneCanvasData.Key)
        context(field, euclideanSpace, G) {
            val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
            val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 0.0
            val center = (center - shift) * zoom
            val radius = radius * zoom
            circle {
                cx = Length.None(center.x)
                cy = Length.None(center.y)
                r = Length.None(radius)
                
                if (fillColor != null)
                    contextOf<AttributeConsumer>()["fill"] = fillColor.svg4kt
                if (strokeColor != null) {
                    stroke(strokeColor.svg4kt)
                    contextOf<AttributeConsumer>()["stroke-width"] = Length.None(strokeWidth)
                }
            }
        }
    }
}

public object KoneCanvasSvg4ktCircle : KoneCanvasCircle {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.circle(
        center: Point2<Double>,
        radius: Double,
        strokeColor: KoneColor?,
        strokeWidth: Double,
        fillColor: KoneColor?
    ) {
        KoneCanvasSvg4ktContext(this).circle(
            center = center,
            radius = radius,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

public fun KoneCanvasSvg4ktContext.ellipse(
    center: Point2<Double>,
    size: Vector2<Double>,
    rotation: Angle = Angle.zero,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    contextRegistry.getOrNull(KoneCanvasSvg4ktTagConsumerKey)?.apply {
        val providedKoneContextRegistry = contextRegistry.getOrNull(KoneContextRegistry.Key)
        val field = providedKoneContextRegistry?.getOrNull(Field.Key<Double>())
            ?: defaultKoneContextRegistry[Field.Key<Double>()]
        val euclideanSpace = providedKoneContextRegistry?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        context(field, euclideanSpace, G) {
            val data = contextRegistry.getOrNull(KoneCanvasData.Key)
            val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
            val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 0.0
            val center = (center - shift) * zoom
            val size = size * zoom
            val _ = ellipse {
//                contextOf<AttributeConsumer>()["transform"] = "matrix()" // TODO: Add rotation
                cx = Length.None(center.x)
                cy = Length.None(center.y)
                contextOf<AttributeConsumer>()["rx"] = Length.None(size.x)
                contextOf<AttributeConsumer>()["ry"] = Length.None(size.x)
                
                if (fillColor != null)
                    contextOf<AttributeConsumer>()["fill"] = fillColor.svg4kt
                if (strokeColor != null) {
                    stroke(strokeColor.svg4kt)
                    contextOf<AttributeConsumer>()["stroke-width"] = Length.None(strokeWidth)
                }
            }
        }
    }
}

public object KoneCanvasSvg4ktEllipse : KoneCanvasEllipse {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.ellipse(
        center: Point2<Double>,
        size: Vector2<Double>,
        rotation: Angle,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double
    ) {
        KoneCanvasSvg4ktContext(this).ellipse(
            center = center,
            size = size,
            rotation = rotation,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

@PublishedApi
internal class KoneCanvasSvg4ktPathContext(
    private val context: KoneMutableList<String>,
    private val euclideanSpace: EuclideanSpace2OverField<Double>,
    private val shift: Point2<Double>,
    private val zoom: Double,
) : KoneCanvasPathContext {
    override fun moveTo(point: Point2<Double>) {
        euclideanSpace {
            val point = (point - shift) * zoom
            context.add("M ${point.x} ${point.y}")
        }
    }
    override fun moveToRelative(vector: Vector2<Double>) {
        euclideanSpace {
            val vector = vector * zoom
            context.add("m ${vector.x} ${vector.y}")
        }
    }
    override fun lineTo(point: Point2<Double>) {
        euclideanSpace {
            val point = (point - shift) * zoom
            context.add("L ${point.x} ${point.y}")
        }
    }
    override fun lineToRelative(vector: Vector2<Double>) {
        euclideanSpace {
            val vector = vector * zoom
            context.add("l ${vector.x} ${vector.y}")
        }
    }
    override fun quadraticBezierTo(point1: Point2<Double>, point2: Point2<Double>) {
        euclideanSpace {
            val point1 = (point1 - shift) * zoom
            val point2 = (point2 - shift) * zoom
            context.add("Q ${point1.x} ${point1.y} ${point2.x} ${point2.y}")
        }
    }
    override fun quadraticBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>) {
        euclideanSpace {
            val vector1 = vector1 * zoom
            val vector2 = vector2 * zoom
            context.add("q ${vector1.x} ${vector1.y} ${vector2.x} ${vector2.y}")
        }
    }
    override fun cubicBezierTo(point1: Point2<Double>, point2: Point2<Double>, point3: Point2<Double>) {
        euclideanSpace {
            val point1 = (point1 - shift) * zoom
            val point2 = (point2 - shift) * zoom
            val point3 = (point3 - shift) * zoom
            context.add("C ${point1.x} ${point1.y} ${point2.x} ${point2.y} ${point3.x} ${point3.y}")
        }
    }
    override fun cubicBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>, vector3: Vector2<Double>) {
        euclideanSpace {
            val vector1 = vector1 * zoom
            val vector2 = vector2 * zoom
            val vector3 = vector3 * zoom
            context.add("c ${vector1.x} ${vector1.y} ${vector2.x} ${vector2.y} ${vector3.x} ${vector3.y}")
        }
    }
    override fun arcRelative(size: Vector2<Double>, rotation: Angle, startAngle: Angle, sweepAngle: Angle) {
        euclideanSpace {
            val size = size * zoom
            val isMoreThanHalf = abs(sweepAngle.inRadians()) >= PI
            val isPositiveArc = sweepAngle.inRadians() > 0.0
            // TODO: It's incorrect!!! 'size' and 'rotation' are not took into account in 'startVector' and 'endVector'!
            val startVector = Vector2(sin(startAngle), cos(startAngle))
            val endVector = Vector2(sin(startAngle + sweepAngle), cos(startAngle + sweepAngle))
            val finalPoint = endVector - startVector
            context.add("a ${size.x} ${size.y} ${rotation.inDegrees()} ${if (isMoreThanHalf) 1 else 0} ${if (isPositiveArc) 1 else 0} ${finalPoint.x} ${finalPoint.y}")
        }
    }
    override fun close() {
        context.add("Z")
    }
}

context(controller: KoneCanvasController)
public inline fun KoneCanvasSvg4ktContext.path(
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
    pathBuilderBlock: KoneCanvasPathContext.() -> Unit,
) {
    val tagConsumer = contextRegistry.getOrNull(KoneCanvasSvg4ktTagConsumerKey) ?: return
    val euclideanSpace = controller.getOrNull(KoneContextRegistry.Key)?.getOrNull(EuclideanSpace2OverField.Key<Double>())
        ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
    val data = contextRegistry.getOrNull(KoneCanvasData.Key)
    val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
    val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 0.0
    val pathBuilder = KoneMutableList.of<String>()
    KoneCanvasSvg4ktPathContext(
        context = pathBuilder,
        euclideanSpace = euclideanSpace,
        shift = shift,
        zoom = zoom,
    ).apply(pathBuilderBlock)
    tagConsumer.onTagStart(Path)
    tagConsumer.attributeConsumer["d"] = pathBuilder.joinToString(separator = " ")
    if (fillColor != null)
        tagConsumer.attributeConsumer["fill"] = fillColor.svg4kt
    if (strokeColor != null) {
        tagConsumer.attributeConsumer["stroke"] = strokeColor.svg4kt
        tagConsumer.attributeConsumer["stroke-width"] = Length.None(strokeWidth)
    }
    tagConsumer.onTagEnd(Path)
}

public object KoneCanvasSvg4ktPath : KoneCanvasPath {
    private class PathContextDelegate(
        private val tagConsumer: TagConsumer<*>,
        private val pathBuilder: KoneList<String>,
        private val koneCanvasPathContext: KoneCanvasPathContext,
    ) : KoneCanvasPath.Context, KoneCanvasPathContext by koneCanvasPathContext {
        override fun commit(fillColor: KoneColor?, strokeColor: KoneColor?, strokeWidth: Double) {
            tagConsumer.onTagStart(Path)
            tagConsumer.attributeConsumer["d"] = pathBuilder.joinToString(separator = " ")
            if (fillColor != null)
                tagConsumer.attributeConsumer["fill"] = fillColor.svg4kt
            if (strokeColor != null) {
                tagConsumer.attributeConsumer["stroke"] = strokeColor.svg4kt
                tagConsumer.attributeConsumer["stroke-width"] = Length.None(strokeWidth)
            }
            tagConsumer.onTagEnd(Path)
        }
    }
    
    context(controller: KoneCanvasController)
    override fun pathContext(context: KoneCanvasContextRegistry): KoneCanvasPath.Context {
        val tagConsumer = context.getOrNull(KoneCanvasSvg4ktTagConsumerKey) ?: return KoneCanvasPath.Context.IDLE
        val euclideanSpace = controller.getOrNull(KoneContextRegistry.Key)?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        val data = context.getOrNull(KoneCanvasData.Key)
        val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
        val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 0.0
        val pathBuilder = KoneMutableList.of<String>()
        return PathContextDelegate(
            tagConsumer = tagConsumer,
            pathBuilder = pathBuilder,
            koneCanvasPathContext = KoneCanvasSvg4ktPathContext(
                context = pathBuilder,
                euclideanSpace = euclideanSpace,
                shift = shift,
                zoom = zoom,
            )
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasPrimitivesRegistry<KoneCanvasSvg4ktContext>>)
public fun setDefaultPrimitives() {
    KoneCanvasGroup.Key correspondsTo KoneCanvasSvg4ktGroup
    KoneCanvasLine.Key correspondsTo KoneCanvasSvg4ktLine
    KoneCanvasRectangle.Key correspondsTo KoneCanvasSvg4ktRectangle
    KoneCanvasCircle.Key correspondsTo KoneCanvasSvg4ktCircle
    KoneCanvasEllipse.Key correspondsTo KoneCanvasSvg4ktEllipse
    KoneCanvasPath.Key correspondsTo KoneCanvasSvg4ktPath
}

context(_: MutableOwnedProviderRegistry<KoneCanvasTargetsRegistry>)
public fun KoneCanvasSvg4ktContext.Companion.setDefaultPrimitivesRegistry() {
    KoneCanvasTargetKey<KoneCanvasSvg4ktContext>() correspondsTo KoneCanvasPrimitivesRegistry.build {
        setDefaultPrimitives()
    }
}