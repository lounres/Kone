/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverField
import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.computationalGeometry.default2.Vector2
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.buildWithProvider
import dev.lounres.kone.contexts.unwrap
import dev.lounres.kone.misc.canvas.*
import dev.lounres.kone.registry.*
import dev.lounres.kone.scope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.exp


@Composable
public fun KoneComposeCanvas(modifier: Modifier = Modifier, canvasController: KoneCanvasController, canvasData: KoneCanvasData) {
    val triggerState = remember { mutableStateOf(KoneCanvasTrigger) }
    LaunchedEffect(canvasData) {
        val triggerConsumer = canvasData.getOrNull(KoneCanvasTriggerConsumer.Key) ?: KoneCanvasTriggerConsumer { it() }
        triggerConsumer.onTrigger { triggerState.value = KoneCanvasTrigger }
    }
    LaunchedEffect(canvasController) {
        val triggerFlow = canvasController.getOrNull(KoneCanvasTriggerConsumer.Key) ?: KoneCanvasTriggerConsumer { it() }
        triggerFlow.onTrigger { triggerState.value = KoneCanvasTrigger }
    }
    val viewportMutableStateFlow = remember { MutableStateFlow(Vector2(0.0, 0.0)) }
    val correctedCanvasData = remember(canvasData) {
        KoneCanvasData.build {
            setFrom(canvasData)
            KoneCanvasViewportKey correspondsTo { viewportMutableStateFlow.value }
            KoneCanvasViewportMutableStateFlowKey correspondsTo viewportMutableStateFlow
        }
    }
    Canvas(modifier = modifier) {
        triggerState.value
        viewportMutableStateFlow.value = this.size.let { Vector2(it.width.toDouble(), it.height.toDouble()) }
        canvasController.getOrNull(KoneCanvasComposeMultiplatformDrawer.Key)?.apply { context(canvasController) { draw(correctedCanvasData) } }
    }
}

@Composable
public fun KoneComposeMapCanvas(modifier: Modifier = Modifier, canvasController: KoneCanvasController, canvasData: KoneCanvasData) {
    val correctedCanvasController = remember(canvasController) {
        KoneCanvasController.build {
            setFrom(canvasController)
            this.provideOrNull(KoneCanvasComposeMultiplatformDrawer.Key)?.let { drawerProvider ->
                KoneCanvasComposeMultiplatformDrawer.Key correspondsTo RegisteredValueProvider.cached {
                    val drawer = drawerProvider.get()
                    KoneCanvasComposeMultiplatformDrawer { data ->
                        contextOf<DrawScope>().withTransform(
                            {
                                translate(size.width / 2, size.height / 2)
                                scale(1f, -1f, Offset.Zero)
                            }
                        ) {
                            drawer.apply { draw(data) }
                        }
                    }
                }
            }
        }
    }
    
    val triggerStateFlow = remember { MutableStateFlow(KoneCanvasTrigger) }
    val triggerProducer = remember { KoneCanvasTriggerProducer { triggerStateFlow.value = KoneCanvasTrigger } }
    val triggerConsumer = remember { KoneCanvasTriggerConsumer { block -> triggerStateFlow.collect { block() } } }
    LaunchedEffect(canvasData) {
        val initialTriggerConsumer = canvasData.getOrNull(KoneCanvasTriggerConsumer.Key) ?: KoneCanvasTriggerConsumer { it() }
        initialTriggerConsumer.onTrigger { triggerProducer.trigger() }
    }
    
    val zoomLocalStateFlow = remember { MutableStateFlow(1.0) }
    val zoomGlobalStateFlow = remember(canvasData) { canvasData.getOrNull(KoneCanvasZoomMutableStateFlowKey) }
    val zoomStateFlow = zoomGlobalStateFlow ?: zoomLocalStateFlow
    LaunchedEffect(zoomGlobalStateFlow) {
        zoomGlobalStateFlow?.collect { zoomLocalStateFlow.value = it }
    }
    LaunchedEffect(null) {
        zoomLocalStateFlow.collect { triggerProducer.trigger() }
    }
    fun getZoom(): Double = zoomLocalStateFlow.value
    fun setZoom(value: Double) { zoomStateFlow.value = value }
    
    val offsetLocalStateFlow = remember { MutableStateFlow(Point2(0.0, 0.0)) }
    val offsetGlobalStateFlow = remember(canvasData) { canvasData.getOrNull(KoneCanvasOffsetMutableStateFlowKey) }
    val offsetStateFlow = offsetGlobalStateFlow ?: offsetLocalStateFlow
    LaunchedEffect(offsetGlobalStateFlow) {
        offsetGlobalStateFlow?.collect { offsetLocalStateFlow.value = it }
    }
    LaunchedEffect(null) {
        offsetLocalStateFlow.collect { triggerProducer.trigger() }
    }
    fun getOffset(): Point2<Double> = offsetLocalStateFlow.value
    fun setOffset(value: Point2<Double>) { offsetStateFlow.value = value }
    
    val hoveringStateFlow = remember { MutableStateFlow(KoneCanvasHoverData.empty()) }
    LaunchedEffect(null) {
        hoveringStateFlow.collect { triggerProducer.trigger() }
    }
    fun getHovering(): KoneCanvasHoverData = hoveringStateFlow.value
    fun setHovering(value: KoneCanvasHoverData) { hoveringStateFlow.value = value }
    
    val clickStateFlow = remember { MutableStateFlow(KoneCanvasClickData.empty()) }
    LaunchedEffect(null) {
        clickStateFlow.collect { triggerProducer.trigger() }
    }
    fun getClick(): KoneCanvasClickData = clickStateFlow.value
    fun setClick(value: KoneCanvasClickData) { clickStateFlow.value = value }
    
    var pointerPosition by remember { mutableStateOf<Point2<Double>?>(null) }
    LaunchedEffect(null) {
        snapshotFlow { pointerPosition }.collect { triggerProducer.trigger() }
    }
    
    val coroutineScope = rememberCoroutineScope()
    
    val koneContextRegistry = remember(canvasController) {
        KoneContextRegistry.buildWithProvider {
            defaultKoneContextRegistryBuilder()
            canvasController.getOrNull(KoneContextRegistry.Key)?.let { setFrom(it) }
        }
    }
    
    val euclideanSpace = remember(koneContextRegistry) {
        koneContextRegistry[EuclideanSpace2OverField.Key<Double>()]
    }
    
    val correctedCanvasData = remember(canvasData) {
        KoneCanvasData.build {
            setFrom(canvasData)
            KoneCanvasTriggerConsumer.Key correspondsTo triggerConsumer
            KoneCanvasTriggerProducer.Key correspondsTo triggerProducer
            KoneCanvasZoomKey correspondsTo { getZoom() }
            KoneCanvasOffsetKey correspondsTo { getOffset() }
            KoneCanvasHoverData.Key correspondsTo { getHovering() }
            KoneCanvasClickData.Key correspondsTo { getClick() }
            KoneCanvasPointerPositionKey correspondsTo { pointerPosition }
            KoneCanvasCoroutineScopeKey correspondsTo coroutineScope
        }
    }
    
    LaunchedEffect(canvasController) {
        val controller = canvasController.getOrNull(KoneCanvasHoverController.Key)
        if (controller != null) triggerStateFlow.collect { setHovering(controller.hover(correctedCanvasData)) }
        else setHovering(KoneCanvasHoverData.empty())
    }
    
    LaunchedEffect(canvasData) {
        canvasData.getOrNull(KoneCanvasHoverConsumer.Key)?.let { consumer ->
            hoveringStateFlow.collect {
                consumer.onHover(correctedCanvasData)
            }
        }
    }
    
    KoneComposeCanvas(
        modifier = Modifier
            .pointerInput(null) {
                detectDragGestures { _, [x, y] ->
                    KoneContext.unwrap(euclideanSpace)
                    setOffset(getOffset() - Vector2(x.toDouble(), -y.toDouble()) / getZoom())
                }
            }
            .let {
                key(correctedCanvasData, canvasController) {
                    scope {
                        val clickController = canvasController.getOrNull(KoneCanvasClickController.Key) ?: return@scope it
                        val clickConsumer = correctedCanvasData.getOrNull(KoneCanvasClickConsumer.Key)
                        it.pointerInput(clickController, clickConsumer) {
                            detectTapGestures { coordinates ->
                                val _ = runCatching {
                                    KoneContext.unwrap(euclideanSpace)
                                    val coordinates = Vector2(
                                        size.width.toDouble() / 2 - coordinates.x.toDouble(),
                                        coordinates.y.toDouble() - size.height.toDouble() / 2
                                    )
                                    pointerPosition = getOffset() - coordinates / getZoom()
                                    setClick(clickController.click(correctedCanvasData))
                                    clickConsumer?.onClick(correctedCanvasData)
                                }
                            }
                        }
                    }
                }
            }
            .pointerInput(canvasData) {
                val coercionRange = canvasData.getOrNull(KoneCanvasZoomCoercionRange)
                awaitPointerEventScope {
                    KoneContext.unwrap(euclideanSpace)
                    var isHovered = true
                    val change = currentEvent.changes.first()
                    val coordinates = Vector2(
                        size.width.toDouble() / 2 - change.position.x.toDouble(),
                        change.position.y.toDouble() - size.height.toDouble() / 2
                    )
                    pointerPosition = getOffset() - coordinates / getZoom()
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> {
                                pointerPosition = null
                                isHovered = false
                            }
                            PointerEventType.Move -> {
                                if (isHovered) {
                                    val change = event.changes.first()
                                    val _ = runCatching {
                                        val coordinates = Vector2(
                                            size.width.toDouble() / 2 - change.position.x.toDouble(),
                                            change.position.y.toDouble() - size.height.toDouble() / 2
                                        )
                                        pointerPosition = getOffset() - coordinates / getZoom()
                                    }
                                }
                            }
                            
                            PointerEventType.Scroll -> {
                                if (isHovered) {
                                    val change = event.changes.first()
                                    val coordinates = Vector2(
                                        size.width.toDouble() / 2 - change.position.x.toDouble(),
                                        change.position.y.toDouble() - size.height.toDouble() / 2
                                    )
                                    
                                    val currentZoom = getZoom()
                                    val newZoom = (currentZoom / exp(change.scrollDelta.y.toDouble() / 10)).let {
                                        if (coercionRange != null) it.coerceIn(coercionRange) else it
                                    }
                                    setZoom(newZoom)
                                    
                                    val oldOffset = getOffset()
                                    val zoomPosition = oldOffset - coordinates / currentZoom
                                    pointerPosition = zoomPosition
                                    val newOffset = zoomPosition + (oldOffset - zoomPosition) / (newZoom / currentZoom)
                                    setOffset(newOffset)
                                }
                            }
                        }
                    }
                }
            }
            .then(modifier),
        canvasController = correctedCanvasController,
        canvasData = correctedCanvasData,
    )
}