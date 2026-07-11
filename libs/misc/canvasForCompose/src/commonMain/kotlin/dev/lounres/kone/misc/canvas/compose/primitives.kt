/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.toPath
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.computationalGeometry.angles.*
import dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverField
import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.computationalGeometry.default2.Vector2
import dev.lounres.kone.contexts.KoneContextHolder
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.unwrapLocallyAsExtensionReceivers
import dev.lounres.kone.misc.canvas.*
import dev.lounres.kone.misc.canvas.common.*
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.getOrNull
import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.abs


public val KoneColor.composeMultiplatform: Color get() = Color(toArgbUInt().toInt())
public val Color.kone: KoneColor get() = KoneColor.fromArgbUInt(toArgb().toUInt())

@JvmInline
public value class KoneCanvasComposeMultiplatformContext(public val contextRegistry: KoneCanvasContextRegistry) {
    public companion object;
}

public val KoneCanvasCommonContext.composeMultiplatform: KoneCanvasComposeMultiplatformContext
    get() = KoneCanvasComposeMultiplatformContext(this.contextRegistry)

// TODO: A bug in supplied types compiler plugin
//public typealias KoneCanvasComposeMultiplatformTargetKey = KoneCanvasTargetKey<KoneCanvasComposeMultiplatformContext>

public data class KoneCanvasComposeMultiplatformStack(val drawScope: DrawScope, val sizes: KoneMutableList<Size>) {
    public companion object;
    
    public data object Key : RegistryKey<KoneCanvasComposeMultiplatformStack> {
        override fun toString(): String = "dev.lounres.kone.misc.canvas.compose.KoneCanvasComposeMultiplatformStack"
    }
}

@JvmInline
@PublishedApi
internal value class KoneCanvasComposeMultiplatformTransformationContext(private val context: DrawTransform) : KoneCanvasTransformationContext, KoneCanvasGroup.Transformation {
    override fun affine(values: MDList2<Double>) {
        require(values.rowNumber == 2u && values.columnNumber == 3u)
        context.transform(
            androidx.compose.ui.graphics.Matrix(
                floatArrayOf(
                    values[0u, 0u].toFloat(), values[1u, 0u].toFloat(), 0f, 0f,
                    values[0u, 1u].toFloat(), values[1u, 1u].toFloat(), 0f, 0f,
                    0f, 0f, 1f, 0f,
                    values[0u, 2u].toFloat(), values[1u, 2u].toFloat(), 0f, 0f,
                )
            )
        )
    }
    
    override fun translate(vector: Vector2<Double>) {
        context.translate(
            left = vector.x.toFloat(),
            top = vector.y.toFloat(),
        )
    }
    
    override fun rotate(pivot: Point2<Double>, angle: Angle) {
        context.rotateRad(
            radians = angle.inRadians().toFloat(),
            pivot = pivot.let { Offset(it.x.toFloat(), it.y.toFloat()) },
        )
    }
    
    override fun scale(pivot: Point2<Double>, scaleX: Double, scaleY: Double) {
        context.scale(
            scaleX = scaleX.toFloat(),
            scaleY = scaleY.toFloat(),
            pivot = pivot.let { Offset(it.x.toFloat(), it.y.toFloat()) },
        )
    }
    
    override fun commit() {}
}

public inline fun KoneCanvasComposeMultiplatformContext.group(
    transformation: KoneCanvasTransformationContext.() -> Unit,
    groupBody: KoneCanvasComposeMultiplatformContext.() -> Unit,
) {
    contextRegistry.getOrNull(KoneCanvasComposeMultiplatformStack.Key)?.let { stack ->
        with(stack.drawScope) {
            stack.sizes.add(drawContext.size)
            drawContext.canvas.save()
            KoneCanvasComposeMultiplatformTransformationContext(drawContext.transform).transformation()
            this@group.groupBody()
            drawContext.canvas.restore()
            drawContext.size = stack.sizes.last()
            stack.sizes.removeAt(stack.sizes.lastIndex)
        }
    }
}

public object KoneCanvasComposeMultiplatformGroup : KoneCanvasGroup {
    context(controller: KoneCanvasController)
    override fun transformationContext(context: KoneCanvasContextRegistry): KoneCanvasGroup.Transformation {
        val stack = context.getOrNull(KoneCanvasComposeMultiplatformStack.Key) ?: return KoneCanvasGroup.Transformation.IDLE
        return KoneCanvasComposeMultiplatformTransformationContext(stack.drawScope.drawContext.transform)
    }
    
    context(controller: KoneCanvasController)
    override fun groupContext(context: KoneCanvasContextRegistry): KoneCanvasGroup.Group {
        val stack = context.getOrNull(KoneCanvasComposeMultiplatformStack.Key) ?: return KoneCanvasGroup.Group.IDLE
        stack.sizes.add(stack.drawScope.drawContext.size)
        stack.drawScope.drawContext.canvas.save()
        return KoneCanvasGroup.Group {
            stack.drawScope.drawContext.canvas.restore()
            stack.drawScope.drawContext.size = stack.sizes.last()
            stack.sizes.removeAt(stack.sizes.lastIndex)
        }
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasComposeMultiplatformContext.line(
    start: Point2<Double>,
    end: Point2<Double>,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    contextRegistry.getOrNull(KoneCanvasComposeMultiplatformStack.Key)?.let { stack ->
        val euclideanSpace = controller.getOrNull(KoneContextRegistry.Key)?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val data = contextRegistry.getOrNull(KoneCanvasData.Key)
        val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
        val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 1.0
        val start = (start - shift) * zoom
        val end = (end - shift) * zoom
        if (strokeColor != null) stack.drawScope.drawLine(
            color = strokeColor.composeMultiplatform,
            start = Offset(start.x.toFloat(), start.y.toFloat()),
            end = Offset(end.x.toFloat(), end.y.toFloat()),
            strokeWidth = strokeWidth.toFloat(),
        )
    }
}

public object KoneCanvasComposeMultiplatformLine : KoneCanvasLine {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.line(
        start: Point2<Double>,
        end: Point2<Double>,
        strokeColor: KoneColor?,
        strokeWidth: Double
    ) {
        KoneCanvasComposeMultiplatformContext(this).line(
            start = start,
            end = end,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasComposeMultiplatformContext.rectangle(
    center: Point2<Double>,
    size: Vector2<Double>,
    rotation: Angle = Angle.zero,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    contextRegistry.getOrNull(KoneCanvasComposeMultiplatformStack.Key)?.let { stack ->
        val providedKoneContextRegistry = controller.getOrNull(KoneContextRegistry.Key)
        val euclideanSpace = providedKoneContextRegistry?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val data = contextRegistry.getOrNull(KoneCanvasData.Key)
        val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
        val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 1.0
        val center = (center - shift) * zoom
        val size = size * zoom
        val topLeftVector = center - size / 2
        stack.drawScope.withTransform(
            {
                rotate(
                    degrees = rotation.inDegrees().toFloat(),
                    pivot = Offset(center.x.toFloat(), center.y.toFloat())
                )
            }
        ) {
            if (fillColor != null) drawRect(
                color = fillColor.composeMultiplatform,
                topLeft = Offset(topLeftVector.x.toFloat(), topLeftVector.y.toFloat()),
                size = Size(size.x.toFloat(), size.y.toFloat()),
                style = Fill,
            )
            if (strokeColor != null) drawRect(
                color = strokeColor.composeMultiplatform,
                topLeft = Offset(topLeftVector.x.toFloat(), topLeftVector.y.toFloat()),
                size = Size(size.x.toFloat(), size.y.toFloat()),
                style = Stroke(width = strokeWidth.toFloat()),
            )
        }
    }
}

public object KoneCanvasComposeMultiplatformRectangle : KoneCanvasRectangle {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.rectangle(
        center: Point2<Double>,
        size: Vector2<Double>,
        rotation: Angle,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double
    ) {
        KoneCanvasComposeMultiplatformContext(this).rectangle(
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
public fun KoneCanvasComposeMultiplatformContext.circle(
    center: Point2<Double>,
    radius: Double,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
    fillColor: KoneColor? = null,
) {
    contextRegistry.getOrNull(KoneCanvasComposeMultiplatformStack.Key)?.let { stack ->
        val providedKoneContextRegistry = controller.getOrNull(KoneContextRegistry.Key)
        val euclideanSpace = providedKoneContextRegistry?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        val data = contextRegistry.getOrNull(KoneCanvasData.Key)
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
        val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 1.0
        val center = (center - shift) * zoom
        val radius = radius * zoom
        if (fillColor != null) stack.drawScope.drawCircle(
            color = fillColor.composeMultiplatform,
            radius = radius.toFloat(),
            center = Offset(center.x.toFloat(), center.y.toFloat()),
            style = Fill,
        )
        if (strokeColor != null) stack.drawScope.drawCircle(
            color = strokeColor.composeMultiplatform,
            radius = radius.toFloat(),
            center = Offset(center.x.toFloat(), center.y.toFloat()),
            style = Stroke(width = strokeWidth.toFloat()),
        )
    }
}

public object KoneCanvasComposeMultiplatformCircle : KoneCanvasCircle {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.circle(
        center: Point2<Double>,
        radius: Double,
        strokeColor: KoneColor?,
        strokeWidth: Double,
        fillColor: KoneColor?
    ) {
        KoneCanvasComposeMultiplatformContext(this).circle(
            center = center,
            radius = radius,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

context(controller: KoneCanvasController)
public fun KoneCanvasComposeMultiplatformContext.ellipse(
    center: Point2<Double>,
    size: Vector2<Double>,
    rotation: Angle = Angle.zero,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    contextRegistry.getOrNull(KoneCanvasComposeMultiplatformStack.Key)?.let { stack ->
        val providedKoneContextRegistry = controller.getOrNull(KoneContextRegistry.Key)
        val euclideanSpace = providedKoneContextRegistry?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val data = contextRegistry.getOrNull(KoneCanvasData.Key)
        val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
        val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 1.0
        val center = (center - shift) * zoom
        val size = size * zoom
        val topLeftVector = center - size / 2
        stack.drawScope.withTransform(
            {
                rotate(
                    degrees = rotation.inDegrees().toFloat(),
                    pivot = Offset(center.x.toFloat(), center.y.toFloat())
                )
            }
        ) {
            if (fillColor != null) drawOval(
                color = fillColor.composeMultiplatform,
                topLeft = Offset(topLeftVector.x.toFloat(), topLeftVector.y.toFloat()),
                size = Size(size.x.toFloat(), size.y.toFloat()),
                style = Fill,
            )
            if (strokeColor != null) drawOval(
                color = strokeColor.composeMultiplatform,
                topLeft = Offset(topLeftVector.x.toFloat(), topLeftVector.y.toFloat()),
                size = Size(size.x.toFloat(), size.y.toFloat()),
                style = Stroke(width = strokeWidth.toFloat()),
            )
        }
    }
}

public object KoneCanvasComposeMultiplatformEllipse : KoneCanvasEllipse {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.ellipse(
        center: Point2<Double>,
        size: Vector2<Double>,
        rotation: Angle,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double
    ) {
        KoneCanvasComposeMultiplatformContext(this).ellipse(
            center = center,
            size = size,
            rotation = rotation,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

// TODO: Arc

context(controller: KoneCanvasController)
public fun KoneCanvasComposeMultiplatformContext.polygon(
    vertices: KoneList<Point2<Double>>,
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
) {
    contextRegistry.getOrNull(KoneCanvasComposeMultiplatformStack.Key)?.let { stack ->
        val providedKoneContextRegistry = controller.getOrNull(KoneContextRegistry.Key)
        val euclideanSpace = providedKoneContextRegistry?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val data = contextRegistry.getOrNull(KoneCanvasData.Key)
        val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
        val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 1.0
        val vertices = vertices.map { (it - shift) * zoom }
        val path = Path().apply {
            if (vertices.isNotEmpty()) {
                vertices.first().also { moveTo(it.x.toFloat(), it.y.toFloat()) }
                for (i in 1u ..< vertices.size) vertices[i].also { lineTo(it.x.toFloat(), it.y.toFloat()) }
                close()
            }
        }
        stack.drawScope.apply {
            if (fillColor != null) drawPath(
                path = path,
                color = fillColor.composeMultiplatform,
                style = Fill,
            )
            if (strokeColor != null) drawPath(
                path = path,
                color = strokeColor.composeMultiplatform,
                style = Stroke(width = strokeWidth.toFloat()),
            )
        }
    }
}

public object KoneCanvasComposeMultiplatformPolygon : KoneCanvasPolygon {
    context(controller: KoneCanvasController)
    override fun KoneCanvasContextRegistry.polygon(
        vertices: KoneList<Point2<Double>>,
        fillColor: KoneColor?,
        strokeColor: KoneColor?,
        strokeWidth: Double
    ) {
        KoneCanvasComposeMultiplatformContext(this).polygon(
            vertices = vertices,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
        )
    }
}

@PublishedApi
internal class KoneCanvasComposeMultiplatformPathContext(
    private val context: PathBuilder,
    private val euclideanSpace: EuclideanSpace2OverField<Double>,
    private val shift: Point2<Double>,
    private val zoom: Double,
) : KoneCanvasPathContext {
    override fun moveTo(point: Point2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val point = (point - shift) * zoom
        context.moveTo(point.x.toFloat(), point.y.toFloat())
    }
    override fun moveToRelative(vector: Vector2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val vector = vector * zoom
        context.moveToRelative(vector.x.toFloat(), vector.y.toFloat())
    }
    override fun lineTo(point: Point2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val point = (point - shift) * zoom
        context.lineTo(point.x.toFloat(), point.y.toFloat())
    }
    override fun lineToRelative(vector: Vector2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val vector = vector * zoom
        context.lineToRelative(vector.x.toFloat(), vector.y.toFloat())
    }
    override fun quadraticBezierTo(point1: Point2<Double>, point2: Point2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val point1 = (point1 - shift) * zoom
        val point2 = (point2 - shift) * zoom
        context.quadTo(point1.x.toFloat(), point1.y.toFloat(), point2.x.toFloat(), point2.y.toFloat())
    }
    override fun quadraticBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val vector1 = vector1 * zoom
        val vector2 = vector2 * zoom
        context.quadToRelative(vector1.x.toFloat(), vector1.y.toFloat(), vector2.x.toFloat(), vector2.y.toFloat())
    }
    override fun cubicBezierTo(point1: Point2<Double>, point2: Point2<Double>, point3: Point2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val point1 = (point1 - shift) * zoom
        val point2 = (point2 - shift) * zoom
        val point3 = (point3 - shift) * zoom
        context.curveTo(point1.x.toFloat(), point1.y.toFloat(), point2.x.toFloat(), point2.y.toFloat(), point3.x.toFloat(), point3.y.toFloat())
    }
    override fun cubicBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>, vector3: Vector2<Double>) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val vector1 = vector1 * zoom
        val vector2 = vector2 * zoom
        val vector3 = vector3 * zoom
        context.curveToRelative(vector1.x.toFloat(), vector1.y.toFloat(), vector2.x.toFloat(), vector2.y.toFloat(), vector3.x.toFloat(), vector3.y.toFloat())
    }
    override fun arcRelative(size: Vector2<Double>, rotation: Angle, startAngle: Angle, sweepAngle: Angle) {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(euclideanSpace)
        val size = size * zoom
        // TODO: It's incorrect!!! 'size' and 'rotation' are not took into account in 'startVector' and 'endVector'!
        val startVector = Vector2(sin(startAngle), cos(startAngle))
        val endVector = Vector2(sin(startAngle + sweepAngle), cos(startAngle + sweepAngle))
        val finalPoint = endVector - startVector
        context.arcToRelative(
            a = size.x.toFloat(),
            b = size.y.toFloat(),
            theta = rotation.inDegrees().toFloat(),
            isMoreThanHalf = abs(sweepAngle.inRadians()) >= PI,
            isPositiveArc = sweepAngle.inRadians() > 0.0,
            dx1 = finalPoint.x.toFloat(),
            dy1 = finalPoint.y.toFloat(),
        )
    }
    override fun close() {
        context.close()
    }
}

context(controller: KoneCanvasController)
public inline fun KoneCanvasComposeMultiplatformContext.path(
    fillColor: KoneColor? = null,
    strokeColor: KoneColor? = null,
    strokeWidth: Double = 0.0,
    pathBuilderBlock: KoneCanvasPathContext.() -> Unit,
) {
    val stack = contextRegistry.getOrNull(KoneCanvasComposeMultiplatformStack.Key) ?: return
    val euclideanSpace = controller.getOrNull(KoneContextRegistry.Key)?.getOrNull(EuclideanSpace2OverField.Key<Double>())
        ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
    val data = contextRegistry.getOrNull(KoneCanvasData.Key)
    val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
    val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 1.0
    val pathBuilder = PathBuilder()
    KoneCanvasComposeMultiplatformPathContext(
        context = pathBuilder,
        euclideanSpace = euclideanSpace,
        shift = shift,
        zoom = zoom,
    ).apply(pathBuilderBlock)
    val path = pathBuilder.nodes.toPath()
    if (fillColor != null) stack.drawScope.drawPath(
        path = path,
        color = fillColor.composeMultiplatform,
        style = Fill,
    )
    if (strokeColor != null) stack.drawScope.drawPath(
        path = path,
        color = strokeColor.composeMultiplatform,
        style = Stroke(width = strokeWidth.toFloat()),
    )
}

public object KoneCanvasComposeMultiplatformPath : KoneCanvasPath {
    private class PathContextDelegate(
        private val stack: KoneCanvasComposeMultiplatformStack,
        private val pathBuilder: PathBuilder,
        private val koneCanvasPathContext: KoneCanvasPathContext,
    ) : KoneCanvasPath.Context, KoneCanvasPathContext by koneCanvasPathContext {
        override fun commit(fillColor: KoneColor?, strokeColor: KoneColor?, strokeWidth: Double) {
            val path = pathBuilder.nodes.toPath()
            if (fillColor != null) stack.drawScope.drawPath(
                path = path,
                color = fillColor.composeMultiplatform,
                style = Fill,
            )
            if (strokeColor != null) stack.drawScope.drawPath(
                path = path,
                color = strokeColor.composeMultiplatform,
                style = Stroke(width = strokeWidth.toFloat()),
            )
        }
    }
    
    context(controller: KoneCanvasController)
    override fun pathContext(context: KoneCanvasContextRegistry): KoneCanvasPath.Context {
        val stack = context.getOrNull(KoneCanvasComposeMultiplatformStack.Key) ?: return KoneCanvasPath.Context.IDLE
        val euclideanSpace = controller.getOrNull(KoneContextRegistry.Key)?.getOrNull(EuclideanSpace2OverField.Key<Double>())
            ?: defaultKoneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
        val data = context.getOrNull(KoneCanvasData.Key)
        val shift = data?.getOrNull(KoneCanvasOffsetKey) ?: Point2(0.0, 0.0)
        val zoom = data?.getOrNull(KoneCanvasZoomKey) ?: 1.0
        val pathBuilder = PathBuilder()
        return PathContextDelegate(
            stack = stack,
            pathBuilder = pathBuilder,
            koneCanvasPathContext = KoneCanvasComposeMultiplatformPathContext(
                context = pathBuilder,
                euclideanSpace = euclideanSpace,
                shift = shift,
                zoom = zoom,
            )
        )
    }
}

context(_: MutableOwnedProviderRegistry<KoneCanvasPrimitivesRegistry<KoneCanvasComposeMultiplatformContext>>)
public fun setDefaultPrimitives() {
    KoneCanvasGroup.Key correspondsTo KoneCanvasComposeMultiplatformGroup
    KoneCanvasLine.Key correspondsTo KoneCanvasComposeMultiplatformLine
    KoneCanvasRectangle.Key correspondsTo KoneCanvasComposeMultiplatformRectangle
    KoneCanvasCircle.Key correspondsTo KoneCanvasComposeMultiplatformCircle
    KoneCanvasEllipse.Key correspondsTo KoneCanvasComposeMultiplatformEllipse
    KoneCanvasPolygon.Key correspondsTo KoneCanvasComposeMultiplatformPolygon
    KoneCanvasPath.Key correspondsTo KoneCanvasComposeMultiplatformPath
}

context(_: MutableOwnedProviderRegistry<KoneCanvasTargetsRegistry>)
public fun KoneCanvasComposeMultiplatformContext.Companion.setDefaultPrimitivesRegistry() {
    KoneCanvasTargetKey<KoneCanvasComposeMultiplatformContext>() correspondsTo KoneCanvasPrimitivesRegistry.build {
        setDefaultPrimitives()
    }
}