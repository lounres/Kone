/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Vector2
import dev.lounres.kone.computationalGeometry.inEuclideanKategoryScope2For
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.computationalGeometry.times
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlin.math.exp


// TODO: Add rotation to parameters.
@Serializable
public data class KoneCanvasState(
    val offset: Point2<Float> = Point2(0f, 0f),
    val zoom: Float = 1.0f,
)

public data class ViewRegion(
    val xMin: Float,
    val xMax: Float,
    val yMin: Float,
    val yMax: Float,
)

context(drawScope: DrawScope)
public val KoneCanvasState.viewRegion: ViewRegion
    get() = ViewRegion(
        xMin = offset.x - drawScope.size.width / 2 * zoom,
        xMax = offset.x + drawScope.size.width / 2 * zoom,
        yMin = offset.y - drawScope.size.height / 2 * zoom,
        yMax = offset.y + drawScope.size.height / 2 * zoom,
    )

public val ViewRegion.leftBottom: Offset
    get() = Offset(xMin, yMin)
public val ViewRegion.leftTop: Offset
    get() = Offset(xMin, yMax)
public val ViewRegion.rightBottom: Offset
    get() = Offset(xMax, yMin)
public val ViewRegion.rightTop: Offset
    get() = Offset(xMax, yMax)

@Composable
public fun KoneCanvas(
    modifier: Modifier = Modifier,
    koneCanvasState: KoneCanvasState,
    clip: Boolean = true,
    onDraw: DrawScope.() -> Unit,
) {
    Canvas(
        modifier = modifier,
    ) {
        withTransform(
            {
                if (clip) clipRect(0.0f, 0.0f, size.width, size.height, ClipOp.Intersect)
                transform(
                    Matrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f,
                            0f, -1f, 0f, 0f,
                            0f, 0f, 1f, 0f,
                            size.width / 2 * koneCanvasState.zoom - koneCanvasState.offset.x, size.height / 2 * koneCanvasState.zoom + koneCanvasState.offset.y, 0f, koneCanvasState.zoom
                        )
                    )
                )
            },
            onDraw
        )
    }
}

public inline fun Modifier.defaultKoneCanvasPointerInput(
    crossinline getKoneCanvasState: () -> KoneCanvasState,
    crossinline setKoneCanvasState: (KoneCanvasState) -> Unit,
): Modifier =
    pointerInput(Unit) {
        koneCanvasContextRegistry.inEuclideanKategoryScope2For(floatSuppliedType) {
            awaitPointerEventScope {
                var currentPressPosition: Offset? = null
                while (true) {
                    val event = awaitPointerEvent()
                    when (event.type) {
                        PointerEventType.Press -> {
                            currentPressPosition = event.changes.last().position
                        }
                        
                        PointerEventType.Release -> {
                            currentPressPosition = null
                        }
                        
                        PointerEventType.Move -> {
                            if (currentPressPosition != null) {
                                val (oldOffset, oldZoom) = getKoneCanvasState()
                                val lastPosition = event.changes.last().position
                                val offset = lastPosition - currentPressPosition
                                currentPressPosition = lastPosition
                                setKoneCanvasState(
                                    KoneCanvasState(
                                        offset = oldOffset - Vector2(offset.x, -offset.y) * oldZoom,
                                        zoom = oldZoom
                                    )
                                )
                            }
                        }
                        
                        PointerEventType.Scroll -> {
                            val lastChange = event.changes.last()
                            val zoomDelta = exp(lastChange.scrollDelta.y / 10)
                            
                            val (oldOffset, oldZoom) = getKoneCanvasState()
                            val newZoom = oldZoom * zoomDelta
                            val pointerOffset =
                                lastChange.position.let { Vector2(-size.width / 2 + it.x, size.height / 2 - it.y) }
                            
                            setKoneCanvasState(
                                KoneCanvasState(
                                    offset = oldOffset + pointerOffset * (oldZoom - newZoom),
                                    zoom = newZoom
                                )
                            )
                        }
                    }
                }
            }
        }
    }

public fun Modifier.defaultKoneCanvasPointerInput(
    koneCanvasState: MutableState<KoneCanvasState>,
): Modifier = defaultKoneCanvasPointerInput(
    getKoneCanvasState = { koneCanvasState.value },
    setKoneCanvasState = { koneCanvasState.value = it },
)

public fun Modifier.defaultKoneCanvasPointerInput(
    koneCanvasState: MutableStateFlow<KoneCanvasState>,
): Modifier = defaultKoneCanvasPointerInput(
    getKoneCanvasState = { koneCanvasState.value },
    setKoneCanvasState = { koneCanvasState.value = it },
)

@Composable
public fun KoneDefaultCanvas(
    modifier: Modifier = Modifier,
    koneCanvasStateState: MutableState<KoneCanvasState> = remember { mutableStateOf(KoneCanvasState()) },
    clip: Boolean = true,
    onDraw: DrawScope.(KoneCanvasState) -> Unit,
) {
    var canvasState by koneCanvasStateState
    KoneCanvas(
        modifier = modifier.defaultKoneCanvasPointerInput(koneCanvasStateState),
        clip = clip,
        koneCanvasState = canvasState,
    ) {
        onDraw(canvasState)
    }
}