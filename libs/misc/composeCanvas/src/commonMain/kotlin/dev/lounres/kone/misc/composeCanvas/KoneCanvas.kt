/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.computationalGeometry.*
import dev.lounres.kone.computationalGeometry.angles.cos
import dev.lounres.kone.computationalGeometry.angles.degrees
import dev.lounres.kone.computationalGeometry.angles.plus
import dev.lounres.kone.computationalGeometry.angles.sin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.exp


public data class ViewRegion(
    val xMin: Double,
    val xMax: Double,
    val yMin: Double,
    val yMax: Double,
)

context(koneCanvasSize: KoneCanvasSize)
public val KoneCanvasState.viewRegion: ViewRegion
    get() = ViewRegion(
        xMin = offset.x - koneCanvasSize.width / 2 * zoom,
        xMax = offset.x + koneCanvasSize.width / 2 * zoom,
        yMin = offset.y - koneCanvasSize.height / 2 * zoom,
        yMax = offset.y + koneCanvasSize.height / 2 * zoom,
    )

public val ViewRegion.leftBottomOffset: Offset
    get() = Offset(xMin.toFloat(), yMin.toFloat())
public val ViewRegion.leftTopOffset: Offset
    get() = Offset(xMin.toFloat(), yMax.toFloat())
public val ViewRegion.rightBottomOffset: Offset
    get() = Offset(xMax.toFloat(), yMin.toFloat())
public val ViewRegion.rightTopOffset: Offset
    get() = Offset(xMax.toFloat(), yMax.toFloat())

@Composable
public fun KoneCanvas(
    modifier: Modifier = Modifier,
    koneCanvasState: KoneCanvasState,
    clip: Boolean = true,
    onDraw: context(KoneCanvasSize, Density) KoneCanvasScope.() -> Unit,
) {
    Canvas(
        modifier = modifier,
    ) {
        val koneCanvasScope = CollectingKoneCanvasScope()
        val cos = cos(koneCanvasState.rotation)
        val sin = sin(koneCanvasState.rotation)
        koneCanvasScope.transform(
            transformation = KoneCanvasTransformationMatrix(
                KoneDoubleArray.of(
                    cos, -sin, -koneCanvasState.offset.x * cos + koneCanvasState.offset.y * sin + size.width / 2 * koneCanvasState.zoom,
                    -sin, -cos, koneCanvasState.offset.x * sin + koneCanvasState.offset.y * cos + size.height / 2 * koneCanvasState.zoom,
                    0.0, 0.0, koneCanvasState.zoom,
                )
            )
        ) {
            onDraw(KoneCanvasSize(height = size.height.toDouble(), width = size.width.toDouble()), this@Canvas, this)
        }
        withTransform(
            {
                if (clip) clipRect()
            }
        ) {
            for (pathToDraw in koneCanvasScope.paths)
                when (pathToDraw) {
                    is CollectingKoneCanvasScope.PathToDraw.Brushed ->
                        drawPath(
                            path = pathToDraw.path.toComposePath(),
                            brush = pathToDraw.brush,
                            alpha = pathToDraw.alpha,
                            style = pathToDraw.style,
                            colorFilter = pathToDraw.colorFilter,
                            blendMode = pathToDraw.blendMode,
                        )
                    is CollectingKoneCanvasScope.PathToDraw.Colored ->
                        drawPath(
                            path = pathToDraw.path.toComposePath(),
                            color = pathToDraw.color,
                            alpha = pathToDraw.alpha,
                            style = pathToDraw.style,
                            colorFilter = pathToDraw.colorFilter,
                            blendMode = pathToDraw.blendMode,
                        )
                }
        }
    }
}

@Composable
public inline fun Modifier.defaultKoneCanvasPointerInput(
    crossinline getKoneCanvasState: () -> KoneCanvasState,
    crossinline setKoneCanvasState: (KoneCanvasState) -> Unit,
): Modifier {
    var isCtrlPressed by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    return this
        .focusRequester(focusRequester)
        .focusable()
        .onKeyEvent {
            isCtrlPressed = it.isCtrlPressed
            true
        }
        .pointerInput(Unit) {
            koneCanvasContextRegistry.inEuclideanKategoryScope2For<Double, _>(doubleSuppliedType) {
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
                                    val (oldOffset, oldZoom, oldRotation) = getKoneCanvasState()
                                    val lastPosition = event.changes.last().position
                                    val offset = lastPosition - currentPressPosition
                                    currentPressPosition = lastPosition
                                    setKoneCanvasState(
                                        KoneCanvasState(
                                            offset = oldOffset - Vector2(
                                                offset.x.toDouble(),
                                                -offset.y.toDouble()
                                            ) * oldZoom,
                                            zoom = oldZoom,
                                            rotation = oldRotation,
                                        )
                                    )
                                }
                            }

                            PointerEventType.Scroll -> {
                                val lastChange = event.changes.last()

                                val (oldOffset, oldZoom, oldRotation) = getKoneCanvasState()

                                val pointerOffset =
                                    lastChange.position.let {
                                        Vector2(
                                            (-size.width / 2 + it.x).toDouble(),
                                            (size.height / 2 - it.y).toDouble()
                                        )
                                    }

                                if (!isCtrlPressed) {
                                    val zoomDelta = exp(lastChange.scrollDelta.y / 10)

                                    val newZoom = oldZoom * zoomDelta
                                    val cos = cos(oldRotation)
                                    val sin = sin(oldRotation)

                                    setKoneCanvasState(
                                        KoneCanvasState(
                                            offset = oldOffset + pointerOffset.let {
                                                Vector2(
                                                    it.x * cos + it.y * sin,
                                                    -it.x * sin + it.y * cos
                                                )
                                            } * (oldZoom - newZoom),
                                            zoom = newZoom,
                                            rotation = oldRotation,
                                        )
                                    )
                                } else {
                                    val angleDelta = lastChange.scrollDelta.y.toDouble().degrees

                                    setKoneCanvasState(
                                        KoneCanvasState(
                                            offset = oldOffset
                                                    + pointerOffset.let {
                                                        val cos = cos(oldRotation)
                                                        val sin = sin(oldRotation)
                                                        Vector2(it.x * cos + it.y * sin, it.x * -sin + it.y * cos) * oldZoom
                                                    }
                                                    - pointerOffset.let {
                                                        val cos = cos(oldRotation + angleDelta)
                                                        val sin = sin(oldRotation + angleDelta)
                                                        Vector2(it.x * cos + it.y * sin, it.x * -sin + it.y * cos) * oldZoom
                                                    },
                                            zoom = oldZoom,
                                            rotation = oldRotation + angleDelta,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
}

@Composable
public fun Modifier.defaultKoneCanvasPointerInput(
    koneCanvasState: MutableState<KoneCanvasState>,
): Modifier = defaultKoneCanvasPointerInput(
    getKoneCanvasState = { koneCanvasState.value },
    setKoneCanvasState = { koneCanvasState.value = it },
)

@Composable
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
    onDraw: context(KoneCanvasSize, Density) KoneCanvasScope.(KoneCanvasState) -> Unit,
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