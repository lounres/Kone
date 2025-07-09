/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.utils.lastThatOrNull
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.computationalGeometry.Vector2
import dev.lounres.kone.computationalGeometry.inEuclideanKategoryScope2For
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.computationalGeometry.times
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.exp


//public interface KoneCanvasLayer {
//    context(drawScope: DrawScope)
//    public fun draw(canvasState: KoneCanvasState)
//}
//
//public interface KoneCapturableCanvasLayer : KoneCanvasLayer {
//    public fun capturesPointer(viewSizes: Size, canvasState: KoneCanvasState, pointerPoint: Point2<Float>): Boolean
//}
//
//public interface KoneDraggableCanvasLayer : KoneCapturableCanvasLayer {
//    public fun shiftBy(shiftVector: Vector2<Float>)
//}
//
//public data class PressedLayer @PublishedApi internal constructor(
//    var currentPosition: Offset,
//    val layer: KoneDraggableCanvasLayer?,
//)
//
//@Composable
//public fun KoneCanvasWithLayers(
//    modifier: Modifier = Modifier,
//    koneCanvasState: KoneCanvasState,
//    clip: Boolean = true,
//    layers: KoneList<KoneCanvasLayer> = KoneList.empty(),
//) {
//    KoneCanvas(
//        modifier = modifier,
//        koneCanvasState = koneCanvasState,
//        clip = clip,
//    ) {
//        for (layer in layers) layer.draw(koneCanvasState)
//    }
//}
//
//public inline fun Modifier.defaultKoneCanvasWithLayersPointerInput(
//    crossinline getKoneCanvasState: () -> KoneCanvasState,
//    crossinline setKoneCanvasState: (KoneCanvasState) -> Unit,
//    layers: KoneList<KoneDraggableCanvasLayer>,
//): Modifier =
//    pointerInput(layers) {
//        koneCanvasContextRegistry.inEuclideanKategoryScope2For(doubleSuppliedType) {
//            awaitPointerEventScope {
//                var pressedLayer: PressedLayer? = null
//                val size = size.toSize()
//                while (true) {
//                    val event = awaitPointerEvent()
//                    when (event.type) {
//                        PointerEventType.Press -> {
//                            val lastPosition = event.changes.last().position
//                            val (canvasOffset, canvasZoom) = getKoneCanvasState()
//                            val lastCoords = Point2(
//                                (lastPosition.x - size.width / 2) * canvasZoom + canvasOffset.x,
//                                (-lastPosition.y + size.height / 2) * canvasZoom + canvasOffset.y
//                            )
//                            pressedLayer = PressedLayer(
//                                currentPosition = event.changes.last().position,
//                                layer = layers.lastThatOrNull { it.capturesPointer(size, getKoneCanvasState(), lastCoords) },
//                            )
//                        }
//
//                        PointerEventType.Release -> {
//                            pressedLayer = null
//                        }
//
//                        PointerEventType.Move -> {
//                            if (pressedLayer != null) {
//                                val (oldOffset, oldZoom) = getKoneCanvasState()
//                                val lastPosition = event.changes.last().position
//                                val offset = lastPosition - pressedLayer.currentPosition
//                                pressedLayer.currentPosition = lastPosition
//                                if (pressedLayer.layer != null) {
//                                    pressedLayer.layer.shiftBy(Vector2(offset.x, -offset.y) * oldZoom)
//                                } else {
//                                    setKoneCanvasState(
//                                        KoneCanvasState(
//                                            offset = oldOffset - Vector2(offset.x, -offset.y) * oldZoom,
//                                            zoom = oldZoom
//                                        )
//                                    )
//                                }
//                            }
//                        }
//
//                        PointerEventType.Scroll -> {
//                            val lastChange = event.changes.last()
//                            val zoomDelta = exp(lastChange.scrollDelta.y / 10)
//
//                            val (oldOffset, oldZoom) = getKoneCanvasState()
//                            val newZoom = oldZoom * zoomDelta
//                            val pointerOffset = lastChange.position.let {
//                                Vector2(
//                                    -size.width / 2 + it.x,
//                                    size.height / 2 - it.y
//                                )
//                            }
//
//                            setKoneCanvasState(
//                                KoneCanvasState(
//                                    offset = oldOffset + pointerOffset * (oldZoom - newZoom),
//                                    zoom = newZoom
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//public fun Modifier.defaultKoneCanvasWithLayersPointerInput(
//    koneCanvasState: MutableState<KoneCanvasState>,
//    layers: KoneList<KoneDraggableCanvasLayer>,
//): Modifier = defaultKoneCanvasWithLayersPointerInput(
//    getKoneCanvasState = { koneCanvasState.value },
//    setKoneCanvasState = { koneCanvasState.value = it },
//    layers = layers,
//)
//
//public fun Modifier.defaultKoneCanvasWithLayersPointerInput(
//    koneCanvasState: MutableStateFlow<KoneCanvasState>,
//    layers: KoneList<KoneDraggableCanvasLayer>,
//): Modifier = defaultKoneCanvasWithLayersPointerInput(
//    getKoneCanvasState = { koneCanvasState.value },
//    setKoneCanvasState = { koneCanvasState.value = it },
//    layers = layers,
//)
//
//@Composable
//public fun KoneDefaultCanvasWithLayers(
//    modifier: Modifier = Modifier,
//    koneCanvasStateState: MutableState<KoneCanvasState> = remember { mutableStateOf(KoneCanvasState()) },
//    clip: Boolean = true,
//    layers: KoneList<KoneDraggableCanvasLayer> = KoneList.empty(),
//) {
//    KoneCanvasWithLayers(
//        modifier = modifier.defaultKoneCanvasWithLayersPointerInput(koneCanvasStateState, layers),
//        clip = clip,
//        koneCanvasState = koneCanvasStateState.value,
//        layers = layers,
//    )
//}