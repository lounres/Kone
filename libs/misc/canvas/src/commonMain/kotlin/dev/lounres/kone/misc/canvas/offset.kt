/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.registry.RegistryKey
import kotlinx.coroutines.flow.MutableStateFlow


public data object KoneCanvasOffsetKey : RegistryKey<Point2<Double>> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasOffsetKey"
}

public data object KoneCanvasOffsetMutableStateFlowKey : RegistryKey<MutableStateFlow<Point2<Double>>> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasOffsetMutableStateFlowKey"
}