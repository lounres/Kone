/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.composeCanvas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.emptyKoneList
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.lastThatOrNull
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Vector2
import dev.lounres.kone.context.invoke
import kotlin.math.exp


public interface KoneCanvasDraggableLayer {
    context(DrawScope)
    public fun draw(canvasState: KoneCanvasState)
    public fun capturesPointer(viewSizes: Size, canvasState: KoneCanvasState, pointerPoint: Point2<Float>): Boolean
    public fun shiftBy(shiftVector: Vector2<Float>)
}

private data class PressedLayer(
    var currentPosition: Offset,
    val obj: KoneCanvasDraggableLayer?,
)

@Composable
public fun KoneCanvasWithDraggableLayers(
    modifier: Modifier = Modifier,
    canvasState: KoneCanvasState = KoneCanvasState(),
    onGetCanvasState: () -> KoneCanvasState,
    onChangeCanvasState: (KoneCanvasState) -> Unit = {},
    objects: KoneList<KoneCanvasDraggableLayer> = emptyKoneList(),
) {
    KoneCanvas(
        modifier = modifier
            .pointerInput(Unit) {
                euclideanKategory {
                    awaitPointerEventScope {
                        var pressedLayer: PressedLayer? = null
                        val size = size.toSize()
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Press -> {
                                    val lastPosition = event.changes.last().position
                                    val canvasState = onGetCanvasState()
                                    val (canvasOffset, canvasZoom) = canvasState
                                    val lastCoords = Point2(
                                        (lastPosition.x - size.width / 2) * canvasZoom + canvasOffset.x,
                                        (-lastPosition.y + size.height / 2) * canvasZoom + canvasOffset.y
                                    )
                                    pressedLayer = PressedLayer(
                                        currentPosition = event.changes.last().position,
                                        obj = objects.lastThatOrNull { it.capturesPointer(size, canvasState, lastCoords) },
                                    )
                                }
                                
                                PointerEventType.Release -> {
                                    pressedLayer = null
                                }
                                
                                PointerEventType.Move -> {
                                    if (pressedLayer != null) {
                                        val canvasState = onGetCanvasState()
                                        val (oldOffset, oldZoom) = canvasState
                                        val lastPosition = event.changes.last().position
                                        val offset = lastPosition - pressedLayer.currentPosition
                                        pressedLayer.currentPosition = lastPosition
                                        if (pressedLayer.obj != null) {
                                            pressedLayer.obj.shiftBy(Vector2(offset.x, -offset.y) * oldZoom)
                                        } else {
                                            onChangeCanvasState(
                                                KoneCanvasState(
                                                    offset = euclideanKategory { oldOffset - Vector2(offset.x, -offset.y) * oldZoom },
                                                    zoom = oldZoom
                                                )
                                            )
                                        }
                                    }
                                }
                                
                                PointerEventType.Scroll -> {
                                    val lastChange = event.changes.last()
                                    val zoomDelta = exp(lastChange.scrollDelta.y / 10)
                                    
                                    val canvasState = onGetCanvasState()
                                    val (oldOffset, oldZoom) = canvasState
                                    val newZoom = oldZoom * zoomDelta
                                    val pointerOffset = lastChange.position.let {
                                        Vector2(
                                            -size.width / 2 + it.x,
                                            size.height / 2 - it.y
                                        )
                                    }
                                    
                                    onChangeCanvasState(
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
            },
        canvasState = canvasState,
    ) {
        for (obj in objects) {
            obj.draw(canvasState)
        }
    }
}