/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.registry.RegistryKey
import kotlinx.coroutines.flow.MutableStateFlow


public data object KoneCanvasZoomKey : RegistryKey<Double> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasZoomKey"
}

public data object KoneCanvasZoomMutableStateFlowKey : RegistryKey<MutableStateFlow<Double>> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasZoomMutableStateFlowKey"
}

public data object KoneCanvasZoomCoercionRange : RegistryKey<ClosedFloatingPointRange<Double>> {
    override fun toString(): String = "dev.lounres.kone.misc.canvas.KoneCanvasZoomCoercionRange"
}