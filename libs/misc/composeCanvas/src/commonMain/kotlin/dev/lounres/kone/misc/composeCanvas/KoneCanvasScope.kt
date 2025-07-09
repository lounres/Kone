/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Vector2
import dev.lounres.kone.computationalGeometry.angles.Angle
import dev.lounres.kone.computationalGeometry.angles.cos
import dev.lounres.kone.computationalGeometry.angles.sin
import dev.lounres.kone.computationalGeometry.inEuclideanKategoryScope2For
import dev.lounres.kone.computationalGeometry.lengthSquared
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.computationalGeometry.times
import kotlin.math.sqrt


@DslMarker
public annotation class KoneCanvasDsl

@KoneCanvasDsl
public interface KoneCanvasScope {
    public fun drawPath(
        path: KoneCanvasPath,
        brush: Brush,
        alpha: Float = 1.0f,
        style: DrawStyle = Fill,
        colorFilter: ColorFilter? = null,
        blendMode: BlendMode = DefaultBlendMode
    )
    
    public fun drawPath(
        path: KoneCanvasPath,
        color: Color,
        alpha: Float = 1.0f,
        style: DrawStyle = Fill,
        colorFilter: ColorFilter? = null,
        blendMode: BlendMode = DefaultBlendMode
    )
}

internal class CollectingKoneCanvasScope : KoneCanvasScope {
    sealed interface PathToDraw {
        data class Brushed(
            val path: KoneCanvasPath,
            val brush: Brush,
            val alpha: Float,
            val style: DrawStyle,
            val colorFilter: ColorFilter?,
            val blendMode: BlendMode,
        ) : PathToDraw
        data class Colored(
            val path: KoneCanvasPath,
            val color: Color,
            val alpha: Float,
            val style: DrawStyle,
            val colorFilter: ColorFilter?,
            val blendMode: BlendMode,
        ) : PathToDraw
    }
    
    val paths: KoneMutableList<PathToDraw> = KoneMutableList.of()
    
    override fun drawPath(
        path: KoneCanvasPath,
        brush: Brush,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) {
        paths.add(
            PathToDraw.Brushed(
                path = path,
                brush = brush,
                alpha = alpha,
                style = style,
                colorFilter = colorFilter,
                blendMode = blendMode,
            )
        )
    }
    
    override fun drawPath(
        path: KoneCanvasPath,
        color: Color,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) {
        paths.add(
            PathToDraw.Colored(
                path = path,
                color = color,
                alpha = alpha,
                style = style,
                colorFilter = colorFilter,
                blendMode = blendMode,
            )
        )
    }
}

internal class TransformedKoneCanvasScope(
    val parentScope: KoneCanvasScope,
    val transformation: KoneCanvasTransformationMatrix,
) : KoneCanvasScope {
    override fun drawPath(
        path: KoneCanvasPath,
        brush: Brush,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) {
        parentScope.drawPath(
            path = path.transform(transformation),
            brush = brush,
            alpha = alpha,
            style = style,
            colorFilter = colorFilter,
            blendMode = blendMode
        )
    }
    
    override fun drawPath(
        path: KoneCanvasPath,
        color: Color,
        alpha: Float,
        style: DrawStyle,
        colorFilter: ColorFilter?,
        blendMode: BlendMode
    ) {
        parentScope.drawPath(
            path = path.transform(transformation),
            color = color,
            alpha = alpha,
            style = style,
            colorFilter = colorFilter,
            blendMode = blendMode
        )
    }
}

public fun KoneCanvasScope.transform(
    transformation: KoneCanvasTransformationMatrix,
    draw: KoneCanvasScope.() -> Unit
) {
    TransformedKoneCanvasScope(this, transformation.normalized()).draw()
}

public fun KoneCanvasScope.drawLine(
    brush: Brush,
    start: Point2<Double>,
    end: Point2<Double>,
    strokeWidth: Double = 0.0,
    cap: StrokeCap = Stroke.DefaultCap,
//    pathEffect: PathEffect? = null,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    koneCanvasContextRegistry.inEuclideanKategoryScope2For(doubleSuppliedType) {
        val directionVector = end - start
        val strokeVector = directionVector.let { Vector2(-it.y, it.x) } * (strokeWidth / 2 / sqrt(directionVector.lengthSquared))
        drawPath(
            path = when (cap) {
                StrokeCap.Butt -> KoneCanvasPath {
                    moveTo(start + strokeVector)
                    lineTo(end + strokeVector)
                    lineTo(end - strokeVector)
                    lineTo(start - strokeVector)
                    lineTo(start + strokeVector)
                }
                StrokeCap.Round -> KoneCanvasPath {
                    error("Unsupported type of cap for now")
                }
                StrokeCap.Square -> KoneCanvasPath {
                    val directionPadVector = strokeVector.let { Vector2(it.y, -it.x) }
                    val start = start - directionPadVector
                    val end = end + directionPadVector
                    moveTo(start + strokeVector)
                    lineTo(end + strokeVector)
                    lineTo(end - strokeVector)
                    lineTo(start - strokeVector)
                    lineTo(start + strokeVector)
                }
                else -> error("Unknown kind of cap")
            },
            brush = brush,
            alpha = alpha,
            style = Fill,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}

public fun KoneCanvasScope.drawLine(
    color: Color,
    start: Point2<Double>,
    end: Point2<Double>,
    strokeWidth: Double = 0.0,
    cap: StrokeCap = Stroke.DefaultCap,
//    pathEffect: PathEffect? = null,
    alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    koneCanvasContextRegistry.inEuclideanKategoryScope2For(doubleSuppliedType) {
        val directionVector = end - start
        val strokeVector = directionVector.let { Vector2(-it.y, it.x) } * (strokeWidth / 2 / sqrt(directionVector.lengthSquared))
        drawPath(
            path = when (cap) {
                StrokeCap.Butt -> KoneCanvasPath {
                    moveTo(start + strokeVector)
                    lineTo(end + strokeVector)
                    lineTo(end - strokeVector)
                    lineTo(start - strokeVector)
                    lineTo(start + strokeVector)
                }
                StrokeCap.Round -> KoneCanvasPath {
                    error("Unsupported type of cap for now")
                }
                StrokeCap.Square -> KoneCanvasPath {
                    val directionPadVector = strokeVector.let { Vector2(it.y, -it.x) }
                    val start = start - directionPadVector
                    val end = end + directionPadVector
                    moveTo(start + strokeVector)
                    lineTo(end + strokeVector)
                    lineTo(end - strokeVector)
                    lineTo(start - strokeVector)
                    lineTo(start + strokeVector)
                }
                else -> error("Unknown kind of cap")
            },
            color = color,
            alpha = alpha,
            style = Fill,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}

public fun KoneCanvasScope.drawRectangle(
    brush: Brush,
    center: Point2<Double>,
    size: KoneCanvasSize,
    direction: Angle = Angle.zero,
    alpha: Float = 1.0f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    koneCanvasContextRegistry.inEuclideanKategoryScope2For(doubleSuppliedType) {
        val widthVector = Vector2(cos(direction), sin(direction)) * size.width
        val heightVector = Vector2(-sin(direction), cos(direction)) * size.height
        drawPath(
            brush = brush,
            path = KoneCanvasPath {
                moveTo(center + widthVector + heightVector)
                lineTo(center - widthVector + heightVector)
                lineTo(center - widthVector - heightVector)
                lineTo(center + widthVector - heightVector)
                lineTo(center + widthVector + heightVector)
            },
            alpha = alpha,
            style = style,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}

public fun KoneCanvasScope.drawRectangle(
    color: Color,
    center: Point2<Double>,
    size: KoneCanvasSize,
    direction: Angle = Angle.zero,
    alpha: Float = 1.0f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    koneCanvasContextRegistry.inEuclideanKategoryScope2For(doubleSuppliedType) {
        val widthVector = Vector2(cos(direction), sin(direction)) * (size.width / 2)
        val heightVector = Vector2(-sin(direction), cos(direction)) * (size.height / 2)
        drawPath(
            color = color,
            path = KoneCanvasPath {
                moveTo(center + widthVector + heightVector)
                lineTo(center - widthVector + heightVector)
                lineTo(center - widthVector - heightVector)
                lineTo(center + widthVector - heightVector)
                lineTo(center + widthVector + heightVector)
            },
            alpha = alpha,
            style = style,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}

//public fun KoneCanvasScope.drawCircle(
//    brush: Brush,
//    radius: Double,
//    center: Point2<Double>,
//    alpha: Float = 1.0f,
//    style: DrawStyle = Fill,
//    colorFilter: ColorFilter? = null,
//    blendMode: BlendMode = DefaultBlendMode
//) {
//    TODO()
//}
//
//public fun KoneCanvasScope.drawCircle(
//    color: Color,
//    radius: Double,
//    center: Point2<Double>,
//    alpha: Float = 1.0f,
//    style: DrawStyle = Fill,
//    colorFilter: ColorFilter? = null,
//    blendMode: BlendMode = DefaultBlendMode
//) {
//    TODO()
//}
//
//public fun KoneCanvasScope.drawOval(
//    brush: Brush,
//    center: Point2<Double>,
//    size: KoneCanvasSize,
//    direction: Angle = Angle.zero,
//    alpha: Float = 1.0f,
//    style: DrawStyle = Fill,
//    colorFilter: ColorFilter? = null,
//    blendMode: BlendMode = DefaultBlendMode
//) {
//    TODO()
//}
//
//public fun KoneCanvasScope.drawOval(
//    color: Color,
//    center: Point2<Double>,
//    size: KoneCanvasSize,
//    direction: Angle = Angle.zero,
//    alpha: Float = 1.0f,
//    style: DrawStyle = Fill,
//    colorFilter: ColorFilter? = null,
//    blendMode: BlendMode = DefaultBlendMode
//) {
//    TODO()
//}
//
//public fun KoneCanvasScope.drawArc(
//    brush: Brush,
//    startAngle: Angle,
//    sweepAngle: Angle,
//    useCenter: Boolean,
//    center: Point2<Double>,
//    size: KoneCanvasSize,
//    direction: Angle = Angle.zero,
//    alpha: Float = 1.0f,
//    style: DrawStyle = Fill,
//    colorFilter: ColorFilter? = null,
//    blendMode: BlendMode = DefaultBlendMode
//) {
//    TODO()
//}
//
//public fun KoneCanvasScope.drawArc(
//    color: Color,
//    startAngle: Angle,
//    sweepAngle: Angle,
//    useCenter: Boolean,
//    center: Point2<Double>,
//    size: KoneCanvasSize,
//    direction: Angle = Angle.zero,
//    alpha: Float = 1.0f,
//    style: DrawStyle = Fill,
//    colorFilter: ColorFilter? = null,
//    blendMode: BlendMode = DefaultBlendMode
//) {
//    TODO()
//}