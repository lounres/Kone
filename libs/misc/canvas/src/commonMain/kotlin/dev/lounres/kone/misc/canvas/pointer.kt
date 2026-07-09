/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.registry.RegistryKey


public data object KoneCanvasPointerPositionKey : RegistryKey<Point2<Double>?> {
    override fun toString(): String {
        return super.toString()
    }
}