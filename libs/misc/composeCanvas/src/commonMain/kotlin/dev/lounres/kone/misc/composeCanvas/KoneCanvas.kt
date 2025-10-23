/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.computationalGeometry.angles.cos
import dev.lounres.kone.computationalGeometry.angles.degrees
import dev.lounres.kone.computationalGeometry.angles.plus
import dev.lounres.kone.computationalGeometry.angles.sin
import dev.lounres.kone.computationalGeometry.default2.Vector2
import dev.lounres.kone.computationalGeometry.plus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.exp


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
        val cos = cos(koneCanvasState.rotation) / koneCanvasState.zoom
        val sin = sin(koneCanvasState.rotation) / koneCanvasState.zoom
        koneCanvasScope.transform(
            transformation = KoneCanvasTransformationMatrix(
                KoneDoubleArray.of(
                    cos, -sin, -koneCanvasState.offset.x * cos + koneCanvasState.offset.y * sin + size.width / 2,
                    -sin, -cos, koneCanvasState.offset.x * sin + koneCanvasState.offset.y * cos + size.height / 2,
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
            for (elementToDraw in koneCanvasScope.elements)
                when (elementToDraw) {
                    is CollectingKoneCanvasScope.ElementToDraw.PathBrushed ->
                        drawPath(
                            path = elementToDraw.path.toComposePath(),
                            brush = elementToDraw.brush,
                            alpha = elementToDraw.alpha,
                            style = elementToDraw.style,
                            colorFilter = elementToDraw.colorFilter,
                            blendMode = elementToDraw.blendMode,
                        )
                    is CollectingKoneCanvasScope.ElementToDraw.PathColored ->
                        drawPath(
                            path = elementToDraw.path.toComposePath(),
                            color = elementToDraw.color,
                            alpha = elementToDraw.alpha,
                            style = elementToDraw.style,
                            colorFilter = elementToDraw.colorFilter,
                            blendMode = elementToDraw.blendMode,
                        )
                    is CollectingKoneCanvasScope.ElementToDraw.EllipseBrushed ->
                        withTransform(
                            {
                                val koneMatrix = elementToDraw.ellipse.transformationMatrix.coefficients
                                transform(
                                    Matrix(
                                        floatArrayOf(
                                            koneMatrix[0u].toFloat(), koneMatrix[3u].toFloat(), 0f, 0f,
                                            koneMatrix[1u].toFloat(), koneMatrix[4u].toFloat(), 0f, 0f,
                                            0f, 0f, 1f, 0f,
                                            koneMatrix[2u].toFloat(), koneMatrix[5u].toFloat(), 0f, 1f,
                                        )
                                    )
                                )
                            }
                        ) {
                            drawCircle(
                                brush = elementToDraw.brush,
                                radius = 1f,
                                center = Offset.Zero,
                                alpha = elementToDraw.alpha,
                                style = elementToDraw.style,
                                colorFilter = elementToDraw.colorFilter,
                                blendMode = elementToDraw.blendMode,
                            )
                        }
                    is CollectingKoneCanvasScope.ElementToDraw.EllipseColored ->
                        withTransform(
                            {
                                val koneMatrix = elementToDraw.ellipse.transformationMatrix.coefficients
                                transform(
                                    Matrix(
                                        floatArrayOf(
                                            koneMatrix[0u].toFloat(), koneMatrix[3u].toFloat(), 0f, 0f,
                                            koneMatrix[1u].toFloat(), koneMatrix[4u].toFloat(), 0f, 0f,
                                            0f, 0f, 1f, 0f,
                                            koneMatrix[2u].toFloat(), koneMatrix[5u].toFloat(), 0f, 1f,
                                        )
                                    )
                                )
                            }
                        ) {
                            drawCircle(
                                color = elementToDraw.color,
                                radius = 1f,
                                center = Offset.Zero,
                                alpha = elementToDraw.alpha,
                                style = elementToDraw.style,
                                colorFilter = elementToDraw.colorFilter,
                                blendMode = elementToDraw.blendMode,
                            )
                        }
                    is CollectingKoneCanvasScope.ElementToDraw.Image ->
                        withTransform(
                            {
                                val koneMatrix = elementToDraw.image.transformationMatrix.coefficients
                                transform(
                                    Matrix(
                                        floatArrayOf(
                                            koneMatrix[0u].toFloat(), koneMatrix[3u].toFloat(), 0f, 0f,
                                            -koneMatrix[1u].toFloat(), -koneMatrix[4u].toFloat(), 0f, 0f,
                                            0f, 0f, 1f, 0f,
                                            koneMatrix[2u].toFloat(), koneMatrix[5u].toFloat(), 0f, 1f,
                                        )
                                    )
                                )
                            }
                        ) {
                            val image = elementToDraw.image.image
                            drawImage(
                                image = image,
                                topLeft = Offset(-image.width.toFloat() / 2, -image.height.toFloat() / 2),
                                alpha = elementToDraw.alpha,
                                style = elementToDraw.style,
                                colorFilter = elementToDraw.colorFilter,
                                blendMode = elementToDraw.blendMode,
                            )
                        }
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
    
    SideEffect {
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
            inKoneCanvasEuclideanSpace {
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
                                    val cos = cos(oldRotation)
                                    val sin = sin(oldRotation)
                                    currentPressPosition = lastPosition
                                    setKoneCanvasState(
                                        KoneCanvasState(
                                            offset = oldOffset +
                                                -Vector2(
                                                    offset.x.toDouble() * cos - offset.y.toDouble() * sin,
                                                    -offset.y.toDouble() * cos - offset.x.toDouble() * sin,
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
                                                    + -pointerOffset.let {
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