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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import dev.lounres.kone.algebraic.field
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.emptyKoneList
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.utils.lastThatOrNull
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Vector2
import dev.lounres.kone.computationalGeometry.euclideanKategory
import dev.lounres.kone.context.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.exp


@PublishedApi
internal val euclideanKategory = Float.field.euclideanKategory

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

public interface KoneCanvasObject {
    context(DrawScope)
    public fun draw(canvasState: KoneCanvasState)
    public fun capturesPointer(viewSizes: Size, canvasState: KoneCanvasState, pointerPoint: Point2<Float>): Boolean
    public fun shiftBy(shiftVector: Vector2<Float>)
}

private data class PressedObject(
    var currentPosition: Offset,
    val obj: KoneCanvasObject?,
)

@Composable
public fun KoneCanvasWithObjects(
    modifier: Modifier = Modifier,
    canvasState: KoneCanvasState = KoneCanvasState(),
    onGetCanvasState: () -> KoneCanvasState,
    onChangeCanvasState: (KoneCanvasState) -> Unit = {},
    objects: KoneList<KoneCanvasObject> = emptyKoneList(),
) {
    KoneCanvas(
        modifier = modifier
            .pointerInput(Unit) {
                euclideanKategory {
                    awaitPointerEventScope {
                        var pressedObject: PressedObject? = null
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
                                    pressedObject = PressedObject(
                                        currentPosition = event.changes.last().position,
                                        obj = objects.lastThatOrNull { it.capturesPointer(size, canvasState, lastCoords) },
                                    )
                                }
                                
                                PointerEventType.Release -> {
                                    pressedObject = null
                                }
                                
                                PointerEventType.Move -> {
                                    if (pressedObject != null) {
                                        val canvasState = onGetCanvasState()
                                        val (oldOffset, oldZoom) = canvasState
                                        val lastPosition = event.changes.last().position
                                        val offset = lastPosition - pressedObject.currentPosition
                                        pressedObject.currentPosition = lastPosition
                                        if (pressedObject.obj != null) {
                                            pressedObject.obj.shiftBy(Vector2(offset.x, -offset.y) * oldZoom)
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
//            obj.draw2(this, canvasState)
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
                                    offset = euclideanKategory { oldOffset - Vector2(offset.x, -offset.y) * oldZoom },
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
                        val pointerOffset = lastChange.position.let { Vector2(-size.width/2 + it.x, size.height/2 - it.y) }
                        
                        setKoneCanvasState(
                            KoneCanvasState(
                                offset = euclideanKategory { oldOffset + pointerOffset * (oldZoom - newZoom) },
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