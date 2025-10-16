/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.computationalGeometry.angles.Angle
import dev.lounres.kone.computationalGeometry.default2.Point2
import kotlinx.serialization.Serializable


@Serializable
public data class KoneCanvasState(
    val offset: Point2<Double> = Point2(0.0, 0.0),
    val zoom: Double = 1.0,
    val rotation: Angle = Angle.zero,
)

