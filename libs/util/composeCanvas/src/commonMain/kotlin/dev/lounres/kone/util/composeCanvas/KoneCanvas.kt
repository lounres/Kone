/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.composeCanvas

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.exp


public data class KoneCanvasState(
    val offset: Offset = Offset.Zero,
    val zoom: Float = 1.0f,
)

public data class ViewRegion(
    val xMin: Float,
    val xMax: Float,
    val yMin: Float,
    val yMax: Float,
)

context(DrawScope)
public val KoneCanvasState.viewRegion: ViewRegion
    get() = ViewRegion(
        xMin = offset.x - size.width / 2 * zoom,
        xMax = offset.x + size.width / 2 * zoom,
        yMin = offset.y - size.height / 2 * zoom,
        yMax = offset.y + size.height / 2 * zoom,
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
    canvasState: KoneCanvasState = KoneCanvasState(),
    onDraw: DrawScope.() -> Unit,
) {
    Canvas(
        modifier = modifier,
    ) {
        clipRect {
            withTransform(
                {
                    transform(
                        Matrix(
                            floatArrayOf(
                                1f, 0f, 0f, 0f,
                                0f, -1f, 0f, 0f,
                                0f, 0f, 1f, 0f,
                                size.width / 2 * canvasState.zoom - canvasState.offset.x, size.height / 2 * canvasState.zoom + canvasState.offset.y, 0f, canvasState.zoom
                            )
                        )
                    )
                },
                onDraw
            )
        }
    }
}

public inline fun Modifier.defaultKoneCanvasPointerInput(
    crossinline getKoneCanvasState: () -> KoneCanvasState,
    crossinline setKoneCanvasState: (KoneCanvasState) -> Unit,
): Modifier = this
    .pointerInput(Unit) {
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
                                    offset = oldOffset - Offset(offset.x, -offset.y) * oldZoom,
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
                        val pointerOffset = lastChange.position.let { Offset(-size.width/2 + it.x, size.height/2 - it.y) }
                        
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

public fun Modifier.defaultKoneCanvasPointerInput(
    koneCanvasState: MutableState<KoneCanvasState>
): Modifier = defaultKoneCanvasPointerInput(
    getKoneCanvasState = { koneCanvasState.value },
    setKoneCanvasState = { koneCanvasState.value = it },
)

public fun Modifier.defaultKoneCanvasPointerInput(
    koneCanvasState: MutableStateFlow<KoneCanvasState>
): Modifier = defaultKoneCanvasPointerInput(
    getKoneCanvasState = { koneCanvasState.value },
    setKoneCanvasState = { koneCanvasState.value = it },
)